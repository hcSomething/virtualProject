package com.hc.frame;
import java.util.*;



public class GameStart {
	

	private static GameStart gameStart;
	private static MyServer myServer;
	
	public static void main(String[] args) {
		gameStart = new GameStart();
		gameStart.initialize(); //所有实体的初始化

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
	public void initialize() {
		Context.initialize();
	}
	
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
