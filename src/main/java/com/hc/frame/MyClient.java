package com.hc.frame;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.Channel;

import com.hc.frame.swing.CommandAction;
import com.hc.frame.swing.GameDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hc.frame.handlers.ClientDisplayHandler;
import com.hc.frame.handlers.ClientSender;
import com.hc.frame.handlers.TestClientHadler;


public class MyClient {
	
	//private static String host = ""; //String host = "127.0.0.1"
	//private static int port = 1; //int port = 4001;
	static GameDisplay gameDisplay;
	static CommandAction action;
	public static Channel clientChannel;
	
	private static AtomicBoolean isStart = new AtomicBoolean(false);

	
	ClientContext clientContext = ClientContext.getInstance();
	
	public static void main(String[] args) throws Exception{
		gameDisplay = new GameDisplay();
		gameDisplay.startGame(); //启动显示器
		if(isStart.get() == false) {
			//Channel channel = new MyClient().start("127.0.0.1", 4001);
		}
		
		
	}
	
	
	
	
	public static Channel start(String host, int port){
		
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			Bootstrap b = new Bootstrap();
			b.group(workerGroup)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.SO_KEEPALIVE, true)
			 .handler(new ChannelInitializer<SocketChannel>() {
				 @Override
				 public void initChannel(SocketChannel ch) throws Exception{
					 ch.pipeline().addLast(new ClientDisplayHandler())
					              .addLast(new ClientSender());
				 }
			 });
			
			ChannelFuture f = null;
			try {
				f = b.connect(host, port).sync();
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
			
			
			Channel channel = f.channel();
			clientChannel  = channel;
			//重要
			action = gameDisplay.getCommandAction();
			ClientContext.getMap().put(channel, action);
			System.out.println("map中有：" + ClientContext.getMap().size());

			
			CommandAction.setChannel(channel); 
			
			
			
			try {
				channel.closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return channel;
		}finally{
			workerGroup.shutdownGracefully();
		}
		
		
		
	}

}
