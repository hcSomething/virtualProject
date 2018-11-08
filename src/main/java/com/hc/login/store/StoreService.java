package com.hc.login.store;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;

@Component
public class StoreService {
	
	/**
	 * 浏览商店，并更新玩家商店页面
	 * @param session
	 * @param page
	 */
	public void lookStore(Session session, int page) {
		Store store = Context.getStore();
		if(!store.isValiedPage(session.getPlayer(), page)) {
			session.sendMessage("跳转失败！商店没有这么页面，或者玩家不在相邻页面");
			return;
		}
		String dis = store.displStore(page);
		//更新玩家浏览商店页面
		session.getPlayer().setPageNumber(page); 
		if(dis.equals("")) session.sendMessage("空");
		else session.sendMessage(dis);
	}
	
	/**
	 * 购买物品，并验证当前商店页面是否有此物品
	 * @param session
	 * @param gid
	 * @param amount
	 */
	public void validBuyGood(Session session, int gid, int amount) {
		if(session.getPlayer().getPageNumber() == 0) {
			session.sendMessage("当前还不在商店页面，请先进入商店");
			return;
		}
		Store store = Context.getStore();
		//验证要购买的商品是否在这个商店页面
		boolean within = store.withinPage(session.getPlayer(), gid);
		if(!within) {
			session.sendMessage("您要购买的商品不在当前列表中，请检查参数是否正确！");
			return;
		}
		if(amount > 100) {
			session.sendMessage("购买失败，一次只能购买100件相应物品");
			return;
		}
		boolean hasbuy = store.buyGood(session.getPlayer(), gid, amount);
		if(!hasbuy) session.sendMessage("购买失败，请检查是否有足够的金币");
		else session.sendMessage("购买成功");
	}

}
