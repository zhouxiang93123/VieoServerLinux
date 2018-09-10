package com.wspn.jetty;

public class User {
	int state;
	int action;
	int rate;
	int channel;
	int request;
	
	int qLevel;
	int bufLevel;
	int bwLevel;
	int bwLevel2;
	int bwLevel3;
	int switches;
	
	

	String ip;
	double speedAction;
	int  speed;
	int speed2;
	int speed3;
	int buffer;
	int Rsrp=0;
	int qoe;
	int bufferLevel;
	
	int action2;
	
	int from;
	
	
	public int getBwLevel3() {
		return bwLevel3;
	}

	public void setBwLevel3(int bwLevel3) {
		this.bwLevel3 = bwLevel3;
	}

	public int getSpeed3() {
		return speed3;
	}

	public void setSpeed3(int speed3) {
		this.speed3 = speed3;
	}

	public int getSpeed2() {
		return speed2;
	}

	public void setSpeed2(int speed2) {
		this.speed2 = speed2;
	}

	public int getBwLevel2() {
		return bwLevel2;
	}

	public void setBwLevel2(int bwLevel2) {
		this.bwLevel2 = bwLevel2;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getAction2() {
		return action2;
	}

	public void setAction2(int action2) {
		this.action2 = action2;
	}

	public int getBufferLevel() {
		return bufferLevel;
	}

	public void setBufferLevel(int bufferLevel) {
		this.bufferLevel = bufferLevel;
	}

	public int getSwitches() {
		return switches;
	}

	public void setSwitches(int switches) {
		this.switches = switches;
	}
	
	public int getqLevel() {
		return qLevel;
	}

	public void setqLevel(int qLevel) {
		this.qLevel = qLevel;
	}

	public int getBufLevel() {
		return bufLevel;
	}

	public void setBufLevel(int bufLevel) {
		this.bufLevel = bufLevel;
	}

	public int getBwLevel() {
		return bwLevel;
	}

	public void setBwLevel(int bwLevel) {
		this.bwLevel = bwLevel;
	}

	
	


	public int getQoe() {
		return qoe;
	}

	public void setQoe(int qoe) {
		this.qoe = qoe;
	}

	public int getRsrp() {
		return Rsrp;
	}

	public void setRsrp(int rsrp) {
		Rsrp = rsrp;
	}

	public double getSpeedAction() {
		return speedAction;
	}

	public void setSpeedAction(double speedAction) {
		this.speedAction = speedAction;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getRequest() {
		return request;
	}

	public void setRequest(int request) {
		this.request = request;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}
	
}
