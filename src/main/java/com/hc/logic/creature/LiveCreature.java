package com.hc.logic.creature;

public abstract class LiveCreature {
	
	
	protected int cId;

	//这个生物的描述
	protected String describe;
	

	
	public abstract void setDescribe();



	public int getcId() {
		return cId;
	}



	public abstract void setcId();
	
	
	
}
