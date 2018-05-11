package com.github.zxj5470.wxapp;

import com.intellij.lang.Language;
import com.intellij.lang.css.CSSLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * WARNINGS:
 * - This file MUST NOT be converted to Kotlin!
 * - DO NOT replace <code>String NAME = "Julia"</code> with
 * <code>String NAME = JuliaBundle.message("julia.name")</code>
 * but static import JULIA_LANGUAGE_NAME.
 * <p>
 * ERRORS:
 * - Tests will be failed.
 * - LanguageType `language="Julia"` in plugin.xml will become red.
 *
 * @author zxj5470
 */
public final class WxssLanguage extends Language {
	public static final @NotNull
	WxssLanguage INSTANCE = new WxssLanguage();

	private WxssLanguage() {
		super(CSSLanguage.INSTANCE,"wxss", "text/" + "wxss");
	}

	@Override
	@Contract(pure = true)
	public boolean isCaseSensitive() {
		return false;
	}
}