package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.logic.config.MonstConfig;
import com.hc.logic.creature.Player;

@Component
public class AwardService {

	/**
	 * 击杀怪物/boss获得奖励
	 * 包括经验，金币，武器，材料等等
	 * @param player
	 * @param monstConfig
	 */
	public void obtainAward(Player player, MonstConfig monstConfig) {
		//增加经验
		player.addExp(monstConfig.getExp());
		//TODO 获得金币，武器等等（待续）
		
		
	}
}
