package com.hc.logic.base;

public enum Profession {

	ZHANSHI("zhanShi", "战士", "拥有强大的攻击力，并且防御力惊人，天生的战斗机器"),
	MUSHI("muShi", "牧师", "团战中不可缺少的灵魂，拥有起死回生的作用"),
	FASHI("faShi","法师", "掌控大局者，拥有同时以一敌百的能力"),
	ZHAOHUANSHI("zhaoHuanShi","召唤师", "拥有跨越阴阳的神通，我家终于也有boss了");
	
	private String job;
	private String title;
	private String description;
	
	private Profession(String job, String na, String desc) {
		this.job = job;
		this.title = na;
		this.description = desc;
	}
	
	/**
	 * 根据字符串，返回相应的排位(从0开始)。
	 * @param a
	 * @return  -1表示没有这个值
	 */
	public static int getJob(String a) {
		for(Profession pf : Profession.values()) {
			if(pf.getJob().equals(a)) {
				return pf.ordinal();
			}
		}
		return -1;
	}

	/**
	 * 根据位置获得相应的枚举对象
	 * @param index
	 * @return
	 */
	public static Profession getProfByIndex(int index) {
		for(Profession pf : Profession.values()) {
			if(pf.ordinal() == index) {
				return pf;
			}
		}
		return null;
	}
	
	public String getJob() {
		return job;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}


	
	
}
