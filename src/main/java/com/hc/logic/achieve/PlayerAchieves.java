package com.hc.logic.achieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.AchieveEntity;


public class PlayerAchieves {

	/**
	 * 未完成的成就。key：成就id，value：进度
	 */
	private Map<Integer, Integer> continueAch = new HashMap<>();
	/**
	 * 所有完成了的成就。成就id
	 */
	private List<Integer> completeAch = new ArrayList<>();
	
	public PlayerAchieves(Player player) {
		//转换
		AchieveEntity achieveEntity = player.getAchieveEntity();
		if(achieveEntity.getCompleteAchieve() != null && !achieveEntity.getCompleteAchieve().equals("")) {
			String[] sachi = achieveEntity.getCompleteAchieve().split(",");
			for(int i = 0; i < sachi.length; i++) {
				completeAch.add(Integer.parseInt(sachi[i]));
			}
		}
		if(achieveEntity.getProgressAchieve() != null && !achieveEntity.getProgressAchieve().equals("")) {
			String[] spro = achieveEntity.getProgressAchieve().split(";");
			for(int i = 0; i < spro.length; i++) {
				String[] id2num = spro[i].split(":");
				continueAch.put(Integer.parseInt(id2num[0]), Integer.parseInt(id2num[1]));
			}
		}
	}
	public PlayerAchieves() {
		
	}
	
	/**
	 * 成就进度更新
	 * @param id 成就id
	 */
	public int achieveProgress(int id) {
		if(isAchieveComplet(id)) {
			return -1;
		}
		int amount = continueAch.getOrDefault(new Integer(id), 0) + 1;
		continueAch.put(id, amount);
		return amount;
	}
	
	/**
	 * 设置成就完成
	 * @param id
	 */
	public void isComplete(int id) {
		continueAch.remove(new Integer(id));
		completeAch.add(id);
	}
	
	/**
	 * 此成就是否完成
	 * @param tid 成就id
	 * @return
	 */
	public boolean isAchieveComplet(int tid) {
		return completeAch.contains(new Integer(tid));
	}
	
	public List<Integer> getAchieveCompletes(){
		return completeAch;
	}
	public Map<Integer, Integer> getContinueAch(){
		return continueAch;
	}
}
