package com.hc.logic.domain;

import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 玩家实体
 * 所有属性的变化，都是在player类的set方法中设置的。
 * @author hc
 *
 */
@Entity
@Table(name = "player")
public class PlayerEntity{
	
	@Id
	private Integer id;
    //名字
	@Column
	private String name;
	//密码
	@Column
	private String password;
	//等级
	@Column
	private int level;	
	//经验
	@Column
	private int exp;
	//场景id
	@Column
	private int sceneId;	
	//血量
	@Column
	private int hp;
	//法力
	@Column
	private int mp;
	//金币
	@Column
	private int gold = 100;
	//职业
	@Column
	private int profession = -1;  //表示没有职业
	
	@ManyToOne
	private CopyEntity copyEntity;
	
	@Transient
	private boolean needDel=false;   //是否需要从数据库中删除对应的CopyEntity
	
	@Column
	private String unionName;   //玩家所在工会的名字。
	@Column
	private int unionTitle = -1;  //所在工会的职位.title id: 1,2,

	//技能id
	@Column
	private String skills; //
	
	//物品
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private Set<GoodsEntity> goods = new HashSet<>();  //都用list会出现错误
	
	//邮件
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	private List<EmailEntity> emails = new ArrayList<>();
	//所有装备/武器，不包括已经穿戴的//orphanRemoval：在这个自段中删除Equip时，会在Equip表中删除相应的Equip

	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private AchieveEntity achieveEntity;
	
	@OneToOne(cascade = CascadeType.ALL)
	private TaskEntity taskEntity;
	
	
	
	//private boolean isChanged = false;
	
	public PlayerEntity() {
		
	}
	public PlayerEntity(int id, int level, String name, String pass, int sceneId, 
			int hp, int mp, int exp, String skil, List<Equip> equips, GoodsEntity goods) {
		this.id = id;
		this.level = level;
		this.name = name;
		this.password = pass;
		this.sceneId = sceneId;
		this.hp = hp;
		this.mp = mp;
		this.exp = exp;
		this.skills = skil;
		this.achieveEntity = new AchieveEntity();
		this.taskEntity = new TaskEntity(this);
		//this.equips = new ArrayList<>(equips);  //已穿戴的装备
		//this.goods = goods;
		
		//this.ortherEquips = new ArrayList<>();  //未穿戴的装备和其他物品
	}

	
	
	
	/**
	 * 通过数据库中的玩家实体，构造一个玩家对象
	 * @return
	 */
	public Player createPlayer(Session session) {
		String[] sk = skills.split(",");
		//System.out.println("createPlayer--" + skills + "-8888-");
		int[] re = {};
		if(sk != null && sk.length > 0 && !skills.equals("")) {
			re = new int[sk.length];
			for(int i = 0; i < sk.length; i++) {
				re[i] = Integer.parseInt(sk[i]);
			}
		}

		Player pe = new Player(session, re, this); 
		return pe;
	}

	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getSceneId() {
		return sceneId;
	}
	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public int getHp() {
		return hp;
	}


	public void setHp(int hp) {
		this.hp = hp;
	}


	public int getMp() {
		return mp;
	}


	public void setMp(int mp) {
		this.mp = mp;
	}

	
	
	public TaskEntity getTaskEntity() {
		return taskEntity;
	}
	public void setTaskEntity(TaskEntity taskEntity) {
		this.taskEntity = taskEntity;
	}
	public String getSkills() {
		return skills;
	}


	public void setSkills(List<Integer> skl) {
		StringBuilder sb = new StringBuilder();
		for(int ii : skl) {
			sb.append(ii + ",");
		}
		sb.deleteCharAt(sb.length()-1);

		this.skills = sb.toString();
	}
	
	public void addSkills(int sk) {
		StringBuilder sb = new StringBuilder(skills);
		sb.append("," + sk);
		skills = sb.toString();
	}

	
	
	public int getExp() {
		return exp;
	}


	public void setExp(int exp) {
		this.exp = exp;
	}
	
	

	public Set<GoodsEntity> getGoods() {
		return goods;
	}
	public void setGoods(Set<GoodsEntity> goods) {
		this.goods = goods;
	}
	public void delGoods(GoodsEntity ge) {
		this.goods.remove(ge);
	}
	
	/**
	 * 删除邮件
	 */
	public void delEmail(EmailEntity emailEnt) {
		this.emails.remove(emailEnt);
	}
	public List<EmailEntity> getEmails() {
		return emails;
	}
	public void setEmails(List<EmailEntity> emails) {
		this.emails = emails;
	}
	public boolean isNeedDel() {
		return needDel;
	}
	public void setNeedDel(boolean needDel) {
		this.needDel = needDel;
	}
	
	//public void addCopyEntity(CopyEntity copyEntity) {
	//	copyEntity.setPlayerEntity(this);
	//	this.copyEntity = copyEntity;
	//}
	//public void removeCopyEntity() {
	//	if(copyEntity != null) {
	//		copyEntity.setPlayerEntity(null);
	//		this.copyEntity = null;
	//	}
	//}
	public CopyEntity getCopyEntity() {
		return copyEntity;
	}
	public void setCopyEntity(CopyEntity copyEntity) {
		this.copyEntity = copyEntity;
	}

	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	public int getProfession() {
		return profession;
	}
	public void setProfession(int profession) {
		this.profession = profession;
	}
	
	public String getUnionName() {
		return unionName;
	}
	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}
	
	public int getUnionTitle() {
		return unionTitle;
	}
	public void setUnionTitle(int unionTitle) {
		this.unionTitle = unionTitle;
	}
	
	public AchieveEntity getAchieveEntity() {
		return achieveEntity;
	}
	public void setAchieveEntity(AchieveEntity achieveEntity) {
		this.achieveEntity = achieveEntity;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("名字：" + name + "\n");
		sb.append("场景: " + sceneId + "\n");
		sb.append("copyentity: " + copyEntity);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() != this.getClass()) return false;
		PlayerEntity p = (PlayerEntity)o;
		if(p.getName().equals(name)) return true;
		return false;
	}
	
	
}
