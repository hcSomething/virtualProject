package com.hc.logic.skill;

import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

public interface SkillAttack {

	/**
	 * 技能持续效果进行攻击
	 * @param player 攻击者
	 */
	void doContiAttack(Player player);   
	/**
	 * 添加技能持续效果
	 * @param monster
	 * @param skillId
	 */
	void addContiAttack(Monster monster, int skillId);
	/**
	 * 添加技能持续效果
	 * @param player  被攻击的玩家
	 * @param skillId
	 */
	void addContiAttack(Player player, int skillId);
	
	/**
	 * 清空技能的持续效果
	 */
	void cleanup();
}
