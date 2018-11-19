package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.TaskConfig;

public class Task {

	private int tid;  //配置中的任务id
	private TaskConfig taskConfig;  //任务配置
	private Target taskTarget;    //任务对于的目标
	
	public Task(int tid) {  //任务id
		this.tid = tid;
		this.taskConfig = Context.getTaskParse().getTaskConfigByid(tid);
		this.taskTarget = TargetType.getTargetById(taskConfig.getType());
	}
	//任务恢复
	public Task(int tid, Map<Integer, Integer> maps) {
		this(tid);
		taskTarget.setTaskComplete(maps);
	}
	
	/**
	 * 添加任务进度
	 * @param id
	 */
	public void addComplete(int id) {
		taskTarget.addComplete(id);
	}
	
	/**
	 * 验证是否完成任务
	 * @return
	 */
	public boolean checkTaskComplete() {
		return taskTarget.checkTaskComplete(taskConfig);
	}
	
	/**
	 * 任务进度描述
	 * @return
	 */
	public String taskProgessDesc() {
		return  taskTarget.taskProgessDesc(taskConfig);
	}
	
	/**
	 * 验证是否是同一类型
	 * @param typeId
	 * @return
	 */
	public boolean isSameTaskType(int typeId) {
		return taskConfig.getType() == typeId;
	}
	
	public int getTid() {
		return tid;
	}

	public TaskConfig getTaskConfig() {
		return taskConfig;
	}

	public String getName() {
		return taskConfig.getName();
	}

	public Target getTaskTarget() {
		return taskTarget;
	}

	public void setTaskTarget(Target taskTarget) {
		this.taskTarget = taskTarget;
	}

	
}
