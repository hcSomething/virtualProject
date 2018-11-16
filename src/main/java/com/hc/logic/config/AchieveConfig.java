package com.hc.logic.config;

public class AchieveConfig {

	private int id;       
	private String name;      //成就名
	private String desc;      //成就描述
	private String charac;    //这个值和成就实体中的字段相同
	
	
	
	
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCharac() {
		return charac;
	}
	public void setCharac(String charac) {
		this.charac = charac;
	}
	
	@Override
	public String toString() {
		return "{name=" + name
				+ ", charac=" + charac + "}";
	}
	
}
