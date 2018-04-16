package com.github.zxj5470.weapp.psi

import com.github.zxj5470.weapp.WxmlLanguage
import com.github.zxj5470.weapp.WxmlLexerAdapter
import com.github.zxj5470.weapp.psi.wxml.WxmlElementTypes
import com.github.zxj5470.weapp.psi.wxml.WxmlParser
import com.github.zxj5470.weapp.registry.WxmlFile
import com.intellij.lang.*
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.tree.*

class WxmlParserDefinition : ParserDefinition {
	override fun createLexer(project: Project): Lexer {
		return WxmlLexerAdapter()
	}

	override fun getWhitespaceTokens() = WHITE_SPACES

	override fun getCommentTokens() = COMMENTS

	override fun getStringLiteralElements() = TokenSet.EMPTY

	override fun createParser(project: Project) = WxmlParser()

	override fun getFileNodeType(): IFileElementType = FILE

	override fun createFile(viewProvider: FileViewProvider): PsiFile = WxmlFile(viewProvider)

	override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
		return ParserDefinition.SpaceRequirements.MAY
	}

	override fun createElement(node: ASTNode): PsiElement {
		return WxmlElementTypes.Factory.createElement(node)
	}

	companion object {
		val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
		val COMMENTS = TokenSet.create(WxmlElementTypes.COMMENT)
		val FILE = IFileElementType(WxmlLanguage.INSTANCE)
	}
}

