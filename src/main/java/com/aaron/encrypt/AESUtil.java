package com.aaron.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 * 
 * @author Aaron
 *
 */
public class AESUtil {

	public static final String bm = "UTF-8";

	/**
	 * 加密
	 * 
	 * @param dataPassword
	 *            密钥
	 * @param VIPARA
	 *            算法模式非非ECB模式，设置密钥偏移量
	 * @param cleartext
	 *            原始数据
	 * @return 加密后内容
	 */
	public static String encrypt(String dataPassword, String VIPARA, String cleartext) {

		try {
			IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
			SecretKeySpec key = new SecretKeySpec(dataPassword.getBytes(), "AES");
			Cipher cipher;
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] encryptedData = cipher.doFinal(cleartext.getBytes(bm));
			return Base64Util.encode(encryptedData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 解密
	 * 
	 * @param dataPassword
	 *            密钥
	 * @param VIPARA
	 *            算法模式非非ECB模式，设置密钥偏移量
	 * @param encrypted
	 *            密文
	 * @return 原始数据
	 */
	public static String decrypt(String dataPassword, String VIPARA, String encrypted) {
		try {
			byte[] byteMi = Base64Util.decode(encrypted);
			IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
			SecretKeySpec key = new SecretKeySpec(dataPassword.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte[] decryptedData = cipher.doFinal(byteMi);

			return new String(decryptedData, bm);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(encrypt("CEmGySxZCxa5xZoW", "1114144464453111", "test"));
		System.out.println(decrypt("CEmGySxZCxa5xZoW", "1114144464453111", "HLXMfNpB6NFGWiPVsfQcTA=="));

	}
}
