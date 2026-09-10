package org.clothfoundation.cloth

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object ClothFileType : LanguageFileType(ClothLanguage) {
    override fun getName(): String = "Cloth"

    override fun getDescription(): String = "Cloth source file"

    override fun getDefaultExtension(): String = "co"

    override fun getIcon(): Icon = ClothIcons.CLASS
}
