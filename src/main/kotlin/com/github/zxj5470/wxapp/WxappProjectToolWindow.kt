@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")
package com.github.zxj5470.wxapp

import com.intellij.ide.*
import com.intellij.ide.impl.ProjectViewSelectInTarget
import com.intellij.ide.projectView.*
import com.intellij.ide.projectView.impl.*
import com.intellij.ide.projectView.impl.nodes.*
import com.intellij.ide.util.treeView.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.ui.SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES
import java.awt.Font
import java.util.*
import javax.swing.Icon
import javax.swing.tree.DefaultTreeModel


class WxProjectViewPane(val project: Project) : AbstractProjectViewPSIPane(project) {
	override fun createTreeUpdater(treeBuilder: AbstractTreeBuilder): AbstractTreeUpdater {
		return MyProjectViewTreeUpdater(treeBuilder)
	}

	override fun createSelectInTarget(): SelectInTarget {
		return MyProjectPaneSelectInTarget(project)
	}

	override fun createStructure(): ProjectAbstractTreeStructureBase = MyProjectViewPaneTreeStructure()

	override fun createTree(treeModel: DefaultTreeModel): ProjectViewTree {
		return object : ProjectViewTree(project, treeModel) {
			override fun toString(): String {
				return title + " [wx] " + super.toString()
			}

			override fun setFont(font: Font) {
				var f = font
				if (Registry.`is`("bigger.font.in.project.view")) {
					f = f.deriveFont(f.size + 1.0f)
				}
				super.setFont(f)
			}
		}
	}

	override fun getIcon(): Icon = WxappIcons.weChatIcon
	override fun getWeight(): Int = 0x8ab8ec
	override fun getId(): String = "WxProject"
	override fun getTitle(): String = "WxProject"

	private inner class MyProjectViewPaneTreeStructure internal constructor() : ProjectTreeStructure(project, id), ProjectViewSettings {

		override fun createRoot(project: Project, settings: ViewSettings): AbstractTreeNode<*> {
			return object : ProjectViewProjectNode(project, settings) {
				override fun getChildren(): MutableCollection<AbstractTreeNode<Any>> {
//					val ret = super.getChildren()
					val file = project.getUserData(APP_JS_KEY) ?: return mutableListOf()
					val collection = mutableListOf<AbstractTreeNode<Any>>(WxappAppGlobalGroupNode(project, file, settings))
//					collection.addAll(ret)
					return collection
				}
			}
		}

		override fun isShowExcludedFiles(): Boolean = true
		override fun isUseFileNestingRules(): Boolean = true
		override fun isToBuildChildrenInBackground(element: Any): Boolean = false
	}

	private inner class MyProjectViewTreeUpdater constructor(treeBuilder: AbstractTreeBuilder) : AbstractTreeUpdater(treeBuilder) {
		override fun addSubtreeToUpdateByElement(element: Any): Boolean {
			if (element is PsiDirectory && !myProject.isDisposed) {
				val treeStructure = myTreeStructure as ProjectTreeStructure
				var dirToUpdateFrom: PsiDirectory? = element
				// optimization
				// isEmptyMiddleDirectory can be slow when project VFS is not fully loaded (initial dumb mode).
				// It's easiest to disable the optimization in any dumb mode
				if (!treeStructure.isFlattenPackages && treeStructure.isHideEmptyMiddlePackages && !DumbService.isDumb(myProject)) {
					while (dirToUpdateFrom != null && ProjectViewDirectoryHelper.getInstance(myProject).isEmptyMiddleDirectory(dirToUpdateFrom, true)) {
						dirToUpdateFrom = dirToUpdateFrom.parentDirectory
					}
				}
				var addedOk: Boolean
				while ((!super.addSubtreeToUpdateByElement(dirToUpdateFrom
						?: myTreeStructure.rootElement).also { addedOk = it })) {
					if (dirToUpdateFrom == null) {
						break
					}
					dirToUpdateFrom = dirToUpdateFrom.parentDirectory
				}
				return addedOk
			}

			return super.addSubtreeToUpdateByElement(element)
		}
	}

	inner class MyProjectPaneSelectInTarget(project: Project) : ProjectViewSelectInTarget(project), DumbAware {
		override fun toString(): String = SelectInManager.PROJECT
		override fun isSubIdSelectable(subId: String?, context: SelectInContext?): Boolean = canSelect(context!!)
		override fun getMinorViewId(): String? = id
		override fun getWeight(): Float = StandardTargetWeights.PROJECT_SETTINGS_WEIGHT
	}
}

interface FolderGroupNode {
//	fun getFolders(): Array<PsiDirectory>
}

interface FileGroupNode {
//	fun getFiles(): Array<PsiFile>
}

class WxappAppGlobalGroupNode(project: Project,
										file: PsiFile,
										settings: ViewSettings) : ProjectViewNode<Any>(project, file, settings), FolderGroupNode {
	override fun contains(file: VirtualFile): Boolean {
		return true
	}

	override fun update(presentation: PresentationData) {
		presentation.addText(APP_NODE, REGULAR_BOLD_ATTRIBUTES)
		val icon = WxappIcons.weChatIcon
		presentation.setIcon(icon)
		presentation.presentableText = APP_NODE
	}

	override fun getChildren(): MutableCollection<out AbstractTreeNode<out Any>> {
		val children = ArrayList<AbstractTreeNode<PsiFile>>()
		val project = project ?: return children
		val appJs = project.getUserData(APP_JS_KEY) ?: return children
		val appWxss = project.getUserData(APP_WXSS_KEY) ?: return children
		val appJson = project.getUserData(APP_JSON_KEY) ?: return children
		val arr = arrayOf(appJs, appWxss, appJson)
		arr.map { PsiFileNode(project, it, settings) }.forEach {
			children.add(it)
		}
		return children
	}

	override fun expandOnDoubleClick(): Boolean = true
	private val APP_NODE = "app [global]"

}