package com.intellij.psi.css.impl.util.scheme

import com.intellij.psi.PsiElement
import com.intellij.psi.css.CssMediaFeatureDescriptor
import com.intellij.psi.css.CssPropertyDescriptor
import com.intellij.psi.css.descriptor.CssPseudoSelectorDescriptor
import com.intellij.psi.css.descriptor.value.CssValueDescriptor
import com.intellij.psi.css.impl.util.CssUtil
import com.intellij.psi.css.impl.util.table.CssDescriptorsUtil

/**
 * @author zxj5470
 * @date 2018/5/11
 */
class WxssElementDescriptorProvider : CssElementDescriptorProviderImpl() {

	override fun findPseudoSelectorDescriptors(name: String, context: PsiElement?): MutableCollection<out CssPseudoSelectorDescriptor> {
		return factory().findPseudoSelector(name) as MutableCollection<out CssPseudoSelectorDescriptor>
	}

	override fun getNamedValueDescriptors(name: String, parent: CssValueDescriptor?): MutableCollection<out CssValueDescriptor> {
		return factory().findNamedValue(name, parent) as MutableCollection<out CssValueDescriptor>
	}

	override fun findPropertyDescriptors(propertyName: String, context: PsiElement?): MutableCollection<out CssPropertyDescriptor> {
		val descriptorFactory = factory()
		val result = descriptorFactory.findProperty(propertyName)
		return when {
			!result.isEmpty() -> result as MutableCollection<out CssPropertyDescriptor>
			propertyName.length > 1 && CssUtil.isHackPropertyName(propertyName) -> {
				val var5 = descriptorFactory.findProperty(propertyName.substring(1))
				var5 as MutableCollection<out CssPropertyDescriptor>
			}
			else -> mutableListOf()
		}
	}

	override fun getAllPseudoSelectorDescriptors(context: PsiElement?): MutableCollection<out CssPseudoSelectorDescriptor> {
		return CssDescriptorsUtil.filterDescriptorsByContext(factory().getPseudoSelectors().values(), context)
	}

	override fun getAllPropertyDescriptors(context: PsiElement?): MutableCollection<out CssPropertyDescriptor> {
		return CssDescriptorsUtil.filterDescriptorsByMediaType(CssDescriptorsUtil.filterDescriptorsByContext(factory().properties.values(), context), context)
	}

	override fun getAllMediaFeatureDescriptors(context: PsiElement?): MutableCollection<out CssMediaFeatureDescriptor> {
		return CssDescriptorsUtil.filterDescriptorsByMediaType(CssDescriptorsUtil.filterDescriptorsByContext<CssMediaFeatureDescriptor>(factory().getMediaFeatures().values(), context), context)
	}

	companion object {
		private fun factory(): WxssElementDescriptorFactory2 {
			return WxssElementDescriptorFactory2.instance
		}
	}

}