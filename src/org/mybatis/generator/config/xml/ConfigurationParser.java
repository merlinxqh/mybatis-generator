/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mybatis.generator.config.xml;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.util.messages.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.facegarden.mybatis.plugins.CodeLayoutEnum;

public class ConfigurationParser {

    private List<String> warnings;
    private List<String> parseErrors;
    private Properties properties;

    public ConfigurationParser(List<String> warnings) {
        this(null, warnings);
    }

    public ConfigurationParser(Properties properties, List<String> warnings) {
        super();
        if (properties == null) {
            this.properties = System.getProperties();
        } else {
            this.properties = properties;
        }

        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }

        parseErrors = new ArrayList<String>();
    }

    public Configuration parseConfiguration(File inputFile) throws IOException,
            XMLParserException {
        FileReader fr = new FileReader(inputFile);
        return parseConfiguration(fr);
    }

    public Configuration parseConfiguration(Reader reader) throws IOException,
            XMLParserException {
        InputSource is = new InputSource(reader);
        return parseConfiguration(is, null, null, "");
    }

    public Configuration parseConfiguration(InputStream inputStream)
            throws IOException, XMLParserException {
        InputSource is = new InputSource(inputStream);
        return parseConfiguration(is, null, null, "");
    }
    
	public Configuration parseConfiguration(InputSource inputSource,
			List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout,
			String codeVersion) throws IOException, XMLParserException {
		this.parseErrors.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ParserEntityResolver());

			ParserErrorHandler handler = new ParserErrorHandler(this.warnings,
					this.parseErrors);
			builder.setErrorHandler(handler);

			Document document = null;
			try {
				document = builder.parse(inputSource);
			} catch (SAXParseException localSAXParseException) {
				throw new XMLParserException(this.parseErrors);
			} catch (SAXException e) {
				if (e.getException() == null) {
					this.parseErrors.add(e.getMessage());
				} else {
					this.parseErrors.add(e.getException().getMessage());
				}
			}
			if (this.parseErrors.size() > 0) {
				throw new XMLParserException(this.parseErrors);
			}
			Element rootNode = document.getDocumentElement();
			DocumentType docType = document.getDoctype();
			Configuration config;
			if ((rootNode.getNodeType() == 1)
					&& (docType.getPublicId()
							.equals("-//Apache Software Foundation//DTD Apache iBATIS Ibator Configuration 1.0//EN"))) {
				config = parseIbatorConfiguration(rootNode);
			} else {
				if ((rootNode.getNodeType() == 1)
						&& (docType.getPublicId()
								.equals("-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"))) {
					config = parseMyBatisGeneratorConfiguration(rootNode,
							tableList, codeLayout, codeVersion);
				} else {
					throw new XMLParserException(
							Messages.getString("RuntimeError.5"));
				}
			}
			if (this.parseErrors.size() > 0) {
				throw new XMLParserException(this.parseErrors);
			}
			return config;
		} catch (ParserConfigurationException e) {
			this.parseErrors.add(e.getMessage());
			throw new XMLParserException(this.parseErrors);
		}
	}
    
    private Configuration parseIbatorConfiguration(Element rootNode)
            throws XMLParserException {
        IbatorConfigurationParser parser = new IbatorConfigurationParser(
                properties);
        return parser.parseIbatorConfiguration(rootNode);
    }

    /*private Configuration parseMyBatisGeneratorConfiguration(Element rootNode)
            throws XMLParserException {
        MyBatisGeneratorConfigurationParser parser = new MyBatisGeneratorConfigurationParser(
                properties);
        return parser.parseConfiguration(rootNode);
    }*/
    
	private Configuration parseMyBatisGeneratorConfiguration(Element rootNode,
			List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout,
			String codeVersion) throws XMLParserException {
		MyBatisGeneratorConfigurationParser parser = new MyBatisGeneratorConfigurationParser(
				this.properties);
		return parser.parseConfiguration(rootNode, tableList, codeLayout,
				codeVersion);
	}
}
