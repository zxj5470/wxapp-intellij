package com.github.zxj5470.wxapp.registry.navigator

import com.github.zxj5470.wxapp.WxmlLanguage
import com.github.zxj5470.wxapp.ktlext.isWxFunction
import com.intellij.lang.javascript.navigation.JSGotoDeclarationHandler
import com.intellij.lang.javascript.psi.*
import com.intellij.navigation.HtmlGotoRelatedProvider
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.css.CssElement
import com.intellij.psi.css.CssRuleset
import com.intellij.psi.css.impl.CssTokenImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.xml.XmlAttribute

/**
 * @author zxj5470
 * @date 2018/5/12
 */
class WxmlGoToDeclarationProvider : JSGotoDeclarationHandler() {
	override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
		val elem: PsiElement = sourceElement ?: return null
		if (elem.containingFile.language != WxmlLanguage.INSTANCE) return null
		val el = PsiUtilCore.getElementAtOffset(elem.containingFile, offset)

		val fileName = el.containingFile.virtualFile.nameWithoutExtension
		val jsFile = el.containingFile.containingDirectory.findFile("$fileName.js") ?: return null
		val wxssFile = el.containingFile.containingDirectory.findFile("$fileName.wxss") ?: return null
		val globalWxssFile = el.containingFile.containingDirectory?.parentDirectory?.parentDirectory?.findFile("app.wxss")
		println(globalWxssFile)
		val identifierName = el.text
		val grandpar = elem.parent.parent
		when {
//			id or class
			grandpar is XmlAttribute && grandpar.nameElement.text
				.let { it == "id" && it == "class" } -> {
//				val ret = PsiTreeUtil.findChildrenOfType(wxssFile, CssTokenImpl::class.java).filter { token ->
//					token.text == grandpar.nameElement.text
//				}.mapNotNull { it as PsiElement }.firstOrNull() ?: return null
//				return arrayOf(ret)
				println("TODO OR NOT")
			}

//		 data variable
			"{{" in identifierName && "}}" in identifierName -> {
				val ident = identifierName.substringAfter("{{").substringBefore("}}")
				val ret = PsiTreeUtil.findChildrenOfType(jsFile, JSExpression::class.java).flatMap {
					PsiTreeUtil.findChildrenOfType(it, JSProperty::class.java).filter {
						it.nameIdentifier?.text == "data" &&
							it.value is JSObjectLiteralExpression
					}.mapNotNull {
						(it.value as JSObjectLiteralExpression).findProperty(ident) as PsiElement?
					}
				}.toTypedArray().firstOrNull() ?: return null
				return arrayOf(ret)
			}

//		 function
			identifierName.isWxFunction() -> {
				val ret = PsiTreeUtil.findChildrenOfType(jsFile, JSExpression::class.java).flatMap {
					PsiTreeUtil.findChildrenOfType(it, JSProperty::class.java).filter {
						it.value is JSFunctionExpression && it.nameIdentifier?.text == identifierName
					}
				}.mapNotNull { it as PsiElement }.toTypedArray().firstOrNull() ?: return null
				return arrayOf(ret)
			}
		}
		return null
	}

	override fun getActionText(context: DataContext?): String? = null

}