package com.aaron.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MD5加密通用类
 * 
 * @author Aaron
 */
public class MD5Util {

	private static Logger logger = LoggerFactory.getLogger(MD5Util.class);

	/**
	 * 
	 * @param string
	 *            需要MD5加密字符串
	 * @return 返回经过MD5加密后的值
	 */
	public static String MD5(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(string.getBytes("utf-8"));
			return bytesToHex(bytes);
		} catch (Exception e) {
			logger.error(string + ":MD5编码失败");
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

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		System.out.println(MD5Util.MD5("clientIdopentesteTime1524476511209phoneTypeios"));
	}
}
