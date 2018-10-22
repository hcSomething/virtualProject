package com.hc.logic.basicService;

import com.hc.logic.base.Session;

/**
 * 首次注册进入场景时
 * @author hc
 *
 */
public class EnterWorld {

	public EnterWorld() {
		
	}
	
	public void enterWorld(Session session) {
		session.sendMessage("欢迎进入虚拟世界");
	}
}
