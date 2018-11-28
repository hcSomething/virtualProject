package com.hc.frame.handlers;

import io.netty.channel.ChannelInboundHandlerAdapter;

import com.hc.frame.ClientContext;
import com.hc.frame.swing.CommandAction;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ClientDisplayHandler extends ChannelInboundHandlerAdapter{

	CommandAction commandAction;

	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		commandAction = CommandAction.getInstance();
		System.out.println("这里是ClientH的channelActive");
	}

	//接收从服务端发来的信息，并进行显示
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	     System.out.println("这里是客户端的channelRead");
	     //还要做相关的msg内容的转换
	     try {
			String toDisplay = EncodeAndDecode.decode(msg);
			//用于显示服务器发送的数据
			CommandAction.backDisplay(toDisplay);
		    //上面需要修改
		    //CommandAction ac = ClientContext.getMap().get(ctx.channel());
		    //ac.backDisplay(toDisplay);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	     
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
