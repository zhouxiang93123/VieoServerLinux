package com.wspn.web;
/*
 * Copyright (c) 2010-2018 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.lang.reflect.Executable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.wspn.util.LimitQueue;

/**
 * This example demonstrates how to create a websocket connection to a server.
 * Only the most important callbacks are overloaded.
 */
public class ExampleClient extends WebSocketClient {
	long inteval;
	long delay;
	String flag;
	String sendMessage;
	HashMap<String, LimitQueue<Integer>> mapResult;
	HashMap<Integer, String> mapMME;

	public ExampleClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public ExampleClient(URI serverURI) {
		super(serverURI);
	}

	public ExampleClient(URI serverUri, Map<String, String> httpHeaders, long delay, long inteval, String flag,
			String sendMessage) {
		super(serverUri, httpHeaders);
		this.delay = delay;
		this.inteval = inteval;
		this.flag = flag;
		this.sendMessage = sendMessage;

	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				send(sendMessage);
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, delay, inteval);
		// if you plan to refuse connection based on ip or httpfields overload:
		// onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage(String message) {
		Set<Integer> set = new HashSet<>();
		if (flag.equals("enb")) {
			JSONObject jsonObject = new JSONObject(message);
			JSONArray jsonArray = jsonObject.getJSONArray("ue_list");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject ueData = jsonArray.getJSONObject(i);
				int enbUeId = ueData.getInt("enb_ue_id");
				set.add(enbUeId);
				JSONArray cellInfoArray = ueData.getJSONArray("cells");
				JSONObject cellObject = cellInfoArray.getJSONObject(0);
				double dlBitRate = cellObject.getDouble("dl_bitrate");
				//System.out.println("enbUeId: " + enbUeId + "  dlBitRate: " + (int) (dlBitRate / 1024.0) + "KB/s");
				if (mapMME.containsKey(enbUeId)) {
					//System.err.println("new ueid" + enbUeId);
					if(!mapResult.containsKey(mapMME.get(enbUeId))) {
						LimitQueue<Integer> limitQueue=new LimitQueue<>(10);
						limitQueue.offer((int) (dlBitRate / 1024.0));
						mapResult.put(mapMME.get(enbUeId), limitQueue);
					}else {
						mapResult.get(mapMME.get(enbUeId)).offer((int) (dlBitRate / 1024.0));;
					}
					
				}

			}
			//System.err.println(mapResult.toString());
		} else {
			JSONObject jsonObject = new JSONObject(message);
			JSONArray jsonArray = jsonObject.getJSONArray("ue_list");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject ueData = jsonArray.getJSONObject(i);
				boolean registered = ueData.getBoolean("registered");
				if (ueData.has("enb_ue_id")) {
					int enbUeId = ueData.getInt("enb_ue_id");
					set.add(enbUeId);
					long imsi = ueData.getLong("imsi");
					JSONArray bearerInfoArray = ueData.getJSONArray("bearers");
					JSONObject bearerObject = bearerInfoArray.getJSONObject(bearerInfoArray.length() - 1);
					String ip = bearerObject.getString("ip");
					mapMME.put(enbUeId, ip);
					// System.err.println("enbUeId: " + enbUeId + " ip: " + ip + " imsi: " + imsi);
				}
			}
			for (Integer key : mapMME.keySet()) {
				if (!set.contains(key)) {
					mapMME.remove(key);
				}

			}
			//System.out.println("mapMME: " + mapMME.toString());
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println(
				"Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}



	public HashMap<String, LimitQueue<Integer>> getMapResult() {
		return mapResult;
	}

	public void setMapResult(HashMap<String, LimitQueue<Integer>> mapResult) {
		this.mapResult = mapResult;
	}

	public HashMap<Integer, String> getMapMME() {
		return mapMME;
	}

	public void setMapMME(HashMap<Integer, String> mapMME2) {
		this.mapMME = mapMME2;
	}

}