package com.hc.logic.basicService;
import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Teleport;
import com.hc.logic.creature.Player;


public class TransferService implements Teleport{

	@Override
	public void transfer(Player player, int sId, int tId) {

		Scene target = Context.getWorld().getSceneById(tId);
		Scene source =  Context.getWorld().getSceneById(sId);
		//在新手村中加入玩家
		target.addPlayer(player);
		//也要在初始地删除玩家
		source.deletePlayer(player);
		
		//不再受原场景中的怪物攻击, 要在改变sceneId前
		player.getScene().deleteAttackPlayer(player);

		
		//重设玩家的sceneid字段。
		player.setSceneId(tId);
		
		//到新场景时，会受到新场景的主动攻击怪物的攻击(待续)
		
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
