package com.hc.logic.base;

import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.achieve.PlayerAchieves;
import com.hc.logic.achieve.PlayerTasks;
import com.hc.logic.achieve.Task;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.AchieveEntity;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.TaskEntity;

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
		System.out.println("logout --------------------" + player==null);
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
		//从副本中删除玩家
		manageCopy(player);
		//更新任务实体
		updateTaskEntity(player);
		updateAchieveEntity(player);
		
		//若在缓存中没有，也就是数据库中没有，则插入一条数据到数据库，同时也在缓存中缓存一条数据
		if(pe == null) {
			System.out.println("logout，进行数据库插入");	
			if(cPE.getProfession() == -1) return;
			new PlayerDaoImpl().insert(cPE);
			Context.getWorld().addPlayerEntity(cPE);  //加入缓存
		}else {
			//若存在于缓存中，则只要更新数据库就行
			System.out.println("logout，进行数据库更新");
			new PlayerDaoImpl().update(cPE);  
			//delCopys(cPE);
		}
		
	}
	
	/**
	 * 更新任务实体，存入数据库
	 * @param player
	 */
	public void updateTaskEntity(Player player) {
		//System.out.println("--------------实体存储------------");
		PlayerTasks pts = player.getPlayerTasks();
		TaskEntity te = player.getTaskEntity();
		StringBuilder sb = new StringBuilder();
		for(Task task : pts.getProgressTask()) {
			int tid = task.getTid();
			for(Map.Entry<Integer, Integer> ent : task.getTaskTarget().getTaskComplete().entrySet()) {
				sb.append(tid +","+ ent.getKey() +","+ ent.getValue() + ";");
			}
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		te.setProgressTask(sb.toString());
		sb = new StringBuilder();
		for(int tid : pts.getCompleteTask()) {
			sb.append(tid + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		te.setNotAward(sb.toString());
		sb = new StringBuilder();
		for(int tid : pts.getAwardedTask()){
			sb.append(tid + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		te.setAwarded(sb.toString());
	}
	/**
	 * 更新成就实体
	 * @param player
	 */
	public void updateAchieveEntity(Player player) {
		PlayerAchieves playerAchieve = player.getPlayerAchieves();
		AchieveEntity achieveEntity = player.getAchieveEntity();
		StringBuilder sb = new StringBuilder();
		for(int id : playerAchieve.getAchieveCompletes()) {
			sb.append(id + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		achieveEntity.setCompleteAchieve(sb.toString());
		
		sb = new StringBuilder();
		for(Map.Entry<Integer, Integer> ent : playerAchieve.getContinueAch().entrySet()) {
			sb.append(ent.getKey() + ":" + ent.getValue() + ";");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		achieveEntity.setProgressAchieve(sb.toString());
	}
	
	/**
	 *断线时，需要从副本中删除玩家。
	 *最后一个玩家断线，则清除副本
	 * @param player
	 */
	private void manageCopy(Player player) {
		System.out.println("------------manageCopy " + player.getCopys());
		//玩家断开连接时，需要清除副本任务调度线程
		Copys copy = player.getCopys();
		if(copy == null) return;
		copy.delPlayer(player.getId());
		//每次有玩家断线都要更新副本实体
		CopyEntity cpe = Context.getWorld().getCopyEntityById(copy.getId());
		cpe.setBossindex(copy.getBossIndex());
		new PlayerDaoImpl().update(cpe);
		if(!copy.haveAvailablePlayer()) {
			System.out.println("------------manageCopy ");
			//玩家断开连接,并且是最后一个离开副本的，需要清除副本任务调度线程
			Context.getWorld().delCopys(copy.getId(), player.getSponserNmae());
			Context.getWorld().delCopyThread(player);
		}
	}
	
	/**
	 * 这个方法会在TaskProducer中执行.
	 */
	@Override
	public void run() {
		updateDB(p);
	}
}
