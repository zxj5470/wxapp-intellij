package com.github.zxj5470.wxapp

import com.intellij.lexer.FlexAdapter
import java.io.Reader

class WxmlLexerAdapter : FlexAdapter(WxmlLexer(null as Reader?))