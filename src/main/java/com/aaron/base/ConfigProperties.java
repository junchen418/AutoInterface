package com.aaron.base;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;

/**
 * 读取配置文件类，config.properties文件是必须的，默认加载该文件
 * 
 * @author shenbing
 * 
 */
public class ConfigProperties {

	// config.properties文件内容
	private static HashMap<String, String> map = new HashMap<String, String>();

	private static ConfigProperties configProperties = null;

	private ConfigProperties() {
		loadFile("config.properties");
	}

	/**
	 * 返回ConfigProperties对象
	 * 
	 * @return
	 */
	public synchronized static ConfigProperties getInstance() {
		if (configProperties == null) {
			configProperties = new ConfigProperties();
		}
		return configProperties;

	}

	/**
	 * 导入文件
	 * 
	 * @param properties
	 *            文件名
	 */
	private void loadFile(String properties) {
		// String filePath = System.getProperty("user.dir") +
		// "/src/main/resources/" + properties;
		InputStream in = ConfigProperties.class.getClassLoader().getResourceAsStream(properties);
		InputStreamReader ir = null;
		LineNumberReader input = null;
		try {
			in = new BufferedInputStream(in);
			ir = new InputStreamReader(in);
			input = new LineNumberReader(ir);
			String line;
			line = input.readLine();
			while (line != null) {
				parceLine(line);
				line = input.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ir != null) {
					ir.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void parceLine(String line) {
		String str = line;
		String tmp = str.trim();
		if (tmp.startsWith("#") || tmp.isEmpty()) {
			return;
		}
		String[] value = tmp.split("=", 2);
		map.put(value[0].trim(), value[1].trim());

	}

	public String getString(String key) {
		return map.get(key);
	}

	public int getInt(String key) {
		return Integer.valueOf(map.get(key));
	}

	public static void main(String[] args) {
		System.out.println(ConfigProperties.getInstance().getString("urlEncode"));
	}
}
