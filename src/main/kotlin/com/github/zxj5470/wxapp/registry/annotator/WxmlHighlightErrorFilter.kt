package com.github.zxj5470.wxapp.registry.annotator

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.xml.XmlDocument
import com.intellij.psi.xml.XmlTag

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlHighlightErrorFilter : HighlightErrorFilter() {
	override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
		return !element.multiRootElement
	}
}

inline val PsiErrorElement.multiRootElement: Boolean
	get() = this.parent.parent is XmlDocument && this.parent is XmlTag
