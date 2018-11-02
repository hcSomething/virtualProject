package com.hc.logic.pk;

import org.springframework.stereotype.Component;

import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;

@Component
public class TwoPlayerPk {

	/**
	 * pk结束，胜利的一个获得奖励，输的一方将获得惩罚
	 * @param p1  胜利方
	 * @param p2 失败方
	 */
	public void geReward(Player p1, Player p2) {
		int gold = goldReword(p1, p2);
		p1.addGold(gold);
		int gId = goodReword(p1, p2);
		swapGood(p1, p2, gId);	
	}
	
	
	private void swapGood(Player p1, Player p2, int gId) {
		p1.addGoods(gId, 1);
		p2.delGoods(gId, 1);
	}
	
	/**
	 * 需要交换的物品
	 * @param p1 胜
	 * @param p2 败
	 * @return 物品id
	 */
	private int goodReword(Player p1, Player p2) {
		int size = p2.getPlayerEntity().getGoods().size();
		int index = (int)(1 + Math.random() * size);
		int i = 1;
		for(GoodsEntity ge : p2.getPlayerEntity().getGoods()) {
			if(i == index) {
				return ge.geteId();
			}
			i++;
		}
		return 1;
	}
	
	/**
	 * 金币奖励
	 * @param p1 获胜方
	 * @param p2 失败方
	 * @return
	 */
	private int goldReword(Player p1, Player p2) {
		int levelGold = p1.getLevel() * 50;   //获胜得到的金币和等级有关
		int difGold = (p1.getLevel() - p2.getLevel()) * p1.getLevel() * 10;  //获胜得到的金币和双方等级差有关
		int gold = levelGold - difGold;
		return gold;
	}
	
	
	
	
}
