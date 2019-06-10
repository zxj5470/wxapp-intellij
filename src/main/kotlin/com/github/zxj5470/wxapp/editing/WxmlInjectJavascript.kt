package com.github.zxj5470.wxapp.editing

import com.github.zxj5470.wxapp.ktlext.indicesOf
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.xml.*

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlInjectJavascript : LanguageInjector {
	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		when (host) {
			// xml content
			is XmlText -> {
				val text = (host as XmlText).text
				fragment(text, places)
			}
			// xml attr value
			is XmlAttributeValue -> {
				val value = host as XmlAttributeValue
				val text = value.text
				fragment(text, places)
				(value.parent as? XmlAttribute)?.let { attr ->
					if (attr.nameElement.text.startsWith("bind")) {
						places.addPlace(JavascriptLanguage.INSTANCE, TextRange(0, value.textLength), "call(", ")")
					}
				}
			}
		}
	}

	private fun fragment(text: String, places: InjectedLanguagePlaces) {
		val begin = text.indicesOf("{{")
		val end = text.indicesOf("}}")
		if (begin.size == end.size && begin.isNotEmpty()) {
			for (i in begin.indices) {
				val b = begin[i] + 2
				val e = end[i]
				places.addPlace(JavascriptLanguage.INSTANCE, TextRange(b, e), "get(", ")")
			}
		}
	}
}