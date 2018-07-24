package com.aaron.base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.aaron.utils.HttpService;
import com.aaron.assertion.IAssert;

@SuppressWarnings("unused")
public class HttpBaseCase {
	public HttpService httpService;

	@BeforeClass
	public void before() {
		httpService = new HttpService(Boolean.parseBoolean(ConfigProperties.getInstance().getString("urlEncode")));
	}

	@AfterClass
	public void after() {
		httpService = null;
	}

	public static String getXmlPath(String filePath) {
		String xmlPath = null;
		if (filePath.startsWith("/")) {
			xmlPath = System.getProperty("user.dir") + "/src/test/java" + filePath;
		} else {
			xmlPath = System.getProperty("user.dir") + "/src/test/java/" + filePath;
		}
		return xmlPath;
	}
	
	public static void sleep(long time) {
		try {
			Thread.currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
