package com.aaron.base;

public class FormatUtils {

	/**
	 * 格式化json字符串
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return
	 */
	public static String formatJson(String jsonStr) {
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
	 * @param sb
	 *            添加缩进字符字符串
	 * @param indent
	 *            天剑缩进字符量，每一个缩进字符为四个空格符
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append("    ");
		}
	}

	public static void main(String[] args) {
		String string = "{\"command\":\"online\",\"body\":{\"userToken\":\"2222334sddssdsd=\",\"clientType\":\"web\",\"deviceName\":\"iphone7s\"}}";
		String result = formatJson(string);
		System.out.println(result);
	}
}
