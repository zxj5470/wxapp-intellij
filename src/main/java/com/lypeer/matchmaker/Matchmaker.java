package com.lypeer.matchmaker;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action的入口类，在这里执行各种初始化以及访问.wxml文件得到方法名的操作
 * <p>
 * Created by lypeer on 2016/9/28.
 */
class Matchmaker extends AnAction {
	@Override
	public void update(@NotNull AnActionEvent e) {
		boolean b = Ext.available(e);
		e.getPresentation().setEnabled(b);
	}

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		List<String> functionsName = new ArrayList<>();
		Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
		Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

		PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
		if (project == null || editor == null) {
			Utils.showErrorNotification(project, Constants.Message.ERROR_FILE_NULL);
			return;
		}

		if (file == null) {
			Utils.showErrorNotification(project, Constants.Message.ERROR_FILE_NULL);
			return;
		}

		String wxmlFileName = file.getName().replace("js", "wxml");
		PsiFile[] wxmlFiles = FilenameIndex.getFilesByName(project, wxmlFileName, GlobalSearchScope.allScope(project));
		if (wxmlFiles.length < 1) {
			Utils.showErrorNotification(project, Constants.Message.ERROR_NOT_FOUND);
			return;
		}
		if (wxmlFiles.length > 1) {
			Utils.showErrorNotification(project, Constants.Message.ERROR_MORE_THAN_ONE_FILE + wxmlFileName);
			return;
		}

		PsiFile wxmlFile = wxmlFiles[0];
		String wxmlContent = wxmlFile.getText();

		Pattern re = Utils.getRegexPattern();
		Matcher matcher = re.matcher(wxmlContent);

		List<String> tempList = new ArrayList<>();
		while ((matcher.find())) {
			tempList.add(matcher.group());
		}

		for (String s : tempList) {
			String quotes = "";
			if (s.contains("\"")) {
				quotes = "\"";
			} else if (s.contains("\'")) {
				quotes = "\'";
			}
			if (s.equals("") || s.split(quotes).length < 2) {
				continue;
			}
			functionsName.add(s.split(quotes)[1]);
		}


		try {
			int r = editor.getCaretModel().getOffset();
			ApplicationManager.getApplication().runWriteAction(() -> {
				CommandProcessor.getInstance().runUndoTransparentAction(() -> {
					try {
						Writer writer = new Writer(file, functionsName);
						writer.run();
						editor.getDocument().insertString(r, writer.res);
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					}
				});
			});
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

//    @Override
//    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
//        //保证只在js文件里显示
//        return file.getName().endsWith(".js");
//    }
}
