package com.wspn.jetty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.wspn.pcap4j.InitPcap;

public class RL {
	static int Num_User = 10;
	static double alpha = 1;
	static double gamma = 0.7;// learning parameter
	static double[][] R ;
	static double[][] q ;
	static double[][] q1;
	static int count = 0;// counter
	static int limit = 10;
	static int block_ratio = 0;
	static int episode_max = 50000;
	static double[] total_rate;
	
	static int episode = 0;
	public static HashMap<String, User> users = new HashMap<>();
	static List<User> usersList = null;
	static List<String> usersIPList = null;
	static int workload = 0;
	static int block = 0;

	public static boolean stop=false;
	
	public static void main(String[] args) {
		init();
		run();
		normalizeQ();
	}
	
	public static void init() {
		total_rate = new double[50000];
		for (int i = 0; i < total_rate.length; i++) {
			total_rate[i] = 0;
			//System.out.println("ss");
		}
		R=new double[3][4*Num_User];
		q=new double[3][4*Num_User];
		q1=new double[3][4*Num_User];
		for(int i=0;i<3;i++) {
			for(int j=0;j<4*Num_User;j++) {
				R[i][j]=0;
				q[i][j]=0;
				q1[i][j]=999;
			}
		}
	}
	
	public static void run() {
		while (true) {
			users.clear();
			int a=(int) (Math.random()*2)+1;
			//int a=2;
			System.out.println(a);
			for(int i=0;i<a;i++) {
				User user=new User();
				//user.setRequest((int) (Math.random()*2)+1);
				//user.setChannel((int) (Math.random()*2)+1);
				user.setRequest(2);
				user.setChannel(2);
				if(user.getRequest()==1) {
					if(user.getChannel()==1) {
						user.setState(1);
					}else {
						user.setState(2);
					}
				}else {
					if(user.getChannel()==1) {
						user.setState(3);
					}else {
						user.setState(4);
					}
				}
				users.put(String.valueOf(i), user);
			}

			usersList = new ArrayList<User>(users.values());
			
			System.out.println(users.size());
			workload=0;
			block=0;
			for (User user : usersList) {
				//System.out.println(user.getState());
				if (q[1][user.getState()-1] <=q[2][user.getState()-1]&&Math.random()>0.3) {
					user.setAction(2);
				} else {
					user.setAction(1);
				}
				workload = workload + user.getAction();
				if (workload > limit) {
					//System.out.println(limit + user.getAction() - workload);
					user.setAction(limit + user.getAction() - workload);
					//System.out.println("break");
					break;
				}
			}
			for (User user : usersList) {
				user.setRate(Math.min(Math.min(user.getAction(), user.getChannel()), user.getRequest()));
				if (user.getRate() != 0) {
					block++;
				}
				total_rate[episode] = total_rate[episode] + user.getRate();
				//System.out.println(total_rate[episode] );
			}
			for (User user : usersList) {
				
				q[user.getAction()][user.getState()+(usersList.size()-1)*4-1] = (1.0 - gamma) * q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]
						+ gamma * total_rate[episode];// q值更新
				show();
				System.out.println();
			}
			block_ratio = block_ratio + block / usersList.size();
			total_rate[episode ] = 200.35 - sumQ1jianQ();
			//System.err.println(sumQ1jianQ());
			if (sumQ1jianQ() < 1 && sumQ() > 0) {
				if (count > 10) {
					System.err .println(episode);
					break;
				} else {
					count++;
					//System.out.println(count);
				}
			} else {
				//System.err.println("jjj");
				fuzhi();
				count = 0;
			}
			alpha = alpha * (episode_max - episode) / episode_max;
			gamma = gamma * (episode_max - episode) / episode_max;
			episode++;
			//System.out.println(episode);
		}
	}

	public static void setAction(String ip) {
		if(!stop) {
			
			usersList = new ArrayList<User>(users.values());
			usersIPList=new ArrayList<String>(users.keySet());
			System.out.println("users:  "+usersList.size());
			for(User user:usersList) {
				if(user.getIp().equals(ip)) {
					if(user.getRequest()==1) {
						if(user.getChannel()==1) {
							user.setState(1);
						}else {
							user.setState(2);
						}
					}else {
						if(user.getChannel()==1) {
							user.setState(3);
						}else {
							user.setState(4);
						}
					}
				}
				
			}	
				workload=0;
				block=0;
				for (User user : usersList) {
					if(user.getIp().equals(ip)) {
						System.out.println("用户: "+user.getIp()+"的状态为: "+user.getState());
						if(episode>9) {
							if (q[1][user.getState()+(usersList.size()-1)*4-1] <=q[2][user.getState()+(usersList.size()-1)*4-1]&&Math.random()>0.3) {
								user.setAction(2);
								MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 2);
								System.out.println("该用户: "+user.ip+"的限制速度: "+20000);
							} else {
								user.setAction(1);
								MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 1);
								System.out.println("该用户: "+user.ip+"的限制速度: "+10000);
							}
						}else {
							if(Math.random()>0.5) {
								user.setAction(2);
								MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 2);
								System.out.println("该用户: "+user.ip+"的限制速度: "+20000);
							}else {
								user.setAction(1);
								MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 1);
								System.out.println("该用户: "+user.ip+"的限制速度: "+10000);
							}
						}
					}
						/*workload = workload + user.getAction();
						if (workload > limit) {
							//System.out.println(limit + user.getAction() - workload);
							user.setAction(limit + user.getAction() - workload);
							MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), user.getAction());
							//System.out.println("break");
							break;
						}*/
					
		
				}
		}
	}
	
	public static void episode(String ip) {
		
		if(!stop) {
		
			for (User user : usersList) {
				//user.setRate(Math.min(Math.min(user.getAction(), user.getChannel()), user.getRequest()));
				if (user.getRate()==0 ) {
					block++;
				}
				/*if(user.getState()==1) {
					if(user.getAction()==2) {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}else {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}
				}else if (user.getState()==2) {
					if(user.getAction()==2) {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}else {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}
				}else if (user.getState()==3) {
					if(user.getAction()==2) {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}else {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}
				}else {
					if(user.getAction()==2) {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}else {
						total_rate[episode] = total_rate[episode] + user.getQoe();
					}
				}*/
				if(user.getIp().equals(ip)) {
					total_rate[episode] = total_rate[episode] +user.getAction()/2.0+ (user.getQoe()/2000.0);
					System.out.println("用户: "+user.getIp()+"的reward为: "+total_rate[episode]);
				}
			
			}
			for (User user : usersList) {
				if(user.getIp().equals(ip)) {
				if(user.getAction()!=0) {
				/*	if(total_rate[episode]<0) {
						if(user.getAction()==1) {
							q[1][user.getState()+(usersList.size()-1)*4-1] = (1.0 - gamma) * q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]
									+ gamma * (total_rate[episode]);// q值更新
							q[2][user.getState()+(usersList.size()-1)*4-1] = (1.0 - gamma) * q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]
									+ gamma * (total_rate[episode]);// q值更新
							System.out.println("更新的值: "+q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]);
						}else {
							q[user.getAction()][user.getState()+(usersList.size()-1)*4-1] = (1.0 - gamma) * q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]
									+ gamma * (total_rate[episode]);// q值更新
							System.out.println("更新的值: "+q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]);
						}
						
					}else {
						q[user.getAction()][user.getState()+(usersList.size()-1)*4-1] = (1.0 - gamma) * q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]
								+ gamma * (total_rate[episode]);// q值更新 
						System.out.println("更新的值: "+q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]);
					}*/
					q[user.getAction()][user.getState()+(usersList.size()-1)*4-1] = (1.0 - gamma) * q[user.getAction()][user.getState()+(usersList.size()-1)*4-1]
							+ gamma * (total_rate[episode]);// q值更新 
					
					show();
					System.out.println();
				}else {
					System.err.println("getAction  "+0);
				}
				}
			}
			//block_ratio = block_ratio + block / usersList.size();
			//total_rate[episode ] = 200.35 - sumQ1jianQ();
			//System.err.println(sumQ1jianQ());
			if (sumQ1jianQ() < 1 && sumQ() > 0) {
				if (count > 30) {
					System.err .println("episode stop");
					stop=true;
					
					for (User user : usersList) {
						int a=0;
						double max=q[a][user.getState()+(usersList.size()-1)*4-1];
						for(int i=1;i<3;i++) {
							if(max<q[i][user.getState()+(usersList.size()-1)*4-1]) {
								a=i;
							}
						}
						if(a==0) {
							MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 0);
							System.out.println("该用户: "+user.ip+"的限制速度: "+0);
						}else if (a==1) {
							MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 1);
							System.out.println("该用户: "+user.ip+"的限制速度: "+1);
						}else {
							MyProxyServlet2.hashMapIpSpeed.put(user.getIp(), 2);
							System.out.println("该用户: "+user.ip+"的限制速度: "+2);
						}
					}
					normalizeQ();
					
				} else {
					count++;
					//System.out.println(count);
				}
			} else {
				//System.err.println("jjj");
				fuzhi();
				count = 0;
			}
			//alpha = alpha * (episode_max - episode) / episode_max;
			//gamma = gamma * (episode_max - episode) / episode_max;
			episode++;
			System.out.println("一次episode  "+episode);
		}
	}
	
	
	public static double sumQ1jianQ() {
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4*Num_User; j++) {
				//System.out.print(q[i][j]+" ");
				sum += Math.abs(q1[i][j] - q[i][j]);
				//System.out.println(q[i][j]);
			}
			//System.out.println("\n");
		}

		return sum;
	}

	public static double sumQ() {
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4*Num_User; j++) {
				if (q[i][j] > 0) {
					sum++;
				}
			}
		}

		return sum;

	}

	public static void fuzhi() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4*Num_User; j++) {
				q1[i][j] = q[i][j];
			}
		}
	}

	public static void show() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4*Num_User; j++) {
				System.out.print(String.format("%3.3f", q[i][j]) + "  ");
			}
			System.out.println("");
		}
	}
	public static void normalizeQ() {
		// normalize q
		double max = q[0][0];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4*Num_User; j++) {
				if (max < q[i][j]) {
					max = q[i][j];
				}
			}
		}
		if (max > 0) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 4*Num_User; j++) {
					q[i][j] = 100.0 * q[i][j] / max;
					System.out.print(String.format("%3.0f", q[i][j]) + "  ");
				}
				System.out.println("");
			}
		}
	}
	
	public static double max() {
		// normalize q
		double max = q[0][0];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4*Num_User; j++) {
				if (max < q[i][j]) {
					max = q[i][j];
				}
			}
		}
	   return max;
	}
	
	
}
