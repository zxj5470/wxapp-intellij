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

/**
 * count how many times it appears.
 * @usage
 * ```
 * "asdf asdf asdf".countTimes("sd") shouldBe 3
 * ```
 * @receiver String
 * @param string String
 * @return Int
 */
fun String.countTimes(string: String): Int =
	this.indicesOf(string).size

/**
 * Because kotlin-stdlib hasn't got this method!!!
 * @usage
 * ```
 * "asdf asdf asdf".replaceLast("df", "_ _") shouldBe "asdf asdf as_ _"
 * ```
 * @receiver String
 * @param oldValue String
 * @param newValue String
 * @param useRegex Boolean
 * @return String
 */
fun String.replaceLast(oldValue: String, newValue: String, useRegex: Boolean = false) =
	this.indicesOf(oldValue, useRegex)
		.takeIf { it.isNotEmpty() }
		?.last()
		?.let {
			this.substring(0, it) +
				this.substring(it).let {
					if (useRegex)
						it.replace(oldValue.toRegex(), newValue)
					else
						it.replace(oldValue, newValue)
				}
		}
		?: this