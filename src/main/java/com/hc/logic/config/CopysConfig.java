package com.hc.logic.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 副本
 * @author hc
 *
 */
public class CopysConfig {

	private int id;
	private String name;            //副本名
	private String description;     //副本描述
	private int times;              //每天能进入副本次数
	private int condition;          //进入副本条件，这里是等级限制
	private int place;              //进入副本的场景id，同时也是从副本中出来的目的地
	private int continueT;          //（分钟）副本持续时间，超过时间还没完成，算失败
	private String sBosses;          //副本中所有的boss的id,以逗号分隔
	private List<Integer> bosses;
	private String sRewords;         //完成副本获得的奖励
	private Map<String, Integer> rewords;   //格式，key：奖励名，value：数量
	
	
	
	public void convert() {
		if(sBosses != null) {
			bosses = new ArrayList<>();
			String[] sbo = sBosses.split(",");
			for(String ss : sbo) {
				bosses.add(Integer.parseInt(ss));
			}
		}
		if(sRewords != null) {
			rewords = new HashMap<>();
			String[] sRe = sRewords.split(";");
			for(String ss : sRe) {
				String[] rewo = ss.split(":");
				rewords.put(rewo[0], Integer.parseInt(rewo[1]));
			}
		}
	}
	
	/**
	 * v判断bid这个boss之后是否还有boss。若有，则返回 boss id，否则，返回 -1
	 * 
	 * 怪物的生成是按顺序来到的
	 * @param bid boss的id
	 * @return
	 */
	public int moreBoss(int bid) {
		System.out.println("-----------------moreBoss--bid=" + bid);
		int bIndex = bosses.indexOf(new Integer(bid));
		if(bIndex >= (bosses.size()-1)) return -1;
		System.out.println("-----------------moreBoss-后-index=" + (bIndex+1) + " bosses列表" + bosses.toString());
		return bosses.get(bIndex+1);
	}
	
	
	
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getCondition() {
		return condition;
	}
	public void setCondition(int condition) {
		this.condition = condition;
	}
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public int getContinueT() {
		return continueT;
	}
	public void setContinueT(int continueT) {
		this.continueT = continueT;
	}
	public String getsBosses() {
		return sBosses;
	}
	public void setsBosses(String sBosses) {
		this.sBosses = sBosses;
	}
	public List<Integer> getBosses() {
		return bosses;
	}

	public String getsRewords() {
		return sRewords;
	}
	public void setsRewords(String sRewords) {
		this.sRewords = sRewords;
	}
	public Map<String, Integer> getRewords() {
		return rewords;
	}

	
	
	
	
	
}
