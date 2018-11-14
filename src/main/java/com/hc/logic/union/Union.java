package com.hc.logic.union;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.BagService;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;

/**
 * 工会
 * @author hc
 *
 */
public class Union {

	private Lock lock = new ReentrantLock();
	private UnionEntity unionEntity;
	private BagService bagService;
	//申请加入公会的玩家名
	private List<String> candidate = new ArrayList<>();
	//本工会活跃的玩家
	private Set<Player> activePlayers = new HashSet<>();
	
	public Union(UnionEntity ue) {
		this.unionEntity = ue;
		int size = Context.getUnionParse().getUCByid(unionEntity.getGrade()).getWarehouse();
		bagService = new BagService(initUnionWarehouse(), size);
	}
	
	private Map<Integer, Integer> initUnionWarehouse(){
		Map<Integer, Integer> map = new HashMap<>();
		for(GoodsEntity ge : unionEntity.getGoods()) {
			map.put(ge.geteId(), map.getOrDefault(ge.geteId(), 0) + 1);
		}
		return map;
	}
	
	/**
	 * 解散工会
	 * @param uname
	 * @param pname
	 * @return
	 */
	public boolean dissolveUnion(String uname, String pname) {
		lock.lock();
		try {
			if(!unionEntity.getOriginator().equals(pname)) return false;
			Context.getWorld().delUnionEntity(uname);
			new PlayerDaoImpl().delete(unionEntity);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 申请加入工会
	 * @param uname
	 * @return
	 */
	public boolean enterUion(String uname, Player player) {
		lock.lock();
		try {
			if(unionEntity == null) {
				return false;
			}
			if(unionEntity.getPnum() == Context.getUnionParse().getUCByid(unionEntity.getGrade()).getNum()) {
				return false;
			}
			//unionEntity.getCandidate().add(player.getPlayerEntity());
			candidate.add(player.getName());
			addUnionExp(2);
			updateUnion();
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 退出工会
	 * @param player
	 * @return
	 */
	public boolean quitUnion(Player player) {
		lock.lock();
		try{
			if(player.getName().equals(unionEntity.getOriginator())) {
				changeOriginate();
			}
			player.delUnion();
			int num = unionEntity.getPnum() - 1;
			if(num == 0) return false;
			if(player.getName().equals(unionEntity.getOriginator())) return false;
			unionEntity.setPnum(num);
			this.activePlayers.add(player);
			updateUnion();
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 同意加入工会
	 * @param player  同意者
	 * @param pname   申请者的名字
	 * @return
	 */
	public boolean agreeEnter(Player player, String pname) {
		lock.lock();
		try {
			PlayerEntity tpe = Context.getWorld().getPlayerEntityByName(pname);
			System.out.println("----允许加入工会-1-" + (tpe==null));
			if(tpe == null) return false;
			System.out.println("----允许加入工会-2-" + (tpe.getUnionName() !=null));
			if(tpe.getUnionName() != null) return false;
			if(unionEntity.getPnum() >= Context.getUnionParse().getUCByid(unionEntity.getGrade()).getNum()) {
				System.out.println("----允许加入工会-3-" + unionEntity.getPnum() +", "
						+ "--" + Context.getUnionParse().getUCByid(unionEntity.getGrade()).getNum() );
				return false;
			}
			if(!candidate.contains(pname))  return false;
			tpe.setUnionName(unionEntity.getName());
			tpe.setUnionTitle(1);
			unionEntity.setPnum(unionEntity.getPnum() + 1);
			//unionEntity.delCandidate(pname);
			this.activePlayers.add(player);
			addUnionExp(3);
			updateUnion();
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 拒绝某玩家加入工会
	 * @param player
	 * @param pname
	 * @return
	 */
	public boolean rejectEnter(Player player, String pname) {
		lock.lock();
		try {
			if(!candidate.contains(pname)) return false;
			candidate.remove(pname);
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 职位提升
	 * @param player
	 * @param pname
	 * @return
	 */
	public boolean titleUp(Player player, String pname, int d) {
		lock.lock();
		try {
			PlayerEntity pe = Context.getWorld().getPlayerEntityByName(pname);
			if(pe == null || pe.getUnionName() == null) return false;
			if(pe.getUnionTitle() >= player.getUnionTitle()) return false;
			if(pe.getUnionTitle() == 1 && d == -1) return false; 
			pe.setUnionTitle(pe.getUnionTitle() + d);
			addUnionExp(5);
			updateUnion();
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 工会成员捐赠物品和金币
	 * @param player
	 * @param gname : 物品名或gold
	 * @param amount ： 捐赠的数量
	 * @return
	 */
	public boolean donateGoods(Player player, String gname, int amount) {
		lock.lock();
		try {
			if(gname.equals("gold")) {
				if(!player.minusGold(amount)) return false;
				unionEntity.setGold(unionEntity.getGold() + amount);
				return true;
			}
			int gid = Integer.parseInt(gname);	
			System.out.println("------捐赠----1--" + player.hasEnoughGoods(gid, amount));
			if(!player.hasEnoughGoods(gid, amount)) return false; //表示玩家没有这么多物品
			Map<Integer, Integer> map = new HashMap<>();
			map.put(gid, amount);
			boolean inserted = bagService.insertBag(map); 
			System.out.println("------捐赠---2---" + inserted);
			if(!inserted) return false; //表示仓库装不下
			
			List<GoodsEntity> glist = player.delGoods(gid, amount);
			for(GoodsEntity ge : glist) {  //交换物品(玩家和工会之间)
				GoodsEntity nge = Context.getGoodsService().changeGoods(ge);
				nge.setUnionEntity(unionEntity);
				unionEntity.getGoods().add(nge);
			}
			addUnionExp(10);
			updateUnion();
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 从工会中获得物品
	 * 默认：当取的是金币时，自动 * 10
	 * @param player
	 * @param gname
	 * @param amount
	 * @return
	 */
	public boolean obtainGoods(Player player, String gname, int amount) {
		lock.lock();
		try {
			if(gname.equals("gold")) {
				int am = amount * 10;
				if(unionEntity.getGold() < am) return false;
				unionEntity.setGold(unionEntity.getGold() - am);
				player.addGold(am);
				return true;
			}
			int gid = Integer.parseInt(gname);	
			if(goodsNum(gid) < amount) return false;  //表示仓库中没有这么多物品
			
			Map<Integer, Integer> map = new HashMap<>();
			map.put(gid, amount);
			//System.out.println("addGoods " + map.size() + "to " + map.toString());
			boolean inserted = bagService.insertBag(map); //显示		
			if(!inserted) return false;     //表示玩家背包中没有那么多容量
			
			for(int i = 0; i < amount; i++) {
				GoodsEntity nge = Context.getGoodsService().changeGoods(unionEntity.delGoods(gid));
				nge.setPlayerEntity(player.getPlayerEntity());
				player.addGoods(nge);
			}
			updateUnion();
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 公会状态
	 * @param session
	 */
	public void unionState(Session session) {
		this.activePlayers.add(session.getPlayer());
		StringBuilder sb = new StringBuilder();
		sb.append("欢迎来到公会：\n");
		sb.append("当前活跃的成员有: \n");
		lock.lock();
		try {
			for(Player pp : activePlayers) {
				String tit = Context.getTitlParse().getTCByid(pp.getUnionTitle()).getName();
				sb.append("    " + pp.getName() + " " + tit + "\n");
			}
			sb.append("公会当前等级：" + Context.getUnionParse().getUCByid(unionEntity.getGrade()).getName() + "\n");
			sb.append("公会仓库中的物品有：\n");
			sb.append("金币: " + unionEntity.getGold() + "\n");
			sb.append(bagService.bagGoodsdis());
			session.sendMessage(sb.toString());
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 当会长退出公会时
	 * 可以指定最高等级的成员为会长
	 * 但如何没有成员，则解散公会
	 */
	private void changeOriginate() {
		Player player = null;
		int g = 0;
		for(Player pp : activePlayers) {
			if(pp.getUnionTitle() > g) {
				player = pp;
				g = pp.getUnionTitle();
			}
		}
		if(player != null) {
			player.getPlayerEntity().setUnionTitle(4);
			unionEntity.setOriginator(player.getName());
		}else {
			dissolveUnion(unionEntity.getName(), unionEntity.getOriginator());
		}
	}
	 
	
	public int goodsNum(int gid) {
		int si = 0;
		for(GoodsEntity ge : unionEntity.getGoods()) {
			if(ge.geteId() == gid) {
				si += 1;
			}
		}
		return si;
	}
	
	//进行更新
	private void updateUnion() {
		System.out.println("union进行更新");
		new PlayerDaoImpl().update(unionEntity);
	}

	public List<String> getCandidate() {
		return candidate;
	}

	/**
	 * 增加公会经验或升级
	 * @param am
	 */
	private void addUnionExp(int am) {
		int allexp = unionEntity.getExp();
		allexp += am;
		if(allexp >= Context.getUnionParse().getUCByid(unionEntity.getGrade()).getExp()) {
			int gradeu = unionEntity.getGrade() + 1;
			if(gradeu > 4) gradeu = 4;
			allexp -= Context.getUnionParse().getUCByid(unionEntity.getGrade()).getExp();
			unionEntity.setGrade(gradeu);
		}
		unionEntity.setExp(allexp);
	}

	public UnionEntity getUnionEntity() {
		return unionEntity;
	}

	public Set<Player> getActivePlayers() {
		return activePlayers;
	}

	
	
}
