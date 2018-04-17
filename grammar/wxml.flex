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

      /** 冰冰化 */
      private void icefy(int state) {
        stateStack.push(yystate());
        leftBracketStack.push(leftBraceCount);
        leftBraceCount = 0;
        yybegin(state);
      }

      /** 去冰冰化 */
      private void deicefy() {
        leftBraceCount = leftBracketStack.pop();
        yybegin(stateStack.pop());
      }

      /** 重新冰冰化 */
      private void reicefy(int state) {
        deicefy();
        icefy(state);
      }

      private static void init() {
        leftBraceCount = 0;
        noInAndUnion = false;
        stateStack.clear();
        leftBracketStack.clear();
      }
%}

%class WxmlLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
	init();
%eof}

%state LONG_TEMPLATE

WHITE_SPACE=[\ \r\t\f]+

//WX_BEGIN=wx:
SIMPLE_SYMBOL={VALID_CHAR}({VALID_CHAR}|[\d\!])*
//NO_WX_SYMBOL={WX_BEGIN}

VALID_CHAR=[a-zA-Z_\U000100-\U10ffff]
VALID_INTEROP={VALID_CHAR}({VALID_CHAR}|[\d]|{OPS})*
OPS=[\+\-\*\/\^\~\!\&\|\;\{\}\(\)\[\]]
//NON_W=[a-vx-zA-Z_\U000100-\U10ffff]
//NON_X=[a-wyzA-Z_\U000100-\U10ffff]
assign=\=
lt=<
LT_END=\<\/
gt=>
//GT_END=\/>
commentBegin=<\!--
COMMENT_END=-->
colon=:
minus=\-
S_QUOTE=\'
D_QUOTE=\"
doubleLBR=\{\{
doubleRBR=\}\}

%%

<YYINITIAL> {WHITE_SPACE} { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
<YYINITIAL> \n+ { return WxmlTypes.EOL; }
<YYINITIAL> {SIMPLE_SYMBOL} { return WxmlTypes.SIMPLE_SYMBOL; }
<YYINITIAL> {VALID_INTEROP} { return WxmlTypes.VALID_INTEROP; }
<YYINITIAL> {minus} { return WxmlTypes.MINUS; }
<YYINITIAL> {colon} { return WxmlTypes.COLON; }

<YYINITIAL> {doubleLBR} { return WxmlTypes.DBL_LBR; }
<YYINITIAL> {doubleRBR} { return WxmlTypes.DBL_RBR; }

<YYINITIAL> {assign} { return WxmlTypes.ASSIGN; }
<YYINITIAL> {lt} { return WxmlTypes.LT; }
<YYINITIAL> {gt} { return WxmlTypes.GT; }
<YYINITIAL> {commentBegin} { return WxmlTypes.COMMENT_BEGIN; }
<YYINITIAL> {LT_END} { return WxmlTypes.LT_END; }
//<YYINITIAL> {GT_END} { return WxmlTypes.GT_END; }
<YYINITIAL> {COMMENT_END} { return WxmlTypes.COMMENT_END; }

<YYINITIAL> {S_QUOTE} { return WxmlTypes.S_QUOTE; }
<YYINITIAL> {D_QUOTE} { return WxmlTypes.D_QUOTE; }

//<WAITING_VALUE> {SINGLE_QUOTE} { return WxmlTypes.SINGLE_QUOTE_SYMBOL; }
//<WAITING_VALUE> {DOUBLE_QUOTE} { return WxmlTypes.DOUBLE_QUOTE_SYMBOL; }
