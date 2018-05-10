package com.github.zxj5470.wxapp.registry.highlighter

import com.github.zxj5470.wxapp.psi.wxml.WxmlTypes
import com.intellij.ide.highlighter.XmlFileHighlighter
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType


/**
 * @author: zxj5470
 * @date: 2018/4/17
 */

class WxmlHighlighter : XmlFileHighlighter() {

	companion object {
		/*
		val tagNames = createTextAttributesKey("WXML_SEPARATOR", XmlHighlighterColors.XML_TAG_NAME)
		val tagSymbol = createTextAttributesKey("WXML_SYMBOL", DefaultLanguageHighlighterColors.OPERATION_SIGN)
		val keywords = createTextAttributesKey("WXML_KEY", DefaultLanguageHighlighterColors.KEYWORD)
		val value = createTextAttributesKey("WXML_VALUE", DefaultLanguageHighlighterColors.STRING)
		val comments = createTextAttributesKey("WXML_COMMENT", XmlHighlighterColors.XML_COMMENT)
		val WX = createTextAttributesKey("WXML_BAD_CHARACTER", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
		*/
	}

	private val WX_KEYWORDS_LIST = listOf(
		WxmlTypes.WX_FOR,
		WxmlTypes.WX_IF,
		WxmlTypes.WX_ELIF,
		WxmlTypes.WX_ELSE,
		WxmlTypes.WX_KEY,
		WxmlTypes.WX_FOR,
		WxmlTypes.WX_ITEM,
		WxmlTypes.WX_FOR_INDEX,
		WxmlTypes.WX_FOR_ITEM
	)

	override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
		return super.getTokenHighlights(tokenType)
	}
}