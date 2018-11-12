package com.hc.logic.config;

public class SkillConfig {

	private int skillId;   
	private String name;
	private String description;
	private int cd;  //冷却时间
	private int attack;  //攻击力
	private int protect;  //对自身的保护，比如能加防御，
	private int cure;     //（牧师）专有，恢复生命值
	private int mp;      //消耗的法力
	private int weapon;   //需要的武器(物品id)
	private int continueT;   //(秒)技能持续时间
	private int dizziness;  //眩晕（秒），期间不能使用技能
	private int profession; //能学此技能的职业。0，1，2，3：战士，牧师，法师，召唤师。10：所有职业都可以学
	private int summonBoss;  //（召唤师）召唤的boss id
	private int scope;  //能同时攻击多少玩家;0：无限；1：一个,。 。 。
	
	
	
	
	
	
	public int getSkillId() {
		return skillId;
	}
	public void setSkillId(int skillId) {
		this.skillId = skillId;
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
	public int getCd() {
		return cd;
	}
	public void setCd(int cd) {
		this.cd = cd;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public int getProtect() {
		return protect;
	}
	public void setProtect(int protect) {
		this.protect = protect;
	}
	
	public int getCure() {
		return cure;
	}
	public void setCure(int cure) {
		this.cure = cure;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getContinueT() {
		return continueT;
	}
	public void setContinueT(int continueT) {
		this.continueT = continueT;
	}	
	public int getDizziness() {
		return dizziness;
	}
	public void setDizziness(int dizziness) {
		this.dizziness = dizziness;
	}
	public int getScope() {
		return scope;
	}
	public void setScope(int scope) {
		this.scope = scope;
	}
	public int getWeapon() {
		return weapon;
	}
	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}
	
	public int getProfession() {
		return profession;
	}
	public void setProfession(int profession) {
		this.profession = profession;
	}
	
	public int getSummonBoss() {
		return summonBoss;
	}
	public void setSummonBoss(int summonBoss) {
		this.summonBoss = summonBoss;
	}
	@Override
	public String toString() {
		return name;
	}
	
	
}
