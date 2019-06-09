package com.github.zxj5470.wxapp.annotator

import com.github.zxj5470.wxapp.ktlext.lastOr
import com.intellij.css.util.CssPsiUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.css.impl.CssElementTypes
import com.intellij.psi.css.impl.util.CssHighlighter
import com.intellij.psi.css.impl.util.CssHighlightingAnnotator
import com.intellij.psi.tree.IElementType

/**
 * @author zxj5470
 * @date 2018/5/11
 */
class WxssHighlightingAnnotator : CssHighlightingAnnotator() {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		element.children.apply {
			if (isNotEmpty()) {
				forEach { annotate(it, holder) }
			}
		}
		if (!holder.isBatchMode) {
			when {
				element.node.elementType === CssElementTypes.CSS_IDENT && element.text == "rpx" -> {
					highlight(holder, element, CssElementTypes.CSS_PROPERTY_VALUE, CssHighlighter.CSS_PROPERTY_VALUE)
				}
				else -> {
					super.annotate(element, holder)
				}
			}
		}
	}

	private fun highlight(holder: AnnotationHolder, element: PsiElement, type: IElementType, defaultKey: TextAttributesKey) {
		val key = getTextAttributesKey(element, type, defaultKey)
		holder.createInfoAnnotation(element, "weixin rpx unit for devices").setTextAttributes(key)
	}

	private fun getTextAttributesKey(element: PsiElement, elementType: IElementType, defaultKey: TextAttributesKey): TextAttributesKey {
		val language = CssPsiUtil.getStylesheetLanguage(element)
		return if (language == null) {
			defaultKey
		} else {
			val file = element.containingFile.virtualFile
			val highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(language, element.project, file)
			val keys = highlighter.getTokenHighlights(elementType)
			keys.lastOr(defaultKey)
		}
	}
}