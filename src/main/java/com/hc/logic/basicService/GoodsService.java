package com.hc.logic.basicService;

import com.hc.frame.Context;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

public class GoodsService {

	/**
	 * 添加物品
	 * @param pe
	 * @param gid 物品id
	 */
	public void addGoods(PlayerEntity pe, int gid) {
		int tID = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId();
		int cont = Context.getGoodsParse().getGoodsConfigById(gid).getContinueT();
		
		GoodsEntity ge = null;
		if(tID != 2 && tID != 3) { //添加物品
			ge = new GoodsEntity(gid, pe);		
		}else {//添加装备
			ge = new Equip(gid, cont, pe);
		}
		pe.getGoods().add(ge);
	}
	
	/**
	 * 删除物品
	 * @param pe
	 * @param gid 物品id
	 */
	public void delGoods(PlayerEntity pe, int gid) {
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid) {
				if(delGood(ge, pe)) return;
			}
		}
	}
	
	private boolean delGood(GoodsEntity goodsEntity, PlayerEntity playerEntity) {
		if(goodsEntity instanceof Equip) {
			Equip equip = (Equip)goodsEntity;
			if(equip.getState() == 1)
				return false;
		}
		playerEntity.delGoods(goodsEntity);
		return true;
	}
	
	/**
	 * 穿上装备
	 * @param gid 物品id
	 */
	public void doEquip(int gid, PlayerEntity pe) {
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid) {
				if(ge instanceof Equip) {
					Equip eq = (Equip)ge;
					eq.setState(1);
					return;
				}
			}
		}
	}
	
	/**
	 * 卸下装备
	 * @param gid 物品id
	 * @param pe
	 */
	public void deEquip(int gid, PlayerEntity pe) {
		int tID = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId();
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == gid && (eq.getState() == 1)) {
					eq.setState(0);
					return;
				}
			}
		}

	}
	/**
	 * 判断是否已经装备
	 * @param gid 物品id
	 * @param pe
	 * @return
	 */
	public boolean isEquiped(int gid, PlayerEntity pe) {
		int tID = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId();
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == gid && eq.getState() == 1) {  //1表示已穿着
					return true;
				}
			}
		}
		return false;
	}
}
