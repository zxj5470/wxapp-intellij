package com.github.zxj5470.wxapp.psi

import com.github.zxj5470.wxapp.WxmlLanguage
import com.github.zxj5470.wxapp.registry.WxmlFile
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType

class WxmlParserDefinition : XMLParserDefinition() {
	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxmlFile(viewProvider)

	companion object {
		val FILE = IFileElementType(WxmlLanguage.INSTANCE)
	}
}

