package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.logic.creature.LiveCreature;


public class MonstConfig{

	private int monstId;
	private String name;
	private String description;
	private int Hp;  //怪物血量
	private int attack; //攻击力
	private boolean isAlive = true; //初始时，默认是活的
	private int exp; //击杀获得经验
	private int attackP;  //是否会主动攻击玩家。0：不会，1：会
	private int revive;   //复活需要的时间（秒）
		
	//boss专用
	private int gold;
	private String skiStr;     //boss skills的字符串形式
	private List<Integer> skills; //boss的技能id
	
	
	/**
	 * 解析skiStr，化为skills
	 */
	public void convert() {
		if(skiStr == null) return;
		if(skills == null) skills = new ArrayList<>();
		String[] sk = skiStr.split(",");
		for(String s : sk) {
			skills.add(Integer.parseInt(s));
		}
	}
	
	public int getMonstId() {
		return monstId;
	}
	public void setMonstId(int monstId) {
		this.monstId = monstId;
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
	
	
	
	
	public int getAttackP() {
		return attackP;
	}

	public void setAttackP(int attackP) {
		this.attackP = attackP;
	}

	public String getSkiStr() {
		return skiStr;
	}
	public void setSkiStr(String skiStr) {
		this.skiStr = skiStr;
	}
	public List<Integer> getSkills() {
		return skills;
	}

	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
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
	
	public int getRevive() {
		return revive;
	}

	public void setRevive(int revive) {
		this.revive = revive;
	}

	@Override
	public String toString() {
		return name;
	}
		
	/**
	 * 返回boss技能
	 * @return
	 */
	public String bossSkillList() {
		StringBuilder sb = new StringBuilder();
		for(int i : skills) {
			String name = Context.getSkillParse().getSkillConfigById(i).getName();
			sb.append(name + ",");
		}
		if(sb.length() > 1) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
