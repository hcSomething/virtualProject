package com.hc.logic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class EmailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private String content;  //邮件内容

	@Column
	private long sendTime;  //发送邮件的时间
	
	@ManyToOne
	private PlayerEntity playerEntity;

	
	
	public EmailEntity() {
		
	}
	
	public EmailEntity(String content, PlayerEntity pe) {
		this.content = content;
		this.playerEntity = pe;
		this.sendTime = System.currentTimeMillis();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}

	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}

	
	@Override
	public String toString() {
		return "email {content=" + content
				+ ", time=" + sendTime + "}";
	}
	
}
