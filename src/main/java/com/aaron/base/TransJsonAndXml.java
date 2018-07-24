package com.aaron.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

public class TransJsonAndXml {
	/**
	 * json字符串转换为xml字符串
	 */
	public static String json2xml(String json, String root) {
		StringReader input = new StringReader(json);
		StringWriter output = new StringWriter();
		JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).repairingNamespaces(true).autoArray(true)
				.prettyPrint(true).virtualRoot(root).build();
		try {
			XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);
			XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
			writer = new PrettyXMLEventWriter(writer);
			writer.add(reader);
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// if (output.toString().length() >= 38) {
		// // remove <?xml version="1.0" encoding="UTF-8"?>
		// return output.toString().substring(39);
		// }
		return output.toString();
	}

	/**
	 * xml字符串转换为json字符串
	 */
	public static String xml2json(String xml, String root) {
		StringReader input = new StringReader(xml);
		StringWriter output = new StringWriter();
		JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).autoArray(true).autoPrimitive(true)
				.prettyPrint(true).virtualRoot(root).build();
		try {
			XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);
			XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);
			writer = new PrettyXMLEventWriter(writer);
			writer.add(reader);
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		String sjson = "{\"total\":7,\"result\":\"success\",\"data\":[{\"classlevelName\":\"初一\",\"finished\":\"Y\",\"id\":\"9ed13baf11f14c499f55ccb6cf02dbea\",\"resourceId\":\"870043bb245045dfb31ffd90c838ec59\",\"resourceName\":\"\",\"serverAddress\":\"http://reserver.9itest.com:8081\",\"subjectName\":\"语文测试2\",\"thumb\":\"http://reserver.9itest.com:8081/images/thumbPicDefault.jpg\",\"type\":\"ZD\",\"userName\":\"姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗老师\",\"videoId\":\"\",\"videoType\":\"\",\"watchTime\":0},{\"classlevelName\":\"初一\",\"finished\":\"N\",\"id\":\"30512e5f90294e468bc4864c1c4d0220\",\"resourceId\":\"8f8ba9e8713e48e7a1d9c1ea0e917fbc\",\"resourceName\":\"\",\"serverAddress\":\"http://reserver.9itest.com:8081\",\"subjectName\":\"语文测试2\",\"thumb\":\"http://reserver.9itest.com:8081/images/3ef365e1de32437b867e58f58162d7d1_5a1bcf48c52146879ebf159bc75ea525.small.jpg\",\"type\":\"ZD\",\"userName\":\"姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗姗老师\",\"videoId\":\"8f8ba9e8713e48e7a1d9c1ea0e917fbc\",\"videoType\":\"MOVIE\",\"watchTime\":16}],\"msg\":\"执行操作成功!\"}";
		System.out.println(json2xml(sjson, "json"));
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(xml2json(json2xml(sjson, "json"), "json"));
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		File file = new File("D:/test.xml");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(json2xml(sjson, "json").getBytes("UTF-8"));
		fileOutputStream.flush();
		fileOutputStream.close();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[1024];
		StringBuilder stringBuilder = new StringBuilder();
		int len;
		while ((len = bufferedInputStream.read(bytes)) != -1) {
			stringBuilder.append(new String(bytes, 0, len, "UTF-8"));
		}
		bufferedInputStream.close();
		System.out.println(xml2json(stringBuilder.toString(), "json"));
		System.out.println("#####################################################################################");
		System.out.println(0x80 | 126);
	}
}
