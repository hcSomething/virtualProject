package com.hc.logic.achieve;

import java.util.List;

import com.hc.frame.Context;
import com.hc.logic.config.AchieveConfig;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.AchieveEntity;

public enum Achievement {

	KILLM("KILLM", "杀怪"){
		@Override
		public void achieve(Player player, int args) {
			int id = args;  //需要传入怪物id
			player.getPlayerTasks().monstRecord(id);  //记录玩家击杀的怪物,击杀任务
			List<AchieveConfig> achieves = Context.getAchieveParse()
					                       .getAchieveConfigByType(KILLM.ordinal()+1);
			for(AchieveConfig ac : achieves) {
				if(ac.getSid() == args) {
					//更新成就进度
					int amount = player.getPlayerAchieves().achieveProgress(ac.getId());
					if(ac.getNum() <= amount) {  //验证是否达成成就
						player.getPlayerAchieves().isComplete(ac.getId());
					}
				}
			}			
		}
	},
	LEVEL("level", "等级"){
		@Override
		public void achieve(Player player, int args) {
			
		}
	},
	NPC("npc", "npc相关"){
		@Override
		public void achieve(Player player, int para) {
			if(para == 1) {//需要传入npc id
				
			}
		}
	},
	EQUIP("equip", "装备相关"){
		@Override
		public void achieve(Player player, int para) {
			GoodsConfig goodsConfig = Context.getGoodsParse().getGoodsConfigById(para);//传入装备id			
			List<AchieveConfig> achieves = Context.getAchieveParse()
                    .getAchieveConfigByType(4);
			PlayerAchieves playerAchieve = player.getPlayerAchieves();
			for(AchieveConfig ac : achieves) {
				if(playerAchieve.isAchieveComplet(ac.getId())) return;
				if(ac.getDtype() == 1) {  //极品装备数量
					int num = -1;
					if(goodsConfig.getAttack() > ac.getSid()) {
						num = playerAchieve.achieveProgress(ac.getId());
					}
					if(ac.getNum() < num) {
						playerAchieve.isComplete(ac.getId());
					}
				}else if(ac.getDtype() == 2) { //装备等级
					
				}
			}							
		}
	},
	COPYS("copys", "副本相关"){
		@Override
		public void achieve(Player player, int para) {
			System.out.println("完成的副本id：" + para);
			player.getPlayerTasks().copyRecord(para);  //需要传入完成的副本id， 任务			
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
			
		}
	},
	PARTY("party", "公会相关"){
		@Override
		public void achieve(Player player, int para) {
			
		}
	},
	PK("pk", "pk相关"){
		@Override
		public void achieve(Player player, int para) {
			
		}
	},
	GOLD("gold", "金币相关"){
		@Override
		public void achieve(Player player, int para) {
			if(player.getGold() > 500) {  //金币超过500，达成成就
				
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
