package org.clothfoundation.cloth.editor

import org.clothfoundation.cloth.lexer.ClothTokenTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClothEditorSupportTest {
    @Test
    fun `registers Cloth line and block comment markers`() {
        val commenter = ClothCommenter()

        assertEquals("//", commenter.lineCommentPrefix)
        assertEquals("/*", commenter.blockCommentPrefix)
        assertEquals("*/", commenter.blockCommentSuffix)
    }

    @Test
    fun `declares parentheses brackets and braces as matching pairs`() {
        val pairs = ClothBraceMatcher().pairs

        assertEquals(3, pairs.size)
        assertTrue(pairs.any { it.leftBraceType == ClothTokenTypes.LEFT_PARENTHESIS && it.rightBraceType == ClothTokenTypes.RIGHT_PARENTHESIS })
        assertTrue(pairs.any { it.leftBraceType == ClothTokenTypes.LEFT_BRACKET && it.rightBraceType == ClothTokenTypes.RIGHT_BRACKET })
        assertTrue(pairs.any { it.leftBraceType == ClothTokenTypes.LEFT_BRACE && it.rightBraceType == ClothTokenTypes.RIGHT_BRACE })
        assertFalse(pairs.first { it.leftBraceType == ClothTokenTypes.LEFT_PARENTHESIS }.isStructural)
        assertTrue(pairs.first { it.leftBraceType == ClothTokenTypes.LEFT_BRACE }.isStructural)
    }
}
