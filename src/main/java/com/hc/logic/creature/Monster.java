package com.hc.logic.creature;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hc.frame.Context;
import com.hc.logic.config.MonstConfig;

public class Monster extends LiveCreature{

	private int monstId;
	private int Hp;  //怪物血量
	private int attack; //攻击力
	private boolean isAlive = true; //初始时，默认是活的
	private Lock lock = new ReentrantLock();
	
	MonstConfig monstConfig;	
	
	public Monster(int mId) {
		this.monstId = mId; 
		monstConfig = Context.getSceneParse().getMonsters().getMonstConfgById(monstId);
		this.Hp = monstConfig.getHp();
		this.attack = monstConfig.getAttack();
	}
	
	/**
	 * 判断能不能打.
	 * 攻击失败，怪物被别的玩家击杀：-1
	 * 攻击成功，怪物没被自己击杀：0
	 * 攻击成功，怪物被自己击杀：1
	 * @param skillid
	 * @param player
	 * @return  是否攻击成功
	 */
	public int canAttack(int skillid, Player player) {
		int reduce = player.AllAttack(skillid);
		System.out.println("------------Monster.canAttack-------");
	    return attack(reduce);
	}
	/**
	 * 进行攻击
	 * 攻击失败，怪物被别的玩家击杀：-1
	 * 攻击成功，怪物没被自己击杀：0
	 * 攻击成功，怪物被自己击杀：1
	 * @param attack
	 * @return
	 */
	public int attack(int attack) {
		lock.lock();
		try {
			int diff = Hp - attack;
		    if(Hp > 0) {  //未死，可以攻击
		    	if(diff > 0) {
		    		Hp = diff;
		    	}else {
		    		Hp = 0;
		    		isAlive = false;
		    		return 1;
		    	}
		    	return 0;
		    }else {
		    	return -1;
		    }
		}finally {
			lock.unlock();
		}
	}
	
	
	public int pAttackM() {
		return 0;
	}
	
	public String getName() {
		return monstConfig.getName();
	}
	public String getDescription() {
		return monstConfig.getDescription();
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public int getExp() {
		return monstConfig.getExp();
	}
	public int getAttackP() {
		return monstConfig.getAttackP();
	}
	public MonstConfig getMonstConfig() {
		return monstConfig;
	}
	public void setMonstConfig(MonstConfig monstConfig) {
		this.monstConfig = monstConfig;
	}
	public int getGold() {
		return this.monstConfig.getGold();
	}



	public int getMonstId() {
		return monstId;
	}
	public void setMonstId(int monstId) {
		this.monstId = monstId;
	}
	public int getHp() {
		return Hp;
	}
	public void setHp(int hp) {
		Hp = hp;
	}
    public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public String bossSkillList() {
		return this.monstConfig.bossSkillList();
	}


	@Override
	public void setDescribe() {
		this.describe = getDescription();
	}
	
	@Override
	public void setcId() {
		this.cId = monstId;
	}

	@Override
	public String toString() {
		return monstConfig.getName();
	}
}
