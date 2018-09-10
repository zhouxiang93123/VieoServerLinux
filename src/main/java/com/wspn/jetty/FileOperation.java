package com.wspn.jetty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class FileOperation {
	static HashMap<String, File> hashMapFile = new HashMap<>();
	static HashMap<String, Integer> hashMapFilePopularity = new HashMap<>();
	/**
	 * 文件操作
	 * 
	 * @param downloadPath @return @throws
	 */
	public void fileOperation(String downloadPath) {
		File fileCacheFolder = new File(downloadPath);
		int filesNum = 0;
		if (fileCacheFolder.exists()) {
			File[] filesCache = fileCacheFolder.listFiles();
			for (File fileCache : filesCache) {
				if (fileCache.isFile()) {
					fileCache.length();
					hashMapFile.put(fileCache.getName(), fileCache);
				}
			}
		}
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
	 * @param logPath @return @throws
	 */
	public static void countPopularity(String logPath) {
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
							if (nameKey.equals("")) {
								nameKey = "index.html";
							}
							if (!hashMapFilePopularity.containsKey(nameKey)) {
								hashMapFilePopularity.put(nameKey, 1);
							} else {
								hashMapFilePopularity.put(nameKey, hashMapFilePopularity.get(nameKey) + 1);
							}
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
}
