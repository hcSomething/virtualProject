package com.hc.logic.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
	
	@Column
	private String sponsor;   //组队的发起者名字
	
	@OneToMany
	private List<PlayerEntity> players = new ArrayList<>();
	
	public CopyEntity() {
		
	}
	/**
	 * 
	 * @param copyId       副本id
	 * @param firstEn      首次进入此副本的时间(毫秒)
	 * @param playerEntity 对应的玩家
	 * @param bindex        在副本中已经打到第几个boss了
	 * @param sponsorId   发起者id
	 */
	public CopyEntity(int copyId, long firstEn, List<PlayerEntity> player, int bindex, String sponsorId) {
		this.copyId = copyId;
		this.firstEnterTime = firstEn;
		//this.playerEntity = playerEntity;
		this.players = new ArrayList<>(player);
		this.bossindex = bindex;
		this.sponsor = sponsorId;
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

	public List<PlayerEntity> getPlayers() {
		return players;
	}
	public void setPlayers(List<PlayerEntity> players) {
		this.players = players;
	}
	public PlayerEntity getPlayerEntityById(int pid) {
		for(PlayerEntity pe : players) {
			if(pe.getId() == pid) {
				return pe;
			}
		}
		return null;
	}
	public int getBossindex() {
		return bossindex;
	}
	public void setBossindex(int bossindex) {
		this.bossindex = bossindex;
	}
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	
	
	@Override
	public String toString() {
		return "copyEntity cID=" + copyId
				+ ", sponsor=" + sponsor + ". ";
	}
	
	
}

