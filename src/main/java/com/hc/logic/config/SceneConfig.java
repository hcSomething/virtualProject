package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

public class SceneConfig {

	private int sceneId;
	private String name;
	private String description;
	private String monst;   //解析之前的怪物id用逗号隔开, 只能写
	private String npc;
	private String teleport;
	
	private List<Integer> monsts = new ArrayList<>();   //解析之后的所有怪物的id，只能读
	private List<Integer> npcs = new ArrayList<>();
	private List<Integer> teleports = new ArrayList<>();
	
	
	public void parseString() {
		String[] s1 = {};
		if(monst != null) {
			s1 = monst.split(",");
		}
		
		String[] s2 = {};
		if(npc != null) {
			s2 = npc.split(",");
		}
		String[] s3 = {};
		if(teleport != null) {
			s3 = teleport.split(",");
		}
		
		for(String s : s1) {
			monsts.add(Integer.parseInt(s));
		}
		for(String s : s2) {
			npcs.add(Integer.parseInt(s));
		}
		for(String s : s3) {
			teleports.add(Integer.parseInt(s));
		}
	}

	
	
	
	
	public int getSceneId() {
		return sceneId;
	}
	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
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
	public void setMonst(String monst) {
		this.monst = monst;
	}
	public void setNpc(String npc) {
		this.npc = npc;
	}
	public void setTeleport(String teleport) {
		this.teleport = teleport;
	}

	public List<Integer> getMonsts() {
		return monsts;
	}

	public List<Integer> getNpcs() {
		return npcs;
	}

	public List<Integer> getTeleports() {
		return teleports;
	}


	
	
	
	
	
}
