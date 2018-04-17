package com.github.zxj5470.wxapp.registry.highlighter

import com.github.zxj5470.wxapp.WxmlLexerAdapter
import com.github.zxj5470.wxapp.psi.wxml.WxmlTypes
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey


/**
 * @author: zxj5470
 * @date: 2018/4/17
 */

class WxmlHighlighter : SyntaxHighlighterBase() {

	companion object {
		val tagNames = createTextAttributesKey("WXML_SEPARATOR", XmlHighlighterColors.XML_TAG_NAME)
		val tagSymbol = createTextAttributesKey("WXML_SYMBOL", DefaultLanguageHighlighterColors.OPERATION_SIGN)
		val keywords = createTextAttributesKey("WXML_KEY", DefaultLanguageHighlighterColors.KEYWORD)
		val value = createTextAttributesKey("WXML_VALUE", DefaultLanguageHighlighterColors.STRING)
		val comments = createTextAttributesKey("WXML_COMMENT", XmlHighlighterColors.XML_COMMENT)
		val WX = createTextAttributesKey("WXML_BAD_CHARACTER", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
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

	private val TAG_KEYS = arrayOf(tagNames)
	private val WX_KEYWORDS_KEYS = arrayOf(keywords)
	private val STRING_KEYS = arrayOf(value)
	private val COMMENT_KEYS = arrayOf(comments)
	private val WX_KEY = arrayOf(WX)
	private val TAG_SYMBOL_KEYS = arrayOf(tagSymbol)


	override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
		return when (tokenType) {
			WxmlTypes.ATTR_VALUE,
			WxmlTypes.D_QUOTE,
			WxmlTypes.S_QUOTE,
			WxmlTypes.ATTR_SINGLE_QUOTED_VALUE,
			WxmlTypes.ATTR_DOUBLE_QUOTED_VALUE -> STRING_KEYS

			WxmlTypes.COMMENT -> COMMENT_KEYS
			WxmlTypes.WX -> WX_KEY
			in WX_KEYWORDS_LIST -> WX_KEYWORDS_KEYS
			WxmlTypes.TAG_NAME -> TAG_KEYS
			WxmlTypes.LT, WxmlTypes.GT -> TAG_SYMBOL_KEYS
			else -> emptyArray()
		}
	}

	override fun getHighlightingLexer() = WxmlLexerAdapter()

}