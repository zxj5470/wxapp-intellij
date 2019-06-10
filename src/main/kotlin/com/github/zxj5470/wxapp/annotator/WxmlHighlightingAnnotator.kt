package com.github.zxj5470.wxapp.annotator

import com.intellij.codeInsight.daemon.impl.analysis.XmlNamespaceAnnotator
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlHighlightingAnnotator : XmlNamespaceAnnotator() {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is XmlAttribute -> {
				val nameElement = element.nameElement
				if (nameElement.text.startsWith("wx:")) {
					nameElement.textRange.apply {
						holder.createInfoAnnotation(TextRange(startOffset + 3, endOffset), null)
							.textAttributes = DefaultLanguageHighlighterColors.KEYWORD
					}
				}
			}
		}
	}
}