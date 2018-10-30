package com.hc.logic.base;

import com.hc.logic.creature.Player;

public interface Teleport {

	/**
	 * 进行传送
	 * @param player
	 * @param sSceneId  源场景id
	 * @param tSceneId  目标场景id
	 */
	public void transfer(Player player, int sSceneId, int tSceneId);
	

	
	/**
	 * 这个传送阵的介绍
	 * @return
	 */
	public String getDescribe();
	
	public String toString();
}
