package com.intellij.psi.css.impl.util.scheme
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.intellij.psi.css.CssMediaFeatureDescriptor
import com.intellij.psi.css.CssPropertyDescriptor
import com.intellij.psi.css.descriptor.CssFunctionDescriptor
import com.intellij.psi.css.descriptor.CssPseudoSelectorDescriptor
import com.intellij.psi.css.descriptor.value.CssValueDescriptor
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.MultiMap

class WxssDescriptorsHolder {
	val properties = MultiMap.createSmart<String, CssPropertyDescriptor>()
	val mediaFeatures = MultiMap.createSmart<String, CssMediaFeatureDescriptor>()
	val functions = MultiMap.createSmart<String, CssFunctionDescriptor>()
	val pseudoSelectors = MultiMap.createSmart<String, CssPseudoSelectorDescriptor>()
	val namedValues = MultiMap.createSmart<String, CssValueDescriptor>()
	internal val valueIdentifiers: HashSet<String> = ContainerUtil.newHashSet()
}
