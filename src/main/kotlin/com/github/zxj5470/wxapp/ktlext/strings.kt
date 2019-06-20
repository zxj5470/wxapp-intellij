package com.github.zxj5470.wxapp.ktlext

/**
 * @author: zxj5470
 * @date: 2018/3/27
 */
/**
 * find all indices for matching `string` in sourceString
 * @receiver String
 * @param string String
 * @param useRegex Boolean whether to use regexExpression
 * @return List<Int> Indices, understand?!
 */
fun String.indicesOf(string: String, useRegex: Boolean = false): List<Int> =
	if (string.isNotEmpty()) {
		if (!useRegex) {
			this.indicesOf(string.first()).filter {
				it == this.indexOf(string, it)
			}
		} else {
			string.toRegex().findAll(this).map { it.range.first }.toList()
		}
	} else {
		emptyList()
	}

/**
 *
 * @receiver String
 * @param char Char
 * @return List<Int>
 */
fun String.indicesOf(char: Char): List<Int> =
	this.mapIndexedNotNull { index, c ->
		index.takeIf { c == char }
	}