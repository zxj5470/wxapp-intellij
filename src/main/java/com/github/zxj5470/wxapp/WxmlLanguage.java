package com.github.zxj5470.wxapp;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.github.zxj5470.wxapp.constants.ConstsKt.WXML_EXT;

/**
 * @author zxj5470
 */
public final class WxmlLanguage extends XMLLanguage {
	public static final @NotNull
	WxmlLanguage INSTANCE = new WxmlLanguage();

	private WxmlLanguage() {
		super(HTMLLanguage.INSTANCE, "wxml", "text/" + WXML_EXT);
	}

	@Override
	@Contract(pure = true)
	public boolean isCaseSensitive() {
		return false;
	}
}