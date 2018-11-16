package com.hc.logic.achieve;

import com.hc.frame.Context;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.AchieveEntity;

public enum Achievement {

	KILLM("KILLM", "杀怪"){
		@Override
		public void achieve(Player player, int args) {
			int id = args;  //需要传入怪物id
			player.getPlayerTasks().monstRecord(id);  //记录玩家击杀的怪物
			if(id == 2) {
				int num = player.getPlayerEntity().getAchieveEntity().getAtm2_5();
				player.getPlayerEntity().getAchieveEntity().setAtm2_5(num+1);
				if(num != -1 && (num+1) == 5) {
					//成就达成
					player.getPlayerEntity().getAchieveEntity().setAtm2_5(-1);
				}
			}
		}
	},
	LEVEL("level", "等级"){
		@Override
		public void achieve(Player player, int args) {
			if(player.getLevel() == 2) {
				AchieveEntity ae = player.getPlayerEntity().getAchieveEntity();
				if(ae.getLevl2() != -1) {
					ae.setLevl2(-1);
				}
			}
		}
	},
	NPC("npc", "npc相关"){
		@Override
		public void achieve(Player player, int para) {
			if(para == 1) {//需要传入npc id
				player.getAchieveEntity().setNpc1(-1);
			}
		}
	},
	EQUIP("equip", "装备相关"){
		@Override
		public void achieve(Player player, int para) {
			GoodsConfig gcf = Context.getGoodsParse().getGoodsConfigById(para);//传入装备id
			if(gcf.getAttack() > 100) {//将攻击力大于100的成为极品装备
				int nume = player.getAchieveEntity().gettEquip5();
				player.getAchieveEntity().settEquip5(nume+1);
				if(nume+1 == 5) {  //有5件极品装备，成就达成
					player.getAchieveEntity().settEquip5(-1);
				}
				return;
			}
			
				
		}
	},
	COPYS("copys", "副本相关"){
		@Override
		public void achieve(Player player, int para) {
			player.getPlayerTasks().copyRecord(para);  //需要传入完成的副本id
			if(para == 1) { //需要传入副本id
				player.getAchieveEntity().setCopy1(-1);
			}
		}
	},
	SOCIAL("social", "社交相关"){
		@Override
		public void achieve(Player player, int para) {
			
		}
	},
	GROUP("group", "组队相关"){
		@Override
		public void achieve(Player player, int para) {
			player.getAchieveEntity().setGroup1(-1);
		}
	},
	PARTY("party", "公会相关"){
		@Override
		public void achieve(Player player, int para) {
			player.getAchieveEntity().setParty1(-1);
		}
	},
	PK("pk", "pk相关"){
		@Override
		public void achieve(Player player, int para) {
			player.getAchieveEntity().setPk1(-1);
		}
	},
	GOLD("gold", "金币相关"){
		@Override
		public void achieve(Player player, int para) {
			if(player.getGold() > 500) {  //金币超过500，达成成就
				player.getAchieveEntity().setGold500(-1);
			}
		}
	};
	
	private String key;
	private String desc;
	
	private Achievement(String k, String v) {
		this.key = k;
		this.desc = v;
	}
	
	/**
	 * 验证是否达成成就
	 * @param player
	 * @param type
	 * @param aid, 
	 */
	public static void getService(Player player, String type, int aid) {
		for(Achievement ac : Achievement.values()) {
			if(ac.getKey().equals(type)) {
				ac.achieve(player, aid);
			}
		}
		
	}
	
	public abstract void achieve(Player player, int args);
	
	public String getKey() {
		return key;
	}
	
	public String getDesc() {
		return desc;
	}
	
	
}
