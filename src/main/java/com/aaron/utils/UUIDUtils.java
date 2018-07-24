package com.aaron.utils;

import java.util.UUID;

public class UUIDUtils {

	// 返回uuid生成的随机值
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		return uuid;
	}
}