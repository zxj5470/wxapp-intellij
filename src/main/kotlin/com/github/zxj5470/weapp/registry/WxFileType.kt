package com.github.zxj5470.weapp.registry

import com.github.zxj5470.weapp.WeappIcons
import com.github.zxj5470.weapp.WxssLanguage
import com.github.zxj5470.weapp.constants.*
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType

object WxssFileType : LanguageFileType(WxssLanguage.INSTANCE) {
	override fun getDefaultExtension() = "wxss"
	override fun getName() = "Wxss"
	override fun getIcon() = WeappIcons.wxssIcon
	override fun getDescription() = "WeiXin Style Sheet"
}

object WxmlFileType : LanguageFileType(WxssLanguage.INSTANCE) {
	override fun getDefaultExtension() = WXML_EXT
	override fun getName() = "Wxss"
	override fun getIcon() = WeappIcons.wxmlIcon
	override fun getDescription() = "WeiXin Markup Language"
}

class WeappFileFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
//		consumer.consume(WxssFileType, WXSS_EXT)
		consumer.consume(WxmlFileType)
	}
}