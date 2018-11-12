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

	private String tpName;  //������һ��������
	private String spName;  //����������
	//����˫�����׵���Ʒ����������ʽ�� key����Ʒid/gold ; value: ����
	private Map<String, Integer> tpDeal = new HashMap<>();
	private Map<String, Integer> spDeal = new HashMap<>();  //������ϣ�����׵���Ʒ������
	private boolean tpAccep;   //�Ƿ�ͬ����н���
	private boolean spAccep;
	
	//��Ҫ��������Ʒ
	private List<GoodsEntity> exchange;

	public Deal(String p) {
		this.tpName = p;
	}
	
	/**
	 * ��˫����ͬ�⽻���󣬿�ʼ������Ʒ
	 */
	public void exchangeGoods() {
		System.out.println("-----��ʼ����-----" + tpDeal.toString() +", " + spDeal.toString());
		System.out.println("---����˫��-----" + tpName + ", " + spName);
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tpName);
			Player sPlayer = Context.getOnlinPlayer().getPlayerByName(spName);
			playerDelGoods(tPlayer, tpDeal, exchange);
			playerAddGoods(tPlayer, spDeal);
			playerAddGoods(sPlayer, tpDeal, exchange);
			playerDelGoods(sPlayer, spDeal, exchange);
			notHopeDeal(tpName);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 
	 * @param player ������Ʒ�����
	 * @param goods  ���ٵ���Ʒ
	 * @param changex ������ٵ�����Ʒ�������������棬���򱣳�null
	 */
	private void playerDelGoods(Player player, Map<String, Integer> goods,  List<GoodsEntity> changex) {
		for(Entry<String, Integer> ent : goods.entrySet()) {
			if(!OrderVerifyService.isDigit(ent.getKey())) { //���
				player.minusGold(ent.getValue());
			}else {  //��Ʒ
			    int gid = Integer.parseInt(ent.getKey());
			    this.exchange = new ArrayList<>(player.delGoods(gid, ent.getValue()));
			}
		}
		if(exchange != null) System.out.println("-----------exchange.tostrp----" + exchange.toString());
	}
	/**
	 * ��ý�������Ʒ
	 * @param player ��õ����
	 * @param goods  ��õ���Ʒ
	 * @param changex  �����õ�����Ʒ�����������棬�������Ϊnull
	 */
	private void playerAddGoods(Player player, Map<String, Integer> goods, List<GoodsEntity> exchanges) {
		if(exchange == null) {
			player.addGold(goods.get("gold"));
			return;
		}
		for(GoodsEntity ge : exchanges) {
			player.addGoods(ge);
		}
		exchange = null;
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
	 * ���뽻��״̬�󣬿��Է�����Ʒ
	 * @param pName : ������Ʒ�������
	 * @param args: deal ��Ʒ�� ����
	 */
	public void showGoods(String pName, String[] args) {
		Lock lock = new ReentrantLock();
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
	 * ��ý��׶Է�������
	 * @param name
	 * @return
	 */
	public String getCounter(String name) {
		if(name.equals(spName)) return tpName;
		return spName;
	}

	/**
	 * ͬ����н���
	 * @param pName ͬ�⽻���������
	 */
	public void hopeDeal(String pName) {
		Lock lock = new ReentrantLock();
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
	 * ��ͬ�⽻��
	 * @param pName
	 */
	public void notHopeDeal(String pName) {
		Lock lock = new ReentrantLock();
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
	 * �Ƿ��Ѿ����Կ�ʼ���н���ȷ��
	 * @return
	 */
	public boolean isReadyVerify() {
		Lock lock = new ReentrantLock();
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
	 * �Ƿ���Կ�ʼ������
	 * @return
	 */
	public boolean isReadyChange() {
		Lock lock = new ReentrantLock();
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