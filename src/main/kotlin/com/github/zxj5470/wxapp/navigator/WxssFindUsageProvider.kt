package com.github.zxj5470.wxapp.navigator

import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.findUsages.JSWordsScanner
import com.intellij.psi.ElementDescriptionUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.css.*
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewShortNameLocation

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxssFindUsageProvider : FindUsagesProvider {

	override fun canFindUsagesFor(element: PsiElement): Boolean =
		element is CssKeyframesRule ||
			element is CssCustomMixin ||
			element is CssDeclaration &&
			element.isCustomProperty ||
			element is CssSelectorSuffix &&
			element.type != CssSelectorSuffixType.UNKNOWN ||
			element is CssValueDeclaration ||
			element is CssValueImportedAlias

	override fun getHelpId(element: PsiElement): String? = "reference.dialogs.findUsages.other"

	override fun getType(element: PsiElement): String {
		val var10000: String?
		when {
			element is CssKeyframesRule -> {
				var10000 = CssBundle.message("css.terms.animation", *arrayOfNulls(0))
				return var10000
			}
			element is CssSelectorSuffix -> {
				var10000 = CssBundle.message("css.terms.selector", *arrayOfNulls(0))
				return var10000
			}
			element is CssRuleset -> {
				var10000 = CssBundle.message("css.terms.ruleset", *arrayOfNulls(0))
				return var10000
			}
			element is CssCustomMixin -> {
				var10000 = CssBundle.message("css.terms.custom.property.set", *arrayOfNulls(0))
				return var10000
			}
			element is CssDeclaration -> {
				var10000 = CssBundle.message(if (element.isCustomProperty) "css.terms.variable" else "css.terms.property", *arrayOfNulls(0))
				return var10000
			}
			element is CssAtRule -> {
				var10000 = element.name
				return var10000
			}
			element !is CssValueDeclaration && element !is CssValueImportedAlias -> return ""
			else -> {
				var10000 = CssBundle.message("css.value.defined.in.at.value.rule", *arrayOfNulls(0))
				return var10000
			}
		}
	}

	override fun getDescriptiveName(element: PsiElement): String =
		ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE)

	override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
		ElementDescriptionUtil.getElementDescription(element, UsageViewShortNameLocation.INSTANCE)

	override fun getWordsScanner(): WordsScanner? = null
}
