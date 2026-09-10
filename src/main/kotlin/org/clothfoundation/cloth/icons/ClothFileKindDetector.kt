package org.clothfoundation.cloth.icons

import com.intellij.psi.TokenType
import org.clothfoundation.cloth.lexer.ClothLexer
import org.clothfoundation.cloth.lexer.ClothTokenTypes

internal object ClothFileKindDetector {
    fun detect(source: CharSequence): ClothFileKind {
        val lexer = ClothLexer()
        lexer.start(source)
        var insideImport = false

        while (lexer.tokenType != null) {
            val tokenType = lexer.tokenType
            val tokenText = source.subSequence(lexer.tokenStart, lexer.tokenEnd)

            if (insideImport) {
                if (tokenText.any { it == '\r' || it == '\n' } || tokenText.contentEquals(";")) {
                    insideImport = false
                }
                lexer.advance()
                continue
            }

            if (tokenType == TokenType.WHITE_SPACE ||
                tokenType == ClothTokenTypes.LINE_COMMENT ||
                tokenType == ClothTokenTypes.BLOCK_COMMENT
            ) {
                lexer.advance()
                continue
            }

            if (tokenType == ClothTokenTypes.KEYWORD && tokenText.contentEquals("import")) {
                insideImport = true
                lexer.advance()
                continue
            }

            if (tokenType == ClothTokenTypes.MODIFIER) {
                lexer.advance()
                continue
            }

            return when {
                tokenText.contentEquals("interface") -> ClothFileKind.INTERFACE
                tokenText.contentEquals("struct") -> ClothFileKind.STRUCT
                tokenText.contentEquals("enum") -> ClothFileKind.ENUM
                tokenText.contentEquals("error") -> ClothFileKind.ERROR
                else -> ClothFileKind.CLASS
            }
        }

        return ClothFileKind.CLASS
    }
}
