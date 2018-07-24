package com.aaron.testcase;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import com.aaron.base.ConfigProperties;
import com.aaron.base.HttpBaseCase;
import com.aaron.db.DbUnit;
import com.aaron.encrypt.Base64Util;
import com.aaron.encrypt.MD5Util;
import com.aaron.utils.HttpService;
import com.aaron.utils.SocketClientService;
import com.zf.zson.ZSON;
import com.zf.zson.result.ZsonResult;

public class TestLogin extends HttpBaseCase {

	public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
		// TestNG testNG = new TestNG();
		// testNG.setTestClasses(new Class[] { TestHttpClient.class });
		// testNG.run();
		// DbUnit dbProxoolUnit1 = new DbUnit("db.properties");
		//
		// String result1 = dbProxoolUnit1.getValue("SELECT * FROM resource", 0,
		// 1);
		//
		// ArrayList<String> list = new ArrayList<>();
		// list.add("285643e5e88d411e950e993b960db506");
		// list.add("res_resource");
		// list.add("is null");
		//
		// dbProxoolUnit1.queryOfList(
		// "SELECT * FROM resource WHERE resource_id =
		// 285643e5e88d411e950e993b960db506 AND source_type = 'res_resource' AND
		// resource_size= 10240");
		// System.out.println(Base64Util.encode("中国人".getBytes()));
		// System.out.println(new String(Base64Util.decode("5Lit5Zu9")));
		// System.out.println(MD5("/reserver/open/video/a8d41b6a65434359beebbfe7efe23fa2clientIdtesteTime1524812393163phoneTypeios123456"));
		// System.out.println(Long.valueOf("1524471614000").longValue());
		// System.out.println(System.currentTimeMillis());
		String url  = "https://openapi.9itest.com/v1/class/create/oauth2/token";
		HttpService class_create = new HttpService(false) ;
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("client_id", "3AADF94D0B60446AA88ED0B83A02878F");
		map.put("client_secret", "AC59963740394AB59AE6A093CEB9DBFC");
		map.put("grant_type", "client_credentials");
		

		class_create.executePost(url, map);

	}

	private static String MD5(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(string.getBytes("utf-8"));
			return bytesToHex(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static String bytesToHex(byte[] bytes) {
		final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			stringBuilder.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			stringBuilder.append(HEX_DIGITS[(bytes[i]) & 0x0f]);
		}
		return stringBuilder.toString();
	}

}