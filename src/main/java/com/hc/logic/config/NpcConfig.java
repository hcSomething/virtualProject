package com.hc.logic.config;

import com.hc.logic.creature.LiveCreature;

public class NpcConfig  extends LiveCreature{

	private int npcId;
	private String name;
	private String description;
	private String task;  //任务
	private int receive;  //可以发放的任务id
	private int checkout; //可以验证任务是否完成的任务id。即，可接收完成的任务id
	private int goodId = 0;    //此npc可以卖的物品的id。0表示默认不卖东西
	private int copyId = 0;    //可以进入的副本id
	
	
	
	
	
	
	
	
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
	
	
	public int getReceive() {
		return receive;
	}
	public void setReceive(int receive) {
		this.receive = receive;
	}
	public int getCheckout() {
		return checkout;
	}
	public void setCheckout(int checkout) {
		this.checkout = checkout;
	}
	
	public int getGoodId() {
		return goodId;
	}
	public void setGoodId(int goodId) {
		this.goodId = goodId;
	}
	
	public int getCopyId() {
		return copyId;
	}
	public void setCopyId(int copyId) {
		this.copyId = copyId;
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
