//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
@file:Suppress("SENSELESS_COMPARISON")

package com.intellij.psi.css.impl.util.scheme

import com.google.common.base.Splitter
import com.google.common.base.Strings
import com.google.common.collect.Sets
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.css.*
import com.intellij.psi.css.descriptor.*
import com.intellij.psi.css.descriptor.CssElementDescriptor.CssVersion
import com.intellij.psi.css.descriptor.value.CssValueDescriptor
import com.intellij.psi.css.descriptor.value.CssValueType
import com.intellij.psi.css.impl.CssTermTypeImpl
import com.intellij.psi.css.impl.descriptor.*
import com.intellij.psi.css.impl.descriptor.value.*
import com.intellij.psi.css.impl.descriptor.value.CssGroupValue.Type
import com.intellij.psi.css.impl.util.table.CssElementDescriptorConstants
import com.intellij.util.Function
import com.intellij.util.Functions
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.ContainerUtilRt
import com.intellij.util.io.URLUtil
import org.jdom.*
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class WxssDescriptorsLoader(private val myProgressIndicator: ProgressIndicator?) {
	private val myDescriptors = CssDescriptorsHolder()

	val descriptors: CssDescriptorsHolder
		get() = myDescriptors

	fun loadDescriptors(resource: URL) {
		try {
			val var2 = load(resource).children.iterator()
			while (var2.hasNext()) {
				val element = var2.next() as Element
				this.checkCancelled()
				val tagName = element.name
				if ("property" == tagName) {
					this.loadPropertyDescriptor(element)
				} else if ("prefixed-property" == tagName) {
					this.loadPrefixedPropertyDescriptor(element)
				} else if ("pseudo-element" == tagName) {
					this.loadPseudoElementDescriptor(element)
				} else if ("pseudo-class" == tagName) {
					this.loadPseudoClassDescriptor(element)
				} else if ("function" == tagName) {
					this.loadFunctionDescriptor(element)
				} else if ("prefixed-function" == tagName) {
					this.loadPrefixedFunctionDescriptor(element)
				} else if ("named-value" == tagName) {
					this.loadNamedValueDescriptor(element)
				} else if ("media-feature" == tagName) {
					this.loadMediaFeatureDescriptor(element)
				}
			}
		} catch (var5: JDOMException) {
			LOG.error("Error loading " + resource.path, var5)
		} catch (var6: IOException) {
			LOG.error("Error loading " + resource.path, var6)
		}

	}

	private fun loadPropertyDescriptor(element: Element) {
		if (element == null) {

		}

		val name = element.getAttributeValue("id")
		val mediaGroup = parseMediaGroups(element)
		val initialValue = element.getAttributeValue("initial")
		val appliesToValue = element.getAttributeValue("applies")
		val percentageValue = element.getAttributeValue("percentage")
		val inherited = parseBoolean(element, "inherited", false)
		this.myDescriptors.properties.putValue(name.toLowerCase(Locale.US), CssPropertyDescriptorImplEx(parseCommonDescriptorData(element), this.loadValueOfElement(element), initialValue, appliesToValue, percentageValue, inherited, mediaGroup))
	}

	private fun loadPrefixedPropertyDescriptor(element: Element) {
		val name = element.getAttributeValue("id")
		val descriptors = ContainerUtil.newArrayList(this.myDescriptors.properties.get(name))
		if (descriptors.isEmpty()) {
			LOG.error("Property should be declared before using prefixed declaration.", *arrayOf(name))
		} else {
			val originalProperty = ContainerUtil.getLastItem<CssPropertyDescriptor, List<CssPropertyDescriptor>>(descriptors)
			val prefix = element.getAttributeValue("prefix")
			val propertyName = "-$prefix-$name"
			val browserVersions = parseBrowsers(element)

			assert(originalProperty != null)

			var url: String? = element.getAttributeValue("url")
			val cssVersion = if (url != null) originalProperty!!.cssVersion else CssVersion.UNKNOWN
			if (url == null) {
				url = originalProperty!!.specificationUrl
			}

			val commonDescriptorData = CssCommonDescriptorData(propertyName, "-" + prefix + "-" + originalProperty!!.presentableName, originalProperty.allowedContextTypes, browserVersions, cssVersion, url, originalProperty.description)
			this.myDescriptors.properties.putValue(propertyName.toLowerCase(Locale.US), CssPropertyDescriptorImplEx(commonDescriptorData, originalProperty.valueDescriptor, originalProperty.initialValue, originalProperty.appliesToValue, originalProperty.percentageValue, originalProperty.isInherited, originalProperty.mediaGroups))
		}
	}

	private fun loadFunctionDescriptor(element: Element) {
		val name = element.getAttributeValue("id")
		val returnTypeName = element.getAttributeValue("returnType")
		val returnType = if (StringUtil.isNotEmpty(returnTypeName)) CssTermTypeImpl.find(returnTypeName) else CssTermType.UNKNOWN
		this.myDescriptors.functions.putValue(name.toLowerCase(Locale.US), CssFunctionDescriptorImpl(parseCommonDescriptorData(element, "", PROCESS_FUNCTION_PRESENTABLE_NAME), this.loadValueOfElement(element), returnType))
	}

	private fun loadPrefixedFunctionDescriptor(element: Element) {
		if (element == null) {

		}

		val name = element.getAttributeValue("id")
		val descriptors = this.myDescriptors.functions.get(name)
		if (descriptors.size != 1) {
			LOG.error("Function should be declared before using prefixed declaration.", *arrayOf(name))
		} else {
			val originalFunction = ContainerUtil.getFirstItem(descriptors)
			val prefix = element.getAttributeValue("prefix")
			val functionName = "-$prefix-$name"
			val browserVersions = parseBrowsers(element)

			assert(originalFunction != null)

			var url: String? = element.getAttributeValue("url")
			val cssVersion = if (url != null) originalFunction!!.cssVersion else CssVersion.UNKNOWN
			if (url == null) {
				url = originalFunction!!.specificationUrl
			}

			val commonDescriptorData = CssCommonDescriptorData(functionName, "-" + prefix + "-" + originalFunction!!.presentableName, originalFunction.allowedContextTypes, browserVersions, cssVersion, url, originalFunction.description)
			this.myDescriptors.functions.putValue(functionName.toLowerCase(Locale.US), CssFunctionDescriptorImpl(commonDescriptorData, originalFunction.valueDescriptor, originalFunction.type))
		}
	}

	private fun loadMediaFeatureDescriptor(element: Element) {
		if (element == null) {

		}

		val name = element.getAttributeValue("id").toLowerCase(Locale.US)
		val valueDescriptor = this.loadValueOfElement(element)
		val commonDescriptorData = parseCommonDescriptorData(element)
		val mediaGroups = parseMediaGroups(element)
		val appliesToValue = element.getAttributeValue("applies")
		this.myDescriptors.mediaFeatures.putValue(name, CssMediaFeatureDescriptorImpl(appliesToValue, mediaGroups, commonDescriptorData, valueDescriptor))
		val acceptsMinMaxPrefixed = parseBoolean(element, "min-max", false)
		if (acceptsMinMaxPrefixed) {
			this.myDescriptors.mediaFeatures.putValue("min-$name", CssMediaFeatureDescriptorImpl(appliesToValue, mediaGroups, commonDescriptorData.cloneWithPrefix("min-"), valueDescriptor))
			this.myDescriptors.mediaFeatures.putValue("max-$name", CssMediaFeatureDescriptorImpl(appliesToValue, mediaGroups, commonDescriptorData.cloneWithPrefix("max-"), valueDescriptor))
		}

	}

	private fun loadNamedValueDescriptor(element: Element) {
		if (element == null) {

		}

		val name = element.getAttributeValue("id")
		val valueDescriptor = CssValueDescriptorModificator.withCommonData(this.loadValueOfElement(element), parseCommonDescriptorData(element))
		this.myDescriptors.namedValues.putValue(name.toLowerCase(Locale.US), valueDescriptor)
	}

	private fun loadPseudoClassDescriptor(element: Element) {
		if (element == null) {

		}

		val hasArguments = element.children.size > element.getChildren("description", myNamespace).size
		val name = element.getAttributeValue("id")
		this.myDescriptors.pseudoSelectors.putValue(name.toLowerCase(Locale.US), CssPseudoClassDescriptorImpl(parseCommonDescriptorData(element), parseBoolean(element, "elementRequired", false), hasArguments))
	}

	private fun loadPseudoElementDescriptor(element: Element) {
		if (element == null) {

		}

		val name = element.getAttributeValue("id")
		val hasArguments = element.children.size > element.getChildren("description", myNamespace).size
		this.myDescriptors.pseudoSelectors.putValue(name.toLowerCase(Locale.US), CssPseudoElementDescriptorImpl(parseCommonDescriptorData(element), parseBoolean(element, "elementRequired", false), hasArguments))
	}

	private fun loadValueOfElement(valueOwnerElement: Element): CssValueDescriptor {
		if (valueOwnerElement == null) {

		}

		val children = valueOwnerElement.children
		val var3 = children.iterator()

		var valueDescriptor: CssValueDescriptor?
		do {
			if (!var3.hasNext()) {
				throw IllegalArgumentException("Value cannot be empty")
			}

			val child = var3.next() as Element
			valueDescriptor = this.parseValue(child, null as CssValueDescriptor?)
		} while (valueDescriptor == null)

		if (valueDescriptor == null) {

		}

		return valueDescriptor
	}

	private fun parseValue(valueElement: Element, parent: CssValueDescriptor?): CssValueDescriptor? {
		if (valueElement == null) {

		}

		this.checkCancelled()
		val name = valueElement.name
		if ("description".equals(name, ignoreCase = true)) {
			return null
		} else {
			val valueType = CssValueType.fromTag(name)
			return if (valueType == null) {
				throw IllegalArgumentException("Unknown value: $valueElement")
			} else {
				when (valueType) {
					CssValueType.GROUP -> this.parseGroupValue(valueElement, parent)
					CssValueType.INLINE -> this.parseInlineValue(valueElement, parent)
					CssValueType.URI -> this.parseUriValue(valueElement, parent)
					CssValueType.STRING -> this.parseStringValue(valueElement, parent)
					CssValueType.INTEGER -> this.parseIntegerValue(valueElement, parent)
					CssValueType.PERCENTAGE -> this.parsePercentageValue(valueElement, parent)
					CssValueType.NAME -> this.parseNameValue(valueElement, parent)
					CssValueType.PROPERTY -> this.parsePropertyReferenceValue(valueElement, parent)
					CssValueType.ANGLE -> this.parseAngleValue(valueElement, parent)
					CssValueType.FREQUENCY -> this.parseFrequencyValue(valueElement, parent)
					CssValueType.POSITION -> this.parsePositionValue(valueElement, parent)
					CssValueType.RESOLUTION -> this.parseResolutionValue(valueElement, parent)
					CssValueType.LENGTH -> this.parseLengthValue(valueElement, parent)
					CssValueType.NUMBER -> this.parseNumberValue(valueElement, parent)
					CssValueType.TIME -> this.parseTimeValue(valueElement, parent)
					CssValueType.DECIBEL -> this.parseDecibelValue(valueElement, parent)
					CssValueType.SEMITONES -> this.parseSemitoneValue(valueElement, parent)
					CssValueType.FLEX -> this.parseFlexValue(valueElement, parent)
					CssValueType.URANGE -> this.parseUrangeValue(valueElement, parent)
					CssValueType.INVOKE -> this.parseFunctionValue(valueElement, parent)
					CssValueType.TEXT -> this.parseTextValue(valueElement, parent)
					CssValueType.COLOR -> this.parseColorValue(valueElement, parent)
					CssValueType.EXPRESSION -> this.parseExpressionValue(valueElement, parent)
					CssValueType.JAVASCRIPT -> this.parseJavaScriptValue(valueElement, parent)
					CssValueType.SELECTOR -> this.parseSelectorValue(valueElement, parent)
					CssValueType.ANY -> this.parseAnyValue(valueElement, parent)
					else -> throw IllegalArgumentException("Unknown value: $valueElement")
				}
			}
		}
	}

	private fun checkCancelled() {
		if (this.myProgressIndicator != null) {
			this.myProgressIndicator.checkCanceled()
		}

	}

	private fun parseFunctionValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssFunctionInvocationValue(element.getAttributeValue("name"), parseCommonDescriptorData(element, "", PROCESS_FUNCTION_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parsePropertyReferenceValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val refId = element.getAttributeValue("id")

		assert(StringUtil.isNotEmpty(refId))

		val var10000 = CssPropertyReferenceValue(refId, parseCommonDescriptorData(element, refId, PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseNameValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val value = element.getAttributeValue("value")
		ContainerUtil.addIfNotNull(this.myDescriptors.valueIdentifiers, value)
		val var10000 = CssNameValue(value, false, parseCommonDescriptorData(element, CssBundle.message("name.value.presentable.name", *arrayOfNulls(0))), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseJavaScriptValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssJavaScriptValue(parseCommonDescriptorData(element, CssBundle.message("java.script.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseAngleValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssAngleValue(parseCommonDescriptorData(element, CssBundle.message("angle.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseFrequencyValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssFrequencyValue(parseCommonDescriptorData(element, CssBundle.message("frequency.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parsePositionValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssPositionValue(parseCommonDescriptorData(element, CssBundle.message("position.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseResolutionValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssResolutionValue(parseCommonDescriptorData(element, CssBundle.message("resolution.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseLengthValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssLengthValue(parseCommonDescriptorData(element, CssBundle.message("length.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseNumberValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssNumberValue(parseCommonDescriptorData(element, CssBundle.message("number.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseTimeValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssTimeValue(parseCommonDescriptorData(element, CssBundle.message("time.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseDecibelValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssDecibelValue(parseCommonDescriptorData(element, CssBundle.message("decibel.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseSemitoneValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssSemitonesValue(parseCommonDescriptorData(element, CssBundle.message("semitones.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseFlexValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssFlexValue(parseCommonDescriptorData(element, CssBundle.message("flex.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseUrangeValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssUrangeValue(parseCommonDescriptorData(element, CssBundle.message("urange.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parsePercentageValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssPercentageValue(parseCommonDescriptorData(element, CssBundle.message("percentage.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseColorValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssColorValue(parseCommonDescriptorData(element, CssBundle.message("color.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent), true)
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseAnyValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssAnyValueImpl(parseCommonDescriptorData(element, CssBundle.message("any.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseIntegerValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssIntegerValue(parseCommonDescriptorData(element, CssBundle.message("integer.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseStringValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssStringValue(element.getAttributeValue("value"), parseCommonDescriptorData(element, CssBundle.message("string.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseTextValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val value = element.getAttributeValue("value")
		ContainerUtil.addIfNotNull(this.myDescriptors.valueIdentifiers, value)
		val var10000 = CssTextValue(value, parseCommonDescriptorData(element), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseExpressionValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssExpressionValue(parseCommonDescriptorData(element, CssBundle.message("expression.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseSelectorValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssSelectorValue(parseCommonDescriptorData(element, CssBundle.message("selector.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseUriValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (element == null) {

		}

		val var10000 = CssUrlValue(parseCommonDescriptorData(element, CssBundle.message("url.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
		if (var10000 == null) {

		}

		return var10000
	}

	private fun parseInlineValue(element: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		val inline = parseBoolean(element, "inline", false)
		val name = element.getAttributeValue("id")
		if ("id".equals(name, ignoreCase = true)) {
			return CssIdValue(parseCommonDescriptorData(element, CssBundle.message("id.value.presentable.name", *arrayOfNulls(0)), PROCESS_VALUE_PRESENTABLE_NAME), parseValueDescriptorData(element, parent))
		} else {
			val inlineValue = CssInlineValue(name, parseCommonDescriptorData(element, name, PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(element, parent))
			if (inline) {
				println("by :$name")
				val namedValues = this.myDescriptors.namedValues.get(name)
				if (namedValues.size == 1) {
					val namedValue = ContainerUtil.getFirstItem(namedValues)
					if (namedValue != null) {
						val result = CssValueDescriptorModificator.withParent(namedValue, parent)
						return if (!inlineValue.isCommaSeparated && inlineValue.minOccur == 1 && inlineValue.maxOccur == 1) {
							result
						} else CssValueDescriptorModificator.withQuantifiers(result, inlineValue.minOccur, inlineValue.maxOccur, inlineValue.isCommaSeparated)
					}
				}
				LOG.error("Named value should be declared before inlining.", *arrayOf(name))
			}

			if (inlineValue == null) {

			}

			return inlineValue
		}
	}

	private fun parseGroupValue(valueElement: Element, parent: CssValueDescriptor?): CssValueDescriptor {
		if (valueElement == null) {

		}

		val cssGroupValue = CssGroupValue.create(parseCommonDescriptorData(valueElement, "", PROCESS_VALUE_PRESENTABLE_NAME), this.parseValueDescriptorData(valueElement, parent), parseBoolean(valueElement, "ignoreWhitespaces", true), this.parseSeparator(valueElement), parseGroupType(valueElement))
		val var4 = valueElement.children.iterator()

		while (var4.hasNext()) {
			val child = var4.next() as Element
			if ("separator" != child.name) {
				val descriptor = this.parseValue(child, cssGroupValue)
				if (descriptor != null) {
					cssGroupValue.addChild(descriptor)
				}
			}
		}

		if (cssGroupValue == null) {

		}

		return cssGroupValue
	}

	private fun parseSeparator(element: Element): CssValueDescriptor? {
		if (element == null) {

		}

		val separatorTag = element.getChild("separator", myNamespace)
		return if (separatorTag != null) this.loadValueOfElement(separatorTag) else null
	}

	private fun parseExclusion(element: Element): CssValueDescriptor? {
		if (element == null) {

		}

		val exclusionTag = element.getChild("exclusion", myNamespace)
		return if (exclusionTag != null) this.loadValueOfElement(exclusionTag) else null
	}

	private fun parseValueDescriptorData(element: Element, parent: CssValueDescriptor?): CssValueDescriptorData {
		if (element == null) {

		}

		val commaSeparated = parseBoolean(element, "comma-separated", false)
		val minOccur = parseInt(element, "min", 1)
		var maxOccur = parseInt(element, "max", if (commaSeparated) -1 else 1)
		if (maxOccur != -1 && maxOccur < minOccur) {
			maxOccur = minOccur
		}

		val var10000 = CssValueDescriptorData(parseBoolean(element, "completion", true), minOccur, maxOccur, parseObsoleteVersion(element), processDocumentation(StringUtil.escapeXml(element.getAttributeValue("obsolete-tooltip"))), parent, this.parseExclusion(element), commaSeparated)
		if (var10000 == null) {

		}

		return var10000
	}

	companion object {
		val myNamespace: Namespace = Namespace.getNamespace("urn:schemas-jetbrains-com:css-xml");
		private val LOG = Logger.getInstance(WxssDescriptorsLoader::class.java)
		private val PROCESS_VALUE_PRESENTABLE_NAME: Function<String, String> = Function { presentableName -> if (!Strings.isNullOrEmpty(presentableName) && !StringUtil.startsWithChar(presentableName, '<') && presentableName.length > 1) "<$presentableName>" else presentableName }
		private val PROCESS_FUNCTION_PRESENTABLE_NAME: Function<String, String> = Function { presentableName -> presentableName + "()" }
		private val ANY_AT_RULE_TYPES: Array<CssContextType>
		private val COMMA_SPLITTER: Splitter
		private val DOCUMENTATION_LINK_PATTERN: Pattern

		@Throws(JDOMException::class, IOException::class)
		fun load(url: URL): Element {
			return JDOMUtil.load(URLUtil.openStream(url))
		}

		private fun parseBoolean(element: Element, attributeName: String, defaultValue: Boolean): Boolean {
			val attributeValue = element.getAttributeValue(attributeName)
			return if (attributeValue != null) "yes" == attributeValue else defaultValue
		}

		private fun parseInt(element: Element, attributeName: String, defaultValue: Int): Int {
			if (element == null) {

			}

			try {
				val attribute = element.getAttribute(attributeName)
				return attribute?.intValue ?: defaultValue
			} catch (var4: DataConversionException) {
				return defaultValue
			}

		}

		private fun parseContextTypes(element: Element): Array<CssContextType> {
			if (element == null) {

			}

			val rules = element.getAttributeValue("rules")
			val var10000: Array<CssContextType>?
			if (Strings.isNullOrEmpty(rules)) {
				var10000 = ANY_AT_RULE_TYPES
				if (var10000 == null) {

				}

				return var10000
			} else {
				val result = Sets.newTreeSet(CssContextType.COMPARATOR)
				val var3 = COMMA_SPLITTER.split(rules).iterator()

				while (var3.hasNext()) {
					val rule = var3.next() as String
					result.add(CssContextType.fromString(rule))
				}

				var10000 = result.toTypedArray()
				if (var10000 == null) {

				}

				return var10000
			}
		}

		private fun parseVersion(element: Element): CssVersion {
			if (element == null) {

			}

			val var10000 = CssVersion.fromString(element.getAttributeValue("declared-in"))
			if (var10000 == null) {

			}

			return var10000
		}

		private fun parseObsoleteVersion(element: Element): CssVersion? {
			if (element == null) {

			}

			val obsoleteInValue = element.getAttributeValue("obsolete-in")
			return if (obsoleteInValue != null) CssVersion.fromString(obsoleteInValue) else null
		}

		@JvmStatic
		private fun parseDescription(element: Element): String? {
			if (element == null) {

			}

			val descriptionTag = element.getChild("description", myNamespace)
			return if (descriptionTag != null) processDocumentation(descriptionTag.textTrim) else processDocumentation(StringUtil.escapeXml(element.getAttributeValue("tooltip")))
		}

		private fun processDocumentation(result: String?): String? {
			var result = result
			if (result != null && !result.isEmpty()) {
				val matcher = DOCUMENTATION_LINK_PATTERN.matcher(result)
				if (matcher.find()) {
					result = matcher.replaceAll("<a href=\"psi_element://$1\">$2</a>")
				}

				return result
			} else {
				return null
			}
		}

		private fun parseBrowsers(element: Element): Array<BrowserVersion> {
			if (element == null) {

			}

			val browsers = element.getAttributeValue("browsers")
			if (Strings.isNullOrEmpty(browsers)) {
				return BrowserVersion.EMPTY_ARRAY
			} else {
				val result = ContainerUtilRt.newTreeSet(BrowserVersion.COMPARATOR)
				val var3 = COMMA_SPLITTER.split(browsers).iterator()

				while (var3.hasNext()) {
					val browser = var3.next() as String
					result.add(BrowserVersion.fromString(browser))
				}

				return result.toTypedArray()
			}
		}

		private fun parseGroupType(element: Element): Type {
			if (element == null) {

			}

			val typeAttributeValue = element.getAttributeValue("type")
			val var10000 = if (Strings.isNullOrEmpty(typeAttributeValue)) Type.ALL else Type.valueOf(typeAttributeValue.toUpperCase(Locale.US))
			if (var10000 == null) {

			}

			return var10000
		}

		private fun parseCommonDescriptorData(element: Element): CssCommonDescriptorData {
			if (element == null) {

			}

			val var10000 = parseCommonDescriptorData(element, "", Functions.id())
			if (var10000 == null) {

			}

			return var10000
		}

		private fun parseCommonDescriptorData(element: Element, defaultPresentableName: String): CssCommonDescriptorData {
			if (element == null) {

			}

			if (defaultPresentableName == null) {

			}

			val var10000 = parseCommonDescriptorData(element, defaultPresentableName, Functions.id())
			if (var10000 == null) {

			}

			return var10000
		}

		private fun parseCommonDescriptorData(element: Element, defaultPresentableName: String, processPresentableName: Function<String, String>): CssCommonDescriptorData {
			if (element == null) {

			}

			if (defaultPresentableName == null) {

			}

			if (processPresentableName == null) {

			}

			val id = element.getAttributeValue("id", defaultPresentableName)
			val valueAttribute = element.getAttributeValue("value", id)
			val presentableName = processPresentableName.`fun`(element.getAttributeValue("name", valueAttribute)) as String
			val var10000 = CssCommonDescriptorData(id, presentableName, parseContextTypes(element), parseBrowsers(element), parseVersion(element), element.getAttributeValue("url"), Strings.nullToEmpty(parseDescription(element)))
			if (var10000 == null) {

			}

			return var10000
		}

		private fun parseMediaGroups(element: Element): Array<CssMediaGroup> {
			val result = ContainerUtil.newLinkedHashSet<CssMediaGroup>()
			val var2 = StringUtil.split(element.getAttributeValue("media", "all").toUpperCase(Locale.US), " ").iterator()

			while (var2.hasNext()) {
				val s = var2.next() as String

				try {
					result.add(CssMediaGroup.valueOf(s.trim { it <= ' ' }))
				} catch (var5: IllegalArgumentException) {
					Collections.addAll(result, *CssMediaType.valueOf(s.trim { it <= ' ' }).supportedGroups)
				}

			}

			val var10000 = if (!result.isEmpty()) result.toTypedArray() else CssElementDescriptorConstants.EMPTY_MEDIA_GROUP
			if (var10000 == null) {

			}

			return var10000
		}

		init {
			ANY_AT_RULE_TYPES = arrayOf(CssContextType.ANY)
			COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings()
			DOCUMENTATION_LINK_PATTERN = Pattern.compile("<see[\n\r ]+cref=\"([^:]*:([^\"]+))\"\\s*/>")
		}
	}
}
