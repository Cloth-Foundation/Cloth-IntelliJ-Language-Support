package org.clothfoundation.cloth.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.clothfoundation.cloth.ClothIcons
import javax.swing.Icon

class ClothColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon = ClothIcons.LOGO

    override fun getHighlighter(): SyntaxHighlighter = ClothSyntaxHighlighter()

    override fun getDemoText(): String = DEMO_TEXT

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Cloth"

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Comments//Line comment", ClothSyntaxHighlighter.LINE_COMMENT),
            AttributesDescriptor("Comments//Block comment", ClothSyntaxHighlighter.BLOCK_COMMENT),
            AttributesDescriptor("Keyword", ClothSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Modifier", ClothSyntaxHighlighter.MODIFIER),
            AttributesDescriptor("Types//Primitive", ClothSyntaxHighlighter.PRIMITIVE_TYPE),
            AttributesDescriptor("Types//Named", ClothSyntaxHighlighter.TYPE),
            AttributesDescriptor("Constant", ClothSyntaxHighlighter.CONSTANT),
            AttributesDescriptor("Language variable", ClothSyntaxHighlighter.LANGUAGE_VARIABLE),
            AttributesDescriptor("Functions//Declaration", ClothSyntaxHighlighter.FUNCTION_DECLARATION),
            AttributesDescriptor("Functions//Call", ClothSyntaxHighlighter.FUNCTION_CALL),
            AttributesDescriptor("Functions//Built-in", ClothSyntaxHighlighter.BUILTIN_FUNCTION),
            AttributesDescriptor("Property", ClothSyntaxHighlighter.PROPERTY),
            AttributesDescriptor("Number", ClothSyntaxHighlighter.NUMBER),
            AttributesDescriptor("String", ClothSyntaxHighlighter.STRING),
            AttributesDescriptor("Character", ClothSyntaxHighlighter.CHARACTER),
            AttributesDescriptor("String escapes//Valid", ClothSyntaxHighlighter.VALID_ESCAPE),
            AttributesDescriptor("String escapes//Invalid", ClothSyntaxHighlighter.INVALID_ESCAPE),
            AttributesDescriptor("Operator", ClothSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Punctuation", ClothSyntaxHighlighter.PUNCTUATION),
            AttributesDescriptor("Invalid character", ClothSyntaxHighlighter.BAD_CHARACTER),
        )

        private val DEMO_TEXT = """
            import cloth::io;

            // Cloth syntax highlighting
            abstract class : Widget {
                static final string Label = "Cloth \\u{1F9F5}";

                override func Render(int32? count): void {
                    /* Calls, properties, and numeric suffixes */
                    println(self.Label + count?::typeName);
                    var scaled = 1.25e2f32;
                    var initial = 'C';
                }
            }
        """.trimIndent()
    }
}
