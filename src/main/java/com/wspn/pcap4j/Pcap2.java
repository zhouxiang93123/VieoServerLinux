package com.wspn.pcap4j;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

/**
 * Hello world!
 *
 */
public class Pcap2 {

	static Long content_length = 0L;
	static Long tmp_content_length = 0L;
	static HashMap<Short, Long> hashMap_contentLength = new HashMap<>();
	static HashMap<Short, Long> hashMap_contentLengthTmp = new HashMap<>();
	static HashMap<Short, Long> hashMap_ack = new HashMap<>();
	static HashMap<Long, Integer> hashMap_everyTcpSegmentLength = new HashMap<>();
	static boolean flag = false;
	static HashMap<Long, Long> hashMap_length_time = new HashMap<>();
	static Timestamp oldTimestamp;
	static Timestamp newTimestamp;
	static Timestamp lastTcpSegmentTime;
	static int count_ack = 0;
	static int count_tcp = 0;
	static int lastTcpSegmentLength = 0;
	static int responseLength = 0;
	static int countTCPSegment = 0;
	static Long firstTcpSegmentSeq;
static Long lengthOfSomeSegment=0L;
	static boolean speed=false;
	static Timestamp responceTime; 
	static List<Double> speedList=new ArrayList<>();
	
	public  void start() throws PcapNativeException {
		
		final PcapHandle handle = InitPcap.phb.build();
		// handle = nif.openLive(snaplen, PromiscuousMode.NONPROMISCUOUS, timeout);

		/** 设置TCP过滤规则 */
		String filter = "(dst host 10.108.147.170 and src host 10.108.144.243) or (dst host 10.108.144.243 and src host 10.108.147.170) ";
		// 设置过滤器
		try {
			handle.setFilter(filter, BpfCompileMode.OPTIMIZE);
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化listener
		PacketListener listener = new PacketListener() {
			public void gotPacket(Packet packet) {

				TcpPacket tcpPacket = packet.get(TcpPacket.class);

				if (tcpPacket != null) {
					Short dstPort = tcpPacket.getHeader().getDstPort().value();
					Short srcPort = tcpPacket.getHeader().getSrcPort().value();
					if (tcpPacket.getPayload() != null) {
						if (tcpPacket.getPayload().length()!=0) {
							byte[] payload = tcpPacket.getPayload().getRawData();
							try {
								String string = new String(payload, "UTF-8");
								if (flag) {
									if (string.contains("HTTP/1.1") && dstPort == 8888) {
										if (!string.contains("dash.all.min.js.map")) {
											//System.out.println(handle.getTimestamp());
											//System.out.println("这是http get请求:\n" + tcpPacket.getPayload().length());
										}
									} else if (string.contains("HTTP/1.1 200") && srcPort == 8888) {
										if (string.indexOf("Content-Length:") > 0) {
											tmp_content_length = 0L;
											String subStr = string.substring(string.indexOf("Content-Length:") + 16,
													string.indexOf("Server:"));
											String contenLengthString = subStr.substring(0, subStr.length() - 2);
											content_length = Long.valueOf(contenLengthString);
											//System.out.println(handle.getTimestamp());
											firstTcpSegmentSeq=tcpPacket.getHeader().getSequenceNumberAsLong();
											//System.out.println("这是response:\n" + content_length);
											responseLength = string.split("\r\n\r\n")[0].length() + 4;
											content_length += responseLength;
											hashMap_contentLength.put(dstPort, content_length);
											responceTime=handle.getTimestamp();
											lengthOfSomeSegment=0L;
											oldTimestamp=handle.getTimestamp();
											//System.out.println(
											//		"hashMap_contentLength放入" + hashMap_contentLength.get(dstPort)+"\nSequenceNumber  "+Long.valueOf((firstTcpSegmentSeq+hashMap_contentLength.get(dstPort))));
										}
									}
									if (srcPort == 8888) {
									
											lengthOfSomeSegment+=tcpPacket.getPayload().length();
											if(handle.getTimestamp().getTime()-oldTimestamp.getTime()>=100&&handle.getTimestamp().getTime()-oldTimestamp.getTime()<5000) {
												long time = handle.getTimestamp().getTime()
														- responceTime.getTime();
												if(time>1000) {
													double speed = (lengthOfSomeSegment / 1024.0 * 8)
															/ (time / 1000.0);
													speedList.add(speed);
													System.out.println(handle.getTimestamp());
													System.out.println("速度为:" + speed + "Kbps"+"时间为:  "+time/1000.0);
													oldTimestamp=handle.getTimestamp();
												}
												
											}
										
										
										if (hashMap_contentLength.containsKey(dstPort)) {
											/*	if (tcpPacket.getHeader().getSequenceNumberAsLong()+tcpPacket.getPayload().length()>= hashMap_contentLength
														.get(dstPort)+firstTcpSegmentSeq-3000&&tcpPacket.getHeader().getSequenceNumberAsLong()+tcpPacket.getPayload().length()< hashMap_contentLength
														.get(dstPort)+firstTcpSegmentSeq) {
													System.out.println(handle.getTimestamp());
													System.out.println("ssss "+tcpPacket.getHeader().getSequenceNumberAsLong()+"  "+tcpPacket.getPayload().length());
													
												}*/
												if (tcpPacket.getHeader().getSequenceNumberAsLong()+tcpPacket.getPayload().length()== hashMap_contentLength
														.get(dstPort)+firstTcpSegmentSeq) {
													lastTcpSegmentTime = handle.getTimestamp();
													//System.out.println(handle.getTimestamp());
													//System.out.println("这是最后一个tcp  "+tcpPacket.getHeader().getSequenceNumberAsLong()+"  "+tcpPacket.getPayload().length());
													//System.out.println("这是最后一个tcp");
													count_tcp++;
													lastTcpSegmentLength = tcpPacket.getPayload().length();
													hashMap_ack.put(dstPort, tcpPacket.getHeader().getSequenceNumberAsLong()
															+ lastTcpSegmentLength);
													//System.out.println(tcpPacket.getHeader().getSequenceNumberAsLong()
													//		+ tcpPacket.getPayload().length());
													hashMap_contentLength.remove(dstPort);
												} 
													
											}
						
										}
								}
								if (string.contains("HTTP/1.1") && string.contains("i.mp4") && dstPort == 8888) {
									flag = true;
								}

							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							// System.out.println("payLoad's length==0");
						}
					} else {
						// System.out.println("no payload");
					}
					if (dstPort == 8888 && flag) {
						if (hashMap_ack.containsKey(srcPort)) {
							/*if (tcpPacket.getHeader().getAcknowledgmentNumberAsLong() > hashMap_ack.get(srcPort)
									- content_length) {
						newTimestamp = handle.getTimestamp();
						//System.out.println(newTimestamp);
						//System.out.println(tcpPacket.getHeader().getAcknowledgmentNumberAsLong());
					}*/
							
							if (tcpPacket.getHeader().getAcknowledgmentNumberAsLong() == hashMap_ack.get(srcPort)||tcpPacket.getHeader().getAcknowledgmentNumberAsLong() == hashMap_ack.get(srcPort)+1) {
								//System.out.println(handle.getTimestamp());
								newTimestamp = handle.getTimestamp();
								count_ack++;
								hashMap_everyTcpSegmentLength.clear();
								System.out.println("这是最后一个ack   " + count_ack + "  " + count_tcp);
								//System.out.println("估计接收到的时间:"
										//+ (new Timestamp((newTimestamp.getTime() + lastTcpSegmentTime.getTime()) / 2)));
								long time = newTimestamp.getTime() - responceTime.getTime();
							//	hashMap_length_time.put(time, content_length);
								double speed = (content_length / 1024.0 * 8) / (time / 1000.0);
								//System.out.println("ack速度为:" + speed + "Kbps");

//								 System.out.println(tcpPacket.getHeader().getAcknowledgmentNumberAsLong() +
//								  "   " + hashMap_ack.get(srcPort));
								// System.out.println("用时:  "+time);
								hashMap_ack.remove(srcPort);
							} 
						}
					} else {
					}
				} else {
					// not TCP
					// System.out.println("not tcp");
				}

			}
		};
		// 直接使用loop
		try {
			handle.loop(-1, listener);
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handle.close();
	}
}
