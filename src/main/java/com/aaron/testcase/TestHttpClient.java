package com.aaron.testcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;

public class TestHttpClient {

	// 创建CookieStore实例
	static CookieStore cookieStore = null;
	static HttpClientContext context = null;
	String loginUrl = "http://10.5.32.212:8080/reserver/serverWeb/loginCheck";
	String toFilesListPage = "http://10.5.32.212:8080/reserver/serverWeb/filesPageList";

	@Test
	public void testLogin() throws Exception {
		System.out.println("----testLogin");

		// // 创建HttpClientBuilder
		// HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// // HttpClient
		// CloseableHttpClient client = httpClientBuilder.build();
		// 直接创建client
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(loginUrl);
		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("userName", "administrator");
		parameterMap.put("password", "123456");
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
		httpPost.setEntity(postEntity);
		System.out.println("request line:" + httpPost.getRequestLine());
		try {
			// 执行post请求
			HttpResponse httpResponse = client.execute(httpPost);
			printResponse(httpResponse);

			setCookieStore(httpResponse);
			setContext();

			// 执行get请求
			HttpGet httpGet = new HttpGet(toFilesListPage);
			System.out.println("request line:" + httpGet.getRequestLine());
			HttpResponse httpResponse1 = client.execute(httpGet);
			printResponse(httpResponse1);
			System.out.println("cookie store:" + cookieStore.getCookies());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test(dependsOnMethods = { "testLogin" })
	public void testContext() throws Exception {
		System.out.println("----testContext");
		// 使用context方式
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(toFilesListPage);
		System.out.println("request line:" + httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpGet, context);
			System.out.println("#####################context cookies:" + context.getCookieStore().getCookies());
			printResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test(dependsOnMethods = { "testLogin" })
	public void testCookieStore() throws Exception {
		System.out.println("----testCookieStore");
		// 使用cookieStore方式
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		HttpGet httpGet = new HttpGet(toFilesListPage);
		System.out.println("request line:" + httpGet.getRequestLine());
		try {
			System.out.println("cookie store:" + cookieStore.getCookies());
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpGet);
			printResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printResponse(HttpResponse httpResponse) throws ParseException, IOException {
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		// 响应状态
		System.out.println("status:" + httpResponse.getStatusLine());
		System.out.println("headers:");
		HeaderIterator iterator = httpResponse.headerIterator();
		while (iterator.hasNext()) {
			System.out.println("\t" + iterator.next());
		}
		// 判断响应实体是否为空
		if (entity != null) {
			String responseString = EntityUtils.toString(entity);
			System.out.println("response length:" + responseString.length());
			System.out.println("response content:" + responseString.replace("\r\n", ""));
		}
	}

	public static void setContext() {
		System.out.println("----setContext");
		PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
		context = HttpClientContext.create();
		Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create()
				.register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider(publicSuffixMatcher))
				.register(CookieSpecs.STANDARD, new RFC6265CookieSpecProvider(publicSuffixMatcher)).build();
		context.setCookieSpecRegistry(registry);
		context.setCookieStore(cookieStore);
	}

	public static void setCookieStore(HttpResponse httpResponse) {
		System.out.println("----setCookieStore");
		cookieStore = new BasicCookieStore();
		// JSESSIONID
		String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
		String JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
		System.out.println("JSESSIONID:" + JSESSIONID);
		// 新建一个Cookie
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
		cookie.setVersion(0);
		cookie.setDomain("10.5.32.212");
		cookie.setPath("/reserver/");
		cookieStore.addCookie(cookie);
	}

	public static List<NameValuePair> getParam(Map parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Iterator it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry parmEntry = (Entry) it.next();
			param.add(new BasicNameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue()));
		}
		return param;
	}
}
