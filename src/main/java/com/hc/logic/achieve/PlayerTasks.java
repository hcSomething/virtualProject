package com.hc.logic.achieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hc.frame.Context;
import com.hc.logic.config.TaskConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.TaskEntity;

/**
 * 玩家的所有任务
 * 包括: 所有正在进行的任务
 *     所有已经完成但还没有提交的任务
 * @author hc
 *
 */
public class PlayerTasks {
	
	//击杀的怪物.key:怪物id;value:击杀的数量
	private Map<Integer, Integer> mid2amount = new HashMap<>();
	//完成的副本。key：副本id；value:完成的次数
	private Map<Integer, Integer> cid2amount = new HashMap<>();


	//正在进行的任务
	private List<Task> progressTask = new ArrayList<>();
	//已经完成但没有提交的任务.放的是任务id
	private List<Integer> completeTask = new ArrayList<>();
	//已领取奖励的任务.放的是任务id
	private List<Integer> awardedTask = new ArrayList<>();
	
	public PlayerTasks(Player player) {
		//从数据库中恢复任务
		System.out.println("------从数据库中恢复任务------");
		TaskEntity taskEntity = player.getTaskEntity();		
		if(taskEntity.getProgressTask() != null && !taskEntity.getProgressTask().equals("")) {
			String[] pgs = taskEntity.getProgressTask().split(";");
			Map<Integer, Map<Integer, Integer>> mpgs = new HashMap<>();
			for(int i = 0; i < pgs.length; i++) {
				String[] stask = pgs[i].split(",");
				int tid = Integer.parseInt(stask[0]);
				int id = Integer.parseInt(stask[1]);
				int num = Integer.parseInt(stask[2]);
				if(mpgs.get(new Integer(tid)) == null) {
					Map<Integer, Integer> tem = new HashMap<>();
					tem.put(id, num);
					mpgs.put(tid, tem);
				}else {
					mpgs.get(new Integer(tid)).put(id, num);
				}
			}
			System.out.println("数据库中，未完成的任务：" + mpgs.toString());
			for(Entry<Integer, Map<Integer, Integer>> ent : mpgs.entrySet()) {
				progressTask.add(new Task(ent.getKey(), ent.getValue()));
			}
		}
		
		if(taskEntity.getNotAward() != null && !taskEntity.getNotAward().equals("")) {
			String[] ste = taskEntity.getNotAward().split(",");
			for(int i = 0; i < ste.length; i++) {
				int te = Integer.parseInt(ste[i]);
				completeTask.add(te);
			}
		}
		if(taskEntity.getAwarded() != null && !taskEntity.getAwarded().equals("")) {
			String[] saw = taskEntity.getAwarded().split(",");
			for(int i = 0; i < saw.length; i++) {
				int aw = Integer.parseInt(saw[i]);
				awardedTask.add(aw);
			}	
		}
	}
	public PlayerTasks() {
		
	}
	
	/**
	 * 添加任务
	 * @param tid：任务id
	 */
	public boolean addTask(int tid) {
		if(hadDoTask(tid)) return false;
		Task task = new Task(tid);
		progressTask.add(task);
		System.out.println("---------添加任务------" + progressTask.size() + ", "
				 + completeTask.size());
		return true;
	}
	
	private boolean hadDoTask(int tid) {  //验证这个任务以前是否做过
		if(completeTask.contains(new Integer(tid)) ||
				awardedTask.contains(new Integer(tid))) {
			return true;
		}
		for(Task task : progressTask) {
			if(task.getTid() == tid) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 验证任务是否完成
	 * @param tid
	 */
	public void isTaskComplete(Task task) {
		if(task.checkTaskComplete()) {
			completeTask.add(task.getTid());
			progressTask.remove(task);
		}	
	}
	private int isTaskComplete(int tid) {
		for(int id : completeTask) {
			if(id == tid) {
				return id;
			}
		}
		return -1;
	}
	/**
	 * 验证任务是否完成，完成的需要从进行任务中删除
	 * 加入已完成任务中
	 * @param tid
	 * @return
	 */
	private void isTaskComplete(List<Task> tasks) {
		List<Task> completed = new ArrayList<>();
		for(Task task : tasks) {
			if(task.checkTaskComplete()) {
				completed.add(task);
			}
		}
		taskComplete(completed);
	}
	private void taskComplete(List<Task> tasks) {
		for(Task task : tasks) {
			completeTask.add(task.getTid());
			progressTask.remove(task);
		}
	}
	
	
	/**
	 * 记录玩家击杀怪物的数量
	 * @param mid
	 */
	public void monstRecord(int mid) {
		mid2amount.put(mid, mid2amount.getOrDefault(new Integer(mid), 0) + 1);		
		updateTask(1, mid);  
	}
	/**
	 * 记录玩家副本完成数量
	 * @param cid 副本id
	 */
	public void copyRecord(int cid) {
		System.out.println("当前玩家副本的记录：" + cid2amount.toString());
		cid2amount.put(cid, cid2amount.getOrDefault(new Integer(cid), 0) + 1);
		updateTask(3, cid);
	}
	
	/**
	 * 更新任务进度
	 * @param taskType：任务的类型。1：击杀怪物。2：采集物品。3：完成副本
	 * @param id
	 */
	private void updateTask(int taskType, int id) {
		//System.out.println("更新任务进度，任务类型：" + taskType + ", " + id);
		List<Task> updatedtasks = new ArrayList<>();
		System.out.println("进行任务更新： " + progressTask.toString());
		for(Task task : progressTask) {
			if(task.isSameTaskType(taskType)) {
				task.addComplete(id);
				updatedtasks.add(task);
			}
		}
		isTaskComplete(updatedtasks);  //验证更新了进度的任务是否完成		
	}
	
	/**
	 * npc接收任务，从而获得奖励
	 * @param tid: 任务id
	 */
	public boolean getTaskAward(Player player, int tid) {
		int isCom = isTaskComplete(tid);
		if(isCom == -1) return false;
		awardedTask.add(tid);
		completeTask.remove(new Integer(tid));
		getAward(player, tid);  //发奖励
		delSerchGoods(player, tid);  //删除需要提交任务的采集物品
		return true;
	}
	private void getAward(Player player, int tid) {
		Map<Integer, Integer> award = Context.getTaskParse().getTaskConfigByid(tid).getAwardit();
		Context.getAwardService().obtainAward(player, award);
	}
	//tid: 任务id
	private void delSerchGoods(Player player, int tid) {  //提交采集任务时，需要删除采集到的物品
		TaskConfig taskConfig = Context.getTaskParse().getTaskConfigByid(tid);
		if(taskConfig.getType() != TargetType.getTargetTypeById(taskConfig.getType())) { //只有采集型任务菜肴删除搜寻的物品
			return;
		}
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			player.delGoods(ent.getKey(), ent.getValue());
		}
		
	}
	
	

	public List<Task> getProgressTask() {
		//System.out.println("----------任务进程----" + progressTask.size());
		return progressTask;
	}

	
	public List<Integer> getCompleteTask() {
		return completeTask;
	}
	public List<Integer> getAwardedTask() {
		return awardedTask;
	}
	/**
	 * 验证是否完成，并且提交了这个任务
	 * @param tid 任务id
	 * @return
	 */
	public boolean taskAwarded(int tid) {
		for(int i : awardedTask) {
			if(i == tid)
				return true;
		}
		return false;
	}

	
	
}
