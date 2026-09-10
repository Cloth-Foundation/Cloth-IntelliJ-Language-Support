package org.clothfoundation.cloth.icons

import org.junit.Assert.assertEquals
import org.junit.Test

class ClothFileKindDetectorTest {
    @Test
    fun `detects every explicit Cloth file kind`() {
        val cases = mapOf(
            "class {}" to ClothFileKind.CLASS,
            "interface {}" to ClothFileKind.INTERFACE,
            "struct {}" to ClothFileKind.STRUCT,
            "enum { Ready, Done }" to ClothFileKind.ENUM,
            "error {}" to ClothFileKind.ERROR,
        )

        for ((source, expected) in cases) {
            assertEquals(source, expected, ClothFileKindDetector.detect(source))
        }
    }

    @Test
    fun `skips imports comments and envelope modifiers`() {
        val source = """
            import cloth.types::interface;
            import app::Contract as Base;

            // The declaration controls the icon.
            /* struct enum error */
            abstract interface : Base {}
        """.trimIndent()

        assertEquals(ClothFileKind.INTERFACE, ClothFileKindDetector.detect(source))
    }

    @Test
    fun `uses the class icon for implicit and incomplete files`() {
        for (source in listOf("", "// empty for now", "static func Main() {}", "@ interface {}")) {
            assertEquals(source, ClothFileKind.CLASS, ClothFileKindDetector.detect(source))
        }
    }

    @Test
    fun `ends an unterminated import at the line boundary`() {
        val source = """
            import old::Thing
            enum { Current }
        """.trimIndent()

        assertEquals(ClothFileKind.ENUM, ClothFileKindDetector.detect(source))
    }
}
