package com.facegarden.mybatis.plugins;

public enum CodeLayoutEnum {
	
	DAL_LAYOUT("dal"), 
	SERVICE_LAYOUT("service"), 
	MANAGER_LAYOUT("manager"), 
	CONTROLLER_LAYOUT("controller");

	public String name;

	private CodeLayoutEnum(String name) {
		this.name = name;
	}
}
