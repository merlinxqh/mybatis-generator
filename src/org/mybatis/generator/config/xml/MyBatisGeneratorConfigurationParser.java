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

import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.ColumnRenamingRule;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.IgnoredColumn;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.YouGouControllerGeneratorConfiguration;
import org.mybatis.generator.config.YouGouManagerGeneratorConfiguration;
import org.mybatis.generator.config.YouGouServiceGeneratorConfiguration;
import org.mybatis.generator.config.YouGouSqlMapConfigConfiguration;
import org.mybatis.generator.config.YouGouTableSettingConfiguration;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.facegarden.mybatis.plugins.CodeLayoutEnum;

/**
 * This class parses configuration files into the new Configuration API
 * 
 * @author Jeff Butler
 */
public class MyBatisGeneratorConfigurationParser {
    private Properties properties;

    public MyBatisGeneratorConfigurationParser(Properties properties) {
        super();
        if (properties == null) {
            this.properties = System.getProperties();
        } else {
            this.properties = properties;
        }
    }
    
	public Configuration parseConfiguration(Element rootNode,
			List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout,
			String codeVersion) throws XMLParserException {
		Configuration configuration = new Configuration();
		
		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == 1) {
				if ("properties".equals(childNode.getNodeName())) {
					parseProperties(configuration, childNode);
				} else if ("classPathEntry".equals(childNode.getNodeName())) {
					parseClassPathEntry(configuration, childNode);
				} else if ("context".equals(childNode.getNodeName())) {
					parseContext(configuration, childNode, tableList,
							codeLayout, codeVersion);
				}
			}
		}
		configuration.setProperties(properties);
		return configuration;
	}
    
    public Configuration parseConfiguration(Element rootNode)
            throws XMLParserException {

        Configuration configuration = new Configuration();

        NodeList nodeList = rootNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("properties".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperties(configuration, childNode);
            } else if ("classPathEntry".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseClassPathEntry(configuration, childNode);
            } else if ("context".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseContext(configuration, childNode);
            }
        }
        configuration.setProperties(properties);
        return configuration;
    }

    private void parseProperties(Configuration configuration, Node node)
            throws XMLParserException {
        Properties attributes = parseAttributes(node);
        String resource = attributes.getProperty("resource"); //$NON-NLS-1$
        String url = attributes.getProperty("url"); //$NON-NLS-1$

        if (!stringHasValue(resource)
                && !stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14")); //$NON-NLS-1$
        }

        if (stringHasValue(resource)
                && stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14")); //$NON-NLS-1$
        }

        URL resourceUrl;

        try {
            if (stringHasValue(resource)) {
                resourceUrl = ObjectFactory.getResource(resource);
                if (resourceUrl == null) {
                    throw new XMLParserException(getString(
                            "RuntimeError.15", resource)); //$NON-NLS-1$
                }
            } else {
                resourceUrl = new URL(url);
            }

            InputStream inputStream = resourceUrl.openConnection()
                    .getInputStream();

            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            if (stringHasValue(resource)) {
                throw new XMLParserException(getString(
                        "RuntimeError.16", resource)); //$NON-NLS-1$
            } else {
                throw new XMLParserException(getString(
                        "RuntimeError.17", url)); //$NON-NLS-1$
            }
        }
    }

    private void parseContext(Configuration configuration, Node node) {

        Properties attributes = parseAttributes(node);
        String defaultModelType = attributes.getProperty("defaultModelType"); //$NON-NLS-1$
        String targetRuntime = attributes.getProperty("targetRuntime"); //$NON-NLS-1$
        String introspectedColumnImpl = attributes
                .getProperty("introspectedColumnImpl"); //$NON-NLS-1$
        String id = attributes.getProperty("id"); //$NON-NLS-1$

        ModelType mt = defaultModelType == null ? null : ModelType
                .getModelType(defaultModelType);

        Context context = new Context(mt);
        context.setId(id);
        if (stringHasValue(introspectedColumnImpl)) {
            context.setIntrospectedColumnImpl(introspectedColumnImpl);
        }
        if (stringHasValue(targetRuntime)) {
            context.setTargetRuntime(targetRuntime);
        }

        configuration.addContext(context);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(context, childNode);
            } else if ("plugin".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parsePlugin(context, childNode);
            } else if ("commentGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseCommentGenerator(context, childNode);
            } else if ("jdbcConnection".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseJdbcConnection(context, childNode);
            } else if ("javaModelGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseJavaModelGenerator(context, childNode);
            } else if ("javaTypeResolver".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseJavaTypeResolver(context, childNode);
            } else if ("sqlMapGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseSqlMapGenerator(context, childNode);
            } else if ("javaClientGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseJavaClientGenerator(context, childNode);
            } else if ("table".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseTable(context, childNode);
            }
        }
    }
    
    private void parseSqlMapGenerator(Context context, Node node) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();

        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage"); //$NON-NLS-1$
        String targetProject = attributes.getProperty("targetProject"); //$NON-NLS-1$

        sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(sqlMapGeneratorConfiguration, childNode);
            }
        }
    }

    private void parseTable(Context context, Node node) {
        TableConfiguration tc = new TableConfiguration(context);
        context.addTableConfiguration(tc);

        Properties attributes = parseAttributes(node);
        String catalog = attributes.getProperty("catalog"); //$NON-NLS-1$
        String schema = attributes.getProperty("schema"); //$NON-NLS-1$
        String tableName = attributes.getProperty("tableName"); //$NON-NLS-1$
        String domainObjectName = attributes.getProperty("domainObjectName"); //$NON-NLS-1$
        String alias = attributes.getProperty("alias"); //$NON-NLS-1$
        String enableInsert = attributes.getProperty("enableInsert"); //$NON-NLS-1$
        String enableSelectByPrimaryKey = attributes
                .getProperty("enableSelectByPrimaryKey"); //$NON-NLS-1$
        String enableSelectByExample = attributes
                .getProperty("enableSelectByExample"); //$NON-NLS-1$
        String enableUpdateByPrimaryKey = attributes
                .getProperty("enableUpdateByPrimaryKey"); //$NON-NLS-1$
        String enableDeleteByPrimaryKey = attributes
                .getProperty("enableDeleteByPrimaryKey"); //$NON-NLS-1$
        String enableDeleteByExample = attributes
                .getProperty("enableDeleteByExample"); //$NON-NLS-1$
        String enableCountByExample = attributes
                .getProperty("enableCountByExample"); //$NON-NLS-1$
        String enableUpdateByExample = attributes
                .getProperty("enableUpdateByExample"); //$NON-NLS-1$
        String selectByPrimaryKeyQueryId = attributes
                .getProperty("selectByPrimaryKeyQueryId"); //$NON-NLS-1$
        String selectByExampleQueryId = attributes
                .getProperty("selectByExampleQueryId"); //$NON-NLS-1$
        String modelType = attributes.getProperty("modelType"); //$NON-NLS-1$
        String escapeWildcards = attributes.getProperty("escapeWildcards"); //$NON-NLS-1$
        String delimitIdentifiers = attributes
                .getProperty("delimitIdentifiers"); //$NON-NLS-1$
        String delimitAllColumns = attributes.getProperty("delimitAllColumns"); //$NON-NLS-1$

        if (stringHasValue(catalog)) {
            tc.setCatalog(catalog);
        }

        if (stringHasValue(schema)) {
            tc.setSchema(schema);
        }

        if (stringHasValue(tableName)) {
            tc.setTableName(tableName);
        }

        if (stringHasValue(domainObjectName)) {
            tc.setDomainObjectName(domainObjectName);
        }

        if (stringHasValue(alias)) {
            tc.setAlias(alias);
        }

        if (stringHasValue(enableInsert)) {
            tc.setInsertStatementEnabled(isTrue(enableInsert));
        }

        if (stringHasValue(enableSelectByPrimaryKey)) {
            tc.setSelectByPrimaryKeyStatementEnabled(
                    isTrue(enableSelectByPrimaryKey));
        }

        if (stringHasValue(enableSelectByExample)) {
            tc.setSelectByExampleStatementEnabled(
                    isTrue(enableSelectByExample));
        }

        if (stringHasValue(enableUpdateByPrimaryKey)) {
            tc.setUpdateByPrimaryKeyStatementEnabled(
                    isTrue(enableUpdateByPrimaryKey));
        }

        if (stringHasValue(enableDeleteByPrimaryKey)) {
            tc.setDeleteByPrimaryKeyStatementEnabled(
                    isTrue(enableDeleteByPrimaryKey));
        }

        if (stringHasValue(enableDeleteByExample)) {
            tc.setDeleteByExampleStatementEnabled(
                    isTrue(enableDeleteByExample));
        }

        if (stringHasValue(enableCountByExample)) {
            tc.setCountByExampleStatementEnabled(
                    isTrue(enableCountByExample));
        }

        if (stringHasValue(enableUpdateByExample)) {
            tc.setUpdateByExampleStatementEnabled(
                    isTrue(enableUpdateByExample));
        }

        if (stringHasValue(selectByPrimaryKeyQueryId)) {
            tc.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
        }

        if (stringHasValue(selectByExampleQueryId)) {
            tc.setSelectByExampleQueryId(selectByExampleQueryId);
        }

        if (stringHasValue(modelType)) {
            tc.setConfiguredModelType(modelType);
        }

        if (stringHasValue(escapeWildcards)) {
            tc.setWildcardEscapingEnabled(isTrue(escapeWildcards));
        }

        if (stringHasValue(delimitIdentifiers)) {
            tc.setDelimitIdentifiers(isTrue(delimitIdentifiers));
        }

        if (stringHasValue(delimitAllColumns)) {
            tc.setAllColumnDelimitingEnabled(isTrue(delimitAllColumns));
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(tc, childNode);
            } else if ("columnOverride".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseColumnOverride(tc, childNode);
            } else if ("ignoreColumn".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseIgnoreColumn(tc, childNode);
            } else if ("generatedKey".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseGeneratedKey(tc, childNode);
            } else if ("columnRenamingRule".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseColumnRenamingRule(tc, childNode);
            }
        }
    }

    private void parseColumnOverride(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column"); //$NON-NLS-1$
        String property = attributes.getProperty("property"); //$NON-NLS-1$
        String javaType = attributes.getProperty("javaType"); //$NON-NLS-1$
        String jdbcType = attributes.getProperty("jdbcType"); //$NON-NLS-1$
        String typeHandler = attributes.getProperty("typeHandler"); //$NON-NLS-1$
        String delimitedColumnName = attributes
                .getProperty("delimitedColumnName"); //$NON-NLS-1$

        ColumnOverride co = new ColumnOverride(column);

        if (stringHasValue(property)) {
            co.setJavaProperty(property);
        }

        if (stringHasValue(javaType)) {
            co.setJavaType(javaType);
        }

        if (stringHasValue(jdbcType)) {
            co.setJdbcType(jdbcType);
        }

        if (stringHasValue(typeHandler)) {
            co.setTypeHandler(typeHandler);
        }

        if (stringHasValue(delimitedColumnName)) {
            co.setColumnNameDelimited(isTrue(delimitedColumnName));
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(co, childNode);
            }
        }

        tc.addColumnOverride(co);
    }

    private void parseGeneratedKey(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);

        String column = attributes.getProperty("column"); //$NON-NLS-1$
        boolean identity = isTrue(attributes
                .getProperty("identity")); //$NON-NLS-1$
        String sqlStatement = attributes.getProperty("sqlStatement"); //$NON-NLS-1$
        String type = attributes.getProperty("type"); //$NON-NLS-1$

        GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);

        tc.setGeneratedKey(gk);
    }

    private void parseIgnoreColumn(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column"); //$NON-NLS-1$
        String delimitedColumnName = attributes
                .getProperty("delimitedColumnName"); //$NON-NLS-1$

        IgnoredColumn ic = new IgnoredColumn(column);

        if (stringHasValue(delimitedColumnName)) {
            ic.setColumnNameDelimited(isTrue(delimitedColumnName));
        }

        tc.addIgnoredColumn(ic);
    }

    private void parseColumnRenamingRule(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String searchString = attributes.getProperty("searchString"); //$NON-NLS-1$
        String replaceString = attributes.getProperty("replaceString"); //$NON-NLS-1$

        ColumnRenamingRule crr = new ColumnRenamingRule();

        crr.setSearchString(searchString);

        if (stringHasValue(replaceString)) {
            crr.setReplaceString(replaceString);
        }

        tc.setColumnRenamingRule(crr);
    }

    private void parseJavaTypeResolver(Context context, Node node) {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();

        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); //$NON-NLS-1$

        if (stringHasValue(type)) {
            javaTypeResolverConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(javaTypeResolverConfiguration, childNode);
            }
        }
    }

    private void parsePlugin(Context context, Node node) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();

        context.addPluginConfiguration(pluginConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); //$NON-NLS-1$

        pluginConfiguration.setConfigurationType(type);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(pluginConfiguration, childNode);
            }
        }
    }

    private void parseJavaModelGenerator(Context context, Node node) {
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();

        context
                .setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage"); //$NON-NLS-1$
        String targetProject = attributes.getProperty("targetProject"); //$NON-NLS-1$

        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(javaModelGeneratorConfiguration, childNode);
            }
        }
    }

    private void parseJavaClientGenerator(Context context, Node node) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();

        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); //$NON-NLS-1$
        String targetPackage = attributes.getProperty("targetPackage"); //$NON-NLS-1$
        String targetProject = attributes.getProperty("targetProject"); //$NON-NLS-1$
        String implementationPackage = attributes
                .getProperty("implementationPackage"); //$NON-NLS-1$
        String interfaceExtendSupInterface = attributes.getProperty("interfaceExtendSupInterface");
        String mapperScanAnnotation = attributes.getProperty("mapperScanAnnotation");
        
        javaClientGeneratorConfiguration.setConfigurationType(type);
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);
        javaClientGeneratorConfiguration
                .setImplementationPackage(implementationPackage);
        
        javaClientGeneratorConfiguration.setInterfaceExtendSupInterface(interfaceExtendSupInterface);
        javaClientGeneratorConfiguration.setMapperScanAnnotation(mapperScanAnnotation);
        
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
			Properties attr = parseAttributes(childNode);
			if ("interfaceExtendSupInterface".equals(attr.getProperty("name"))) {
				javaClientGeneratorConfiguration.setInterfaceExtendSupInterface(attr.getProperty("value"));
			} else if ("mapperScanAnnotation".equals(attr.getProperty("name"))) { 
				javaClientGeneratorConfiguration.setMapperScanAnnotation(attr.getProperty("value"));
			} else {
				parseProperty(javaClientGeneratorConfiguration, childNode);
			}
        }
    }

    private void parseJdbcConnection(Context context, Node node) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        Properties attributes = parseAttributes(node);
        String driverClass = attributes.getProperty("driverClass"); //$NON-NLS-1$
        String connectionURL = attributes.getProperty("connectionURL"); //$NON-NLS-1$
        String userId = attributes.getProperty("userId"); //$NON-NLS-1$
        String password = attributes.getProperty("password"); //$NON-NLS-1$

        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(connectionURL);

        if (stringHasValue(userId)) {
            jdbcConnectionConfiguration.setUserId(userId);
        }

        if (stringHasValue(password)) {
            jdbcConnectionConfiguration.setPassword(password);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(jdbcConnectionConfiguration, childNode);
            }
        }
    }

    private void parseClassPathEntry(Configuration configuration, Node node) {
        Properties attributes = parseAttributes(node);

        configuration.addClasspathEntry(attributes.getProperty("location")); //$NON-NLS-1$
    }

    private void parseProperty(PropertyHolder propertyHolder, Node node) {
        Properties attributes = parseAttributes(node);

        String name = attributes.getProperty("name"); //$NON-NLS-1$
        String value = attributes.getProperty("value"); //$NON-NLS-1$
       
        propertyHolder.addProperty(name, value);
    }

    private Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node attribute = nnm.item(i);
            String value = parsePropertyTokens(attribute.getNodeValue());
            attributes.put(attribute.getNodeName(), value);
        }

        return attributes;
    }

    private String parsePropertyTokens(String string) {
        final String OPEN = "${"; //$NON-NLS-1$
        final String CLOSE = "}"; //$NON-NLS-1$

        String newString = string;
        if (newString != null) {
            int start = newString.indexOf(OPEN);
            int end = newString.indexOf(CLOSE);

            while (start > -1 && end > start) {
                String prepend = newString.substring(0, start);
                String append = newString.substring(end + CLOSE.length());
                String propName = newString.substring(start + OPEN.length(),
                        end);
                String propValue = properties.getProperty(propName);
                if (propValue != null) {
                    newString = prepend + propValue + append;
                }

                start = newString.indexOf(OPEN, end);
                end = newString.indexOf(CLOSE, end);
            }
        }

        return newString;
    }

    private void parseCommentGenerator(Context context, Node node) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); //$NON-NLS-1$

        if (stringHasValue(type)) {
            commentGeneratorConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(commentGeneratorConfiguration, childNode);
            }
        }
    }
    
//===========================================================================
    
	private void parseContext(Configuration configuration, Node node,
			List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout,
			String codeVersion) {
		Properties attributes = parseAttributes(node);
		String defaultModelType = attributes.getProperty("defaultModelType");
		String targetRuntime = attributes.getProperty("targetRuntime");
		String introspectedColumnImpl = attributes
				.getProperty("introspectedColumnImpl");
		String id = attributes.getProperty("id");

		ModelType mt = defaultModelType == null ? null : ModelType
				.getModelType(defaultModelType);

		Context context = new Context(mt);
		context.setId(id);
		
		context.setCodeVersion(codeVersion);
		if (StringUtility.stringHasValue(introspectedColumnImpl)) {
			context.setIntrospectedColumnImpl(introspectedColumnImpl);
		}
		if (StringUtility.stringHasValue(targetRuntime)) {
			context.setTargetRuntime(targetRuntime);
		}
		configuration.addContext(context);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == 1) {
				if ("property".equals(childNode.getNodeName())) {
					parseProperty(context, childNode);
				} else if ("plugin".equals(childNode.getNodeName())) {
					parsePlugin(context, childNode);
				} else if ("commentGenerator".equals(childNode.getNodeName())) {
					parseCommentGenerator(context, childNode);
				} else if ("jdbcConnection".equals(childNode.getNodeName())) {
					parseJdbcConnection(context, childNode);
				} else if ("javaModelGenerator".equals(childNode.getNodeName())) {
					parseJavaModelGenerator(context, childNode);
				} else if ("javaTypeResolver".equals(childNode.getNodeName())) {
					parseJavaTypeResolver(context, childNode);
				} else if ("sqlMapGenerator".equals(childNode.getNodeName())) {
					parseSqlMapGenerator(context, childNode);
				} else if ("javaClientGenerator".equals(childNode.getNodeName())) {
					parseJavaClientGenerator(context, childNode);
				} else if ("javaServiceGenerator".equals(childNode.getNodeName())) {
					if (getSwitch(codeLayout, CodeLayoutEnum.SERVICE_LAYOUT)) {
						parseJavaServiceGenerator(context, childNode);
					}
				} else if ("javaManagerGenerator".equals(childNode.getNodeName())) {
					if (getSwitch(codeLayout, CodeLayoutEnum.MANAGER_LAYOUT)) {
						parseJavaManagerGenerator(context, childNode);
					}
				} else if ("javaControllerGenerator".equals(childNode.getNodeName())) {
					if (getSwitch(codeLayout, CodeLayoutEnum.CONTROLLER_LAYOUT)) {
						parseJavaControllerGenerator(context, childNode);
					}
				} else if ("table".equals(childNode.getNodeName())) {
					parseTable(context, childNode);
				} else if ("tableSetting".equals(childNode.getNodeName())) {
					parseYouGouTable(context, childNode, tableList);
				} else if ("sqlMapConfigFileAppend".equals(childNode
						.getNodeName())) {
					parseYouGouSqlMapConfigConfiguration(context, childNode);
				}
			}
		}
	}
	
	private boolean getSwitch(Map<CodeLayoutEnum, Boolean> codeLayout,
			CodeLayoutEnum key) {
		if ((codeLayout == null) || (codeLayout.get(key) == null)) {
			return false;
		}
		return ((Boolean) codeLayout.get(key)).booleanValue();
	}
	
	public void addTables(Context context,
			YouGouTableSettingConfiguration ygtc, List<String> tables) {
		if ((tables == null) || (tables.size() < 1)) {
			return;
		}
		for (String tableName : tables) {
			TableConfiguration tc = new TableConfiguration(context);
			context.addTableConfiguration(tc);

			String[] tableInfo = tableName.split("\\.");
			if (tableInfo.length < 2) {
				return;
			}
			if (!ygtc.isSchema()) {
				tc.setCatalog(tableInfo[0]);
			}
			if (ygtc.isSchema()) {
				tc.setSchema(tableInfo[0]);
			}
			if (StringUtility.stringHasValue(tableInfo[1])) {
				tc.setTableName(tableInfo[1]);
			}
			tc.setInsertStatementEnabled(ygtc.isInsertStatementEnabled());

			tc.setSelectByPrimaryKeyStatementEnabled(ygtc
					.isSelectByPrimaryKeyStatementEnabled());

			tc.setSelectByExampleStatementEnabled(ygtc
					.isSelectByExampleStatementEnabled());

			tc.setUpdateByPrimaryKeyStatementEnabled(ygtc
					.isUpdateByPrimaryKeyStatementEnabled());

			tc.setDeleteByPrimaryKeyStatementEnabled(ygtc
					.isDeleteByPrimaryKeyStatementEnabled());

			tc.setDeleteByExampleStatementEnabled(ygtc
					.isDeleteByExampleStatementEnabled());

			tc.setCountByExampleStatementEnabled(ygtc
					.isCountByExampleStatementEnabled());

			tc.setUpdateByExampleStatementEnabled(ygtc
					.isUpdateByExampleStatementEnabled());

			tc.setDelimitIdentifiers(ygtc.isDelimitIdentifiers());

			tc.setAllColumnDelimitingEnabled(ygtc
					.isAllColumnDelimitingEnabled());
		}
	}
	
	private void parseYouGouTable(Context context, Node node,
			List<String> tableList) {
		YouGouTableSettingConfiguration tc = new YouGouTableSettingConfiguration(
				context);
		context.setYouGouTableSettingConfiguration(tc);

		Properties attributes = parseAttributes(node);
		String isSchema = attributes.getProperty("isSchema");
		String alias = attributes.getProperty("alias");
		String enableInsert = attributes.getProperty("enableInsert");
		String enableSelectByPrimaryKey = attributes
				.getProperty("enableSelectByPrimaryKey");
		String enableSelectByExample = attributes
				.getProperty("enableSelectByExample");
		String enableUpdateByPrimaryKey = attributes
				.getProperty("enableUpdateByPrimaryKey");
		String enableDeleteByPrimaryKey = attributes
				.getProperty("enableDeleteByPrimaryKey");
		String enableDeleteByExample = attributes
				.getProperty("enableDeleteByExample");
		String enableCountByExample = attributes
				.getProperty("enableCountByExample");
		String enableUpdateByExample = attributes
				.getProperty("enableUpdateByExample");
		String selectByPrimaryKeyQueryId = attributes
				.getProperty("selectByPrimaryKeyQueryId");
		String selectByExampleQueryId = attributes
				.getProperty("selectByExampleQueryId");
		String modelType = attributes.getProperty("modelType");
		String escapeWildcards = attributes.getProperty("escapeWildcards");
		String delimitIdentifiers = attributes
				.getProperty("delimitIdentifiers");
		String delimitAllColumns = attributes.getProperty("delimitAllColumns");

		HashMap<String, String> prefixMap = new HashMap<String, String>();
		boolean ignoreGeneratorSchema = false;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if ("prop".equals(childNode.getNodeName())) {
				Properties a = parseAttributes(childNode);
				if ("replaceTablePrefix".equals(a.getProperty("name"))) {
					NodeList childNodeList = childNode.getChildNodes();
					for (int j = 0; j < childNodeList.getLength(); j++) {
						Node listNode = childNodeList.item(i);
						if ("list".equals(listNode.getNodeName())) {
							NodeList prefixNodeList = listNode.getChildNodes();
							for (int k = 0; k < prefixNodeList.getLength(); k++) {
								Node prefixNode = prefixNodeList.item(k);
								if ("prefix".equals(prefixNode.getNodeName())) {
									Properties attributes1 = parseAttributes(prefixNode);
									prefixMap.put(
											attributes1.getProperty("name"),
											attributes1.getProperty("value"));
								}
							}
						}
					}
				} else if ("ignoreGeneratorSchema"
						.equals(a.getProperty("name"))) {
					ignoreGeneratorSchema = new Boolean(a.getProperty("value"))
							.booleanValue();
				}
			}
		}
		tc.setReplaceTablePrefixMap(prefixMap);
		tc.setIgnoreGeneratorSchema(ignoreGeneratorSchema);
		if (!new Boolean(isSchema).booleanValue()) {
			tc.setSchema(false);
		}
		if (new Boolean(isSchema).booleanValue()) {
			tc.setSchema(true);
		}
		if (StringUtility.stringHasValue(alias)) {
			tc.setAlias(alias);
		}
		if (StringUtility.stringHasValue(enableInsert)) {
			tc.setInsertStatementEnabled(StringUtility.isTrue(enableInsert));
		}
		if (StringUtility.stringHasValue(enableSelectByPrimaryKey)) {
			tc.setSelectByPrimaryKeyStatementEnabled(StringUtility
					.isTrue(enableSelectByPrimaryKey));
		}
		if (StringUtility.stringHasValue(enableSelectByExample)) {
			tc.setSelectByExampleStatementEnabled(StringUtility
					.isTrue(enableSelectByExample));
		}
		if (StringUtility.stringHasValue(enableUpdateByPrimaryKey)) {
			tc.setUpdateByPrimaryKeyStatementEnabled(StringUtility
					.isTrue(enableUpdateByPrimaryKey));
		}
		if (StringUtility.stringHasValue(enableDeleteByPrimaryKey)) {
			tc.setDeleteByPrimaryKeyStatementEnabled(StringUtility
					.isTrue(enableDeleteByPrimaryKey));
		}
		if (StringUtility.stringHasValue(enableDeleteByExample)) {
			tc.setDeleteByExampleStatementEnabled(StringUtility
					.isTrue(enableDeleteByExample));
		}
		if (StringUtility.stringHasValue(enableCountByExample)) {
			tc.setCountByExampleStatementEnabled(StringUtility
					.isTrue(enableCountByExample));
		}
		if (StringUtility.stringHasValue(enableUpdateByExample)) {
			tc.setUpdateByExampleStatementEnabled(StringUtility
					.isTrue(enableUpdateByExample));
		}
		if (StringUtility.stringHasValue(selectByPrimaryKeyQueryId)) {
			tc.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
		}
		if (StringUtility.stringHasValue(selectByExampleQueryId)) {
			tc.setSelectByExampleQueryId(selectByExampleQueryId);
		}
		if (StringUtility.stringHasValue(modelType)) {
			tc.setConfiguredModelType(modelType);
		}
		if (StringUtility.stringHasValue(escapeWildcards)) {
			tc.setWildcardEscapingEnabled(StringUtility.isTrue(escapeWildcards));
		}
		if (StringUtility.stringHasValue(delimitIdentifiers)) {
			tc.setDelimitIdentifiers(StringUtility.isTrue(delimitIdentifiers));
		}
		if (StringUtility.stringHasValue(delimitAllColumns)) {
			tc.setAllColumnDelimitingEnabled(StringUtility
					.isTrue(delimitAllColumns));
		}
		addTables(context, tc, tableList);
	}
	
	private void parseJavaServiceGenerator(Context context, Node node) {
		YouGouServiceGeneratorConfiguration javaYouGouServiceGeneratorConfiguration = new YouGouServiceGeneratorConfiguration();

		context.setYouGouServiceGeneratorConfiguration(javaYouGouServiceGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String targetPackage = attributes.getProperty("targetPackage");
		String targetProject = attributes.getProperty("targetProject");
		String implementationPackage = attributes
				.getProperty("implementationPackage");

		String interfaceExtendSupInterface = attributes
				.getProperty("interfaceExtendSupInterface");
		String enableInterfaceSupInterfaceGenericity = attributes
				.getProperty("enableInterfaceSupInterfaceGenericity");
		enableInterfaceSupInterfaceGenericity = (interfaceExtendSupInterface == null)
				|| ("".equals(interfaceExtendSupInterface))
				|| (enableInterfaceSupInterfaceGenericity == null)
				|| ("".equals(enableInterfaceSupInterfaceGenericity)) ? "false"
				: enableInterfaceSupInterfaceGenericity;
		javaYouGouServiceGeneratorConfiguration
				.setInterfaceExtendSupInterface(interfaceExtendSupInterface);
		javaYouGouServiceGeneratorConfiguration
				.setEnableInterfaceSupInterfaceGenericity(enableInterfaceSupInterfaceGenericity);

		String extendSupClass = attributes.getProperty("extendSupClass");
		String enableSupClassGenericity = attributes
				.getProperty("enableSupClassGenericity");
		enableSupClassGenericity = (extendSupClass == null)
				|| ("".equals(extendSupClass))
				|| (enableSupClassGenericity == null)
				|| ("".equals(enableSupClassGenericity)) ? "false"
				: enableSupClassGenericity;

		javaYouGouServiceGeneratorConfiguration
				.setExtendSupClass(extendSupClass);
		javaYouGouServiceGeneratorConfiguration
				.setEnableSupClassGenericity(enableSupClassGenericity);
		javaYouGouServiceGeneratorConfiguration.setTargetPackage(targetPackage);
		javaYouGouServiceGeneratorConfiguration.setTargetProject(targetProject);
		javaYouGouServiceGeneratorConfiguration
				.setImplementationPackage(implementationPackage);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == 1) {
				if ("property".equals(childNode.getNodeName())) {
					parseProperty(javaYouGouServiceGeneratorConfiguration,
							childNode);
				}
			}
		}
	}

	private void parseJavaManagerGenerator(Context context, Node node) {
		YouGouManagerGeneratorConfiguration javaYouGouManagerGeneratorConfiguration = new YouGouManagerGeneratorConfiguration();

		context.setYouGouManagerGeneratorConfiguration(javaYouGouManagerGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String targetPackage = attributes.getProperty("targetPackage");
		String targetProject = attributes.getProperty("targetProject");
		String implementationPackage = attributes
				.getProperty("implementationPackage");

		String interfaceExtendSupInterface = attributes
				.getProperty("interfaceExtendSupInterface");
		String enableInterfaceSupInterfaceGenericity = attributes
				.getProperty("enableInterfaceSupInterfaceGenericity");
		enableInterfaceSupInterfaceGenericity = (interfaceExtendSupInterface == null)
				|| ("".equals(interfaceExtendSupInterface))
				|| (enableInterfaceSupInterfaceGenericity == null)
				|| ("".equals(enableInterfaceSupInterfaceGenericity)) ? "false"
				: enableInterfaceSupInterfaceGenericity;
		javaYouGouManagerGeneratorConfiguration
				.setInterfaceExtendSupInterface(interfaceExtendSupInterface);
		javaYouGouManagerGeneratorConfiguration
				.setEnableInterfaceSupInterfaceGenericity(enableInterfaceSupInterfaceGenericity);

		String extendSupClass = attributes.getProperty("extendSupClass");
		String enableSupClassGenericity = attributes
				.getProperty("enableSupClassGenericity");
		javaYouGouManagerGeneratorConfiguration
				.setExtendSupClass(extendSupClass);
		enableSupClassGenericity = (extendSupClass == null)
				|| ("".equals(extendSupClass))
				|| (enableSupClassGenericity == null)
				|| ("".equals(enableSupClassGenericity)) ? "false"
				: enableSupClassGenericity;
		javaYouGouManagerGeneratorConfiguration
				.setEnableSupClassGenericity(enableSupClassGenericity);
		javaYouGouManagerGeneratorConfiguration.setTargetPackage(targetPackage);
		javaYouGouManagerGeneratorConfiguration.setTargetProject(targetProject);
		javaYouGouManagerGeneratorConfiguration
				.setImplementationPackage(implementationPackage);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == 1) {
				if ("property".equals(childNode.getNodeName())) {
					parseProperty(javaYouGouManagerGeneratorConfiguration,
							childNode);
				}
			}
		}
	}

	private void parseJavaControllerGenerator(Context context, Node node) {
		YouGouControllerGeneratorConfiguration javaYouGouControllerGeneratorConfiguration = new YouGouControllerGeneratorConfiguration();

		context.setYouGouControllerGeneratorConfiguration(javaYouGouControllerGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String targetPackage = attributes.getProperty("targetPackage");
		String targetProject = attributes.getProperty("targetProject");
		String implementationPackage = attributes
				.getProperty("implementationPackage");

		String interfaceExtendSupInterface = attributes
				.getProperty("interfaceExtendSupInterface");
		String enableInterfaceSupInterfaceGenericity = attributes
				.getProperty("enableInterfaceSupInterfaceGenericity");
		enableInterfaceSupInterfaceGenericity = (interfaceExtendSupInterface == null)
				|| ("".equals(interfaceExtendSupInterface))
				|| (enableInterfaceSupInterfaceGenericity == null)
				|| ("".equals(enableInterfaceSupInterfaceGenericity)) ? "false"
				: enableInterfaceSupInterfaceGenericity;
		javaYouGouControllerGeneratorConfiguration
				.setInterFaceExtendSupInterface(interfaceExtendSupInterface);
		javaYouGouControllerGeneratorConfiguration
				.setEnableInterfaceSupInterfaceGenericity(enableInterfaceSupInterfaceGenericity);

		String extendSupClass = attributes.getProperty("extendSupClass");
		String enableSupClassGenericity = attributes
				.getProperty("enableSupClassGenericity");
		javaYouGouControllerGeneratorConfiguration
				.setExtendSupClass(extendSupClass);
		enableSupClassGenericity = (extendSupClass == null)
				|| ("".equals(extendSupClass))
				|| (enableSupClassGenericity == null)
				|| ("".equals(enableSupClassGenericity)) ? "false"
				: enableSupClassGenericity;
		javaYouGouControllerGeneratorConfiguration
				.setEnableSupClassGenericity(enableSupClassGenericity);
		javaYouGouControllerGeneratorConfiguration
				.setTargetPackage(targetPackage);
		javaYouGouControllerGeneratorConfiguration
				.setTargetProject(targetProject);
		javaYouGouControllerGeneratorConfiguration
				.setImplementationPackage(implementationPackage);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == 1) {
				if ("property".equals(childNode.getNodeName())) {
					parseProperty(javaYouGouControllerGeneratorConfiguration,
							childNode);
				}
			}
		}
	}

	private void parseYouGouSqlMapConfigConfiguration(Context context, Node node) {
		YouGouSqlMapConfigConfiguration youGouSqlMapConfigConfiguration = new YouGouSqlMapConfigConfiguration();

		Context.setYouGouSqlMapConfigConfiguration(youGouSqlMapConfigConfiguration);

		Properties attributes = parseAttributes(node);
		String targetPackage = attributes.getProperty("targetPackage");
		String targetProject = attributes.getProperty("targetProject");
		String confileFileName = attributes.getProperty("confileFileName");
		String confileFilePackagePath = attributes
				.getProperty("confileFilePackagePath");
		confileFilePackagePath = confileFilePackagePath == null ? ""
				: confileFilePackagePath;

		youGouSqlMapConfigConfiguration.setTargetPackage(targetPackage);
		youGouSqlMapConfigConfiguration.setTargetProject(targetProject);
		youGouSqlMapConfigConfiguration.setConfileFileName(confileFileName);
		youGouSqlMapConfigConfiguration
				.setConfileFilePackagePath(confileFilePackagePath);
	}
}
