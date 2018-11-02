package com.hc.logic.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

@Component
public class ChatService {
	
	/**
	 * 私聊
	 * @param session
	 * @param tagPlaId  目标玩家id
	 * @param content
	 */
	public void privateChat(Session session, int tagPlaId, String[] args) {
		if(Context.getOnlinPlayer().getPlayerById(tagPlaId) == null) {
			session.sendMessage("对方不在线，不能发送信息！");
			return;
		}
		String content = chatFormat(session.getPlayer(), bindMessage(delPart(args)));
		Player tagPla = Context.getOnlinPlayer().getPlayerById(tagPlaId);
		System.out.println("--------privateChat----" + content + ", args= " + args.toString());
		tagPla.getSession().sendMessage(content);
		session.sendMessage("发送成功！");
	}

	/**
	 * 解析命令, 全服聊天
	 * @param args
	 */
	public void decOrder(Session session, String[] args) {
		Player player = session.getPlayer();
		if(args.length == 1) {
			enterWorldChat(player); //首次进入聊天频道
		}else {
			//验证是否已经进入了世界聊天频道
			if(!Context.getWorldChatObservable().containPlayer(player)) {
				session.sendMessage("请先进入世界聊天频道");
				return;
			}		
			sendChat(player, bindMessage(args)); //发送消息到世界频道
		}
	}
	
	/**
	 * 发送世界频道聊天
	 * @param player
	 * @param msg
	 */
	public void sendChat(Player player, String msg) {
		String mesg = chatFormat(player, msg);
		Context.getWorldChat().addRecord(mesg);
	}
	
	/**
	 * 进入世界聊天频道
	 * @param player
	 */
	public void enterWorldChat(Player player) {
		Vector<String> chatRecord = Context.getWorldChat().getRecords();
		StringBuilder sb = new StringBuilder();
		sb.append("欢迎来到全服聊天频道！\n"  + "以下是历史信息\n");
		for(int i = chatRecord.size()-1; i > 0 ; i--) {
			if(i < 0) break;
			sb.append(chatRecord.get(i) + "\n");
		}
		if(chatRecord.size() > 0) sb.append(chatRecord.get(0));
		player.getSession().sendMessage(sb.toString());
		Context.getWorldChatObservable().registerPlayer(player);
	}
	
	/**
	 * 玩家主动离开世界频道
	 * @param player
	 */
	public void quitWorldChat(Player player) {
		Context.getWorldChatObservable().removePlayer(player);
	}
	
	/**
	 * 调整信息格式
	 * @param player 发送者
	 * @param msg
	 * @return
	 */
	private String chatFormat(Player player, String msg) {
		String name = player.getName();
		StringBuilder sb = new StringBuilder();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
		sb.append("[" + sdf.format(date) + "] ");
		sb.append(name + ": " + msg);
		return sb.toString();
	}
	
	/**
	 * 还原聊天内容
	 * @param args
	 * @return
	 */
	private String bindMessage(String[] args) {
		System.out.println("-----------bindMessage----" +  args.length);
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < args.length; i++) {
			System.out.println("-----------bindMessage----" +  args[i]);
			sb.append(args[i] + " ");
		}
		return sb.toString();
	}
	
	private String[] delPart(String[] args) {
		String[] result = new String[args.length -1];
		result[0] = args[0];
		for(int i = 2; i < args.length; i++) {
			result[i-1] = args[i];
		}
		return result;
	}
	
	
}
