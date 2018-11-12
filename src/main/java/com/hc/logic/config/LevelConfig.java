package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

public class LevelConfig {

	private int id;  //也表示等级
	private String exp; //最大经验
	private List<Integer> exps = new ArrayList<>();
	private String hp;  //最大血量
	private List<Integer> hps = new ArrayList<>();
	private String mp;  //最大法力
	private List<Integer> mps = new ArrayList<>();
	private int uHp; //每秒恢复血量
	private int uMp; //每秒恢复法力
	private String lAttack; //等级对应的攻击力
	private List<Integer> lAttacks = new ArrayList<>();
	
	
	
	/**
	 * 转换对应的string到相应的list
	 */
	public void convert() {
		convert(exp, exps);
		convert(hp, hps);
		convert(mp, mps);
		convert(lAttack, lAttacks);
	}	
	private void convert(String cont, List<Integer> resu) {
		String[] conts = cont.split(",");
		for(int i = 0; i < conts.length; i++) {
			resu.add(Integer.parseInt(conts[i]));
		}
	}
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
    public int getuHp() {
		return uHp;
	}
	public void setuHp(int uHp) {
		this.uHp = uHp;
	}
	public int getuMp() {
		return uMp;
	}
	public void setuMp(int uMp) {
		this.uMp = uMp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public void setHp(String hp) {
		this.hp = hp;
	}
	public void setMp(String mp) {
		this.mp = mp;
	}
	public void setlAttack(String lAttack) {
		this.lAttack = lAttack;
	}
	/**
	 * 根据职业获得等级对应的经验值
	 * @param prof
	 * @return
	 */
	public int getExpByProf(int prof) {
		return exps.get(prof);
	}
	public List<Integer> getExps() {
		return exps;
	}
	/**
	 * 根据职业获得等级对应的最大血量
	 * @param prof
	 * @return
	 */
	public int getHpByProf(int prof) {
		return hps.get(prof);
	}
	public List<Integer> getHps() {
		return hps;
	}
	/**
	 * 根据职业获得等级对于的最大法力
	 * @param prof
	 * @return
	 */
	public int getMpByProf(int prof) {
		return mps.get(prof);
	}
	public List<Integer> getMps() {
		return mps;
	}
	/*
	 * 根据职业获得等级对应的攻击力
	 */
	public int getAttackByProf(int prof) {
		return lAttacks.get(prof);
	}
	public List<Integer> getlAttacks() {
		return lAttacks;
	}
	
	
	
	
	
}
