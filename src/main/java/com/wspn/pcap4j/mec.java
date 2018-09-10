package com.wspn.pcap4j;

import static org.pcap4j.util.ByteArrays.BYTE_SIZE_IN_BYTES;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.EthernetPacket.EthernetHeader;
import org.pcap4j.packet.GtpV1Packet;
import org.pcap4j.packet.GtpV1Packet.GtpV1Header;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IcmpV4EchoReplyPacket;
import org.pcap4j.packet.IcmpV4EchoReplyPacket.IcmpV4EchoReplyHeader;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.UdpPacket.UdpHeader;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.AbstractPacket.AbstractBuilder;
import org.pcap4j.packet.IpV4Packet.IpV4Header;
import org.pcap4j.packet.IpV4Packet.IpV4Tos;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.packet.namednumber.NotApplicable;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

import com.wspn.jetty.start;

import org.pcap4j.core.BpfProgram.BpfCompileMode;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;

public class mec {

	static public GtpV1Header gtpV1Header=null;
	static public UdpHeader udpHeader=null;
	static public IpV4Header ipV4Header=null;
	static public EthernetHeader ethernetHeader=null;
	
	public static void main(String[] args) throws PcapNativeException, InterruptedException, NotOpenException, UnknownHostException {
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
		String filter = "(src host 10.108.145.31 and dst host 10.108.147.185) or (src host 10.108.147.49 and dst host 10.108.147.185) or(dst host 10.108.147.185 and icmp) ";

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
				if(ipV4Packet!=null) {
					if(ipV4Packet.getHeader().getProtocol().equals(IpNumber.SCTP)) {
						if (ipV4Packet != null) {
							System.out.println(packet.getHeader());
							System.err.println("sctp");
							IpV4Header ipV4Header = ipV4Packet.getHeader();
							byte[] bs = new byte[] { (byte) 10, (byte) 108, (byte) 147, (byte) 49 };
							String macDst="00:0c:29:51:86:f7";
							if(ipV4Header.getSrcAddr().toString().equals("/10.108.145.31")) {
								bs = new byte[] { (byte) 10, (byte) 108, (byte) 147, (byte) 49 };
								macDst="00:0c:29:51:86:f7";
							}else if (ipV4Header.getSrcAddr().toString().equals("/10.108.147.49")){
								bs = new byte[] { (byte) 10, (byte) 108, (byte) 145, (byte) 31 };
								//macDst="20:47:47:ad:f4:62";
								macDst="00:0c:29:7c:58:9d";
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
					}else if(ipV4Packet.getHeader().getProtocol().equals(IpNumber.UDP)) {
						if (ipV4Packet != null) {
							System.out.println(ipV4Packet);
							System.err.println("gtp");
							UdpPacket udpPacket = packet.get(UdpPacket.class);
							GtpV1Packet gtpV1Packet=ipV4Packet.get(GtpV1Packet.class);
							udpHeader =udpPacket.getHeader();
							
							gtpV1Header=gtpV1Packet.getHeader();
							ipV4Header=ipV4Packet.getHeader();
							ethernetHeader=ethernetPacket.getHeader();
							System.out.println(ipV4Header.getSrcAddr());
							System.out.println(ipV4Header.getDstAddr());
							byte[] rawData=udpPacket.getPayload().getPayload().getRawData();
							
							
							 byte versionAndIhl
						        = ByteArrays.getByte(rawData, IpV4Packet.IpV4Header.VERSION_AND_IHL_OFFSET);
							 IpVersion version = IpVersion.getInstance(
						                       (byte)((versionAndIhl & 0xF0) >> 4)
						                     );
							
							IpV4Tos tos
						        = PacketFactories.getFactory(
						            IpV4Tos.class, NotApplicable.class
						          ).newInstance(rawData, IpV4Packet.IpV4Header.TOS_OFFSET, BYTE_SIZE_IN_BYTES);
							
							byte ttl
					        = ByteArrays.getByte(rawData, IpV4Packet.IpV4Header.TTL_OFFSET);
							
							IpNumber protocol
					        = IpNumber
					            .getInstance(ByteArrays.getByte(rawData, IpV4Packet.IpV4Header.PROTOCOL_OFFSET));
							
							Inet4Address srcAddr
						        = ByteArrays.getInet4Address(udpPacket.getPayload().getPayload().getRawData(), 12);
							Inet4Address dstAddr= ByteArrays.getInet4Address(udpPacket.getPayload().getPayload().getRawData(), 16);
							
							
							
						     byte[] bytes=new byte[udpPacket.getPayload().getPayload().getRawData().length-20];
						     System.arraycopy(udpPacket.getPayload().getPayload().getRawData(), 20, bytes, 0, udpPacket.getPayload().getPayload().getRawData().length-20);
							
						     //System.out.println(DatatypeConverter.printHexBinary(bytes));
						     final IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
								ipV4Builder
										.version(version).tos(tos).ttl(ttl)
										.protocol(protocol).srcAddr(ipV4Header.getDstAddr())
										.dstAddr(dstAddr)
										.payloadBuilder(
												new UnknownPacket.Builder().rawData(bytes))
										.correctChecksumAtBuild(true).correctLengthAtBuild(true);
								
								String macDst="00:0f:e2:6a:09:78";
								
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
								
								Packet p = etherBuilder.build();
								
								try {
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
					else if (ipV4Packet.getHeader().getProtocol().equals(IpNumber.ICMPV4)) {
						IcmpV4CommonPacket icmpV4CommonPacket=ipV4Packet.get(IcmpV4CommonPacket.class);
						IcmpV4EchoReplyPacket icmpV4EchoReplyPacket=ipV4Packet.get(IcmpV4EchoReplyPacket.class);
					
						byte[] bs1 = new byte[] { (byte) 10, (byte) 108, (byte) 147, (byte) 49 };
						String macDst="00:0c:29:51:86:f7";
						
						//System.out.println(ipV4Packet);
						//System.err.println("icmp");
						IpV4Header ipV4HeaderResponse = ipV4Packet.getHeader();
						byte[] bs = new byte[] { (byte) 172, (byte) 16, (byte) 0, (byte) 2 };
						 final IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
							ipV4Builder
									.version(ipV4HeaderResponse.getVersion()).tos(ipV4HeaderResponse.getTos()).ttl(ipV4HeaderResponse.getTtl())
									.protocol(ipV4HeaderResponse.getProtocol()).srcAddr(ipV4HeaderResponse.getSrcAddr())
									.dstAddr((Inet4Address) InetAddress.getByAddress(bs))
									.payloadBuilder(icmpV4CommonPacket.getBuilder())
									.correctChecksumAtBuild(true).correctLengthAtBuild(true);
							
							GtpV1Packet.Builder gBuilder=new GtpV1Packet.Builder();
							gBuilder.version(gtpV1Header.getVersion()).protocolType(gtpV1Header.getProtocolType())
							.reserved(true)
							.extensionHeaderFlag(false)
							.sequenceNumberFlag(false).nPduNumberFlag(false)
							.messageType(gtpV1Header.getMessageType())
							.correctLengthAtBuild(true)
							.teid(Integer.parseInt("566c76ca", 16))
							.payloadBuilder(ipV4Builder);
							
						UdpPacket.Builder udpBuild=new UdpPacket.Builder();
						udpBuild.dstAddr(ipV4Header.getSrcAddr())
						.srcAddr((Inet4Address)Inet4Address.getByAddress(bs1)).
						srcPort(udpHeader.getSrcPort()).
						dstPort(udpHeader.getDstPort()).correctLengthAtBuild(true).correctChecksumAtBuild(true)
						.payloadBuilder(gBuilder);
						
						
						 final IpV4Packet.Builder ipV4Builder2 = new IpV4Packet.Builder();
						    ipV4Builder2
							  .version(IpVersion.IPV4)
							  .tos(IpV4Rfc791Tos.newInstance((byte)0))
							  .ttl((byte)64)
							  .protocol(IpNumber.UDP)
							  .srcAddr((Inet4Address)Inet4Address.getByAddress(bs1))
							  .dstAddr(ipV4Header.getSrcAddr())
							  .payloadBuilder(udpBuild)
							  .correctChecksumAtBuild(true)
							  .correctLengthAtBuild(true);
						   
						    EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
							etherBuilder.dstAddr(ethernetHeader.getSrcAddr())
									.srcAddr(MacAddress.getByName(macDst, ":")).type(ethernetHeader.getType())
									 .paddingAtBuild(true);
							
							etherBuilder.payloadBuilder(new AbstractBuilder() {
								@Override
								public Packet build() {
									return ipV4Builder2.build();
								}
							});
							Packet p = etherBuilder.build();
							try {
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

}
