package com.aaron.testcase;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.aaron.base.NaiveSSLContext;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

public class NVWebsocket {

	public static void main(String[] args) throws IOException, WebSocketException, InterruptedException,
			NoSuchAlgorithmException, KeyManagementException {
		WebSocket nvWebSocket = new WebSocketFactory().setConnectionTimeout(5)
				.setSSLContext(NaiveSSLContext.createIgnoreVerifySSL()).createSocket("wss://10.5.224.201:9843/wss")
				.addListener(new WebSocketAdapter() {
					public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
						System.out.println("onStateChanged");
					}

					public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
						System.out.println("onConnected");
					}

					public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
						System.out.println("onConnectError");
					}

					public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
							WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
						System.out.println("onDisconnected");
					}

					public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onFrame");
					}

					public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onContinuationFrame");
					}

					public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onTextFrame");
					}

					public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onBinaryFrame");
					}

					public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onCloseFrame");
					}

					public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onPingFrame");
					}

					public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onPongFrame");
					}

					public void onTextMessage(WebSocket websocket, String text) throws Exception {
						System.out.println("onTextMessage:" + text);
					}

					public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
						System.out.println("onBinaryMessage");
					}

					public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onSendingFrame");
					}

					public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onFrameSent");
					}

					public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
						System.out.println("onFrameUnsent");
					}

					public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
						System.out.println("onError");
					}

					public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
							throws Exception {
						System.out.println("onFrameError");
					}

					public void onMessageError(WebSocket websocket, WebSocketException cause,
							List<WebSocketFrame> frames) throws Exception {
						System.out.println("onMessageError");
					}

					public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause,
							byte[] compressed) throws Exception {
						System.out.println("onMessageDecompressionError");
					}

					public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data)
							throws Exception {
						System.out.println("onTextMessageError");
					}

					public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
							throws Exception {
						System.out.println("onSendError");
					}

					public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
						System.out.println("onUnexpectedError");
					}

					public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
						System.out.println("handleCallbackError");
					}

					public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers)
							throws Exception {
						System.out.println("onSendingHandshake");
					}
				}).addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
		nvWebSocket.connect();
		nvWebSocket.sendText(
				"{\"command\":\"online\",\"body\":{\"userToken\":\"Uz9/HTr9Su9/FUcfpF5BTY1wHaLPkighzBSyoUxyOO4=\",\"clientType\":\"web\",\"deviceName\":\"iphone7s\"}}");
		nvWebSocket.sendText(
				"{\"command\": \"addGroup\",\"body\": {\"groupId\": \"99-11-29\",\"userId\": \"abc\",\"userType\": \"commom-user\",\"userToken\": \"Uz9/HTr9Su9/FUcfpF5BTY1wHaLPkighzBSyoUxyOO4=\"}}");
		nvWebSocket.sendText(
				"{\"command\": \"broadcastMessage\",\"body\": {\"groupId\": \"99-11-29\",\"message\": {\"groupId\":\"哈哈哈 99-11-23\"}}}");
		// nvWebSocket.sendPing();
		Thread.sleep(60000);
		nvWebSocket.sendText(
				"{\"command\": \"broadcastMessage\",\"body\": {\"groupId\": \"99-11-29\",\"message\": {\"groupId\":\"test 99-11-23\"}}}");
		Thread.sleep(10000);
		nvWebSocket.disconnect();
	}
}
