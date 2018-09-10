package com.wspn.pcap4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.EthernetPacket.EthernetHeader;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.AbstractPacket.AbstractBuilder;
import org.pcap4j.packet.IpV4Packet.IpV4Header;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.MacAddress;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;

public class mec2 {

	public static void main(String[] args) throws PcapNativeException, InterruptedException, NotOpenException {
		// 获取所有网卡设备
		List<PcapNetworkInterface> alldev = Pcaps.findAllDevs();
		// 根据设备名称初始化抓包接口
		PcapNetworkInterface nif = Pcaps.getDevByName(alldev.get(0).getName());

		// 抓取包长度
		int snaplen = 64 * 1024;
		// 超时50ms
		int timeout = 50;
		// 初始化抓包器
		/*
		 * PcapHandle.Builder phb = new
		 * PcapHandle.Builder(nif.getName()).snaplen(snaplen)
		 * .promiscuousMode(PromiscuousMode.PROMISCUOUS).timeoutMillis(timeout)
		 * .bufferSize(1 * 1024 * 1024); PcapHandle handle = phb.build();
		 */
		// handle = nif.openLive(snaplen, PromiscuousMode.NONPROMISCUOUS, timeout);

		PcapHandle handle = nif.openLive(65536, PromiscuousMode.PROMISCUOUS, 10);
		final PcapHandle sendHandle = nif.openLive(65536, PromiscuousMode.PROMISCUOUS, 10);

		/** 设置TCP过滤规则 */
		String filter = "(src host 10.108.147.75 and dst host 10.108.146.76) or (src host 10.108.147.49 and dst host 10.108.146.76)";

		// 设置过滤器

		handle.setFilter(filter, BpfCompileMode.OPTIMIZE);

		// 初始化listener
		PacketListener listener = new PacketListener() {
			@Override
			public void gotPacket(Packet packet) {

			}
		};
		// 直接使用loop
		// handle.loop(-1, listener);

		while (true) {
			
			Packet packet = handle.getNextPacket();
			
			if (packet == null) {
				continue;
			} else {
				final IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
				final EthernetPacket ethernetPacket=packet.get(EthernetPacket.class);
				if (ipV4Packet != null) {
					System.out.println(packet);
					IpV4Header ipV4Header = ipV4Packet.getHeader();
					System.out.println(packet);
					byte[] bs = new byte[] { (byte) 10, (byte) 108, (byte) 147, (byte) 49 };
					String macDst="00:0c:29:51:86:f7";
					if(ipV4Header.getSrcAddr().toString().equals("/10.108.147.75")) {
						bs = new byte[] { (byte) 10, (byte) 108, (byte) 147, (byte) 49 };
						macDst="00:0c:29:51:86:f7";
					}else if (ipV4Header.getSrcAddr().toString().equals("/10.108.147.49")){
						bs = new byte[] { (byte) 10, (byte) 108, (byte) 147, (byte) 75 };
						macDst="20:47:47:ad:f4:62";
					}


					final IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
					try {
						ipV4Builder
								.version(ipV4Header.getVersion()).tos(ipV4Header.getTos()).ttl(ipV4Header.getTtl())
								.protocol(ipV4Header.getProtocol()).srcAddr(ipV4Header.getDstAddr())
								.dstAddr((Inet4Address) InetAddress.getByAddress(bs))
								.payloadBuilder(
										new UnknownPacket.Builder().rawData(ipV4Packet.getPayload().getRawData()))
								.correctChecksumAtBuild(true).correctLengthAtBuild(true);
					} catch (UnknownHostException e1) {
						throw new IllegalArgumentException(e1);
					}
					EthernetHeader ethernetHeader=ethernetPacket.getHeader();
					EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
					etherBuilder.dstAddr(MacAddress.getByName(macDst, ":"))
							.srcAddr(ethernetHeader.getDstAddr()).type(ethernetHeader.getType())
							 .paddingAtBuild(true);
					etherBuilder.payloadBuilder(new AbstractBuilder() {
						@Override
						public Packet build() {
							return ipV4Builder.build();
						}
					});

					try {
						Packet p = etherBuilder.build();
						sendHandle.sendPacket(p);
					} catch (PcapNativeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotOpenException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

	}

}
