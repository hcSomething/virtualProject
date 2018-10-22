package com.hc.frame;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;

import java.io.IOException;

import com.hc.frame.handlers.ServerHandler;
import com.hc.frame.handlers.ServerSender;



public class MyServer {

	private int port = 4001;

	
	public void run() throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup)
			 .channel(NioServerSocketChannel.class)
			 .childHandler(new ChannelInitializer<SocketChannel>() {
				 @Override
				 public void initChannel(SocketChannel ch) throws Exception{
					 ch.pipeline()
					   .addLast(new ServerHandler())
					   .addLast(new ServerSender());
				 }
			 })
			 .option(ChannelOption.SO_BACKLOG, 128)
			 .childOption(ChannelOption.SO_KEEPALIVE, true);
		
			ChannelFuture f = b.bind(port).sync();			

			f.channel().closeFuture().sync();		
		}finally {
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}



	
	/**
	public static void main(String[] args) {
		try {
			new MyServer().run();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	*/
	
	
}
