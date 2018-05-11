package com.github.zxj5470.wxapp.registry.annotator

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.xml.XmlAttribute

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlHighlightErrorFilter : HighlightErrorFilter() {
	override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
		return !(element.parent is XmlAttribute && element.parent.firstChild.text.startsWith("wx:"))
	}
}