package com.wspn.jetty;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;  
import org.eclipse.jetty.server.Server;  
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.pcap4j.core.PcapNativeException;

import com.wspn.pcap4j.InitPcap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;  
   
public class JettyServer2 {   
    
    public static void main(String[] args) throws Exception {
       /* // 创建Server
        Server server = new Server(8888);
        // 添加Handler
        ContextHandlerCollection context = new ContextHandlerCollection();
        ContextHandler contextHandler = context.addContext("/", "/");//the ContextHandler just added
        contextHandler.setHandler(new DispatchHandler());//Set the Handler which should be wrapped.

        //Default Handler--该句柄将处理服务器中未处理的请求。 对于favicon.ico的请求，提供Jetty图标。
        //对于'/'的请求，提供具有已知上下文列表的404。 对于所有其他请求，正常的404被提供。
        Handler defaults = new DefaultHandler();
        
        //HandlerCollection--A collection of handlers. 
        //默认实现以列表顺序调用所有处理程序，而不管响应状态或异常如何。
        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[] { context, defaults });
 
        server.setHandler(collection);
 
        // 启动服务
        server.start();
        while (server.isStarted()) {
            System.out.println("server starting...");
            break;
        }
        System.out.println("server stared...");
        System.out.println("ContextHandlerCollectio.getServer() = "
                + context.getServer().hashCode());
        System.out.println("Server:" + server.hashCode());
        server.join();*/
    	Server server = new Server(8889);
    	
    	  ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
          context.setContextPath("/");
          context.setResourceBase(System.getProperty("java.io.tmpdir"));
          server.setHandler(context);
    	
    	
		//ServletHandler handler = new ServletHandler();
		//server.setHandler(handler);
		
		context.addServlet(HelloServlet.class, "/hello");
		context.addServlet(PauseServlet.class, "/pause");
		context.addServlet(PlayServlet.class, "/play");
		context.addServlet(StartDownloadBufferServlet.class, "/start");
		server.start();
		server.join();

    	
    }
    
    public static void start() throws Exception {
        /* // 创建Server
         Server server = new Server(8888);
         // 添加Handler
         ContextHandlerCollection context = new ContextHandlerCollection();
         ContextHandler contextHandler = context.addContext("/", "/");//the ContextHandler just added
         contextHandler.setHandler(new DispatchHandler());//Set the Handler which should be wrapped.

         //Default Handler--该句柄将处理服务器中未处理的请求。 对于favicon.ico的请求，提供Jetty图标。
         //对于'/'的请求，提供具有已知上下文列表的404。 对于所有其他请求，正常的404被提供。
         Handler defaults = new DefaultHandler();
         
         //HandlerCollection--A collection of handlers. 
         //默认实现以列表顺序调用所有处理程序，而不管响应状态或异常如何。
         HandlerCollection collection = new HandlerCollection();
         collection.setHandlers(new Handler[] { context, defaults });
  
         server.setHandler(collection);
  
         // 启动服务
         server.start();
         while (server.isStarted()) {
             System.out.println("server starting...");
             break;
         }
         System.out.println("server stared...");
         System.out.println("ContextHandlerCollectio.getServer() = "
                 + context.getServer().hashCode());
         System.out.println("Server:" + server.hashCode());
         server.join();*/
     	Server server = new Server(8889);
     	
     	  ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
           context.setContextPath("/");
           context.setResourceBase(System.getProperty("java.io.tmpdir"));
           server.setHandler(context);
     	
     	
 		//ServletHandler handler = new ServletHandler();
 		//server.setHandler(handler);
 		
 		context.addServlet(HelloServlet.class, "/hello");
 		context.addServlet(PauseServlet.class, "/pause");
		context.addServlet(PlayServlet.class, "/play");
		context.addServlet(StartDownloadBufferServlet.class, "/start");
 	
 		server.start();
 		server.join();

     	
     }
     
    
}  
 