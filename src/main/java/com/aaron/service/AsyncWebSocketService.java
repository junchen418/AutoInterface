package com.aaron.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.SslEngineFactory;
import org.asynchttpclient.netty.ssl.JsseSslEngineFactory;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaron.base.NaiveSSLContext;

public class AsyncWebSocketService {

	private static Logger logger = LoggerFactory.getLogger(AsyncWebSocketService.class);
	private AsyncHttpClientConfig config = null;
	private DefaultAsyncHttpClient client;
	private WebSocket websocket = null;
	private String resultText = null;
	private StringBuilder stringBuilder = new StringBuilder();
	private long receiveCount = 0;

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            websocket服务url
	 */
	public AsyncWebSocketService(String url) {
		try {
			this.config = new DefaultAsyncHttpClientConfig.Builder().setUseOpenSsl(true)
					.setSslEngineFactory(createSslEngineFactory()).build();
			this.client = new DefaultAsyncHttpClient(config);
			websocket = createWebSocket(url);
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			logger.error("connect to server failed!");
			e.printStackTrace();
		}

	}

	/**
	 * 创建websocket连接
	 * 
	 * @param url
	 *            websocket服务url
	 * @return websocket连接
	 */
	private WebSocket createWebSocket(String url) {
		try {
			websocket = client.prepareGet(url)
					.execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {

						@Override
						public void onMessage(String message) {
							receiveCount++;
							resultText = message;
							stringBuilder.append(message).append("\n");
						}

						@Override
						public void onOpen(WebSocket websocket) {
						}

						@Override
						public void onClose(WebSocket websocket) {
							client.close();
						}

						@Override
						public void onError(Throwable t) {
							client.close();
						}
					}).build()).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			logger.error("create websocket failed!");
			e.printStackTrace();
		}
		return websocket;

	}

	/**
	 * 返回SslEngineFactory实例
	 * 
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	private SslEngineFactory createSslEngineFactory() throws KeyManagementException, NoSuchAlgorithmException {
		return new JsseSslEngineFactory(NaiveSSLContext.createIgnoreVerifySSL());
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 *            消息内容
	 * @param waitTime
	 *            等待响应时间
	 * @return 响应内容
	 */
	public String sendMessage(String message, int waitTime) {
		try {
			websocket.sendMessage(message);
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.resultText;
	}

	/**
	 * 发送消息，默认等待时间1秒
	 * 
	 * @param message
	 *            消息内容
	 * @return 响应内容
	 */
	public String sendMessage(String message) {
		return sendMessage(message, 1000);
	}

	/**
	 * 关闭websocket连接
	 */
	public void disconnect() {
		try {
			Thread.sleep(1000);
			this.websocket.close();
		} catch (IOException | InterruptedException e) {
			logger.error("close websocket failed!");
			e.printStackTrace();
		}
	}

	/**
	 * 返回接收响应内容
	 * 
	 * @return
	 */
	public String getAllResult() {
		if (websocket.isOpen()) {
			disconnect();
		}
		return stringBuilder.toString();
	}

	/**
	 * 返回接收响应次数
	 * 
	 * @return
	 */
	public long getReceiveCount() {
		if (websocket.isOpen()) {
			disconnect();
		}
		return this.receiveCount;
	}

	public static void main(String[] args) throws InterruptedException {
		AsyncWebSocketService service2 = new AsyncWebSocketService("wss://10.5.31.138:8843/wss");
		service2.sendMessage(
				"{\"command\": \"addGroup\",\"body\": {\"groupId\": \"99-11-29\",\"userId\": \"abc\",\"userType\": \"commom-user\",\"userToken\": \"Uz9/HTr9Su9/FUcfpF5BTY1wHaLPkighzBSyoUxyOO4=\"}}");
		service2.sendMessage(
				"{\"command\": \"groupChat\",\"body\": {\"groupId\": \"99-11-29\",\"message\": {\"type\": \"text\",\"content\":\"群发消息\"}}}");
		Thread.sleep(2000);
		service2.disconnect();
		String teString2 = service2.getAllResult();
		System.out.println(teString2);
		System.out.println(teString2.split("\n").length);
		System.out.println(service2.getReceiveCount());
	}
}
