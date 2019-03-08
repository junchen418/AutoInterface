package com.aaron.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlCommon {

	public static Logger logger = LoggerFactory.getLogger(XmlCommon.class);
	public String fileName;

	public XmlCommon(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 读取xml信息
	 * 
	 * @param beanName bean标签的beanName值
	 * @param tag      bean下子标签
	 * @param document xml文档
	 * @return
	 */
	private HashMap<String, String> getXml2Map(String beanName, String tag, Document document) {
		HashMap<String, String> paramterMap = new HashMap<String, String>();
		for (Iterator<?> iter = document.getRootElement().elementIterator(); iter.hasNext();) {
			Element beanElement = (Element) iter.next();
			if (beanElement.attributeValue("beanName").equalsIgnoreCase(beanName)) {
				for (Iterator<?> iter1 = beanElement.elementIterator(); iter1.hasNext();) {
					Element paramtersElement = (Element) iter1.next();
					for (Iterator<?> iter2 = paramtersElement.elementIterator(tag); iter2.hasNext();) {
						Element paramterElement = (Element) iter2.next();
						String elementName = paramterElement.attributeValue("name").toString();
						String elementValue = paramterElement.attributeValue("value").toString();
						paramterMap.put(elementName, elementValue);
					}
				}
				return paramterMap;
			}
		}
		return paramterMap;
	}

	/**
	 * 读取xml信息
	 * 
	 * @param beanName bean标签的beanName值
	 * @param tag      bean下子标签
	 * @param document xml文档
	 * @return
	 */
	private Collection<Map<String, String>> getXml2Collection(String beanName, String tag, Document document) {
		Collection<Map<String, String>> collection = new LinkedList<Map<String, String>>();
		for (Iterator<?> iter = document.getRootElement().elementIterator(); iter.hasNext();) {
			Element beanElement = (Element) iter.next();
			if (beanElement.attributeValue("beanName").equalsIgnoreCase(beanName)) {
				for (Iterator<?> iter1 = beanElement.elementIterator(); iter1.hasNext();) {
					Element paramtersElement = (Element) iter1.next();
					for (Iterator<?> iter2 = paramtersElement.elementIterator(tag); iter2.hasNext();) {
						HashMap<String, String> tempMap = new HashMap<String, String>();
						Element paramterElement = (Element) iter2.next();
						String elementName = paramterElement.attributeValue("name").toString();
						String elementValue = paramterElement.attributeValue("value").toString();
						tempMap.put(elementName, elementValue);
						collection.add(tempMap);
					}
				}
				return collection;
			}
		}
		return collection;
	}

	/**
	 * 读取标签value值
	 * 
	 * @param beanName bean标签的beanName值
	 * @param tag      bean下子标签
	 * @param document xml文档
	 * @return
	 */
	private String getTagValue(String beanName, String tag, Document document) {
		String valueString = "";
		for (Iterator<?> iter = document.getRootElement().elementIterator(); iter.hasNext();) {
			Element beanElement = (Element) iter.next();
			if (beanElement.attributeValue("beanName").equalsIgnoreCase(beanName)) {
				for (Iterator<?> iter1 = beanElement.elementIterator(); iter1.hasNext();) {
					Element paramtersElement = (Element) iter1.next();
					for (Iterator<?> iter2 = paramtersElement.elementIterator(tag); iter2.hasNext();) {
						Element paramterElement = (Element) iter2.next();
						valueString = paramterElement.attributeValue("value").toString();
						if (valueString != null) {
							break;
						}
					}
				}
				return valueString;
			}
		}
		return valueString;
	}

	/**
	 * 返回xml文件的Document实例
	 * 
	 * @param filename
	 * @return
	 */
	private Document getDocument(String filename) {
		File file = new File(fileName);
		Document document = null;
		try {
			if (!file.exists()) {
				logger.error(filename + " 文件不存在！");
			}
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(file);
		} catch (DocumentException e) {
			logger.error("读取文件异常！");
			try {
				throw e;
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
		}
		return document;
	}

	/**
	 * 适用于有重复参数的请求
	 * 
	 * @param beanNames
	 * @return [[Map,Collection,Collection,Collection,Collection,Map],]
	 *         一维数组元素分别为url字典、pathparam集合、bodyparam集合、head字典、assert字典
	 */
	public Object[][] getParameters(String... beanNames) {
		Object[][] dataObject = new Object[beanNames.length][];
		for (int i = 0; i < beanNames.length; i++) {
			Document document = getDocument(fileName);
			HashMap<String, String> urlMap = getXml2Map(beanNames[i], "url", document);
			Collection<Map<String, String>> pathParamterMap = getXml2Collection(beanNames[i], "pathparam", document);
			Collection<Map<String, String>> bodyParamterMap = getXml2Collection(beanNames[i], "bodyparam", document);
			Collection<Map<String, String>> headParamterMap = getXml2Collection(beanNames[i], "headparam", document);
			HashMap<String, String> assertMap = getXml2Map(beanNames[i], "assert", document);
			dataObject[i] = new Object[] { urlMap, pathParamterMap, bodyParamterMap, headParamterMap, assertMap };

		}
		return dataObject;
	}

	/**
	 * 适用于没有重复参数的请求
	 * 
	 * @param beanNames
	 * @return [[Map,Map,Map,Map,Map],]
	 *         一维数组元素分别为url字典、pathparam字典、bodyparam字典、head字典、assert字典
	 */
	public Object[][] getMapParameters(String... beanNames) {
		Object[][] dataObject = new Object[beanNames.length][];
		for (int i = 0; i < beanNames.length; i++) {
			Document document = getDocument(fileName);
			HashMap<String, String> urlMap = getXml2Map(beanNames[i], "url", document);
			HashMap<String, String> pathParamterMap = getXml2Map(beanNames[i], "pathparam", document);
			HashMap<String, String> bodyParamterMap = getXml2Map(beanNames[i], "bodyparam", document);
			HashMap<String, String> headParamterMap = getXml2Map(beanNames[i], "headparam", document);
			HashMap<String, String> assertMap = getXml2Map(beanNames[i], "assert", document);
			dataObject[i] = new Object[] { urlMap, pathParamterMap, bodyParamterMap, headParamterMap, assertMap };

		}
		return dataObject;
	}

	/**
	 * 用于post请求时，body为String或JSON场景（body内容非Map）
	 * 
	 * @param beanNames
	 * @return [[Map,Map,String,Map,Map],]
	 */
	public Object[][] getParamOfString(String... beanNames) {
		Object[][] dataObject = new Object[beanNames.length][];
		for (int i = 0; i < beanNames.length; i++) {
			Document document = getDocument(fileName);
			HashMap<String, String> urlMap = getXml2Map(beanNames[i], "url", document);
			HashMap<String, String> pathParamterMap = getXml2Map(beanNames[i], "pathparam", document);
			String tagValue = getTagValue(beanNames[i], "bodyparam", document);
			HashMap<String, String> headParamterMap = getXml2Map(beanNames[i], "headparam", document);
			HashMap<String, String> assertMap = getXml2Map(beanNames[i], "assert", document);
			dataObject[i] = new Object[] { urlMap, pathParamterMap, tagValue, headParamterMap, assertMap };
		}
		return dataObject;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String filepath = System.getProperty("user.dir") + "/src/test/java/testparamters/" + "test" + ".xml";
		XmlCommon xmlUtil = new XmlCommon(filepath);
		Object[][] collection = xmlUtil.getParamOfString("socketTest", "socketTest2");
		for (int i = 0; i < collection.length; i++) {
			Object[] item = collection[i];
			for (Object iitem : item) {
				if (iitem instanceof HashMap) {
					for (String key : ((HashMap<String, String>) iitem).keySet()) {
						System.out.println(key + ((HashMap<String, String>) iitem).get(key));
					}
				} else if (iitem instanceof String) {
					System.out.println(iitem);
				}

			}
		}
	}
}
