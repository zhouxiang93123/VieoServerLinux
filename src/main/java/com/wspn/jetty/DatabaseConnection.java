package com.wspn.jetty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	// JDBC 驱动名及数据库 URL
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/mec?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&useSSL=false";

	// 数据库的用户名与密码，需要根据自己的设置
	static final String USER = "root";
	static final String PASS = "zx93123";

	private static Connection conn = null;

	public static Connection getCon() {

		try {
			// 注册 JDBC 驱动
			Class.forName("com.mysql.cj.jdbc.Driver");
			// 打开链接
			System.out.println("连接数据库...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("连接数据库失败");
			e.printStackTrace();
		}
		return conn;
	}
}
