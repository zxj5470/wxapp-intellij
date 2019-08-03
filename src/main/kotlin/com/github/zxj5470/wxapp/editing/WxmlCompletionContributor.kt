package com.github.zxj5470.wxapp.editing

import com.github.zxj5470.wxapp.*
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.css.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN
import com.intellij.psi.xml.XmlTokenType.XML_NAME
import com.intellij.util.ProcessingContext

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlCompletionContributor : CompletionContributor(), DumbAware {

	private val tags by lazy {
		this.javaClass.getResource("/templates/wxmlTags.txt")
			.readText().split("\n")
	}

	private val attrs by lazy {
		this.javaClass.getResource("/templates/wxmlAttr.txt")
			.readText().split("\n")
	}

	private val xmlTags = tags
		.map {
			LookupElementBuilder
				.create(it)
				.withIcon(WxappIcons.wxmlTagIcon)
				.withTypeText("WeiXin $it", true)
		}

	/**
	 * with attribute value
	 */
	private val wxssWithAttr = attrs
		.map {
			LookupElementBuilder
				.create(it)
				.withIcon(WxappIcons.weChatIcon)
				.withTypeText("Wxml Attr $it", true)
				.withInsertHandler { context, item ->
					context.editor.apply {
						document.insertString(context.tailOffset, "=\"\"")
						caretModel.moveToOffset(context.tailOffset - 1)
					}
				}
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
			.withInsertHandler { context, item ->
				context.editor.apply {
					document.insertString(context.tailOffset, "=\"\"")
					caretModel.moveToOffset(context.tailOffset - 1)
				}
			}
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
			.withInsertHandler { context, item ->
				context.editor.apply {
					document.insertString(context.tailOffset, "=\"\"")
					caretModel.moveToOffset(caretModel.offset + 2)
				}
			}
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
				.inside(XmlToken::class.java),
			WxmlReferenceCompletionProvider())

		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java),
			WxmlCompletionProvider(wxKeywordsWithAttr))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java),
			WxmlCompletionProvider(wxssWithAttr))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java)
				.andNot(psiElement().afterSibling(psiElement(XML_NAME))),
			WxmlCompletionProvider(xmlTags))

		extend(CompletionType.BASIC,
			psiElement()
				.inside(XmlTag::class.java),
			WxmlCompletionProvider(wxElse))

		extend(CompletionType.BASIC,
			psiElement()
				.andOr(
					psiElement().inside(psiElement(XML_ATTRIBUTE_VALUE_TOKEN)),
					psiElement().inside(JSExpressionStatement::class.java)
				), WxmlCompletionProvider(values)
		)
	}
}

open class WxmlCompletionProvider(private val list: List<LookupElement>) : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) =
		list.forEach(result::addElement)
}

class WxmlReferenceCompletionProvider() : CompletionProvider<CompletionParameters>() {

	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val pos = parameters.originalPosition ?: return
		val kvPair = pos.parent?.parent ?: return
		val project = pos.project
		val attrName = kvPair.firstChild.text
		val name = pos.text.substringAfterLast(' ')
		when (attrName) {
			"id" -> {
				val wxssFile = pos.containingFile.containingDirectory.children.firstOrNull { it.language is WxssLanguage } as? WxssFile
				wxssFile?.let {
					val ret = PsiTreeUtil.findChildrenOfType(it, CssIdSelector::class.java)
					ret.asSequence().filter { it.text.contains(name, true) }
						.forEach { str ->
							result.addElement(LookupElementBuilder
								.create(str)
								.withIcon(str.getIcon(ICON_FLAG_VISIBILITY))
								.withTypeText(str.containingFile.name)
								.prioritized(0))
						}
				}
				val globalWxss = project.getUserData(APP_WXSS_KEY) ?: return
				val ret = PsiTreeUtil.findChildrenOfType(globalWxss, CssIdSelector::class.java)
				ret.asSequence().filter { it.text.contains(name, true) }
					.forEach { str ->
						result.addElement(LookupElementBuilder
							.create(str)
							.withIcon(str.getIcon(ICON_FLAG_VISIBILITY))
							.withTypeText(str.containingFile.name)
							.prioritized(0))
					}

			}
			"class" -> {
				val wxssFile = pos.containingFile.containingDirectory.children.firstOrNull { it.language is WxssLanguage } as? WxssFile
				wxssFile?.let {
					val ret = PsiTreeUtil.findChildrenOfType(it, CssClass::class.java)
					ret.asSequence().filter { it.text.contains(name, true) }
						.forEach { str ->
							result.addElement(LookupElementBuilder
								.create(str)
								.withIcon(str.getIcon(ICON_FLAG_VISIBILITY))
								.withTypeText(str.containingFile.name)
								.prioritized(0))
						}
				}
				val globalWxss = project.getUserData(APP_WXSS_KEY) ?: return
				val ret = PsiTreeUtil.findChildrenOfType(globalWxss, CssClass::class.java)
				ret.asSequence().filter { it.text.contains(name, true) }
					.forEach { str ->
						result.addElement(LookupElementBuilder
							.create(str)
							.withIcon(str.getIcon(ICON_FLAG_VISIBILITY))
							.withTypeText(str.containingFile.name)
							.prioritized(0))
					}
			}
		}
	}
}
