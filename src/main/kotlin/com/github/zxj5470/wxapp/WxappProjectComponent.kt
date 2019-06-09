package com.github.zxj5470.wxapp

import com.intellij.lang.javascript.ecmascript6.TypeScriptUtil
import com.intellij.lang.javascript.library.JSLibraryKind
import com.intellij.lang.javascript.library.JSLibraryUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.*
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.impl.*
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTableImpl
import com.intellij.openapi.roots.libraries.*
import com.intellij.openapi.util.Conditions
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.stubs.StubIndex
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
	ProjectLibraryTableImpl(this)
	runWriteAction {
		val dtsFile = Paths.get(PathManager.getPluginsPath(), "wxapp-intellij", "wx.d.ts").apply {
			val path = this@apply
			if (!path.exists()) path.createFile()
			val bytes = WxappProjectComponent::class.java.getResource("/libs/wx.d.ts").readBytes()
			if (!Files.readAllBytes(this)!!.contentEquals(bytes)) {
				this.toFile().writeBytes(bytes)
			}
		}.toFile()
		val f = JSLibraryUtil.findFileByIO(dtsFile, true)
		f ?: return@runWriteAction
		val model = ProjectLibraryTableImpl(this).modifiableModel
		println(model)
		val module = ModuleManager.getInstance(this).modules.first()
		OrderEntryUtil.getModuleLibraries(ModuleRootManager.getInstance(module))
		val s = OrderEntryUtil.getModuleLibraries(ModuleRootManager.getInstance(module))
		if (s.none { it.name == "wx.d.ts" }) {
			val lib = model.createLibrary("wx.d.ts")
			lib.modifiableModel.addRoot(dtsFile.absolutePath, OrderRootType.SOURCES)
			lib.modifiableModel.commit()
			ModuleRootModificationUtil.addDependency(module, lib)
			StubIndex.getInstance().forceRebuild(RuntimeException("Rebuild Index Error!"))
		}
	}
}
