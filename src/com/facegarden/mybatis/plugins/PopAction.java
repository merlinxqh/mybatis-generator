package com.facegarden.mybatis.plugins;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.mybatis.generator.eclipse.ui.content.ConfigurationFileAdapter;

public class PopAction implements IObjectActionDelegate {
	private IFile selectedFile;
	private Shell shell;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.shell = targetPart.getSite().getShell();
	}

	public void run(IAction action) {
		createWizrd(this.selectedFile);
	}

	private void createWizrd(IFile selectedFile) {
		try {
			WizardWindow w = new WizardWindow(selectedFile);
			if (!w.isValidate()) {
				MessageDialog.openError(this.shell, "提醒", "配置文件有问题，请检查下列配置:\n"
						+ w.getMessage());
				return;
			}
			WizardDialogExt dialog = new WizardDialogExt(this.shell, w);
			dialog.setPageSize(550, 450);
			dialog.create();

			Rectangle screenSize = Display.getDefault().getClientArea();
			Shell shell = dialog.getShell();
			shell.setLocation(
					(screenSize.width - dialog.getShell().getBounds().width) / 2,
					(screenSize.height - dialog.getShell().getBounds().height) / 2);
			dialog.open();
		} catch (Exception e) {
			Tools.writeLine("show window is error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;
		ConfigurationFileAdapter adapter = new ConfigurationFileAdapter(
				(IFile) ss.getFirstElement());
		if (adapter != null) {
			this.selectedFile = adapter.getBaseFile();
		}
	}
}
