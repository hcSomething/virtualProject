package com.hc.logic.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.logic.achieve.Achievement;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

public class SkillAttackMonst implements SkillAttack{

	//针对单个怪物的有持续效果的技能。key：怪物，value：( key:技能id, 时间终点 )。
	//而对多个怪物怪物有持续性效果的技能，也可以利用这个，只不过要每个怪物都设置一些
	private Map<Monster, Map<Integer, Long>> attMonster = new HashMap<>();

	
	/**
	 * @param player : 攻击者
	 */
	public void doContiAttack(Player player) {
		if(attMonster.size() == 0) return;
		Map<Monster, Integer> monst2skiId = new HashMap<>();  //将被删除的过期持续效果
		List<Monster> monsters = new ArrayList<>();          //将被删除的所有和此怪物相关的持续效果
		int sumAttack = 0;  //这时所有未过期的技能持续效果对某个怪物造成的总伤害。
		//System.out.println("99999999999999999999999999999999");
		for(Entry<Monster, Map<Integer, Long>> enti : attMonster.entrySet()) {
			Monster m = enti.getKey();
			Map<Integer, Long> entiMap = enti.getValue();
			for(Entry<Integer, Long> ent : entiMap.entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				boolean timeout = System.currentTimeMillis() > terminal;
				System.out.println(System.currentTimeMillis() + ", " + terminal + ", " + timeout);
				if(timeout) {
					monst2skiId.put(m, skillId);  //记录已经过期了的技能效果
					continue;
				}
				int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
				sumAttack += attack;
			}	
			//if(sumAttack < 1) return;
			int attackSuccess = m.attack(sumAttack);
			//只要attackSuccess不等于0，就表示怪物已经被击杀了，就要删掉对此怪物的所有攻击
			if(attackSuccess != 0) {
				monsters.add(m);
			}
			if(attackSuccess == 1) {  //表示怪物被自己击杀
				Context.getSkillService().doAttack(player.getSession(), m, 1);
				//杀掉怪物就需要验证是否达成某成就
				Achievement.getService(player, "KILLM", m.getMonstId());
			}
			if(attackSuccess == 0 && sumAttack > 0) {
				String msg = m.getName() + "被玩家[" + player.getName() +"]的持续技能攻击，剩余血量为：" + m.getHp();
				BroadcastService.broadInScene(player.getSession(), msg);
			}
			sumAttack = 0;
		}
		delAMonstAttack(monsters);
		delStaleDated(monst2skiId);
	}
	
	/**
	 * 删除所有和某个怪物相关的攻击。因怪物死亡
	 * @param result
	 * @param tobeDrop
	 */
	private void delAMonstAttack(List<Monster> monsts) {
		System.out.println("怪物死亡" + monsts.toString());
		for(Monster m : monsts) {
			attMonster.remove(m);
		}
	}
	
	/**
	 * 删除已经过期了的技能效果
	 * @param p2s
	 */
	private void delStaleDated(Map<Monster, Integer> m2s) {
		System.out.println("过期的技能" + m2s.toString());
		List<Monster> mostdel = new ArrayList<>();
		for(Entry<Monster, Integer> enti : m2s.entrySet()) {
			attMonster.get(enti.getKey()).remove(new Integer(enti.getValue()));
			if(attMonster.get(enti.getKey()).size() == 0) {
				mostdel.add(enti.getKey());
			}
		}
		System.out.println("过期的技能---后" + m2s.toString());
		clearNull(mostdel);
	}
	private void clearNull(List<Monster> mostdel) {
		System.out.println("**清空" + mostdel.toString());
		for(Monster monst : mostdel) {
			attMonster.remove(monst);
		}
		System.out.println("**清空" + attMonster.toString());
	}

	
	/**
	 * 添加有持续效果的技能
	 * @param player
	 * @param skillId 技能id
	 */
	public void addContiAttack(Monster monster, int skillId) {
		System.out.println("-----添加有持续效果的技能" + skillId + ", " + monster.getName());
		//System.out.println("----------" + attMonster.toString());
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attMonster.containsKey(monster)) attMonster.put(monster, new HashMap<Integer, Long>());
		if(!attMonster.get(monster).containsKey(new Integer(skillId))) {
			attMonster.get(monster).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//上一个技能的持续时间还没有结束，直接延长持续时间
			long term = attMonster.get(monster).get(new Integer(skillId)) + terminate;
			attMonster.get(monster).put(skillId, term);
		}
		System.out.println("-----添加有持续效果的技能--后---" + attMonster.toString());
	}
	
	/**
	 * 清空技能持续效果
	 */
	public void cleanup() {
		attMonster.clear();
	}

	public void addContiAttack(Player player, int skillId) {
		
	}
}
