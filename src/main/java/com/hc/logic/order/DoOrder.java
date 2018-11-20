package com.hc.logic.order;

import com.hc.logic.base.Session;

public class DoOrder implements Runnable{

	private Order order;
	private Session session;
	private String[] args;
	
	public DoOrder(Order order, Session session, String[] args) {
		this.order = order;
		this.session = session;
		this.args = args;
	}
	
	@Override
	public void run() {
		order.doService(args, session);
	}
}
