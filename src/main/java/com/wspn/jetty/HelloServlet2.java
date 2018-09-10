package com.wspn.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet2() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();
		String title = "使用 GET 方法读取表单数据";
		// 处理中文
		String name = new String(request.getParameter("name"));
		String docType = "<!DOCTYPE html> \n";
		out.println(
				docType + "<html>\n" + "<head><title>" + title + "</title></head>\n" + "<body bgcolor=\"#f0f0f0\">\n"
						+ "<h1 align=\"center\">" + title + "</h1>\n" + "<ul>\n" + "  <li><b>站点名</b>：" + name + "\n"
						+ "  <li><b>网址</b>：" + request.getParameter("url") + "\n" + "</ul>\n" + "</body></html>");
	}

	// 处理 POST 方法请求的方法
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> map = new HashMap<>();
		Rnis rnis = new Rnis();
		rnis.setRsrp(Integer.valueOf(request.getParameter("rsrp")));
		rnis.setRssnr(Integer.valueOf(request.getParameter("rssnr")));
		rnis.setImsi(request.getParameter("imsi"));
		map = request.getParameterMap();
		boolean res = RnisOperation.getInstance().saverRnis(rnis);
		if (res == true) {
			System.out.println("向rnis表中插入数据成功");
		} else {
			System.out.println("向rnis表中插入数据失败");
		}

		// for(Entry<String, String[]> entry:map.entrySet()) {
		// for(String string:entry.getValue()) {
		// System.out.println(entry.getKey()+" "+string);
		// }
		// }
	}

}
