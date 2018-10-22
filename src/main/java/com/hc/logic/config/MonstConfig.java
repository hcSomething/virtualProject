package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

import com.hc.logic.creature.LiveCreature;


public class MonstConfig extends LiveCreature{

	private int monstId;
	private String name;
	private String description;
	private int Hp;  //怪物血量
	private int attack; //攻击力
	private boolean isAlive = true; //初始时，默认是活的
	private int exp; //击杀获得经验
	
	
	
	
	
	public int getMonstId() {
		return monstId;
	}
	public void setMonstId(int monstId) {
		this.monstId = monstId;
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
	public int getHp() {
		return Hp;
	}
	public void setHp(int hp) {
		Hp = hp;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	
	
	
	
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
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
		this.cId = monstId;
	}
}
