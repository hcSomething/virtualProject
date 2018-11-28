package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.TaskConfig;

/**
 * 通过副本目标
 * @author hc
 *
 */
public class PassCopyTarget implements Target{

	
	/**
	 * 此任务完成项。对应于相应任务配置中的need项。对于副本任务，value只有0，1两种
	 * 对于type=3的副本通关任务：key：副本id，value：0
	 */
	private Map<Integer, Integer> taskComplete;

	public PassCopyTarget() {
		taskComplete = new HashMap<>();
	}
	
	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			int amount = ent.getValue();
			System.out.println("----副本任务allthing" + taskComplete.toString());
			if(taskComplete.get(ent.getKey()) == null) return false;
			if(taskComplete.get(ent.getKey()) <amount) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append("任务[" + taskConfig.getName() +"]的进度如下: \n");
		System.out.println("-----------副本任务描述---" + taskComplete == null);
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			System.out.println("-----------副本任务描述---" + taskComplete.toString());
			Integer cid = ent.getKey();
			CopysConfig cConfig = Context.getCopysParse().getCopysConfById(cid);
			int num = 0;
			if(taskComplete.get(cid) != null) num = taskComplete.get(cid);
			sb.append("完成副本["+cConfig.getName()+"]：" + num + " /1 \n");
		}
		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		System.out.println("验证是否是这个任务的目标: " + id);
		System.out.println("验证是否是这个任务的目标: " + taskComplete.toString());
		if(!taskComplete.containsKey(new Integer(id))) return;
		taskComplete.put(id, 1);
	}

	public Map<Integer, Integer> getTaskComplete() {
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		//this.taskComplete = taskComplete;
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			for(int i = 0; i < ent.getValue(); i++) {
				addComplete(ent.getKey());
			}
		}
	}
	
	
}
