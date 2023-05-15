package com.github.pavelpilyak.aioptimizer.actions

import com.github.pavelpilyak.aioptimizer.services.ApiService
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager


class OptimizeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val service = project?.service<ApiService>()
        val editor = e.getData(CommonDataKeys.EDITOR)
        val document = editor?.document

        editor?.caretModel?.primaryCaret?.run {
            val start = selectionStart
            val end = selectionEnd
            document?.getText(TextRange(start, end))?.let { code ->
                WriteCommandAction.runWriteCommandAction(project) {
                    service?.optimizeCode(code)?.let {
                        document.replaceString(start, end, it)
                    }
                }
                removeSelection()
            }
        }

        ReformatCodeProcessor(project, false).run()
    }
}