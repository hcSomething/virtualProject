package com.hc.logic.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("equip")
public class Equip extends GoodsEntity{
	
	@Column
	private int duraion;  //耐久度
	
	@Column 
	private int state;  //物品当前的状态,0：背包中；1：已穿着
	
	
	

	
	public Equip() {
		
	}
	/**
	 * 
	 * @param eId 物品id
	 * @param dr  耐久度
	 */
	public Equip(int eId, int dr, PlayerEntity pe, UnionEntity ue) {
		super(eId, pe, ue);
	    this.duraion = dr;
	    this.state = 0;  //新加的物品默认放在背包
	}

	public int getDuraion() {
		return duraion;
	}

	public void setDuraion(int duraion) {
		this.duraion = duraion;
	}
	
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "Equip{ "
				+ ", duration=" + duraion
				+" }";
	}
	
    
	
}
