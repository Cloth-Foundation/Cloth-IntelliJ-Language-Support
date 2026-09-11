package org.clothfoundation.cloth.lexer

import com.intellij.psi.tree.TokenSet

object ClothTokenSets {
    @JvmField
    val COMMENTS: TokenSet = TokenSet.create(
        ClothTokenTypes.LINE_COMMENT,
        ClothTokenTypes.BLOCK_COMMENT,
    )

    @JvmField
    val LITERALS: TokenSet = TokenSet.create(
        ClothTokenTypes.INTEGER_LITERAL,
        ClothTokenTypes.FLOAT_LITERAL,
        ClothTokenTypes.STRING_LITERAL,
        ClothTokenTypes.CHARACTER_LITERAL,
    )

    @JvmField
    val KEYWORDS: TokenSet = TokenSet.create(
        ClothTokenTypes.KEYWORD,
        ClothTokenTypes.MODIFIER,
        ClothTokenTypes.PRIMITIVE_TYPE,
        ClothTokenTypes.TYPE,
        ClothTokenTypes.BOOLEAN,
        ClothTokenTypes.NULL,
    )

    @JvmField
    val PAIRED_DELIMITERS: TokenSet = TokenSet.create(
        ClothTokenTypes.LEFT_PARENTHESIS,
        ClothTokenTypes.RIGHT_PARENTHESIS,
        ClothTokenTypes.LEFT_BRACE,
        ClothTokenTypes.RIGHT_BRACE,
        ClothTokenTypes.LEFT_BRACKET,
        ClothTokenTypes.RIGHT_BRACKET,
    )
}
