package com.wspn.jetty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//rnisloyeeOperation类用于操作数据库，以下的编写方法是JAVA软件编程的一种设计模式，称为！！！！！ 单例模式，！！！！！！！
//方法中先判断该类的对象是否为空，只有为空才创建(new) ,因此保证该对象在程序中永远是唯一的，可以避免重复创建对象造成的系统内存被过多占用
public class RnisOperation {
	private static RnisOperation instance = null;

	public static RnisOperation getInstance() { // 返回rnisloyeeOperation类实例的静态方法,单例模式！！！！
		if (instance == null) {
			instance = new RnisOperation();
		}
		
		
		return instance;
	}

	public boolean saverRnis(Rnis rnis) { // 向数据库中加入数据
		boolean result = false;
		Connection conn = null;
		try {

			conn = DatabaseConnection.getCon(); // 建立数据库连接
			String sqlInset = "insert into rnis(rsrp, rssnr,imsi) values(?, ?, ?)";
			String sqlUpdate = "update rnis set rsrp=? ,rssnr=? where imsi=?";
			
			PreparedStatement stmt = conn.prepareStatement(sqlUpdate); // 会抛出异常

			stmt.setInt(1, rnis.getRsrp()); // 设置SQL语句第一个“？”的值
			stmt.setInt(2, rnis.getRssnr()); // 设置SQL语句第二个“？”的值
			stmt.setString(3, rnis.getImsi()); // 设置SQL语句第三个“？”的值
			int i = stmt.executeUpdate(); // 执行插入数据操作，返回影响的行数
			if (i == 1) {
				result = true;
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { // finally的用处是不管程序是否出现异常，都要执行finally语句，所以在此处关闭连接
			try {

				if (conn != null)
					conn.close(); // 打开一个Connection连接后，最后一定要调用它的close（）方法关闭连接，以释放系统资源及数据库资源
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	public List<Rnis> selectrnisloyee() { // 从数据库中查询所需数据
		List<Rnis> rnisList = new ArrayList<Rnis>();
		Connection conn = null;
		try {
			conn = DatabaseConnection.getCon();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from rnis");// 执行SQL并返回结果集
			while (rs.next()) {
				Rnis rnis = new Rnis();
				rnis.setRsrp(rs.getInt("rsrp")); // 从结果集rs中获取内容时，若为字符串类型的，用rs.getString("string")方法
				rnis.setRssnr(rs.getInt("rssnr")); // 其中str为想要从 数据库的 表 中获取的信息
				rnis.setImsi(rs.getString("imsi")); // 若为int类型，用rs.getInt(number);
				rnisList.add(rnis);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();// 关闭连接
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rnisList; // 返回结果
	}

	public boolean deleternisloyeeById(Rnis rnis) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DatabaseConnection.getCon();
			String sql = "delete from rnis where imsi = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, rnis.getImsi());
			int i = stmt.executeUpdate();
			if (i == 1) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

}