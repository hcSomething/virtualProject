package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;

@Component
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
			ge = new GoodsEntity(gid, pe, null);		
		}else {//添加装备
			ge = new Equip(gid, cont, pe, null);
		}
		pe.getGoods().add(ge);
	}
	
	/**
	 * 删除物品
	 * @param pe
	 * @param gid 物品id
	 */
	public GoodsEntity delGoods(PlayerEntity pe, int gid) {
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid) {
				if(delGood(ge, pe)) return ge;
			}
		}
		return null;
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
	
	/**
	 * 返回所有已穿着的装备
	 * @param pe
	 * @return
	 */
	public String allEquips(PlayerEntity pe) {
		StringBuilder sb = new StringBuilder();
		sb.append("身上的装备：- - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge instanceof Equip) {
				Equip equip = (Equip)ge;
				if(equip.getState() != 1) continue;
				int gid = equip.geteId();
				String name = Context.getGoodsParse().getGoodsConfigById(gid).getName();
				int dua = equip.getDuraion();
				sb.append(name + " 剩余耐久度：" + dua + "\n");
			}
		}
		sb.append("- - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
		return sb.toString();
	}
	
	/**
	 * 判断是否有这个物品，数量是否够
	 * @param gName: 物品id，或者是"gold"
	 * @param amount
	 * @return
	 */
	public boolean goodsEnough(PlayerEntity pe, String gName, int amount) {
		if(!OrderVerifyService.isDigit(gName)) {
			return pe.getGold() >= amount;
		}
		int gid = Context.getGoodsParse().getGoodsConfigById(Integer.parseInt(gName)).getId();
		int numb = 0;
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid)
				numb++;
		}
		return numb >= amount;
	}
	
	/**
	 * 交换物品
	 * @param ge
	 */
	public GoodsEntity changeGoods(GoodsEntity ge) {
		GoodsEntity res = null;
		if(ge instanceof Equip){
			Equip eq = (Equip)ge;
			res = new Equip(eq.geteId(), eq.getDuraion(), null, null);
			return res;
		}
		res = new GoodsEntity(ge.geteId(), null, null);
		return res;
	}
	
	
	
}
