package org.clothfoundation.cloth.projectview

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClothSourceFolderDetectorTest {
    @Test
    fun `recognizes an empty Shuttle source directory`() {
        assertTrue(isSourceFolder("src", hasManifest = true))
    }

    @Test
    fun `recognizes standalone source directories containing Cloth files`() {
        for (extension in listOf("co", "cloth", "cl", "CO")) {
            assertTrue(isSourceFolder("src", sourceExtensions = arrayOf(extension)))
        }
    }

    @Test
    fun `does not decorate unrelated source directories`() {
        assertFalse(isSourceFolder("src", sourceExtensions = arrayOf("java", "kt")))
        assertFalse(isSourceFolder("source", hasManifest = true, sourceExtensions = arrayOf("co")))
    }

    private fun isSourceFolder(
        name: String,
        hasManifest: Boolean = false,
        sourceExtensions: Array<String> = emptyArray(),
    ): Boolean = ClothSourceFolderDetector.isClothSourceFolder(
        name,
        hasManifest,
        sourceExtensions.asSequence(),
    )
}
