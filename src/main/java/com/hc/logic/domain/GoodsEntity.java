package com.hc.logic.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * 物品实体，不包括武器、装备
 * @author hc
 *
 */
@Entity
@Table(name = "goods")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("goods")
public class GoodsEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;   //物品id	
	/**
	 * 物品id
	 */
	@Column(name="goodId")
	private int eId;

	@ManyToOne
	private PlayerEntity playerEntity;
	
	@ManyToOne
	private UnionEntity unionEntity;
	
	
	
	
	
		
	public GoodsEntity() {
		
	}
	/**
	 * 
	 * @param goo  物品
	 * @param pe   玩家实体
	 */
	public GoodsEntity(int gId, PlayerEntity pe, UnionEntity ue) {
		this.eId = gId;
		this.playerEntity = pe;
		this.unionEntity = ue;
	}
	
	


	public int getId() {
		return id;
	}
	public void setId(int idd) {
		this.id = idd;
	}
	public int geteId() {
		return eId;
	}
	public void seteId(int eId) {
		this.eId = eId;
	}
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	public UnionEntity getUnionEntity() {
		return unionEntity;
	}
	public void setUnionEntity(UnionEntity unionEntity) {
		this.unionEntity = unionEntity;
	}
	@Override
	public String toString() {
		return "GoodsEntity {id=" + id
	           + ", eId=" + eId
	           + ", playerId=" + playerEntity.getId() + "}";
	}

	

	
}
