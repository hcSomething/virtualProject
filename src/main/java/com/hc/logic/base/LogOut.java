package com.hc.logic.base;

import java.util.List;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * 玩家登出
 * 不应该在主线程中做
 * 这个类本身是一个runnable的类，会被放在TaskProduce中被当作一个任务执行。
 * @author hc
 *
 */
public class LogOut implements Runnable{
	private Player p;

	public LogOut(Player player) {
		//玩家登出时，也要从在线列表中删除
		this.p = player;
		Context.getOnlinPlayer().deletePlayer(player);
	}
	
	/**
	 * 刷新数据库，
	 */
	public void updateDB(Player player) {
		System.out.println("logout --------------------");
		if(player == null)
			return;
		//缓存中是否有这个玩家（因为在服务器启动时，就会将数据库中的信息导出来）
		//缓存是World中allplayerEntiy字段
		String name = player.getName();
		
		if(name.equals(null)) 
			return;
		
		//由玩家创建一个玩家实体,用来更新数据库
		PlayerEntity cPE = player.getPlayerEntity();  
		PlayerEntity pe = Context.getWorld().getPlayerEntityByName(name);
		
		//玩家物品实体
		//GoodsEntity cGE = player.getGoodsEntity();
		
		//若在缓存中没有，也就是数据库中没有，则插入一条数据到数据库，同时也在缓存中缓存一条数据
		if(pe == null) {
			System.out.println("logout，进行数据库插入");			
			//playerEntSave(cPE);
			new PlayerDaoImpl().insert(cPE);
			//new PlayerDaoImpl().insert(cGE);
			Context.getWorld().addPlayerEntity(cPE);  //加入缓存
			//Context.getWorld().addGoodsEntity(cGE);
		}else {
			//若存在于缓存中，则只要更新数据库就行
			System.out.println("logout，进行数据库更新");
			//System.out.println(cPE.toString());
			//updatePE(cPE);
			new PlayerDaoImpl().update(cPE);  //用pe反倒不会更新
			//new PlayerDaoImpl().update(cGE);
			//Context.getWorld().updCache(cPE);  //不用更新缓存，因为传到player类的playerEntity是引用
		}
		
	}

	public void updatePE(PlayerEntity pp) {
		//Context.getWorld().updatePE(pp);
		new PlayerDaoImpl().update(pp);
	}
	
	
	/**
	 * 这个方法会在TaskProducer中执行.
	 */
	@Override
	public void run() {
		updateDB(p);
	}
}
