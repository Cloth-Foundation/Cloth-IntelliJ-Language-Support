package org.clothfoundation.cloth.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.clothfoundation.cloth.lexer.ClothLexer
import org.clothfoundation.cloth.lexer.ClothTokenTypes

class ClothSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = ClothLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        ClothTokenTypes.LINE_COMMENT -> LINE_COMMENT_KEYS
        ClothTokenTypes.BLOCK_COMMENT -> BLOCK_COMMENT_KEYS
        ClothTokenTypes.KEYWORD -> KEYWORD_KEYS
        ClothTokenTypes.MODIFIER -> MODIFIER_KEYS
        ClothTokenTypes.PRIMITIVE_TYPE -> PRIMITIVE_TYPE_KEYS
        ClothTokenTypes.TYPE -> TYPE_KEYS
        ClothTokenTypes.BOOLEAN, ClothTokenTypes.NULL -> CONSTANT_KEYS
        ClothTokenTypes.FUNCTION_DECLARATION -> FUNCTION_DECLARATION_KEYS
        ClothTokenTypes.FUNCTION_CALL -> FUNCTION_CALL_KEYS
        ClothTokenTypes.BUILTIN_FUNCTION -> BUILTIN_FUNCTION_KEYS
        ClothTokenTypes.PROPERTY -> PROPERTY_KEYS
        ClothTokenTypes.INTEGER_LITERAL, ClothTokenTypes.FLOAT_LITERAL -> NUMBER_KEYS
        ClothTokenTypes.STRING_LITERAL -> STRING_KEYS
        ClothTokenTypes.CHARACTER_LITERAL -> CHARACTER_KEYS
        ClothTokenTypes.VALID_ESCAPE -> VALID_ESCAPE_KEYS
        ClothTokenTypes.INVALID_ESCAPE -> INVALID_ESCAPE_KEYS
        ClothTokenTypes.OPERATOR -> OPERATOR_KEYS
        ClothTokenTypes.PUNCTUATION,
        ClothTokenTypes.LEFT_PARENTHESIS,
        ClothTokenTypes.RIGHT_PARENTHESIS,
        ClothTokenTypes.LEFT_BRACE,
        ClothTokenTypes.RIGHT_BRACE,
        ClothTokenTypes.LEFT_BRACKET,
        ClothTokenTypes.RIGHT_BRACKET,
        -> PUNCTUATION_KEYS
        TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS
        else -> EMPTY_KEYS
    }

    companion object {
        @JvmField
        val LINE_COMMENT = createTextAttributesKey(
            "CLOTH_LINE_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT,
        )

        @JvmField
        val BLOCK_COMMENT = createTextAttributesKey(
            "CLOTH_BLOCK_COMMENT",
            DefaultLanguageHighlighterColors.BLOCK_COMMENT,
        )

        @JvmField
        val KEYWORD = createTextAttributesKey("CLOTH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val MODIFIER = createTextAttributesKey("CLOTH_MODIFIER", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val PRIMITIVE_TYPE = createTextAttributesKey(
            "CLOTH_PRIMITIVE_TYPE",
            DefaultLanguageHighlighterColors.KEYWORD,
        )

        @JvmField
        val TYPE = createTextAttributesKey("CLOTH_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME)

        @JvmField
        val CONSTANT = createTextAttributesKey("CLOTH_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT)

        @JvmField
        val FUNCTION_DECLARATION = createTextAttributesKey(
            "CLOTH_FUNCTION_DECLARATION",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION,
        )

        @JvmField
        val FUNCTION_CALL = createTextAttributesKey(
            "CLOTH_FUNCTION_CALL",
            DefaultLanguageHighlighterColors.FUNCTION_CALL,
        )

        @JvmField
        val BUILTIN_FUNCTION = createTextAttributesKey(
            "CLOTH_BUILTIN_FUNCTION",
            DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL,
        )

        @JvmField
        val PROPERTY = createTextAttributesKey(
            "CLOTH_PROPERTY",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD,
        )

        @JvmField
        val NUMBER = createTextAttributesKey("CLOTH_NUMBER", DefaultLanguageHighlighterColors.NUMBER)

        @JvmField
        val STRING = createTextAttributesKey("CLOTH_STRING", DefaultLanguageHighlighterColors.STRING)

        @JvmField
        val CHARACTER = createTextAttributesKey("CLOTH_CHARACTER", DefaultLanguageHighlighterColors.STRING)

        @JvmField
        val VALID_ESCAPE = createTextAttributesKey(
            "CLOTH_VALID_ESCAPE",
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE,
        )

        @JvmField
        val INVALID_ESCAPE = createTextAttributesKey(
            "CLOTH_INVALID_ESCAPE",
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE,
        )

        @JvmField
        val OPERATOR = createTextAttributesKey(
            "CLOTH_OPERATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN,
        )

        @JvmField
        val PUNCTUATION = createTextAttributesKey(
            "CLOTH_PUNCTUATION",
            DefaultLanguageHighlighterColors.BRACES,
        )

        @JvmField
        val BAD_CHARACTER = createTextAttributesKey(
            "CLOTH_BAD_CHARACTER",
            HighlighterColors.BAD_CHARACTER,
        )

        private val LINE_COMMENT_KEYS = arrayOf(LINE_COMMENT)
        private val BLOCK_COMMENT_KEYS = arrayOf(BLOCK_COMMENT)
        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val MODIFIER_KEYS = arrayOf(MODIFIER)
        private val PRIMITIVE_TYPE_KEYS = arrayOf(PRIMITIVE_TYPE)
        private val TYPE_KEYS = arrayOf(TYPE)
        private val CONSTANT_KEYS = arrayOf(CONSTANT)
        private val FUNCTION_DECLARATION_KEYS = arrayOf(FUNCTION_DECLARATION)
        private val FUNCTION_CALL_KEYS = arrayOf(FUNCTION_CALL)
        private val BUILTIN_FUNCTION_KEYS = arrayOf(BUILTIN_FUNCTION)
        private val PROPERTY_KEYS = arrayOf(PROPERTY)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val STRING_KEYS = arrayOf(STRING)
        private val CHARACTER_KEYS = arrayOf(CHARACTER)
        private val VALID_ESCAPE_KEYS = arrayOf(VALID_ESCAPE)
        private val INVALID_ESCAPE_KEYS = arrayOf(INVALID_ESCAPE)
        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val PUNCTUATION_KEYS = arrayOf(PUNCTUATION)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}
