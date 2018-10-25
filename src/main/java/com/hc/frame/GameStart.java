package com.hc.frame;
import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;



public class GameStart {
	

	private static GameStart gameStart;
	private static MyServer myServer;
	private static ApplicationContext appContext;
	private static Context con;
	
	public static void main(String[] args) {
		
		//加入spring初始化和配置bean
		appContext = new AnnotationConfigApplicationContext(AppConfig.class);

		
		//gameStart = new GameStart();
		//gameStart.initialize(); //所有实体的初始化
		//con = Context.getInstance();
		con = appContext.getBean(Context.class);
				
		
		try {
			//new MyServer().run();
			myServer = new MyServer();
			myServer.run();
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	

		gameStart.alive();
		
	}
	
	/**
	 * 所有实体的初始化
	 */

	
	public void alive() {
		
		while(true) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					stop();
				}
			});
		}
	}

}
