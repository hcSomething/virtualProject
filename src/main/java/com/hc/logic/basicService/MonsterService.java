package com.hc.logic.basicService;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;

public class MonsterService {
	
	/**
	 * 当前怪物的详细信息
	 */
	public void mDescribe(Session session, int mId) {
		MonstConfig mc = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		StringBuilder sb = new StringBuilder();
		sb.append(mc.getDescription() + "\n");
		sb.append("血量：");
		sb.append(mc.getHp() + "\n");
		sb.append("攻击力：");
		sb.append(mc.getAttack() + "\n");
		sb.append("是否活着：");
		sb.append(mc.isAlive());
		session.sendMessage(sb.toString());
	}

}
