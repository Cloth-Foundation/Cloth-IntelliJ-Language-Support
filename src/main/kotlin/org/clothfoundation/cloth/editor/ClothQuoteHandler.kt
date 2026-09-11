package org.clothfoundation.cloth.editor

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import org.clothfoundation.cloth.lexer.ClothTokenTypes

class ClothQuoteHandler : SimpleTokenSetQuoteHandler(
    ClothTokenTypes.STRING_LITERAL,
    ClothTokenTypes.CHARACTER_LITERAL,
)
