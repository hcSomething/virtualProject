package com.hc.logic.base;

import com.hc.logic.creature.Player;

public enum Constants {

	GOLD("gold", "金币"){
		@Override
		public void getReword(Player player, String goodName, String amount) {
			int am = Integer.parseInt(amount);
			player.addGold(am);
		}
	},
	EXP("exp", "经验"){
		@Override
		public void getReword(Player player, String goodName, String amount) {
			player.addExp(Integer.parseInt(amount));
		}
	},
	GOOD("good", "物品装备"){
		@Override
		public void getReword(Player player, String goodId, String amount) {
			//物品在配置中的格式：物品id：数量。
			player.addGoods(Integer.parseInt(goodId), Integer.parseInt(amount));
		}
	};
	
	private String key;
	private String value;
	
	private Constants(String k, String v) {
		this.key = k;
		this.value = v;
	}
	
	/**
	 * 解析语句，获得奖励
	 */
	public static void doReword(Player player, String consName, String consVal) {
		//所有的物品装备的配置格式。物品id：数量
		if(Character.isDigit(consName.charAt(0))) {
			for(Constants constants : Constants.values()) {
				if(constants.getKey().equals("good")) {
					constants.getReword(player, consName, consVal);
					return;
				}
			}
		}
		
		for(Constants constants : Constants.values()) {
			if(constants.getKey().equals(consName)) {
				constants.getReword(player, consName, consVal);
				break;
			}
		}
	}
	
	public abstract void getReword(Player player, String goodName, String amount);
	
	
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
