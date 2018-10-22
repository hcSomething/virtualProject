package com.hc.frame;
import java.util.*;
import com.hc.logic.creature.*;
/**
 * 所有场景中的在线玩家
 * @author hc
 *
 */
public class OnlinePlayer {

	//所有在线玩家
	private List<Player> onlinePlayers = new ArrayList<>();

	
	
	
	
	
	public List<Player> getOnlinePlayers() {
		return onlinePlayers;
	}

	public void addPlayer(Player player) {
		onlinePlayers.add(player);
	}
	
	public void deletePlayer(Player player) {
		onlinePlayers.remove(player);
	}
	
	public Player getPlayerById(int id) {
		for(Player p : onlinePlayers) {
			if(p.getId() == id)
				return p;
		}
		return null;
	}
	
}
