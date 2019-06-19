@file:JvmName("Ext")

package com.lypeer.matchmaker

import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.types.JavaScriptDialectFileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

fun AnActionEvent.available(): Boolean {
	val e = this
	return e.getData(CommonDataKeys.PSI_FILE)?.fileType?.let {
		it is JavaScriptFileType || it is JavaScriptDialectFileType
	}
		?: false
}