package com.github.zxj5470.wxapp.editing

import com.github.zxj5470.wxapp.WxappIcons
import com.github.zxj5470.wxapp.WxssFile
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxssCompletionContributor : CompletionContributor() {

	private val tags by lazy {
		this.javaClass.getResource("/templates/wxmlTags.txt")
			.readText().split("\n")
	}

	private val xmlTags = tags
		.map {
			LookupElementBuilder
				.create(it)
				.withIcon(WxappIcons.wxmlTagIcon)
				.withTypeText("WeiXin $it", true)
		}

	private val rpx = listOf(
		"rpx"
	).map {
		LookupElementBuilder
			.create(it)
			.withIcon(WxappIcons.weChatIcon)
			.withTypeText("WeiXin Unit rpx", true)
			.prioritized(0x233333)
	}

	init {
		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement()
				.inside(WxssFile::class.java),
			WxmlCompletionProvider(rpx)
		)

		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement()
				.inside(WxssFile::class.java),
			WxmlCompletionProvider(xmlTags)
		)
	}
}

/**
 * convert a LookupElementBuilder into a Prioritized LookupElement
 * @receiver [LookupElementBuilder]
 * @param priority [Int] the priority of current LookupElementBuilder
 * @return [LookupElement] Prioritized LookupElement
 */
fun LookupElementBuilder.prioritized(priority: Int): LookupElement = PrioritizedLookupElement.withPriority(this, priority.toDouble())
