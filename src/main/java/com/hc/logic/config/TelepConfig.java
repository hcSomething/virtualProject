package com.hc.logic.config;

public class TelepConfig {

	private int teleId;
	private String description;
	private int sceneid;       //能传送到的目标场景id
	private int level = 0;     //能使用此传送阵的等级
	private int task = 0;      //需要完成此任务才能使用此传送阵
	
	
	
	public int getTeleId() {
		return teleId;
	}
	public void setTeleId(int teleId) {
		this.teleId = teleId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getTask() {
		return task;
	}
	public void setTask(int task) {
		this.task = task;
	}
	public int getSceneid() {
		return sceneid;
	}
	public void setSceneid(int sceneid) {
		this.sceneid = sceneid;
	}
	
	
	
	
	
	
}
