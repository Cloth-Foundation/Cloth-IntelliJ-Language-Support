package org.clothfoundation.cloth.projectview

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.project.DumbAware

class ClothSourceFolderDecorator : ProjectViewNodeDecorator, DumbAware {
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        val directory = node.virtualFile ?: return
        if (ClothSourceFolderDetector.isClothSourceFolder(directory)) {
            data.setIcon(AllIcons.Modules.SourceRoot)
        }
    }
}
