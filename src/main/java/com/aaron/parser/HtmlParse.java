package com.aaron.parser;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlParse {
	private static Logger logger = LoggerFactory.getLogger(HtmlParse.class);
	
	
	public static Document getDocument(String str) {
		if(str.startsWith("http")) {
			try {
				return Jsoup.connect(str).get();
			} catch (IOException e) {
				logger.error(str + " visit error");
				e.printStackTrace();
			}
		}
		return Jsoup.parse(str);
	}
	
	public static void main(String[] args) {
		Document doc = HtmlParse.getDocument("https://analytics.zhihuiya.com/");
		Elements scripts = doc.getElementsByTag("script");
		Element element = scripts.get(1);
		String value  = element.data();
		System.out.println(value);
		System.out.println("-------------------");
		Document doc3 = HtmlParse.getDocument("<html><body><div>test</div></body></html>");
		Elements scripts3 = doc3.getElementsByTag("div");
		Element element3 = scripts3.get(0);
		String value3  = element3.text();
		System.out.println(value3);
	}

}
