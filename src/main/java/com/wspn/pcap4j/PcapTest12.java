package com.wspn.pcap4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

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

import com.wspn.jetty.MyProxyServlet2;
import com.wspn.jetty.RL;
import com.wspn.jetty.RLUser;
import com.wspn.jetty.User;

/**
 * Hello world!
 *
 */
public class PcapTest12 {

	boolean flagflag=false;
	
	int segTime=4000;
	
	 Long content_length = 0L;
	 long content_total=0L;
	 long time_total=0L;
	 Long tmp_content_length = 0L;
	 HashMap<Short, Long> hashMap_contentLength = new HashMap<>();
	 HashMap<Short, Long> hashMap_contentLengthTmp = new HashMap<>();
	 HashMap<Short, Long> hashMap_ack = new HashMap<>();
	 HashMap<Long, Integer> hashMap_everyTcpSegmentLength = new HashMap<>();
	 boolean flag = false;
	 HashMap<Long, Long> hashMap_length_time = new HashMap<>();
	 Timestamp oldTimestamp;
	 Timestamp newTimestamp;
	 Timestamp lastTcpSegmentTime;
	 int count_ack = 0;
	 int count_tcp = 0;
	 int lastTcpSegmentLength = 0;
	 int responseLength = 0;
	 int countTCPSegment = 0;
	 Long firstTcpSegmentSeq;
	 Long lengthOfSomeSegment=0L;
	 boolean speed=false;
	 Timestamp responceTime; 
	 List<Double> speedList=new ArrayList<>();
	 String ip=null;
	User user=new User();
	
	Long oldLength=0L;
	Long newLength=0L;
	Long oldTime;
	Long newTime;
	Long totalLength=0L;
	Double speedTest=0.0; 
	long lastRequstTime=0L;
	long lastFinTime=91513761301594L;
	Boolean threadFlag=false;
	Boolean firstAck=false;
	Long firstAckNum;
	Long newAckNum;
	Long oldAckTime;
	Long oldAckNum;
	Long newAckTime;
	Long firstAckTime;
	
	long beginPlay=0;
	long beginStop=0;
	int bufferAck=-1;
	int oldbufferAck=0;
	
	int speedAction=100000;
	
	long episodeOldTime;
	long baseTime; 
	
	long playTime=0;
	long stoptime=0;
	long stoptimeOld=0;
	long Bt=0;
	long BTOld=0;
			
	
	long firstPlayTime=0;
	int playedSeg=0;
	long playedSeqTime=0;
	
	int stopTimes=0;
	
	RLUser rlUser=new RLUser();
	
	FileWriter fw = null;
	File file=null;
	
	 public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public List<Double> getSpeedList() {
		return speedList;
	}


	public void setSpeedList(List<Double> speedList) {
		this.speedList = speedList;
	}


	String filter;
	
	
	public  String getFilter() {
		return filter;
	}


	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	boolean myThread2Flag=false;
	boolean consume=false;
	public class MyThread2 extends Thread{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if(myThread2Flag) {
					/*if ((System.currentTimeMillis()-beginPlay)%100==0) {
						System.err.println(System.currentTimeMillis()-beginPlay);
					}*/
					
					if((System.currentTimeMillis()-beginPlay)>(segTime+100)&&bufferAck>=2) {
						bufferAck=bufferAck-1;
						System.err.println("消耗buffer  "+bufferAck);
						//beginPlay=beginPlay+8000;
						
						beginPlay=beginPlay+segTime;
						playedSeqTime=(System.currentTimeMillis()-100-firstPlayTime-stoptime);
						playedSeg=(int)((System.currentTimeMillis()-100-firstPlayTime-stoptime)/segTime);
						System.out.println("paly stop total:   "+playedSeg*segTime+"  "+stoptime+"  "+(System.currentTimeMillis()-100-firstPlayTime));
					}
					
					if(System.currentTimeMillis()-beginPlay>bufferAck*segTime) {
						
						beginStop=System.currentTimeMillis();
						playTime+=(beginStop-beginPlay);
						
						System.out.println("卡顿  "+bufferAck);
						stopTimes++;
						bufferAck=0;
						playedSeqTime=(System.currentTimeMillis()-firstPlayTime-stoptime);
						playedSeg=(int)((System.currentTimeMillis()-firstPlayTime-stoptime)/segTime);
						System.out.println("paly stop total:   "+playedSeg*segTime+"  "+stoptime+"  "+(System.currentTimeMillis()-firstPlayTime));
						myThread2Flag=false;
						
				}
				}else {
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				}
				
		}
		
	
	public class MyThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if(System.currentTimeMillis()-lastFinTime>5000) {
					System.err.println("stop");
					MyProxyServlet2.hashMapPcap.remove(ip);
					rlUser.writeFile("C:\\Users\\Administrator\\workspace\\zx\\logs\\q_"+ip);
					myThread2Flag=false;
					try {
						Thread.sleep(99999999);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	public class MyThreadSpeedTest extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("开始测速");
			while (true) {  
                oldLength= totalLength;
                oldTime=System.currentTimeMillis();
                System.out.println(oldTime+"  "+oldLength);
                try {  
                    Thread.sleep(1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                newLength=totalLength;
                newTime=System.currentTimeMillis();
                System.out.println(newTime+"  "+newLength);
                speedTest = ((newLength-oldLength) / 1024.0 * 8.0/1024.0)
						/ ((newTime-oldTime) / 1000.0);
				speedList.add(speedTest);
				if(ip!=null) {
					System.out.println(ip+" 速度为: " +String.format("%.3f", speedTest)  + "Mb/s"+"时间为:  "+(newTime-oldTime)/1000.0);
					user.setChannel(2);
					System.out.println("user.setChannel(2);");
				}
            }  
		}  
		
		}  
	
	MyThreadSpeedTest myThreadSpeedTest=new MyThreadSpeedTest();
	MyThread myThread=new MyThread();
	MyThread2 myThread2=new MyThread2();
	PcapHandle handle;
	public  void start() throws PcapNativeException {

		handle = InitPcap.phb.build();
		// handle = nif.openLive(snaplen, PromiscuousMode.NONPROMISCUOUS, timeout);

		/** 设置TCP过滤规则 */
	//	String filter = "(dst host 10.108.147.170 and src host 10.108.145.180) or (dst host 10.108.145.180 and src host 10.108.147.170) ";

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
								
						if (string.contains("HTTP/1.1") && string.contains("i.mp4") && dstPort == 8888) {
									firstAckNum=tcpPacket.getHeader().getAcknowledgmentNumberAsLong() ;
									oldAckNum=tcpPacket.getHeader().getAcknowledgmentNumberAsLong() ;
									firstAckTime=System.currentTimeMillis();
									oldAckTime=System.currentTimeMillis();
									flag = true;
									file=new File("C:\\Users\\Administrator\\workspace\\zx\\logs\\"+ip);
									
								/*	if(string.contains("4Mbps")) {
										user.setRequest(2);
										System.out.println("user.setRequest(2);");
										user.setAction(2);
									}else if (string.contains("2Mbps")) {
										user.setRequest(1);
										System.out.println("user.setRequest(1);");
										user.setAction(2);
									}else {
										System.out.println("unknown request");
									}*/
							//	RL.setAction();
									//myThreadSpeedTest.start();
									if(!threadFlag) {
										myThread.start();
										threadFlag=true;
									}
								}
								
								
								if (flag) {
									if (string.contains("HTTP/1.1") && dstPort == 8888) {
										if (!string.contains("dash.all.min.js.map")) {
											
											lastRequstTime=System.currentTimeMillis();
											
											firstAckNum=tcpPacket.getHeader().getAcknowledgmentNumberAsLong() ;
											oldAckNum=tcpPacket.getHeader().getAcknowledgmentNumberAsLong() ;
											firstAckTime=System.currentTimeMillis();
											oldAckTime=System.currentTimeMillis();
											
											System.out.println("这是http get请求:  "+handle.getTimestamp()+"  "+System.currentTimeMillis());
											responceTime=handle.getTimestamp();
											flagflag=true;
											//System.out.println("Bt:  "+(4000*(count_ack-1-playedSeg)-(System.currentTimeMillis()-beginPlay)));
											
											/*
											if((count_ack-1)>=4&&(count_ack-1)%3==0) {
												user.setQoe(bufferAck-oldbufferAck);
												oldbufferAck=bufferAck;
												RL.episode();
											}*/
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
											
											responseLength = string.split("\r\n\r\n")[0].length() + 4;
											content_length += responseLength;
											hashMap_contentLength.put(dstPort, content_length);
											System.out.println("这是response:\n" + content_length+" "+firstTcpSegmentSeq+"  "+(content_length+firstTcpSegmentSeq)+" "+System.currentTimeMillis());
											lengthOfSomeSegment=0L;
											oldTimestamp=handle.getTimestamp();
											//System.out.println(
											//		"hashMap_contentLength放入" + hashMap_contentLength.get(dstPort)+"\nSequenceNumber  "+Long.valueOf((firstTcpSegmentSeq+hashMap_contentLength.get(dstPort))));
										}
									}
									if (srcPort == 8888) {
								
										//System.out.println(tcpPacket.getHeader().getSequenceNumberAsLong()+tcpPacket.getPayload().length()+"  "+System.currentTimeMillis());
										lastFinTime=System.currentTimeMillis();
										
										if (hashMap_contentLength.containsKey(dstPort)) {
												if (tcpPacket.getHeader().getSequenceNumberAsLong()+tcpPacket.getPayload().length()+1514*4>hashMap_contentLength
														.get(dstPort)+firstTcpSegmentSeq&&flagflag) {
													//lastTcpSegmentTime = handle.getTimestamp();
													//System.out.println(handle.getTimestamp());
													//System.out.println("这是最后一个tcp  "+tcpPacket.getHeader().getSequenceNumberAsLong()+"  "+tcpPacket.getPayload().length());
													System.out.println("这是最后一个tcp  "+" "+handle.getTimestamp()+"  "+System.currentTimeMillis());
													flagflag=false;
													long timeold=System.currentTimeMillis();
													count_tcp++;
													//lastTcpSegmentLength = tcpPacket.getPayload().length();
													/*hashMap_ack.put(dstPort, tcpPacket.getHeader().getSequenceNumberAsLong()
															+ lastTcpSegmentLength);*/
													//System.out.println(tcpPacket.getHeader().getSequenceNumberAsLong()
													//		+ tcpPacket.getPayload().length());
													
													
													//System.out.println(tcpPacket.getHeader().getAcknowledgmentNumberAsLong());
														newTimestamp = handle.getTimestamp();
														count_ack++;
														//System.out.println("估计接收到的时间:"
																//+ (new Timestamp((newTimestamp.getTime() + lastTcpSegmentTime.getTime()) / 2)));
														
														System.out.println(ip+"  这是最后一个ack   " + count_ack + "  " + count_tcp);
														
														long time = newTimestamp.getTime() - responceTime.getTime();
													//	hashMap_length_time.put(time, content_length);
														System.out.println("content_length: "+ content_length+"time: "+time);
														content_total+=content_length;
														time_total+=time;
														double speed = (content_length / 1024.0 * 8) / (time / 1000.0);
														
														
														/*if(count_ack==2) {
															baseTime=time*3;
														}*/
														
															speedList.add(speed);
														
														System.out.println("ack速度为:" + speed + "Kbps");
														
														
														bufferAck++;
														
														System.out.println("buffer length:  "+bufferAck);
														int a=(int)(speedComputer()/4000)+1;
														System.out.println(a);
														if(a>3) {
															a=3;
														}
														user.setBwLevel(a);
														/*if(speed<4000) {
															user.setBwLevel(1);
														}else {
															user.setBwLevel(2);
														}
														*/
														System.out.println("user:  "+ip+"的带宽等级为: "+user.getBwLevel());
														
														if(bufferAck<6) {
															user.setBufLevel(1);
														}else if (bufferAck<12) {
															user.setBufLevel(2);
														}
														else {
															user.setBufLevel(3);
														}
														System.out.println("user:  "+ip+"的buf等级为: "+user.getBufLevel());
														
														stoptimeOld=stoptime;
														
														if(myThread2Flag==false&&count_ack>2) {
															beginPlay=System.currentTimeMillis();
															stoptime+=(beginPlay-beginStop);
															myThread2Flag=true;
															System.out.println("重新播放");
														}
														if(count_ack==2) {
															myThread2Flag=true;
															 myThread2.start();
															 beginPlay=System.currentTimeMillis();
															 firstPlayTime=beginPlay;
															 System.out.println("start buffer test");
														 }
														
														System.out.println("paly stop total:   "+(System.currentTimeMillis()-firstPlayTime-stoptime)+"  "+stoptime+"  "+(System.currentTimeMillis()-firstPlayTime));
														BTOld=Bt;
														
														Bt=(segTime*(count_ack-1)-playedSeqTime)-(System.currentTimeMillis()-beginPlay);
														
														System.out.println("Bt:  "+Bt);
														
														System.out.println("新增等待时间:  "+(stoptime-stoptimeOld));
														System.out.println("新增Bt:  "+(Bt-BTOld));
														/*if(count_ack==2) {
															user.setqLevel(3);
															System.out.println("user:  "+ip+"的q等级为: "+user.getqLevel());
															rlUser.setAction(user);
														}*/
														
														try {
															fw = new FileWriter(file,true);
															fw.write(count_ack+"  "+user.getAction()+"  "+(stoptime-stoptimeOld)+"  "+bufferAck+"\n");
															fw.close();
														} catch (IOException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														
														
												/*		if(count_ack>2) {
															try {
																fw = new FileWriter(file,true);
																fw.write(count_ack+"  "+user.getAction()+"  "+(stoptime-stoptimeOld)+"\n");
																fw.close();
															} catch (IOException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
															}
															user.setBufferLevel(Math.max(0, 12000-(int) Bt));
															user.setQoe((int)(stoptime-stoptimeOld));
															//user.setQoe((int)(Bt-BTOld-4*(stoptime-stoptimeOld)));
															
															if(user.getAction()!=user.getAction2()) {
																user.setAction(user.getAction2());
																System.err.println("action error");
															}
															
															if(content_length<2*1024*1024) {
																user.setAction(1);
															}else if (content_length<4*1024*1024) {
																user.setAction(2);
															}else {
																user.setAction(3);
															}
															
															user.setSwitches(Math.abs(user.getqLevel()-user.getAction()));
															System.out.println("user:  "+ip+"的Switches: "+user.getSwitches());
															
															rlUser.episode(user);
															
															user.setqLevel(user.getAction());
															System.out.println("user:  "+ip+"的q等级为: "+user.getqLevel());
															rlUser.setAction(user);
														}*/
													hashMap_ack.remove(srcPort);
													hashMap_contentLength.remove(dstPort);
													System.out.println("ssss:  "+(System.currentTimeMillis()-timeold));
												} 
													
											}
						
										}
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
	
	
	public double speedComputer() {
		
		int a=speedList.size();
		if(a==1) {
			return speedList.get(0);
		}else if (a==0) {
			return 0;
		}
		else {
			return (0.8*speedList.get(a-1)+0.2*speedList.get(a-2));
		}
		
		
	}
	public void creatFilr(String add) {
		rlUser.init(user);
		File file=new File("C:\\Users\\Administrator\\workspace\\zx\\logs\\"+add);   
	    if(!file.exists())   
	    {   
	        try {   
	            file.createNewFile();   
	        } catch (IOException e) {   
	            // TODO Auto-generated catch block   
	            e.printStackTrace();   
	        }   
	    } 
	}
	
}
