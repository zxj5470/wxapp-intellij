package com.github.zxj5470.wxapp;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class WxappIcons {
    @NotNull
    public final static Icon wxssIcon = getIcon("/icons/wxss.svg");
    @NotNull
    public final static Icon wxmlIcon = getIcon("/icons/wxml.svg");
    @NotNull
    public final static Icon weChatIcon = getIcon("/icons/wx-keyword@16x16.svg");
    @NotNull
    public final static Icon wxmlTagIcon = getIcon("/icons/wxml-tag@16x16.svg");

    private static Icon getIcon(String path) {
        return WxUtil.getIcon(path);
    }
}
