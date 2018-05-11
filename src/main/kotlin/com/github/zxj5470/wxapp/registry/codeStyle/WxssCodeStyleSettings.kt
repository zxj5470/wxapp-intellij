package com.github.zxj5470.wxapp.registry.codeStyle

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

/**
 * @author zxj5470
 * @date 2018/5/11
 */
class WxssCodeStyleSettings(settings: CodeStyleSettings) : CustomCodeStyleSettings("WxssCodeStyleSettings", settings) {
	var SPACE_BETWEEN_RPX = false
}