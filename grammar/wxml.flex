package com.github.zxj5470.weapp;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.*;
import com.github.zxj5470.weapp.psi.wxml.WxmlElementTypes;
import com.intellij.psi.TokenType;

%%

%{
	private static final IntStack stateStack = new IntStack();
	private static final IntStack leftBracketStack = new IntStack();
	private static int leftBraceCount = 0;
	private static boolean noInAndUnion = false;
%}

%class WxmlLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{

%eof}

WHITE_SPACE=[\ \n\t\f]

alphabets=[a-zA-Z]
digits=[0-9]
str=[a-zA-Z\-]
SINGLE_QUOTE=\'
DOUBLE_QUOTE=\"
ASSIGN=\=
%state WAITING_VALUE

%%

<WAITING_VALUE> {WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
<YYINITIAL> {alphabets} { return WxmlElementTypes.ALPHABETS; }
<YYINITIAL> {digits} { return WxmlElementTypes.DIGITS; }
<YYINITIAL> {str} { return WxmlElementTypes.STR; }

<YYINITIAL> {ASSIGN} { return WxmlElementTypes.ASSIGN; }
//<WAITING_VALUE> {SINGLE_QUOTE} { return WxmlElementTypes.SINGLE_QUOTE_SYMBOL; }
//<WAITING_VALUE> {DOUBLE_QUOTE} { return WxmlElementTypes.DOUBLE_QUOTE_SYMBOL; }
