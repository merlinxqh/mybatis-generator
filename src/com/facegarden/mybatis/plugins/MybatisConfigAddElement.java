package com.facegarden.mybatis.plugins;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MybatisConfigAddElement {
	
	public static void appendGeneratorXml(String configFilePath,
			String confileFilePackagePath, String xmlFileName) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(configFilePath));
			doc.setXmlStandalone(true);

			NodeList nodeList = doc.getElementsByTagName("mappers");
			if (nodeList.getLength() > 0) {
				if (confileFilePackagePath.length() > 0) {
					confileFilePackagePath =

					confileFilePackagePath + "/";
				}
				xmlFileName = confileFilePackagePath + xmlFileName;

				NodeList mapperList = doc.getElementsByTagName("mapper");
				boolean flag = false;
				for (int i = 0; i < mapperList.getLength(); i++) {
					NamedNodeMap map = mapperList.item(i).getAttributes();
					if (map.getNamedItem("resource").getNodeValue() != null) {
						if (map.getNamedItem("resource").getNodeValue()
								.equals(xmlFileName)) {
							flag = true;
							break;
						}
					}
				}
				if (flag) {
					return;
				}
				Node mappersNode = nodeList.item(0);
				Element mapperNode = doc.createElement("mapper");
				mapperNode.setAttribute("resource", xmlFileName);

				mappersNode.appendChild(mapperNode);

				doc.normalizeDocument();
				TransformerFactory tfactory = TransformerFactory.newInstance();
				Transformer transformer = tfactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(configFilePath));
				transformer.setOutputProperty("indent", "yes");
				transformer.setOutputProperty("cdata-section-elements", "yes");
				transformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");

				transformer.setOutputProperty("doctype-public", doc
						.getDoctype().getPublicId());
				transformer.setOutputProperty("doctype-system", doc
						.getDoctype().getSystemId());

				transformer.setOutputProperty("encoding", "UTF-8");
				transformer.transform(source, result);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
