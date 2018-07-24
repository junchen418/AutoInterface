package com.aaron.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.aaron.base.ConfigProperties;
import com.aaron.base.FormatUtils;
import com.aaron.base.NaiveSSLContext;
import com.aaron.base.ResponseBean;
import com.zf.zson.ZSON;
import com.zf.zson.result.ZsonResult;

public class HttpService {
	private static Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final String DEFAULT_CHARSET = "UTF-8";
	private final int DEFAULT_SOCKET_TIMEOUT = 5000;
	private final int DEFAULT_RETRY_TIMES = 0;
	private SSLConnectionSocketFactory socketFactory;
	private String cookies;
	private ResponseBean responseBean;
	private boolean isUrlEncode = false;
	private CloseableHttpClient closeableHttpClient;

	public HttpService() {
		this.cookies = "";
		this.responseBean = new ResponseBean();
	}

	public HttpService(boolean isUrlEncode) {
		this.cookies = "";
		this.responseBean = new ResponseBean();
		this.isUrlEncode = isUrlEncode;
	}

	public CloseableHttpClient createHttpClient() {
		return createHttpClient(DEFAULT_RETRY_TIMES, DEFAULT_SOCKET_TIMEOUT);
	}

	public CloseableHttpClient createHttpClient(int socketTimeout) {
		return createHttpClient(DEFAULT_RETRY_TIMES, socketTimeout);
	}

	public CloseableHttpClient createHttpClient(int retryTimes, int socketTimeout) {
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(DEFAULT_SOCKET_TIMEOUT);
		builder.setConnectionRequestTimeout(DEFAULT_SOCKET_TIMEOUT);
		builder.setSocketTimeout(socketTimeout);
		builder.setRedirectsEnabled(Boolean.valueOf(ConfigProperties.getInstance().getString("RedirectsEnabled")));
		RequestConfig defaultRequestConfig = builder.setCookieSpec(CookieSpecs.DEFAULT).setExpectContinueEnabled(true)
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
		openSSL();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
		// 创建连接管理器,添加连接配置信息
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		if (retryTimes > 0) {
			setRetryHandler(httpClientBuilder, retryTimes);
		}
		PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
		Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
				.register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider(publicSuffixMatcher))
				.register(CookieSpecs.STANDARD, new RFC6265CookieSpecProvider(publicSuffixMatcher)).build();
		CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connectionManager)
				.setDefaultCookieSpecRegistry(r).setDefaultRequestConfig(defaultRequestConfig).build();
		closeableHttpClient = httpClient;
		return httpClient;
	}

	/**
	 * 执行HttpGet请求
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 * @param reffer
	 *            reffer信息，可传null
	 * @param contentType
	 *            Content-Type信息
	 * @param cookie
	 *            cookies信息，可传null
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executeGet(CloseableHttpClient httpClient, String url, Object pathParamObj, String reffer,
			String contentType, String cookie, String charset, boolean isCloseHttpClient) {
		Map<String, String> headerMap = buildHeadsMap(cookie, reffer, contentType);
		executeGet(httpClient, url, pathParamObj, headerMap, charset, isCloseHttpClient);
	}

	/**
	 * 执行HttpGet请求
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 * @param headerMap
	 *            header头信息
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executeGet(CloseableHttpClient httpClient, String url, Object pathParamObj,
			Map<String, String> headerMap, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		try {
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpGet get = (HttpGet) initialRequestPath("GET", url, pathEntity, charset);
			handleRequestHeads(get, headerMap);
			httpResponse = httpClient.execute(get);
			logInfo(get, httpResponse);
			String tmpCookies = getCookies(httpResponse, charset);
			handleCookies(tmpCookies);
			setResponseBean(httpResponse, charset);
		} catch (IOException e) {
			logger.error("Request failed!");
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 执行HttpGet请求
	 * 
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 * @param headerMap
	 *            header头信息
	 */
	public void executeGet(String url, Object pathParamObj, Map<String, String> headerMap) {
		executeGet(null, url, pathParamObj, headerMap, null, true);
	}

	/**
	 * 执行HttpGet请求
	 * 
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 * @param cookie
	 *            cookies信息，可传null
	 */
	public void executeGet(String url, Object pathParamObj, String cookie) {
		executeGet(null, url, pathParamObj, null, null, cookie, null, true);
	}

	/**
	 * 执行HttpGet请求
	 * 
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 */
	public void executeGet(String url, Object pathParamObj) {
		executeGet(null, url, pathParamObj, null, null, this.cookies, null, true);
	}

	/**
	 * 执行HttpPost请求
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的path参数信息，目前支持Map,和String(JSON\xml)
	 * @param bodyParamObj
	 *            提交的body参数信息，目前支持Map,和String(JSON\xml)
	 * @param reffer
	 *            reffer信息，可传null
	 * @param contentType
	 *            Content-Type信息
	 * @param cookie
	 *            cookies信息，可传null
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executePost(CloseableHttpClient httpClient, String url, Object pathParamObj, Object bodyParamObj,
			String reffer, String contentType, String cookie, String charset, boolean isCloseHttpClient) {
		Map<String, String> headerMap = buildHeadsMap(cookie, reffer, contentType);
		executePost(httpClient, url, pathParamObj, bodyParamObj, headerMap, charset, isCloseHttpClient);
	}

	/**
	 * 执行HttpPost请求
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的path参数信息，目前支持Map,和String(JSON\xml)
	 * @param bodyParamObj
	 *            提交的body参数信息，目前支持Map,和String(JSON\xml)
	 * @param headerMap
	 *            header头信息
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executePost(CloseableHttpClient httpClient, String url, Object pathParamObj, Object bodyParamObj,
			Map<String, String> headerMap, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			HttpPost post = (HttpPost) initialRequestPath("POST", url, pathEntity, charset);
			handleRequestHeads(post, headerMap);
			HttpEntity bodyEntity = getEntity(bodyParamObj, charset);
			if (bodyEntity != null) {
				post.setEntity(bodyEntity);
			}
			httpResponse = httpClient.execute(post);
			logInfo(post, httpResponse);
			String tmpCookies = getCookies(httpResponse, charset);
			handleCookies(tmpCookies);
			setResponseBean(httpResponse, charset);
		} catch (IOException e) {
			logger.error("Request failed!");
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e2) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * 执行HttpPost请求
	 * 
	 * @param url
	 *            请求的远程地址
	 * @param bodyParamObj
	 *            提交的body参数信息，目前支持Map,和String(JSON\xml)
	 * @param headerMap
	 *            header头信息
	 */
	public void executePost(String url, Object pathParamObj, Object bodyParamObj, Map<String, String> headerMap) {
		executePost(null, url, pathParamObj, bodyParamObj, headerMap, null, true);
	}

	/**
	 * 执行HttpPost请求
	 * 
	 * @param url
	 *            请求的远程地址
	 * @param bodyParamObj
	 *            提交的body参数信息，目前支持Map,和String(JSON\xml)
	 */
	public void executePost(String url, Object bodyParamObj) {
		executePost(null, url, null, bodyParamObj, null, null, this.cookies, null, true);
	}

	/**
	 * 执行HttpPost请求
	 * 
	 * @param url
	 *            请求的远程地址
	 * @param pathParamObj
	 *            提交的path参数信息，目前支持Map,和String(JSON\xml)
	 * @param bodyParamObj
	 *            提交的body参数信息，目前支持Map,和String(JSON\xml)
	 */
	public void executePost(String url, Object pathParamObj, Object bodyParamObj) {
		executePost(null, url, pathParamObj, bodyParamObj, null, null, this.cookies, null, true);
	}

	/**
	 * 执行httpPut请求
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求url
	 * @param pathParamObj
	 *            url路径参数
	 * @param bodyParamObj
	 *            请求body信息
	 * @param reffer
	 *            请求头Reffer信息
	 * @param cookie
	 *            请求头cookie信息
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executePut(CloseableHttpClient httpClient, String url, Object pathParamObj, Object bodyParamObj,
			String reffer, String cookie, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			HttpEntity bodyEntity = getEntity(bodyParamObj, charset);
			HttpPut put = (HttpPut) initialRequestPath("PUT", url, pathEntity, charset);
			handleRequestHeads(put, cookie, reffer, null);
			if (bodyEntity != null) {
				put.setEntity(bodyEntity);
			}
			httpResponse = httpClient.execute(put);
			logInfo(put, httpResponse);
			String tmpCookies = getCookies(httpResponse, charset);
			handleCookies(tmpCookies);
			setResponseBean(httpResponse, charset);
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e2) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * 执行httpPut请求
	 * 
	 * @param url
	 *            请求url
	 * @param pathParamObj
	 *            url路径参数
	 * @param bodyParamObj
	 *            请求body信息
	 */
	public void executePut(String url, Object pathParamObj, Object bodyParamObj) {
		executePut(null, url, pathParamObj, bodyParamObj, null, this.cookies, null, true);
	}

	/**
	 * 执行httpDelete请求
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求url
	 * @param pathParamObj
	 *            url路径参数
	 * @param reffer
	 *            请求头Reffer信息
	 * @param cookie
	 *            请求头cookie信息
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executeDelete(CloseableHttpClient httpClient, String url, Object pathParamObj, String reffer,
			String cookie, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			HttpDelete delete = (HttpDelete) initialRequestPath("DELETE", url, pathEntity, charset);
			handleRequestHeads(delete, cookie, reffer, null);
			httpResponse = httpClient.execute(delete);
			logInfo(delete, httpResponse);
			String tmpCookies = getCookies(httpResponse, charset);
			handleCookies(tmpCookies);
			setResponseBean(httpResponse, charset);
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e2) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * 执行httpDelete请求
	 * 
	 * @param url
	 *            请求url
	 * @param pathParamObj
	 *            url路径参数
	 * @param cookie
	 *            请求头cookie信息
	 */
	public void executeDelete(String url, Object pathParamObj, String cookie) {
		executeDelete(null, url, pathParamObj, null, cookie, null, true);
	}

	/**
	 * 执行httpDelete请求
	 * 
	 * @param url
	 *            请求url
	 * @param pathParamObj
	 *            url路径参数
	 */
	public void executeDelete(String url, Object pathParamObj) {
		executeDelete(null, url, pathParamObj, null, this.cookies, null, true);
	}

	/**
	 * 执行文件上传
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param pathParamObj
	 *            url路径参数
	 * @param localFilePath
	 *            本地文件地址
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executeUploadFile(CloseableHttpClient httpClient, String remoteFileUrl, Object pathParamObj,
			String localFilePath, String cookie, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			charset = charset == null ? DEFAULT_CHARSET : charset;
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			HttpPost httpPost = (HttpPost) initialRequestPath("POST", remoteFileUrl, pathEntity, charset);
			if (localFilePath != null) {
				File localFile = new File(localFilePath);
				FileBody fileBody = new FileBody(localFile);
				// 以浏览器兼容模式运行，防止文件名乱码。
				HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addPart("uploadFile", fileBody).setCharset(CharsetUtils.get("UTF-8")).build();
				httpPost.setEntity(reqEntity);
			} else {
				HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.setCharset(CharsetUtils.get("UTF-8")).build();
				httpPost.setEntity(reqEntity);
			}
			handleRequestHeads(httpPost, cookie, null, null);
			httpResponse = httpClient.execute(httpPost);
			logInfo(httpPost, httpResponse);
			String tmpCookies = getCookies(httpResponse, charset);
			handleCookies(tmpCookies);
			setResponseBean(httpResponse, charset);
		} catch (IOException e) {
			logger.error("Upload File failed!");
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 执行文件上传
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param pathParamObj
	 *            url路径参数
	 * @param localFilePath
	 *            本地文件地址
	 * @param headerMap
	 *            请求头信息
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 */
	public void executeUploadFile(CloseableHttpClient httpClient, String remoteFileUrl, Object pathParamObj,
			Map<String, String> headerMap, String localFilePath, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			charset = charset == null ? DEFAULT_CHARSET : charset;
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			HttpPost httpPost = (HttpPost) initialRequestPath("POST", remoteFileUrl, pathEntity, charset);
			if (localFilePath != null) {
				File localFile = new File(localFilePath);
				FileBody fileBody = new FileBody(localFile);
				// 以浏览器兼容模式运行，防止文件名乱码。
				HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addPart("uploadFile", fileBody).setCharset(CharsetUtils.get("UTF-8")).build();
				httpPost.setEntity(reqEntity);
			} else {
				HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.setCharset(CharsetUtils.get("UTF-8")).build();
				httpPost.setEntity(reqEntity);
			}
			handleRequestHeads(httpPost, headerMap);
			httpResponse = httpClient.execute(httpPost);
			logInfo(httpPost, httpResponse);
			String tmpCookies = getCookies(httpResponse, charset);
			handleCookies(tmpCookies);
			setResponseBean(httpResponse, charset);
		} catch (IOException e) {
			logger.error("Upload File failed!");
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 执行文件上传
	 * 
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param pathParamObj
	 *            url路径参数
	 * @param localFilePath
	 *            本地文件地址
	 */
	public void executeUploadFile(String remoteFileUrl, Object pathParamObj, String localFilePath) {
		executeUploadFile(null, remoteFileUrl, pathParamObj, localFilePath, cookies, null, true);
	}

	/**
	 * 执行文件上传
	 * 
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param localFilePath
	 *            本地文件地址
	 */
	public void executeUploadFile(String remoteFileUrl, String localFilePath) {
		executeUploadFile(null, remoteFileUrl, null, localFilePath, cookies, null, true);
	}

	/**
	 * 执行文件上传
	 * 
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param pathParamObj
	 *            路径参数
	 * @param headMap
	 *            请求信息系头信息
	 */
	public void executeUploadFile(String remoteFileUrl, Object pathParamObj, HashMap<String, String> headMap,
			String localFilePath) {
		executeUploadFile(null, remoteFileUrl, pathParamObj, headMap, localFilePath, null, true);
	}

	/**
	 * 执行文件下载
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            远程下载文件地址
	 * @param localFilePath
	 *            本地存储文件地址
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param isCloseHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return 返回Boolean结果
	 */
	public boolean executeDownloadFile(CloseableHttpClient httpClient, String remoteFileUrl, Object pathParamObj,
			String localFilePath, String cookie, String charset, boolean isCloseHttpClient) {
		CloseableHttpResponse response = null;
		InputStream in = null;
		FileOutputStream fout = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		try {
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpGet get = (HttpGet) initialRequestPath("GET", remoteFileUrl, pathEntity, charset);
			handleRequestHeads(get, cookie, null, null);
			response = httpClient.execute(get);
			logInfo(get, response);
			String tmpCookies = getCookies(response, charset);
			handleCookies(tmpCookies);
			HttpEntity entity = response.getEntity();
			if (entity == null || -1L == entity.getContentLength()) {
				return false;
			} else if (entity != null) {
				in = entity.getContent();
				File file = new File(localFilePath);
				fout = new FileOutputStream(file);
				int len = -1;
				byte[] tmp = new byte[1024 * 1024];
				while ((len = in.read(tmp)) != -1) {
					fout.write(tmp, 0, len);
				}
				fout.flush();
				EntityUtils.consume(entity);
			}
			return true;
		} catch (IOException e) {
			logger.error("Download File failed!");
			return false;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public boolean executeDownloadFile(String remoteFileUrl, String localFilePath) {
		return executeDownloadFile(null, remoteFileUrl, null, localFilePath, cookies, null, true);
	}

	public boolean executeDownloadFile(String remoteFileUrl, Object pathParamObj, String localFilePath)
			throws IOException {
		return executeDownloadFile(null, remoteFileUrl, pathParamObj, localFilePath, cookies, null, true);
	}

	/**
	 * 断点续传
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            上传请求url
	 * @param pathParamObj
	 *            请求路径参数
	 * @param bodyBytes
	 *            请求上传文件片段
	 * @param headerMap
	 *            请求的头信息
	 * @param uploadFile
	 *            要上传的文件路径
	 * @param charset
	 *            请求编码
	 * @param isCloseHttpClient
	 *            请求完成后是否关闭
	 * @return HttpResponse
	 */
	public HttpResponse uploadFileWithSegment(CloseableHttpClient httpClient, String url, Object pathParamObj,
			byte[] bodyBytes, Map<String, String> headerMap, File uploadFile, String charset,
			boolean isCloseHttpClient) {
		CloseableHttpResponse httpResponse = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			HttpPost httpPost = (HttpPost) initialRequestPath("POST", url, pathEntity, charset);
			handleRequestHeads(httpPost, headerMap);
			if (bodyBytes != null) {
				HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
						.addPart(uploadFile.getName(), new ByteArrayBody(bodyBytes, uploadFile.getName()))
						.setCharset(CharsetUtils.get(charset)).build();
				httpPost.setEntity(reqEntity);
			}
			httpResponse = httpClient.execute(httpPost);
			logInfo(httpPost, httpResponse);
			setResponseBean(httpResponse, DEFAULT_CHARSET);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
		return httpResponse;
	}

	/**
	 * 断点续传
	 * 
	 * @param url
	 *            上传请求url
	 * @param pathParamObj
	 *            请求路径参数
	 * @param bodyBytes
	 *            请求上传文件片段
	 * @param headerMap
	 *            请求的头信息
	 * @param uploadFile
	 *            要上传的文件路径
	 * @return HttpResponse
	 */
	public HttpResponse uploadFileWithSegment(String url, Object pathParamObj, byte[] bodyBytes,
			Map<String, String> headerMap, File uploadFile) {
		return uploadFileWithSegment(null, url, pathParamObj, bodyBytes, headerMap, uploadFile, null, true);
	}

	/**
	 * 下载文件分片
	 * 
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            下载文件请求url
	 * @param pathParamObj
	 *            请求路径参数
	 * @param localFileDir
	 *            下载数据保存的文件目录
	 * @param headerMap
	 *            请求头信息
	 * @param charset
	 *            请求支持的编码
	 * @param isCloseHttpClient
	 *            请求结束后是否关闭
	 * @return 返回文件名
	 */
	public String downloadFileWithSegment(CloseableHttpClient httpClient, String remoteFileUrl, Object pathParamObj,
			String localFileDir, Map<String, String> headerMap, String charset, String resposeCharset,
			boolean isCloseHttpClient) {
		CloseableHttpResponse response = null;
		InputStream in = null;
		FileOutputStream fout = null;
		charset = charset == null ? DEFAULT_CHARSET : charset;
		String fileName = null;
		if (localFileDir.endsWith("/")) {
			localFileDir = localFileDir.substring(0, localFileDir.length() - 1);
		}

		try {
			HttpEntity pathEntity = getEntity(pathParamObj, charset);
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			HttpGet get = (HttpGet) initialRequestPath("GET", remoteFileUrl, pathEntity, charset);
			handleRequestHeads(get, headerMap);
			response = httpClient.execute(get);
			logInfo(get, response);
			String name;
			Header header = response.getFirstHeader("Content-Disposition");
			Header contentType = response.getFirstHeader("Content-Type");
			if (header != null) {
				String contentDisposition;
				if ("".equals(resposeCharset) || resposeCharset == null) {
					contentDisposition = header.getValue();
				} else {
					contentDisposition = new String(header.getValue().getBytes(resposeCharset), charset);
				}
				name = contentDisposition.substring(contentDisposition.indexOf("filename=")).split("=")[1];
				if (name != null && !"".equals(name)) {
					fileName = localFileDir + File.separatorChar + name;
				} else {
					fileName = localFileDir + File.separatorChar + UUIDUtils.getUUID();
				}
			} else {
				fileName = localFileDir + File.separatorChar + UUIDUtils.getUUID();
			}
			if (contentType == null || "".equals(contentType.getValue())
					|| contentType.getValue().contains("application/x-www-form-urlencoded")
					|| contentType.getValue().contains("multipart/form-data")
					|| contentType.getValue().contains("text/plain") || contentType.getValue().contains("text/xml")
					|| contentType.getValue().contains("text/html")
					|| contentType.getValue().contains("application/json")) {
				setResponseBean(response, charset);
			} else {
				HttpEntity entity = response.getEntity();
				if (entity == null || -1L == entity.getContentLength()) {
					return null;
				} else if (entity != null) {
					in = entity.getContent();
					File file = new File(fileName);
					fout = new FileOutputStream(file, true);
					int len = -1;
					byte[] tmp = new byte[1024 * 1024];
					while ((len = in.read(tmp)) != -1) {
						fout.write(tmp, 0, len);
					}
					fout.flush();
					EntityUtils.consume(entity);
				}
			}
			return fileName;
		} catch (IOException e) {
			logger.error("Download File failed!\n" + e.getMessage());
			return null;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (isCloseHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 下载文件分片
	 * 
	 * @param remoteFileUrl
	 *            下载文件请求url
	 * @param pathParamObj
	 *            请求路径参数
	 * @param localFileDir
	 *            下载数据保存的文件目录
	 * @param headerMap
	 *            请求头信息
	 * @return 文件名
	 */
	public String downloadFileWithSegment(String remoteFileUrl, Object pathParamObj, String localFileDir,
			Map<String, String> headerMap) {
		return downloadFileWithSegment(null, remoteFileUrl, pathParamObj, localFileDir, headerMap, null, null, true);
	}

	/**
	 * 下载文件分片
	 * 
	 * @param remoteFileUrl
	 *            下载文件请求url
	 * @param pathParamObj
	 *            请求路径参数
	 * @param localFileDir
	 *            下载数据保存的文件目录
	 * @param headerMap
	 *            请求头信息
	 * @param ContentDispositionCharset
	 *            Content-Disposition的编码格式
	 * @return 文件名
	 */
	public String downloadFileWithSegment(String remoteFileUrl, Object pathParamObj, String localFileDir,
			Map<String, String> headerMap, String ContentDispositionCharset) {
		return downloadFileWithSegment(null, remoteFileUrl, pathParamObj, localFileDir, headerMap, null,
				ContentDispositionCharset, true);
	}

	/**
	 * 获取Cookies信息
	 * 
	 * @return cookies
	 */
	public String getCookies() {
		return this.cookies;
	}

	/**
	 * 获取响应信息
	 * 
	 * @return ResponseBean
	 */
	public ResponseBean getResponseBean() {
		return this.responseBean;
	}

	/**
	 * 获取响应内容
	 * 
	 * @return 响应内容
	 */
	public String getResponseBody() {
		return this.responseBean.getBody();
	}

	/**
	 * 获取响应内容的json处理结果
	 * 
	 * @return ZsonResult
	 */
	public ZsonResult getZresponse() {
		return ZSON.parseJson(getResponseBody());
	}

	/**
	 * 清除cookies
	 */
	public void clearCookies() {
		this.cookies = "";
	}

	/**
	 * 获取httpclient客户端
	 * 
	 * @return CloseableHttpClient
	 */
	public CloseableHttpClient getCloseableHttpClient() {
		return closeableHttpClient;
	}

	/**
	 * 设置是否对url进行urlencode编码
	 * 
	 * @param flag
	 */
	public void setIsUrlEncode(boolean flag) {
		this.isUrlEncode = flag;
	}

	/**
	 * 是否对url进行url编码
	 * 
	 * @param flag
	 */
	public boolean getIsUrlEncode(boolean flag) {
		return this.isUrlEncode;
	}

	private HttpRequestBase initialRequestPath(String methodName, String url, HttpEntity pathEntity, String charset)
			throws ParseException, IOException {
		String httpURL = handleURL(url);
		HttpRequestBase request = null;
		switch (methodName) {
		case "GET":
			request = new HttpGet();
			break;
		case "POST":
			request = new HttpPost();
			break;
		case "PUT":
			request = new HttpPut();
			break;
		case "DELETE":
			request = new HttpDelete();
			break;
		default:
			request = new HttpPost();
			break;
		}
		if (pathEntity == null) {
			request.setURI(URI.create(httpURL));
		} else {
			if (isUrlEncode) {
				request.setURI(URI.create(httpURL + '?' + EntityUtils.toString(pathEntity, charset)));
			} else {
				request.setURI(URI
						.create(httpURL + '?' + URLDecoder.decode(EntityUtils.toString(pathEntity, charset), charset)));
			}
		}
		return request;
	}

	private String handleURL(String url) {
		if (!url.startsWith("http") && !url.startsWith("https")) {
			String baseURL = ConfigProperties.getInstance().getString("baseURL");
			if (baseURL.endsWith("/") && url.startsWith("/")) {
				url = baseURL + url.substring(1);
			} else if (!baseURL.endsWith("/") && !url.startsWith("/")) {
				url = baseURL + "/" + url;
			} else {
				url = baseURL + url;
			}
		}
		return url;
	}

	/**
	 * 请求参数转换HttpEntity
	 * 
	 * @param pathParamObj
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	private HttpEntity getEntity(Object paramsObj, String charset) throws UnsupportedEncodingException {
		if (paramsObj == null) {
			return null;
		}
		if (Map.class.isInstance(paramsObj)) {
			Map<String, String> paramsMap = (Map<String, String>) paramsObj;
			List<NameValuePair> list = getNameValuePairs(paramsMap);
			UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
			httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
			return httpEntity;
		} else if (String.class.isInstance(paramsObj)) {
			String paramsStr = paramsObj.toString();
			StringEntity httpEntity = new StringEntity(paramsStr, charset);
			if (checkJson(paramsStr)) {
				httpEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
			} else if (checkXML(paramsStr)) {
				httpEntity.setContentType(ContentType.APPLICATION_XML.getMimeType());
			} else {
				httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
			}
			return httpEntity;
		} else if (Collection.class.isInstance(paramsObj)) {
			Collection<Map<String, String>> mCollection = (Collection<Map<String, String>>) paramsObj;
			List<NameValuePair> list = getNameValuePairs(mCollection);
			UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
			httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
			return httpEntity;

		} else if (byte[].class.isInstance(paramsObj)) {
			ByteArrayEntity httpEntity = new ByteArrayEntity((byte[]) paramsObj);
			httpEntity.setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
			return httpEntity;
		} else {
			logger.info("当前传入参数不能识别类型，无法生成HttpEntity");
		}
		return null;
	}

	private void setResponseBean(CloseableHttpResponse httpResponse, String charset) {
		try {
			if (httpResponse != null) {
				String responseBoyd = getResult(httpResponse, charset);
				this.responseBean.setStatus(httpResponse.getStatusLine().getReasonPhrase());
				this.responseBean.setStatusCode(Integer.toString(httpResponse.getStatusLine().getStatusCode()));
				this.responseBean.setBody(responseBoyd);
				this.responseBean.setContentType(httpResponse.getFirstHeader("Content-Type") == null ? ""
						: httpResponse.getFirstHeader("Content-Type").getValue());
				this.responseBean.setVersion(httpResponse.getProtocolVersion().toString());
				this.responseBean.setSetCookie(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
						: httpResponse.getFirstHeader("Set-Cookie").getValue());
				Header contentLength = httpResponse.getFirstHeader("Content-Length");
				this.responseBean
						.setContentLength((contentLength != null) ? Long.parseLong(contentLength.getValue()) : null);
				Header contentRange = httpResponse.getFirstHeader("Content-Range");
				this.responseBean.setContentRange((contentRange != null) ? contentRange.getValue() : null);
				Header location = httpResponse.getFirstHeader("Location");
				this.responseBean.setLocation((location != null) ? location.getValue() : null);
				logger.info(FormatUtils.formatJson(responseBoyd));
			}
		} catch (ParseException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 从结果中获取出String数据
	 * 
	 * @param httpResponse
	 * @param charset
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private String getResult(CloseableHttpResponse httpResponse, String charset) throws ParseException, IOException {
		String result = "";
		if (httpResponse == null) {
			return result;
		}
		HttpEntity entity = httpResponse.getEntity();
		if (entity == null) {
			return result;
		}
		result = EntityUtils.toString(entity, charset);
		EntityUtils.consume(entity);
		return result;
	}

	private String getCookies(CloseableHttpResponse httpResponse, String charset) throws ParseException, IOException {
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		if (headers.length > 0) {
			for (Header header : headers) {
				String cookieValue = responseBean.getSetCookie() + " " + header.getValue() + " ";
				this.responseBean.setSetCookie(cookieValue.trim());
				String value = header.getValue();
				if (!cookies.contains(value)) {
					cookies = cookies + ";" + value;
				}
				if (cookies.startsWith(";")) {
					cookies = cookies.substring(1);
				}
				return cookies;
			}
		}
		return "";
	}

	private List<NameValuePair> getNameValuePairs(Map<String, String> paramsMap) {
		List<NameValuePair> list = new ArrayList<>();
		if (paramsMap == null || paramsMap.isEmpty()) {
			return list;
		}
		for (Entry<String, String> entry : paramsMap.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return list;
	}

	private List<NameValuePair> getNameValuePairs(Collection<Map<String, String>> paramsCollection) {
		List<NameValuePair> list = new ArrayList<>();
		for (Iterator<Map<String, String>> miterator = paramsCollection.iterator(); miterator.hasNext();) {
			Map<String, String> map = miterator.next();
			if (map == null || map.isEmpty()) {
				return list;
			}
			for (Entry<String, String> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return list;
	}

	private void openSSL() {
		try {
			socketFactory = new SSLConnectionSocketFactory(NaiveSSLContext.createIgnoreVerifySSL(),
					NoopHostnameVerifier.INSTANCE);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private Map<String, String> buildHeadsMap(String cookie, String reffer, String contentType) {
		Map<String, String> headerMap = new HashMap<>();
		if (cookie != null && !"".equals(cookie)) {
			headerMap.put("Cookie", cookie);
		}
		if (reffer != null && !"".equals(reffer)) {
			headerMap.put("Reffer", reffer);
		}
		if (contentType != null && !"".equals(contentType)) {
			headerMap.put("Content-Type", contentType);
		}
		return headerMap;
	}

	private void handleRequestHeads(HttpRequestBase request, String cookie, String reffer, String contentType) {
		Map<String, String> headerMap = new HashMap<>();
		if (cookie != null && !"".equals(cookie)) {
			headerMap.put("Cookie", cookie);
		}
		if (reffer != null && !"".equals(reffer)) {
			headerMap.put("Reffer", reffer);
		}
		if (contentType != null && !"".equals(contentType)) {
			headerMap.put("Content-Type", contentType);
		}
		handleRequestHeads(request, headerMap);
	}

	private void handleRequestHeads(HttpRequestBase request, Map<String, String> headerMap) {
		if (headerMap == null) {
			return;
		} else if (!headerMap.containsKey("Cookie") || headerMap.get("Cookie") == null
				|| "".equals(headerMap.get("Cookie"))) {
			headerMap.put("Cookie", cookies);
		} else if (!headerMap.get("Cookie").contains(cookies)) {
			headerMap.put("Cookie", headerMap.get("Cookie") + ";" + cookies);
		}
		Header[] headers = new Header[headerMap.size()];
		int i = 0;
		for (Entry<String, String> entry : headerMap.entrySet()) {
			headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
			i++;
		}
		request.setHeaders(headers);
	}

	private void handleCookies(String cookie) {
		if (cookie != null && !"".equals(cookie)) {
			if (!cookies.contains(cookie)) {
				this.cookies = this.cookies + ";" + cookies;
			}
			if (this.cookies.startsWith(";")) {
				this.cookies = this.cookies.substring(1);
			}
		}
	}

	private void setRetryHandler(HttpClientBuilder httpClientBuilder, final int retryTimes) {
		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= retryTimes) {
					return false;
				}
				if (exception instanceof InterruptedIOException) {
					return false;
				}
				if (exception instanceof UnknownHostException) {
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {
					return false;
				}
				if (exception instanceof SSLException) {
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					return true;
				}
				return false;
			}
		};
		httpClientBuilder.setRetryHandler(retryHandler);
	}

	private void logInfo(HttpRequestBase request, CloseableHttpResponse httpResponse) {
		logger.info("#####################################Start#####################################");
		logger.info("Request URL : " + request.getRequestLine().getUri());
		logger.info("Request Method : " + request.getMethod());
		logger.info("StatusCode : " + httpResponse.getStatusLine().getStatusCode());
		String cookieString = "";
		for (Header cookie : request.getHeaders("Cookie")) {
			cookieString += cookie.getValue() + ";";
		}
		if ("".equals(cookieString)) {
			logger.info("Cookie : " + cookieString);
		} else {
			logger.info("Cookie : " + cookieString.substring(0, cookieString.length() - 1));
		}
		logger.info("######################################End######################################\n");
	}

	private boolean checkJson(String json) {
		try {
			ZSON.parseJson(json);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private static boolean checkXML(String xmlString) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			builder.parse(new InputSource(new StringReader(xmlString)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws IOException {

		HttpService uploadSegment = new HttpService(false);
		File file = new File(
				"D:\\测试执行\\testdata\\音视频\\音视频格式文件\\转换前的视频格式\\class_83c18e1afeb9401d9579d6e8ab812615_u_4f2bf5b5b96f4b13859bc93c8d382b09__main_1.flv");
		long start = 0L;
		long end = 0L;
		// long end = -1L;
		int buffer_size = 1024 * 1024;
		HashMap<String, String> pathMap = new HashMap<String, String>();
		pathMap.put("areaCode", "9000");
		pathMap.put("originalFileName", file.getName());
		HashMap<String, String> headMap = new HashMap<String, String>();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer_size);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			int buf_size = buffer_size;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			int count = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				start = end;
				end += len;
				// start = end + 1;
				// end += len;
				bos.write(buffer, 0, len);
				bos.flush();
				headMap.put("Range", start + "-" + end);
				logger.info("This request's Range is : " + start + "-" + end);
				uploadSegment.uploadFileWithSegment(null, "http://10.5.32.212:8080/reserver/dms/uploadFile", pathMap,
						bos.toByteArray(), headMap, file, null, true);
				bos.reset();
				count++;
				// Thread.sleep(5000);
				if (!uploadSegment.getResponseBody().contains("upload success")) {
					break;
				}
			}
			headMap.put("Range", "0-0");
			logger.info("This request's Range is : 0-0");
			uploadSegment.uploadFileWithSegment(null, "http://10.5.32.212:8080/reserver/dms/uploadFile", pathMap, null,
					headMap, file, null, true);
			logger.info("total send count : " + count++);

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

}