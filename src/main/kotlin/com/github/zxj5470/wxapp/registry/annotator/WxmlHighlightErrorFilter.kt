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
		return when {
			element.errorDescription in errors ||
				element.multiRootElement ||
				element.equalsExpected -> false
			else -> true
		}
	}
}

inline val PsiErrorElement.multiRootElement: Boolean
	get() = this.parent.parent is XmlDocument && this.parent is XmlTag

val errors = arrayOf(
	"Unescaped & or nonterminated character/entity reference"
)

inline val PsiErrorElement.equalsExpected: Boolean
	get() = this.errorDescription == "'=' expected" && this.prevSibling.text.startsWith("wx:")