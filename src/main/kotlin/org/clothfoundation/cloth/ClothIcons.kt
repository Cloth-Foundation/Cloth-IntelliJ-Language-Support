package org.clothfoundation.cloth

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object ClothIcons {
    @JvmField
    val CLASS: Icon = IconLoader.getIcon("/META-INF/icons/class.svg", ClothIcons::class.java)

    @JvmField
    val ENUM: Icon = IconLoader.getIcon("/META-INF/icons/enum.svg", ClothIcons::class.java)

    @JvmField
    val ERROR: Icon = IconLoader.getIcon("/META-INF/icons/error.svg", ClothIcons::class.java)

    @JvmField
    val INTERFACE: Icon = IconLoader.getIcon("/META-INF/icons/interface.svg", ClothIcons::class.java)

    @JvmField
    val STRUCT: Icon = IconLoader.getIcon("/META-INF/icons/struct.svg", ClothIcons::class.java)

    @JvmField
    val LOGO: Icon = IconLoader.getIcon("/META-INF/icons/logo.svg", ClothIcons::class.java)

}
