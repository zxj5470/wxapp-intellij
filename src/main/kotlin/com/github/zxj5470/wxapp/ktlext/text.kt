package com.github.zxj5470.wxapp.ktlext

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