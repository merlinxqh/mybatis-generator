package com.facegarden.mybatis.plugins;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardPageTwo extends WizardPage {
	private Map<CodeLayoutEnum, Boolean> codeLayoutMap;
	private String codeVersion;
	private Text txt_version;

	public WizardPageTwo() {
		super("page2");
		setTitle("生成代码层");
		setDescription("选择生成的代码层");
	}

	public void createControl(Composite parent) {
		this.codeLayoutMap = new HashMap<CodeLayoutEnum, Boolean>(3);
		Composite container = new Composite(parent, 0);
		container.setLayout(new BorderLayout());
		setControl(container);

		Group group = new Group(container, 0);
		group.setLayoutData(new BorderLayout.BorderData(2));
		group.setText("代码层");

		Button check_dal = new Button(group, 32);
		check_dal.setEnabled(false);
		check_dal.setSelection(true);
		check_dal.setBounds(10, 51, 98, 17);
		check_dal.setText("Dal层");

		final Button check_service = new Button(group, 32);
		check_service.setBounds(123, 51, 98, 17);
		check_service.setText("Service层");

		final Button check_manager = new Button(group, 32);
		check_manager.setBounds(247, 51, 98, 17);
		check_manager.setText("Manager层");

		final Button check_controller = new Button(group, 32);
		check_controller.setBounds(380, 51, 98, 17);
		check_controller.setText("Controller层");

		this.txt_version = new Text(group, 2048);
		this.txt_version.setToolTipText("此选项在生成java代码的注解中的版本号");
		this.txt_version.setText("1.0.0");
		this.txt_version.setBounds(55, 104, 73, 23);

		Label lblNewLabel = new Label(group, 0);
		lblNewLabel.setBounds(10, 107, 41, 17);
		lblNewLabel.setText("版本号");
		check_service.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean flag = ((Button) e.getSource()).getSelection();
				if (!flag) {
					check_manager.setSelection(false);
					check_controller.setSelection(false);
				}
				WizardPageTwo.this.codeLayoutMap.clear();
				WizardPageTwo.this.codeLayoutMap.put(
						CodeLayoutEnum.SERVICE_LAYOUT, Boolean.valueOf(true));
			}
		});
		check_manager.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean flag = ((Button) e.getSource()).getSelection();
				if (flag) {
					check_service.setSelection(flag);
				}
				if ((!flag) && (check_controller.getSelection())) {
					check_controller.setSelection(false);
				}
				WizardPageTwo.this.codeLayoutMap.clear();
				WizardPageTwo.this.codeLayoutMap.put(
						CodeLayoutEnum.SERVICE_LAYOUT, Boolean.valueOf(true));
				WizardPageTwo.this.codeLayoutMap.put(
						CodeLayoutEnum.MANAGER_LAYOUT, Boolean.valueOf(true));
			}
		});
		check_controller.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean flag = ((Button) e.getSource()).getSelection();
				if (flag) {
					check_service.setSelection(flag);
					check_manager.setSelection(flag);
				}
				WizardPageTwo.this.codeLayoutMap.clear();
				WizardPageTwo.this.codeLayoutMap.put(
						CodeLayoutEnum.SERVICE_LAYOUT, Boolean.valueOf(true));
				WizardPageTwo.this.codeLayoutMap.put(
						CodeLayoutEnum.MANAGER_LAYOUT, Boolean.valueOf(true));
				WizardPageTwo.this.codeLayoutMap.put(
						CodeLayoutEnum.CONTROLLER_LAYOUT, Boolean.valueOf(true));
			}
		});
	}

	public Map<CodeLayoutEnum, Boolean> getCodeLayoutMap() {
		return this.codeLayoutMap;
	}

	public String getCodeVersion() {
		return this.txt_version.getText();
	}
}
