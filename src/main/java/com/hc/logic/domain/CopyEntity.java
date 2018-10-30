package com.hc.logic.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class CopyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private int copyId;  //副本id
	
	@Column
	private long firstEnterTime;  //首次进入此副本的时间(毫秒)
	
	//表示在副本中已经打到了第几个boss
	@Column
	private int bossindex;   //进行到了第几个boss，index
	
	@OneToOne
	private PlayerEntity playerEntity;
	
	public CopyEntity() {
		
	}
	/**
	 * 
	 * @param copyId       副本id
	 * @param firstEn      首次进入此副本的时间(毫秒)
	 * @param playerEntity 对应的玩家
	 * @param index        在副本中已经打到第几个boss了
	 */
	public CopyEntity(int copyId, long firstEn, PlayerEntity playerEntity, int index) {
		this.copyId = copyId;
		this.firstEnterTime = firstEn;
		this.playerEntity = playerEntity;
		this.bossindex = index;
	}
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCopyId() {
		return copyId;
	}

	public void setCopyId(int copyId) {
		this.copyId = copyId;
	}

	public long getFirstEnterTime() {
		return firstEnterTime;
	}

	public void setFirstEnterTime(long firstEnterTime) {
		this.firstEnterTime = firstEnterTime;
	}

	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}

	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	public int getBossindex() {
		return bossindex;
	}
	public void setBossindex(int bossindex) {
		this.bossindex = bossindex;
	}
	
	
	
	
}

