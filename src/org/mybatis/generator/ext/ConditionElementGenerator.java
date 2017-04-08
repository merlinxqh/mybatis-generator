package org.mybatis.generator.ext;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class ConditionElementGenerator extends AbstractXmlElementGenerator {
	
	public ConditionElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("sql");

        answer.addAttribute(new Attribute("id", 
                introspectedTable.getConditionStatementId()));

        context.getCommentGenerator().addComment(answer);

        XmlElement ifEl = new XmlElement("if");
        ifEl.addAttribute(new Attribute("test", "null!=params"));
        /*XmlElement ifEl2 = new XmlElement("if");
        ifEl2.addAttribute(new Attribute("test", "null!=params.queryCondition and ''!=params.queryCondition"));
        ifEl2.addElement(new TextElement("${params.queryCondition}"));
        ifEl.addElement(ifEl2);*/
        answer.addElement(ifEl);
        
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
            if (introspectedColumn.isIdentity()) {
                // cannot set values on identity fields
                continue;
            }

            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                continue;
            }            

            XmlElement valuesNotNullElement = new XmlElement("if"); 
            sb.setLength(0);
            sb.append("params.");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); 
            if ("String".equals(introspectedColumn.getFullyQualifiedJavaType().getShortName())) {
            	sb.append(" and '' != ");
            	sb.append("params.");
            	sb.append(introspectedColumn.getJavaProperty());
            }
            valuesNotNullElement.addAttribute(new Attribute("test", sb.toString())); 
            
            sb.setLength(0);
            sb.append("and ");
            sb.append("t." + introspectedColumn.getActualColumnName());//edit by zhugj "t."
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn, "params."));
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            ifEl.addElement(valuesNotNullElement);
        }
        
        if (context.getPlugins().sqlMapBaseColumnListElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
