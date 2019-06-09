package com.github.zxj5470.wxapp.navigator

import com.github.zxj5470.wxapp.getWxRootDir
import com.intellij.ide.impl.ProjectUtil
import com.intellij.lang.javascript.*
import com.intellij.lang.javascript.navigation.JSGotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileSystemUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.*
import java.io.File

/**
 * @author zxj5470
 * @date 2019/6/9
 */
class JsGoToDeclarationProvider : JSGotoDeclarationHandler() {
	override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
		val elem: PsiElement = sourceElement ?: return null
		val lang = elem.containingFile.language
		if (lang !is JSLanguageDialect && lang !is JavascriptLanguage) return null
		val project = elem.project
		fun arrayOfPsiElements(dir: VirtualFile, text: String): Array<PsiElement>? {
			val url = dir.url + File.separator + text
			val vf = VirtualFileManagerEx.getInstance().findFileByUrl(url) ?: return null
			val f = PsiManager.getInstance(project).findFile(vf) ?: return null
			return arrayOf(f)
		}
//		js literal
		val elementType = sourceElement.node.elementType
//		cannot find type
		when (elementType.toString()) {
			"JS:STRING_LITERAL" -> {
				val path = sourceElement.text.trim('\'', '\"') + ".js"
				if (path[0] == '/') {
					val f = elem.project.getWxRootDir.apply { println(this) } ?: return null
					val par = f.parent.apply { println(this) }
					return arrayOfPsiElements(par, path)
				}
				val file = sourceElement.containingFile ?: return null
				val currentFileDir = file.containingDirectory ?: return null
				return arrayOfPsiElements(currentFileDir.virtualFile, path)
			}
		}
		return null
	}

	override fun getActionText(context: DataContext): String? = null
}