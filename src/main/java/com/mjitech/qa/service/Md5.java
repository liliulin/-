package com.mjitech.qa.service;

import java.security.MessageDigest;

/**
 * 为加密字段预留功能
 * 
 * @author android
 * @date 2018-05-23
 */
public class Md5 {
	public static String digist(String text) {
		String result = "";

		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			// 定义编码方式
			byte[] bufs = text.getBytes("UTF-8");
			md.update(bufs);
			byte[] b = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static byte[] md5(String text) {
		byte[] returnByte = null;

		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			// 定义编码方式
			returnByte = md.digest(text.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnByte;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}
}
