package com.hc.logic.config;

public class TitleConfig {

	private int id;
	private String name;
	private int allow;                // 是否有处理加入申请的权限。0：没有；1：有 
	private int promotion;            //是否有提升下级职位的权限。0：没有；1：有.降低权限和这个一样
	private int donate;               //捐献物品到工会，获得的经验 
	private int exp;                  //在工会中的职位提升需要的经验
	private int acquire;              //可从工会仓库中得到的物品数
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAllow() {
		return allow;
	}
	public void setAllow(int allow) {
		this.allow = allow;
	}
	public int getPromotion() {
		return promotion;
	}
	public void setPromotion(int promotion) {
		this.promotion = promotion;
	}
	public int getDonate() {
		return donate;
	}
	public void setDonate(int donate) {
		this.donate = donate;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getAcquire() {
		return acquire;
	}
	public void setAcquire(int acquire) {
		this.acquire = acquire;
	}
	
	
	
	
}
