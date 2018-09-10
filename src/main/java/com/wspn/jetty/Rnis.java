package com.wspn.jetty;

public class Rnis {
	private int rsrp;
	private int rssnr;
	private String imsi;
	
	public Rnis() {
	}
	public int getRsrp() {
		return rsrp;
	}
	public void setRsrp(int rsrp) {
		this.rsrp = rsrp;
	}
	public int getRssnr() {
		return rssnr;
	}
	public void setRssnr(int rssnr) {
		this.rssnr = rssnr;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
}
