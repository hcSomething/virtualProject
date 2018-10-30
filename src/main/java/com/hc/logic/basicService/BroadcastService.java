package com.hc.logic.basicService;

import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.OnlinePlayer;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

/**
 * 广播
 * @author hc
 *
 */
public class BroadcastService {

	/**
	 * 广播给当前场景的所有在线玩家
	 * @param session 
	 * @param mesg 需要广播的信息
	 */
	public static void broadInScene(Session session, String mesg) {
		OnlinePlayer oP = Context.getOnlinPlayer();
		int sceneId = session.getPlayer().getSceneId();
		for(Player player : oP.getOnlinePlayers()) {
			if(player.getSceneId() == sceneId) {
				player.getSession().sendMessage(mesg);
			}
		}
	}
	
	/**
	 * 给玩家发送信息
	 * @param player
	 * @param mesg
	 */
	public static void broadToPlayer(List<Player> players, String mesg) {
		for(Player p : players) {
			p.getSession().sendMessage(mesg);
		}
	}
	
	
}
