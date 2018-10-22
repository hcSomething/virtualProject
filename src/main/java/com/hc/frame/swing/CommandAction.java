package com.hc.frame.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.hc.frame.handlers.ClientSender;
import com.hc.frame.MyClient;

import io.netty.channel.Channel;

/**
 * 这个类的作用就是获得我们输入的指令，
 * 清空输入栏的指令
 * 将指令放入输出栏
 * 
 * @author hc
 *
 */
public class CommandAction implements ActionListener{
	private static MyJPanel myPanel;
	private String playerOrder;  //用户输入的命令
	private String backOrder; //服务器的输出
	private static Channel channel;
	private String playerName = "*****"; 
	
	private static CommandAction instance;
	
	public CommandAction(MyJPanel mPanel) {
		this.instance = this;
		this.myPanel = mPanel;
	}
	
	//这个方法自动调用
	@Override
	public void actionPerformed(ActionEvent event) {
		//获取输入的指令
		playerOrder = myPanel.getIn().getText();
		//清空输入栏
		myPanel.getIn().setText("");
		//将指令分解一下
		String[] splitOrder = playerOrder.split(" ");
		//用于启动客户端
		/**
		if(splitOrder[0].equals("ip")) {
			MyClient.start(splitOrder[1], Integer.parseInt(splitOrder[2]));
			return;
		}
		*/
		//用于显示器显示用户名
		if(splitOrder[0].equals("login")) {
			playerName = splitOrder[1];
		}
		//只要输入了指令，就要将指令发给服务端
		sendOrderToServer();
		playerOrder = "-----------------" + playerName + "--发出指令: " + playerOrder;
		//将输入栏的内容加到输出栏
		myPanel.getOut().append(playerOrder + "\n"); 
	}
	
	/**
	 * 将用户的指令发送给服务器
	 */
	public void sendOrderToServer() {
		System.out.println("开始发送数据到服务器");
		channel.writeAndFlush(playerOrder);
	}

	/**
	 * 将服务端的数据显示
	 * 要先调用setBackOrder()方法，再调用这个方法。
	 * @return
	 */
	public static void backDisplay(String msg) {
		myPanel.getOut().append(msg + "\n");
	}
	
	
	public String getPlayerOrder() {
		return playerOrder;
	}

	public void setPlayerOrder(String playerOrder) {
		this.playerOrder = playerOrder;
	}

	public String getBackOrder() {
		return backOrder;
	}

	public void setBackOrder(String backOrder) {
		this.backOrder = backOrder;
	}
	
	public static void setChannel(Channel ch) {
		channel = ch;
	}

	public static CommandAction getInstance() {
		return instance;
	}



}
