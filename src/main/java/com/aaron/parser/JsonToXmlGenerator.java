package com.aaron.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

public class JsonToXmlGenerator {
	
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
		return output.toString();
	}

}
