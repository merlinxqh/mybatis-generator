package org.mybatis.generator.ext;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class SelectByPageElementGenerator extends AbstractXmlElementGenerator {
	
	public SelectByPageElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); 

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getSelectByPageStatementId())); 
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap", 
                    introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap", 
                    introspectedTable.getBaseResultMapId()));
        }

        answer.addAttribute(new Attribute("parameterType", "map"));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select "); 
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(",")); 
            answer.addElement(getBlobColumnListElement());
        }

        sb.setLength(0);
        sb.append("from "); 
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime() + " t ");//add by zhugj
        answer.addElement(new TextElement(sb.toString()));
        
        answer.addElement(new TextElement("where 1=1"));
        //include condtion
        XmlElement xmlEl = new XmlElement("include"); 
        xmlEl.addAttribute(new Attribute("refid", "condition"));
        answer.addElement(xmlEl);
        
        XmlElement ifEl = new XmlElement("if");
        ifEl.addAttribute(new Attribute("test", "orderByField != null and ''!=orderByField"));
        ifEl.addElement(new TextElement("order by t.${orderByField} "));//add by zhugj
        XmlElement ifEl2 = new XmlElement("if");
        ifEl2.addAttribute(new Attribute("test", "orderByField"));
        ifEl2.addElement(new TextElement("${orderBy}"));
        ifEl.addElement(ifEl2);
        answer.addElement(ifEl);
        //noted by zhugj,remove limit statement.
        //answer.addElement(new TextElement("limit #{page.startRowNum} ,#{page.pageSize}"));
        
        if (context.getPlugins()
                .sqlMapSelectByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
