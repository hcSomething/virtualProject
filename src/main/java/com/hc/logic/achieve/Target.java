package com.hc.logic.achieve;

import java.util.Map;

import com.hc.logic.config.TaskConfig;

public interface Target {
	
	/**
	 * 验证是否完成任务
	 */
	boolean checkTaskComplete(TaskConfig taskConfig);
	/**
	 * 任务进度描述
	 * @param taskConfig: 任务配置
	 */
	String taskProgessDesc(TaskConfig taskConfig);
	/**
	 * 添加任务进度，也就是完成的项
	 * @param id.相应的id。如怪物id，物品id，副本id
	 */
	void addComplete(int id);
	
	Map<Integer, Integer> getTaskComplete();
	void setTaskComplete(Map<Integer, Integer> taskComplete);
}
