package org.mybatis.generator.ext;

import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

public class SelectByParamsMethodGenerator extends
		AbstractJavaMapperMethodGenerator {
	
    public SelectByParamsMethodGenerator() {
        super();
    }
    
	@Override
	public void addInterfaceElements(Interface interfaze) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 条件查询.");
        method.addJavaDocLine(" */");
        
        FullyQualifiedJavaType modelType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("java.util.List<"+modelType.getShortName()+">");        
        importedTypes.add(returnType);
        importedTypes.add(FullyQualifiedJavaType.getNewMapInstance());
        method.setReturnType(returnType);
        
        method.setName(introspectedTable.getSelectByParamsStatementId());
        
        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param")); 
        
        importedTypes.add(modelType);
        
        StringBuilder sb = new StringBuilder();
        
        //param 2
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String, Object>"), "params");
        sb.setLength(0);
        sb.append("@Param(\""); 
        sb.append("params");
        sb.append("\")"); 
        parameter.addAnnotation(sb.toString());
        method.addParameter(parameter);
        
        addMapperAnnotations(interfaze, method);

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
	}

	public void addMapperAnnotations(Interface interfaze, Method method) {
        return;
    }
}
