
package com.hc.logic.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TaskEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	/**
	 * 格式：任务id,id,数量;任务id,id,数量
	 * 任务id可以重复。比如端点是：任务1，杀了两只id= 1的怪，杀了一只id= 2的怪
	 *             则：1,1,2;1,2,1
	 */
	@Column
	private String progressTask;
	/** 格式：任务id，任务id*/
	@Column
	private String notAward;
	/** 格式：任务id，任务id*/
	@Column
	private String awarded;
	
	@OneToOne(mappedBy="taskEntity")
	private PlayerEntity playerEntity;
	
	
	public TaskEntity() {
		
	}
	public TaskEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProgressTask() {
		return progressTask;
	}
	public void setProgressTask(String progressTask) {
		this.progressTask = progressTask;
	}
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	public String getNotAward() {
		return notAward;
	}
	public void setNotAward(String notAward) {
		this.notAward = notAward;
	}
	public String getAwarded() {
		return awarded;
	}
	public void setAwarded(String awarded) {
		this.awarded = awarded;
	}
	
	
	
	
}
