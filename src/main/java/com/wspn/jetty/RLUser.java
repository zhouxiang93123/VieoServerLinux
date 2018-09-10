package com.wspn.jetty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.pcap4j.core.PcapNativeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wspn.pcap4j.InitPcap;

public class RLUser {
	int qLevel=3;
	int bufLevel=3;
	int bwLevel=3;
	int userNum=3;
	
	
	double alpha = 0.5;
	double gamma = 0.2;// learning parameter
	double[][] R;
	double[][] q;
	double[][] q1;
	int [][]qshow;
	int count = 0;// counter
	int limit = 10;
	int block_ratio = 0;
	int episode_max = 50000;
	double[] total_rate;

	int episode = 0;
	public HashMap<String, User> users = new HashMap<>();
	List<User> usersList = null;
	List<String> usersIPList = null;
	int workload = 0;
	int block = 0;
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	public boolean stop = false;


	public void init(User user) {
		total_rate = new double[50000];
		for (int i = 0; i < total_rate.length; i++) {
			total_rate[i] = 0;
			// System.out.println("ss");
		}
		R = new double[qLevel][userNum*qLevel*bufLevel*bwLevel];
		q = new double[qLevel][userNum*qLevel*bufLevel*bwLevel];
		q1 = new double[qLevel][userNum*qLevel*bufLevel*bwLevel];
		qshow=new int[qLevel][userNum*qLevel*bufLevel*bwLevel];;
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				R[i][j] = 0;
				q[i][j] = 0;
				q1[i][j] = 9999;
				qshow[i][j]=0;
			}
		}
		//readFile("C:\\Users\\Administrator\\workspace\\zx\\logs\\q_"+user.getIp());
		readFile("C:\\Users\\Administrator\\workspace\\zx\\logs\\q_");
		show();
	}
	public void setAction(User user) {
		if (!stop) {
			System.err.println("usersNum:  "+MyProxyServlet2.hashMapPcap.size());
			System.out.println("users:  " + user.ip+"getBwLevel: "+user.getBwLevel()+"getBufLevel: "+user.getBufLevel()+"getqLevel:  "+user.getqLevel());
			user.setState((MyProxyServlet2.hashMapPcap.size()-1)*(qLevel*bufLevel*bwLevel)+(user.getBwLevel()-1)*(qLevel*bufLevel)+(user.getBufLevel()-1)*(qLevel)+(user.getqLevel()-1)+1);
			System.out.println("用户: " + user.getIp() + "的状态为: " + user.getState());
			
				//	if (episode > 1) {
			if (episode > -1) {
						if(Math.random()>-0.1) {
							user.setAction(getMaxAction(user.getState())+1);
							System.out.println("action");
							show1(user.getState()-1);
						}
						else {
							/*if((getMaxAction(user.getState())+1)==1) {
								user.setAction(2);
							}else if ((getMaxAction(user.getState())+1)==qLevel) {
								user.setAction(qLevel-1);
							}else {
								user.setAction(getMaxAction(user.getState())+1+1-(int) (Math.random()*3));
							}*/
							user.setAction((int) (Math.random()*qLevel)+1);
						}
						MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), user.getAction());
						System.out.println("该用户: " + user.ip + "的暂定视频质量为: " + user.getAction());
					} else {
							//user.setAction(1);
						user.setAction((int) (Math.random()*qLevel)+1);
							MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), user.getAction());
							System.out.println("该用户: " + user.ip + "的暂定视频质量为: " + user.getAction());
					}
			
		}
	}

	

	public void episode(User user) {
		if (!stop) {
		//total_rate[episode] = 1.0/(Math.abs((int)((user.getBwLevel()-1)/3+1)-user.getAction())+1.0)+ ((user.getAction()+0.0)/(((int)((user.getBwLevel()-1)/3+1)))+0.0)*(user.getQoe()/2000.0);
		
		total_rate[episode] = 0-Math.abs(user.getBwLevel()-user.getAction())-(user.getAction()+0.0)/(user.getBwLevel()+0.0)*(user.getQoe()/1000.0)-user.getBufferLevel()/4000.0;
		//System.out.println("用户: " + user.getIp() + "的reward为: " + String.valueOf(1.0/(Math.abs((int)((user.getBwLevel()-1)/3+1)-user.getAction())+1.0))+"  "+String.valueOf(((user.getAction()+0.0)/(((int)((user.getBwLevel()-1)/3+1)))+0.0)*(user.getQoe()/2000.0))+"  "+total_rate[episode]);

			//total_rate[episode] = user.getAction()+ user.getQoe()/800.0;
			System.out.println("用户: " + user.getIp() + "的reward为: " +Math.abs(user.getBwLevel()-user.getAction())+"  "+(user.getAction()+0.0)/(user.getBwLevel()+0.0)*(user.getQoe()/1000.0) +"  "+user.getBufferLevel()/4000+"  "+total_rate[episode]);
			System.out.println("user stare:  "+user.getState()+"user next state:  "+((MyProxyServlet2.hashMapPcap.size()-1)*(qLevel*bufLevel*bwLevel)+(user.getBwLevel()-1)*(qLevel*bufLevel)+(user.getBufLevel()-1)*(qLevel)+(user.getqLevel()-1)+1)+" 为  "+getMaxQForNextState((user.getBwLevel()-1)*(qLevel*bufLevel)+(user.getBufLevel()-1)*(qLevel)+(user.getqLevel()-1)+1));
		/*q[user.getAction()-1][user.getState()-1] = (1.0 - gamma)
								* q[user.getAction()-1][user.getState()-1]
								+ gamma * (total_rate[episode]+alpha*getMaxQForNextState((user.getBwLevel()-1)*(qLevel*bufLevel)+(user.getBufLevel()-1)*(qLevel)+(user.getqLevel()-1)+1)) ;// q值更新
*/		
		
	/*	q[user.getAction()-1][user.getState()-1] = (1.0 - gamma)
				* q[user.getAction()-1][user.getState()-1]
				+ gamma * (total_rate[episode]+alpha*getMaxQForNextState((MyProxyServlet2.hashMapPcap.size()-1)*(qLevel*bufLevel*bwLevel)+(user.getBwLevel()-1)*(qLevel*bufLevel)+(user.getBufLevel()-1)*(qLevel)+(user.getqLevel()-1)+1)) ;*/
		
		if(episode>=99999) {
			q[user.getAction()-1][user.getState()-1] = (1.0 - gamma)
					* q[user.getAction()-1][user.getState()-1]
					+ gamma * (total_rate[episode]) ;
			System.out.println("更新q");
		}
		
		
		
		//writeFile("C:\\Users\\Administrator\\workspace\\zx\\logs\\q_"+user.getIp());
		
		/*new Thread(new Runnable() {
			public void run() {
				writeFile("C:\\Users\\Administrator\\workspace\\zx\\logs\\q_"+ip);
			}
			}).start();*/
	  show1(user.getState()-1);
	  System.out.println();
				if (episode >500 ) {
					System.err.println("episode stop");
					stop = true;
					normalizeQ();
				}
			episode++;
			System.out.println("一次episode  " + episode);
		}
	}

	private double getMaxQForNextState(int state) {
		// TODO Auto-generated method stub
		/*double max=q[(int)(qLevel/2)][state-1];
		int max_i=(int)(qLevel/2);*/
		double max=q[1][state-1];
		int max_i=1;
		for(int i=0;i<qLevel;i++) {
			if(max<q[i][state-1]) {
				max=q[i][state-1];
				max_i=i;
			}
		}
		
		return max;
	}

	public double sumQ1jianQ() {
		double sum = 0;
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				// System.out.print(q[i][j]+" ");
				sum += Math.abs(q1[i][j] - q[i][j]);
				// System.out.println(q[i][j]);
			}
			// System.out.println("\n");
		}

		return sum;
	}

	public double sumQ() {
		double sum = 0;
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				if (q[i][j] > 0) {
					sum++;
				}
			}
		}

		return sum;

	}

	public void fuzhi() {
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				q1[i][j] = q[i][j];
			}
		}
	}

	public void show1(int column ) {
		StringBuffer a=new StringBuffer();
		a.append("\n");
		for (int i = 0; i < qLevel; i++) {
			//for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				a.append(String.format("%3.3f", q[i][column]));
			//}
			a.append("\n");
		}
		//logger.info(a.toString());
		System.out.println(a.toString());
	}
	public void show( ) {
		StringBuffer a=new StringBuffer();
		a.append("\n");
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				a.append(String.format("%3.3f", q[i][j])+" ");
			}
			a.append("\n");
		}
		//logger.info(a.toString());
		System.out.println(a.toString());
	}
	public void normalizeQ() {
		// normalize q
		double max = q[0][0];
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				if (max < q[i][j]) {
					max = q[i][j];
				}
			}
		}
		if (max > 0) {
			for (int i = 0; i < qLevel; i++) {
				for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
					q[i][j] = 100.0 * q[i][j] / max;
					System.out.print(String.format("%3.0f", q[i][j]) + "  ");
				}
				System.out.println("");
			}
		}
	}

	public double max() {
		// normalize q
		double max = q[0][0];
		for (int i = 0; i < qLevel; i++) {
			for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
				if (max < q[i][j]) {
					max = q[i][j];
				}
			}
		}
		return max;
	}
	private int getMaxAction(int state) {
		// TODO Auto-generated method stub
	//	double max=q[(int)(qLevel/2)][state-1];
	//	int max_i=(int)(qLevel/2);
		double max=q[1][state-1];
		int max_i=1;
		for(int i=0;i<qLevel;i++) {
			if(max<q[i][state-1]) {
				max=q[i][state-1];
				max_i=i;
			}
		}
		
		return max_i;
	}
	
	private void readFile(String filrPath) {
		File file = new File(filrPath);
		BufferedReader reader = null;
		if (file.isFile()) {
			int line = 0;
			try {
				String temp = null;
				reader = new BufferedReader(new FileReader(file));
				while ((temp = reader.readLine()) != null) {
					int state=0;
					for(String string:temp.split("  ")) {
						q[line][state++]=Double.valueOf(string);
						//qshow[line][state++]=(int)(q[line][state++]*1000);
					}
					line++;
				}
				reader.close();
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
	
	public void writeFile(String filrPath) {
		
		File file=new File(filrPath);   
	    if(!file.exists())   
	    {   
	        try {   
	            file.createNewFile();   
	        } catch (IOException e) {   
	            // TODO Auto-generated catch block   
	            e.printStackTrace();   
	        }   
	    } 
		
		FileWriter fw = null;
	    try {
			fw = new FileWriter(file);
			for (int i = 0; i < qLevel; i++) {
				for (int j = 0; j < userNum*qLevel*bufLevel*bwLevel; j++) {
					if(j==0) {
						fw.write(String.format("%3.3f", q[i][j]));
					}else {
						fw.write("  "+String.format("%3.3f", q[i][j]));
					}
				}
				if(i!=qLevel-1) {
					fw.write("\n");
				}
			}  
			fw.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
	}
	
}
