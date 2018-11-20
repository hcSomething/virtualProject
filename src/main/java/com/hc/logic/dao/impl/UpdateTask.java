package com.hc.logic.dao.impl;

import com.hc.frame.taskSchedule.TaskProducer;


public class UpdateTask implements Runnable{

	Object pe;
	
	public UpdateTask(Object pp) {
		this.pe = pp;
		TaskProducer.addTask(this);
	}
	
	@Override
	public void run() {
		updateTask(pe);
	}
	
	public void updateTask(Object pp) {
		new PlayerDaoImpl().update(pp);
	}
}
