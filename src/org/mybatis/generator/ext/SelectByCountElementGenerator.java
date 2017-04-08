package org.mybatis.generator.ext;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class SelectByCountElementGenerator extends AbstractXmlElementGenerator {
	
	public SelectByCountElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); 

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getSelectByCountStatementId())); 
        
        answer.addAttribute(new Attribute("resultType", "java.lang.Integer"));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select count(1) as s from "); 
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + " t "); //add by zhugj
        sb.append(" where 1=1");
        answer.addElement(new TextElement(sb.toString()));
        //include condtion
        XmlElement xmlEl = new XmlElement("include"); 
        xmlEl.addAttribute(new Attribute("refid", "condition"));
        answer.addElement(xmlEl);
        
        if (context.getPlugins()
                .sqlMapSelectByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
