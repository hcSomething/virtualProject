package com.hc.logic.creature;

public class Npc extends LiveCreature{
	protected String mission;
	private int npcId;

	 void setMission(String mission) {
		 this.mission = mission;
	 }
	
	public String getMission() {
		return mission;
	}
	
	@Override
	public void setDescribe() {
		
	}
	
	@Override
	public void setcId() {
		this.cId = npcId;
	}
}
