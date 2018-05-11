package com.github.zxj5470.wxapp.registry.editing

import com.github.zxj5470.wxapp.WxappIcons
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlCompletionContributor : XmlCompletionContributor() {

	private val tags by lazy {
		this.javaClass.getResource("/templates/xmlTags.txt")
			.readText().split("\n")
	}

	private val xmlTags = tags
		.map {
			LookupElementBuilder
				.create(it)
				.withIcon(WxappIcons.wxmlTagIcon)
				.withTypeText(it, true)
		}

	/**
	 * with attribute value
	 */
	private val wxKeywordsWithAttr = listOf(
		"wx:for",
		"wx:if",
		"wx:elif",
		"wx:key",
		"wx:for",
		"wx:for-index",
		"wx:for-item"
	).map {
		LookupElementBuilder
			.create(it)
			.withIcon(WxappIcons.weChatIcon)
			.withTypeText(it.substring(3), true)
			.withInsertHandler(WxmlAttributeInsertHandler())
	}

	/**
	 * No attribute value
	 */
	private val wxElse = listOf(
		"wx:else"
	).map {
		LookupElementBuilder
			.create(it)
			.withIcon(WxappIcons.weChatIcon)
			.withTypeText(it.substring(3), true)
	}

	private val values = listOf(
		"value"
	).map {
		LookupElementBuilder
			.create(it)
			.withIcon(WxappIcons.weChatIcon)
			.withTypeText(it.substring(3), true)
	}

	init {
		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java),
			WxmlCompletionProvider(wxKeywordsWithAttr))

		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java),
			WxmlCompletionProvider(xmlTags))

		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java),
			WxmlCompletionProvider(wxElse))

		extend(CompletionType.BASIC,
			psiElement().inside(XmlPatterns.xmlAttributeValue()),
			WxmlCompletionProvider(values)
		)
	}
}

open class WxmlCompletionProvider(private val list: List<LookupElement>) : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) =
		list.forEach(result::addElement)
}