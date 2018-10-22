package com.hc.logic.creature;

public abstract class Npc extends LiveCreature{
	protected String mission;

	abstract void setMission(String mission);
	
	public String getMission() {
		return mission;
	}
}
