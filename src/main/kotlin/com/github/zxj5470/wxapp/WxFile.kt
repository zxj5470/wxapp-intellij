package com.github.zxj5470.wxapp

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.css.impl.CssFileImpl
import com.intellij.psi.tree.IElementType
import javax.swing.Icon

class WxmlFile(viewProvider: FileViewProvider, element: IElementType) : PsiFileBase(viewProvider, WxmlLanguage.INSTANCE) {
	override fun getFileType() = WxmlFileType
	override fun getIcon(flags: Int): Icon = WxappIcons.wxmlIcon
	override fun toString() = "Wxml File"
}

class WxssFile(viewProvider: FileViewProvider) : CssFileImpl(viewProvider, WxssLanguage.INSTANCE) {
	override fun getFileType() = WxssFileType
	override fun getIcon(flags: Int): Icon = WxappIcons.wxssIcon
	override fun toString() = "Wxss File"
}
