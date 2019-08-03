package com.github.zxj5470.wxapp.annotator

import com.github.zxj5470.wxapp.WxmlFile
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
		if(element.containingFile !is WxmlFile) return true
		return when {
			element.errorDescription in errors ||
				element.multiRootElement ||
				element.wxKeywords -> false
			else -> true
		}
	}
}

inline val PsiErrorElement.multiRootElement: Boolean
	get() = this.parent is XmlTag && this.parent?.parent is XmlDocument

@Suppress("unsupported")
val errors = [
	"Unescaped & or nonterminated character/entity reference",
	"'=' expected",
	"Should have prefix ':'"
]

inline val PsiErrorElement.wxKeywords: Boolean
	get() = this.prevSibling?.run { text.startsWith("wx:") } ?: false
