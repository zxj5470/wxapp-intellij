package com.github.zxj5470.wxapp.registry.editing

import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.xml.XmlTextImpl
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlText

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlInjectJavascript : LanguageInjector {
	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		when (host) {
			is XmlAttributeValue -> {
				val h = (host as XmlAttributeValue)
				if (h.text.let {
						it.startsWith("{{") && it.endsWith("}}")
					}) {
					h.textRange.apply {
						places.addPlace(JavascriptLanguage.INSTANCE, TextRange(startOffset + 2, endOffset - 2), null, null)
					}
				}
			}
			is XmlText -> {
//				val h = (host as XmlTextImpl)
//				if (h.text.let {
//						it.startsWith("{{") && it.endsWith("}}")
//					}) {
//					h.textRange.apply {
//						places.addPlace(JavascriptLanguage.INSTANCE, TextRange(startOffset + 2, endOffset - 2), null, null)
//					}
//				}
			}
		}
	}
}