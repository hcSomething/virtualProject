package com.hc.logic.base;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
/**
 * 注册
 * 
 * 这里传入的密码还没有用处，以后添加用文本记录玩家的密码
 * @author hc
 *
 */
@Component
public class Register {
	
	public void register(Session session, String name, String password) {
		//防止重复注册
		if((Context.getWorld().getPlayerByName(name) != null)  || (Context.getWorld().getPlayerEntityByName(name) != null)) {
			session.sendMessage("当前用户已存在，请重新输入");
			return;
		}
		int id = Context.getpID();
		int hp = 0;
		int mp = 0;
		//血量和法力在选择职业后设置
		int[] skills = {0}; //默认没有技能
		Player player = new Player(id, 1, name, password, 1, hp, mp, 0, skills, session, true, new ArrayList<>());
		
		//Context.getWorld().getSceneById(1).addPlayer(player); //在场景1中注册了玩家，
		
		Context.getWorld().addAllRegisteredPlayer(player);  //充当数据库，解决客户端重连问题
		session.setPlayer(player);  
		
		//System.out.println("--register--: " + session + " channel: " + session.getChannel());
		session.sendMessage("注册成功");
		choiceProf(session);
	}
	
	/**
	 * 注册后，提示选择职业
	 * @param session
	 */
	private void choiceProf(Session session) {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for(Profession pf : Profession.values()) {
			sb.append( i + ", "+ pf.getTitle() + ": ");
			sb.append(pf.getDescription() + "\n");
			i++;
		}
		if(sb.length() > 1) sb.deleteCharAt(sb.length()-1);
		session.sendMessage("请选择一种职业(设置后不能修改)： \n" + sb.toString());
	}
	
	/**
	 * 正在选择职业
	 * @param session
	 * @param index
	 */
	public void inChoiceProf(Session session, int index) {
		if(session.getPlayer().haveProf()) {
			session.sendMessage("不能重复设置");
			return;
		}
		session.getPlayer().setProf(index);
		session.sendMessage("职业设置成功，您的职业是：" + Profession.getProfByIndex(index-1).getTitle());
		session.getPlayer().setHp(Context.getLevelParse().getLevelConfigById(1).getHpByProf(index-1));
		session.getPlayer().setMp(Context.getLevelParse().getLevelConfigById(1).getMpByProf(index-1));
	}



	
}
