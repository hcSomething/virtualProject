package com.hc.logic.basicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * 背包服务
 * 每个玩家都不一样，因此只能从玩家那里获得背包信息
 * 显示所有没有穿着的物品
 * @author hc
 *
 */
public class BagService {

	//背包默认大小
	private int size = 20;
	//背包，存储的是物品的id。按物品类型id排序，id小的排在前面；map中，key: 物品id，value: 物品数量
	private Map<Integer, Integer>[] bags = new HashMap[size];
	//默认每个格子可叠加同一物品的数量
	//private int superposition = 2;
	private Player p;
	
	
	//id为玩家id, 从数据库中得到物品信息，并放入bag中显示
	public BagService(Player pe) {
		this.p = pe;
		//System.out.println("bagservide------------: " + (pe==null) + " && " );
		if(pe != null) {
			//这里，key：物品的物品 id，value：数量
			Map<Integer, Integer> maop = pe.getAllOrtherEq();
			insertBag(maop);
		}
	}
	
	/**
	 * 用作工会仓库。每个工会一个
	 * @param gid2amount： key：物品的物品 id，value：数量
	 * @param size： 仓库大小
	 */
	public BagService(Map<Integer, Integer> gid2amount, int size) {
		this.size = size;
		this.bags = new HashMap[size];
		insertBag(gid2amount);
	}
	
	/**
	 * 返回客户端背包状态
	 * @param session
	 */
	public void dispBag(Session session) {
		StringBuilder sb = new StringBuilder();
		sb.append("背包中有：- - - - - - - - - - - - - - - - - - - - \n");
		//System.out.println("bag: " + bags[0].toString());
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) break;  //当某个格子为null，则后面的格子都应该为null
			int goodId = bags[i].keySet().iterator().next();
			String name = Context.getGoodsParse().getGoodsConfigById(goodId).getName();
			sb.append(name + ", 数量：" + bags[i].get(goodId) + "\n");
		}
		sb.append("- - - - - - - -  - - - - - - - - - -  - - - - - - - - - - - - - - - - - -\n");
		session.sendMessage(sb.toString());
		
		//测试用
		//getGoods(1, 7);
		//System.out.println("***************************88888*************");
		//test(bags);
	}
	
	public String bagGoodsdis() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) break;  //当某个格子为null，则后面的格子都应该为null
			int goodId = bags[i].keySet().iterator().next();
			String name = Context.getGoodsParse().getGoodsConfigById(goodId).getName();
			sb.append(name + ", 数量：" + bags[i].get(goodId) + "\n");
		}
		return sb.toString();
	}
	
	
	/**
	 * 从背包中取出一定数量的某个物品
	 * 
	 * 当某个格子中的物品都取出去了，那么就将这个格子设为null
	 * @param gid
	 * @param amount
	 * @return 背包中有这个物品，并且数量足够时，返回true，否则返回false
	 */
	public boolean getGoods(int gid, int amount) {
		//System.out.println("getGoods&$$$$$$$$$$$" + gid + ", " );
		int pos = 10000;
		//对应的Equip的id
		//int equId = -1;
		//验证背包中是否有这个物品
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) return false;  //没有这个物品
			if(bags[i].keySet().iterator().next() == gid) {
				pos = i;
				break;
			}
		}
		
		if(pos >= bags.length) return false;  //没有这个物品
		//验证数量是否够
		int all = 0;
		for(int i = pos; i < bags.length; i ++) {
			if(bags[i] == null) break;
			if(bags[i].keySet().iterator().next() != gid) break;
			all += bags[i].get(gid);
			if(all >= amount) break;  
		}
		if(all < amount) return false; //数量不够
		
		//System.out.println("************************* pos " + pos + " amount " + amount);
		
		//从背包中删除给定数量的物品
		for(int i = pos; i < bags.length; i ++) {
			
			if(amount <= 0) break;
			
			boolean dif = (amount - bags[i].get(gid)) >= 0;
			//System.out.println("i " + i + " amount " + amount + " dif " + dif);
			
			if(dif )  {   //当前格子中的物品数量不够
				amount -= bags[i].get(gid);
				bags[i] = null;
			}else {      //当前格子中的物品数量足够
				bags[i].put(gid, bags[i].get(gid) - amount);
				amount -= bags[i].get(gid);
			}
			//System.out.println("i " + i + " amount " + amount);
			
		}
		//System.out.println("**************88前------- " );
		//test(bags);
		//取出物品后，要对背包进行调整
		adjustBag( gid, pos);
		
		return true;
		
	}
	
	/**
	 * 对背包进行调整
	 * 消除中间的空格子，使连续多个相同物品格子，左端满，右端不满
	 * @param gid 需要调整的物品的id
	 * @param pos 需要调整的物品的位置
	 */
	private void adjustBag( int gid, int pos) {
		//消除空格子
		remEmpty(pos);
		//整理连续多个相同物品格子
		int rPos = leftFull(gid, pos);

		if(bags[rPos] == null) {
			remEmpty(rPos);
		}else if((rPos + 1 < size) && bags[rPos+1] == null) {
			remEmpty(rPos+1);
		}
		
	}
	
	/**
	 * 按照规则，连续多个防止相同物品的格子，最左侧要满，右侧可以不满
	 * 用adjustBag方法
	 * @param gid
	 * @param pos 需要调整的物品的位置
	 * @return
	 */
	private int leftFull(int gid, int pos) {
		int rPos = pos;  //装此物品的最右侧的格子
		int superposition = Context.getGoodsParse().getGoodsConfigById(gid).getSuperposi();
		for(int i = pos+1; i < bags.length; i++) {
			if(bags[i] == null) break;
			if(bags[i].keySet().iterator().next() != gid) break;
			rPos = i;
		}
		if(rPos == pos) return rPos; //只有一个格子装有该物品

		//if(need == 0) return;  //最左侧是满的
		while(rPos > pos) {
			int lm = bags[pos].get(gid);
			int need = superposition - lm;
			int rm = bags[rPos].get(gid);		
			if(need >= rm) { //还没有填满最左侧
				bags[pos].put(gid, lm + rm);
				bags[rPos] = null;
			}else {
				bags[pos].put(gid, superposition);
				bags[rPos].put(gid, rm - need );
				break;
			}
			need -= rm;
			rPos--;
		}
		
		return rPos;

	}
	
	/**
	 * 消除空格子
	 * @param pos 需要消除空格子的开始位置
	 */
	private void remEmpty(int pos) {
		int isNull = -1;
		for(int i = pos; i < bags.length; i++) {
			if(bags[i] != null) break;
			isNull = i;
		}
		if(isNull == (bags.length - 1)) isNull = -1; //也就是说pos后面全是null，也就不需要消除空格子了
		if(isNull != -1) {  //有空格子
			setForward(isNull + 1, (isNull - pos + 1) );
		}

	}
	

	/**
	 * 将一系列物品放入背包  (很多物品，一定的数量)
	 * @param goods key:物品id， value：物品数量
	 */
	public boolean insertBag(Map<Integer, Integer> goods) {
		System.out.println("insertBag: " + goods.toString());
		for(Entry<Integer, Integer> ent : goods.entrySet()) {
			boolean a = insertBag(ent.getKey(), ent.getValue());
			if(!a) return false;
		}
		return true;
	}
	
	
	/**
	 * 将物品放入背包，(一种物品，一定的数量)
	 * @param gid 物品的id
	 * @param amount 物品的数量
	 * @param superposition 可叠加数量
	 */
	public boolean insertBag(int gid, int amount) {
		//System.out.println("单个insertBag " );
		int superposition = Context.getGoodsParse().getGoodsConfigById(gid).getSuperposi();
		int hasInsert = 0;  //记录已经插入了多少个物品，方便当背包满了回滚
		while(true) {
			if(amount <= 0) break;
			int insertAm = amount;
			if(amount > superposition) {
				insertAm = superposition;
			}
			amount -= superposition;
			int posi = firstEmpty();
			System.out.println("posi-------------: " + posi);
			if(posi == -1) {
				p.getSession().sendMessage("背包容量不够");
				getGoods(gid, hasInsert);  //回滚操作，
				return false;
			}
			bags[posi] = new HashMap<>();
			bags[posi].put(gid, insertAm);

			//System.out.println("-----前*****************8");
			//test(bags);
			doSort(gid, posi);
		   // test(bags);
			//System.out.println("-----后************8*****");
			hasInsert++;
		}
		return true;
	}
	
	/**
	 * 对背包进行排序
	 * @param pos 新加入需要排序的物品的放入的背包格子位置
	 *        gid : 需要排序的物品id
	 */
	private void doSort(int gid, int pos) {
		//System.out.println("doSort***gid: " + gid +" pos "+ pos );
		int amount = bags[pos].get(gid);
		int type = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId(); //获得物品的类型id
		int insertPo = getInsetPos(gid, type);  //要插入的位置
		//将要插入位置即之后的元素，都向后移一位
		setBack(pos, insertPo);
		bags[insertPo] = new HashMap<>();
		bags[insertPo].put(gid, amount);
		
		//System.out.println("((((((&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + gid + " insertPo " + insertPo);
		//test(bags);
		
		//进行左侧满，右侧可不满调整
		adjustBag(gid, insertPo);
		//System.out.println("doSort**---- " + bags[0].toString() );
	}
	
	/**
	 * 将从begin到end之间的数据(包括begin)完后退一步
	 * @param begin
	 * @param end
	 */
	private void setBack(int end, int begin) {
		//System.out.println("begin " + begin + " end " + end);
		for(int i = end; i > begin; i--) {
			//System.out.println("i " + i);
			bags[i] = bags[i-1];
			bags[i-1] = null;
		}

	}
	
	/**
	 * 将从begin往后的数据(包括begin)往前进step步
	 * 不允许需要移动的数据中有null
	 * @param begin
	 * @param step 需要前进的格数
	 */
	private void setForward(int begin, int step) {
		for(int i = begin; i < bags.length; i++) {
			if(bags[i] == null) break;
			bags[i-step] = bags[i];
			bags[i] = null;
		}
	}
	
	/**
	 * 获得插入的位置
	 * @param typeId 要插入物品的类型id
	 *        gid   要插入物品的id
	 * @return
	 */
	private int getInsetPos(int gid, int typeId) {
		//System.out.println("getInsetPos " + typeId );
		int insertPo = 10000;
		for(int i = 0; i < bags.length; i++) {
			int iid = bags[i].keySet().iterator().next(); //位置i上的物品的id
			int gTy = Context.getGoodsParse().getGoodsConfigById(iid).getTypeId(); //位置i上的物品的类型id
			//对于类型id相同的，再增加验证物品名称的。从而：若插入的物品是一个新物品，则会被放在同类型id的最后
			//                                          若插入的物品背包里有，则会被放在同种物品之前。
			if(gTy == typeId) {
				String insertName = Context.getGoodsParse().getGoodsConfigById(gid).getName();
				String name = Context.getGoodsParse().getGoodsConfigById(iid).getName();
				if(insertName.equals(name)) {
					insertPo = i;
					break;
				}
				continue;
			}
			if(gTy > typeId) {  //寻找第一个类型id大于要插入的物品的类型id
				insertPo = i;
				break;
			}
		}
		//System.out.println("getInsetPos***** " + insertPo );
		return insertPo;

	}
	
	/**
	 * 寻找第一个空格子
	 * @return -1: 代表满了
	 */
	private int firstEmpty() {
		for(int i=0; i<bags.length; i++) {
			if(bags[i] == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 测试用
	 * @param map
	 */
	public void test(Map<Integer, Integer>[] map) {
		StringBuilder sb = new StringBuilder();
		sb.append("背包中有：- - - - - - - - - - - - - - - - - - - - \n");
		for(int i = 0; i < map.length; i++) {
			if(map[i] == null) {
				sb.append("null" + "\n");
				continue;  //当某个格子为null，则后面的格子都应该为null
			}
			int goodId = map[i].keySet().iterator().next();
			String name = Context.getGoodsParse().getGoodsConfigById(goodId).getName();
			sb.append(name + ", 数量：" + map[i].get(goodId) + "\n");
		}
		sb.append("- - - - - - - -  - - - - - - - - - -  - - - - - - - - - - - -\n");
		System.out.println(sb.toString());

	}
	
	/**
	 * 把背包的内容，转化为数据库中存储的形式
	 * @return
	 */
	private String retBag(){
		StringBuilder sb = new StringBuilder();
		if(bags == null) return sb.toString();
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) break;
			int gid = bags[i].keySet().iterator().next();
			int amo = bags[i].get(new Integer(gid));
			sb.append(gid + ":" + amo + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
}
