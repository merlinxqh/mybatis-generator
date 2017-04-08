package org.mybatis.generator.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mybatis.generator.internal.util.StringUtility;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MybatisConfigTools {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {       
        appendConfig(new File("D://conf/mybatis-config.xml"), "mapper/TestMapper.xml");
	}
	
	public static void appendConfig(File configFile, String appendStr) {
		try { 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        Document doc = builder.parse(configFile);
	        
	        Node parentNode = doc.getElementsByTagName("mappers").item(0);
	        NodeList nList = parentNode.getChildNodes();
	        for (int i = 0; i < nList.getLength(); i++) {
	        	Node node = nList.item(i);
	        	if ("mapper".equals(node.getNodeName())) {
	        		String attrVal = node.getAttributes().getNamedItem("resource").getNodeValue();
	        		if (StringUtility.stringHasValue(attrVal) &&
	        				attrVal.equals(appendStr)) {
	        			return;
	        		}
	        	}
			}
	        
	        //新增mapper节点
	        Element el = doc.createElement("mapper");
	        //给mapper节点增加resource属性
	        Attr att = doc.createAttribute("resource");
	        att.setValue(appendStr);
	        el.setAttributeNode(att);
	        parentNode.appendChild(el);
	        
	        TransformerFactory tf = TransformerFactory.newInstance();         
            
        	Transformer transformer = tf.newTransformer();          
        	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");             
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        	transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        	
        	transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-config.dtd");
        	transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTD Config 3.0//EN");
        	PrintWriter pw = new PrintWriter(new FileOutputStream(configFile));             
        	StreamResult result = new StreamResult(pw);
        	transformer.transform(new DOMSource(doc), result);
        	pw.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
}
