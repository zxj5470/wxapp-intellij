package com.github.zxj5470.weapp

import com.github.zxj5470.weapp.constants.WXML_EXT
import com.github.zxj5470.weapp.psi.WxmlParserDefinition
import com.intellij.testFramework.ParsingTestCase

/**
 * @author: zxj5470
 * @date: 2018/4/16
 */
class WxmlParsingTest : ParsingTestCase("", WXML_EXT, WxmlParserDefinition()) {
	override fun getTestDataPath() = "testData"
	override fun skipSpaces() = true
	fun testHello() {
		println(name)
		doTest(true)
	}
}