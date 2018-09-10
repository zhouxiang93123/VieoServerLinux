package com.wspn.pcap4j;

import java.util.List;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;

public class InitPcap {

	// 获取所有网卡设备
	static List<PcapNetworkInterface> alldev = null;
	static PcapNetworkInterface nif = null;
	static PcapHandle.Builder phb=null;

	static public void init() {
		try {
			alldev = Pcaps.findAllDevs();
		} catch (PcapNativeException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		for (PcapNetworkInterface pcapNetworkInterface : alldev) {
			System.out.println(pcapNetworkInterface.toString());
		}
		// 根据设备名称初始化抓包接口
		try {
			nif = Pcaps.getDevByName(alldev.get(0).getName());
		} catch (PcapNativeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// 抓取包长度
		int snaplen = 1024 * 1024 * 100;
		// 超时50ms
		int timeout = 1000;
		// 初始化抓包器
		phb = new PcapHandle.Builder(nif.getName()).snaplen(snaplen)
				.promiscuousMode(PromiscuousMode.PROMISCUOUS).timeoutMillis(timeout).bufferSize(100 * 1024 * 1024);
	}

}
