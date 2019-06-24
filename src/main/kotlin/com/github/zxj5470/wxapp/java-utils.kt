@file:JvmName("WxUtil")

package com.github.zxj5470.wxapp

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

fun compare(version: String): Boolean {
	val str = version.substringBefore('.')
	return try {
		str.toInt() >= 183
	} catch (e: Exception) {
		false
	}
}

fun getIcon(string: String): Icon {
	val ver = ApplicationInfo.getInstance().build.asStringWithoutProductCode()
	return if (compare(ver)) {
		IconLoader.getIcon(string)
	} else {
		IconLoader.getIcon(string.replace(".svg", "-png.png"))
	}
}