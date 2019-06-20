package com.github.zxj5470.wxapp

import com.intellij.codeInspection.ex.InspectionProfileModifiableModel
import com.intellij.notification.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.*
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.*
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.*
import com.intellij.profile.codeInspection.InspectionProjectProfileManager
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

	private fun VirtualFile.setupInspection() {
		runWriteAction {
			val idea = this.findChild(".idea") ?: return@runWriteAction
			val dirName = "inspectionProfiles"
			val profile = "Project_Default.xml"
			val inspectionProfiles = idea.findChild(dirName) ?: idea.createChildDirectory(null, dirName)
			val path = Paths.get(inspectionProfiles.path, profile)
			if (!path.exists()) {
				Notifications.Bus.notify(Notification("com.github.zxj5470.wxapp.notification",
					WxappBundle.message("notification.title"),
					WxappBundle.message("notification.create.profile"),
					NotificationType.INFORMATION,
					NotificationListener { notification, event ->
						when (event.description) {
							"createProfile" -> {
								path.createFile()
								val bytes = WxappProjectComponent::class.java
									.getResource("/$dirName/$profile").readBytes()
								path.toFile().writeBytes(bytes)
								notification.hideBalloon()
								ProjectManager.getInstance().reloadProject(project)
							}
						}
					}
				), project)
			} else {
				var ret = false
				run {
					InspectionProjectProfileManager.getInstance(project).profiles.forEach {
						it.getToolsOrNull("CheckTagEmptyBody", project)?.apply {
							if (isEnabled) apply { ret = true;return@run }
						} ?: apply { ret = true;return@run }
						it.getToolsOrNull("CssInvalidPropertyValue", project)?.apply {
							if (isEnabled) apply { ret = true;return@run }
						} ?: apply { ret = true;return@run }
						it.getToolsOrNull("UnterminatedStatementJS", project)?.apply {
							if (isEnabled) apply { ret = true;return@run }
						} ?: apply { ret = true;return@run }
					}
				}
				if (!ret) return@runWriteAction
				Notifications.Bus.notify(Notification("com.github.zxj5470.wxapp.notification.exist",
					WxappBundle.message("notification.title"),
					WxappBundle.message("notification.create.profile.exist"),
					NotificationType.INFORMATION,
					NotificationListener { notification, event ->
						if (event.description == "existCheck") {
							InspectionProjectProfileManager.getInstance(project).profiles.forEach {
								it.modifiableModel.apply {
									ignores("CheckTagEmptyBody")
									ignores("CssInvalidPropertyValue")
									ignores("UnterminatedStatementJS")
								}
							}
							notification.hideBalloon()
							ProjectManager.getInstance().reloadProject(project)
						}
					}
				), project)
			}
		}
	}

	private fun InspectionProfileModifiableModel.ignores(name: String) {
		getToolsOrNull(name, project)?.apply {
			isEnabled = false
			// some function become private
			defaultState.isEnabled = isEnabled
			isEnabled = if (isEnabled) {
				true
			} else {
				if (defaultState.isEnabled) {
					return
				}
				for (tool in tools) {
					if (tool.isEnabled) {
						return
					}
				}
				false
			}
			commit()
		}
	}

	fun setupWxapp() {
		fun VirtualFile?.toPsiFile() = this?.let { PsiManager.getInstance(project).findFile(it) }
		val vf = project.getWxAppJs
		if (vf != null) {
			vf.parent.findChild("app.json") ?: return
			project.syncDTsLibrary()
			val appJs = vf.toPsiFile()
			val appWxss = vf.parent.findChild("app.wxss").toPsiFile()
			val appJson = vf.parent.findChild("app.json").toPsiFile()
			project.putUserData(APP_JS_KEY, appJs)
			project.putUserData(APP_WXSS_KEY, appWxss)
			project.putUserData(APP_JSON_KEY, appJson)
			vf.parent.setupInspection()
		}
	}
}


val Project.getWxAppJs: VirtualFile?
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
	}
}
