package com.hc.frame.handlers;


import com.hc.frame.Context;
import com.hc.frame.taskSchedule.TaskProducer;
import com.hc.logic.base.LogOut;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.order.Order;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter{
	


    //private Session session;
    //所有客户端的channel和session的对应,
    //private ConcurrentHashMap<Channel, Session> channel2Session = new ConcurrentHashMap<>();
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session session = new Session();
		session.setChannel(ctx.channel());
		//防止了新建的客户端覆盖以前客户端的channel
		Context.addChannel2Session(ctx.channel(), session);
		System.out.println("这是serever handler channelActive方法");		
		Context.channelToString();		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf mesg = (ByteBuf)msg;
		String order = "";
		try {
			order = EncodeAndDecode.decode(mesg);
			System.out.println("服务端接收到的信息为：" + order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//解析命令
		String[] st = order.split(" ");

		Order.getService(st, Context.getSessionByChannel(ctx.channel()));
		

	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		//String name = Context.getSessionByChannel(ctx.channel()).getPlayer().getName();
		Player pl= Context.getSessionByChannel(ctx.channel()).getPlayer();
		String name = "";
		if(pl != null) {
			name = pl.getName();
			//处理玩家断线
			pl.brakLine();
			//处理某一个客户端断开后，从在线列表中的删除
			Context.getOnlinPlayer().deletePlayer(pl);
			//处理某一个客户端断开后，清理场景中的信息
			pl.getScene().deletePlayer(pl);
			//执行登出操作, 在线程池中做
			//new LogOut(pl).updateDB(pl);;
			System.out.println( "------------task前");
			TaskProducer.addTask(new LogOut(pl));
			System.out.println( "------------task后");
		}		
	
		System.out.println( "用户【" + name + "】已关闭连接");
		//客户端断开连接时，就要将这个channel-session从Context中删除，session已经存放在player中
		Context.deleteChannel2Session(ctx.channel());

		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("----客户端已关闭连接");
		super.channelInactive(ctx);
	}




	
	
	
}
