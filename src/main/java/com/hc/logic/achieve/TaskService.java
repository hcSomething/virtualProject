package com.hc.logic.achieve;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

@Component
public class TaskService {

	/**
	 * 命令解析
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length != 2) {
			session.sendMessage("命令参数错误");
			return;
		}
		if(args[1].equals("p")) {
			inProgressTasks(session);
		}
		if(args[1].equals("n")) {
			allProgressTask(session);
		}
	}
	
	/**
	 * 展示所有未完成任务的状态
	 * @param session, task p
	 */
	public void inProgressTasks(Session session) {
		System.out.println("-------所有任务-------");
		Player player = session.getPlayer();
		List<Task> progressTask = new ArrayList<>(player.getPlayerTasks().getProgressTask());
		StringBuilder sb = new StringBuilder();
		sb.append("所有正在进行的任务： \n");
		for(Task task: progressTask) {
			sb.append(task.taskProgessDesc());
		}
		session.sendMessage(sb.toString());
	}
	
	/**
	 * 获得玩家所有任务的名称
	 * @param session task n
	 */
	public void allProgressTask(Session session) {
		Player player = session.getPlayer();
		List<Task> progressTask = new ArrayList<>(player.getPlayerTasks().getProgressTask());
		List<Integer> completeTask = new ArrayList<>(player.getPlayerTasks().getCompleteTask());
		StringBuilder sb = new StringBuilder();
		sb.append("所有正在进行的任务： \n");
		for(Task task: progressTask) {
			sb.append( " " + task.getName() + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n所有已完成的任务： \n");
		for(int tid : completeTask) {
			String name = Context.getTaskParse().getTaskConfigByid(tid).getName();
			sb.append(" " + name + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		session.sendMessage(sb.toString());;
	}
	
}
