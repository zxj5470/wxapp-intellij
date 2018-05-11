package com.github.zxj5470.wxapp.registry.codeStyle

import com.github.zxj5470.wxapp.WxssLanguage
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider

/**
 * @author zxj5470
 * @date 2018/5/11
 */
class WxssLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
	override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
		consumer.showStandardOptions()
		super.customizeSettings(consumer, settingsType)
	}

	override fun getCodeSample(settingsType: SettingsType): String =
		"""view{
			|	margin-left: 10rpx;
			|	margin-top: 10rpx;
			|}""".trimMargin()

	override fun getLanguage(): Language = WxssLanguage.INSTANCE

}