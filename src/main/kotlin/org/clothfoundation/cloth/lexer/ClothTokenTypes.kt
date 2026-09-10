package org.clothfoundation.cloth.lexer

object ClothTokenTypes {
    @JvmField val LINE_COMMENT = ClothTokenType("LINE_COMMENT")
    @JvmField val BLOCK_COMMENT = ClothTokenType("BLOCK_COMMENT")

    @JvmField val KEYWORD = ClothTokenType("KEYWORD")
    @JvmField val MODIFIER = ClothTokenType("MODIFIER")
    @JvmField val PRIMITIVE_TYPE = ClothTokenType("PRIMITIVE_TYPE")
    @JvmField val TYPE = ClothTokenType("TYPE")
    @JvmField val BOOLEAN = ClothTokenType("BOOLEAN")
    @JvmField val NULL = ClothTokenType("NULL")
    @JvmField val LANGUAGE_VARIABLE = ClothTokenType("LANGUAGE_VARIABLE")

    @JvmField val IDENTIFIER = ClothTokenType("IDENTIFIER")
    @JvmField val IMPORT_PATH = ClothTokenType("IMPORT_PATH")
    @JvmField val FUNCTION_DECLARATION = ClothTokenType("FUNCTION_DECLARATION")
    @JvmField val FUNCTION_CALL = ClothTokenType("FUNCTION_CALL")
    @JvmField val BUILTIN_FUNCTION = ClothTokenType("BUILTIN_FUNCTION")
    @JvmField val PROPERTY = ClothTokenType("PROPERTY")

    @JvmField val INTEGER_LITERAL = ClothTokenType("INTEGER_LITERAL")
    @JvmField val FLOAT_LITERAL = ClothTokenType("FLOAT_LITERAL")
    @JvmField val STRING_LITERAL = ClothTokenType("STRING_LITERAL")
    @JvmField val CHARACTER_LITERAL = ClothTokenType("CHARACTER_LITERAL")
    @JvmField val VALID_ESCAPE = ClothTokenType("VALID_ESCAPE")
    @JvmField val INVALID_ESCAPE = ClothTokenType("INVALID_ESCAPE")

    @JvmField val OPERATOR = ClothTokenType("OPERATOR")
    @JvmField val PUNCTUATION = ClothTokenType("PUNCTUATION")
}
