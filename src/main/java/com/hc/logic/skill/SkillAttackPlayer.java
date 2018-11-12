package com.hc.logic.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

public class SkillAttackPlayer implements SkillAttack{

	//针对单个玩家的有持续效果的技能。key：玩家，value：( key:技能id, 时间终点 )。
	//而对多个玩家有持续性效果的技能，也可以利用这个，只不过要每个玩家都设置一些
	private Map<Player, Map<Integer, Long>> attPlayer = new HashMap<>();


	public void addContiAttack(Player player, int skillId) {
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attPlayer.containsKey(player)) attPlayer.put(player, new HashMap<Integer, Long>());
		if(!attPlayer.get(player).containsKey(new Integer(skillId))) {
			attPlayer.get(player).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//上一个技能的持续时间还没有结束，直接延长持续时间
			long term = attPlayer.get(player).get(new Integer(skillId)) + terminate;
			attPlayer.get(player).put(skillId, term);
		}
	}

	
	/**
	 * 具有持续性效果的技能给玩家带来持续掉血
	 * 并删除已经过期了的技能持续效果
	 */
	@Override
	public void doContiAttack(Player player) {
		if(attPlayer.size() == 0) return;
		Map<Player, Integer> player2skiId = new HashMap<>();
		List<Player> diedPlayer = new ArrayList<>();
		int sumAttack = 0;
		for(Entry<Player, Map<Integer, Long>> enti : attPlayer.entrySet()) {
			Player p = enti.getKey();
			for(Entry<Integer, Long> ent : enti.getValue().entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				if(terminal < System.currentTimeMillis()) {
					player2skiId.put(p, skillId);  //记录已经过期了的技能效果
					continue;
				}
				int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
				sumAttack += attack;
			}	
			//玩家可以减少伤害的buff，比如护盾
			int redu = p.allReduce();
			sumAttack -= redu;
			if(sumAttack < 0) sumAttack = 0;   //防止护盾的保护大于受到的伤害				
			p.addHpMp(-sumAttack, 0); //加个负号，就变成减了
			if(!p.isAlive()) {
				diedPlayer.add(p);
				p.getSession().sendMessage("您已被玩家["+ player.getName()+"]杀死");
				player.getSession().sendMessage("您已将玩家[" +p.getName() +"]杀死");
			}else{
				if(sumAttack > 0) {
					p.getSession().sendMessage("玩家["+player.getName() +
							"]的持续技能对你造成伤害， 减少血量" + sumAttack);
				}
			}

		}
		delStaleDated(player2skiId);
		deldiedPlayer(diedPlayer);
	}
	/**
	 * 删除已经过期了的技能效果
	 * @param p2s
	 */
	private void delStaleDated(Map<Player, Integer> p2s) {
		List<Player> timeoutps = new ArrayList<>();
		System.out.println("删除过期的技能效果" + attPlayer.toString());
		for(Entry<Player, Integer> enti : p2s.entrySet()) {
			attPlayer.get(enti.getKey()).remove(new Integer(enti.getValue()));
			if(attPlayer.get(enti.getKey()).size() == 0) timeoutps.add(enti.getKey());
		}
		removetimeout(timeoutps);
	}
	private void removetimeout(List<Player> players) {
		for(Player p : players) {
			attPlayer.remove(p);
		}
		System.out.println("删除过期的技能效果--后" + attPlayer.toString());
	}
	/**
	 * 删除对已死亡玩家的持续效果
	 * @param players
	 */
	private void deldiedPlayer(List<Player> players) {
		System.out.println("删除对已死亡玩家的技能效果" + attPlayer.toString());
		for(Player p : players) {
			attPlayer.remove(p);
		}
		System.out.println("删除对已死亡玩家的技能效果--后" + attPlayer.toString());
	}
	
	@Override
	public void cleanup() {
		attPlayer.clear();
	}
	
	@Override
	public void addContiAttack(Monster monster, int skillId) {
		
	}

}
