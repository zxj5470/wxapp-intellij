package com.github.zxj5470.wxapp.ktlext

import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace

/**
 * @author zxj5470
 * @date 2018/5/12
 */
fun String.isWxFunction(): Boolean {
	if (this.isNotEmpty()) {
		if (this[0].isJavaIdentifierStart()) {
			return if (this.length > 1) {
				this.substring(1).all { it.isJavaIdentifierPart() }
			} else true
		}
	}
	return false
}

val PsiElement.prevRealSibling: PsiElement?
	get() {
		var pre = this.prevSibling
		while (pre != null) {
			if (pre is PsiWhiteSpace || pre.node.elementType.let { it == JSTokenTypes.XML_ATTR_EQUAL }) {
				pre = pre.prevSibling
			} else {
				return pre
			}
		}
		return pre
	}