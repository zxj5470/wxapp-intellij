package com.github.zxj5470.weapp;

import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.github.zxj5470.weapp.constants.ConstsKt.WXML_EXT;

/**
 * @author zxj5470
 */
public final class WxmlLanguage extends Language {
	public static final @NotNull
	WxmlLanguage INSTANCE = new WxmlLanguage();

	private WxmlLanguage() {
		super(XMLLanguage.INSTANCE, "wxml", "text/" + WXML_EXT);
	}

	@Override
	@Contract(pure = true)
	public boolean isCaseSensitive() {
		return false;
	}
}