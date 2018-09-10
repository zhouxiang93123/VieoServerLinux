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

import com.wspn.pcap4j.Pcap;
import com.wspn.pcap4j.Pcap2;
import com.wspn.pcap4j.PcapTest;

public class MyProxyServlet2Client extends AsyncProxyServlet {


	public class MyThread extends Thread{
		private String aString; 
		private String savepath;
		private String  resurl;
		public MyThread(String aString,String savepath, String resurl) 
		{ 
		this.aString = aString; 
		this.savepath=savepath;
		this.resurl=resurl;
		} 
		@Override
		public void run() {
			// TODO Auto-generated method stub
			hashMapLoading.add(aString);
			download(savepath, resurl.concat(aString), aString);
		}
		
	}
	
	
	
	static HashMap<String, File> hashMapFile = new HashMap<>();
	static List<String>hashMapLoading=new ArrayList<>();
	static List<String>hashMapDownloaded=new ArrayList<>();
	static List<MyThread>myThreads=new ArrayList<>();
	static List<Integer>countDownloadList=new ArrayList<>();
	static int coutDownload=0;
	static int busy=0;
	static HashMap<String, Integer> hashMapFilePopularity = new HashMap<>();
	static HashMap<String, Long> hashMapFileLatestTime = new HashMap<>();
	static HashMap<String, List> hashMapAdrr = new HashMap<>();
	String urlForDL = "http://10.108.145.24:8080/ddd/";
	/*String urlForDL = "http://localhost:8080/Dash_test/";*/
	String urlForCache = "http://localhost:8080/eee/";
	//String downloadPath = "F:\\apache-tomcat-8.5.23-windows-x64\\apache-tomcat-8.5.23\\webapps\\cache";
	//String logPath = "E:\\jetty_mec\\logs";
	
	String downloadPath = "D:\\Program Files\\apache-tomcat-8.5.5\\webapps\\eee";
	String logPath = "C:\\Users\\Administrator\\Documents\\GitHub\\cache\\logs";
	
	static long totalCacheBytes = 0;
	boolean fileFlag = true;
	long Timestamp1;
	long Timestamp2;
	int num=0;
	int state=0;
	List<Long> intervalList = new ArrayList<>();
	
	long sum=0;
	
	public static HashMap<String, PcapTest> hashMapPcap=new HashMap<>();
	HashMap<String, List<Double>> hashMapSpeed =new HashMap<>();
	
	static HashMap<String, Integer> hashMapIpSpeed=new HashMap<>();
	
	@Override
	protected void onResponseContent(HttpServletRequest arg0, HttpServletResponse arg1, Response arg2, byte[] arg3,
			int arg4, int arg5, Callback arg6) {
		// TODO Auto-generated method stub
	/*try {
		System.out.println("该用户: "+arg0.getRemoteAddr()+"的限制速度: "+hashMapIpSpeed.get(arg0.getRemoteAddr()));
		Thread.sleep((arg3.length*8*1000)/(hashMapIpSpeed.get(arg0.getRemoteAddr())*1024));
		super.onResponseContent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		// System.out.println(System.currentTimeMillis());
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
		super.onResponseContent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	
	}

	@Override
	protected StreamWriter newWriteListener(HttpServletRequest request, Response proxyResponse) {
		// TODO Auto-generated method stub
		return new MyStreamWriter(request, proxyResponse);
	}

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		
		final String getRequestURL = request.getRequestURI().replace("/", "");
		
		String actionURL=getRequestURL;
		
		final String add=request.getRemoteAddr();
		
		if(!hashMapPcap.containsKey(add)) {
			System.err.println("------"+hashMapPcap.size()+"------");
			PcapTest pcap=new PcapTest();
			//pcap.getUser().setRequest(2);
			pcap.getUser().setChannel(2);
			pcap.getUser().setIp(add);
			String filter="(dst host 10.108.147.120 and src host "+add+") or (dst host "+add+" and src host 10.108.147.120) ";
			pcap.setFilter(filter);
			pcap.setIp(add);
			pcap.creatFilr(add);
			hashMapPcap.put(add, pcap);
			hashMapSpeed.put(add, pcap.getSpeedList());
			hashMapIpSpeed.put(add, 1);
			//RL.users.put(add, pcap.getUser());
			new Thread(new Runnable() {
				public void run() {
					try {
						hashMapPcap.get(add).start();
					} catch (PcapNativeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}).start();
			
		}
		System.out.println("客户端请求目标: " + getRequestURL + "  "+System.currentTimeMillis());
		
		
		if(actionURL.contains("10Mbps")) {
			hashMapPcap.get(add).getUser().setAction(3);
		}else if (actionURL.contains("6Mbps")) {
			hashMapPcap.get(add).getUser().setAction(2);
		}else {
			hashMapPcap.get(add).getUser().setAction(1);
		}
		
	/*	if(hashMapDownloaded.size()>10) {
			int max=0;
			for(String file:hashMapDownloaded) {
				if(Integer.valueOf(file.split("\\.")[0].split("_")[2])>max){
					max=Integer.valueOf(file.split("\\.")[0].split("_")[2]);
				}
			}
			
			if(max<Integer.valueOf(actionURL.split("\\.")[0].split("_")[2])+4) {
				System.err.println("busy:  "+max+" "+actionURL.split("\\.")[0].split("_")[2]);
				
				
			}
		}*/
		
		
	/*	
		if(RL.stop==false) {
			RL.episode();
		}
		*/
		
		//stateJudge(request);
		//System.out.println("state: "+state);
		/*
		 * for (String name:hashMapFilePopularity.keySet()) {
		 * System.out.println(name+"  "+hashMapFilePopularity.get(name)); }
		 */
		/*System.out.println(getRequestURL.split("\\.")[getRequestURL.split("\\.").length - 1].indexOf("4"));*/
		
		/*if (!hashMapFilePopularity.containsKey(getRequestURL)) {
			hashMapFilePopularity.put(getRequestURL, 1);
		} else {
			hashMapFilePopularity.put(getRequestURL, hashMapFilePopularity.get(getRequestURL) + 1);
		}
		Date date = new Date();
		hashMapFileLatestTime.put(getRequestURL, date.getTime() - 28800000);*/

		
	//	System.out.println("客户端请求目标: " + getRequestURL + "的热度为" + hashMapFilePopularity.get(getRequestURL)+"  "+System.currentTimeMillis());
		//System.out.println("现有文件" + totalCacheBytes);
//		for (Entry<String, File> entry : hashMapFile.entrySet()) {
//			System.out.println(entry.getKey());
//		}
//		if (hashMapFile.containsKey(actionURL) ) {
		if(true) {
			
			//System.out.println("请求目标: " + getRequestURL + "已存在");
			//System.out.println("返回: " + urlForCache.concat(getRequestURL).toString());
		
			final String downLoadUrl=actionURL;
			if (fileFlag) {
				new Thread(new Runnable() {
					public void run() {
						if (getRequestURL.split("\\.")[getRequestURL.split("\\.").length - 1].indexOf("4") != -1) {
							/*getInternetRes("F:\\apache-tomcat-8.5.23-windows-x64\\apache-tomcat-8.5.23\\webapps\\cache", return_URL,
								getRequestURL);*/
							/*prefetch(downloadPath, urlForDL,
									downLoadUrl,0);*/
							
						}
					}
					}).start();
			} else {
				System.err.println("不能判断是否下载文件");
			}
			System.out.println("从MEC上来的视频: " + actionURL.toString());
			hashMapPcap.get(add).getUser().setFrom(1);
			//return urlForCache.concat(actionURL);
			return urlForDL.concat(actionURL);
		} else {
			String return_URL = urlForDL.concat(actionURL);
			// System.out.println(Thread.currentThread().getId());
			final String downLoadUrl=actionURL;
			if (fileFlag) {
				new Thread(new Runnable() {
					public void run() {
						if (getRequestURL.split("\\.")[getRequestURL.split("\\.").length - 1].indexOf("4") != -1) {
							/*getInternetRes("F:\\apache-tomcat-8.5.23-windows-x64\\apache-tomcat-8.5.23\\webapps\\cache", return_URL,
								getRequestURL);*/
							prefetch(downloadPath, urlForDL,
									downLoadUrl,0);
							
						}
					}
					}).start();
			} else {
				System.err.println("不能判断是否下载文件");
			}
			System.out.println("从远端来的视频: " + return_URL.toString());
			hashMapPcap.get(add).getUser().setFrom(0);
			return return_URL;
		}
		
		
		
	}

	protected class MyStreamWriter extends StreamWriter {

		protected MyStreamWriter(HttpServletRequest request, Response proxyResponse) {
			
			super(request, proxyResponse);
			
			
			System.out.println("响应目标: " + proxyResponse.getRequest() + proxyResponse.toString());
			// System.out.println(Thread.currentThread().getId());
		}
	}

	
	
	public void prefetch(final String savepath, final String resurl, String fileName, int state) {
		URL url = null;
		HttpURLConnection con = null;
		InputStream in = null;
		FileOutputStream out = null;
		final List<String> fileNameList = new ArrayList<>();
			if(fileName.contains("i.mp4")) {
				System.out.println("此时多载3个");
				for (int i = 1; i <= 6; i++) {
						fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + fileName.split("\\.")[0].split("_")[1]
								+ "_" + String.valueOf(i) + ".m4s");
					/*	fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + "6Mbps"
								+ "_" + String.valueOf(i) + ".m4s");
						fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + "2Mbps"
								+ "_" + String.valueOf(i) + ".m4s");*/
					}
					for (int i = 0; i < fileNameList.size(); i++) {
						if(!hashMapFile.containsKey(fileNameList.get(i))&&!hashMapLoading.contains(fileNameList.get(i))) {
							final String aString=fileNameList.get(i);
							MyThread myThread=new MyThread(aString, savepath, resurl);
							myThreads.add(myThread);
							myThread.start();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
			}else {
				
				
		//		if((hashMapLoading.size())>(Integer.valueOf(fileName.split("\\.")[0].split("_")[2])+11)) {
				if(true) {
					System.out.println("此时多载4个");
					
					for (int i = Integer.valueOf(fileName.split("\\.")[0].split("_")[2])+1; i <=Integer.valueOf(fileName.split("\\.")[0].split("_")[2])+7; i++) {
						fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + fileName.split("\\.")[0].split("_")[1]
								+ "_" + String.valueOf(i) + ".m4s");
					/*	fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + "6Mbps"
								+ "_" + String.valueOf(i) + ".m4s");
						fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + "2Mbps"
								+ "_" + String.valueOf(i) + ".m4s");*/
					}
					
						/*fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" +fileName.split("\\.")[0].split("_")[1]
									+ "_" + String.valueOf(Integer.valueOf(fileName.split("\\.")[0].split("_")[2])+3) + ".m4s");
						fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" +"6Mbps"
								+ "_" + String.valueOf(hashMapLoading.size()+1) + ".m4s");
						fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" +"2Mbps"
								+ "_" + String.valueOf(hashMapLoading.size()+1) + ".m4s");*/
						for (int i = 0; i < fileNameList.size(); i++) {
							if(!hashMapFile.containsKey(fileNameList.get(i))&&!hashMapLoading.contains(fileNameList.get(i))) {
								final String aString=fileNameList.get(i);
								MyThread myThread=new MyThread(aString, savepath, resurl);
								myThreads.add(myThread);
								myThread.start();
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
				}
			}
		

		
	}
	
	
	public void download(String savepath, String resurl, String fileName) {
		System.out.println("开始下载: "+fileName);
		coutDownload++;
		URL url = null;
		HttpURLConnection con = null;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			url = new URL(resurl);
			// 建立http连接，得到连接对象
			con = (HttpURLConnection) url.openConnection();
			if(con.getResponseCode()==404) {
				return;
			}
			// con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;
			// Windows NT; DigExt)");
			in = con.getInputStream();
			byte[] data = getByteData(in);// 转化为byte数组

			File file = new File(savepath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File res = new File(file + File.separator + fileName);
			out = new FileOutputStream(res);
			out.write(data);
			System.out.println("请求目标" + fileName + "下载成功!" + file + File.separator + fileName);
			hashMapDownloaded.add(fileName);
			hashMapFile.put(fileName, res);
			//totalCacheBytes += res.length();
			coutDownload--;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out)
					out.close();
				if (null != in)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 从输入流中获取字节数组
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private byte[] getByteData(InputStream in) throws IOException {
		byte[] b = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		while ((len = in.read(b)) != -1) {
			bos.write(b, 0, len);
		}
		if (null != bos) {
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * 文件操作
	 * 
	 * @param downloadPath
	 * @return @throws
	 */
	public static void fileOperation(String downloadPath) {
		System.out.println("计算已有文件");
		File fileCacheFolder = new File(downloadPath);
		int filesNum = 0;
		if (fileCacheFolder.exists()) {
			File[] filesCache = fileCacheFolder.listFiles();
			for (File fileCache : filesCache) {
				if (fileCache.isFile()) {
					hashMapFile.put(fileCache.getName(), fileCache);
					totalCacheBytes += fileCache.length();
				}
			}
		}
		// System.out.println("一共缓存了"+totalCacheBytes+"bytes");
		/*
		 * for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
		 * System.out.println("文件" + fileEntry.getKey() + "的大小为:" +
		 * fileEntry.getValue().length() / 1024); filesNum++; }
		 * System.out.println(filesNum);
		 */
	}

	/**
	 * 获取Popularity
	 * 
	 * @param logPath
	 * @return @throws
	 */
	public static void countPopularity(String logPath) {
		System.out.println("计算文件热度和最新访问时间");
		File fileCacheFolder = new File(logPath);
		int filesNum = 0;

		if (fileCacheFolder.exists()) {
			File[] filesCache = fileCacheFolder.listFiles();
			for (File fileCache : filesCache) {
				if (fileCache.isFile()) {
					int line = 0;
					filesNum++;
					// File file = new File("D:\\aa.txt");
					BufferedReader reader = null;
					String temp = null;
					try {
						reader = new BufferedReader(new FileReader(fileCache));
						while ((temp = reader.readLine()) != null) {
							// System.out.println("line" + line + ":" + temp);
							// line++;
							String[] arr = temp.split("\\s+");
							String nameKey = arr[6].replace("/", "");
							String time = arr[3].replace("[", "");
							SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
							Date date = format.parse(time);
							Long lastestTime = date.getTime();
							if (nameKey.equals("")) {
								nameKey = "index.html";
							}
							if (!hashMapFilePopularity.containsKey(nameKey)) {
								hashMapFilePopularity.put(nameKey, 1);
							} else {
								hashMapFilePopularity.put(nameKey, hashMapFilePopularity.get(nameKey) + 1);
							}
							hashMapFileLatestTime.put(nameKey, lastestTime);
						}
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
		}
		/*
		 * for (Entry<String, Integer> entry : hashMapFilePopularity.entrySet()) {
		 * System.out.println("文件" + entry.getKey() + "的热度为" + entry.getValue()); }
		 */

	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	public void cachePolicy(long bytesForCached) {
		fileFlag = false;
		long bytesForDelete = 0;
		HashMap<String, Integer> hashMapContentPopularity = new HashMap<>();
		HashMap<String, Integer> hashMapaSegmentsPopularity = new HashMap<>();
		HashMap<String, Integer> hashMapaRepPopularity = new HashMap<>();
		HashMap<String, Integer> hashMapSegmentsRep = new HashMap<>();
		System.out.println("现有文件" + totalCacheBytes);
		for (Entry<String, File> entry : hashMapFile.entrySet()) {
			System.out.println(entry.getKey());
		}
		for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
			if (fileEntry.getKey().split("\\.")[fileEntry.getKey().split("\\.").length - 1].indexOf("4") != -1) {
				String contentName = fileEntry.getKey().split("_")[0];
				if (!hashMapContentPopularity.containsKey(contentName)) {
					hashMapContentPopularity.put(contentName, hashMapFilePopularity.get(fileEntry.getKey()));
				} else {
					hashMapContentPopularity.put(contentName,
							hashMapContentPopularity.get(contentName) + hashMapFilePopularity.get(fileEntry.getKey()));
				}
				String segmentsName = contentName + "_" + fileEntry.getKey().split("\\.")[0].split("_")[2];
				if (!hashMapaSegmentsPopularity.containsKey(segmentsName)) {
					hashMapaSegmentsPopularity.put(segmentsName, hashMapFilePopularity.get(fileEntry.getKey()));
					hashMapSegmentsRep.put(segmentsName, 1);
				} else {
					hashMapaSegmentsPopularity.put(segmentsName, hashMapaSegmentsPopularity.get(segmentsName)
							+ hashMapFilePopularity.get(fileEntry.getKey()));
					hashMapSegmentsRep.put(segmentsName, hashMapSegmentsRep.get(segmentsName) + 1);
				}
				if (!hashMapaRepPopularity.containsKey(fileEntry.getKey())) {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapFilePopularity.get(fileEntry.getKey()));
				} else {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapaRepPopularity.get(fileEntry.getKey())
							+ hashMapFilePopularity.get(fileEntry.getKey()));
				}
			}
		}

		List<Map.Entry<String, Integer>> listContent = new ArrayList<Map.Entry<String, Integer>>(
				hashMapContentPopularity.entrySet());
		List<Map.Entry<String, Integer>> listSegment = new ArrayList<Map.Entry<String, Integer>>(
				hashMapaSegmentsPopularity.entrySet());
		List<Map.Entry<String, Integer>> listRep = new ArrayList<Map.Entry<String, Integer>>(
				hashMapaRepPopularity.entrySet());
		Collections.sort(listContent, new Comparator<Map.Entry<String, Integer>>() {
			// 升序排序
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Collections.sort(listSegment, new Comparator<Map.Entry<String, Integer>>() {
			// 升序排序
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Collections.sort(listRep, new Comparator<Map.Entry<String, Integer>>() {
			// 升序排序
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		for (Map.Entry<String, Integer> mapping : listContent) {

			System.out.println("Content:  " + mapping.getKey() + ":" + mapping.getValue());
		}

		for (Map.Entry<String, Integer> mapping : listSegment) {

			System.out.println("Segment:  " + mapping.getKey() + ":" + mapping.getValue());
		}
		for (Map.Entry<String, Integer> mapping : listRep) {

			System.out.println("Rep:  " + mapping.getKey() + ":" + mapping.getValue());
		}
		for (Entry<String, Integer> entry : hashMapSegmentsRep.entrySet()) {
			System.out.println("hashMapSegmentsRep:  " + entry.getKey() + ":" + entry.getValue());
		}

		loop: for (Map.Entry<String, Integer> mappingContent : listContent) {
			System.out.println("判断Content " + mappingContent.getKey());
			for (Map.Entry<String, Integer> mappingSegment : listSegment) {
				System.out.println("判断Segment " + mappingSegment.getKey());
				if (mappingSegment.getKey().split("_")[0].equals(mappingContent.getKey())) {
					if (hashMapSegmentsRep.get(mappingSegment.getKey()) == 1) {
						continue;
					} else {
						for (Map.Entry<String, Integer> mappingRep : listRep) {
							if ((mappingRep.getKey().split("_")[0] + "_"
									+ mappingRep.getKey().split("\\.")[0].split("_")[2])
											.equals(mappingSegment.getKey())) {
								System.out.println("判断Rep1 " + mappingRep.getKey());
								long fileLength = hashMapFile.get(mappingRep.getKey()).length();
								bytesForDelete += fileLength;
								totalCacheBytes -= fileLength;
								System.out.println("需要删除的字节数和一共删除的字节数1 " + bytesForCached + "  " + bytesForDelete);
								final String filePath = hashMapFile.get(mappingRep.getKey()).getAbsolutePath();
								hashMapFile.remove(mappingRep.getKey());
								new Thread(new Runnable() {
									public void run() {
										deleteFile(filePath);
									}
								}).start();
								if (bytesForDelete >= bytesForCached) {
									break loop;
								}

								hashMapSegmentsRep.put(mappingSegment.getKey(),
										hashMapSegmentsRep.get(mappingSegment.getKey()) - 1);
								if (hashMapSegmentsRep.get(mappingSegment.getKey()) == 1) {
									break;
								}

							}
						}
					}
				}
			}
			for (Map.Entry<String, Integer> mappingSegment : listSegment) {
				if (mappingSegment.getKey().split("_")[0].equals(mappingContent.getKey())) {
					for (Entry<String, File> entry : hashMapFile.entrySet()) {
						if ((entry.getKey().split("_")[0] + "_" + entry.getKey().split("\\.")[0].split("_")[2])
								.equals(mappingSegment.getKey())) {
							System.out.println("判断Rep1 " + entry.getKey());
							long fileLength = hashMapFile.get(entry.getKey()).length();
							bytesForDelete += fileLength;
							totalCacheBytes -= fileLength;
							System.out.println("需要删除的字节数和一共删除的字节数2 " + bytesForCached + "  " + bytesForDelete);
							final String filePath = hashMapFile.get(entry.getKey()).getAbsolutePath();
							hashMapFile.remove(entry.getKey());
							new Thread(new Runnable() {
								public void run() {
									deleteFile(filePath);
								}
							}).start();
							if (bytesForDelete >= bytesForCached) {
								break loop;
							}
						}
					}
				}
			}
		}
		fileFlag = true;
	}

	/**
	 * LFU -- 最近最少使用
	 * 
	 * @param bytesForCached
	 * @author zhouxiang
	 * 
	 */
	public void LFU(long bytesForCached) {
		fileFlag = false;
		long bytesForDelete = 0;
		HashMap<String, Integer> hashMapaRepPopularity = new HashMap<>();
		for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
			if (fileEntry.getKey().split("\\.")[fileEntry.getKey().split("\\.").length - 1].indexOf("4") != -1) {
				if (!hashMapaRepPopularity.containsKey(fileEntry.getKey())) {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapFilePopularity.get(fileEntry.getKey()));
				} else {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapaRepPopularity.get(fileEntry.getKey())
							+ hashMapFilePopularity.get(fileEntry.getKey()));
				}
			}
		}
		List<Map.Entry<String, Integer>> listRep = new ArrayList<Map.Entry<String, Integer>>(
				hashMapaRepPopularity.entrySet());
		Collections.sort(listRep, new Comparator<Map.Entry<String, Integer>>() {
			// 升序排序
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		System.out.println("现有文件的热度排序:");
		for (Entry<String, Integer> entry : listRep) {
			System.out.println(entry.getKey() + "的热度为:" + entry.getValue());
		}
		for (Map.Entry<String, Integer> mappingRep : listRep) {
			System.out.println("LFU判断Rep " + mappingRep.getKey());
			long fileLength = hashMapFile.get(mappingRep.getKey()).length();
			final String filePath = hashMapFile.get(mappingRep.getKey()).getAbsolutePath();
			hashMapFile.remove(mappingRep.getKey());
			bytesForDelete += fileLength;
			totalCacheBytes -= fileLength;
			System.out.println("需要删除的字节数和一共删除的字节数 " + bytesForCached + "  " + bytesForDelete);
			new Thread(new Runnable() {
				public void run() {
					deleteFile(filePath);
				}
			}).start();
			if (bytesForDelete >= bytesForCached) {
				break;
			}
		}
		fileFlag = true;
	}

	/**
	 * LRU -- 最近最久未使用
	 * 
	 * @param bytesForCached
	 * @author zhouxiang
	 * 
	 */
	public void LRU(long bytesForCached) {
		fileFlag = false;
		long bytesForDelete = 0;
		HashMap<String, Long> hashMapaRepLastest = new HashMap<>();
		for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
			if (fileEntry.getKey().split("\\.")[fileEntry.getKey().split("\\.").length - 1].indexOf("4") != -1) {
				if (!hashMapaRepLastest.containsKey(fileEntry.getKey())) {
					hashMapaRepLastest.put(fileEntry.getKey(), hashMapFileLatestTime.get(fileEntry.getKey()));
				} else {
					hashMapaRepLastest.put(fileEntry.getKey(),
							hashMapaRepLastest.get(fileEntry.getKey()) + hashMapFileLatestTime.get(fileEntry.getKey()));
				}
			}
		}
		List<Map.Entry<String, Long>> listRep = new ArrayList<Map.Entry<String, Long>>(hashMapaRepLastest.entrySet());
		Collections.sort(listRep, new Comparator<Map.Entry<String, Long>>() {
			// 升序排序
			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		System.out.println("现有文件的最先时间排序:");
		for (Entry<String, Long> entry : listRep) {
			System.out.println(entry.getKey() + "的最新时间为:" + entry.getValue());
		}
		for (Map.Entry<String, Long> mappingRep : listRep) {
			System.out.println("LRU判断Rep " + mappingRep.getKey());
			long fileLength = hashMapFile.get(mappingRep.getKey()).length();
			final String filePath = hashMapFile.get(mappingRep.getKey()).getAbsolutePath();
			hashMapFile.remove(mappingRep.getKey());
			bytesForDelete += fileLength;
			totalCacheBytes -= fileLength;
			System.out.println("需要删除的字节数和一共删除的字节数 " + bytesForCached + "  " + bytesForDelete);
			new Thread(new Runnable() {
				public void run() {
					deleteFile(filePath);
				}
			}).start();
			if (bytesForDelete >= bytesForCached) {
				break;
			}
		}
		fileFlag = true;
	}

}


