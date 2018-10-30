package com.hc.frame.taskSchedule;

public interface TaskManage extends Runnable{

	public void exe(int interval, String taskId);
	
	public void run();
}
