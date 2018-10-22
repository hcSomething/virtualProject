package com.hc.logic.basicService;

import java.util.List;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.NpcConfig;

public class NpcService {
	
	/**
	 * npc和player是否在同一场景
	 * 参数 id：npc的id
	 */
	public boolean isOneScene(Session session, int id) {
		List<Integer> nSId = Context.getSceneParse().getSceneById(session.getPlayer().getSceneId()).getNpcs();
		for(int ii : nSId) {
			if(ii == id)
				return true;
		}
		return false;
	}

	/**
	 * 完整的介绍当前npc
	 * @param id npc的id
	 */
	public void introduce(Session session, int id) {
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(id);
		session.sendMessage(npcC.getDescription());
	}
	
	/**
	 * 当前npc的任务
	 * @param session
	 * @param id npc的id
	 */
	public void task(Session session, int id) {
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(id);
		session.sendMessage(npcC.getTask());
	}
}
