package com.wspn.jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wspn.pcap4j.DQN;

public class PlayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PlayServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* 允许跨域的主机地址 */
		response.setHeader("Access-Control-Allow-Origin", "*");  
		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		response.setHeader("Access-Control-Allow-Methods", "*");  
		/* 重新预检验跨域的缓存时间 (s) */
		response.setHeader("Access-Control-Max-Age", "3600");  
		/* 允许跨域的请求头 */
		response.setHeader("Access-Control-Allow-Headers", "*");  
		/* 是否携带cookie */
		response.setHeader("Access-Control-Allow-Credentials", "true");  
		DQN dqn = MyProxyServlet2.hashMapDQN.get(request.getRemoteAddr());
		if(dqn.isPauseFlag()) {
			long stopTime=System.currentTimeMillis()-dqn.getPauseTime();
			long time=stopTime+dqn.getStopToalTime();
			dqn.setStopToalTime(time);
			dqn.setPauseFlag(false);
			System.err.println("重新播放  "+stopTime/1000.0+" "+time/1000.0);
		}
		else {
			System.err.println("播放下一段");
		}
	}

}
