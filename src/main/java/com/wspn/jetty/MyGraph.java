package com.wspn.jetty;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.tensorflow.Tensor;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.types.UInt8;  

/**
 * @author wspn
 *
 */
public class MyGraph {
   public static Graph graph;
   public static Session sess;
   static public void init(String path) throws IllegalArgumentException, IOException {
	   System.out.println("graph 初始化开始");
	   		graph = new Graph();
		   graph.importGraphDef(Files.readAllBytes(Paths.get(path)));
		   sess = new Session(graph);
		   System.out.println("graph 初始化完成");
   }
   
   public Graph getGraph() {
	return graph;
}

   public void setGraph(Graph graph) {
	this.graph = graph;
}
   
   public Session getSess() {
	return sess;
}

   public void setSess(Session sess) {
	this.sess = sess;
}
   public static void close() {
	sess.close();
	graph.close();
}

//public static void main(String[] args) throws IllegalArgumentException, IOException {
//	MyGraph myGraph = new MyGraph("E:\\PycharmProjects\\DQN\\pb_dir\\Mygraph.pb");
//	float[][] input = {{1, 3, 1}, {1, 3, 3}, {1, 2, 3}, {1, 1, 2}, {1, 2, 1}, {1, 3, 3}, {1, 3, 3}, {1, 3, 3}, 
//    		{1, 2, 3}, {1, 3, 2}, {1, 3, 3}, {1, 2, 3}, {1, 3, 2}, {1, 2, 3}};
//	Tensor<?> s = Tensor.create(input);
//	Tensor<?> action = myGraph.getSess().runner().feed("s", s).fetch("eval_net/aset").run().get(0);
//	long [] ashape = action.shape();
//	int abatchSize = (int) ashape[0];
//	long[] aresult=new long [abatchSize];
//	action.copyTo(aresult);
//	for (int i = 0; i < aresult.length; i++) {
//		System.out.println(aresult[i]+1);
//	}
//	action.close();
//	s.close();
//	myGraph.close();
//}
 
	
}
