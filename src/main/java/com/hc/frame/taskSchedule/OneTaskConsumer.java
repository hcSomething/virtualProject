package com.hc.frame.taskSchedule;

import java.util.concurrent.BlockingQueue;

public class OneTaskConsumer implements Runnable{

	private BlockingQueue<Runnable> task;
	
	public OneTaskConsumer(BlockingQueue<Runnable> task) {
		this.task = task;
	}
	
	@Override
	public void run() {
			try {
				while(true) {
					task.take().run();
					Thread.sleep(200);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
