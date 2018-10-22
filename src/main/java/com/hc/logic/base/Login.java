package com.hc.logic.base;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.PlayerEntity;

/**
 * 登陆
 * @author hc
 *
 */
public class Login {
	
	
	private String name;
	private String password;

	public Login(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public void login(Session session) {
		//新注册的玩家，缓存中是没有的。
		Player player = Context.getWorld().getPlayerByName(name);
		//服务器重启，那么就要从数据库的缓存中，寻找是否已经注册过
		PlayerEntity playerEnt = Context.getWorld().getPlayerEntityByName(name);
		
		//只有当数据库缓存和新注册缓存中都没有时，才能确信这个名字没有注册
		if(playerEnt == null && player == null) {
		    session.sendMessage("用户名不存在，请先注册");
		    return;
		}
		if(playerEnt != null) {
			player = playerEnt.createPlayer(session);
		}
		
		
		if(!player.getPassword().equals(password)) {
			session.sendMessage("密码错误");
			return;
		}
		
		//判断这个登陆的账号是否已经登陆了。
		for(Player p : Context.getOnlinPlayer().getOnlinePlayers()) {
			if(p.getName().equals(player.getName())) {
				session.sendMessage("这个账号已经登陆");
				return;
			}
		}
		
		
		//登陆时，要将玩家加入在线玩家列表OnlinePlayer，如果玩家没有登陆，就不会加入在线玩家列表
		Context.getOnlinPlayer().addPlayer(player);
		
		//重新登陆，要把玩家加入原来的场景
		Scene sc = Context.getWorld().getSceneById(player.getSceneId());
		if(sc.getPlayerByName(player.getName()) == null) {
			sc.addPlayer(player);
		}
		
		session.setPlayer(player);
		session.sendMessage("登陆成功");
		//将登陆的玩家的session的channel更新
		Session pSession = player.getSession();
		pSession.setChannel(session.getChannel());
		//pSession.sendMessage("登陆成功");
	    
	}
}
