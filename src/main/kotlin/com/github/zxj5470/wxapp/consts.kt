package com.github.zxj5470.wxapp

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile

const val WXML_EXT="wxml"
const val WXSS_EXT="wxss"

val APP_JS_KEY = Key.create<PsiFile?>("wxapp-app.js")
val APP_WXSS_KEY = Key.create<PsiFile?>("wxapp-app.wxss")
val APP_JSON_KEY = Key.create<PsiFile?>("wxapp-app.json")
