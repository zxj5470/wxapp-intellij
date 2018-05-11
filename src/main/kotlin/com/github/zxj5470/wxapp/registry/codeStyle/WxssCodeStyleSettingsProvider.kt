package com.github.zxj5470.wxapp.registry.codeStyle

/**
 * @author zxj5470
 * @date 2018/5/11
 */
import com.github.zxj5470.wxapp.WxssLanguage
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.lang.Language
import com.intellij.lang.css.CSSLanguage
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import com.intellij.psi.css.codeStyle.CssCodeStyleConfigurable
import com.intellij.psi.css.codeStyle.CssCodeStylePanel
import com.intellij.psi.css.codeStyle.CssCodeStyleSettingsProvider


class WxssCodeStyleSettingsProvider : CssCodeStyleSettingsProvider() {
	override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings {
		return WxssCodeStyleSettings(settings)
	}

	override fun getConfigurableDisplayName(): String = "Wxml"

	override fun createSettingsPage(settings: CodeStyleSettings, originalSettings: CodeStyleSettings): Configurable {
		return WxssCodeStyleConfigurable(settings, originalSettings)
	}

	override fun getLanguage(): Language = WxssLanguage.INSTANCE
}


class WxssCodeStyleConfigurable(settings: CodeStyleSettings, cloneSettings: CodeStyleSettings) :
	CssCodeStyleConfigurable(settings, cloneSettings) {
	override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
		return WxssCodeStyleMainPanel(this.currentSettings, settings)
	}
}

class WxssCodeStyleMainPanel constructor(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) : TabbedLanguageCodeStylePanel(WxssLanguage.INSTANCE, currentSettings, settings) {

	override fun initTabs(settings: CodeStyleSettings) {
		this.addSpacesTab(settings)
		this.addIndentOptionsTab(settings)
		this.addTab(CssCodeStylePanel(settings))
	}
}

