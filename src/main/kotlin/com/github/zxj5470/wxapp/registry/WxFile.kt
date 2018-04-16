package com.github.zxj5470.wxapp.registry

import com.github.zxj5470.wxapp.WxmlLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import org.jetbrains.annotations.NotNull


class WxmlFile(@NotNull viewProvider: FileViewProvider) : PsiFileBase(viewProvider, WxmlLanguage.INSTANCE) {
	@NotNull
	override fun getFileType() = WxmlFileType

	/**
	 *
	 * @return String
	 */
	override fun toString(): String {
		return "Wxml File"
	}
}