package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.logic.config.MonstConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

@Component
public class AwardService {

	/**
	 * 击杀怪物/boss获得奖励
	 * 包括经验，金币，武器，材料等等
	 * @param player
	 * @param monstConfig
	 */
	public void obtainAward(Player player, Monster monst) {
		//增加经验
		System.out.println("---------击杀怪物获得奖励--"+ player.getExp() + ", 获得" + monst.getExp());
		player.addExp(monst.getExp());
		System.out.println("---------击杀怪物获得奖励----后-"+ player.getExp());
		//TODO 获得金币，武器等等（待续）
		
		
	}
}
