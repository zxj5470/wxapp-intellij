package com.github.zxj5470.wxapp

import com.intellij.lang.PsiParser
import com.intellij.lang.css.CSSParserDefinition
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.css.impl.lexing.CssLexer
import com.intellij.psi.css.impl.parsing.CssParser2
import com.intellij.psi.tree.IFileElementType

class WxmlParserDefinition : XMLParserDefinition() {
	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxmlFile(viewProvider, fileNodeType)

	companion object {
		val FILE = IFileElementType(WxmlLanguage.INSTANCE)
	}
}

class WxssParserDefinition : CSSParserDefinition() {
	override fun createLexer(project: Project?): Lexer = CssLexer()

	override fun createParser(project: Project?): PsiParser = CssParser2()

	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxssFile(viewProvider)

	companion object {
		val FILE = IFileElementType(WxssLanguage.INSTANCE)
	}
}
