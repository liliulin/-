package com.mjitech.qa.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
//import com.mjitech.qa.util.Logger;

/**
 * 初始化服务
 * 
 * @author Android
 * @date 2018-05-16
 */
public class InitProperties {
	public static final String PFILEPATH = File.separatorChar + "resources" + File.separatorChar + "config"
			+ File.separatorChar + "config.properties";
	private Properties config = new Properties();
	public static Map<String, String> mapproperties = new HashMap<String, String>();

	public InitProperties() {
		// 构造初始配置文件
		init();
	}

	private void init() {
		String configPath = System.getProperty("user.dir") + PFILEPATH;
		File file = new File(configPath);
	//	Logger.log("加载配置文件%s", configPath);
		InputStreamReader fn = null;
		if (file.exists()) {
			try {
				fn = new InputStreamReader(new FileInputStream(configPath), "UTF-8");
				config.load(fn);
				if (!config.isEmpty()) {
					Set<Object> keys = config.keySet();
					for (Object key : keys) {
						InitProperties.mapproperties.put(key.toString(), config.getProperty(key.toString()));
						if (!System.getProperties().containsKey(key.toString())
								&& !config.getProperty(key.toString()).isEmpty()) {
							System.setProperty(key.toString(), config.getProperty(key.toString()));
						}
					}
					keys.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
