package com.hc.logic.config;

import com.hc.logic.creature.LiveCreature;

public class NpcConfig  extends LiveCreature{

	private int npcId;
	private String name;
	private String description;
	private String task;  //ÈÎÎñ
	
	
	
	
	
	
	
	
	
	public int getNpcId() {
		return npcId;
	}
	public void setNpcId(int npcId) {
		this.npcId = npcId;
		setcId();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	
	
	@Override
	public void setDescribe() {
		this.describe = description;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public void setcId() {
		this.cId = npcId;
	}

	
}
