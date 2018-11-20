package com.hc.frame.taskSchedule;

import java.util.concurrent.*;

import org.springframework.stereotype.Component;


/**
 * 添加任务
 * @author hc
 *
 */
public class TaskProducer{
	//一个阻塞队列，将任务放入这个，就会被执行。
	private static BlockingQueue<Runnable> task;
	
	public TaskProducer(BlockingQueue<Runnable> task) {
		this.task = task;
	}

	/**
	 * 添加任务
	 */
	public static void addTask(Runnable ru) {
		System.out.println("添加了一个任务");
		try {
			task.put(ru);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}

}
