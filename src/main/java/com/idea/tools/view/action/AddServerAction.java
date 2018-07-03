package com.idea.tools.view.action;

import com.idea.tools.JmsMessengerComponent;
import com.idea.tools.utils.ActionUtils;
import com.idea.tools.view.BrowserPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import javax.swing.*;

import static com.idea.tools.utils.GuiUtils.icon;

public class AddServerAction extends AnAction implements DumbAware {

    private static final Icon ICON = icon("plus.png");
    private final BrowserPanel browserPanel;

    public AddServerAction(BrowserPanel browserPanel) {
        super("Add server", "", ICON);
        this.browserPanel = browserPanel;
    }

    private static void showSettingsFor(Project project) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, JmsMessengerComponent.class);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        showSettingsFor(ActionUtils.getProject(event));
    }
}
