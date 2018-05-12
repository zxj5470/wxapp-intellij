//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intellij.psi.css.impl.util.editor

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.intellij.psi.css.*
import com.intellij.psi.css.codeStyle.CssCodeStyleSettings
import com.intellij.psi.css.impl.CssElementTypes
import com.intellij.psi.css.impl.CssTermImpl
import com.intellij.psi.css.impl.CssTokenImpl
import com.intellij.psi.css.impl.parsing.CssMathParser
import com.intellij.psi.css.impl.util.CssStylesheetElementTypeBase
import com.intellij.psi.css.impl.util.completion.TimeUserLookup
import com.intellij.psi.formatter.DocumentBasedFormattingModel
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.templateLanguages.OuterLanguageElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTokenType
import java.util.*

open class WxssFormattingModelBuilder : FormattingModelBuilder {
	override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
		val customSettings = CodeStyleSettingsManager.getSettings(element.project).getCustomSettings(CssCodeStyleSettings::class.java) as CssCodeStyleSettings
		val psiFile = element.containingFile
		val rootElement = if (psiFile is XmlFile) element else psiFile
		val maxPropertyName = intArrayOf(0)
		if (customSettings.VALUE_ALIGNMENT == 2 || customSettings.VALUE_ALIGNMENT == 1) {
			(rootElement as PsiElement).acceptChildren(object : CssElementVisitor() {
				override fun visitCssDeclaration(declaration: CssDeclaration) {
					super.visitCssDeclaration(declaration)
					maxPropertyName[0] = Math.max(maxPropertyName[0], declaration.propertyName.length)
				}

				override fun visitElement(element: PsiElement?) {
					element!!.acceptChildren(this)
				}
			})
		}

		val extension = this.createExtension()
		val root = extension.createRootBlock((rootElement as PsiElement).node, customSettings, maxPropertyName[0], extension)
		val var10000 = DocumentBasedFormattingModel(root, psiFile.project, customSettings.container, psiFile.fileType, psiFile)
		return var10000
	}

	protected fun createExtension(): WxssFormattingModelBuilder.CssFormattingExtension {
		return WxssFormattingModelBuilder.CssFormattingExtension()
	}

	override fun getRangeAffectingIndent(file: PsiFile, offset: Int, elementAtOffset: ASTNode): TextRange? {
		return null
	}

	class CssFormattingExtension {

		fun isComment(elementType: IElementType): Boolean {
			return elementType === CssElementTypes.CSS_COMMENT
		}

		fun addLeaf(token: CssTokenImpl): Boolean {
			return false
		}

		fun isLineComment(elementType: IElementType): Boolean {
			return false
		}

		fun addSubBlocks(element: PsiElement, settings: CssCodeStyleSettings, maxPropertyLength: Int, result: List<Block>): Boolean {
			return false
		}

		fun getCommentIndent(commentType: IElementType, parentType: IElementType): Indent {
			if (parentType !is IFileElementType && parentType !is CssStylesheetElementTypeBase) {
				if (this.isLineComment(commentType) && parentType === CssElementTypes.CSS_DECLARATION) {
					return Indent.getContinuationIndent()
				}

				if (parentType !== CssElementTypes.CSS_IMPORT_LIST && parentType !== CssElementTypes.CSS_RULESET_LIST) {
					return Indent.getNormalIndent()
				}
			}

			return Indent.getNoneIndent()
		}

		fun getTokenIndent(token: CssTokenImpl, settings: CssCodeStyleSettings): Indent {
			return if (token.elementType === CssElementTypes.CSS_RBRACE && settings.ALIGN_CLOSING_BRACE_WITH_PROPERTIES) Indent.getNormalIndent() else Indent.getNoneIndent()
		}

		fun addSubBlocksOfExtendedLanguage(element: PsiElement, settings: CssCodeStyleSettings, maxPropertyLength: Int, result: List<Block>): Boolean {
			return false
		}

		fun createRootBlock(_node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension): WxssFormattingModelBuilder.CssRootBlock {
			return WxssFormattingModelBuilder.CssRootBlock(_node, settings, maxPropertyLength, extension)
		}

		fun createRulesetBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, alignment: Alignment?): WxssFormattingModelBuilder.CssRulesetBlock {
			return WxssFormattingModelBuilder.CssRulesetBlock(_node, settings, indent, maxPropertyLength, extension, alignment)
		}

		fun createPropertyBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, nameLength: Int, alignment: Alignment?, childAlignment: Alignment?): WxssFormattingModelBuilder.CssPropertyBlock {
			return WxssFormattingModelBuilder.CssPropertyBlock(_node, settings, indent, maxPropertyLength, extension, nameLength, alignment, childAlignment)
		}

		fun createPropertyBlock(_node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, nameLength: Int, alignment: Alignment?, childAlignment: Alignment?): WxssFormattingModelBuilder.CssPropertyBlock {
			var indent = if (PsiTreeUtil.getParentOfType(_node.psi, CssBlock::class.java) == null) Indent.getNoneIndent() else Indent.getNormalIndent()
			if (_node.treeParent.elementType === CssElementTypes.CSS_MEDIA && _node.elementType === CssElementTypes.CSS_IMPORT) {
				indent = Indent.getNormalIndent()
			}

			return this.createPropertyBlock(_node, settings, indent, maxPropertyLength, extension, nameLength, alignment, childAlignment)
		}

		fun createSelectorBlock(node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension): WxssFormattingModelBuilder.CssSelectorBlock {
			return WxssFormattingModelBuilder.CssSelectorBlock(node, settings, Indent.getNoneIndent(), maxPropertyLength, extension)
		}

		fun createMediaBlock(node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension): WxssFormattingModelBuilder.CssFormatterBlock {
			return WxssFormattingModelBuilder.CssMediaBlock(node, settings, indent, maxPropertyLength, extension)
		}

		fun createSupportsBlock(node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension): WxssFormattingModelBuilder.CssFormatterBlock {
			return WxssFormattingModelBuilder.CssSupportsBlock(node, settings, indent, maxPropertyLength, extension)
		}

		fun createTermListBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, nameLength: Int, alignment: Alignment?, shouldIndentContent: Boolean): WxssFormattingModelBuilder.CssTermListBlock {
			return WxssFormattingModelBuilder.CssTermListBlock(_node, settings, indent, maxPropertyLength, this, nameLength, alignment, shouldIndentContent)
		}
	}

	protected class LeafBlock(private val myNode: ASTNode, private val myIndent: Indent) : ASTBlock {

		override fun getNode(): ASTNode {
			return this.myNode
		}

		override fun getTextRange(): TextRange {
			val var10000 = this.myNode.textRange
			if (var10000 == null) {

			}

			return var10000
		}

		override fun getSubBlocks(): List<Block> {
			val var10000 = EMPTY_SUB_BLOCKS
			return var10000
		}

		override fun getWrap(): Wrap? {
			return null
		}

		override fun getIndent(): Indent? {
			return this.myIndent
		}

		override fun getAlignment(): Alignment? {
			return null
		}

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child2 == null) {

			}

			return null
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(this.indent, null as Alignment?)
			return var10000
		}

		override fun isIncomplete(): Boolean {
			return false
		}

		override fun isLeaf(): Boolean {
			return true
		}

		companion object {
			private val EMPTY_SUB_BLOCKS = ArrayList<Block>()
		}
	}

	abstract class CssFormatterBlock @JvmOverloads constructor(protected val myNode: ASTNode?, val settings: CssCodeStyleSettings, internal val myIndent: Indent?, protected val myMaxPropertyLength: Int, protected val myExtension: WxssFormattingModelBuilder.CssFormattingExtension, private val myAlignment: Alignment? = null, protected val myChildAlignment: Alignment? = null) : ASTBlock {
		var mySubBlocks: List<Block>? = null
		val myType: IElementType

		protected val spacingBeforeLBrace: Spacing
			get() {
				val settings = this.settings
				val linefeeds = if (settings.BRACE_PLACEMENT == 1) 1 else 0
				val spaces = if (settings.SPACE_BEFORE_OPENING_BRACE) 1 else 0
				return Spacing.createSpacing(spaces, spaces, linefeeds, false, 0)
			}

		init {
			this.myType = this.myNode!!.elementType
		}

		override fun toString(): String {
			return if (this.myNode != null) this.myNode.text else super.toString()
		}

		open fun shouldIndentContent(): Boolean {
			return false
		}

		override fun getTextRange(): TextRange {
			val var10000 = this.myNode!!.textRange
			if (var10000 == null) {

			}

			return var10000
		}

		override fun getNode(): ASTNode? {
			return this.myNode
		}

		override fun getSubBlocks(): List<Block> {
			if (this.mySubBlocks == null) {
				this.mySubBlocks = ArrayList(0)
				val alignment = if (this.settings.VALUE_ALIGNMENT != 2 && this.settings.VALUE_ALIGNMENT != 1) null else Alignment.createAlignment(true)
				this.myNode!!.psi.acceptChildren(WxssFormattingElementVisitor(this.mySubBlocks as ArrayList<Block>, this.settings, this.myMaxPropertyLength, this.myExtension, alignment, this.myChildAlignment, this.myType, this.shouldIndentContent()))
			}

			val var10000 = this.mySubBlocks
			return var10000!!
		}

		override fun getWrap(): Wrap? {
			return null
		}

		override fun getIndent(): Indent? {
			return this.myIndent
		}

		override fun getAlignment(): Alignment? {
			return this.myAlignment
		}

		override fun isIncomplete(): Boolean {
			return false
		}

		override fun isLeaf(): Boolean {
			return this.myNode is LeafElement
		}
	}

	class CssRootBlock(_node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, null as Indent?, maxPropertyLength, extension) {

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child2 == null) {

			}

			if (child1 == null) {
				return null
			} else {
				if (child1 is ASTBlock && child2 is ASTBlock) {
					val node1 = child1.node
					val node2 = child2.node
					val type1 = node1.elementType
					val type2 = node2.elementType
					val parentNode = node1.treeParent
					if (type1 === CssElementTypes.CSS_BAD_AT_RULE) {
						return Spacing.createSpacing(1, 1, 0, true, 1)
					}

					var parentType: IElementType? = null
					if (parentNode != null) {
						parentType = parentNode.elementType
					}

					if (parentType === CssElementTypes.CSS_NAMESPACE) {
						if (type2 === CssElementTypes.CSS_NAMESPACE_SYM) {
							return Spacing.createSpacing(0, 0, 1, true, 1)
						}

						if (type1 === CssElementTypes.CSS_IDENT || type2 === CssElementTypes.CSS_IDENT || type2 === CssElementTypes.CSS_STRING) {
							return Spacing.createSpacing(1, 1, 0, true, 0)
						}

						if (type2 === CssElementTypes.CSS_SEMICOLON) {
							return Spacing.createSpacing(0, 0, 0, false, 0)
						}
					}

					if (this.myExtension.isLineComment(type1)) {
						return Spacing.createSpacing(0, 0, 1, true, 1)
					}

					if (child1 !is WxssFormattingModelBuilder.CssRulesetBlock && this.myExtension.isComment(type2) && node2.text.length < 10) {
						return Spacing.createSpacing(1, 1, 0, true, 1)
					}
				}

				if (child1 !is WxssFormattingModelBuilder.CssPropertyBlock && child1 !is WxssFormattingModelBuilder.CssSimpleBlock) {
					val minLineFeeds = this.settings.BLANK_LINES_BETWEEN_BLOCKS + 1
					return Spacing.createSpacing(2, 2, minLineFeeds, true, minLineFeeds - 1)
				} else {
					return Spacing.createSpacing(2, 2, 1, true, 1)
				}
			}
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val subBlocks = this.subBlocks
			val var10000: ChildAttributes?
			if (newChildIndex != 0 && subBlocks.size >= newChildIndex) {
				val prevBlock = subBlocks[newChildIndex - 1]
				if (prevBlock is WxssFormattingModelBuilder.CssRulesetBlock) {
					var10000 = ChildAttributes(Indent.getNoneIndent(), prevBlock.getAlignment())
					if (var10000 == null) {

					}

					return var10000
				}
			}

			var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)
			if (var10000 == null) {

			}

			return var10000
		}
	}

	internal class NamespaceListBlock(_node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.ImportListBlock(_node, settings, maxPropertyLength, extension)

	internal open class ImportListBlock(_node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, Indent.getNoneIndent(), maxPropertyLength, extension) {

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child2 == null) {

			}

			return if (child1 is WxssFormattingModelBuilder.CssFormatterBlock && child2 is WxssFormattingModelBuilder.CssFormatterBlock) {
				if (child2.myType === CssElementTypes.CSS_SEMICOLON) Spacing.createSpacing(0, 0, 0, false, 0) else Spacing.createSpacing(1, 1, 1, false, 0)
			} else {
				null
			}
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)


			return var10000
		}
	}

	class CssTermListBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, nameLength: Int, alignment: Alignment?, private val myShouldIndentContent: Boolean) : WxssFormattingModelBuilder.CssPropertyBlock(_node, settings, indent, maxPropertyLength, extension, nameLength, alignment, null as Alignment?) {

		override fun shouldIndentContent(): Boolean {
			return this.myShouldIndentContent
		}

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child1 is WxssFormattingModelBuilder.CssFormatterBlock && child2 is WxssFormattingModelBuilder.CssFormatterBlock) {
				val formatterBlock = child1 as WxssFormattingModelBuilder.CssFormatterBlock?
				return if (formatterBlock!!.myType !== CssElementTypes.CSS_COLON && child2.myType !== CssElementTypes.CSS_COLON && formatterBlock!!.myType !== CssElementTypes.CSS_EQ && child2.myType !== CssElementTypes.CSS_EQ && formatterBlock!!.myType !== CssElementTypes.CSS_PERIOD && child2.myType !== CssElementTypes.CSS_PERIOD) {
					if (formatterBlock!!.myType !== CssElementTypes.CSS_SLASH && child2.myType !== CssElementTypes.CSS_SLASH && formatterBlock!!.node !is OuterLanguageElement && child2.node !is OuterLanguageElement) super.getSpacing(child1, child2) else Spacing.getReadOnlySpacing()
				} else {
					Spacing.createSpacing(0, 0, 0, false, 0)
				}
			} else {
				return null
			}
		}
	}

	open class CssMediaBlock internal constructor(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssRulesetBlock(_node, settings, indent, maxPropertyLength, extension, null as Alignment?) {

		protected open val atKeywordElementType: IElementType
			get() = CssElementTypes.CSS_MEDIA_SYM

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child1 is WxssFormattingModelBuilder.CssFormatterBlock && child2 is WxssFormattingModelBuilder.CssFormatterBlock) {
				val formatterBlock = child1 as WxssFormattingModelBuilder.CssFormatterBlock?
				if (child2.myType === CssElementTypes.CSS_LBRACE) {
					return this.spacingBeforeLBrace
				} else if (formatterBlock!!.myType !== this.atKeywordElementType) {
					if (formatterBlock!!.myType === CssElementTypes.CSS_COMMA && child2.myType === CssElementTypes.CSS_IDENT) {
						return Spacing.createSpacing(1, 1, 0, false, 0)
					} else if (formatterBlock!!.myType === CssElementTypes.CSS_LBRACE) {
						return Spacing.createSpacing(2, 2, 1, true, 1)
					} else if (child2.myType === CssElementTypes.CSS_RBRACE) {
						return Spacing.createSpacing(2, 2, 1, true, 1)
					} else if (child2.myType === CssElementTypes.CSS_DECLARATION && formatterBlock!!.myType === CssElementTypes.CSS_SEMICOLON) {
						return Spacing.createSpacing(2, 2, 1, true, 1)
					} else if (this.myExtension.isComment(formatterBlock!!.myType)) {
						return Spacing.createSpacing(1, 1, 1, false, 0)
					} else if (formatterBlock.myType !== CssElementTypes.CSS_RULESET && formatterBlock.myType !== CssElementTypes.CSS_PAGE) {
						return null
					} else {
						val minLineFeeds = this.settings.BLANK_LINES_BETWEEN_BLOCKS + 1
						return Spacing.createSpacing(2, 2, minLineFeeds, true, minLineFeeds - 1)
					}
				} else {
					return if (child2.myType !== CssElementTypes.CSS_DECLARATION && child2 !is WxssFormattingModelBuilder.CssRulesetBlock) Spacing.createSpacing(1, 1, 0, false, 0) else Spacing.createSpacing(2, 2, 1, true, 1)
				}
			} else {
				return null
			}
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000: ChildAttributes?
			if (newChildIndex != 0) {
				val prevBlock = this.subBlocks[newChildIndex - 1]
				if (prevBlock is WxssFormattingModelBuilder.CssRulesetBlock) {
					var10000 = ChildAttributes(Indent.getNormalIndent(), prevBlock.getAlignment())
					if (var10000 == null) {

					}

					return var10000
				}
			}

			var10000 = ChildAttributes(Indent.getNormalIndent(), null as Alignment?)
			if (var10000 == null) {

			}

			return var10000
		}
	}

	internal class CssPageBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssMediaBlock(_node, settings, indent, maxPropertyLength, extension) {

		override val atKeywordElementType: IElementType
			get() = CssElementTypes.CSS_PAGE_SYM

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child2 == null) {

			}

			if (child1 is WxssFormattingModelBuilder.CssFormatterBlock && child2 is WxssFormattingModelBuilder.CssFormatterBlock) {
				val formatterBlock = child1 as WxssFormattingModelBuilder.CssFormatterBlock?
				return if (formatterBlock!!.myType === CssElementTypes.CSS_COLON && child2.myType === CssElementTypes.CSS_IDENT) Spacing.createSpacing(0, 0, 0, false, 0) else super.getSpacing(child1, child2)
			} else {
				return null
			}
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNormalIndent(), null as Alignment?)


			return var10000
		}
	}

	internal class CssSupportsBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssMediaBlock(_node, settings, indent, maxPropertyLength, extension) {

		override val atKeywordElementType: IElementType
			get() = CssElementTypes.CSS_SUPPORTS_SYM

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val subBlocks = this.subBlocks
			val var10000: ChildAttributes?
			if (newChildIndex - 1 < subBlocks.size) {
				val prevSubBlock = subBlocks[newChildIndex - 1]
				if (prevSubBlock is WxssFormattingModelBuilder.CssFormatterBlock) {
					val elementBefore = prevSubBlock.node!!.psi
					if (elementBefore != null && elementBefore.parent is CssSupportsCondition) {
						var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)
						return var10000
					}
				}
			}

			var10000 = super.getChildAttributes(newChildIndex)


			return var10000
		}
	}

	internal class CssViewPortBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssMediaBlock(_node, settings, indent, maxPropertyLength, extension) {

		override val atKeywordElementType: IElementType
			get() = CssElementTypes.CSS_VIEWPORT_SYM

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNormalIndent(), null as Alignment?)
			return var10000
		}
	}

	internal class CssFontFaceBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssMediaBlock(_node, settings, indent, maxPropertyLength, extension) {

		override val atKeywordElementType: IElementType
			get() = CssElementTypes.CSS_FONTFACE_SYM

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNormalIndent(), null as Alignment?)
			return var10000
		}
	}

	internal class CssBadAtRuleBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssMediaBlock(_node, settings, indent, maxPropertyLength, extension) {

		override val atKeywordElementType: IElementType
			get() = CssElementTypes.CSS_ATKEYWORD

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNormalIndent(), null as Alignment?)
			if (var10000 == null) {

			}

			return var10000
		}
	}

	open class CssRulesetBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, alignment: Alignment?) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, indent, maxPropertyLength, extension, alignment, null as Alignment?) {

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child2 == null) {

			}

			if (child1 is WxssFormattingModelBuilder.CssFormatterBlock && child2 is WxssFormattingModelBuilder.CssFormatterBlock) {
				val formatterBlock = child1 as WxssFormattingModelBuilder.CssFormatterBlock?
				if (child2.myType === CssElementTypes.CSS_COLON) {
					return Spacing.createSpacing(0, 0, 0, false, 0)
				} else if (child2.myType !== CssElementTypes.CSS_RULESET && formatterBlock!!.myType !== CssElementTypes.CSS_RULESET) {
					if (formatterBlock is WxssFormattingModelBuilder.CssPropertyBlock && (child2.node is OuterLanguageElement || child2.myType === CssElementTypes.CSS_SEMICOLON)) {
						val psi = formatterBlock.node!!.psi
						val node2 = child2.node
						val settings = this.settings
						if (psi is CssDeclaration) {
							val value = psi.value
							if (value != null && value.textLength != 0) {
								return Spacing.createSpacing(0, 0, 0, false, 0)
							} else {
								val minSpaces = if (node2 is OuterLanguageElement && (node2 as OuterLanguageElement).textLength > 0) 1 else 0
								return if (settings.SPACE_AFTER_COLON) Spacing.createSpacing(minSpaces, 1, 0, false, 0) else Spacing.createSpacing(0, 0, 0, false, 0)
							}
						} else {
							return Spacing.createSpacing(0, 1, 0, false, 0)
						}
					} else return if (child2.myType !== CssElementTypes.CSS_SEMICOLON && child2.myType !== CssElementTypes.CSS_COMMA) {
						if (child1 !is WxssFormattingModelBuilder.CssRulesetBlock && this.myExtension.isComment(child2.myType)) {
							Spacing.createSpacing(1, 1, 0, true, 1)
						} else if (this.myExtension.isLineComment(formatterBlock!!.myType)) {
							Spacing.createSpacing(0, 0, 1, true, 1)
						} else if (formatterBlock.myType !== CssElementTypes.CSS_LBRACE && formatterBlock.myType !== CssElementTypes.CSS_SEMICOLON && !this.myExtension.isComment(formatterBlock.myType) && child2.myType !== CssElementTypes.CSS_RBRACE) {
							if (formatterBlock is WxssFormattingModelBuilder.CssPropertyBlock && child2 is WxssFormattingModelBuilder.CssPropertyBlock) {
								if (this.settings.KEEP_SINGLE_LINE_BLOCKS) Spacing.createSpacing(1, 1, 0, true, 0) else Spacing.createSpacing(2, 2, 1, true, 1)
							} else if (formatterBlock.myType === CssElementTypes.CSS_COMMA) {
								Spacing.createSpacing(1, 1, 0, true, 0)
							} else if (child2.myType === CssElementTypes.CSS_LBRACE) {
								this.spacingBeforeLBrace
							} else if (child2.myType !== XmlTokenType.XML_COMMENT_CHARACTERS && child2.myType !== XmlTokenType.XML_COMMENT_START) {
								if (formatterBlock.myType === CssElementTypes.CSS_LPAREN || child2.myType === CssElementTypes.CSS_LPAREN || child2.myType === CssElementTypes.CSS_RPAREN || (formatterBlock is WxssFormattingModelBuilder.CssSimpleBlock || formatterBlock is WxssFormattingModelBuilder.CssSelectorBlock) && child2.node is OuterLanguageElement) {
									Spacing.createSpacing(0, 1, 0, false, 0)
								} else {
									if ((child1 !is WxssFormattingModelBuilder.CssBadAtRuleBlock || child2 !is WxssFormattingModelBuilder.CssBadAtRuleBlock) && (child1 !is WxssFormattingModelBuilder.CssBadAtRuleBlock || child2 !is WxssFormattingModelBuilder.CssRulesetBlock) && (child1 !is WxssFormattingModelBuilder.CssRulesetBlock || child2 !is WxssFormattingModelBuilder.CssBadAtRuleBlock) && (child1 !is WxssFormattingModelBuilder.CssBadAtRuleBlock || child2.myType !== CssElementTypes.CSS_DECLARATION)) Spacing.createSpacing(1, 1, 0, false, 0) else Spacing.createSpacing(1, 1, 1, false, 1)
								}
							} else {
								Spacing.createSpacing(0, 0, 0, false, 0)
							}
						} else {
							if (this.settings.KEEP_SINGLE_LINE_BLOCKS) Spacing.createSpacing(0, 1, 0, true, 0) else Spacing.createSpacing(0, 1, 1, true, 1)
						}
					} else {
						Spacing.createSpacing(0, 0, 0, false, 0)
					}
				} else {
					return Spacing.createSpacing(1, 1, 1, true, 1)
				}
			} else {
				return null
			}
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val subBlocks = this.subBlocks
			val var10000: ChildAttributes?
			if (newChildIndex - 1 < subBlocks.size) {
				val prevSubBlock = subBlocks[newChildIndex - 1]
				if (prevSubBlock is WxssFormattingModelBuilder.CssFormatterBlock && prevSubBlock.node!!.elementType === CssElementTypes.CSS_COMMA) {
					var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)
					return var10000
				}
			}

			var10000 = ChildAttributes(Indent.getNormalIndent(), null as Alignment?)
			return var10000
		}

		override fun shouldIndentContent(): Boolean {
			return true
		}
	}

	open class CssPropertyBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, private val myNameLength: Int, alignment: Alignment?, childAlignment: Alignment?) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, indent, maxPropertyLength, extension, alignment, childAlignment) {

		override fun shouldIndentContent(): Boolean {
			return true
		}

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			if (child1 is WxssFormattingModelBuilder.CssFormatterBlock && child2 is WxssFormattingModelBuilder.CssFormatterBlock) {
				val formatterBlock = child1 as WxssFormattingModelBuilder.CssFormatterBlock?
				val settings = this.settings
				if (settings.VALUE_ALIGNMENT == 1 && child2.myType === CssElementTypes.CSS_COLON && this.myNameLength != -1) {
					return Spacing.createSpacing(1, 1, 0, false, 0)
				} else if (this.myExtension.isLineComment(formatterBlock!!.myType)) {
					return Spacing.createSpacing(0, 0, 1, true, 1)
				} else if (formatterBlock.myType !== CssElementTypes.CSS_LPAREN && child2.myType !== CssElementTypes.CSS_RPAREN) {
					if (child2.myType === CssElementTypes.CSS_COMMA) {
						return Spacing.createSpacing(0, 0, 0, false, 0)
					} else if (formatterBlock.myType === CssElementTypes.CSS_COMMA) {
						return Spacing.createSpacing(1, 1, 0, true, 0)
					} else if (child2.myType === CssElementTypes.CSS_LPAREN && this.node!!.elementType === CssElementTypes.CSS_IMPORT) {
						return Spacing.createSpacing(0, 1, 0, false, 0)
					} else {
						val textOfFormatterBlock2 = child2.node?.let { it.text.toLowerCase(Locale.US) } ?: ""
						return if (formatterBlock.myType !== CssElementTypes.CSS_MINUS &&
							formatterBlock.myType !== CssElementTypes.CSS_ASTERISK &&
							(formatterBlock.myType !== CssElementTypes.CSS_PLUS || child2.myType !== CssElementTypes.CSS_NUMBER) &&
							(formatterBlock.myType !== CssElementTypes.CSS_PLUS || child2.myType !== CssElementTypes.CSS_COLON) &&
							(formatterBlock.myType !== CssElementTypes.CSS_IDENT || child2.myType !== CssElementTypes.CSS_PLUS) &&
							(formatterBlock.myType !== CssElementTypes.CSS_PLUS || child2.myType !== CssElementTypes.CSS_IDENT) &&
							child2.myType !== CssElementTypes.CSS_COLON &&
							child2.myType !== CssElementTypes.CSS_SEMICOLON &&
							child2.myType !== CssElementTypes.CSS_PERCENT &&
							(formatterBlock.myType !== CssElementTypes.CSS_NUMBER &&
								formatterBlock.myType !== XmlTokenType.XML_COMMENT_START ||
								child2.myType !== CssElementTypes.CSS_IDENT ||
								CssTermImpl.getTypeBySuffix(textOfFormatterBlock2) === CssTermType.UNKNOWN &&
								"n" != textOfFormatterBlock2 && "x" != textOfFormatterBlock2
								&& !TimeUserLookup.isTimeSuffix(textOfFormatterBlock2))) {

							if (child2.myType === CssElementTypes.CSS_LPAREN) {
								Spacing.createSpacing(0, 1, 0, false, 0)
							} else if (formatterBlock.myType === CssElementTypes.CSS_COLON) {
								if (settings.SPACE_AFTER_COLON)
									Spacing.createSpacing(1, 1, 0, false, 0)
								else
									Spacing.createSpacing(0, 0, 0, false, 0)
							} else {
								// TODO Just Do this, and everything is OK ~\(≧▽≦)/~
								// TODO is just a notation, not means todo.
								val firstText = child1.node?.psi?.text.orEmpty()
								val secondText = child2.node?.psi?.text.orEmpty()
								println("$firstText \t $secondText\n")
								val space =
									when {
										"rpx" == secondText -> 0
										secondText.all { it.isLetterOrDigit() || it == '%' } -> 1
										else -> 0
									}
								space.let {
									Spacing.createSpacing(0, it, 0, false, 0)
								}
							}
						} else {
							Spacing.createSpacing(0, 0, 0, false, 0)
						}
					}
				} else {
					return Spacing.createSpacing(0, 0, 0, true, 0)
				}
			} else {
				return null
			}
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)


			return var10000
		}
	}

	class CssSelectorBlock(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, indent, maxPropertyLength, extension) {

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			return if (child2 is WxssFormattingModelBuilder.CssFormatterBlock && child2.myType === CssElementTypes.CSS_PERCENT) Spacing.createSpacing(0, 0, 0, false, 0) else Spacing.createSpacing(1, 1, 0, true, 0)
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)


			return var10000
		}

		override fun toString(): String {
			return this.myNode!!.text
		}
	}

	class CssOperationBlock(_node: ASTNode, settings: CssCodeStyleSettings, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, Indent.getNoneIndent(), maxPropertyLength, extension) {

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			return if (this.myNode!!.elementType !== CssElementTypes.CSS_BINARY_OPERATION || (child1 !is WxssFormattingModelBuilder.CssFormatterBlock || !CssMathParser.OPERATORS.contains(child1.myType)) && (child2 !is WxssFormattingModelBuilder.CssFormatterBlock || !CssMathParser.OPERATORS.contains(child2.myType))) Spacing.createSpacing(0, 0, 0, false, 0) else Spacing.createSpacing(1, 1, 0, true, 0)
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)
			return var10000
		}
	}

	class CssSimpleBlock @JvmOverloads constructor(_node: ASTNode, settings: CssCodeStyleSettings, indent: Indent, maxPropertyLength: Int, extension: WxssFormattingModelBuilder.CssFormattingExtension, alignment: Alignment? = null) : WxssFormattingModelBuilder.CssFormatterBlock(_node, settings, indent, maxPropertyLength, extension, alignment, null as Alignment?) {

		override fun getSpacing(child1: Block?, child2: Block): Spacing? {
			return null
		}

		override fun getSubBlocks(): List<Block> {
			return emptyList()
		}

		override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
			val var10000 = ChildAttributes(Indent.getNoneIndent(), null as Alignment?)
			return var10000
		}

		override fun toString(): String {
			return this.myNode!!.text
		}
	}
}
