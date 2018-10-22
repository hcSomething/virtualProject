package com.hc.logic.config;

public class GoodsConfig {

	private int id;              
	private int typeId;            //类型id
	private String name;           //物品名 
	private String description;    //物品描述
	private int continueT;         //使用后，效果持续时间
	private int mp;                //每次增加法力
	private int hp;                //每次增加血量
	private int protect;           //增加防御
	private int attack;            //增加攻击
	private int superposi;          //在背包中的可叠加数量
	private int price;             //购买价格
	
	
	
	
	
	
	
	
	
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
	public int getProtect() {
		return protect;
	}
	public void setProtect(int protect) {
		this.protect = protect;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
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
	
	
}
