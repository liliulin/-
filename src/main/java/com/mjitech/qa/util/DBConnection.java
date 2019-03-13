package com.mjitech.qa.util;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

/**
 * 数据库连接工具
 * 
 * @author gangwang
 * @date 2018
 */
public class DBConnection {
	String driver = "com.mysql.jdbc.Driver";
	String test_url = "jdbc:mysql://139.129.108.180:3306/maxbox_test";
	String test_user = "root";
	String test_password = "Mjitech20!6";

	String online_url = "jdbc:mysql://test.mjitech.com:3306/maxbox";
	String online_user = "root";
	String online_password = "Mjitech20!6";

	public Connection conn;

	/**
	 * 连接数据库
	 */
	public DBConnection(String env) {
		try {
			Class.forName(driver); // 加载驱动
			if (env.equalsIgnoreCase("online")) {
				conn = (Connection) DriverManager.getConnection(online_url, online_user, online_password);
				if (!conn.isClosed()) {
					System.out.println("online Succeeded connecting to the Database!");
				}
			} else {
				conn = (Connection) DriverManager.getConnection(test_url, test_user, test_password);
				if (!conn.isClosed()) {
					System.out.println("TestEnv Succeeded connecting to the Database!");
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		try {
			this.conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBConnection con = new DBConnection("online");
	}
}
