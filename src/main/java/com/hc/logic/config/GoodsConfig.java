package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品
 * @author hc
 *
 */
public class GoodsConfig{

	private int id;                //物品id
	private int typeId;            //类型id
	private String name;           //物品名 
	private String description;    //物品描述
	private int continueT;         //使用后，效果持续时间；对于武器装备，则是耐久度
	private int mp;                //每次增加法力
	private int hp;                //每次增加血量
	private int protect;            //增加防御  , 对于不是武器装备的物品
	private String sprotect;                   
	private List<Integer> protects = new ArrayList<>();  //对于属于武器装备的物品
	private int attack;             //增加攻击
	private String sattack;            
	private List<Integer> attacks = new ArrayList<>();
	private int superposi;          //在背包中的可叠加数量
	private int price;             //购买价格
	
	
	
	public void convert() {
		if(typeId != 2 && typeId != 3) {
			//不是武器装备
			convert(sprotect, protect);
			convert(sattack, attack);
		}else {
			//武器装备
			convert(sprotect, protects);
			convert(sattack, attacks);
		}
	}
	private void convert(String name, List<Integer> resu) {
		String[] names = name.split(",");
		for(int i = 0; i < names.length; i++) {
			resu.add(Integer.parseInt(names[i]));
		}
	}
	private void convert(String name, int resu) {
		//不是武器装备
		resu = Integer.parseInt(name);
	}
	
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		//System.out.println("typeId &&&&&&&&&&&&&&&&&&&" + typeId);
		this.typeId = typeId;
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
	public int getContinueT() {
		return continueT;
	}
	public void setContinueT(int continueT) {
		this.continueT = continueT;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getSuperposi() {
		return superposi;
	}
	public void setSuperposi(int superposi) {
		this.superposi = superposi;
	}

	public void setSprotect(String sprotect) {
		this.sprotect = sprotect;
	}

	public void setSattack(String sattack) {
		this.sattack = sattack;
	}

	public int getProtect() {
		return protect;
	}
	public List<Integer> getProtects() {
		return protects;
	}
	/**
	 * 根据职业获得相应武器装备的防御力
	 * @param prof
	 * @return
	 */
	public int getProtectByPfog(int prof) {
		return protects.get(prof);
	}
	public void setProtects(List<Integer> protects) {
		this.protects = protects;
	}
	public int getAttack() {
		return attack;
	}
	/**
	 * 根据职业获得物品的攻击力
	 * @param prof
	 * @return
	 */
	public int getAttackByProf(int prof) {
		return attacks.get(prof);
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public List<Integer> getAttacks() {
		return attacks;
	}
	public void setAttacks(List<Integer> attacks) {
		this.attacks = attacks;
	}
	@Override
	public String toString() {
		return "good {id=" + id
				+ ", typeId=" + typeId
				+", name" + name +"}";
	}
}
