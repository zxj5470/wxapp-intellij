package com.github.zxj5470.wxapp.editing

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class WxmlBackspaceHandler : BackspaceHandlerDelegate() {
	override fun beforeCharDeleted(c: Char, file: PsiFile, editor: Editor) {
//		if (c == '{') {
//			val offset = editor.caretModel.offset
//			val text = editor.document.getText(TextRange(offset - 2, offset + 2))
//			println(text)
//		}
	}

	override fun charDeleted(c: Char, file: PsiFile, editor: Editor): Boolean {
		return false
	}
}

class WxmlTypedHandlerDelegate : TypedHandlerDelegate() {
	override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
//		if (c == '{') {
//			val offset = editor.caretModel.offset
//			val text = editor.document.getText(TextRange(offset - 2, offset + 1))
//			println(text)
//		}
		return Result.CONTINUE
	}
}