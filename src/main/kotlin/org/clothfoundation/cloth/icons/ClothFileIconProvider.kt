package org.clothfoundation.cloth.icons

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import org.clothfoundation.cloth.ClothFileType
import org.clothfoundation.cloth.ClothIcons
import javax.swing.Icon

class ClothFileIconProvider : FileIconProvider, DumbAware {
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (file.fileType != ClothFileType) return null

        val document = FileDocumentManager.getInstance().getDocument(file)
            ?: return ClothIcons.CLASS
        val cached = file.getUserData(CACHE_KEY)
        val kind = if (cached?.modificationStamp == document.modificationStamp) {
            cached.kind
        } else {
            ClothFileKindDetector.detect(document.immutableCharSequence).also {
                file.putUserData(CACHE_KEY, CachedKind(document.modificationStamp, it))
            }
        }

        return when (kind) {
            ClothFileKind.CLASS -> ClothIcons.CLASS
            ClothFileKind.INTERFACE -> ClothIcons.INTERFACE
            ClothFileKind.STRUCT -> ClothIcons.STRUCT
            ClothFileKind.ENUM -> ClothIcons.ENUM
            ClothFileKind.ERROR -> ClothIcons.ERROR
        }
    }

    private data class CachedKind(
        val modificationStamp: Long,
        val kind: ClothFileKind,
    )

    companion object {
        private val CACHE_KEY = Key.create<CachedKind>("cloth.file.kind")
    }
}
