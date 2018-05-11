package com.github.zxj5470.wxapp.registry.highlighter

/**
 * @author: zxj5470
 * @date: 2018/4/17
 */


import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class WxmlSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
		return WxmlHighlighter()
	}
}

class WxssSyntaxHighlighterFactory: SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
		return WxssHighlighter()
	}
}