package com.github.zxj5470.wxapp.editing

import com.github.zxj5470.wxapp.ktlext.indicesOf
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.xml.XmlTextImpl
import com.intellij.psi.xml.*

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlInjectJavascript : LanguageInjector {
	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		when (host) {
			is XmlText -> {
				val h = (host as XmlTextImpl)
				val begin = h.text.indicesOf("{{")
				val end = h.text.indicesOf("}}")
				if (begin.size == end.size && begin.isNotEmpty()) {
					for (i in begin.indices) {
						val b = begin[i] + 2
						val e = end[i]
						places.addPlace(JavascriptLanguage.INSTANCE, TextRange(b, e), "const _privateWxapp=", null)
					}
				}
			}
			is XmlAttributeValue -> {
				val attrvalue = (host as XmlAttributeValue)
				attrvalue.apply {
					text.let {
						val lprs = it.indicesOf("{{")
						val rprs = it.indicesOf("}}")
						if (lprs.size == 1 && rprs.size == 1) {
							val begin = lprs.first() + 2
							println("$text: $begin ")
							places.addPlace(JavascriptLanguage.INSTANCE, TextRange(begin, rprs.first()), "const _privateWxapp=", null)
						} else {

						}
					}
					((host as XmlAttributeValue).parent as XmlAttribute).let { attr ->
						if (attr.nameElement.text.startsWith("bind")) {
							places.addPlace(JavascriptLanguage.INSTANCE, TextRange(0, textLength), "const _=", "")
						}
					}
				}
			}
		}
	}
}