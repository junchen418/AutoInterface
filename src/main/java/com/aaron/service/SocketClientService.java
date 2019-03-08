package com.aaron.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketClientService {

	private final String DEFAULT_CHARSET = "UTF-8";
	private String address = null;
	private int port = 0;
	private int timeout = 5000;
	private char end = '\0';

	/**
	 * 构造方法
	 * 
	 * @param address 服务器ip
	 * @param port    服务器port
	 * @param timeout 指定超时时间，单位毫秒
	 * @param end     消息结束标志
	 */
	public SocketClientService(String address, int port, int timeout, char end) {
		this.address = address;
		this.port = port;
		this.timeout = timeout;
		this.end = end;
	}

	/**
	 * 构造方法
	 * 
	 * @param address 服务器ip
	 * @param port    服务器port
	 */
	public SocketClientService(String address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * 发送消息
	 * 
	 * @param message 消息内容
	 * @return 返回接收到的消息
	 */
	public String sendMessage(String message) {
		String result = null;
		Socket socket = null;
		SocketChannel channel = null;
		try {
			Selector selector = Selector.open();
			channel = getSocketChannel(address, port);
			socket = channel.socket();
			// socket.setReuseAddress(true);
			// socket.setSoLinger(true, 0);
			socket.setSoTimeout(timeout);
			byte[] messageBytes = (message + end).getBytes(DEFAULT_CHARSET);
			ByteBuffer messageBuffer = ByteBuffer.wrap(messageBytes);
			channel.write(messageBuffer);
			channel.register(selector, SelectionKey.OP_READ);
			ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
			while (true) {
				if (selector.select(timeout) == 0) {
					result = new String(receiveBuffer.array(), DEFAULT_CHARSET);
					break;
				}
				Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
				while (keyIter.hasNext()) {
					SelectionKey key = keyIter.next();
					if (key.isReadable()) {
						((SocketChannel) key.channel()).read(receiveBuffer);
					}
					keyIter.remove();
				}
				String results = new String(receiveBuffer.array(), DEFAULT_CHARSET);
				int index = results.indexOf(end);
				if (index != -1) {
					result = results.substring(0, index);
					break;
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if ((socket != null && socket.isConnected()) || (channel != null && channel.isConnected())) {
					socket.close();
					channel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 返回socket channel
	 * 
	 * @param address
	 * @param port
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private SocketChannel getSocketChannel(String address, int port) throws UnknownHostException, IOException {
		SocketChannel socketChan = SocketChannel.open();
		socketChan.configureBlocking(false);
		if (!socketChan.connect(new InetSocketAddress(address, port))) {
			while (!socketChan.finishConnect()) {
			}
		}
		return socketChan;
	}

	public static void main(String[] args) {
		String message = "{\"command\":\"task_progress_request\",\"task_type\":\"video_task\",\"id\":\"0xfffffff\"}";
		SocketClientService clientService = new SocketClientService("10.5.233.33", 6665);
		String result = clientService.sendMessage(message);
		System.out.println(result);
	}

}
