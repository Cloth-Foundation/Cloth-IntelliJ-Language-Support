package org.clothfoundation.cloth.lexer

import com.intellij.psi.tree.IElementType
import org.clothfoundation.cloth.ClothLanguage

class ClothTokenType(debugName: String) : IElementType(debugName, ClothLanguage) {
    override fun toString(): String = "ClothTokenType.${super.toString()}"
}
