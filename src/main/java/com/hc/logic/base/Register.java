package com.hc.logic.base;

import java.util.ArrayList;


import com.hc.frame.Context;
import com.hc.logic.creature.Player;
/**
 * 注册
 * 
 * 这里传入的密码还没有用处，以后添加用文本记录玩家的密码
 * @author hc
 *
 */
public class Register {

	private Player player;
	
	private String name;
	private String password;
	
	public Register(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public void register(Session session) {
		//防止重复注册
		if((Context.getWorld().getPlayerByName(name) != null)  || (Context.getWorld().getPlayerEntityByName(name) != null)) {
			session.sendMessage("当前用户已存在，请重新输入");
			return;
		}
		int id = Context.getpID();
		int hp = Context.getLevelParse().getLevelConfigById(1).getHp();
		int mp = Context.getLevelParse().getLevelConfigById(1).getMp();
		int[] skills = {}; //默认没有技能
		player = new Player(id, 1, name, password, 1, hp, mp, 0, skills, session, true, new ArrayList<>());

		//这里先直接增加2技能，以后再添加相应的指令
		//player.addSkill(2);
		//System.out.println("register: " + player.getSkills().toString() + " *** " + player.getPlayerEntity().getSkills());
		

		
		
		//player.setName(name);
		//player.setPassword(password);
		//player.setAlive(true);
		//player.setLevel(1);  //初始等级
		//player.setHp(100);   //初始血量
		//player.setMp(20);    //初始法力
		//player.addSkill(1);  //初始技能
		//player.setExp(0);   //初始经验
	    
		//player.setSceneId(Context.getBornPlace().getId());   //玩家注册场景id
		//Context.getBornPlace().addPlayer(player);            //在场景中注册了玩家，
		//player.setSceneId(1);  //玩家注册，默认进入场景1，即出生地
		Context.getWorld().getSceneById(1).addPlayer(player); //在场景1中注册了玩家，
		
		Context.getWorld().addAllRegisteredPlayer(player);  //充当数据库，解决客户端重连问题
		//Context.getWorld().addPlayerEntity(player.getPlayerEntity());
		//player.setSession(session);  //只有在注册阶段，玩家才会设置session
		//player.setAttack(1);
		//player.setId(Context.getpID());   
		
		
		
		//在session中注册player
		session.setPlayer(player);  
		
		//System.out.println("--register--: " + session + " channel: " + session.getChannel());
		session.sendMessage("注册成功");
	}

	public Player getPlayer() {
		return player;
	}

	
}
