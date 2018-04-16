package com.github.zxj5470.wxapp;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.*;
import com.github.zxj5470.wxapp.psi.wxml.WxmlTypes;
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

WHITE_SPACE=[\ \r\t\f]+

chars=[a-zA-Z]+
digits=[0-9]+
//SINGLE_QUOTE=\'
//DOUBLE_QUOTE=\"
ASSIGN=\=
LT=<
LT_BEGIN=\<\/
GT=>
//GT_END=\/>
COMMENT_BEGIN=<\!--
COMMENT_END=-->
COLON=:
MINUS=\-
S_QUOTE=\'
D_QUOTE=\"
%state WAITING_VALUE

%%

<YYINITIAL> {WHITE_SPACE} { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
<YYINITIAL> \n+ { return WxmlTypes.EOL; }
<YYINITIAL> {chars} { return WxmlTypes.CHARS; }
<YYINITIAL> {digits} { return WxmlTypes.DIGITS; }

<YYINITIAL> {ASSIGN} { return WxmlTypes.ASSIGN; }
<YYINITIAL> {LT} { return WxmlTypes.LT; }
<YYINITIAL> {GT} { return WxmlTypes.GT; }
<YYINITIAL> {COMMENT_BEGIN} { return WxmlTypes.COMMENT_BEGIN; }
<YYINITIAL> {LT_BEGIN} { return WxmlTypes.LT_BEGIN; }
//<YYINITIAL> {GT_END} { return WxmlTypes.GT_END; }
<YYINITIAL> {COMMENT_END} { return WxmlTypes.COMMENT_END; }

<YYINITIAL> {COLON} { return WxmlTypes.COLON; }
<YYINITIAL> {MINUS} { return WxmlTypes.MINUS; }

<YYINITIAL> {S_QUOTE} { return WxmlTypes.S_QUOTE; }
<YYINITIAL> {D_QUOTE} { return WxmlTypes.D_QUOTE; }

//<WAITING_VALUE> {SINGLE_QUOTE} { return WxmlTypes.SINGLE_QUOTE_SYMBOL; }
//<WAITING_VALUE> {DOUBLE_QUOTE} { return WxmlTypes.DOUBLE_QUOTE_SYMBOL; }
