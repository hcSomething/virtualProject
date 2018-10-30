package com.hc.logic.base;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
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
		
		//重新登陆，要把玩家加入原来的场景/副本
		enterScene(player);
		
		session.setPlayer(player);
		session.sendMessage("登陆成功");
		//将登陆的玩家的session的channel更新
		Session pSession = player.getSession();
		pSession.setChannel(session.getChannel());
		//pSession.sendMessage("登陆成功");
	    
	}
	
	/**
	 * 登陆时，需要判断玩家上次退出时是在哪种场景中。
	 * 若是在普通场景中，则正常登陆就好
	 * 若是在副本中，那么就需要先判断是否超时，若没有超时就要恢复副本
	 * 若超时，就进入原场景
	 */
	public void enterScene(Player player) {
		int sceneId = player.getSceneId();
		if(sceneId != 0) {
			//进入普通场景
			System.out.println("进入普通场景");
			enterNormalScene(player);
			return;
		}
		System.out.println("进入副本");
		int copyId = player.getCopEntity().getCopyId();
		long etC = player.getCopEntity().getFirstEnterTime();
		long cur = System.currentTimeMillis();
		long dual = Context.getCopysParse().getCopysConfById(copyId).getContinueT();
		dual = dual * 60 * 1000; 
		System.out.println("!!!!!!!!进入时间"+etC + " 现在时间 " + cur + " 差 " + (cur-etC) + " 开启时间 " + dual );
		if(dual > (cur - etC)) {
			//创建并进入副本, 设定boss的 index
			int bossIndex = player.getCopEntity().getBossindex();
			Context.getCopyService().enterCopy(copyId, player, player.getSession(), bossIndex);
		}else {
			//超时，进入普通场景
			System.out.println("超时");
			player.setSceneId(Context.getCopysParse().getCopysConfById(copyId).getPlace());
			//删除副本数据库信息
			player.getPlayerEntity().setNeedDel(true);
			Context.getTaskProducer().addTask(new CopyPersist(player.getPlayerEntity()));
			System.out.println("超时副本删除成功");
			enterNormalScene(player);
		}
	}
	
	private void enterNormalScene(Player player) {
		Scene sc = Context.getWorld().getSceneById(player.getSceneId());
		if(sc.getPlayerByName(player.getName()) == null) {
			sc.addPlayer(player);
		}
	}
	
	
}
