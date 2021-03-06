package com.hc.logic.creature;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.achieve.PlayerAchieves;
import com.hc.logic.achieve.PlayerTasks;
import com.hc.logic.base.Profession;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.BagService;
import com.hc.logic.basicService.GoodsService;
import com.hc.logic.chat.Email;
import com.hc.logic.config.LevelConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.copys.Copys;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.dao.impl.UpdateTask;
import com.hc.logic.deal.Deal;
import com.hc.logic.domain.AchieveEntity;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.EmailEntity;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.TaskEntity;
import com.hc.logic.order.DoOrder;
import com.hc.logic.skill.SkillAttack;
import com.hc.logic.skill.SkillAttackMonst;
import com.hc.logic.skill.SkillAttackPlayer;
import com.hc.logic.union.Union;

/**
 * 玩家
 * 
 * @author hc
 *
 */
public class Player extends LiveCreature{

	//是否还活着	
	private boolean isAlive;
	
	private Session session;
	
	//技能id
	private List<Integer> skills = new ArrayList<>(); 
	//技能冷却时间, 在添加技能时添加。key：技能id，value：cd时间
	private Map<Integer, Date> cdSkill = new HashMap<>();
	//使用技能，有一段时间减少伤害。key:技能id，value：技能使用的时间；用来判断是否还有效
	private Map<Integer, Date> reduceAtt = new HashMap<>();
	//使用有持续攻击的技能。key：技能id， value：技能持续的终点
	private SkillAttack skillAttack;
	//使用有持续回血的技能。key：技能id，value：技能持续终点
	private Map<Integer, Long> continueRecover = new HashMap<>();
	
	//玩家实体
	private PlayerEntity playerEntity;
	
	//使用恢复类物品后，会在一定时间内持续恢复hp/mp。key：物品id，value：药品使用时间
	private Map<Integer, Date> recoverHpMp = new HashMap<>();
	
	//背包
	private BagService bagService;
	
	//商店页面
	private int pageNumber;
	//邮箱
	private Email email;
	//是否正在pk
	private boolean inPK = false;
	//发起pk时，对方玩家的名字
	private String pkTarget;
	//可以使用技能的时间（终时）毫秒
	private long canUSkill = 0;
	
	//副本组队的队友, 不包括自己。格式，key：玩家名；value：是否同意
	private Map<String, Boolean> teammate = new HashMap<>();
	//组队的发起者名，只要在组队中，就不是null
	private String sponserNmae;
	//交易
	private Deal deal;	
	
	//玩家任务
	private PlayerTasks playerTasks;
	private PlayerAchieves playerAchieve;
	
	private ConcurrentLinkedQueue<Runnable> orderQueue = new ConcurrentLinkedQueue<>();
	
	
	@Override
	public void setDescribe() {
		this.describe = "我是" + playerEntity.getName();
	}
	

	public Player() {
		
	}
	//在playerEntity中调用
	public Player(Session session, int[] skill, PlayerEntity pe) {
		this.session = session;
		this.playerEntity = pe;
		this.isAlive = true;
		for(int i = 0; i < skill.length; i++) {
			addSkill(skill[i]);
		}
		bagService = new BagService(this);
		email = new Email(pe.getEmails());
		if(playerEntity.getCopyEntity() != null)
			this.sponserNmae = playerEntity.getCopyEntity().getSponsor();
		this.playerTasks = new PlayerTasks(this);
		this.playerAchieve = new PlayerAchieves(this);
	}
	//注册专用	
	public Player(int id, int level, String name, String pass, int sceneId, int hp, int mp,
			      int exp, int[] skil, Session session, boolean isAlive, List<Equip> equips) {
		//playerEntity = new PlayerEntity();
		StringBuilder sb = new StringBuilder();
		for(int ii : skil) {
			sb.append(ii + ",");
		}
		this.skills.add(skil[0]);  //天生会技能0
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		playerEntity = new PlayerEntity(id, level, name, pass, sceneId, hp, mp, exp, sb.toString(), new ArrayList<>(), null);
		
		
		this.session = session;
		this.bagService = new BagService(this);
		this.isAlive = true;
		this.email = new Email(playerEntity.getEmails());
		this.playerTasks = new PlayerTasks();
		this.playerAchieve = new PlayerAchieves();
	}
	
	/**
	 * 被周期性的调用
	 */
	public void periodCall() {
		revoverPeriod();
		processOrder();		
	}
	
	public void revoverPeriod() {
		if(!isAlive) return;
		LevelConfig lc = Context.getLevelParse().getLevelConfigById(getLevel());
		int mhp = lc.getuHp();  //从配置文件中获得每秒增加的血量
		int mmp = lc.getuMp(); //从配置文件中获得每秒增加的法力
		
		//使用恢复类丹药后，在一段时间内恢复血量和蓝量，每个玩家都不同
		int[] recorHpMp = allRecover();  //返回长度为2的数组，第一个是恢复的血量，第二个是恢复的法力	 
		mhp += recorHpMp[0];
		mmp += recorHpMp[1];	
	    addHpMp(mhp, mmp);
		skillRecHp();   //技能导致的持续回血		
		skillContiAttack(); //技能的持续攻击效果

	}
	
	/**
	 * 添加需要处理的命令
	 * @param doOrder
	 */
	public void addOrder(Runnable doOrder) {
		orderQueue.add(doOrder);
	}
	
	/**
	 * 处理队列中的命令
	 */
	public void processOrder() {
		while(!orderQueue.isEmpty()) {
			System.out.println("玩家对象中处理命令");
			Runnable aOrder = orderQueue.poll();
			if(aOrder != null) aOrder.run();
		}
	}
	
	/**
	 * 玩家断线时，需要做的一些工作
	 */
	public void brakLine() {
		//断线时，自动认输
		if(inPK) pkFail();
		
	}
	
	/**
	 * 返回客户端玩家当前状态
	 */
	public void pState() {
		LevelConfig lc = Context.getLevelParse().getLevelConfigById(playerEntity.getLevel());
		
		StringBuilder sb = new StringBuilder();
		sb.append("是否还活着：" + isAlive + "\n");
		sb.append("职业：" + getProf().getTitle() + "\n");
		sb.append("等级：" + playerEntity.getLevel()+ "\n");
		sb.append("经验：" + playerEntity.getExp() + "/" + lc.getExpByProf(getProfIndex()) + "\n");
		sb.append("血量: " + playerEntity.getHp() + "/" + lc.getHpByProf(getProfIndex()) + "\n");
		sb.append("剩余法力: " + playerEntity.getMp() + "/" + lc.getMpByProf(getProfIndex()) + "\n");
		sb.append("金币：" + playerEntity.getGold() + "\n");
		sb.append("所在商店页面：" + getPageNumber());
		session.sendMessage(sb.toString());
	}
	
	
	/**
	 * 通过sceneID的获得scene
	 * 如果在副本中，则返回副本Copys
	 * @return
	 */
	public Scene getScene() {
		int sceId = playerEntity.getSceneId();
		System.out.println("sceId=0" + (sceId==0));
		if(sceId == 0) {
			return getCopys();
		}
		return Context.getWorld().getSceneById(sceId);
	}
	
	/**
	 * 获得玩家对应的副本
	 * @return
	 */
	public Copys getCopys() {
		if(playerEntity.getCopyEntity() == null) return null;
		int copeId = playerEntity.getCopyEntity().getCopyId();
		//发起者的id
		String pn = sponserNmae;
		if(sponserNmae == null && teammate.size() == 0) {
			pn = playerEntity.getName();
		}
		int sponsId = Context.getWorld().getPlayerEntityByName(pn).getId();
		System.out.println("copeId" + copeId);
		return Context.getWorld().getCopysByAPlayer(copeId, sponsId);
	}
	
	public String getName() {
		return playerEntity.getName();
	}

	public void setName(String name) {
		this.playerEntity.setName(name);
	}

	public int getLevel() {
		return playerEntity.getLevel();
	}

	public void setLevel(int level) {
		playerEntity.setLevel(level);
	}
	
	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
		if(isAlive == false) {
			palyerDead();
		}
	}
	
	public void palyerDead() {
		session.sendMessage("您已经死亡");
		if(isInPK() == true) {
			//死亡之后自动认输
			Context.getPkService().deadFailed(this);
		}
	}

	/**
	 * 现在能否使用技能进行攻击
	 * @return
	 */
	public boolean canUseSkill() {
		long cur = System.currentTimeMillis();
		if(cur > canUSkill) return true;
		return false;
	}	
	public long getCanUSkill() {
		return canUSkill;
	}
	/**
	 * 需要停止使用技能多长时间
	 * @param canUSkill （秒）
	 */
	public void setCanUSkill(int dizz) {
		long cur = System.currentTimeMillis();
		this.canUSkill = cur + dizz * 1000;
	}


	public int getSceneId() {
		return playerEntity.getSceneId();
	}


	public void setSceneId(int sceneId) {
		//this.sceneId = sceneId;
		playerEntity.setSceneId(sceneId);
	}


	public int getId() {
		return playerEntity.getId();
	}


	public void setId(int id) {
		playerEntity.setId(id);
		setcId();
	}
	
	
	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}


	public String getPassword() {
		return playerEntity.getPassword();
	}

	public void setPassword(String password) {
		playerEntity.setPassword(password);
	}
	
	
	
	public int getHp() {
		return playerEntity.getHp();
	}

	public void setHp(int hp) {
		//this.hp = hp;
		playerEntity.setHp(hp);
	}
	
	

	public int getMp() {
		return playerEntity.getMp();
	}

	public void setMp(int mp) {
		//this.mp = mp;
		playerEntity.setMp(mp);
	}
	public SkillAttack getSkillAttack() {
		return skillAttack;
	}
	public int getGold() {
		return playerEntity.getGold();
	}
	/**
	 * 增加金币
	 * @param amount 数量
	 */
	public void addGold(int amount) {
		playerEntity.setGold(playerEntity.getGold() + amount);
	}
	/**
	 * 减少金币。如果玩家拥有的金币不够，则返回false
	 * @param amount
	 * @return
	 */
	public boolean minusGold(int amount) {
		int diff = playerEntity.getGold() - amount;
		if(diff < 0) return false;
		playerEntity.setGold(diff);
		return true;
	}
	
	/**
	 * 增加(减少)玩家血量，法力
	 * 参数：shp是要增加的血量
	 *      smp是要增加的法力
	 */
	public void addHpMp(int shp, int smp) {
		//验证是否超过最大法力
		LevelConfig lc = Context.getLevelParse().getLevelConfigById(playerEntity.getLevel());
		int mHp = lc.getHpByProf(getProfIndex());
		int mMp = lc.getMpByProf(getProfIndex());
		shp += playerEntity.getHp();
		smp += playerEntity.getMp();
		if(shp > mHp) shp = mHp;
		if(smp > mMp) smp = mMp;
		
		//验证是否小于0
		if(shp < 0) {
			shp = 0;
			//玩家死亡
			setAlive(false);
		}
		if(smp < 0) smp = 0;
		
		setHp(shp);
		setMp(smp);
		//playerEntity.setMp(mp);
	}

	/**
	 * 学习技能
	 * @param sk 技能书id
	 * @return
	 */
	public boolean addSkillByBook(int sbk) {
		int sk = Context.getGoodsParse().getGoodsConfigById(sbk).getContinueT();
		if(!hasEnoughGoods(sbk, 1)) {
			session.sendMessage("没有相应的技能书，不能学习技能");
			return false;
		}
		if(hasSkill(sk)) {
			session.sendMessage("已经拥有这个技能，不能重复学习");
			return false;
		}
		boolean sec = addSkill(sk);
		if(sec) {
			delGoods(sbk, 1);
			return true;
		}
		return false;
	}
	private boolean addSkill(int sk) { //技能id
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(sk);
		if(skillConfig.getProfession() != getProfIndex() && skillConfig.getProfession() != 10) {
			session.sendMessage("当前职业学不会此技能");
			return false;
		}
		skills.add(sk);
		if(playerEntity != null) playerEntity.setSkills(skills);
		//添加技能时，就增加初始化cd时间
		cdSkill.put(sk, new Date());
		return true;
	}
	
	/**
	 * 判断玩家是否拥有这个技能
	 * @param skId 技能id
	 * @return
	 */
	public boolean hasSkill(int skId) {
		for(int ii : skills) {
			if(ii == skId) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> getSkills() {
		return skills;
	}

	
	
	public int getExp() {
		return playerEntity.getExp();
	}

	/**
	 * 增加玩家经验
	 */
	public void addExp(int a) {
		int exp = playerEntity.getExp() + a;
		LevelConfig levelConfig = Context.getLevelParse().getLevelConfigById(getLevel());
		if(levelConfig.getExpByProf(getProfIndex()) <= exp) {
			if(Context.getLevelParse().getLevelConfigById(getLevel()+1) == null) {
				playerEntity.setExp(levelConfig.getExpByProf(getProfIndex()));
				return;
			}
			setLevel(getLevel() + 1);
			playerEntity.setExp(exp - levelConfig.getExpByProf(getProfIndex()));
			return;
		}
		playerEntity.setExp(exp);
	}

	@Override
	public String toString() {
		return playerEntity.getName();
	}

	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	/**
	 * 初始化玩家实体，注册时调用
	 */
	public void initPlayerEntity() {
/**
		StringBuilder sb = new StringBuilder();
		for(int ii : skills) {
			sb.append(ii + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		//注意。除非是新建一个玩家账号，否则都不会新建一个playerEntity
		playerEntity = Context.getWorld().getPlayerEntityByName(name);
		if(playerEntity == null) {
			System.out.println("initPlayerEntity--------------");
			playerEntity = new PlayerEntity(id, level, name, password, sceneId, hp, mp, exp, sb.toString(), new ArrayList<>());
	*/	
	}
	

	
	
	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}


	@Override
	public void setcId() {
		this.cId = playerEntity.getId();
	}


	public Map<Integer, Date> getCdSkill() {
		return cdSkill;
	}

	public void setCdSkill(Map<Integer, Date> cdSkill) {
		this.cdSkill = cdSkill;
	}
	/**
	 * 根据技能id，更新技能cd
	 * @param sId
	 */
	public void updateCdById(int sId) {
		this.cdSkill.put(sId, new Date());
	}
	
	/**
	 * 通过技能id获得相应的cd时间
	 * @param sId
	 * @return
	 */
	public Date getCdTimeByid(int sId) {
		Date date = cdSkill.get(sId);
		if(date == null) {
			cdSkill.put(sId, new Date());
		}
		return cdSkill.get(sId);
	}
	
	
	public Map<Integer, Date> getReduceAtt() {
		return reduceAtt;
	}
	
	/**
	 * 根据攻击，减少玩家血量
	 * @param player  受到伤害的玩家
	 * @param hurt  受到的伤害
	 * @return 玩家实际减少的血量
	 */
	public int attackPlayerReduce(int hurt) {
		int redu = allReduce();
		hurt -= redu;
		if(hurt < 0) hurt = 0;   //防止护盾的保护大于受到的伤害
		addHpMp(-hurt, 0); //加个负号，就变成减了
		return hurt;
	}

	
	/**
	 * 添加减免伤害的技能, 对怪物用
	 * @param skiId
	 * @param monst 需要攻击的怪物
	 */
	public void addContinueAtttib(int skiId, Monster monst) {
		//System.out.println("------------player.addcontinueattib----" + skiId +", " + monst.toString());
		SkillConfig sc = Context.getSkillParse().getSkillConfigById(skiId);
		if(sc.getContinueT() == 0) return;
		if(sc.getAttack() > 0) { //持续攻击
			if(skillAttack ==null) this.skillAttack = new SkillAttackMonst();
			this.skillAttack.addContiAttack(monst, skiId);
		}
	}
	/**
	 * 添加减免伤害的技能, 对玩家用
	 * @param skiId
	 * @param player 需要攻击的玩家
	 */
	public void addContinueAttib(int skiId, Player player) {
		SkillConfig sc = Context.getSkillParse().getSkillConfigById(skiId);
		if(sc.getContinueT() == 0) return;
		if(sc.getAttack() > 0) { //持续攻击
			if(skillAttack ==null) this.skillAttack = new SkillAttackPlayer();
			this.skillAttack.addContiAttack(player, skiId);
		}
	}
	/**
	 * 添加恢复队友和自己血量的技能持续效果
	 * 添加防御的技能的持续效果
	 * @param skiId
	 * @param sc
	 */
	public void addContinueRecov(int skiId) {
		SkillConfig sc = Context.getSkillParse().getSkillConfigById(skiId);
		long termi = System.currentTimeMillis() + sc.getContinueT() * 1000;
		if(sc.getProtect() > 0) this.reduceAtt.put(skiId, new Date()); //持续防御
		if(sc.getCure() > 0) {  //持续补血
			if(playerEntity.getCopyEntity() != null && playerEntity.getCopyEntity().getPlayers() != null) {
				for(PlayerEntity pe : playerEntity.getCopyEntity().getPlayers()) {
					//只有活着的队友需要恢复生命值
					Context.getOnlinPlayer().getPlayerById(pe.getId()).getContinueRecover().put(skiId, termi);
				}
			}else {
				this.continueRecover.put(skiId, termi);
			}			
		}
		//System.out.println("--------addRecover--------时间为" + System.currentTimeMillis() +", " + continueRecover.get(new Integer(skiId)));
	}
	
	/**
	 * 使用技能导致的回血
	 * 在scene中被调用
	 */
	public void skillRecHp() {
		List<Integer> delRevo = new ArrayList<>();
		for(Entry<Integer, Long> rec : continueRecover.entrySet()) {
			SkillConfig skic = Context.getSkillParse().getSkillConfigById(rec.getKey());
			if(System.currentTimeMillis() < rec.getValue()) {
				addHpMp(skic.getCure(), 0);
			}else {
				delRevo.add(rec.getKey());
			}
		}
		deltimeoutRevo(delRevo);
	}
	private void deltimeoutRevo(List<Integer> timeouts) {
		for(int i : timeouts) {
			continueRecover.remove(new Integer(i));
		}
	}
	/**
	 * 技能持续进行攻击
	 */
	public void skillContiAttack() {
		if(skillAttack != null) {
			skillAttack.doContiAttack(this);
		}
	}
	
	/**
	 * 计算所有技能带来的减伤效果
	 * 并且删除过期了的技能
	 */
	public int skillReduce() {
		List<Integer> deleRed = new ArrayList<>(); //记录过期了的技能持续效果
		int allRe = 0;
		for( Entry<Integer, Date> ent : reduceAtt.entrySet()) {
			int sId = ent.getKey();
			Date d = ent.getValue();
			
			long dual = Context.getSkillParse().getSkillConfigById(sId).getContinueT() * 1000;
			long pTime = d.getTime();
			long nTime = new Date().getTime();
			//验证技能是否过期
			if((nTime - pTime) > dual) {
				deleRed.add(sId);  
				continue;
			}
			allRe += Context.getSkillParse().getSkillConfigById(sId).getProtect();
		}
		//在返回前，删除过期了的技能持续效果
		for(int i : deleRed) {
			reduceAtt.remove(i);
		}
		return allRe;
	}
	/**
	 * 计算总的伤害减少
	 * 应该包括技能，装备等等带来的总的减伤。
	 * @return
	 */
	public int allReduce() {
		int allRed = 0;
		allRed += skillReduce();  //所有技能带来的减伤
		System.out.println("---------技能减伤----" + allRed);
		allRed += equipReduce();  //所有装备带来的减伤
		System.out.println("---------装备减伤----" + allRed);
		
		return allRed;
	}
	
	/**
	 * 玩家穿上装备
	 * 当在背包中有很多相同的物品时，通过物品id不能唯一确定是哪一个物品
	 * 这里，默认穿上第一个此id的物品
	 * @param eId: 物品id
	 */
	public void addEquip(int eId) {
		//只有物品的typeId为2，才是装备；同一装备不能装备两次
		//System.out.println(equips.toString() + " cont " + equips.contains(eId));
		int tId = Context.getGoodsParse().getGoodsConfigById(eId).getTypeId();
		if( tId != 2 && tId != 3) {
			session.sendMessage("该物品不是装备");
			return;
		}
		if(hasEquiped(eId)) {
			session.sendMessage("不能重复装备");
			return;
		}
		//将物品id从背包中删除, 默认一次只装备一件
		boolean cont = getBagService().getGoods(eId, 1);
		if(!cont) {
			session.sendMessage("没有该物品");
		    return;
		}

		//playerEntity.getEquips().add(equ);
		//更新goodlEntity中的背包物品字段
		//playerEntity.getOrtherEquipsById(eId).setState(1); //1表示已装备
		Context.getGoodsService().doEquip(eId, playerEntity);
		
		session.sendMessage("穿着完毕");		
	}
	/**
	 * 查看这种类型的装备是否已穿着.现在只是对物品id判断，导致若有多种剑，也能装备
	 * @param eId 物品id
	 * @return
	 */
	public boolean hasEquiped(int eId) {
		return new GoodsService().isEquiped(eId, playerEntity);
	}
	/**
	 * 卸下装备
	 * @param eId
	 */
	public void deletEquip(int eId) {
		if(!hasEquiped(eId)) {
			session.sendMessage("没有穿着该装备");
			return;
		}
		
		//将装备卸下, 默认一次只卸下一件
		getBagService().insertBag(eId, 1);
		

		Context.getGoodsService().deEquip(eId, playerEntity);
		
		session.sendMessage("卸下装备");
	}
	/**
	 * 通过物品id，来判断玩家是否拥有该装备/武器
	 * @param gid
	 * @return
	 */
	public boolean contEquip(int gid) {
		return hasEquiped(gid);
		//return playerEntity.getEquipById(gid) != null;
		//return this.equips.contains(gid);
	}
	/**
	 * 计算玩家所有已穿着装备的总防御
	 * @return
	 */
	public int equipReduce() {
		int alRe = 0;
	    for(GoodsEntity e : playerEntity.getGoods()) {
	    	int tId = Context.getGoodsParse().getGoodsConfigById(e.geteId()).getTypeId();
	    	if(tId != 2 && tId != 3) continue;
	    	Equip eq = (Equip)e;
	    	if(eq.getState() == 0) continue;  //0表示未穿着。则不加防御
	    	int ii = eq.geteId();
	    	//判断并减少防御性装备的耐久度, 也就是所有增加防御的装备都要减少耐久度
	    	if(Context.getGoodsParse().getGoodsConfigById(ii).getProtectByPfog(getProfIndex()) > 0) {
		    	if(eq.getDuraion() < 1) {
		    		continue;
		    	}else {
		    		eq.setDuraion(eq.getDuraion() - 1);
		    	}
	    	}
	    	
	    	alRe += Context.getGoodsParse().getGoodsConfigById(ii).getProtectByPfog(getProfIndex());
	    	System.out.println("------------减伤-000---"+alRe);
	    }
	    return alRe;
	}
	/**
	 * 计算玩家所有已穿着装备的总攻击力
	 * @return
	 */
	public int equipAttack() {
		int eAtt = 0;
	    for(GoodsEntity ge : playerEntity.getGoods()) {
	    	int tId = Context.getGoodsParse().getGoodsConfigById(ge.geteId()).getTypeId();
	    	if(tId != 2 && tId != 3) continue;
	    	Equip e = (Equip)ge;
			if(e.getState() == 0) continue;
			int ii = e.geteId();
			eAtt += Context.getGoodsParse().getGoodsConfigById(ii).getAttackByProf(getProfIndex());
		}
		return eAtt;
	}
	/**
	 * 计算玩家的总的攻击力
	 * 包括等级对于攻击力，所用技能对于攻击力，所穿装备对于攻击力
	 * @param skiId 技能id
	 * @return
	 */
	public int AllAttack(int skiId) {
		int allAt = 0;
		//等级对于攻击
		allAt += Context.getLevelParse().getLevelConfigById(getLevel()).getAttackByProf(getProfIndex());
		System.out.println("等级攻击： " + allAt);
		//技能对于攻击
		allAt += Context.getSkillParse().getSkillConfigById(skiId).getAttack();
		System.out.println("技能攻击： " + allAt);
		//所穿装备对应攻击
		allAt += equipAttack();
		System.out.println("装备攻击： " + allAt);
		
		return allAt;
	}
	

	/**
	 * 增加物品
	 * @param gId 物品id
	 * @param amount 数量
	 */
	public boolean addGoods(int gId, int amount) {
		//暂时在这里将所有物品都放进背包中
		Map<Integer, Integer> map = new HashMap<>();
		map.put(gId, amount);
		//System.out.println("addGoods " + map.size() + "to " + map.toString());
		boolean inserted = bagService.insertBag(map); //显示		
		if(!inserted) return false;
		GoodsService gs = Context.getGoodsService();
		for(int i = 0; i < amount; i++) {
			gs.addGoods(playerEntity, gId);
		}
		return true;
	}
	/**
	 * 添加物品
	 * @param ge
	 * @return
	 */
	public boolean addGoods(GoodsEntity ge) {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(ge.geteId(), 1);
		boolean inserted = bagService.insertBag(map); //显示		
		if(!inserted) return false;
		playerEntity.getGoods().add(ge);
		return true;
	}

	/**
	 * 删除物品
	 * @param gId
	 * @param amount
	 * @return: 要删掉的值
	 */
	public List<GoodsEntity> delGoods(int gId, int amount) {
		List<GoodsEntity> dg = new ArrayList<>();
		int remail = 0;
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge.geteId() == gId) {
				remail++;
			}
		}
		if(remail < amount) {
			return dg;
		}
		boolean hasDel = bagService.getGoods(gId, amount);
		System.out.println("背包中剩余物品是否足够：" + hasDel);
		if(!hasDel) return dg;
		GoodsService gs = Context.getGoodsService();
		for(int i = 0; i < amount; i++) {
			if(gs.delGoods(playerEntity, gId) == null) return dg;
			dg.add(gs.delGoods(playerEntity, gId));
		}		
		return dg;		
	}
	
	/**
	 * 验证是否有这么多物品
	 * @param gId
	 * @param amount
	 * @return
	 */
	public boolean hasEnoughGoods(int gId, int amount) {
		int remail = 0;
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge.geteId() == gId) {
				remail++;
			}
		}
		if(remail < amount) {
			return false;
		}
		return true;
	}
	
	public BagService getBagService() {
		return bagService;
	}
	
	/**
	 * 使用恢复类药品
	 * @param gId  物品id
	 */
	public boolean addRecoverHpMp(int gId) {  
		//验证是否属于恢复类物品
		int typeId = Context.getGoodsParse().getGoodsConfigById(gId).getTypeId();
		if(typeId != 1) return false;
		//验证此物品数量是否足够, 并且删除
		if(delGoods(gId, 1).size() < 1) return false;
		this.recoverHpMp.put(gId, new Date());
		return true;
	}
	public Map<Integer, Date> getRecoverHpMp(){
		return this.recoverHpMp;
	}
	/**
	 * 获得此时hp/mp总的恢复量, 使用恢复类药品的效果持续
	 * @return
	 */
	public int[] allRecover() {
		int allHp = 0;
		int allMp = 0;
		List<Integer> deleRed = new ArrayList<>(); //记录过期了的药品持续效果
		for( Entry<Integer, Date> ent : recoverHpMp.entrySet()) {
			int gId = ent.getKey();
			Date d = ent.getValue();
			
			long dual = Context.getGoodsParse().getGoodsConfigById(gId).getContinueT() * 1000;
			long pTime = d.getTime();
			long nTime = new Date().getTime();
			//System.out.println("------------------player.allrecover90------" + dual + ", " + (nTime-pTime));
			//验证药品是否过期
			if((nTime - pTime) > dual) {
				System.out.println("--------过期了药品" + gId + ", " + nTime + ", " + pTime + ", " + dual);
				deleRed.add(gId);  
				continue;
			}
			allHp += Context.getGoodsParse().getGoodsConfigById(gId).getHp();
			allMp += Context.getGoodsParse().getGoodsConfigById(gId).getMp();
		}
		//在返回前，删除过期了的技能持续效果
		for(int i : deleRed) {
			recoverHpMp.remove(new Integer(i));
		}
		return new int[] {allHp, allMp};
	}

	/**
	 * 通过装备/武器id，获得相应的剩余耐久度
	 * 单纯通过物品id并不能唯一确定一件物品，但是现在只有一件同类型的物品，
	 * 从而只有一件被穿上，所以可以通过这个来确定是哪个物品
	 * @param eId 物品id
	 * @return
	 */
	public int restContiT(int eId) {
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == eId && hasEquiped(eId)) {
					return eq.getDuraion();
				}
			}
		}
		return 0;
	}
	/**
	 * 通过装备/武器id，减少相应的耐久度，默认是1
	 * @param wId 物品id
	 */
	public void minusContT(int wId) {
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == wId && hasEquiped(wId)) {
					int res = eq.getDuraion();
					res -= 1;
					if(res < 0) res = 0;
					eq.setDuraion(res);
					return;
				}
			}
		}

	}
	
	/**
	 * 以map的形式返回玩家所有未穿着的物品和装备
	 * key:  物品id；value：数量
	 * @return
	 */
	public Map<Integer, Integer> getAllOrtherEq(){
		Map<Integer, Integer> map = new HashMap<>();
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.getState() == 1) continue;
			}
			map.put(ge.geteId(), map.getOrDefault(ge.geteId(), 0) + 1);
		}
		return map;
	}
	

	public CopyEntity getCopEntity() {
		return playerEntity.getCopyEntity();
	}

	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * 当断线时，自动认输
	 */
	public void pkFail() {
		Context.getPkService().giveUp(session);
	}
	public boolean isInPK() {
		return inPK;
	}
	public void setInPK(boolean inPK) {
		this.inPK = inPK;
	}

	public String getPkTarget() {
		return pkTarget;
	}
	public void setPkTarget(String pkTarget) {
		this.pkTarget = pkTarget;
	}


	public List<String> getTeammate() {
		Set<String> ts = teammate.keySet();
		System.out.println("玩家队友人数： " + ts.size());
		return new ArrayList<>(ts);
	}
	public boolean teamContain(String pname) {
		for(String ss : teammate.keySet()) {
			if(ss.equals(pname)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 取消组队
	 */
	public void clearTeammate() {
		this.teammate.clear();
	}

	/**
	 * 通过玩家名来确定是否是要邀请的玩家
	 * @param pname
	 * @return
	 */
	public boolean contPlaName(String pname) {
		System.out.println("--------------player的team是---" + teammate + ", pname=" + pname);
		for(String n : teammate.keySet()) {
			if(n.equals(pname))
				return true;
		}
		return false;
	}
	
	public Map<Integer, Long> getContinueRecover() {
		return continueRecover;
	}
	/**
	 * 玩家同意组队
	 * @param pname 同意组队的玩家的名字
	 */
	public void acTeam(String pname) {
		teammate.put(pname, true);
	}
	/**
	 * 是否完成组队。发起者只有在所有玩家都同意之后才能进入副本。
	 * @return
	 */
	public boolean goupComplete() {
		if(teammate.size() < 1) return false;
		for(boolean bb : teammate.values()) {
			if(!bb)
				return false;
		}
		return true;
	}
	public void addTeammate(List<Player> ps) {
		for(Player p : ps) {
			teammate.put(p.getName(), false);
		}
	}
	//public boolean isInParty() {
	//	System.out.println("isInParty------------" + inParty);
	//	return inParty;
	//}
	//public void setInParty(boolean inParty) {
	//	this.inParty = inParty;
	//}
	public String getSponserNmae() {
		return sponserNmae;
	}
	public void setSponserNmae(String sponserNmae) {
		this.sponserNmae = sponserNmae;
	}
	/**
	 * 是否选了职业
	 * @return
	 */
	public boolean haveProf() {
		return !(playerEntity.getProfession() == -1);
	}
	/**
	 * 得到职业对于的枚举对象
	 * @return
	 */
	public Profession getProf() {
		if(!haveProf()) return null;
		return Profession.getProfByIndex(playerEntity.getProfession());
	}
	/**
	 * 设置职业
	 * @param index > -1
	 */
	public void setProf(int index) {
		playerEntity.setProfession(index-1);
	}
	/**
	 * 职业对应的位置
	 * @return
	 */
	public int getProfIndex() {
		return playerEntity.getProfession();
	}
	/**
	 * 初始化交易
	 * @param tPlayer
	 */
	public void beginDeal(String tPlayer) {
		this.deal = new Deal(tPlayer);
	}
	/**
	 * 同意进行交易
	 * @param deal
	 */
	public void accDeal(Deal deal) {
		this.deal = deal;
	}
	public Deal getDeal() {
		return deal;
	}
	public void stopDeal() {
		this.deal = null;
	}
	/**
	 * 获得玩家所在的工会名
	 * 若工会已经被解散，则设置玩家所在工会为null，也就是没有加入工会
	 * @return
	 */
	public String getUnionName() {
		String uname = playerEntity.getUnionName();
		if(Context.getWorld().getUnionEntityByName(uname) == null) {
			playerEntity.setUnionName(null);
		}
		return playerEntity.getUnionName();
	}
	/**
	 * 加入工会
	 * @param name
	 */
	public void enterUnion(String name, int tit) {
		playerEntity.setUnionName(name);
		playerEntity.setUnionTitle(tit);
	}
	public Union getUnion() {
		String uname = getUnionName();
		if(uname == null) return null;
		return Context.getWorld().getUnionEntityByName(uname).getUnion();
	}
	public void delUnion() {
		playerEntity.setUnionName(null);
		playerEntity.setUnionTitle(-1);
	}
	/**
	 * 获得自己在工会中的职位
	 * @return
	 */
	public int getUnionTitle() {
		return playerEntity.getUnionTitle();
	}
	public AchieveEntity getAchieveEntity() {
		return playerEntity.getAchieveEntity();
	}
	
	public PlayerTasks getPlayerTasks() {
		return playerTasks;
	}
	public PlayerAchieves getPlayerAchieves() {
		return playerAchieve;
	}
	public TaskEntity getTaskEntity() {
		return playerEntity.getTaskEntity();
	}
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Player oP = (Player)o;
		return oP.getName().equals(playerEntity.getName());
	}
}
