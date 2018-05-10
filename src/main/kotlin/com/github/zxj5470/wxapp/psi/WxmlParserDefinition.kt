package com.github.zxj5470.wxapp.psi

import com.github.zxj5470.wxapp.WxmlLanguage
import com.github.zxj5470.wxapp.psi.wxml.WxmlTypes
import com.github.zxj5470.wxapp.registry.WxmlFile
import com.intellij.lang.*
import com.intellij.lexer.Lexer
import com.intellij.lexer.XmlLexer
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.parsing.xml.XmlParser
import com.intellij.psi.tree.*
import com.intellij.psi.xml.XmlTokenType

class WxmlParserDefinition : ParserDefinition {
	override fun createLexer(project: Project): Lexer {
		return XmlLexer()
	}

	override fun getWhitespaceTokens() = WHITE_SPACES

	override fun getCommentTokens() = COMMENTS

	override fun getStringLiteralElements() = TokenSet.EMPTY

	override fun createParser(project: Project) = XmlParser()

	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxmlFile(viewProvider)

	override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
		return ParserDefinition.SpaceRequirements.MAY
	}

	override fun createElement(node: ASTNode): PsiElement {
		return WxmlTypes.Factory.createElement(node)
	}

	companion object {
		val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
		val COMMENTS = TokenSet.create(XmlTokenType.XML_COMMENT_CHARACTERS)
		val FILE = IFileElementType(WxmlLanguage.INSTANCE)
	}
}

