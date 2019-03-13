package com.mjitech.qa.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * http协议方法封装
 * 
 * @author android
 * @date 2018-05-17
 **/
public class HttpMethod {
	public BufferedReader getresult;
	public BufferedReader postresult;
	@SuppressWarnings("deprecation")
	static HttpClient hc = new DefaultHttpClient();
	static HttpResponse hr;
	static HttpEntity entity;
	StringBuilder strB = new StringBuilder();
	JSONObject json = JSONObject.fromObject("{}");
	String line;
	List<NameValuePair> list = new ArrayList<NameValuePair>();

	// get 请求 返回字符串
	public String httpget(String url) throws ClientProtocolException, IOException {
		HttpGet hg = new HttpGet(url);

		hr = hc.execute(hg);
		entity = hr.getEntity();
		String rev = EntityUtils.toString(entity);
		hg.abort();
		return rev;
	}

	// get 请求 返回json字符串
	public JSONObject httpgetReturnJson(String url) throws ClientProtocolException, IOException {

		HttpGet hg = new HttpGet(url);

		hr = hc.execute(hg);
		entity = hr.getEntity();
		String rev = EntityUtils.toString(entity);
		hg.abort();

		JSONObject json = null;
		try {
			json = JSONObject.fromObject(rev);
		} catch (Exception e) {
			json = new JSONObject();
			System.out.println("返回不是json数据：" + rev);
		}

		return json;
	}

	// post 请求 返回字符串
	public String httppost(String url, List<NameValuePair> nvps) throws ClientProtocolException, IOException {
		HttpPost hp = new HttpPost(url);
		hp.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		hr = hc.execute(hp);
		entity = hr.getEntity();
		BufferedReader postresult = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

		// 清空 strb
		strB.delete(0, strB.length());
		line = null;
		while ((line = postresult.readLine()) != null) {

			strB.append(line);
		}
		hp.abort();

		return strB.toString();
	}

	// post 请求 直接发送json串
	public String httppostJson(String url, String json) throws ClientProtocolException, IOException {
		HttpPost hp = new HttpPost(url);
		StringEntity entity = new StringEntity(json, "utf-8");// 解决中文乱码问题
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		hp.setEntity(entity);
		hr = hc.execute(hp);
		// entity = hr.getEntity();
		BufferedReader postresult = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

		// 清空 strb
		strB.delete(0, strB.length());
		line = null;
		while ((line = postresult.readLine()) != null) {

			strB.append(line);
		}
		hp.abort();

		return strB.toString();
	}

	// post 请求 返回json字符串
	public JSONObject httppostReturnJson(String url, List<NameValuePair> nvps)
			throws ClientProtocolException, IOException {
		HttpPost hp = new HttpPost(url);
		hp.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		hr = hc.execute(hp);
		entity = hr.getEntity();
		BufferedReader postresult = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

		// 清空 strb
		strB.delete(0, strB.length());
		line = null;
		while ((line = postresult.readLine()) != null) {

			strB.append(line);
		}
		hp.abort();
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(strB.toString());
		} catch (Exception e) {
			json = new JSONObject();
			System.out.println("返回不是json数据：" + strB.toString());
		}

		return json;
	}

	public List<NameValuePair> postParameter(JSONObject json) {
		list.clear();
		Iterator<String> keys = json.keys();
		String key;
		while (keys.hasNext()) {
			key = keys.next();
			list.add(new BasicNameValuePair(key, json.get(key).toString()));
		}

		return list;
	}

	// get 参数组装
	public String getParameter(JSONObject json) {

		Iterator<String> keys = json.keys();
		String key;
		strB.delete(0, strB.length());
		while (keys.hasNext()) {
			key = keys.next().toString();
			strB.append("&" + key + "=" + json.get(key));
		}
		strB.replace(0, 1, "?");

		return strB.toString();
	}

	// 将 json key 排序 组成新的json
	public JSONObject sortJson(JSONObject json) {
		JSONObject json2 = new JSONObject();
		Iterator<String> keys = json.keys();
		String key;
		while (keys.hasNext()) {

		}
		return json2;
	}

	public void clearJson(JSONObject json) {
		Iterator<String> keys = json.keys();
		String key = null;
		while (keys.hasNext()) {
			key = keys.next().toString();
			keys.remove();
		}

	}

	public List<NameValuePair> postJsonParameter(String args[]) {

		list.clear();
		for (int i = 0; i < args.length; i++) {
			list.add(new BasicNameValuePair(args[i].toString(), args[i].toString()));
		}
		return list;
	}

	// 参数sign加密
	public String getSign(JSONObject json, String secret) throws Exception {
		String strs = sort(json);
		String strss = secret + strs + secret;
		// System.out.println("md5加密前参数组合："+strss);
		String sign = Md5.byte2hex(Md5.md5(strss)); // md5加密并转换为大写
		// System.out.println("md5 加密：" + sign);
		return sign;

	}

	// 参数排序
	public String sort(JSONObject json) throws Exception {
		Iterator keys = json.keys();
		String key;
		StringBuffer sbf = new StringBuffer();
		sbf.append("");
		while (keys.hasNext()) {
			key = keys.next().toString();
			sbf.append(key + ",");
		}
		sbf.append("");
		String str[] = sbf.toString().split(",");
		Arrays.sort(str);
		// 清空buf 重新装
		sbf.delete(0, sbf.length());
		for (int i = 0; i < str.length; i++) {
			sbf.append("" + str[i] + "" + json.getString(str[i]));
		}

		return sbf.toString();
	}

	public JSONObject getjson(String url, List<NameValuePair> nvps) {

		return json;

	}

	// 判断是否为数字
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	// 判断是否为手机号 1开头
	public boolean isMobiles(String mobiles) {
		Pattern p = Pattern.compile("1\\d{10}");
		Matcher m = p.matcher(mobiles);
		boolean b = m.matches();
		return b;
	}

}
