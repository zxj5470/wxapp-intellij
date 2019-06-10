package com.github.zxj5470.wxapp

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.xml.XmlFile
import org.jetbrains.annotations.NotNull

/**
 * Even if you're using Kotlin, you have to annotate with @NotNull.
 */

class WxmlFile(@NotNull viewProvider: FileViewProvider, element: IElementType) : PsiFileBase(viewProvider, WxmlLanguage.INSTANCE) {
	@NotNull
	override fun getFileType() = WxmlFileType
	override fun toString() = "Wxml File"
}

class WxssFile(@NotNull viewProvider: FileViewProvider) : PsiFileBase(viewProvider, WxssLanguage.INSTANCE) {
	@NotNull
	override fun getFileType() = WxssFileType

	override fun toString() = "Wxss File"
}