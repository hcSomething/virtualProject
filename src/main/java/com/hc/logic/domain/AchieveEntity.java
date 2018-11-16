package com.hc.logic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class AchieveEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column
	private int atm2_5;   //杀id=2的怪物的个数到达5达成成就	0，1，2，。。。标识没有达成。-1表示达成
	@Column
	private int npc1;       //和npc id=1对话达成成就
	@Column
	private int levl2;      //等级首次到达2，达成成就
	@Column
	private int tEquip5;     //顶级装备达到5件，达成成就
	@Column
	private int copy1;      //通过副本1，达成成就
	@Column
	private int el5;        //穿戴的装备等级打到5级，达成成就
	@Column
	private int friend1;    //添加第一个好友达成成就
	@Column
	private int group1;      //第一次组队，达成成就
	@Column
	private int party1;     //加入第一个公会，达成成就
	@Column
	private int deal1;       //第一次完成交易，达成成就
	@Column
	private int pk1;        //第一次pk胜利，达成成就 
	@Column
	private int gold500;     //第一次金币达到500，达成成就    
	
	@OneToOne(mappedBy="achieveEntity")
	private PlayerEntity playerEntity;
	
	public AchieveEntity(PlayerEntity pe) {
		this.playerEntity = pe;
	}
	public AchieveEntity() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAtm2_5() {
		return atm2_5;
	}

	public void setAtm2_5(int atm2_5) {
		this.atm2_5 = atm2_5;
	}

	public int getNpc1() {
		return npc1;
	}

	public void setNpc1(int npc1) {
		this.npc1 = npc1;
	}

	public int gettEquip5() {
		return tEquip5;
	}

	public void settEquip5(int tEquip5) {
		this.tEquip5 = tEquip5;
	}

	public int getCopy1() {
		return copy1;
	}

	public void setCopy1(int copy1) {
		this.copy1 = copy1;
	}

	public int getEl5() {
		return el5;
	}

	public void setEl5(int el5) {
		this.el5 = el5;
	}

	public int getFriend1() {
		return friend1;
	}

	public void setFriend1(int friend1) {
		this.friend1 = friend1;
	}

	public int getGroup1() {
		return group1;
	}

	public void setGroup1(int group1) {
		this.group1 = group1;
	}

	public int getParty1() {
		return party1;
	}

	public void setParty1(int party1) {
		this.party1 = party1;
	}

	public int getDeal1() {
		return deal1;
	}

	public void setDeal1(int deal1) {
		this.deal1 = deal1;
	}

	public int getPk1() {
		return pk1;
	}

	public void setPk1(int pk1) {
		this.pk1 = pk1;
	}

	public int getGold500() {
		return gold500;
	}

	public void setGold500(int gold500) {
		this.gold500 = gold500;
	}
	public int getLevl2() {
		return levl2;
	}
	public void setLevl2(int levl2) {
		this.levl2 = levl2;
	}
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}

	
	
	
	
	
}
