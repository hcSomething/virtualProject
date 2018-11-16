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
	private Map<Integer, Integer> taskComplete = new HashMap<>();

	
	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			int amount = ent.getValue();
			if(amount != 1) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append("任务[" + taskConfig.getName() +"]的进度如下: \n");
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			int cid = ent.getKey();
			CopysConfig cConfig = Context.getCopysParse().getCopysConfById(cid);
			int amount = ent.getValue();
			if(amount == 0) {
				sb.append("副本["+cConfig.getName()+"] 完成\n");
			}else{
				sb.append("副本["+cConfig.getName()+"] 未完成\n");
			}
		}
		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		taskComplete.put(id, 1);
	}

	public Map<Integer, Integer> getTaskComplete() {
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		this.taskComplete = taskComplete;
	}
	
	
}
