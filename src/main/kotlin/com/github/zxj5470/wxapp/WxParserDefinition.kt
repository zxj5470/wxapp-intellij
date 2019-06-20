package com.github.zxj5470.wxapp

import com.intellij.lang.Language
import com.intellij.lang.PsiParser
import com.intellij.lang.css.CSSParserDefinition
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.impl.source.parsing.xml.WxmlParser
import com.intellij.psi.tree.IFileElementType

class WxmlParserDefinition : XMLParserDefinition() {
	override fun createParser(project: Project?): PsiParser = WxmlParser()
	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxmlFile(viewProvider, fileNodeType)

	companion object {
		val FILE = IFileElementType(WxmlLanguage.INSTANCE)
	}
}

class WxssParserDefinition : CSSParserDefinition() {
	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxssFile(viewProvider)

	companion object {
		val FILE = IFileElementType(WxssLanguage.INSTANCE)
	}
}


class WxmlFileViewProviderFactory : FileViewProviderFactory {
	override fun createFileViewProvider(file: VirtualFile, language: Language?, manager: PsiManager, eventSystemEnabled: Boolean): FileViewProvider {
		return WxmlSingleRootFileViewProvider(manager, file)
	}

	class WxmlSingleRootFileViewProvider(manager: PsiManager, virtualFile: VirtualFile)
		: SingleRootFileViewProvider(manager, virtualFile) {
	}
}
