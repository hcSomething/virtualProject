package com.hc.frame.taskSchedule;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * 任务执行
 */
public abstract class TaskConsume implements TaskManage{
	
	private AtomicBoolean isActive = new AtomicBoolean(false);
	/**
	 * 当前调度器的周期
	 * 默认周期是10秒
	 */
	private AtomicInteger eInterval = new AtomicInteger(10);
	/**
	 * 这里实现一个周期性的调度器。
	 */
	@Override
	public void run() {
		if(isActive.compareAndSet(false, true)) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(this, 5, eInterval.get(), TimeUnit.SECONDS);
		}
		execute();
		
	}
	
	/**
	 * 通过这个方法，启动一个特定间隔的周期性的调度器
	 */
	@Override
	public void exe(int interval) {
		eInterval.set(interval);
		run();
	}
	
	/**
	 * 
	 * 这个方法会周期性的调用。
	 */
	public abstract void execute();
	
	

	
}
