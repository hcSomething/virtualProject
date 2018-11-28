package com.hc.logic.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

/**
 * 被召唤师召唤的boss
 * @author hc
 *
 */
public class SummonBoss implements Runnable{

	//boss id
	private int bid;
	private Monster summBoss;  //代表这只
	//boss技能对应的攻击力。key：技能id，value：攻击力
	private Map<Integer, Integer> skill2attack = new HashMap<>();
	//boss技能cd。key：技能id， value：上次使用时间
	private Map<Integer, Long> skillsCD = new HashMap<>();
	//该boss对应的场景中的所有可攻击敌人
	//private List<Player> players = new ArrayList<>();
	private List<Monster> monsters = new ArrayList<>();
	//针对单个玩家的有持续效果的技能。key：怪物，value：( key:技能id, 时间终点 )。
	//而对多个玩家有持续性效果的技能，也可以利用这个，只不过要每个玩家都设置一些
	private Map<Monster, Map<Integer, Long>> attEnemy = new HashMap<>();
	//是否主动攻击
	private boolean attackNow = false;
	//召唤这个boss的召唤师
	private Player player;
	//存在的时间
	private long terminate;
	private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
	
	
	/**
	 * 
	 * @param bid boss的id
	 * @param players
	 */
	public SummonBoss(Monster summboss, List<Monster> monsters, Player pp) {
        this.player = pp;
		this.bid = summboss.getMonstId();
		this.summBoss = summboss;
		this.monsters = new ArrayList<>(monsters);
		init();
		this.terminate = System.currentTimeMillis() + 13*1000;  //默认存在7秒
		System.out.println("summonboss初始化" + terminate + ", 当前时间" + System.currentTimeMillis());
		System.out.println("召唤物可攻击列表：" + monsters.toString());
	}
	
	private void init() {
		MonstConfig monstConfig = Context.getSceneParse().getMonsters().getMonstConfgById(bid);
		attackNow = (monstConfig.getAttackP() == 0 ? false : true);
		for(int i : monstConfig.getSkills()) {
			SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(i);
			skill2attack.put(i, skillConfig.getAttack());
		}
	}
	
	
	@Override
	public void run() {
		while(!tasks.isEmpty()) {
			try {
				tasks.take().run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("boss线程, bid=" + bid + ", 时间 " + System.currentTimeMillis());
		//进行攻击
		bossAttack();
		//具有持续性效果的技能，减少相应玩家血量
		reduceHp();
		if(System.currentTimeMillis() > terminate || summBoss.attack(1) == -1) {
			complete();
		}
	}
	
	public void bossAttack() {
		int skillId = nextSkill();
		//构造出每两次技能攻击时间不相等的效果
		if(!skill2attack.containsKey(new Integer(skillId))) return;
		//验证是否cd完
		if(!isCdOut(skillId)) return;
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		int conti = skillConfig.getContinueT()*1000;
		System.out.println("------------bossAttackPlayer---------continue=" + conti + " 技能 " + skillConfig.getName());
		if(skillConfig.getScope() != 0) {
			//此技能只针对一个玩家
			if(nextAttackPlayer() < 0) return;
			Monster monster = monsters.get(nextAttackPlayer());
			//眩晕
			//dizzi(monster, skillId);
			if(conti < 1) instantAttack(monster, skillId);
			else  continueAttack(monster, skillId);	
			//broadcastAttack(monster, skillId);  //播放技能造成的伤害
		}else {
			//针对所有玩家
			if(conti < 1) {
				instantAllAttack(skillId);
			}else {
				continueAllAttack(skillId);
			}
			//broadcastAttack(players.get(0), skillId);  //播放技能造成的伤害
		}
		
	}
		
	/**
	 * 验证技能是否cd完。若cd完，返回true，并更新cd时间
	 * @param skillConfig
	 * @return
	 */
	public boolean isCdOut(int skillId) {
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		if(!skillsCD.containsKey(new Integer(skillId))) {
			skillsCD.put(skillId, System.currentTimeMillis());
			return true;
		}
		long past = skillsCD.get(new Integer(skillId));
		long nowT = System.currentTimeMillis();
		long dual = skillConfig.getCd() * 1000;
		if((nowT - past) > dual) {
			skillsCD.put(skillId,  System.currentTimeMillis());
			return true;
		}
		//System.out.println("--------技能cd时间------------" + skillsCD.toString());
		return false;
	}
	
	/**
	 * 具有持续性效果的技能给玩家带来持续掉血
	 * 并删除已经过期了的技能持续效果
	 */
	public void reduceHp() {
		Map<Monster, Integer> monster2skiId = new HashMap<>();
		for(Entry<Monster, Map<Integer, Long>> enti : attEnemy.entrySet()) {
			Monster m = enti.getKey();
			for(Entry<Integer, Long> ent : enti.getValue().entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				if(terminal < System.currentTimeMillis()) {
					monster2skiId.put(m, skillId);  //记录已经过期了的技能效果
					continue;
				}
				int attack = skill2attack.get(new Integer(skillId));
				
				int attacked = m.attack(attack);
				broadCastMessage(m, attacked, attack);
			}	
		}
		delStaleDated(monster2skiId);
	}
	/**
	 * 删除已经过期了的技能效果
	 * @param p2s
	 */
	private void delStaleDated(Map<Monster, Integer> m2s) {
		for(Entry<Monster, Integer> enti : m2s.entrySet()) {
			attEnemy.get(enti.getKey()).remove(new Integer(enti.getValue()));
		}
	}
	
	/**
	 * 添加有持续效果的技能
	 * @param player
	 * @param skillId 技能id
	 */
	private void continueAttack(Monster monster, int skillId) {
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attEnemy.containsKey(monster)) attEnemy.put(monster, new HashMap<Integer, Long>());
		if(!attEnemy.get(monster).containsKey(new Integer(skillId))) {
			attEnemy.get(monster).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//上一个技能的持续时间还没有结束，直接延长持续时间
			long term = attEnemy.get(monster).get(new Integer(skillId)) + terminate;
			attEnemy.get(monster).put(skillId, term);
		}
		//System.out.println("---------------continueAttack----------- " + attPlayer.toString());
	}
	/**
	 * 有持续效果的群攻技能
	 * @param skillId
	 */
	private void continueAllAttack(int skillId) {
		for(Monster m : monsters) {
			//if(!p.isAlive()) continue;
			continueAttack(m, skillId);
		}
	}
	
	
	/**
	 * 瞬时技能
	 * @param player 被攻击的玩家
	 * @param skillId 技能id
	 */
	private void instantAttack(Monster monster, int skillId) {
		int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
		//player.addHpMp(-attack, 0);
		int attacked = monster.attack(attack);
		broadCastMessage(monster, attacked, attack);
	}
	
	private void broadCastMessage(Monster monster, int attacked, int reduHp) {
		String summonbossName = Context.getSceneParse().getMonsters().getMonstConfgById(bid).getName();
		String mesg = "";
		if(attacked==1) {
			//攻击成功，并杀死怪物
			mesg = "召唤师[" + player.getName() + "]召唤的[" + summonbossName + "]，将怪物[" +
					monster.getName() + "]击杀";
			//击杀怪物/boss获得相应奖励
			Context.getAwardService().obtainAward(player, monster);
		}
		if(attacked == 0) {
			//攻击成功，但没被杀死
			mesg = "召唤师[" + player.getName() + "]召唤的[" + summonbossName + "]，对怪物[" +
					monster.getName() + "]造成[" + reduHp + "]点伤害, 剩余血量[" + monster.getHp()
					+ "]";
		}
		if(attacked == -1) {
			//怪物被别人击杀
			mesg = "召唤师[" + player.getName() + "]召唤的[" + summonbossName + "]，对怪物[" +
					monster.getName() + "进行攻击失败，怪物已死亡";
		}
		broadcastMesg(mesg);
		if(attacked != 0) {
			tasks.add(new Runnable() {
				public void run() {
					delEnermys(monster.getMonstId());
				}
			});
			player.getScene().deleteAttackMonst(monster);
		}
	}
	
	private void broadcastMesg(String mesg) {
		if(player.getCopEntity() != null && player.getCopEntity().getPlayers().size() > 1) {
			BroadcastService.broadInScene(player.getSession(), mesg);
		}else {
			player.getSession().sendMessage(mesg);
		}
	}
	
	private void dizzi(Player player, int skillId) {
		int dizziness = Context.getSkillParse().getSkillConfigById(skillId).getDizziness();
		System.out.println("----------------------更新眩晕时间----------------------" + skillId + ", " + dizziness);
		if(dizziness != 0) player.setCanUSkill(dizziness);
	}
	/**
	 * 瞬时群攻技能
	 * @param skillId
	 */
	private void instantAllAttack(int skillId) {
		for(Monster m: monsters) {
			//if(!p.isAlive()) continue;
			instantAttack(m, skillId);
		}
	}
	
	/**
	 * 选出一个活着的玩家承受攻击
	 * @return -1代表全部玩家已经死亡
	 */
	public int nextAttackPlayer() {
		Random random = new Random();
		int nextone = random.nextInt(monsters.size());
		//去除死亡的玩家
		if(!monsters.get(nextone).isAlive())
			return firstNotDead(nextone);
		return nextone;
	}
	private int firstNotDead(int index) {
		int bound = index + monsters.size();
		for(int i = index; i < bound; i++) {
			i = ((i >= monsters.size()) ? (i - monsters.size()) : i);
			if(monsters.get(i).isAlive()) return i;
		}
		return -1;   //表示玩家队伍全部阵亡
	}
	
	/**
	 * boss选择下一个技能，也会返回不是技能的任意数字
	 * @return
	 */
	public int nextSkill() {
		Random random = new Random();
		int nextone = random.nextInt(skill2attack.size()+6) + 100;  //这个100 要用常数
		return nextone;
	}
	
	
	

	public int getId() {
		return bid;
	}

	public void setId(int id) {
		this.bid = id;
	}
	
	
	public boolean isAttackNow() {
		return attackNow;
	}

	public void setAttackNow(boolean attackNow) {
		this.attackNow = attackNow;
	}

	/**
	 * 删除boss攻击列表
	 * @param pid
	 */
	public void delEnermys(int pid) {
		//System.out.println("----------boss.delplayers, 前" + players.toString());
		for(Monster mm : monsters) {
			if(mm.getMonstId() == pid) {
				monsters.remove(mm);
				attEnemy.remove(mm);
				System.out.println("--------删除死亡的怪物----" + monsters.toString());
				return;
			}
		}
	}
	

	@Override
	public String toString() {
		return "boss{id=" + bid
	          + ", name="+ Context.getCopysParse().getCopysConfById(bid).getName()
	          + ", players.size()=" + monsters.size()
	          + " }";
	}

	/**
	 * 关闭线程
	 * 超时，或死亡
	 */
	public void complete() {
		System.out.println("---------超时退出-----------" + terminate + ", 当前"+ System.currentTimeMillis());
		Context.getWorld().delSummonsThread(player.getId(), bid);
	}
}
