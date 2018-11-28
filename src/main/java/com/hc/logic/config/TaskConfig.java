package com.hc.logic.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务
 * @author hc
 *
 */
public class TaskConfig {

	private int id;
	private int type;             //任务类型id
	private String name;          //任务名字
	private String need;          //字符串形式的任务完成需要的条件
	/**
	 * 格式：
	 * 对于type=1的击杀任务：key：怪物id，value：数量
	 * 对于type=2的采集任务：key：物品id，value：数量
	 * 对于type=3的副本通关任务：key：副本id，value：0
	 */
	private Map<Integer, Integer> needed; 
	private String award;         //字符串形式的任务完成后的奖励
	/**
	 * 格式：
	 * 物品id：数量。如果物品id=0表示金币
	 */
	private Map<Integer, Integer> awardit;
	
	public void convert() {
		convertNeed();
		convertAward();
	}
	private void convertNeed() {
		needed = new HashMap<>();
		String[] aw2nu = need.split(";");
		for(int i = 0; i < aw2nu.length; i++) {
			if(type == 3) {  //表示副本通关类型的任务
				needed.put(Integer.parseInt(aw2nu[i]), 0);
			}else {
				String[] item = aw2nu[i].split(":");
				needed.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
			}
		}
		//System.out.println("任务类型：" + id + name +"，needed= " + needed.toString());
	}
	private void convertAward() {
		awardit = new HashMap<>();
		String[] aw2nu = award.split(";");
		for(int i = 0; i < aw2nu.length; i++) {
			String[] item = aw2nu[i].split(":");
			awardit.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNeed(String need) {
		this.need = need;
	}
	public void setAward(String award) {
		this.award = award;
	}
	public Map<Integer, Integer> getNeeded() {
		return needed;
	}
	/**
	 * 通过id获得相应的值(数量，0/1)
	 * @param id
	 * @return
	 */
	public int getNeededByid(int id) {
		return needed.get(new Integer(id));
	}
	public Map<Integer, Integer> getAwardit() {
		return awardit;
	}
	/**
	 * 通过id获得相应的值(数量)
	 * @param id
	 * @return
	 */
	public int getAwardByid(int id) {
		return awardit.get(new Integer(id));
	}

	
	
	
}
