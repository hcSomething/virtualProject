package com.hc.logic.chat;

import java.util.Vector;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;

@Component
public class WorldChat {

	//一定大小的聊天记录，只记录最新的三十条记录，超过就删除最旧的记录
	private Vector<String> records = new Vector<>(30);
	
	/**
	 * 添加聊天记录。添加之后要推送给所有进入世界聊天的玩家
	 * @param msg
	 */
	synchronized public void addRecord(String msg) {
		if(records.size() < 30) {
			records.add(0, msg);
		}else {
			records.remove(records.size()-1);
			records.add(0, msg);
		}
		//通知所有进入世界频道的玩家
		Context.getWorldChatObservable().setMsg(msg);
	}
	
	
	//得到最新的一条记录
	public String getupdate() {
		return records.get(0);
	}
	
	/**
	 * 获得当前所有聊天记录
	 * @return
	 */
	synchronized public Vector<String> getRecords(){
		return new Vector<>(records);
	}
	
}
