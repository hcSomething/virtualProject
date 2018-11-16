package com.hc.logic.basicService;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
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
	
	/**
	 * 获得奖励。格式：物品id：数量。
	 *               0：数量。表示金币
	 * @param player
	 * @param award
	 */
	public void obtainAward(Player player, Map<Integer, Integer> award) {
		for(Map.Entry<Integer, Integer> ent : award.entrySet()) {
			if(ent.getKey() == 0) {
				player.addGold(ent.getValue());
				player.getSession().sendMessage("获得金币：" + ent.getValue() + " 个");
			}else {
				player.addGoods(ent.getKey(), ent.getValue());
				String name = Context.getGoodsParse().getGoodsConfigById(ent.getKey()).getName();
				player.getSession().sendMessage("获得" +name+": " + ent.getValue() + " 个");
			}
		}
	}
}
