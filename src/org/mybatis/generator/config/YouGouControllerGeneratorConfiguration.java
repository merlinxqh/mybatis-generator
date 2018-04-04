package org.mybatis.generator.config;

import java.util.List;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

public class YouGouControllerGeneratorConfiguration extends TypedPropertyHolder {
	private String targetPackage;
	private String implementationPackage;
	private String targetProject;
	private String interfaceExtendSupInterface;
	private String interfaceExtendSupInterfaceDoMain;
	private String enableInterfaceSupInterfaceGenericity;
	private String extendSupClass;
	private String extendSupClassDoMain;
	private String enableSupClassGenericity;

	public String getTargetProject() {
		return this.targetProject;
	}

	public void setTargetProject(String targetProject) {
		this.targetProject = targetProject;
	}

	public String getTargetPackage() {
		return this.targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	public String getExtendSupClass() {
		return this.extendSupClass;
	}

	public void setExtendSupClass(String extendSupClass) {
		this.extendSupClass = extendSupClass;
	}

	public String getExtendSupClassDoMain() {
		int point = -1;
		if ((this.extendSupClass != null) && (!"".equals(this.extendSupClass))) {
			point = this.extendSupClass.lastIndexOf(".");
		}
		if ((this.extendSupClass != null)
				&& (this.extendSupClass.length() > point)) {
			this.extendSupClassDoMain = this.extendSupClass
					.substring(point + 1);
		} else {
			this.extendSupClassDoMain = "";
		}
		return this.extendSupClassDoMain;
	}

	public String getEnableSupClassGenericity() {
		return this.enableSupClassGenericity;
	}

	public void setEnableSupClassGenericity(String enableSupClassGenericity) {
		this.enableSupClassGenericity = enableSupClassGenericity;
	}

	public String getInterfaceExtendSupInterface() {
		return this.interfaceExtendSupInterface;
	}

	public void setInterFaceExtendSupInterface(
			String interfaceExtendSupInterface) {
		this.interfaceExtendSupInterface = interfaceExtendSupInterface;
	}

	public String getEnableInterfaceSupInterfaceGenericity() {
		return this.enableInterfaceSupInterfaceGenericity;
	}

	public void setEnableInterfaceSupInterfaceGenericity(
			String enableInterfaceSupInterfaceGenericity) {
		this.enableInterfaceSupInterfaceGenericity = enableInterfaceSupInterfaceGenericity;
	}

	public String getInterFaceExtendSupInterfaceDoMain() {
		int point = -1;
		if ((this.interfaceExtendSupInterface != null)
				&& (!"".equals(this.interfaceExtendSupInterface))) {
			point = this.interfaceExtendSupInterface.lastIndexOf(".");
		}
		if ((this.interfaceExtendSupInterface != null)
				&& (this.interfaceExtendSupInterface.length() > point)) {
			this.interfaceExtendSupInterfaceDoMain = this.interfaceExtendSupInterface
					.substring(point + 1);
		} else {
			this.interfaceExtendSupInterfaceDoMain = "";
		}
		return this.interfaceExtendSupInterfaceDoMain;
	}

	public XmlElement toXmlElement() {
		XmlElement answer = new XmlElement("javaControllerGenerator");
		if (this.targetPackage != null) {
			answer.addAttribute(new Attribute("targetPackage",
					this.targetPackage));
		}
		if (this.targetProject != null) {
			answer.addAttribute(new Attribute("targetProject",
					this.targetProject));
		}
		if (this.extendSupClass != null) {
			answer.addAttribute(new Attribute("extendSupClass",
					this.extendSupClass));
		}
		if (this.enableSupClassGenericity != null) {
			answer.addAttribute(new Attribute("enableSupClassGenericity",
					this.enableSupClassGenericity));
		}
		if (this.interfaceExtendSupInterface != null) {
			answer.addAttribute(new Attribute("interfaceExtendSupInterface",
					this.interfaceExtendSupInterface));
		}
		if (this.enableInterfaceSupInterfaceGenericity != null) {
			answer.addAttribute(new Attribute(
					"enableInterfaceSupInterfaceGenericity",
					this.enableInterfaceSupInterfaceGenericity));
		}
		if (this.implementationPackage != null) {
			answer.addAttribute(new Attribute("implementationPackage",
					this.targetProject));
		}
		addPropertyXmlElements(answer);

		return answer;
	}

	public String getImplementationPackage() {
		return this.implementationPackage;
	}

	public void setImplementationPackage(String implementationPackage) {
		this.implementationPackage = implementationPackage;
	}

	public void validate(List<String> errors, String contextId) {
		if (!StringUtility.stringHasValue(this.targetProject)) {
			errors.add(Messages.getString("ValidationError.2", contextId));
		}
		if (!StringUtility.stringHasValue(this.targetPackage)) {
			errors.add(Messages.getString("ValidationError.12",
					"javaClientGenerator", contextId));
		}
	}
}
