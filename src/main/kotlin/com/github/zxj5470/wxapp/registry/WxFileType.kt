package com.github.zxj5470.wxapp.registry

import com.github.zxj5470.wxapp.WxappIcons
import com.github.zxj5470.wxapp.WxmlLanguage
import com.github.zxj5470.wxapp.WxssLanguage
import com.github.zxj5470.wxapp.constants.*
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType

object WxssFileType : LanguageFileType(WxssLanguage.INSTANCE) {
	override fun getDefaultExtension() = "wxss"
	override fun getName() = "Wxss"
	override fun getIcon() = WxappIcons.wxssIcon
	override fun getDescription() = "WeiXin Style Sheet"
}

object WxmlFileType : LanguageFileType(WxmlLanguage.INSTANCE) {
	override fun getDefaultExtension() = WXML_EXT
	override fun getName() = "Wxml"
	override fun getIcon() = WxappIcons.wxmlIcon
	override fun getDescription() = "WeiXin Markup Language"
}

class WxappFileFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
//		consumer.consume(WxssFileType, WXSS_EXT)
		consumer.consume(WxmlFileType)
	}
}