package com.hc.logic.base;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
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
			enterNormalScene(player, sceneId);
			return;
		}
		System.out.println("进入副本");
		CopyEntity copyEntity = player.getCopEntity();		
		int copyId = copyEntity.getCopyId();
		sceneId = Context.getCopysParse().getCopysConfById(copyId).getPlace();
		
		long etC = player.getCopEntity().getFirstEnterTime();
		long cur = System.currentTimeMillis();
		long dual = Context.getCopysParse().getCopysConfById(copyId).getContinueT();
		dual = dual * 60 * 1000; 
		System.out.println("!!!!!!!!进入时间"+etC + " 现在时间 " + cur + " 差 " + (cur-etC) + " 开启时间 " + dual );
		if(dual > (cur - etC)) {
			//创建并进入副本, 设定boss的 index
			if(Context.getWorld().getCopyEntityById(copyId) != null) {
				//副本中还有队友，直接进入副本
				System.out.println("副本中还有队友，直接进入副本" + (player.getCopEntity()==null));
				int spsId = Context.getWorld().getPlayerEntityByName(player.getCopEntity().getSponsor()).getId();				
				Context.getWorld().getCopysByAPlayer(copyId, spsId).playerComeback(player);				
				return;
			}
			//副本已被清除，需要再次创建
			int bossIndex = player.getCopEntity().getBossindex();
			//创建副本时，要注意副本的发起者
			List<Player> players = new ArrayList<>();
			players.add(player);
			int sponsId = Context.getWorld().getPlayerEntityByName(player.getCopEntity().getSponsor()).getId();
			Context.getWorld().creatCopy(copyId, players, bossIndex, sponsId);	
			Context.getWorld().addCopyEntity(player.getCopEntity());  //第一个重连的玩家需要缓存copyenityt
			//Context.getCopyService().enterCopy(copyId, player, player.getSession(), bossIndex);
		}else {
			//超时，进入普通场景
			System.out.println("超时");
			//删除副本数据库信息
			player.getPlayerEntity().setNeedDel(true);

			enterNormalScene(player, sceneId);
			
			timeout(player);
			
			System.out.println("超时副本删除成功");
		}
	}
	
	
	/**
	 * 当重连时，副本已超时，需要清除副本数据库
	 * @param player
	 */
	public void timeout(Player player) {
		CopyEntity ce = player.getPlayerEntity().getCopyEntity();
		System.out.println("----------timeout-------------" + ce.toString());
		String hql = "select ce.players from CopyEntity ce where sponsor "
				+ "like : name";
		List<PlayerEntity> list = new PlayerDaoImpl().find(hql, player.getSponserNmae());
		System.out.println("------------" + (list==null) + ", " + list.toString());
		for(PlayerEntity pe : list) {
			pe.setCopyEntity(null);
			pe.setSceneId(player.getSceneId());
			new PlayerDaoImpl().update(pe);	
			Context.getWorld().updatePlayerEntity(pe);
		}
		player.setSponserNmae(null);
		player.clearTeammate();
		new PlayerDaoImpl().delete(ce);
	}
	
	private void enterNormalScene(Player player, int sceneId) {
		System.out.println("---------------今入normalscene" + player  + ", sceneId="+sceneId);
		player.setSceneId(sceneId);
		Scene sc = Context.getWorld().getSceneById(player.getSceneId());
		System.out.println("---------------今入normalscene后-" + player.getSceneId());
		if(sc.getPlayerByName(player.getName()) == null) {
			sc.addPlayer(player);
		}
	}
	
	
}
