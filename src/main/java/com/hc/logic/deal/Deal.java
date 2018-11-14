package com.hc.logic.deal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hc.frame.Context;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;

public class Deal {

	private String tpName;  //交易另一方的名字
	private String spName;  //发起者名字
	//交易双方交易的物品和数量。格式， key：物品id/gold ; value: 数量
	private Map<String, Integer> tpDeal = new HashMap<>();
	private Map<String, Integer> spDeal = new HashMap<>();  //发起者希望交易的物品和数量
	private boolean tpAccep;   //是否同意进行交换
	private boolean spAccep;
	private Lock lock = new ReentrantLock();
	//需要交换的物品
	private List<GoodsEntity> exchange = new ArrayList<>();
	private List<GoodsEntity> aexchange = new ArrayList<>();

	public Deal(String p) {
		this.tpName = p;
	}
	
	public void acceptDeal(Player tPlayer, String sName) {
		lock.lock();
		try {
			if(spName != null || !tPlayer.getName().equals(tpName)) {
				tPlayer.getSession().sendMessage("对方已经在交易中，组队失败");
				return;
			}
			this.spName = sName;
			tPlayer.accDeal(this);
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 当双方都同意交换后，开始交换物品
	 */
	public void exchangeGoods() {
		System.out.println("-----开始交换-----" + tpDeal.toString() +", " + spDeal.toString());
		System.out.println("---交易双方-----" + tpName + ", " + spName);
		lock.lock();
		try {
			Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tpName);
			Player sPlayer = Context.getOnlinPlayer().getPlayerByName(spName);
			//playerDelGoods(tPlayer, tpDeal, exchange);
			//playerAddGoods(tPlayer, spDeal);
			//playerAddGoods(sPlayer, tpDeal, exchange);
			//playerDelGoods(sPlayer, spDeal, exchange);
			playerDelGoods(tPlayer, tpDeal, exchange);
			playerDelGoods(sPlayer, spDeal, aexchange);
			System.out.println("---------前--exchange---" + exchange.size());
			System.out.println("-----------前---" + aexchange.size());
			playerAddGoods(tPlayer, spDeal, aexchange);
			playerAddGoods(sPlayer, tpDeal, exchange);
			System.out.println("----------后--exchange--" + exchange.size());
			System.out.println("--------------" + aexchange.size());
			notHopeDeal(tpName);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 
	 * @param player 减少物品的玩家
	 * @param goods  减少的物品
	 * @param changex 如果减少的是物品，则放入这个里面，否则保持null
	 */
	private void playerDelGoods(Player player, Map<String, Integer> goods,  List<GoodsEntity> changex) {
		for(Entry<String, Integer> ent : goods.entrySet()) {
			if(!OrderVerifyService.isDigit(ent.getKey())) { //金币
				player.minusGold(ent.getValue());
			}else {  //物品
				//this.exchange = new ArrayList<>();
			    int gid = Integer.parseInt(ent.getKey());
			    List<GoodsEntity> glist = player.delGoods(gid, ent.getValue());
			    for(GoodsEntity ge : glist) {  //交换物品(玩家和工会之间)
					GoodsEntity nge = Context.getGoodsService().changeGoods(ge);
					changex.add(nge);
				}
			}
		}
	}
	/**
	 * 获得交换的物品
	 * @param player 获得的玩家
	 * @param goods  获得的物品
	 * @param changex  如果获得的是物品，则再这里面，否则这个为null
	 */
	private void playerAddGoods(Player player, Map<String, Integer> goods, List<GoodsEntity> changes) {
		if(changes.size() == 0) {
			player.addGold(goods.get("gold"));
			return;
		}
		for(GoodsEntity ge : changes) {
			ge.setPlayerEntity(player.getPlayerEntity());
			player.addGoods(ge);
		}
		changes = null;
	}
	private void playerAddGoods(Player player, Map<String, Integer> goods) {
		for(Entry<String, Integer> ent : goods.entrySet()) {
			if(ent.getKey().equals("gold")){
				player.addGold(ent.getValue());
			}else {
				player.addGoods(Integer.parseInt(ent.getKey()), ent.getValue());
			}
		}
	}
	
	/**
	 * 进入交易状态后，可以放置物品
	 * @param pName : 放置物品的玩家名
	 * @param args: deal 物品名 数量
	 */
	public void showGoods(String pName, String[] args) {
		lock.lock();
		try {
			if(pName.equals(tpName)) {
				tpDeal.put(args[1], Integer.parseInt(args[2]));
			}else {
				spDeal.put(args[1], Integer.parseInt(args[2]));
			}
		}finally {
			lock.unlock();
		}
	}
	
	
	
	public String getTpName() {
		return tpName;
	}
	public String getSpName() {
		return spName;
	}
	public void setSpName(String spName) {
		this.spName = spName;
	}
	/**
	 * 获得交易对方的名字
	 * @param name
	 * @return
	 */
	public String getCounter(String name) {
		if(name.equals(spName)) return tpName;
		return spName;
	}

	/**
	 * 同意进行交换
	 * @param pName 同意交换的玩家名
	 */
	public void hopeDeal(String pName) {
		lock.lock();
		try {
			if(pName.equals(spName)) {
				this.spAccep = true;
			}else if(pName.equals(tpName)) {
				this.tpAccep = true;
			}
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 不同意交换
	 * @param pName
	 */
	public void notHopeDeal(String pName) {
		lock.lock();
		try {
			tpAccep = false;
			spAccep = false;
			tpDeal.clear();
			spDeal.clear();
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 是否已经可以开始进行交换确认
	 * @return
	 */
	public boolean isReadyVerify() {
		lock.lock();
		try {
			if(tpDeal.size() == 0 || spDeal.size() == 0) {
				return false;
			}
			return true;
		}finally {
			lock.unlock();
		}
	}
	/**
	 * 是否可以开始交换了
	 * @return
	 */
	public boolean isReadyChange() {
		lock.lock();
		try {
			if(!tpAccep || !spAccep) {
				return false;
			}
			return true;
		}finally {
			lock.unlock();
		}
	}

	
}
