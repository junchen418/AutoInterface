package com.aaron.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

public class XmlToJsonGenerator {

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

}
