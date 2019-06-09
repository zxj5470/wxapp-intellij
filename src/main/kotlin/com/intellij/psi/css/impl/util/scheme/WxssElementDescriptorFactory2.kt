//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intellij.psi.css.impl.util.scheme

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.css.CssBundle
import com.intellij.psi.css.CssMediaFeatureDescriptor
import com.intellij.psi.css.CssPropertyDescriptor
import com.intellij.psi.css.descriptor.BrowserVersion
import com.intellij.psi.css.descriptor.CssContextType
import com.intellij.psi.css.descriptor.CssElementDescriptor.CssVersion
import com.intellij.psi.css.descriptor.CssFunctionDescriptor
import com.intellij.psi.css.descriptor.CssPseudoSelectorDescriptor
import com.intellij.psi.css.descriptor.value.CssValueDescriptor
import com.intellij.psi.css.impl.descriptor.CssCommonDescriptorData
import com.intellij.psi.css.impl.descriptor.value.CssGroupValue
import com.intellij.psi.css.impl.descriptor.value.CssGroupValue.Type
import com.intellij.psi.css.impl.descriptor.value.CssNameValue
import com.intellij.psi.css.impl.descriptor.value.CssTextValue
import com.intellij.psi.css.impl.descriptor.value.CssValueDescriptorData
import com.intellij.reference.SoftReference
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.MultiMap
import java.lang.ref.Reference
import java.util.*

class WxssElementDescriptorFactory2 @JvmOverloads constructor(private val myProgressManager: ProgressManager?, vararg schemesToLoad: String = SCHEME_NAMES) {
	private val mySchemesToLoad: Array<out String> = schemesToLoad
	private var myCssDescriptorsHolderRef: Reference<CssDescriptorsHolder>? = null

	private val descriptors: CssDescriptorsHolder
		@Synchronized get() = SoftReference.dereference(this.myCssDescriptorsHolderRef) ?: this.reload()

	val properties: MultiMap<String, CssPropertyDescriptor>
		get() {
			return descriptors.properties
		}

	fun getPseudoSelectors(): MultiMap<String, CssPseudoSelectorDescriptor> {
		return descriptors.pseudoSelectors
	}

	fun getMediaFeatures(): MultiMap<String, CssMediaFeatureDescriptor> {
		return descriptors.mediaFeatures
	}

	val valueIdentifiers: Set<String>
		get() {
			return descriptors.valueIdentifiers
		}

	fun findPseudoSelector(name: String): Collection<CssPseudoSelectorDescriptor> {
		return descriptors.pseudoSelectors.get(name.toLowerCase(Locale.US))
	}

	fun findProperty(name: String): Collection<CssPropertyDescriptor> {
		return properties.get(name.toLowerCase(Locale.US))
	}

	fun findFunction(name: String): Collection<CssFunctionDescriptor> {
		return descriptors.functions.get(name.toLowerCase(Locale.US))
	}

	fun findMediaFeature(name: String): Collection<CssMediaFeatureDescriptor> {
		return descriptors.mediaFeatures.get(name.toLowerCase(Locale.US))
	}

	fun findNamedValue(name: String, parent: CssValueDescriptor?): Collection<CssValueDescriptor> {
		if (parent != null) {
			return ContainerUtil.map(descriptors.namedValues.get(name.toLowerCase(Locale.US))) { descriptor -> CssValueDescriptorModificator.withParent(descriptor, parent) }
		} else {
			return descriptors.namedValues.get(name.toLowerCase(Locale.US))
		}
	}
//
//	fun createToggleFunctionDescriptorFromPropertyDescriptor(descriptor: CssPropertyDescriptor): CssFunctionDescriptor {
//		return CssFunctionDescriptorImpl(CSS_TOGGLE_FUNCTION_COMMON_DATA, CssValueDescriptorModificator.withQuantifiers(descriptor.valueDescriptor, 1, -1, true), CssTermTypes.TOGGLE)
//	}
//
//	fun createVarFunctionDescriptorFromPropertyDescriptors(valueDescriptor: CssValueDescriptor): CssFunctionDescriptor {
//		val value = this.createGroupValue(Type.ALL, 1, 1, null as CssValueDescriptor?, null as CssValueDescriptor?)
//		value.addChild(this.createNameValueDescriptor(null as String?, CssBundle.message("custom.property.name.value.presentable.name", *arrayOfNulls(0)), 1, 1, value))
//		val defaultValue = this.createGroupValue(Type.ALL, 0, 1, value, null as CssValueDescriptor?)
//		defaultValue.addChild(this.createTextValueDescriptor(",", 1, 1, defaultValue))
//		defaultValue.addChild(CssValueDescriptorModificator.withParent(valueDescriptor, defaultValue))
//		value.addChild(defaultValue)
//		return CssFunctionDescriptorImpl(CSS_VAR_FUNCTION_COMMON_DATA, value, CssTermTypes.VAR)
//	}

	@Synchronized
	fun reload(): CssDescriptorsHolder {
		val loader = CssDescriptorsLoader(if (this.myProgressManager != null) this.myProgressManager.progressIndicator else null)
		val var2 = this.mySchemesToLoad
		val var3 = var2.size

		for (var4 in 0 until var3) {
			val scheme = var2[var4]
			val resource = this.javaClass.getResource("xml/$scheme")
			if (resource == null) {
				LOG.warn("Cannot find CSS scheme: $scheme")
			} else {
				loader.loadDescriptors(resource)
			}
		}

		val descriptors = loader.descriptors
		this.myCssDescriptorsHolderRef = SoftReference(descriptors)
		return descriptors
	}

	fun createGroupValue(type: Type, minOccur: Int, maxOccur: Int, parent: CssValueDescriptor?, separator: CssValueDescriptor?): CssGroupValue {
		val commonDescriptorData = CssCommonDescriptorData("",
			"", CssContextType.EMPTY_ARRAY, BrowserVersion.EMPTY_ARRAY, CssVersion.UNKNOWN, null as String?,
			"")
		val valueDescriptorData = CssValueDescriptorData(false, minOccur, maxOccur, null as CssVersion?, null as String?, parent, null as CssValueDescriptor?, false)
		return CssGroupValue.create(commonDescriptorData, valueDescriptorData, true, separator, type)
	}

	fun createTextValueDescriptor(text: String, minOccur: Int, maxOccur: Int, parent: CssValueDescriptor): CssTextValue {
		val commonDescriptorData = CssCommonDescriptorData(text, text, CssContextType.EMPTY_ARRAY, BrowserVersion.EMPTY_ARRAY, CssVersion.UNKNOWN, null as String?,
			"")
		val valueDescriptorData = CssValueDescriptorData(true, minOccur, maxOccur, null as CssVersion?, null as String?, parent, null as CssValueDescriptor?, false)
		return CssTextValue(text, commonDescriptorData, valueDescriptorData)
	}

	fun createNameValueDescriptor(id: String?, presentableName: String?, minOccur: Int, maxOccur: Int, parent: CssValueDescriptor): CssNameValue {
		val presentableName1 = StringUtil.notNullize(presentableName, CssBundle.message("name.value.presentable.name", *arrayOfNulls(0)))
		val commonDescriptorData = CssCommonDescriptorData(presentableName1, presentableName1, CssContextType.EMPTY_ARRAY, BrowserVersion.EMPTY_ARRAY, CssVersion.UNKNOWN, null as String?,
			"")
		val valueDescriptorData = CssValueDescriptorData(true, minOccur, maxOccur, null as CssVersion?, null as String?, parent, null as CssValueDescriptor?, false)
		return CssNameValue(id, true, commonDescriptorData, valueDescriptorData)
	}


	companion object {
		private val LOG = Logger.getInstance(WxssElementDescriptorFactory2::class.java)
		val SCHEME_NAMES = arrayOf("css-cascade-4.xml", "css1.0.xml", "css2.0.xml", "css2.1.xml", "css3.0.xml", "css-overflow-3.xml", "css-box.xml", "css3-gcpm.xml", "css-page-floats.xml", "css3-page.xml", "custom-elements.xml", "css3-images.xml", "css4-images.xml", "css3-line-grid.xml", "css3-mediaqueries.xml", "view-mode.xml", "css3-animations.xml", "css-device-adapt.xml", "css3-transitions.xml", "css3-transforms.xml", "css-text-3.xml", "css-ruby-1.xml", "css3-speech.xml", "css3-ui.xml", "css-counter-styles-3.xml", "css-lists-3.xml", "css3-filter-effects.xml", "css-scoping1.xml", "css-regions.xml", "css3-break.xml", "css3-flexbox.xml", "css3-grid.xml", "css-masking-1.xml", "css-shapes-1.xml", "css3-background.xml", "css-display-3.xml", "css3-fonts.xml", "fontface.xml", "css-text-decor-3.xml", "css3-preslev.xml", "css3-positioning.xml", "css-compositing-1.xml", "css3-align.xml", "ie.xml", "css3-writing-modes.xml", "jquery.xml", "mozilla.xml", "mso.xml", "opera.xml", "svg.xml", "webkit.xml", "css-snappoints-1.xml", "css-will-change.xml", "css-inline-3.xml", "appmanifest.xml", "css-color-4.xml", "css-selectors-4.xml", "cssModules.xml", "css-contain-1.xml", "cssom-view-1.xml", "mediaqueries4.xml")
		val instance: WxssElementDescriptorFactory2
			get() = ServiceManager.getService(WxssElementDescriptorFactory2::class.java) as WxssElementDescriptorFactory2
	}
}
