package com.hc.login.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.creature.Player;

@Component
public class Store {
	//每页显示的商品的个数
	private final int PAGENUM = 3;
	
	/**
	 * 购买商品
	 * @param gId 物品id
	 * @return
	 */
	public boolean buyGood(Player player, int gId, int amount) {
		GoodsConfig goodsConfig = Context.getGoodsParse().getGoodsConfigById(gId);
		if(goodsConfig == null) return false;
		int price = goodsConfig.getPrice() * amount;
		if(!player.minusGold(price)) return false;
		player.addGoods(gId, amount);
		return true;
	}
	
	/**
	 * 显示商店的第几页，从1开始
	 * @param page 页数
	 */
	public String displStore(int page) {
		//验证是否有这个多页, 在order中以及验证过了
		//if(!isValiedPage(page)) return "";
		List<GoodsConfig> goodPage = aPage(page);
		StringBuilder sb = new StringBuilder();
		sb.append("【商店】- - - - - - - - - - - - - - - - - - - - - - - -- - - - - - - -\n");
		sb.append("- - - - - - - - - -第【" + page + "】页- - - - - - - - - - - -- -  -- - - - -\n");
		for(GoodsConfig gc : goodPage) {
			sb.append(gc.getName() + " " + gc.getDescription() + " " + gc.getPrice() + "金币\n");
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -- - - - -");
		return sb.toString();
	}
	
	/**
	 * 判断玩家输入的page是否有效
	 * 1、验证商店是否有那么多页面
	 * 2、验证玩家是否在相邻页面。玩家在这个页面也是有效的
	 * 3、验证page是否有效，比如0，负数等
	 * @param page
	 * @return
	 */
	public boolean isValiedPage(Player player, int page) {
		if(page < 1) return false;
		int start = PAGENUM * (page-1) + 1;
		int volum = Context.getGoodsParse().getGoodsList().size();
		if(start > volum) return false;	
		int playerPage = player.getPageNumber();
		if(page != playerPage && page != (playerPage + 1) && page != (playerPage - 1)) return false;
		return true;
	}
	
	/**
	 * 验证物品id是否在玩家浏览的这个商店页面
	 * @param player
	 * @param gId 物品id
	 * @return
	 */
	public boolean withinPage(Player player, int gId) {
		for(GoodsConfig gc : aPage(player.getPageNumber())) {
			if(gc.getId() == gId) return true;
		}
		return false;
	}
	
	/**
	 * 获得第n页的商品列表
	 * @param page 第几页
	 * @return
	 */
	private List<GoodsConfig> aPage(int page){
		List<GoodsConfig> goods = Context.getGoodsParse().getGoodsList();
		sortGoods(goods);
		List<GoodsConfig> result = new ArrayList<>();
		int start = PAGENUM * (page-1);
		int stop = PAGENUM + start;
		for(int i = start; i < stop; i++) {
			if(i >= goods.size()) break; //不足一页
			result.add(goods.get(i));
		}
		return result;
	}
	
	/**
	 * 对所有商品，按照物品类型id排序
	 * @param goods
	 */
	private void sortGoods(List<GoodsConfig> goods) {
		Collections.sort(goods, new Comparator<GoodsConfig>() {
			@Override
			public int compare(GoodsConfig g1, GoodsConfig g2) {
				int t1 = g1.getTypeId();
				int t2 = g2.getTypeId();
				if(t1 > t2) return 1;
				else if(t1 < t2) return -1;
				return 0;
			}
		});
		//System.out.println("----------排序后---------" + goods.toString());
	}
}
