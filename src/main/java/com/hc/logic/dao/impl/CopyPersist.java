package com.hc.logic.dao.impl;

import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;

public class CopyPersist implements Runnable{

	PlayerEntity playerEntity;
	public CopyPersist(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	/**
	 * 删除副本表中的数据
	 * @param playerEntity
	 */
	public void delCopys(PlayerEntity playerEntity) {
		if(!playerEntity.isNeedDel()) return;
		CopyEntity copyEntity = playerEntity.getCopEntity();
		playerEntity.setCopEntity(null);
		new PlayerDaoImpl().delete(copyEntity);
	}

	/**
	 * 这个方法会在TaskProducer中执行.
	 */
	@Override
	public void run() {
		delCopys(playerEntity);
	}

}
