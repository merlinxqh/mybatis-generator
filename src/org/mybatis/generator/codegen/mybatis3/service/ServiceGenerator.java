package org.mybatis.generator.codegen.mybatis3.service;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.YouGouServiceGeneratorConfiguration;

/**
 * Service层代码生成器 
 * 
 * @author HuangTao
 * @version 2015年5月25日
 */
public class ServiceGenerator extends AbstractJavaGenerator {
	
	public List<CompilationUnit> getCompilationUnits() {
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
				introspectedTable.getMyBatis3JavaServiceType());
		Interface interfaze = new Interface(type);
		interfaze.setVisibility(JavaVisibility.PUBLIC);

		JavaClientGeneratorConfiguration clientConfig = context.getJavaClientGeneratorConfiguration();
		YouGouServiceGeneratorConfiguration serviceConfig = context.getYouGouServiceGeneratorConfiguration();
		
		//Mapper 超类
		String clientSupInterface = "com.facegarden.base.common.dao.CrudMapper";
		if (stringHasValue(clientConfig.getInterfaceExtendSupInterface())) {
			clientSupInterface = clientConfig.getInterfaceExtendSupInterface();
		}
		
		String modelName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
		
		if (stringHasValue(serviceConfig.getInterFaceExtendSupInterfaceDoMain())) {
			interfaze.addImportedType(new FullyQualifiedJavaType(serviceConfig
					.getInterfaceExtendSupInterface()));
			String base = serviceConfig.getInterFaceExtendSupInterfaceDoMain();
			if (new Boolean(
					serviceConfig.getEnableInterfaceSupInterfaceGenericity())
					.booleanValue()) {
				interfaze.addImportedType(new FullyQualifiedJavaType(
						this.introspectedTable.getBaseRecordType()));
				interfaze.addSuperInterface(new FullyQualifiedJavaType(base
						+ "<" + modelName + ">"));
			} else {
				interfaze.addSuperInterface(new FullyQualifiedJavaType(base));
			}
			commentGenerator.addInterfaceComment(interfaze, introspectedTable);
		}
		
		//Service Impl
		TopLevelClass c = new TopLevelClass(new FullyQualifiedJavaType(
				introspectedTable.getMyBatis3JavaServiceImplType()));
		c.setVisibility(JavaVisibility.PUBLIC);
		c.addImportedType(new FullyQualifiedJavaType(
				"javax.annotation.Resource"));
		c.addImportedType(new FullyQualifiedJavaType(
				"org.springframework.stereotype.Service"));
		c.addImportedType(new FullyQualifiedJavaType(serviceConfig
				.getExtendSupClass()));
		c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable
				.getMyBatis3JavaMapperType()));
		c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable
				.getMyBatis3JavaServiceType()));
		if (stringHasValue(clientSupInterface)) {
			c.addImportedType(new FullyQualifiedJavaType(clientSupInterface));
		}
		
		String serviceType = introspectedTable.getMyBatis3JavaServiceType();
		if (stringHasValue(clientSupInterface)) {
			String ServiceName = firstCharToLower(serviceType.substring(serviceType.lastIndexOf(".") + 1));
			c.addAnnotation("@Service(\"" + ServiceName + "\")");
			if (new Boolean(serviceConfig.getEnableSupClassGenericity())
					.booleanValue()) {
				c.addImportedType(new FullyQualifiedJavaType(
						this.introspectedTable.getBaseRecordType()));
				c.setSuperClass(new FullyQualifiedJavaType(serviceConfig
						.getExtendSupClassDoMain() + "<" + modelName + ">"));
			} else {
				c.setSuperClass(new FullyQualifiedJavaType(serviceConfig.getExtendSupClassDoMain()));
			}
			c.addSuperInterface(new FullyQualifiedJavaType(modelName + "Service"));
		}
		
		String mapperType = introspectedTable.getMyBatis3JavaMapperType();
		Field field = new Field();
		field.addAnnotation("@Resource");
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(new FullyQualifiedJavaType(mapperType));
		String fieldName = firstCharToLower(mapperType.substring(mapperType.lastIndexOf(".") + 1));
		field.setName(fieldName);
		c.addField(field);

		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addAnnotation("@Override");
		if (new Boolean(serviceConfig.getEnableSupClassGenericity())
				.booleanValue()) {
			method.setReturnType(new FullyQualifiedJavaType(clientSupInterface
					+ "<"
					+ modelName
					+ ">"));
		} else {
			method.setReturnType(new FullyQualifiedJavaType(clientSupInterface));
		}
		method.setName("init");
		method.addBodyLine("return " + fieldName + ";");
		c.addMethod(method);

		commentGenerator.addClassComment(c, introspectedTable);

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(interfaze);
		answer.add(c);
		return answer;
	}

	private String firstCharToLower(String modelName) {
		if (modelName.length() > 0) {
			modelName = new StringBuilder(String.valueOf(modelName.charAt(0)))
					.toString().toLowerCase() + modelName.substring(1);
		} else {
			modelName = modelName.toLowerCase();
		}
		return modelName;
	}
}
