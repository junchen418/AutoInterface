package com.aaron.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zf.zson.ZSON;

/**
 * json字符串通用类
 * 
 * @author Aaron
 *
 */
public class JsonUtil {

	/**
	 * 检查是否符合规范的json字符串
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonString(String json) {
		boolean result = true;
		try {
			ZSON.parseJson(json);
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 返回排序后的json对象字符串
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static String getSortedObject(JSONObject jsonObject) {
		List<String> fields = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		JSONObject tmp = new JSONObject();
		for (Entry<String, Object> entry : jsonObject.entrySet()) {
			if (entry.getValue() instanceof JSONArray) {
				String array = getSortedArray((JSONArray) entry.getValue());
				tmp.put(entry.getKey(), array);
			} else if (entry.getValue() instanceof JSONObject) {
				String array = getSortedObject((JSONObject) entry.getValue());
				tmp.put(entry.getKey(), array);
			} else {
				tmp.put(entry.getKey(), entry.getValue());
			}
			fields.add(entry.getKey());
		}
		Collections.sort(fields);
		sb.append("{");
		for (int i = 0; i < fields.size(); i++) {
			sb.append("\"" + fields.get(i) + "\"");
			sb.append(":");
			if (tmp.get(fields.get(i)) instanceof String) {
				if (isJsonObject(tmp.getString(fields.get(i))) || isJsonArray(tmp.getString(fields.get(i)))) {
					sb.append(tmp.get(fields.get(i)));
				} else {
					sb.append("\"" + tmp.get(fields.get(i)) + "\"");
				}
			} else {
				sb.append(tmp.get(fields.get(i)));
			}

			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("}");
		String result = sb.toString();
		return result;
	}

	/**
	 * 返回排序后的json数组字符串
	 * 
	 * @param jsonArray
	 * @return
	 */
	public static String getSortedArray(JSONArray jsonArray) {
		List<String> fields = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			StringBuilder sb = new StringBuilder();
			if (jsonArray.get(i) instanceof JSONObject) {
				sb.append(getSortedObject((JSONObject) jsonArray.get(i)));
			} else if (jsonArray.get(i) instanceof JSONArray) {
				sb.append(getSortedArray((JSONArray) jsonArray.get(i)));
			} else {
				sb.append(jsonArray.get(i));
			}
			fields.add(sb.toString());
		}
		Collections.sort(fields);
		return Arrays.toString(fields.toArray());
	}

	/**
	 * 字符串时一个json数组
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonArray(String json) {
		boolean result = false;
		try {
			Object jsonResult = JSON.parse(json);
			if (jsonResult instanceof JSONArray) {
				result = true;
			}
		} catch (JSONException e) {
		}
		return result;
	}

	/**
	 * 字符串是一个json对象
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonObject(String json) {
		boolean result = false;
		try {
			Object jsonResult = JSON.parse(json);
			if (jsonResult instanceof JSONObject) {
				result = true;
			}
		} catch (JSONException e) {
		}
		return result;
	}

	/**
	 * 获取排序后的json字符串
	 * 
	 * @param json
	 * @return
	 */
	public static String getSortedJson(String json) {
		StringBuilder builder = new StringBuilder();
		Object jsonResult = JSON.parse(json);
		if (jsonResult instanceof JSONObject) {
			builder.append(getSortedObject((JSONObject) jsonResult));
		} else {
			builder.append(getSortedArray((JSONArray) jsonResult));
		}
		return builder.toString();
	}

	/**
	 * 返回json字符串根路径key值
	 * 
	 * @param json
	 * @return
	 */
	public static List<Object> getRootKeys(String json) {
		List<Object> keys = new LinkedList<>();
		if (json.startsWith("[") && json.endsWith("]")) {
			List<JSONObject> jsonResult = (List<JSONObject>) JSON.parseArray(json, JSONObject.class);
			for (JSONObject result : jsonResult) {
				keys.add(getRootKeys(result.toJSONString()));
			}
		} else {
			LinkedHashMap<String, Object> jsonResult = (LinkedHashMap<String, Object>) JSON.parseObject(json,
					new TypeReference<LinkedHashMap<String, Object>>() {
					});
			for (Entry<String, Object> entry : jsonResult.entrySet()) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	/**
	 * 格式化json字符串
	 * 
	 * @param jsonStr json字符串
	 * @return
	 */
	public static String getFormatedJson(String jsonStr) {
		if (null == jsonStr || "".equals(jsonStr))
			return "";
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			switch (current) {
			case '{':
			case '[':
				sb.append(current);
				sb.append('\n');
				indent++;
				addIndentBlank(sb, indent);
				break;
			case '}':
			case ']':
				sb.append('\n');
				indent--;
				addIndentBlank(sb, indent);
				sb.append(current);
				break;
			case ',':
				sb.append(current);
				if (last != '\\') {
					sb.append('\n');
					addIndentBlank(sb, indent);
				}
				break;
			default:
				sb.append(current);
			}
		}

		return sb.toString();
	}

	/**
	 * 添加缩进字符
	 * 
	 * @param sb     添加缩进字符字符串
	 * @param indent 天剑缩进字符量，每一个缩进字符为四个空格符
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append("    ");
		}
	}

	public static void main(String[] args) throws Exception {
		String pre = "[{\r\n" + "    \"data\": {\r\n" + "      \"pagination\": {\r\n" + "        \"total\": 93,\r\n"
				+ "        \"page\": 1,\r\n" + "        \"rows\": 20\r\n" + "      },\r\n" + "	  \"cnLink\":\"\",\r\n"
				+ "      \"link\": null,\r\n" + "      \"fileWrapperStatus\": 2,\r\n" + "	  \"list\": [\r\n"
				+ "        {\r\n" + "          \"mailRoomDate\": \"2008-01-02\",\r\n"
				+ "          \"documentCode\": \"ISSUE.NTF\",\r\n"
				+ "          \"documentDescription\": \"Issue Notification\",\r\n"
				+ "          \"documentCategory\": \"PROSECUTION\",\r\n" + "          \"pageCount\": 1,\r\n"
				+ "          \"pdfLink\": \"pdfLink\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"mailRoomDate\": \"2007-11-23\",\r\n" + "          \"documentCode\": \"IFEE\",\r\n"
				+ "          \"documentDescription\": \"Issue Fee Payment (PTO-85B)\",\r\n"
				+ "          \"documentCategory\": \"PROSECUTION\",\r\n" + "          \"pageCount\": 1,\r\n"
				+ "          \"pdfLink\": \"pdfLink02\"\r\n" + "        }]\r\n" + "    },\r\n"
				+ "    \"status\": true\r\n" + "}]";
		System.out.println(pre);
		System.out.println("-----------------");
		System.out.println(getFormatedJson((getSortedJson(pre))));
		System.out.println("-----------------");
		System.out.println(getRootKeys(pre));
	}
}
