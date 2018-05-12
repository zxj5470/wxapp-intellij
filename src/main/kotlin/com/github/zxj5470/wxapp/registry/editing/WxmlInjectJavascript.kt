package com.github.zxj5470.wxapp.registry.editing

import com.github.zxj5470.ktlext.text.indicesOf
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.xml.XmlTextImpl
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlText

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlInjectJavascript : LanguageInjector {
	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		when (host) {
			is XmlText -> {
				val h = (host as XmlTextImpl)
				val begin = h.text.indexOf("{{")
				val end = h.text.lastIndexOf("}}")
				if (begin != -1 && end != -1) {
					h.textRange.apply {
						//						places.addPlace(JavascriptLanguage.INSTANCE, TextRange(startOffset + begin + 2, startOffset + end - 1), "", "")
					}
				}
			}
			is XmlAttributeValue -> {
				(host as XmlAttributeValue).apply {
					text.let {
						val lprs = it.indicesOf("{{")
						val rprs = it.indicesOf("}}")
						if (lprs.size == 1 && rprs.size == 1) {
							val begin = lprs.first() + 2
							places.addPlace(JavascriptLanguage.INSTANCE, TextRange(begin, rprs.first()), "", "")
						} else {

						}
					}
					((host as XmlAttributeValue).parent as XmlAttribute).let { attr ->
						if (attr.nameElement.text.startsWith("bind")) {
							places.addPlace(JavascriptLanguage.INSTANCE, TextRange(0, textLength), "", "")
						}
					}
				}
			}
		}
	}
}