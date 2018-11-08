package com.hc.logic.dao.impl;

import com.hc.logic.creature.Player;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;

public class CopyPersist implements Runnable{

	Player player;
	public CopyPersist(Player player) {
		this.player = player;
	}
	
	/**
	 * 删除副本表中的数据
	 * @param playerEntity
	 */
	public void delCopys() {
		if(!player.getPlayerEntity().isNeedDel()) return;
		CopyEntity copyEntity = player.getCopEntity();
		player.getPlayerEntity().setCopyEntity(null);
		if(player.getTeammate().size() > 0) {
			//多人组队时，只有发起者需要删除副本数据库
			new PlayerDaoImpl().delete(copyEntity);
		}
		
	}

	/**
	 * 这个方法会在TaskProducer中执行.
	 */
	@Override
	public void run() {
		delCopys();
	}

}
