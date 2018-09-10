package com.wspn.pcap4j;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.xdevapi.JsonArray;

public class test {
public static void main(String[] args) {
	 String url1 = "http://10.108.146.152:8088/ssmStudy/query?ip=172.16.0.2";
     String result1 = HttpUtil.sendGet(url1);
     System.out.println(result1);
     JSONObject jsonObject=new JSONObject(result1);
     JSONArray jsonArray =jsonObject.getJSONArray("result");
     double sum2aveV = 0.0;
     int num = 0;
     for(int i=0;i<jsonArray.length();i++) {
    	 double tmp = 0.0;
    	 JSONObject jsonObject2=jsonArray.getJSONObject(i);
    	 if((tmp=jsonObject2.getDouble("bandwidth")) >102) {
    		 if(tmp > sum2aveV) {
    			 sum2aveV=tmp;
    		 }
    	 }
//    	 System.out.println(jsonObject2.get("bandwidth"));
     }
     sum2aveV = sum2aveV *2 *8/1024;
	 System.out.println("average: " + sum2aveV);
   int a=999;
   System.out.println(a/1000);
}
}
