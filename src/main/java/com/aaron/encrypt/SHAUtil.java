package com.aaron.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class SHAUtil {
	public static byte[] SHA(String source) {
		byte src[] = source.getBytes();
		byte target[] = SHA(src);
		return target;
	}

	public static byte[] SHA(byte source[]) {
		try {
			MessageDigest mdInstance = MessageDigest.getInstance("SHA");
			mdInstance.update(source);
			byte mdValue[] = mdInstance.digest();
			return mdValue;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	// 对字符串进行加密, 返回加密后的16进制的字符串表示
	public static String SHAString(String source) {
		byte md5Bytes[] = SHA(source);
		String result = Hex.encodeHexString(md5Bytes);
		return result;
	}

	public static String SHAString(byte source[]) {
		byte md5Bytes[] = SHA(source);
		String result = Hex.encodeHexString(md5Bytes);
		return result;
	}

	public static byte[] SHA256(byte[] source) {
		try {
			MessageDigest mdInstance = MessageDigest.getInstance("SHA-256");
			mdInstance.update(source);
			byte mdValue[] = mdInstance.digest();
			return mdValue;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] SHA256(String source) {
		byte[] bytes = source.getBytes();
		return SHA256(bytes);
	}

	public static String SHA256String(byte[] source) {
		byte[] bytes = SHA256(source);
		String r = Hex.encodeHexString(bytes);
		return r;
	}

	public static String SHA256String(String string) {
		byte[] bytes = string.getBytes();
		return SHA256String(bytes);
	}

	public static byte[] SHA512(byte[] source) {
		try {
			MessageDigest mdInstance = MessageDigest.getInstance("SHA-512");
			mdInstance.update(source);
			byte mdValue[] = mdInstance.digest();
			return mdValue;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] SHA512(String source) {
		byte[] bytes = source.getBytes();
		return SHA512(bytes);
	}

	public static String SHA512String(byte[] source) {
		byte[] bytes = SHA512(source);
		String r = Hex.encodeHexString(bytes);
		return r;
	}

	public static String SHA512String(String string) {
		byte[] bytes = string.getBytes();
		return SHA512String(bytes);
	}

	public static void main(String[] args) {
		System.out.println(SHAString("test"));
	}

}
