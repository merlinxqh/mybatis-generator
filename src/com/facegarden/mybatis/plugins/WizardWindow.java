package com.facegarden.mybatis.plugins;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.db.ConnectionFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;

public class WizardWindow extends Wizard {
	public WizardPageOne pageOne;
	public WizardPageTwo pageTwo;
	private Map<String, List<String>> tableMap = null;
	private IFile selectedFile;
	private boolean validate;
	private String message;

	public WizardWindow(IFile selectedFile) {
		setWindowTitle("向导");
		setNeedsProgressMonitor(true);
		setTitleBarColor(new RGB(255, 0, 0));
		setWindowTitle("选择表信息");
		this.selectedFile = selectedFile;
		this.validate = loadConn(selectedFile);
	}

	public boolean isValidate() {
		return this.validate;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean canFinish() {
		if (getContainer().getCurrentPage() == this.pageTwo) {
			return true;
		}
		return false;
	}

	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	public boolean performCancel() {
		return super.performCancel();
	}

	public void addPages() {
		this.pageOne = new WizardPageOne(this.tableMap);
		addPage(this.pageOne);
		this.pageTwo = new WizardPageTwo();
		addPage(this.pageTwo);
	}

	private boolean loadConn(IFile selectedFile) {
		try {
			IPath rawPath = selectedFile.getRawLocation();
			File configurationFile = rawPath.toFile();
			ConfigurationParser cp = new ConfigurationParser(null);
			Tools.writeLine("config file:"
					+ configurationFile.getAbsolutePath());
			Configuration config = cp.parseConfiguration(configurationFile);
			List<Context> contexts = config.getContexts();
			if (config.getClassPathEntries().size() > 0) {
				ClassLoader classLoader = ClassloaderUtility
						.getCustomClassloader(config.getClassPathEntries());
				ObjectFactory.addExternalClassLoader(classLoader);
			} else {
				this.message = "The xml file is invalid.";
				Tools.writeLine(this.message);
				return false;
			}
			if ((contexts != null) && (contexts.size() > 0)
					&& (contexts.get(0) != null)) {
				JDBCConnectionConfiguration jdbcConfig = ((Context) contexts
						.get(0)).getJdbcConnectionConfiguration();
				Connection con = null;
				try {
					con = ConnectionFactory.getInstance().getConnection(
							jdbcConfig);

					DatabaseMetaData meta = con.getMetaData();
					ResultSet rs = meta.getTables(null, null, null,
							new String[] { "TABLE" });
					this.tableMap = new HashMap<String, List<String>>();
					while (rs.next()) {
						if ((rs.getString(1) != null)
								&& (this.tableMap.get(rs.getString(1)) == null)) {
							List<String> tableList = new ArrayList<String>();
							tableList.add(rs.getString(3));
							this.tableMap.put(rs.getString(1), tableList);
						} else if (rs.getString(1) != null) {
							(tableMap.get(rs.getString(1))).add(rs
									.getString(3));
						}
						if ((rs.getString(2) != null)
								&& (this.tableMap.get(rs.getString(2)) == null)) {
							List<String> tableList = new ArrayList<String>();
							tableList.add(rs.getString(3));
							this.tableMap.put(rs.getString(2), tableList);
						} else if (rs.getString(2) != null) {
							(tableMap.get(rs.getString(2))).add(rs
									.getString(3));
						}
					}
					con.close();
					return true;
				} catch (Exception e) {
					try {
						if (con != null) {
							con.close();
						}
					} catch (SQLException e1) {
						this.message = e1.getLocalizedMessage();
						Tools.writeLine(this.message);
					}
					this.message = e.getLocalizedMessage();
					Tools.writeLine(this.message);
					return false;
				}
			}
			this.message = "The xml file is incorrect.";
			Tools.writeLine(this.message);
			return false;
		} catch (Exception e) {
			this.message = ("init connection error:" + e.getMessage());
			Tools.writeLine(this.message);
		}
		return false;
	}

	public boolean performFinish() {
		Shell shell = getShell();
		try {
			List<String> warnings = new ArrayList<String>();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

			Map<CodeLayoutEnum, Boolean> codeLayoutSwitch = this.pageTwo
					.getCodeLayoutMap();
			String codeVersion = this.pageTwo.getCodeVersion();
			if (codeLayoutSwitch.size() == 0) {
				codeLayoutSwitch.put(CodeLayoutEnum.SERVICE_LAYOUT,
						Boolean.valueOf(false));
				codeLayoutSwitch.put(CodeLayoutEnum.MANAGER_LAYOUT,
						Boolean.valueOf(false));
				codeLayoutSwitch.put(CodeLayoutEnum.CONTROLLER_LAYOUT,
						Boolean.valueOf(false));
			}
			IRunnableWithProgress thread = new GeneratorRunner(warnings,
					this.pageOne.getTableList(), codeLayoutSwitch, codeVersion);

			dialog.run(true, false, thread);
			if (warnings.size() > 0) {
				MultiStatus ms = new MultiStatus(
						"org.mybatis.generator.eclipse.ui", 2,
						"Generation Warnings Occured", null);

				Iterator<String> iter = warnings.iterator();
				while (iter.hasNext()) {
					Status status = new Status(2,
							"org.mybatis.generator.eclipse.ui", 2,
							(String) iter.next(), null);
					ms.add(status);
				}
				ErrorDialog.openError(shell, "MyBatis Generator",
						"Run Complete With Warnings", ms, 2);
			}
		} catch (Exception e) {
			handleException(e, shell);
		}
		return true;
	}

	private class GeneratorRunner implements IRunnableWithProgress {
		private List<String> warnings;
		private List<String> tableList;
		private Map<CodeLayoutEnum, Boolean> codeLayout;
		private String codeVersion;

		public GeneratorRunner(List<String> warnings,
				List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout, String codeVersion) {
			this.warnings = warnings;
			this.tableList = tableList;
			this.codeLayout = codeLayout;
			this.codeVersion = codeVersion;
		}

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				RunGeneratorThread thread = new RunGeneratorThread(
						WizardWindow.this.selectedFile, this.warnings,
						this.tableList, this.codeLayout, this.codeVersion);

				ResourcesPlugin.getWorkspace().run(thread, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}
	}

	private void handleException(Exception exception, Shell shell) {
		Throwable exceptionToHandle;
		if ((exception instanceof InvocationTargetException)) {
			exceptionToHandle = ((InvocationTargetException) exception)
					.getCause();
		} else {
			exceptionToHandle = exception;
		}
		IStatus status;
		if ((exceptionToHandle instanceof InterruptedException)) {
			status = new Status(8, "org.mybatis.generator.eclipse.ui", 8,
					"Cancelled by User", exceptionToHandle);
		} else {
			if ((exceptionToHandle instanceof CoreException)) {
				status = ((CoreException) exceptionToHandle).getStatus();
			} else {
				String message = "Unexpected error while running MyBatis Generator.";

				status = new Status(4, "org.mybatis.generator.eclipse.ui", 4,
						message, exceptionToHandle);

				Activator.getDefault().getLog().log(status);
			}
		}
		ErrorDialog.openError(shell, "MyBatis Generator", "Generation Failed",
				status, 12);
	}
}
