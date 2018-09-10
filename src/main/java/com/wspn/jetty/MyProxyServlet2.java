package com.wspn.jetty;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ReadListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.DeferredContentProvider;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.server.handler.ContextHandler.StaticContext;
import org.eclipse.jetty.util.Callback;
import org.pcap4j.core.PcapNativeException;

import com.wspn.pcap4j.DQN;
import com.wspn.pcap4j.Pcap;
import com.wspn.pcap4j.Pcap2;
import com.wspn.pcap4j.PcapTest;
import com.wspn.web.getRnis;

public class MyProxyServlet2 extends AsyncProxyServlet {

	//String urlForDL = "http://10.108.145.24:8080/ddd/";
	//String urlForCache = "http://localhost:8080/ddd/";
	String urlForDL = "http://10.108.145.24:8088/ddd/";
	String urlForCache = "http://localhost:8088/ddd/";
	public static HashMap<String, DQN> hashMapDQN = new HashMap<>();
	public static HashMap<String, Integer> hashMapIpSpeed = new HashMap<>();
	public static HashMap<String, Long> hashMapFileSize = new HashMap<>();
	public static getRnis getRnis = new getRnis();

	@Override
	protected void onResponseContent(HttpServletRequest arg0, HttpServletResponse arg1, Response arg2, byte[] arg3,
			int arg4, int arg5, Callback arg6) {
		super.onResponseContent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	protected StreamWriter newWriteListener(HttpServletRequest request, Response proxyResponse) {
		// TODO Auto-generated method stub
		return new MyStreamWriter(request, proxyResponse);
	}

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		String getRequestURL = request.getRequestURI().replace("/", "");
		String actionURL = getRequestURL;
		final String add = request.getRemoteAddr();

		if (!hashMapDQN.containsKey(add)) {
			System.err.println("------" + hashMapDQN.size() + "------");
			DQN dqn = new DQN();
			dqn.getUser().setIp(add);
			dqn.getUser().setAction(1);// 初始化action
			dqn.getUser().setAction2(1);// 初始化action
			dqn.setIp(add);
			dqn.creatFilr(add);
			dqn.setAction(1, 1, 1);// 预热用
			hashMapDQN.put(add, dqn);
			new Thread(new Runnable() {
				public void run() {
					try {
						hashMapDQN.get(add).start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		}
		System.out.println("客户端请求目标: " + getRequestURL + "  " + System.currentTimeMillis());
		hashMapDQN.get(add).setFileName(getRequestURL);
		if (getRequestURL.contains("2Mbps")) {
			hashMapDQN.get(add).getUser().setAction2(1);
		} else if (getRequestURL.contains("6Mbps")) {
			hashMapDQN.get(add).getUser().setAction2(2);
		} else if (getRequestURL.contains("10Mbps")) {
			hashMapDQN.get(add).getUser().setAction2(3);
		}
		String downLoadUrl = actionURL;
		return urlForCache.concat(actionURL);
	}

	protected class MyStreamWriter extends StreamWriter {
		protected MyStreamWriter(HttpServletRequest request, Response proxyResponse) {
			super(request, proxyResponse);
			System.out.println("响应目标: " + proxyResponse.getRequest() + proxyResponse.toString());
		}
	}

	public static boolean readfile(String filepath) throws FileNotFoundException, IOException, URISyntaxException {
		getRnis.start();
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				System.out.println("文件");
				System.out.println("path=" + file.getPath());
				System.out.println("absolutepath=" + file.getAbsolutePath());
				System.out.println("name=" + file.getName());
			} else if (file.isDirectory()) {
				System.out.println("文件夹");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "/" + filelist[i]);
					if (!readfile.isDirectory()) {
						hashMapFileSize.put(readfile.getName(), readfile.length());
					} else if (readfile.isDirectory()) {
						readfile(filepath + "/" + filelist[i]);
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
		for (String string : hashMapFileSize.keySet()) {
			System.out.println(string + " " + hashMapFileSize.get(string));
		}
		return true;
	}

}
