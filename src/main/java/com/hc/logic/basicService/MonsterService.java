package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.creature.Monster;

@Component
public class MonsterService {
	
	/**
	 * 当前怪物的详细信息
	 */
	public void mDescribe(Session session, int mId) {
		//MonstConfig mc = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		Monster mc = session.getPlayer().getScene().getMonsteById(mId);
		if(mc == null) {
			session.sendMessage("该怪物不在当前场景");
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(mc.getDescription() + "\n");
		sb.append("血量：");
		sb.append(mc.getHp() + "\n");
		sb.append("攻击力：");
		sb.append(mc.getAttack() + "\n");
		sb.append("是否活着：");
		sb.append(mc.isAlive());
		if(mc.getGold() != 0) {
			//表明是boss
			sb.append("\n" + "击杀获得金币：" + mc.getGold());
			sb.append("\n" + "所有技能" + mc.bossSkillList());
		}
		
		session.sendMessage(sb.toString());
	}

}
