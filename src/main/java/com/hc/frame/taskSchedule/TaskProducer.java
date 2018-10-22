package com.hc.frame.taskSchedule;

import java.util.concurrent.*;

/**
 * 添加任务
 * @author hc
 *
 */
public class TaskProducer extends TaskConsume{

	//一个线程池
	ExecutorService exec = Executors.newFixedThreadPool(10);
	//一个阻塞队列，将任务放入这个，就会被执行。
	BlockingQueue<Runnable> task = new LinkedBlockingQueue<>();
	
	public TaskProducer() {
		exe(5);//启动周期性调度器。5秒周期
	}
	

	
	
	
	/**
	 * 
	 * 任务的提交应该本身是一个周期性调度的，即周期性的提交需要处理的任务
	 * @param aTask
	 */
	@Override
	public void execute() {
		
		if(task.peek() != null) {
			exec.submit(task.poll());
		}
		
	}
	
	/**
	 * 添加任务
	 */
	public void addTask(Runnable ru) {
		System.out.println("添加了一个任务");
		try {
			task.put(ru);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

}
