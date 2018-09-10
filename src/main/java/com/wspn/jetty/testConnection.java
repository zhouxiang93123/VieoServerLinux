package com.wspn.jetty;

import java.util.List;

public class testConnection {
	public static void main(String[] args) {
		
		Rnis rnis = new Rnis();
		rnis.setRsrp(1);
		rnis.setRssnr(1);
		rnis.setImsi("1");
		boolean res = RnisOperation.getInstance().saverRnis(rnis);
		if (res == true) {
			System.out.println("向rnis表中插入数据成功");
		} else {
			System.out.println("向rnis表中插入数据失败");
		}
	}
}
