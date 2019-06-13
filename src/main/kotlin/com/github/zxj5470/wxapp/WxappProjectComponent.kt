package com.github.zxj5470.wxapp

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.*
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTableImpl
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.*
import com.intellij.psi.PsiManager
import com.intellij.util.io.createFile
import com.intellij.util.io.exists
import java.nio.file.Files
import java.nio.file.Paths


class WxappProjectComponent(private val project: Project) : ProjectComponent, Disposable {
	override fun dispose() {
	}

	override fun getComponentName() = "WxappProjectComponent"
	override fun projectOpened() {
		setupWxapp()
	}

	fun setupWxapp() {
		val vf = project.getWxRootDir
		if (vf != null) {
			project.syncDTsLibrary()
			val f = PsiManager.getInstance(project).findFile(vf)
			project.putUserData(APP_JS_KEY, f)

		}
	}

}

val Project.getWxRootDir: VirtualFile?
	get() = if (isDefault) {
		@Suppress("deprecation")
		baseDir
	} else {
		this.guessProjectDir()
	}?.run { getChildrenWithDepth(3).find { it.name == "app.js" } }

fun VirtualFile.getChildrenWithDepth(depth: Int): Sequence<VirtualFile> {
	if (depth == 0) return emptySequence()
	return children.asSequence() + children.asSequence().flatMap { it.getChildrenWithDepth(depth - 1) }
}

fun Project.syncDTsLibrary() {
	val name = "wx.d.ts"
	val dtsFile = Paths.get(PathManager.getPluginsPath(), "wxapp-intellij", name).apply {
		val path = this@apply
		if (!path.exists()) path.createFile()
		val bytes = WxappProjectComponent::class.java.getResource("/libs/wx.d.ts").readBytes()
		if (!Files.readAllBytes(this)!!.contentEquals(bytes)) {
			this.toFile().writeBytes(bytes)
		}
	}.toFile()

	val projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(this)
	val projectLibraryModel = projectLibraryTable.modifiableModel

	if (projectLibraryTable.modifiableModel.getLibraryByName(name) == null) {
		val library = projectLibraryModel.createLibrary(name)
		val libraryModel = library.modifiableModel
		val pathUrl = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, dtsFile.path)
		val file = VirtualFileManager.getInstance().findFileByUrl(pathUrl)
		val module = ModuleManager.getInstance(this).modules.first()
		if (file != null) {
			libraryModel.addRoot(file, OrderRootType.CLASSES)
			ApplicationManager.getApplication().runWriteAction {
				libraryModel.commit()
				projectLibraryModel.commit()
				ModuleRootModificationUtil.addDependency(module, library)
			}
		}
	} else {
		println("already")
	}
}
