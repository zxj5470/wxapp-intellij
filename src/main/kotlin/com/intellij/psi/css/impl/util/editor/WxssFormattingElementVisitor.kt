package com.intellij.psi.css.impl.util.editor

/**
 * @author zxj5470
 * @date 2018/5/12
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import com.intellij.lang.css.CSSLanguage
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.css.*
import com.intellij.psi.css.codeStyle.CssCodeStyleSettings
import com.intellij.psi.css.impl.CssElementTypes
import com.intellij.psi.css.impl.CssRulesetWrappingElement
import com.intellij.psi.css.impl.CssTokenImpl
import com.intellij.psi.formatter.FormatterUtil
import com.intellij.psi.templateLanguages.OuterLanguageElement
import com.intellij.psi.tree.IElementType

class WxssFormattingElementVisitor(protected val mySubBlocks: MutableList<Block>, private val mySettings: CssCodeStyleSettings, private val myMaxPropertyLength: Int, private val myExtension: WxssFormattingModelBuilder.CssFormattingExtension, private val alignment: Alignment?, private val myChildAlignment: Alignment?, private val myType: IElementType, private val shouldIndentContent: Boolean) : CssElementVisitor() {
	private var mySeenRuleset: Boolean = false

	override fun visitCssString(_string: CssString) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(_string.node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
	}

	override fun visitNamespace(namespace: CssNamespace) {
		this.mySubBlocks.add(this.myExtension.createPropertyBlock(namespace.node, this.mySettings, this.myMaxPropertyLength, this.myExtension, -1, null as Alignment?, this.alignment))
	}

	override fun visitCssImport(_import: CssImport) {
		this.mySubBlocks.add(this.myExtension.createPropertyBlock(_import.node, this.mySettings, this.myMaxPropertyLength, this.myExtension, -1, null as Alignment?, this.alignment))
	}

	override fun visitCustomMixin(customMixin: CssCustomMixin) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(this.myExtension.createRulesetBlock(customMixin.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension, this.alignment))
	}

	override fun visitCssRuleset(ruleset: CssRuleset) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(this.myExtension.createRulesetBlock(ruleset.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension, if (this.mySeenRuleset) Alignment.createAlignment() else null))
		this.mySeenRuleset = true
	}

	override fun visitCssSelector(selector: CssSelector) {
		var child: PsiElement? = selector.firstChild

		var containsOuterLangElements: Boolean
		containsOuterLangElements = false
		while (child != null) {
			if (child is OuterLanguageElement) {
				containsOuterLangElements = true
				break
			}
			child = child.nextSibling
		}

		if (containsOuterLangElements) {
			selector.acceptChildren(this)
		} else if (selector.node.textRange.length != 0) {
			this.mySubBlocks.add(this.myExtension.createSelectorBlock(selector.node, this.mySettings, this.myMaxPropertyLength, this.myExtension))
		}

	}

	override fun visitCssSimpleSelector(selector: CssSimpleSelector?) {
		if (selector != null && selector !is CssKeyframesSelector && !FormatterUtil.containsWhiteSpacesOnly(selector.node)) {
			this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(selector.node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
		}

	}

	override fun visitCssTermList(_termList: CssTermList) {
		var errorsOnly = true

		var child: PsiElement? = _termList.firstChild
		while (child != null) {
			errorsOnly = child is PsiErrorElement
			if (errorsOnly) {
				break
			}
			child = child.nextSibling
		}

		if (errorsOnly) {
			_termList.acceptChildren(this)
		} else {
			val indent = if (this.shouldIndentContent) Indent.getContinuationIndent() else Indent.getNoneIndent()
			this.mySubBlocks.add(this.myExtension.createTermListBlock(_termList.node, this.mySettings, indent, this.myMaxPropertyLength, -1, if (this.mySettings.VALUE_ALIGNMENT == 2) this.myChildAlignment else null, true))
		}

	}

	override fun visitCssDeclaration(declaration: CssDeclaration) {
		this.mySubBlocks.add(this.myExtension.createPropertyBlock(declaration.node, this.mySettings, this.myMaxPropertyLength, this.myExtension, declaration.propertyName.length, null as Alignment?, this.alignment))
	}

	override fun visitMediaFeature(mediaFeature: CssMediaFeature) {
		this.mySubBlocks.add(this.myExtension.createPropertyBlock(mediaFeature.node, this.mySettings, this.myMaxPropertyLength, this.myExtension, mediaFeature.name.length, null as Alignment?, null as Alignment?))
	}

	private fun visitMediaGroup(mediaGroup: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(this.myExtension.createMediaBlock(mediaGroup.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitPageDeclaration(page: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssPageBlock(page.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitSupportsDeclaration(supports: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(this.myExtension.createSupportsBlock(supports.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitSupportsCondition(condition: PsiElement) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(condition.node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitCssOperationBlock(operation: PsiElement) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssOperationBlock(operation.node, this.mySettings, this.myMaxPropertyLength, this.myExtension))
	}

	override fun visitNamespaceList(namespaceList: CssNamespaceList) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.NamespaceListBlock(namespaceList.node, this.mySettings, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitCssImportList(importList: PsiElement) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.ImportListBlock(importList.node, this.mySettings, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitBadAtRule(badAtRule: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssBadAtRuleBlock(badAtRule.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension))
		this.mySeenRuleset = true
	}

	private fun visitCssExpression(expression: PsiElement) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(expression.node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitCssFontFace(fontFace: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssFontFaceBlock(fontFace.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitCssViewPort(viewPort: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssViewPortBlock(viewPort.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension))
	}

	private fun visitAtRuleWithNestedRulesets(atRule: PsiElement) {
		val indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
		this.mySubBlocks.add(this.myExtension.createRulesetBlock(atRule.node, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension, Alignment.createAlignment()))
		this.mySeenRuleset = true
	}

	private fun visitCharset(charset: PsiElement) {
		this.mySubBlocks.add(this.myExtension.createPropertyBlock(charset.node, this.mySettings, this.myMaxPropertyLength, this.myExtension, -1, null as Alignment?, this.alignment))
	}

	override fun visitComment(comment: PsiComment) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(comment.node, this.mySettings, this.myExtension.getCommentIndent(comment.tokenType, this.myType), this.myMaxPropertyLength, this.myExtension, null as Alignment?))
	}

	override fun visitLineNames(lineNames: CssLineNames) {
		this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(lineNames.node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
	}

	override fun visitElement(element: PsiElement?) {
		val indent: Indent
		if (element is CssTokenImpl) {
			val token = element as CssTokenImpl?
			if (!this.myExtension.addLeaf(token!!)) {
				indent = this.myExtension.getTokenIndent(token, this.mySettings)
				val alignment = if (this.mySettings.VALUE_ALIGNMENT == 1 && token.elementType === CssElementTypes.CSS_COLON) this.myChildAlignment else null
				this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(token, this.mySettings, indent, this.myMaxPropertyLength, this.myExtension, alignment))
			}
		} else {
			val node: ASTNode?
			if (element is CssElement) {
				node = element.node
				if (node != null && CssElementTypes.CSS_MEDIA === node.elementType) {
					this.visitMediaGroup(element)
				} else if (node != null && CssElementTypes.CSS_PAGE === node.elementType) {
					this.visitPageDeclaration(element)
				} else if (node != null && CssElementTypes.CSS_SUPPORTS === node.elementType) {
					this.visitSupportsDeclaration(element)
				} else if (node is CssSupportsCondition) {
					this.visitSupportsCondition(element)
				} else if (node != null && CssElementTypes.CSS_PAGE_MARGIN_RULE === node.elementType) {
					this.visitBadAtRule(element)
				} else if (node != null && CssElementTypes.CSS_IMPORT_LIST === node.elementType) {
					this.visitCssImportList(element)
				} else if (element is CssTermList) {
					this.visitCssTermList(element)
				} else if (node != null && CssElementTypes.CSS_EXPRESSION === node.elementType) {
					this.visitCssExpression(element)
				} else if (node != null && CssElementTypes.CSS_FONTFACE === node.elementType) {
					this.visitCssFontFace(element)
				} else if (node != null && CssElementTypes.CSS_VIEWPORT === node.elementType) {
					this.visitCssViewPort(element)
				} else if (node != null && CssElementTypes.CSS_ATTRIBUTE === node.elementType) {
					this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
				} else if (node != null && (CssElementTypes.CSS_BINARY_OPERATION === node.elementType || CssElementTypes.CSS_UNARY_OPERATION === node.elementType)) {
					this.visitCssOperationBlock(element)
				} else if (node != null && CssElementTypes.CSS_KEYFRAMES_RULE === node.elementType || node != null && CssElementTypes.CSS_REGION_RULE === node.elementType || node != null && CssElementTypes.CSS_SCOPE_RULE === node.elementType || node != null && CssElementTypes.CSS_DOCUMENT_RULE === node.elementType) {
					this.visitAtRuleWithNestedRulesets(element)
				} else if (node != null && CssElementTypes.CSS_CHARSET === node.elementType) {
					this.visitCharset(element)
				} else if (node != null && CssElementTypes.CSS_APPLY === node.elementType) {
					this.mySubBlocks.add(this.myExtension.createPropertyBlock(node, this.mySettings, this.myMaxPropertyLength, this.myExtension, -1, null as Alignment?, this.alignment))
				} else if (node != null && (CssElementTypes.CSS_BAD_AT_RULE === node.elementType || element is CssAtRule)) {
					this.visitBadAtRule(element)
				} else if (!FormatterUtil.containsWhiteSpacesOnly(node) && !this.myExtension.addSubBlocks(element, this.mySettings, this.myMaxPropertyLength, this.mySubBlocks)) {
					if (element.language !== CSSLanguage.INSTANCE && element !is CssStylesheet && element !is CssBlock && element !is CssRulesetWrappingElement) {
						if (!this.myExtension.addSubBlocksOfExtendedLanguage(element, this.mySettings, this.myMaxPropertyLength, this.mySubBlocks)) {
							indent = if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()
							this.mySubBlocks.add(CssFormattingModelBuilder.LeafBlock(node, indent))
						}
					} else {
						element.acceptChildren(this)
					}
				}
			} else (element as? PsiErrorElement)?.acceptChildren(this)
				?: if (element is OuterLanguageElement || element!!.node != null && element.node.elementType === CssElementTypes.CSS_BAD_CHARACTER) {
					this.mySubBlocks.add(WxssFormattingModelBuilder.CssSimpleBlock(element.node, this.mySettings, Indent.getNoneIndent(), this.myMaxPropertyLength, this.myExtension))
				} else {
					node = element.node
					if (!FormatterUtil.containsWhiteSpacesOnly(node) && !this.myExtension.addSubBlocksOfExtendedLanguage(element, this.mySettings, this.myMaxPropertyLength, this.mySubBlocks)) {
						this.mySubBlocks.add(CssFormattingModelBuilder.LeafBlock(node, if (this.shouldIndentContent) Indent.getNormalIndent() else Indent.getNoneIndent()))
					}
				}
		}

	}
}
