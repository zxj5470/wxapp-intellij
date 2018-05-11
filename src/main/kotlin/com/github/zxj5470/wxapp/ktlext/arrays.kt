package com.github.zxj5470.wxapp.ktlext

/**
 * @author zxj5470
 * @date 2018/5/11
 */
fun <T> Array<out T>.lastOr(orValue: T): T =
	if (this.isNotEmpty()) {
		this[lastIndex]
	} else
		orValue

