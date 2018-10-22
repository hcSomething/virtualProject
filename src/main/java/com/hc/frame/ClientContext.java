package com.hc.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc.frame.swing.CommandAction;
import com.hc.frame.swing.GameDisplay;

import io.netty.channel.Channel;

public class ClientContext {
	

	private static Map<Channel, CommandAction> map = new HashMap<>(); //живЊ
	
	private static ClientContext instance = new ClientContext();

	private ClientContext() {}
	
	public static ClientContext getInstance() {
		return instance;
	}

	public static Map<Channel, CommandAction> getMap() {
		return map;
	}


	
}
