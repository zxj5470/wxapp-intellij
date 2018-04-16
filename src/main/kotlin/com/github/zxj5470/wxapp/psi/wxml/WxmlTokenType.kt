package com.github.zxj5470.wxapp.psi.wxml

import com.github.zxj5470.wxapp.WxmlLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull

class WxmlTokenType(@NotNull @NonNls debugName: String) : IElementType(debugName, WxmlLanguage.INSTANCE) {
	override fun toString(): String {
		return "WxmlTokenType." + super.toString()
	}
}

class WxmlElementType(@NotNull @NonNls debugName: String) :
		IElementType(debugName, WxmlLanguage.INSTANCE)