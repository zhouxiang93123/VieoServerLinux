package com.wspn.jetty;

public class start {
	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {
			public void run() {
				try {
					JettyServer.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					JettyServer2.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}).start();
	}
}
