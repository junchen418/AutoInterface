package com.aaron.util;

import java.util.Random;

public class RandomUtil {

	public static String randomBase = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String randomNumberBase = "0123456789";

	private final static int HANZI_LENGTH = 20902;

	public static Random random = new Random();

	/**
	 * 随机生成一个汉字
	 * 
	 * @return
	 */
	protected static char getRandomHanZi() {
		Random ran = new Random();
		return (char) (0x4e00 + ran.nextInt(HANZI_LENGTH));
	}

	/**
	 * 随机生成一个字母或者数字
	 * 
	 * @return
	 */
	protected static char getRandomStr() {
		Random ran = new Random();
		return (char) randomBase.charAt(ran.nextInt(62));
	}

	/**
	 * 随机生成一个带有中文的字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomText(int length) {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int randomNum = random.nextInt(2);
			char ch = 0;
			if (randomNum == 0) {
				ch = getRandomHanZi();
			} else if (randomNum == 1) {
				ch = getRandomStr();
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 随机生成一个字符串
	 * 
	 * @param length     返回字符串长度
	 * @param onlyNumber 返回随机数字
	 * @return
	 */
	public static String getRandom(int length, boolean onlyNumber) {
		String base;
		if (onlyNumber) {
			base = randomNumberBase;
		} else {
			base = randomBase;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char chr;
			do {
				int number = random.nextInt(base.length());
				chr = base.charAt(number);
			} while (i == 0 && chr == '0');

			sb.append(chr);
		}
		return sb.toString();
	}

	/**
	 * 生成定长的字符串数组
	 * 
	 * @param arrLength
	 * @param length
	 * @param flag      纯数字标识
	 * @return
	 */
	public static String getRandomArr(int arrLength, int length, boolean flag) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < arrLength; i++) {
			sBuffer.append(getRandom(length, flag)).append(",");
		}
		int leng = sBuffer.toString().length();
		return sBuffer.toString().substring(0, leng - 1);
	}

}
