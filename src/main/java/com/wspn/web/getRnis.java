package com.wspn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket.READYSTATE;

import com.wspn.util.LimitQueue;

public class getRnis {

	HashMap<Integer, String> mapMME=new HashMap<>();
	HashMap<String, LimitQueue<Integer>> mapResult=new HashMap<>();

	public void start() throws URISyntaxException {
		Map<String, String> httpHeaders = new HashMap<>();
		httpHeaders.put("Origin", "chrome-extension://omalebghpgejjiaoknljcfmglgbpocdp");
		ExampleClient cilentForENB = new ExampleClient(new URI("ws://10.108.145.150:9001"), httpHeaders, 0, 100, "enb",
				"{\"message\": \"ue_get\",\"stats\":true}");
		cilentForENB.setMapMME(mapMME);
		cilentForENB.setMapResult(mapResult);
		cilentForENB.setConnectionLostTimeout(0);
		cilentForENB.connect();
		ExampleClient cilentForMME = new ExampleClient(new URI("ws://10.108.145.150:9000"), httpHeaders, 0, 1000, "mme",
				"{\"message\": \"ue_get\",\"stats\":true}");
		cilentForMME.setConnectionLostTimeout(0);
		cilentForMME.setMapMME(mapMME);
		cilentForMME.setMapResult(mapResult);
		cilentForMME.connect();
//		 TimerTask task = new TimerTask() {
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					System.err.println(mapResult.toString());
//				}
//			};
//			Timer timer = new Timer();
//			timer.schedule(task, 0, 200);
	}

	public static void main(String[] args) throws URISyntaxException {
		Map<String, String> httpHeaders = new HashMap<>();
		httpHeaders.put("Origin", "chrome-extension://omalebghpgejjiaoknljcfmglgbpocdp");
		final HashMap<Integer, String> mapMME = new HashMap<>();
		final HashMap<String, LimitQueue<Integer>> mapResult = new HashMap<>();
		ExampleClient cilentForENB = new ExampleClient(new URI("ws://10.108.145.150:9001"), httpHeaders, 0, 1000, "enb",
				"{\"message\": \"ue_get\",\"stats\":true}");
		cilentForENB.setMapMME(mapMME);
		cilentForENB.setMapResult(mapResult);
		cilentForENB.setConnectionLostTimeout(0);
		cilentForENB.connect();
		ExampleClient cilentForMME = new ExampleClient(new URI("ws://10.108.145.150:9000"), httpHeaders, 0, 100, "mme",
				"{\"message\": \"ue_get\",\"stats\":true}");
		cilentForMME.setConnectionLostTimeout(0);
		cilentForMME.setMapMME(mapMME);
		cilentForMME.setMapResult(mapResult);
		cilentForMME.connect();
//		 while (!cilentForMME.getReadyState().equals(READYSTATE.OPEN)) {
//		 }
		 TimerTask task = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mapResult.containsKey("172.16.0.2")) {
						System.err.println(mapResult.get("172.16.0.2").getQueue().get(0));
					}
					
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 0, 100);
	}

	public HashMap<Integer, String> getMapMME() {
		return mapMME;
	}

	public void setMapMME(HashMap<Integer, String> mapMME) {
		this.mapMME = mapMME;
	}

	public HashMap<String, LimitQueue<Integer>> getMapResult() {
		return mapResult;
	}

	public void setMapResult(HashMap<String, LimitQueue<Integer>> mapResult) {
		this.mapResult = mapResult;
	}


	
	
}