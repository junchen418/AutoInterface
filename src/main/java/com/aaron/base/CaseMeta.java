package com.aaron.base;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.Assert;
import com.aaron.parser.JsonPathExtractor;
import com.aaron.util.FunctionUtil;
import com.aaron.util.StringUtil;

public class CaseMeta {

	/**
	 * 公共参数数据池（全局可用）
	 */
	private static Map<String, String> context = new HashMap<String, String>();

	/**
	 * 替换符，如果数据中包含“${}”则会被替换成公共参数中存储的数据
	 */
	protected Pattern replaceParamPattern = Pattern.compile("\\$\\{(.*?)\\}");

	/**
	 * 截取自定义方法正则表达式：__xxx(ooo)
	 */
	protected Pattern funPattern = Pattern.compile("__(\\w*?)\\((([\\w\\\\\\/:\\.\\$\\s-]*,?)*)\\)");

	/**
	 * 保存数据到公共参数数据池
	 * 
	 * @param map
	 */
	protected void saveDataToContext(Map<String, String> map) {
		context.putAll(map);
	}

	/**
	 * 保存数据到公共参数数据池
	 * 
	 * @param preParam 参数格式：a=1;b=2
	 */
	protected void saveDataToContext(String preParam) {
		if (StringUtil.isEmpty(preParam)) {
			return;
		}
		String[] preParamArr = preParam.split(";");
		String key, value;
		for (String prepar : preParamArr) {
			if (StringUtil.isEmpty(prepar)) {
				continue;
			}
			key = prepar.split("=")[0];
			value = prepar.split("=")[1];
			context.put(key, value);
		}
	}

	/**
	 * 提取json串中的值保存至公共池中
	 * 
	 * @param json    将被提取的json串。
	 * @param allSave 所有将被保存的数据：xx=$.jsonpath.xx;oo=$.jsonpath.oo，将$.jsonpath.
	 *                xx提取出来的值存放至公共池的xx中，将$.jsonpath.oo提取出来的值存放至公共池的oo中
	 */
	protected void saveResult(String json, String allSave) {
		if (null == json || "".equals(json) || null == allSave || "".equals(allSave)) {
			return;
		}
		allSave = getCommonParam(allSave);
		String[] saves = allSave.split(";");
		String key, value;
		for (String save : saves) {
			Pattern pattern = Pattern.compile("([^;=]*)=([^;]*)");
			Matcher m = pattern.matcher(save.trim());
			while (m.find()) {
				key = getBuildValue(json, m.group(1));
				value = getBuildValue(json, m.group(2));
				context.put(key, value);
			}
		}
	}

	/**
	 * 获取格式化后的值
	 * 
	 * @param sourchJson
	 * @param key
	 * @return
	 */
	private String getBuildValue(String sourchJson, String key) {
		key = key.trim();
		Matcher funMatch = funPattern.matcher(key);
		if (key.startsWith("$.")) {
			key = new JsonPathExtractor(sourchJson).getString(key);
		} else if (funMatch.find()) {
			String args = funMatch.group(2);
			String[] argArr = args.split(",");
			for (int index = 0; index < argArr.length; index++) {
				String arg = argArr[index];
				if (arg.startsWith("$.")) {
					argArr[index] = new JsonPathExtractor(sourchJson).getString(arg);
				}
			}
			String value = FunctionUtil.getValue(funMatch.group(1), argArr);
			key = StringUtil.replaceFirst(key, funMatch.group(), value);

		}
		return key;
	}

	/**
	 * 组件预参数（处理__fucn()以及${xxxx}）
	 * 
	 * @param param
	 * @return
	 */
	protected String buildParam(String param) {
		param = getCommonParam(param);
		Matcher m = funPattern.matcher(param);
		while (m.find()) {
			String funcName = m.group(1);
			String args = m.group(2);
			String value;
			if (FunctionUtil.isFunction(funcName)) {
				value = FunctionUtil.getValue(funcName, args.split(","));
				Assert.assertNotNull(value, String.format("解析函数失败：%s。", funcName));
				param = StringUtil.replaceFirst(param, m.group(), value);
			}
		}
		return param;
	}

	/**
	 * 组件预参数（处理__fucn()以及${xxxx}）
	 * 
	 * @param param
	 * @return
	 */
	protected Map<String, String> buildParam(Map<String, String> param) {
		for (String key : param.keySet()) {
			String temp = buildParam(param.get(key));
			param.put(key, temp);
		}
		return param;
	}

	/**
	 * 取公共参数 并替换参数
	 * 
	 * @param param Example：http://qa-analytics.zhihuiya.com/view/patent_id/${patent_id}
	 * @return
	 */
	protected String getCommonParam(String param) {
		if (StringUtil.isEmpty(param)) {
			return "";
		}
		Matcher m = replaceParamPattern.matcher(param);
		while (m.find()) {
			String replaceKey = m.group(1);
			String value;
			value = getContextData(replaceKey);
			Assert.assertNotNull(value, String.format("格式化参数失败，公共参数中找不到%s。", replaceKey));
			param = param.replace(m.group(), value);
		}
		return param;
	}

	/**
	 * 获取公共数据池中的数据
	 * 
	 * @param key 公共数据的key
	 * @return 对应的value
	 */
	protected String getContextData(String key) {
		if ("".equals(key) || !context.containsKey(key)) {
			return null;
		} else {
			return context.get(key);
		}
	}

	public static void sleep(long time) {
		try {
			Thread.currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CaseMeta baseCase = new CaseMeta();
		baseCase.saveDataToContext("patent_id=1");
		System.out.println(
				baseCase.buildParam("http://qa-analytics.zhihuiya.com/view/patent_id/${patent_id}/__date(yyyy-MM-dd)"));
	}
}
