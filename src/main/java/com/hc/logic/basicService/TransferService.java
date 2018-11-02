package com.hc.logic.basicService;
import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.base.Teleport;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.domain.CopyEntity;

@Component
public class TransferService implements Teleport{
	
	
	/**
	 * 手动进行传送。要么是普通场景直接的传送，要么是强制退出副本(副本到普通场景)
	 * @param targetId 普通目标场景
	 * @param sourceId 普通原场景，可能为0
	 * @param session
	 */
	public void allTransfer(int targetId, int sourceId, Session session){
		if(sourceId !=0) {
			if(session.getPlayer().isInPK()) Context.getPkService().giveUp(session);
			//对于在普通场景中传送
			if(!Context.getWorld().getSceneById(targetId).hasTelepId(sourceId)) {
				session.sendMessage("没有这个传送阵，不能传送");
				return;
			}
			transfer(session.getPlayer(), sourceId, targetId);
			return;
		}
		//从副本中强制退出
		int copyId = session.getPlayer().getCopEntity().getCopyId();
		if(targetId != Context.getCopysParse().getCopysConfById(copyId).getPlace()) {
			session.sendMessage("没有这个传送阵，不能传送");
			return;
		}
		transferCopy( session.getPlayer(), copyId);
		Player player = session.getPlayer();
		//主动传送出来时，需要删除副本数据，包括副本数据库
		player.getPlayerEntity().setNeedDel(true);
		Context.getTaskProducer().addTask(new CopyPersist(player.getPlayerEntity()));

	}

	/**
	 * 用于场景和场景之间的传送
	 */
	@Override
	public void transfer(Player player, int sId, int tId) {

		Scene target = Context.getWorld().getSceneById(tId);
		Scene source =  Context.getWorld().getSceneById(sId);
		//在目标场景中加入玩家
		target.addPlayer(player);
		//也要在原场景中删除玩家
		source.deletePlayer(player);
		
		//不再受原场景中的怪物攻击, 要在改变sceneId前
		player.getScene().deleteAttackPlayer(player);

		
		//重设玩家的sceneid字段。
		player.setSceneId(tId);
		
		//到新场景时，会受到新场景的主动攻击怪物的攻击(待续)
		
		player.getSession().sendMessage("欢迎来到" + Context.getWorld().getSceneById(tId).getName());
		
	}
	
	/**
	 * 普通场景到副本的传送
	 * @param player
	 * @param sId  普通场景id
	 * @param tId  副本id
	 */
	public void copyTransfer(Player player, int tId) {
		if(player.getSceneId() == 0) return; //表示已经在副本中，是断线重连
		int sId = Context.getCopysParse().getCopysConfById(tId).getPlace();
		//普通场景
		Scene source =  Context.getWorld().getSceneById(sId);
		
		source.deleteAttackPlayer(player);
		source.deletePlayer(player);
		
		player.setSceneId(0);   //改变玩家sceneid
		CopyEntity copyEntity = new CopyEntity(tId, System.currentTimeMillis(), player.getPlayerEntity(), 0);
		//player.setCopEntity(copyEntity);  
		player.getPlayerEntity().addCopyEntity(copyEntity); //改变copyid的值
		
		player.getSession().sendMessage("欢迎来到副本：" + Context.getCopysParse().getCopysConfById(tId).getName());
		
	}
	
	/**
	 * 从副本中传送出来，进入原场景
	 * 并且要销毁副本
	 * @param player
	 * @param sId  副本id
	 */
	public void transferCopy(Player player, int sId) {
		//获得进入副本前的场景id
		int tId = Context.getCopysParse().getCopysConfById(sId).getPlace();
		Scene target = Context.getWorld().getSceneById(tId);
		//在目标场景中加入玩家
		target.addPlayer(player);
		
		player.setSceneId(tId);
		Context.getWorld().delCopys(player.getCopEntity().getCopyId(), player);//传送出来了，就销毁副本
		Context.getWorld().delCopyThread(player);   //停止副本线程

		System.out.println("已经消除副本，且副本线程已停止 ");
		//player.setCopEntity(null); 
		
		player.getSession().sendMessage("欢迎回到：" + Context.getWorld().getSceneById(tId).getName());
	}
	
	@Override
	public String getDescribe() {
		return "";
	}

	
	public String toString(int id1, int id2) {
		StringBuilder sb = new StringBuilder();
		String start = Context.getWorld().getSceneById(id1).getName();
		String end = Context.getWorld().getSceneById(id2).getName();
		sb.append(start);
		sb.append(" -> ");
		sb.append(end);
		return sb.toString();
	}

	
}
