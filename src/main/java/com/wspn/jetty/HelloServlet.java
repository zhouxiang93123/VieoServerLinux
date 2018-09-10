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

public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		response.setContentType("text/html;charset=UTF-8");
		OutputStream outputStream = response.getOutputStream();// 获取OutputStream输出流
		String data = "1";
		if (MyProxyServlet2.hashMapDQN.containsKey(request.getRemoteAddr())) {
			DQN dqn = MyProxyServlet2.hashMapDQN.get(request.getRemoteAddr());
			if (dqn.isReady()) {
				Integer a = dqn.getUser().getAction();
				data = a.toString();
				byte[] dataByteArr = data.getBytes("UTF-8");// 将字符转换成字节数组，指定以UTF-8编码进行转换
				outputStream.write(dataByteArr);// 使用OutputStream流向客户端输出字节数组
				System.out.println("下一个视频质量为: " + data);
				dqn.setReady(false);
			} else {
				System.err.println("not ready");
			}

		}
		// 设置响应内容类型
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
		response.setContentType("text/html;charset=UTF-8");
		OutputStream outputStream = response.getOutputStream();// 获取OutputStream输出流
		String data = "1";
		Double buffer = null;
		Double throughput = null;
		long endTime = 0;
		Double speed = null;
		double avgSpeed = 0;
		if (request.getParameter("buffer").equals("NaN")) {
			buffer = 0.0;
			System.out.println("bufferLength: " + request.getParameter("buffer") + " " + buffer);
		} else {
			buffer = Double.valueOf(request.getParameter("buffer"));
			System.out.println("bufferLength: " + buffer);
		}
		throughput = Double.valueOf(request.getParameter("throughput"));
		endTime = Long.valueOf(request.getParameter("time"));

		if (MyProxyServlet2.hashMapDQN.containsKey(request.getRemoteAddr())) {
			DQN dqn = MyProxyServlet2.hashMapDQN.get(request.getRemoteAddr());
			dqn.setEndDownload(endTime);
			System.out.println("下载完成时间: " + dqn.getEndDownload());
			if (MyProxyServlet2.hashMapFileSize.containsKey(dqn.getFileName())) {
				long size = MyProxyServlet2.hashMapFileSize.get(dqn.getFileName());
				System.out.println("下载完成文件大小为: " + MyProxyServlet2.hashMapFileSize.get(dqn.getFileName()));
				if (dqn.getStartDownload() != 0) {
					long time = dqn.getEndDownload() - dqn.getStartDownload();
					System.out.println("time " + time);
					speed = (size * 8 / 1024.0 / ((time - 50) / 1000.0));
					if (dqn.getUser().getSpeed3() != 0) {
						avgSpeed = (speed + dqn.getUser().getSpeed3()) / 2.0;
					} else {
						avgSpeed = speed;
					}
					// System.out.println(speed);
				}
			}

			dqn.setBuffer(buffer.doubleValue());
			dqn.setBandWidth();
			dqn.setBandWidth2(throughput.doubleValue());
			dqn.setBandWidth3(avgSpeed);
			System.out.println("rnis speed: " + dqn.getUser().getSpeed());
			System.out.println("client speed: " + throughput.intValue());
			System.out.println("computer speed: " + avgSpeed);
			dqn.setAction(3);
			// dqn.setActionQL(3);

			if (dqn.isReady()) {
				Integer a = dqn.getUser().getAction();
				data = a.toString();
				byte[] dataByteArr = data.getBytes("UTF-8");// 将字符转换成字节数组，指定以UTF-8编码进行转换
				outputStream.write(dataByteArr);// 使用OutputStream流向客户端输出字节数组
				System.out.println("下一个视频质量为: " + data);
				dqn.setReady(false);
			} else {
				System.err.println("not ready");
			}

		}
	}

}
