package com.mjitech.qa.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpPostUtil {
	@SuppressWarnings("unused")
	private static final String APPLICATION_JSON = "application/json";
	@SuppressWarnings("unused")
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	// http请求连接参数默认值
	private static int connectionTimeOut = 15000;// 连接超时时长
	private static int readTimeOut = 25000;// 读取超时时长
	@SuppressWarnings("unused")
	private static int retryTimes = 3;// 重试次数

	public static String connPostRequest(String url, String content) {
		String result = "";
		try {
			URL postUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(connectionTimeOut);
			connection.setReadTimeout(readTimeOut);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();

			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(content);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true) {
				result = reader.readLine();
				if (null == result)
					break;
				return result;
			}
		} catch (MalformedURLException e) {
		} catch (ProtocolException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}
		return result;
	}

}
