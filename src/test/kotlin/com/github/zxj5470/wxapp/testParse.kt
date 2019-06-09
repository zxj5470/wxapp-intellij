package com.github.zxj5470.wxapp

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