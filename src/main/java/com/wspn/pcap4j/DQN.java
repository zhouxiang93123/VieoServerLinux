package com.wspn.pcap4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.tensorflow.Tensor;

import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;
import com.wspn.jetty.MyGraph;
import com.wspn.jetty.MyProxyServlet2;
import com.wspn.jetty.RL;
import com.wspn.jetty.RLUser;
import com.wspn.jetty.User;

public class DQN {

	class MyThread {
		public void run() throws InterruptedException {
			while (true) {
				setSpeed(getUser());
				Thread.sleep(500);
			}
		}
	}

	String fileName = null;
	User user = new User();
	String ip = null;
	boolean isReady = false;
	File file = null;
	FileWriter fw = null;
	boolean pauseFlag = false;

	long pauseTime = 0;
	long stopToalTime = 0;
	long startDownload = 0;
	long endDownload = 0;

	int qLevel = 3;
	int bufLevel = 5;
	int bwLevel = 15;
	int userNum = 1;
	double[][] q;



	public long getStartDownload() {
		return startDownload;
	}

	public void setStartDownload(long startDownload) {
		this.startDownload = startDownload;
	}

	public long getEndDownload() {
		return endDownload;
	}

	public void setEndDownload(long endDownload) {
		this.endDownload = endDownload;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isPauseFlag() {
		return pauseFlag;
	}

	public void setPauseFlag(boolean pauseFlag) {
		this.pauseFlag = pauseFlag;
	}

	public long getPauseTime() {
		return pauseTime;
	}

	public void setPauseTime(long pauseTime) {
		this.pauseTime = pauseTime;
	}

	public long getStopToalTime() {
		return stopToalTime;
	}

	public void setStopToalTime(long stopToalTime) {
		this.stopToalTime = stopToalTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public void start() throws InterruptedException {
		q = new double[qLevel][userNum * qLevel * bufLevel * bwLevel];
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
		//readFile("C:\\Users\\Administrator\\workspace\\zx\\logs\\q1");
		readFile("/home/wspn/git/videoServer/logs/q1");
		show();
	//	file = new File("C:\\Users\\Administrator\\workspace\\zx\\logs\\" + ft.format(dNow) + "_" + getIp() + ".txt");
		file = new File("/home/wspn/git/videoServer/logs/" + ft.format(dNow) + "_" + getIp() + ".txt");
		// MyThread myThread = new MyThread();
		// myThread.run();
	}

	public void setSpeed(User user) {
		// String url1 = "http://10.108.146.152:8088/ssmStudy/query?ip=172.16.0.2";
		String url1 = "http://192.168.111.3:8088/ssmStudy/query?ip=" + user.getIp();
		String result1 = HttpUtil.sendGet(url1);
		// System.err.println(result1);
		// TODO
		JSONObject jsonObject = new JSONObject(result1);
		JSONArray jsonArray = jsonObject.getJSONArray("result");
		double sum2aveV = 0.0;
		double second = 0.0;
		double fin = 0.0;
		int num = 0;
		for (int i = 0; i < jsonArray.length(); i++) {
			double tmp = 0.0;
			JSONObject jsonObject2 = jsonArray.getJSONObject(i);
			tmp = jsonObject2.getDouble("bandwidth");
			if (tmp > sum2aveV) {
				sum2aveV = tmp;
			}
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			double tmp = 0.0;
			JSONObject jsonObject2 = jsonArray.getJSONObject(i);
			tmp = jsonObject2.getDouble("bandwidth");
			if (tmp > second && tmp < sum2aveV) {
				second = tmp;
			}
		}
		if (sum2aveV - second < 2500) {
			fin = (sum2aveV + second) / 2;
		} else {
			fin = sum2aveV;
		}
		fin = fin / 1.1;
		int vel = (int) (fin * 8.0 / 1024.0);
		user.setSpeed(vel);
		// System.err.println("从MEC获取的速度为：" + vel);

		if (vel >= 15000) {
			user.setBwLevel(15);
		} else if (vel < 1000) {
			user.setBwLevel(1);
		} else {
			user.setBwLevel(vel / 1000);
		}

	}

	public void setBandWidth() {
		LinkedList<Integer> linkedList = MyProxyServlet2.getRnis.getMapResult().get("172.16.0.2").getQueue();
		System.out.println(linkedList.toString());
		int sum = 0;
		int count=0;
		for (int i = 0; i < linkedList.size(); i++) {
			if(linkedList.get(i)>250) {
				sum += linkedList.get(i);
				count++;
			}
		}
		User user = getUser();
		int bandWidth = (int) (sum / count);
		user.setSpeed(bandWidth);
		if (bandWidth >= 15000) {
			user.setBwLevel(15);
		} else if (bandWidth < 1000) {
			user.setBwLevel(1);
		} else {
			user.setBwLevel((int) bandWidth / 1000);
		}
	}

	public void setBandWidth2(double bandWidth) {
		User user = getUser();
		user.setSpeed2(Double.valueOf(bandWidth).intValue());
		if (bandWidth >= 15000) {
			user.setBwLevel2(15);
		} else if (bandWidth < 1000) {
			user.setBwLevel2(1);
		} else {
			user.setBwLevel2((int) bandWidth / 1000);
		}
	}

	public void setBandWidth3(double bandWidth) {
		User user = getUser();
		user.setSpeed3(Double.valueOf(bandWidth).intValue());
		if (bandWidth >= 15000) {
			user.setBwLevel3(15);
		} else if (bandWidth < 1000) {
			user.setBwLevel3(1);
		} else {
			user.setBwLevel3((int) bandWidth / 1000);
		}
	}

	public void setBuffer(double buffer) {
		User user = getUser();
		user.setBuffer((int) buffer);
		if (buffer >= 16.0) {
			user.setBufLevel(5);
		} else if (buffer >= 12.0) {
			user.setBufLevel(4);
		} else if (buffer >= 8.0) {
			user.setBufLevel(3);
		} else if (buffer >= 4.0) {
			user.setBufLevel(2);
		} else {
			user.setBufLevel(1);
		}

		getUser().setqLevel(getUser().getAction());
	}

	public void setAction(int speedFrom) { // 1为rnis 2为前端传来 3为计算速度

		float[][] input = { { 1, 3, 1 } };
		User user = getUser();
		input[0][0] = user.getBufLevel();
		if (speedFrom == 1) {
			input[0][1] = user.getBwLevel();
		} else if (speedFrom == 2) {
			input[0][1] = user.getBwLevel2();
		} else {
			input[0][1] = user.getBwLevel3();
		}

		input[0][2] = user.getqLevel();
		System.out.println("state  " + input[0][0] + " " + input[0][1] + " " + input[0][2]);
		Tensor<?> s = Tensor.create(input);
		Tensor<?> action = MyGraph.sess.runner().feed("s", s).fetch("eval_net/aset").run().get(0);
		long[] ashape = action.shape();
		int abatchSize = (int) ashape[0];
		long[] aresult = new long[abatchSize];
		action.copyTo(aresult);
		user.setAction((int) (aresult[0] + 1));
		if (input[0][0] == 1) {
			System.err.println("修正结果");
			if (user.getAction() > 1) {
				user.setAction(user.getAction() - 1);
			} else {
				user.setAction(1);
			}

		}
		/*
		 * if(user.getAction()==3) { user.setAction(2); }
		 */
		setReady(true);
		System.out.println("该用户: " + user.getIp() + "的暂定视频质量为: " + user.getAction());
		action.close();
		s.close();
		try {
			fw = new FileWriter(file, true);
			fw.write(user.getBuffer() + "  " + user.getSpeed() + "  " + user.getSpeed2() + "  " + user.getSpeed3()
					+ "  " + user.getqLevel() + "  " + user.getAction2() + "  " + getStopToalTime() + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setAction(int a, int b, int c) {
		float[][] input = { { 1, 3, 1 } };
		input[0][0] = a;
		input[0][1] = b;
		input[0][2] = c;
		System.err.println("state  " + input[0][0] + " " + input[0][1] + " " + input[0][2]);
		Tensor<?> s = Tensor.create(input);
		Tensor<?> action = MyGraph.sess.runner().feed("s", s).fetch("eval_net/aset").run().get(0);
		long[] ashape = action.shape();
		int abatchSize = (int) ashape[0];
		long[] aresult = new long[abatchSize];
		action.copyTo(aresult);
		System.out.println("预热DQN" + aresult[0] + 1);
		action.close();
		s.close();
	}

	public void setActionQL(int speedFrom) {
		int[][] input = { { 1, 3, 1 } };
		User user = getUser();
		input[0][0] = user.getBufLevel();
		if (speedFrom == 1) {
			input[0][1] = user.getBwLevel();
		} else if (speedFrom == 2) {
			input[0][1] = user.getBwLevel2();
		} else {
			input[0][1] = user.getBwLevel3();
		}
		input[0][2] = user.getqLevel();
		System.out.println("state  " + input[0][0] + " " + input[0][1] + " " + input[0][2]);
		user.setState((MyProxyServlet2.hashMapDQN.size() - 1) * (qLevel * bufLevel * bwLevel)
				+ (input[0][0] - 1) * (qLevel * bwLevel) + (input[0][1] - 1) * (qLevel) + (input[0][2] - 1) + 1);
		System.out.println("用户: " + user.getIp() + "的状态为: " + user.getState());
		user.setAction(getMaxAction(user.getState()) + 1);
		setReady(true);
		System.out.println("该用户: " + user.getIp() + "的暂定视频质量为: " + user.getAction());
		try {
			fw = new FileWriter(file, true);
			fw.write(user.getBuffer() + "  " + user.getSpeed() + "  " + user.getSpeed2() + "  " + user.getSpeed3()
					+ "  " + user.getqLevel() + "  " + user.getAction2() + "  " + getStopToalTime() + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void creatFilr(String add) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
		file = new File("/home/wspn/git/videoServer/logs/"+ ft.format(dNow) + "_" + add + ".txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readFile(String filrPath) {
		File file = new File(filrPath);
		BufferedReader reader = null;
		if (file.isFile()) {
			int line = 0;
			try {
				String temp = null;
				reader = new BufferedReader(new FileReader(file));
				while ((temp = reader.readLine()) != null) {
					int state = 0;
					for (String string : temp.split("  ")) {
						q[line][state++] = Double.valueOf(string);
					}
					line++;
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void show() {
		StringBuffer a = new StringBuffer();
		a.append("\n");
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum * qLevel * bufLevel * bwLevel; j++) {
				a.append(String.format("%3.5f", q[i][j]) + " ");
			}
			a.append("\n");
		}
		// logger.info(a.toString());
		System.out.println(a.toString());
	}

	private int getMaxAction(int state) {
		// TODO Auto-generated method stub
		double max = q[0][state - 1];
		int max_i = 0;
		for (int i = 0; i < qLevel; i++) {
			if (max < q[i][state - 1]) {
				max = q[i][state - 1];
				max_i = i;
			}
		}
		return max_i;
	}
}
