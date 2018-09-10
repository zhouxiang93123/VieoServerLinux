package com.wspn.jetty;

import java.awt.Desktop.Action;
import java.io.*;

import org.tensorflow.Tensor;

public class StartDQN {
	public static void main(String[] args) throws IllegalArgumentException, IOException {
		System.out.println(System.currentTimeMillis());
		MyGraph.init("C:\\Users\\Administrator\\workspace\\zx\\src\\main\\java\\com\\wspn\\jetty\\Mygraph3.pb");
		System.out.println(System.currentTimeMillis());
		float[][] input = {{1, 8, 3}};
		Tensor<?> s = Tensor.create(input);
		System.out.println(System.currentTimeMillis());
		Tensor<?> action = MyGraph.sess.runner().feed("s", s).fetch("eval_net/aset").run().get(0);
		System.out.println(System.currentTimeMillis());
		long [] ashape = action.shape();
		int abatchSize = (int) ashape[0];
		long[] aresult=new long [abatchSize];
		action.copyTo(aresult);
		for (int i = 0; i < aresult.length; i++) {
			System.out.println(aresult[i]+1);
		}
		action.close();
		s.close();
		MyGraph.close();
		System.out.println(System.currentTimeMillis());
	}
}
