package com.mjitech.qa.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import com.mjitech.qa.util.Base64;

/***
 * 
 * 测试基础服务类
 * 
 * @author android
 * @date 2018-05-16
 *
 */
@SuppressWarnings("deprecation")
public class BaseService {
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
	YrdHtmlReport yhr = new YrdHtmlReport(); // 测试报告类

	// get 请求 返回字符串
	public String httpget(String url) throws ClientProtocolException, IOException {
		HttpGet hg = new HttpGet(url);
		hr = hc.execute(hg);
		entity = hr.getEntity();
		String rev = EntityUtils.toString(entity);
		hg.abort();
		return rev;
	}

	public String httpget(URI uri) throws ClientProtocolException, IOException {
		// HttpClient client = new DefaultHttpClient();
		HttpGet hg = new HttpGet(uri);
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
		json.clear();
		try {
			json = JSONObject.fromObject(strB.toString());
		} catch (Exception e) {
			json = new JSONObject();
			System.out.println("返回不是json数据：" + strB.toString());
		}

		return json;
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
			strB.append("&" + key + "=" + java.net.URLEncoder.encode(json.get(key).toString()));
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

	/**
	 * 发送json参数
	 */
	public List<NameValuePair> postJsonParameter(String args[]) {

		list.clear();
		for (int i = 0; i < args.length; i++) {
			list.add(new BasicNameValuePair(args[i].toString(), args[i].toString()));
		}
		return list;
	}

	/**
	 * 参数sign加密
	 */
	public String getSign(JSONObject json, String secret) throws Exception {
		String strs = sort(json);
		String strss = secret + strs + secret;
		System.out.println("md5加密前参数组合：" + strss);
		String sign = Md5.byte2hex(Md5.md5(strss)); // md5加密并转换为大写
		System.out.println("md5 加密：" + sign);
		return sign;

	}

	/**
	 * 参数排序
	 * 
	 * @param JSONObject
	 *            json json参数
	 */
	@SuppressWarnings("rawtypes")
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

	/**
	 * 判断是否为数字
	 * 
	 * @param String
	 *            str 输入的字符串
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断是否为手机号 1开头
	 * 
	 * @param String
	 *            mobiles 手机号
	 */
	public boolean isMobiles(String mobiles) {
		Pattern p = Pattern.compile("1\\d{10}");
		Matcher m = p.matcher(mobiles);
		boolean b = m.matches();
		return b;

	}

	/**
	 * 用例对比方法 将结果写入报告
	 * 
	 * @author String expected 期望结果
	 * @author String actual 实际结果
	 */
	int y = 0;

	public void assertEquals(String expected, String actual) {
		String checkResult = null;
		String beizhu = null; // 用例检查结果 、备注
		boolean flag = true;

		for (int i = 0; i < expected.split("#").length; i++) {
			// 比较第2 3 4列数据 接口状态、应答码、错误说明
			if (expected.split("#")[1].equals(actual.split("#")[0])) {

			} else {
				flag = false;

			}

			if (expected.split("#")[2].equals(actual.split("#")[1])) {

			} else {
				flag = false;

			}
			if (expected.split("#")[3].equals(actual.split("#")[2])) {

			} else {
				flag = false;

			}

		}

		// 3处对比是否全部通过，如果有一处失败则 用例失败
		y++;
		if (flag) {
			checkResult = y + "#" + expected.split("#")[4] + "#" + expected.split("#")[0] + "#" + expected.split("#")[1]
					+ "/" + actual.split("#")[0] + "#" + expected.split("#")[2] + "/" + actual.split("#")[1] + "#"
					+ expected.split("#")[3] + "/" + actual.split("#")[2] + "#" + "passed";
		} else {
			checkResult = y + "#" + expected.split("#")[4] + "#" + expected.split("#")[0] + "#" + expected.split("#")[1]
					+ "/" + actual.split("#")[0] + "#" + expected.split("#")[2] + "/" + actual.split("#")[1] + "#"
					+ expected.split("#")[3] + "/" + actual.split("#")[2] + "#" + "<font color=\"red\">failed</font>";
		}

		System.out.println(checkResult);
		yhr.addReport(checkResult);

	}

	/**
	 * 征信平台signCode加密算法
	 * 
	 * @param code
	 *            输入LoginName
	 */
	public static String getEncryCode(Object code) {
		String resEncry = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update((code + "28B32DEA25063C2226AF7D97CE6FAB").getBytes());
			byte b[] = md.digest();
			for (int i = 0; i < b.length; i++) {
				resEncry += (Integer.toHexString(b[i] & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resEncry;
	}

	/**
	 * Post数据公共方法---加密
	 * 
	 * @param url
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public String postUrl(String url, String jsonStr) throws Exception {
		// String inStr = DES.encryptDES(jsonStr,
		// Configuration.getValue("APP_KEY"));
		System.out.println("jsonStr加密之前：" + jsonStr);
		String content = "json=" + URLEncoder.encode(jsonStr, "UTF-8");
		System.out.println("加密之后content:" + content);
		String result = HttpPostUtil.connPostRequest(url, content);
		return result;
	}
	
	    // post 请求 返回字符串
		/**
		 * 不带参数的post请求。
		 * @author android
		 * @date 2018年5月30
		 * */
		public String httppost(String url)
				throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
			//hp.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			hr = hc.execute(hp);
			entity = hr.getEntity();
			BufferedReader postresult = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"));

			// 清空 strb
			strB.delete(0, strB.length());
			line = null;
			while ((line = postresult.readLine()) != null) {

				strB.append(line);
			}
			hp.abort();

			return strB.toString();
		}

	/**
	 * 对字节数组字符串进行Base64解码并生成图片
	 * 
	 * @param imgStr
	 *            图片字符串
	 * @param imgFilePath
	 *            生成图片的路径
	 */
	public boolean GenerateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		// BASE64Decoder decoder = new BASE64Decoder();
		Base64 decoder = new Base64();
		try {
			// Base64解码
			@SuppressWarnings("static-access")
			byte[] bytes = decoder.decode(imgStr);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			System.out.println(imgFilePath);
			// 生成jpeg图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(bytes);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	/**
	 * 银行id 1 到20的随机数 排除2，没有2的银行id
	 */
	public String getBankId() {
		int random = new Random().nextInt(19) + 1;
		if (random == 2) {
			random += 1;
		}
		String bankId = random + "";
		return bankId;
	}
	
	/**
	* 不带参数的postHeade请求。
	* @author android
	* @date 2018年5月30
	* */
	public String httppostHeader(String url,String employeeid,String token,String body)
			throws ClientProtocolException, IOException {
		HttpPost hp = new HttpPost(url);
		hp.setHeader("Content-Type", "application/json");
		hp.setHeader("employeeid", employeeid);
		hp.setHeader("token",token);	
		//String body = "{\"type\":\"ST\"}";
		hp.setEntity(new StringEntity(body));
		hr = hc.execute(hp);
		entity = hr.getEntity();
		BufferedReader postresult = new BufferedReader(new InputStreamReader(
			entity.getContent(), "UTF-8"));
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
		public JSONObject httppostHeaderReturnJson(String url,String employeeid,String token,String body)
				throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
			hp.setHeader("Content-Type", "application/json");
			hp.setHeader("employeeid", employeeid);
			hp.setHeader("token",token);	
			//String body = "{\"type\":\"ST\"}";			
			hp.setEntity(new StringEntity(body));			
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
			json.clear();
			try {
				json = JSONObject.fromObject(strB.toString());
			} catch (Exception e) {
				json = new JSONObject();
				System.out.println("返回不是json数据：" + strB.toString());
			}

			return json;
		}
		
		/**
		 * post 添加购物车请求。
		 * @return josn
		 * */	
		public JSONObject httppostCartReturnJson(String url, List<NameValuePair> nvps)
				throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
			hp.setHeader("Content-Type", "application/json");
			hp.setHeader("Content-Type", "application/x-www-form-urlencoded");
			hp.setHeader("mjitech-machine-cert", "TWppdGVjaDIwMTY=");
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
			json.clear();
			try {
				json = JSONObject.fromObject(strB.toString());
			} catch (Exception e) {
				json = new JSONObject();
				System.out.println("返回不是json数据：" + strB.toString());
			}

			return json;
		}
		
		/**
		 * post 27寸屏幕
		 * @param url 访问地址
		 * @param nvps 参数集合
		 * @author gangwang
		 * @return josn
		 * */	
		public JSONObject httppostCartReturnJson27(String url, List<NameValuePair> nvps)
				throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
			hp.setHeader("Content-Type", "application/x-www-form-urlencoded");
			hp.setHeader("mjitech-machine-cert", "TWppdGVjaDIwMTY=");
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
			json.clear();
			try {
				json = JSONObject.fromObject(strB.toString());
			} catch (Exception e) {
				json = new JSONObject();
				System.out.println("返回不是json数据：" + strB.toString());
			}

			return json;
		}
		
		/**
		 * post 添加购物车，支付回调。
		 * @return josn
		 * */	
		public JSONObject httppostPayCallBackJson(String url, List<NameValuePair> nvps)
				throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
		//	hp.setHeader("Content-Type", "application/json");
			hp.setHeader("Content-Type", "X-WWW-FORM-URLENCODED");
			hp.setHeader("token","f3Yaw!fay*f234^f1opUh5");
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
			json.clear();
			try {
				json = JSONObject.fromObject(strB.toString());
			} catch (Exception e) {
				json = new JSONObject();
				System.out.println("返回不是json数据：" + strB.toString());
			}

			return json;
		}
		
		
		/**
		 * 支付回调服务接口
		 * @param String url  接口地址
		 * @param String body body类型参数
		 * @author gangwang@mjitech.com
		 * @date 2018-06-20
		 * 
		 * */
		public JSONObject httppostPayCall(String url,String body)
			throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
			hp.setHeader("Content-Type", "application/json");
			hp.setHeader("token","f3Yawfayf234f1opUh5");	
					//String body = "{\"type\":\"ST\"}";			
			hp.setEntity(new StringEntity(body));			
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
					json.clear();
					try {
						json = JSONObject.fromObject(strB.toString());
					} catch (Exception e) {
						json = new JSONObject();
						System.out.println("返回不是json数据：" + strB.toString());
					}

					return json;
			}	
		
		/**
		 * 平板需求服务方法
		 * @param String url  接口地址
		 * @param String body body类型参数
		 * @author gangwang@mjitech.com
		 * @date 2018-07-26
		 * 
		 * */
		public JSONObject httpPostFlat(String url,String body)
			throws ClientProtocolException, IOException {
			HttpPost hp = new HttpPost(url);
			hp.setHeader("Content-Type", "application/json");
			hp.setHeader("mjitech-machine-cert", "TWppdGVjaDIwMTY=");
					//String body = "{\"type\":\"ST\"}";			
			hp.setEntity(new StringEntity(body));			
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
					json.clear();
					try {
						json = JSONObject.fromObject(strB.toString());
					} catch (Exception e) {
						json = new JSONObject();
						System.out.println("返回不是json数据：" + strB.toString());
					}

					return json;
			}	
		
		/**
		 * 图片上传接口
		 * @param  url 上传URL
		 * @param  fileType  文件类型
		 * */
//		public String uploadImageList(String url,String file)
//				throws ClientProtocolException, IOException {
//			HttpClient httpclient = HttpClientBuilder.create().build();
//			HttpPost httppost = new HttpPost(url);
//			HttpEntity req = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//					.addPart("filename", new FileBody(new File(System.getProperty("user.dir") + "\\imageLib\\card.jpg"))).build();
//				//	.addTextBody("files", file).build();
//					
//
//
//			System.out.println("executing request: " + httppost.getRequestLine());
//
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity re = response.getEntity();
//			System.out.println(response.getStatusLine());
//			BufferedReader postresult = new BufferedReader(new InputStreamReader(re.getContent()));
//			StringBuilder strB = new StringBuilder();
//			String line = null;
//			if ((line = postresult.readLine()) != null) {
//				strB.append(line);
//			}
//			EntityUtils.consume(re);
//
//			return strB.toString();
//		}
			/**
			 * 图片上传接口
			 * @param  url 上传URL
			 * @author gangwang
			 * @date 2018-08-08
			 * */
		   public String uploadImageList(String url){ 
			   String status = "" ;
		       HttpClient client=new DefaultHttpClient();  
		       HttpPost httpPost=new HttpPost(url);//通过post传递  
		       /**绑定数据  这里需要注意  如果是中文  会出现中文乱码问题 但只要按设置好*/  
		       MultipartEntity muit=new MultipartEntity();  
		       // 上传 文本， 转换编码为utf-8 其中"text" 为字段名，  
		       //Charset.forName(CHARSET)为参数值,可设置为UTF-8，其实就是正常的值转换成utf-8的编码格式  
		       // 后边new StringBody(text,Charset.forName(CHARSET))  
		       File parent = new File(System.getProperty("user.dir") + "\\imageLib\\");
		       File fileupload=new File(parent,"card.jpg");  
		       FileBody fileBody=new FileBody(fileupload);  
		       muit.addPart("files",fileBody);  
		       httpPost.setEntity(muit);  
		       /**发送请求*/  
		       try {  
		           HttpResponse response=client.execute(httpPost);  
		           //判断是否上传成功  返回200  
		           if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){  
		             //  System.out.println(EntityUtils.toString(response.getEntity()));  
		        	   status = EntityUtils.toString(response.getEntity());
		           }  
		       } catch (ClientProtocolException e){  
		           e.printStackTrace();  
		       } catch (IOException e) {  
		           e.printStackTrace();  
		       }  
		       
		       return status;
		  
		   }  
		
		
		
}
