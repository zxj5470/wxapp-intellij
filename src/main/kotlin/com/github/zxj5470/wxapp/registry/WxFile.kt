package com.github.zxj5470.wxapp.registry

import com.github.zxj5470.wxapp.WxmlLanguage
import com.github.zxj5470.wxapp.WxssLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import org.jetbrains.annotations.NotNull

/**
 * Even if you're using Kotlin, you have to annotate with @NotNull.
 */

class WxmlFile(@NotNull viewProvider: FileViewProvider) : PsiFileBase(viewProvider, WxmlLanguage.INSTANCE) {
	@NotNull
	override fun getFileType() = WxmlFileType
	override fun toString() = "Wxml File"
}

class WxssFile(@NotNull viewProvider: FileViewProvider) : PsiFileBase(viewProvider, WxssLanguage.INSTANCE) {
	@NotNull
	override fun getFileType() = WxssFileType
	override fun toString() = "Wxss File"
}