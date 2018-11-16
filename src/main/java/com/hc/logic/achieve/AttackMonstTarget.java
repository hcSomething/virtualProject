package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.TaskConfig;

/**
 * 攻击怪物目标
 * @author hc
 *
 */
public class AttackMonstTarget implements Target{

	/**
	 * 此任务完成项。对应于相应任务配置中的need项。
	 * 对于type=1的击杀任务：key：怪物id，value：数量
	 */
	private Map<Integer, Integer> taskComplete = new HashMap<>();

	
	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			int gid = ent.getKey();
			int amount = ent.getValue();
			if(taskComplete.get(new Integer(gid)) < amount) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append("任务[" + taskConfig.getName() +"]的进度如下: \n");
		for(Map.Entry<Integer, Integer> ent :taskConfig.getNeeded().entrySet()) {
			int mid = ent.getKey();
			MonstConfig mConfig = Context.getSceneParse().getMonsters().getMonstConfgById(mid);
			int amount = ent.getValue();
			taskComplete.get(new Integer(mid));
			int nam = ((taskComplete.get(new Integer(mid)) == null) ? 0 : taskComplete.get(new Integer(mid))) ;
			sb.append("击杀["+mConfig.getName()+"]: "+ nam +"/" + amount +"\n");
		}

		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		taskComplete.put(id, taskComplete.getOrDefault(id, 0) + 1);
	}
	
	public Map<Integer, Integer> getTaskComplete(){
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		this.taskComplete = taskComplete;
	}
	
}
