package org.mybatis.generator.config;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class YouGouSqlMapConfigConfiguration extends PropertyHolder {
	private String targetPackage;
	private String targetProject;
	private String confileFileName;
	private String confileFilePackagePath;

	public XmlElement toXmlElement() {
		XmlElement answer = new XmlElement("javaServiceGenerator");
		if (this.targetPackage != null) {
			answer.addAttribute(new Attribute("targetPackage",
					this.targetPackage));
		}
		if (this.targetProject != null) {
			answer.addAttribute(new Attribute("targetProject",
					this.targetProject));
		}
		if (this.confileFileName != null) {
			answer.addAttribute(new Attribute("confileFileName", ""));
		}
		if (this.confileFilePackagePath != null) {
			answer.addAttribute(new Attribute("confileFilePackagePath", ""));
		}
		addPropertyXmlElements(answer);

		return answer;
	}

	public String getTargetPackage() {
		return this.targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	public String getTargetProject() {
		return this.targetProject;
	}

	public void setTargetProject(String targetProject) {
		this.targetProject = targetProject;
	}

	public String getConfileFileName() {
		return this.confileFileName;
	}

	public void setConfileFileName(String confileFileName) {
		this.confileFileName = confileFileName;
	}

	public String getConfileFilePackagePath() {
		return this.confileFilePackagePath;
	}

	public void setConfileFilePackagePath(String confileFilePackagePath) {
		this.confileFilePackagePath = confileFilePackagePath;
	}
}
