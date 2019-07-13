//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intellij.psi.css.impl.descriptor.value

import com.intellij.psi.css.descriptor.value.CssValueDescriptorVisitor
import com.intellij.psi.css.impl.descriptor.CssCommonDescriptorData

class CssColorValue(commonDescriptorData: CssCommonDescriptorData, valueDescriptorData: CssValueDescriptorData, val isAllColorKeywordsKnown: Boolean) : CssValueDescriptorBase(commonDescriptorData, valueDescriptorData) {

	override fun accept(visitor: CssValueDescriptorVisitor) {
		if (visitor is CssValueDescriptorVisitorImpl) {
			visitor.visitColorValue(this)
		} else {
			super.accept(visitor)
		}

	}
}
