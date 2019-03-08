package com.aaron.util;

import java.util.UUID;

public class UUIDUtil {

	/**
	 * 返回uuid
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		return uuid;
	}
}