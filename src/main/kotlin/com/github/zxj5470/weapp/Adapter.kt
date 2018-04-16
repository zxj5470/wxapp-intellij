package com.github.zxj5470.weapp

import com.intellij.lexer.FlexAdapter
import java.io.Reader

class WxmlLexerAdapter : FlexAdapter(WxmlLexer(null as Reader?))