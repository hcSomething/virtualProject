package com.hc.logic.base;

import com.hc.logic.creature.Player;

import io.netty.channel.Channel;

public class Session {
	
	//登陆验证
	private int check;
	
	private Player player;

	private  Channel channel;
	
	public Session() {
		this.check = 1;
	}
	
	
	/**
	 * 发送信息
	 * 所有服务端发送到客户端的信息都从这里出发
	 */
	public void sendMessage(String message) {
		channel.writeAndFlush(message);
	}
	
	
	
	
	
	
	
	
	
	public void addCheck() {
		check++;
	}
	
	public int getCheck() {
		return check;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}
	

	@Override
	public String toString() {
		return "session: " + channel.toString();
		
	}

}
