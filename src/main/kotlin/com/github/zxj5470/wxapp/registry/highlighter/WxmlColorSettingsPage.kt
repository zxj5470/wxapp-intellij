package com.github.zxj5470.wxapp.registry.highlighter

import com.github.zxj5470.wxapp.WxappIcons
import com.intellij.ide.highlighter.XmlFileHighlighter
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.intellij.lang.annotations.Language

/**
 * @author: zxj5470
 * @date: 2018/4/17
 */
class WxmlColorSettingsPage : ColorSettingsPage {
	override fun getHighlighter() = XmlFileHighlighter()

	override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null

	override fun getIcon() = WxappIcons.wxmlIcon

	override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
//		AttributesDescriptor("WxKeyWord", WxmlHighlighter.keywords)
	)

	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

	override fun getDisplayName() = "WXML"

	override fun getDemoText() = """
		<view wx:if="{{hasUserInfo}}">
			<text>text</text>
		</view>
	""".trimIndent()

}