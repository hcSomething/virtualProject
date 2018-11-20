package com.hc.frame.taskSchedule;

public interface TaskManage extends Runnable{

	public void exe(int interval, String taskId, Runnable runnalbe);
	
	public void run();
}
