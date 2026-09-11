package org.clothfoundation.cloth.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.clothfoundation.cloth.lexer.ClothTokenTypes

class ClothBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(
        leftBraceType: IElementType,
        contextType: IElementType?,
    ): Boolean = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        private val PAIRS = arrayOf(
            BracePair(ClothTokenTypes.LEFT_PARENTHESIS, ClothTokenTypes.RIGHT_PARENTHESIS, false),
            BracePair(ClothTokenTypes.LEFT_BRACKET, ClothTokenTypes.RIGHT_BRACKET, false),
            BracePair(ClothTokenTypes.LEFT_BRACE, ClothTokenTypes.RIGHT_BRACE, true),
        )
    }
}
