package com.idea.tools.view.action;

import com.idea.tools.view.ServersBrowseToolPanel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class ToolBarEditServerAction extends AbstractEditServerAction {

    public ToolBarEditServerAction(Project project, ServersBrowseToolPanel serversBrowseToolPanel) {
        super(project, serversBrowseToolPanel);
    }

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabled(isServerSelected());
    }

}
