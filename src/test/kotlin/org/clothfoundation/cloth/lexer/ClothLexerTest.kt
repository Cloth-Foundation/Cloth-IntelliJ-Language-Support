package org.clothfoundation.cloth.lexer

import com.intellij.lexer.Lexer
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.clothfoundation.cloth.highlighting.ClothSyntaxHighlighter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClothLexerTest {
    @Test
    fun `classifies Cloth declarations and contextual names`() {
        val tokens = significantTokens(
            "abstract class : Base is Renderable { override func Render(int32? count) { println(self.name, super); } }",
        )

        assertToken(tokens, "abstract", ClothTokenTypes.MODIFIER)
        assertToken(tokens, "class", ClothTokenTypes.KEYWORD)
        assertToken(tokens, "Base", ClothTokenTypes.TYPE)
        assertToken(tokens, "Renderable", ClothTokenTypes.TYPE)
        assertToken(tokens, "override", ClothTokenTypes.MODIFIER)
        assertToken(tokens, "Render", ClothTokenTypes.FUNCTION_DECLARATION)
        assertToken(tokens, "int32", ClothTokenTypes.PRIMITIVE_TYPE)
        assertToken(tokens, "println", ClothTokenTypes.BUILTIN_FUNCTION)
        assertToken(tokens, "self", ClothTokenTypes.KEYWORD)
        assertToken(tokens, "super", ClothTokenTypes.KEYWORD)
        assertToken(tokens, "name", ClothTokenTypes.PROPERTY)
    }

    @Test
    fun `leaves import paths unstyled without affecting later meta access`() {
        val tokens = significantTokens("import cloth::io as Console; text::slice(0, 1);")

        assertToken(tokens, "import", ClothTokenTypes.KEYWORD)
        for (spelling in listOf("cloth", "::", "io", "Console")) {
            assertToken(tokens, spelling, ClothTokenTypes.IMPORT_PATH)
        }
        assertTrue(ClothSyntaxHighlighter().getTokenHighlights(ClothTokenTypes.IMPORT_PATH).isEmpty())
        assertToken(tokens, "slice", ClothTokenTypes.BUILTIN_FUNCTION)
    }

    @Test
    fun `maps primitive types to a visible keyword-derived color`() {
        val tokens = significantTokens("int8 uint64 float32 bool char byte string void object")

        for (token in tokens) assertEquals(ClothTokenTypes.PRIMITIVE_TYPE, token.type)
        assertEquals(
            ClothSyntaxHighlighter.PRIMITIVE_TYPE,
            ClothSyntaxHighlighter().getTokenHighlights(ClothTokenTypes.PRIMITIVE_TYPE).single(),
        )
    }

    @Test
    fun `recognizes member calls and safe meta access`() {
        val tokens = significantTokens("value?.render(); text?::length; text::slice(1, 2);")

        assertToken(tokens, "render", ClothTokenTypes.FUNCTION_CALL)
        assertToken(tokens, "length", ClothTokenTypes.PROPERTY)
        assertToken(tokens, "slice", ClothTokenTypes.BUILTIN_FUNCTION)
        assertTrue(tokens.count { it.type == ClothTokenTypes.OPERATOR } >= 3)
    }

    @Test
    fun `emits distinct tokens for paired delimiters`() {
        val tokens = significantTokens("func Main() { values[0]; }")

        assertToken(tokens, "(", ClothTokenTypes.LEFT_PARENTHESIS)
        assertToken(tokens, ")", ClothTokenTypes.RIGHT_PARENTHESIS)
        assertToken(tokens, "{", ClothTokenTypes.LEFT_BRACE)
        assertToken(tokens, "}", ClothTokenTypes.RIGHT_BRACE)
        assertToken(tokens, "[", ClothTokenTypes.LEFT_BRACKET)
        assertToken(tokens, "]", ClothTokenTypes.RIGHT_BRACKET)
    }

    @Test
    fun `keeps valid numeric spellings atomic and rejects malformed tails`() {
        val tokens = significantTokens("0xFF_80u32 1_000 6.022_140_76E23 1.25e2f32 0b2i8 1__0")

        assertToken(tokens, "0xFF_80u32", ClothTokenTypes.INTEGER_LITERAL)
        assertToken(tokens, "1_000", ClothTokenTypes.INTEGER_LITERAL)
        assertToken(tokens, "6.022_140_76E23", ClothTokenTypes.FLOAT_LITERAL)
        assertToken(tokens, "1.25e2f32", ClothTokenTypes.FLOAT_LITERAL)
        assertToken(tokens, "0b2i8", TokenType.BAD_CHARACTER)
        assertToken(tokens, "1__0", TokenType.BAD_CHARACTER)
    }

    @Test
    fun `highlights escapes independently inside strings and characters`() {
        val tokens = allTokens("\"ok\\n\\u{1F9F5}\\q\" '\\u{41}'")

        assertToken(tokens, "\\n", ClothTokenTypes.VALID_ESCAPE)
        assertToken(tokens, "\\u{1F9F5}", ClothTokenTypes.VALID_ESCAPE)
        assertToken(tokens, "\\q", ClothTokenTypes.INVALID_ESCAPE)
        assertToken(tokens, "\\u{41}", ClothTokenTypes.VALID_ESCAPE)
    }

    @Test
    fun `resumes incremental highlighting inside a block comment`() {
        val lexer = ClothLexer()
        val source = "continued */ let value = 1;"
        lexer.start(source, 0, source.length, ClothLexer.BLOCK_COMMENT_STATE)

        assertEquals(ClothTokenTypes.BLOCK_COMMENT, lexer.tokenType)
        assertEquals("continued */", source.substring(lexer.tokenStart, lexer.tokenEnd))
        lexer.advance()
        assertEquals(TokenType.WHITE_SPACE, lexer.tokenType)
        lexer.advance()
        assertEquals(ClothTokenTypes.KEYWORD, lexer.tokenType)
    }

    @Test
    fun `covers every source character without gaps`() {
        val source = "func Main() { /* open */ var text = \"bad\\u{1234567}\"; @ }"
        val tokens = allTokens(source)

        assertEquals(source, tokens.joinToString(separator = "") { it.text })
        assertToken(tokens, "\\u{1234567}", ClothTokenTypes.INVALID_ESCAPE)
        assertToken(tokens, "@", TokenType.BAD_CHARACTER)
    }

    private fun significantTokens(source: String): List<LexedToken> =
        allTokens(source).filter { it.type != TokenType.WHITE_SPACE }

    private fun allTokens(source: String): List<LexedToken> {
        val lexer: Lexer = ClothLexer()
        lexer.start(source)
        return buildList {
            while (lexer.tokenType != null) {
                add(LexedToken(lexer.tokenType!!, source.substring(lexer.tokenStart, lexer.tokenEnd)))
                lexer.advance()
            }
        }
    }

    private fun assertToken(tokens: List<LexedToken>, text: String, type: IElementType) {
        assertTrue(
            "Expected '$text' to have token type $type, got $tokens",
            tokens.any { it.text == text && it.type == type },
        )
    }

    private data class LexedToken(val type: IElementType, val text: String)
}
