package com.hc.logic.achieve;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.AchieveConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.AchieveEntity;

@Component
public class AchieveService {

	public void desOrder(Session session, String[] args) {
		finishAchi(session);
	}
	
	/**
	 * 查看所有达成的成就
	 * @param session
	 */
	public void finishAchi(Session session) {
		Player player = session.getPlayer();
		List<Integer> completedAchieve = player.getPlayerAchieves().getAchieveCompletes();
		StringBuilder sb = new StringBuilder();
		sb.append("已经达成的成就如下：\n");
		for(int i : completedAchieve) {
			AchieveConfig achieveConfig = Context.getAchieveParse().getAchieveConfigByid(i);
			sb.append(achieveConfig.getName() + " " + achieveConfig.getDesc() + "\n");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.deleteCharAt(sb.length()-1);
	    session.sendMessage(sb.toString());
	}
}
