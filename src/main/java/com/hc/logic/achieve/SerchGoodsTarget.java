package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.TaskConfig;

public class SerchGoodsTarget implements Target {

	/**
	 * 此任务完成项。对应于相应任务配置中的need项。
	 * 对于type=1的击杀任务：key：需要采集的物品id，value：数量
	 */
	private Map<Integer, Integer> taskComplete = new HashMap<>();

	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			int gid = ent.getKey();
			int amount = ent.getValue();
			if(taskComplete.get(new Integer(gid)) == null) return false;
			if(taskComplete.get(new Integer(gid)) < amount) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		System.out.println("----------taskprogerss---serchgoodstask");
		StringBuilder sb = new StringBuilder();
		sb.append("任务[" + taskConfig.getName() +"]的进度如下: \n");
		for(Map.Entry<Integer, Integer> ent :taskConfig.getNeeded().entrySet()) {
			int gid = ent.getKey();
			//MonstConfig mConfig = Context.getSceneParse().getMonsters().getMonstConfgById(mid);
			GoodsConfig gConfig = Context.getGoodsParse().getGoodsConfigById(gid);
			int amount = ent.getValue();
			taskComplete.get(new Integer(gid));
			int nam = ((taskComplete.get(new Integer(gid)) == null) ? 0 : taskComplete.get(new Integer(gid))) ;
			sb.append("采集[" + gConfig.getName() + "]: " + nam + "/" + amount +"\n");
		}

		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		if(!taskComplete.containsKey(new Integer(id))) return;
		taskComplete.put(id, taskComplete.getOrDefault(id, 0) + 1);
	}
	
	public Map<Integer, Integer> getTaskComplete(){
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			for(int i = 0; i < ent.getValue(); i++) {
				addComplete(ent.getKey());
			}
		}
	}

}
