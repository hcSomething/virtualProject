package com.hc.logic.achieve;

import java.lang.reflect.Field;

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
		AchieveEntity ae = player.getPlayerEntity().getAchieveEntity();
		StringBuilder sb = new StringBuilder();
		sb.append("已经达成的成就如下：\n");
		for(Field field : ae.getClass().getDeclaredFields()) {
	    	field.setAccessible(true);
	    	try {
	    		String charac = field.getName();
	    		if(charac.equals("playerEntity")) continue;
	    		int val = (int)field.get(ae);
	    		if(val != -1) continue;
	    		AchieveConfig acig = Context.getAchieveParse().getAchieveConfigByCharac(charac);
	    		sb.append(acig.getName() + " " + acig.getDesc() + "\n");
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    session.sendMessage(sb.toString());
	}
}
