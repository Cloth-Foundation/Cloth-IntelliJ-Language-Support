package org.clothfoundation.cloth.projectview

import com.intellij.openapi.vfs.VirtualFile

internal object ClothSourceFolderDetector {
    private const val SOURCE_DIRECTORY_NAME = "src"
    private const val PROJECT_MANIFEST_NAME = "Shuttle.toml"
    private val SOURCE_EXTENSIONS = setOf("co", "cloth", "cl")

    fun isClothSourceFolder(directory: VirtualFile): Boolean {
        if (!directory.isDirectory) return false

        val hasProjectManifest = directory.parent
            ?.findChild(PROJECT_MANIFEST_NAME)
            ?.isDirectory == false
        val directSourceExtensions = directory.children.asSequence()
            .filterNot(VirtualFile::isDirectory)
            .map(VirtualFile::getExtension)

        return isClothSourceFolder(
            directory.name,
            hasProjectManifest,
            directSourceExtensions,
        )
    }

    internal fun isClothSourceFolder(
        directoryName: String,
        hasProjectManifest: Boolean,
        directSourceExtensions: Sequence<String?>,
    ): Boolean {
        if (directoryName != SOURCE_DIRECTORY_NAME) return false
        if (hasProjectManifest) return true
        return directSourceExtensions.any { it?.lowercase() in SOURCE_EXTENSIONS }
    }
}
