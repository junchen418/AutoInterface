package com.aaron.testcase;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

import com.aaron.service.HttpService;
import org.apache.http.HttpResponse;

import com.aaron.encrypt.Base64Util;
import com.jcraft.jsch.HASH;
import com.zf.zson.ZSON;
import com.zf.zson.result.ZsonResult;

public class ZsonTest {

	public static void main(String[] args) throws IOException {
		long start = 0L;
		long end = 0L;
		HashMap<String, String> bodyMap = new HashMap<String, String>();
		bodyMap.put("clientId", "autotest");
		bodyMap.put("validateCode", "3f1be864fb3d45ce98898c57e2d5293f");
		bodyMap.put("areaCode", "8000");
		bodyMap.put("validateFlag", "false");
		bodyMap.put("original", "中国人.jpg");
		bodyMap.put("param", "");
		File file = new File("D:\\测试执行\\testdata\\图片\\jpg\\0.jpg");
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
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
		HttpService uploadSegment = new HttpService(false);
		bodyMap.put("base64Code", Base64Util.encode(bos.toByteArray()));
		uploadSegment.post("http://10.5.32.212:8080/reserver/file/image/base64/upload", bodyMap);
		
	}
}
