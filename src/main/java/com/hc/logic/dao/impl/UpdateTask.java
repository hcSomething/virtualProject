package com.hc.logic.dao.impl;

import com.hc.frame.Context;
import com.hc.logic.base.LogOut;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

public class UpdateTask implements Runnable{

	Object pe;
	
	public UpdateTask(Object pp) {
		this.pe = pp;
		Context.getTaskProducer().addTask(this);
	}
	
	@Override
	public void run() {
		updateTask(pe);
	}
	
	public void updateTask(Object pp) {
		new PlayerDaoImpl().update(pp);
	}
}
