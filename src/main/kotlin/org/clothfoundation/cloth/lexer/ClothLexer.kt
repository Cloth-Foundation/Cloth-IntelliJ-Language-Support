package org.clothfoundation.cloth.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Highlighting lexer for Cloth source files.
 *
 * This deliberately has no parser dependency so it can also become the lexical
 * foundation for PSI and LSP-backed features later. Its integer state records
 * quoted/comment modes and the small amount of context used to distinguish
 * declarations, calls, and member access during incremental highlighting.
 */
class ClothLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd = 0
    private var currentOffset = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null
    private var lexerState = DEFAULT_STATE
    private var tokenState = DEFAULT_STATE

    override fun start(
        buffer: CharSequence,
        startOffset: Int,
        endOffset: Int,
        initialState: Int,
    ) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        currentOffset = startOffset
        lexerState = normalizeState(initialState)
        locateToken()
    }

    override fun getState(): Int = tokenState

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        currentOffset = tokenEnd
        locateToken()
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    private fun locateToken() {
        if (currentOffset >= bufferEnd) {
            tokenStart = bufferEnd
            tokenEnd = bufferEnd
            tokenType = null
            tokenState = lexerState
            return
        }

        tokenStart = currentOffset
        while (true) {
            tokenState = lexerState
            when (mode) {
                MODE_BLOCK_COMMENT -> {
                    scanBlockComment(currentOffset)
                    return
                }

                MODE_STRING -> if (scanQuotedToken('"', ClothTokenTypes.STRING_LITERAL)) return
                MODE_CHARACTER -> if (scanQuotedToken('\'', ClothTokenTypes.CHARACTER_LITERAL)) return
                else -> {
                    scanDefaultToken()
                    return
                }
            }

            // Quoted literals end at a newline even when their closing quote is
            // missing. Reclassify that newline as ordinary whitespace.
            tokenState = lexerState
        }
    }

    private fun scanDefaultToken() {
        val first = buffer[currentOffset]
        when {
            first.isClothWhitespace() -> scanWhitespace()
            first == '/' && peek(1) == '/' -> scanLineComment()
            first == '/' && peek(1) == '*' -> {
                mode = MODE_BLOCK_COMMENT
                scanBlockComment(currentOffset + 2)
            }

            first == '"' -> {
                context = CONTEXT_NONE
                mode = MODE_STRING
                tokenEnd = currentOffset + 1
                tokenType = ClothTokenTypes.STRING_LITERAL
            }

            first == '\'' -> {
                context = CONTEXT_NONE
                mode = MODE_CHARACTER
                tokenEnd = currentOffset + 1
                tokenType = ClothTokenTypes.CHARACTER_LITERAL
            }

            first.isIdentifierStart() -> scanIdentifier()
            first.isAsciiDigit() -> scanNumber()
            scanOperator() -> Unit
            first in PUNCTUATION -> {
                context = CONTEXT_NONE
                tokenEnd = currentOffset + 1
                tokenType = ClothTokenTypes.PUNCTUATION
            }

            else -> {
                context = CONTEXT_NONE
                tokenEnd = currentOffset + 1
                tokenType = TokenType.BAD_CHARACTER
            }
        }
    }

    private fun scanWhitespace() {
        var offset = currentOffset + 1
        while (offset < bufferEnd && buffer[offset].isClothWhitespace()) offset++
        if (context == CONTEXT_IMPORT && buffer.subSequence(currentOffset, offset).any { it == '\r' || it == '\n' }) {
            context = CONTEXT_NONE
        }
        tokenEnd = offset
        tokenType = TokenType.WHITE_SPACE
    }

    private fun scanLineComment() {
        var offset = currentOffset + 2
        while (offset < bufferEnd && buffer[offset] != '\r' && buffer[offset] != '\n') offset++
        tokenEnd = offset
        tokenType = ClothTokenTypes.LINE_COMMENT
    }

    private fun scanBlockComment(bodyStart: Int) {
        var offset = bodyStart
        while (offset < bufferEnd) {
            if (buffer[offset] == '*' && charAt(offset + 1) == '/') {
                tokenEnd = offset + 2
                mode = MODE_DEFAULT
                tokenType = ClothTokenTypes.BLOCK_COMMENT
                return
            }
            offset++
        }
        tokenEnd = bufferEnd
        tokenType = ClothTokenTypes.BLOCK_COMMENT
    }

    /** Returns false when an unterminated literal ended immediately at a newline. */
    private fun scanQuotedToken(quote: Char, literalType: IElementType): Boolean {
        val first = buffer[currentOffset]
        if (first == '\r' || first == '\n') {
            mode = MODE_DEFAULT
            return false
        }

        if (first == quote) {
            tokenEnd = currentOffset + 1
            tokenType = literalType
            mode = MODE_DEFAULT
            return true
        }

        if (first == '\\') {
            scanEscape(quote)
            return true
        }

        var offset = currentOffset + 1
        while (offset < bufferEnd) {
            val character = buffer[offset]
            if (character == quote || character == '\\' || character == '\r' || character == '\n') break
            offset++
        }
        tokenEnd = offset
        tokenType = literalType
        return true
    }

    private fun scanEscape(quote: Char) {
        val escaped = charAt(currentOffset + 1)
        if (escaped in SIMPLE_ESCAPES) {
            tokenEnd = minOf(currentOffset + 2, bufferEnd)
            tokenType = ClothTokenTypes.VALID_ESCAPE
            return
        }

        if (escaped != 'u') {
            tokenEnd = minOf(currentOffset + 2, bufferEnd)
            tokenType = ClothTokenTypes.INVALID_ESCAPE
            return
        }

        var offset = currentOffset + 2
        if (charAt(offset) != '{') {
            while (offset < bufferEnd && !buffer[offset].endsInvalidEscape(quote)) offset++
            tokenEnd = offset
            tokenType = ClothTokenTypes.INVALID_ESCAPE
            return
        }

        offset++
        val digitsStart = offset
        while (offset < bufferEnd && buffer[offset].isAsciiHexDigit()) offset++
        val digitCount = offset - digitsStart
        if (digitCount in 1..6 && charAt(offset) == '}') {
            tokenEnd = offset + 1
            tokenType = ClothTokenTypes.VALID_ESCAPE
            return
        }

        while (offset < bufferEnd && !buffer[offset].endsInvalidEscape(quote)) {
            val character = buffer[offset++]
            if (character == '}') break
        }
        tokenEnd = offset
        tokenType = ClothTokenTypes.INVALID_ESCAPE
    }

    private fun scanIdentifier() {
        var offset = currentOffset + 1
        while (offset < bufferEnd && buffer[offset].isIdentifierContinue()) offset++
        tokenEnd = offset

        val spelling = buffer.subSequence(currentOffset, offset).toString()
        val priorContext = context
        context = CONTEXT_NONE
        tokenType = when {
            spelling == "import" -> {
                context = CONTEXT_IMPORT
                ClothTokenTypes.KEYWORD
            }

            priorContext == CONTEXT_IMPORT && spelling == "as" -> {
                context = CONTEXT_IMPORT
                ClothTokenTypes.KEYWORD
            }

            priorContext == CONTEXT_IMPORT -> {
                context = CONTEXT_IMPORT
                ClothTokenTypes.IMPORT_PATH
            }

            spelling == "func" -> {
                context = CONTEXT_FUNCTION_NAME
                ClothTokenTypes.KEYWORD
            }

            spelling in MODIFIERS -> ClothTokenTypes.MODIFIER
            spelling in TYPE_KEYWORDS -> ClothTokenTypes.PRIMITIVE_TYPE
            spelling == "true" || spelling == "false" -> ClothTokenTypes.BOOLEAN
            spelling == "null" -> ClothTokenTypes.NULL
            spelling == "self" || spelling == "super" -> ClothTokenTypes.KEYWORD//ClothTokenTypes.LANGUAGE_VARIABLE
            spelling in KEYWORDS -> ClothTokenTypes.KEYWORD
            priorContext == CONTEXT_FUNCTION_NAME -> ClothTokenTypes.FUNCTION_DECLARATION
            priorContext == CONTEXT_MEMBER -> {
                if (isFollowedByOpeningParenthesis(offset)) ClothTokenTypes.FUNCTION_CALL else ClothTokenTypes.PROPERTY
            }

            priorContext == CONTEXT_META -> {
                if (spelling in BUILTIN_META_FUNCTIONS && isFollowedByOpeningParenthesis(offset)) {
                    ClothTokenTypes.BUILTIN_FUNCTION
                } else {
                    ClothTokenTypes.PROPERTY
                }
            }

            spelling in BUILTIN_FUNCTIONS && isFollowedByOpeningParenthesis(offset) -> {
                ClothTokenTypes.BUILTIN_FUNCTION
            }

            spelling.first().isAsciiUppercase() -> ClothTokenTypes.TYPE
            isFollowedByOpeningParenthesis(offset) -> ClothTokenTypes.FUNCTION_CALL
            else -> ClothTokenTypes.IDENTIFIER
        }
    }

    private fun scanNumber() {
        var offset = currentOffset
        val hasBasePrefix = buffer[offset] == '0' && charAt(offset + 1) in BASE_PREFIXES
        if (hasBasePrefix) {
            offset += 2
            while (offset < bufferEnd && buffer[offset].isIdentifierContinue()) offset++
        } else {
            while (offset < bufferEnd && (buffer[offset].isAsciiDigit() || buffer[offset] == '_')) offset++
            if (charAt(offset) == '.' && charAt(offset + 1).isAsciiDigit()) {
                offset++
                while (offset < bufferEnd && (buffer[offset].isAsciiDigit() || buffer[offset] == '_')) offset++
            }
            if (charAt(offset) == 'e' || charAt(offset) == 'E') {
                offset++
                if (charAt(offset) == '+' || charAt(offset) == '-') offset++
                while (offset < bufferEnd && buffer[offset].isIdentifierContinue()) offset++
            } else {
                while (offset < bufferEnd && buffer[offset].isIdentifierContinue()) offset++
            }
        }

        context = CONTEXT_NONE
        tokenEnd = offset
        val spelling = buffer.subSequence(currentOffset, offset).toString()
        tokenType = when {
            FLOAT_LITERAL.matches(spelling) -> ClothTokenTypes.FLOAT_LITERAL
            INTEGER_LITERAL.matches(spelling) -> ClothTokenTypes.INTEGER_LITERAL
            else -> TokenType.BAD_CHARACTER
        }
    }

    private fun scanOperator(): Boolean {
        val operator = OPERATORS.firstOrNull { matches(currentOffset, it) } ?: return false
        tokenEnd = currentOffset + operator.length
        if (context == CONTEXT_IMPORT && operator in IMPORT_PATH_OPERATORS) {
            tokenType = ClothTokenTypes.IMPORT_PATH
            return true
        }

        tokenType = ClothTokenTypes.OPERATOR
        context = when (operator) {
            ".", "?." -> CONTEXT_MEMBER
            "::", "?::" -> CONTEXT_META
            else -> CONTEXT_NONE
        }
        return true
    }

    private fun isFollowedByOpeningParenthesis(offset: Int): Boolean {
        var lookahead = offset
        while (lookahead < bufferEnd && buffer[lookahead].isClothWhitespace()) lookahead++
        return charAt(lookahead) == '('
    }

    private fun matches(offset: Int, spelling: String): Boolean {
        if (offset + spelling.length > bufferEnd) return false
        for (index in spelling.indices) {
            if (buffer[offset + index] != spelling[index]) return false
        }
        return true
    }

    private fun peek(lookahead: Int): Char = charAt(currentOffset + lookahead)

    private fun charAt(offset: Int): Char = if (offset in 0 until bufferEnd) buffer[offset] else '\u0000'

    private var mode: Int
        get() = lexerState and MODE_MASK
        set(value) {
            lexerState = (lexerState and CONTEXT_MASK) or value
        }

    private var context: Int
        get() = lexerState and CONTEXT_MASK
        set(value) {
            lexerState = (lexerState and MODE_MASK) or value
        }

    companion object {
        const val DEFAULT_STATE = 0
        const val BLOCK_COMMENT_STATE = 1
        const val STRING_STATE = 2
        const val CHARACTER_STATE = 3

        private const val MODE_DEFAULT = DEFAULT_STATE
        private const val MODE_BLOCK_COMMENT = BLOCK_COMMENT_STATE
        private const val MODE_STRING = STRING_STATE
        private const val MODE_CHARACTER = CHARACTER_STATE
        private const val MODE_MASK = 0x0F

        private const val CONTEXT_NONE = 0x00
        private const val CONTEXT_FUNCTION_NAME = 0x10
        private const val CONTEXT_MEMBER = 0x20
        private const val CONTEXT_META = 0x30
        private const val CONTEXT_IMPORT = 0x40
        private const val CONTEXT_MASK = 0xF0

        private val KEYWORDS = setOf(
            "return", "if", "else", "while", "for", "in", "break", "continue",
            "switch", "case", "default", "struct", "class", "interface", "enum",
            "error", "throw", "throws", "trait", "let", "var", "const",
            "is", "as", "match", "module", "implements",
        )

        private val MODIFIERS = setOf(
            "abstract", "sealed", "final", "static", "override", "extern", "unsafe",
        )

        private val TYPE_KEYWORDS = setOf(
            "int", "int8", "int16", "int32", "int64",
            "uint", "uint8", "uint16", "uint32", "uint64",
            "float", "float32", "float64", "bool", "char", "byte", "string", "void", "object",
        )

        private val BUILTIN_FUNCTIONS = setOf("print", "println")
        private val BUILTIN_META_FUNCTIONS = setOf("parse", "slice")
        private val SIMPLE_ESCAPES = setOf('n', 'r', 't', '\\', '\'', '"', '0')
        private val BASE_PREFIXES = setOf('b', 'B', 'o', 'O', 'x', 'X')
        private val PUNCTUATION = setOf('(', ')', '{', '}', '[', ']', ',', ';', ':')
        private val IMPORT_PATH_OPERATORS = setOf("::", ".", "*")

        private val OPERATORS = listOf(
            "<<=", ">>=", "?::",
            "::", "?.", "??", "++", "--", "+=", "-=", "*=", "/=", "%=",
            "<<", ">>", "<=", ">=", "==", "!=", "&&", "||", "&=", "|=", "^=",
            ".", "?", "+", "-", "*", "/", "%", "=", "!", "<", ">", "&", "|", "^", "~",
        )

        private val INTEGER_LITERAL = Regex(
            "^(?:0b[01](?:_?[01])*|0o[0-7](?:_?[0-7])*|0x[0-9A-Fa-f](?:_?[0-9A-Fa-f])*|" +
                "[0-9](?:_?[0-9])*)(?:i8|i16|i32|i64|u8|u16|u32|u64)?$",
        )

        private val FLOAT_LITERAL = Regex(
            "^[0-9](?:_?[0-9])*(?:(?:\\.[0-9](?:_?[0-9])*)?" +
                "(?:[eE][+-]?[0-9](?:_?[0-9])*)(?:f32|f64)?|" +
                "\\.[0-9](?:_?[0-9])*(?:f32|f64)?|(?:f32|f64))$",
        )

        private fun normalizeState(state: Int): Int {
            val mode = state and MODE_MASK
            val context = state and CONTEXT_MASK
            if (mode !in MODE_DEFAULT..MODE_CHARACTER) return DEFAULT_STATE
            if (context !in setOf(CONTEXT_NONE, CONTEXT_FUNCTION_NAME, CONTEXT_MEMBER, CONTEXT_META, CONTEXT_IMPORT)) {
                return DEFAULT_STATE
            }
            return mode or context
        }

        private fun Char.isAsciiDigit(): Boolean = this in '0'..'9'
        private fun Char.isAsciiUppercase(): Boolean = this in 'A'..'Z'
        private fun Char.isAsciiHexDigit(): Boolean = isAsciiDigit() || this in 'a'..'f' || this in 'A'..'F'
        private fun Char.isIdentifierStart(): Boolean = this in 'a'..'z' || isAsciiUppercase() || this == '_'
        private fun Char.isIdentifierContinue(): Boolean = isIdentifierStart() || isAsciiDigit()
        private fun Char.isClothWhitespace(): Boolean =
            this == ' ' || this == '\t' || this == '\r' || this == '\n' || this == '\u000C'

        private fun Char.endsInvalidEscape(quote: Char): Boolean =
            this == quote || this == '\r' || this == '\n' || isClothWhitespace()
    }
}
