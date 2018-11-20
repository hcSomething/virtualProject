package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.hc.frame.Context;
import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Player;

/**
 * boss, 代表一个boss, 被周期性的调用。
 * @author hc
 *
 */
public class Boss implements Runnable{

	//boss id
	private int bid;
	//boss技能对应的攻击力。key：技能id，value：攻击力
	private Map<Integer, Integer> skill2attack = new HashMap<>();
	//boss技能cd。key：技能id， value：上次使用时间
	private Map<Integer, Long> skillsCD = new HashMap<>();
	//该boss对应的场景中的所有可攻击玩家
	private List<Player> players = new ArrayList<>();
	//针对单个玩家的有持续效果的技能。key：玩家，value：( key:技能id, 时间终点 )。
	//而对多个玩家有持续性效果的技能，也可以利用这个，只不过要每个玩家都设置一些
	private Map<Player, Map<Integer, Long>> attPlayer = new HashMap<>();
	//是否主动攻击玩家
	private boolean attackNow = false;
	
	
	
	/**
	 * 
	 * @param bid boss的id
	 * @param players
	 */
	public Boss(int bid, List<Player> players) {
		this.bid = bid;
		this.players = new ArrayList<>(players);
		init();
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
		//System.out.println("boss线程, bid=" + bid + ", 时间 " + System.currentTimeMillis());
		//攻击玩家
		bossAttackPlayer();
		//具有持续性效果的技能，减少相应玩家血量
		reduceHp();
	}
	
	public void bossAttackPlayer() {
		if(!attackNow) return;  //如果是被动攻击的boss，attackNow要在玩家攻击了boss时设置	
		int skillId = nextSkill();
		//构造出每两次技能攻击时间不相等的效果
		if(!skill2attack.containsKey(new Integer(skillId))) return;
		//验证是否cd完
		if(!isCdOut(skillId)) return;
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		int conti = skillConfig.getContinueT()*1000;
		//System.out.println("------------bossAttackPlayer---------continue=" + conti + " 技能 " + skillConfig.getName());
		if(skillConfig.getScope() != 0) {
			//此技能只针对一个玩家
			if(nextAttackPlayer() < 0) return;
			Player player = players.get(nextAttackPlayer());
			//眩晕
			dizzi(player, skillId);
			if(conti < 1) instantAttack(player, skillId);
			else  continueAttack(player, skillId);	
			broadcastAttack(player, skillId);  //播放技能造成的伤害
		}else {
			//针对所有玩家
			if(conti < 1) {
				instantAllAttack(skillId);
			}else {
				continueAllAttack(skillId);
			}
			broadcastAttack(players.get(0), skillId);  //播放技能造成的伤害
		}
		
	}
	
	/**
	 * 播放boss释放的技能，以及造成的伤害
	 * @param player
	 * @param skillId
	 */
	public void broadcastAttack(Player player, int skillId) {
		MonstConfig bossConfig = Context.getSceneParse().getMonsters().getMonstConfgById(bid);
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		String allPlayer = ((skillConfig.getScope() == 0) ? "所有玩家" : ("玩家"+player.getName())); 
		String hert = (skillConfig.getContinueT() < 1 ? (skillConfig.getAttack()+"点") : ("每秒"+skillConfig.getAttack()+"点"));
		StringBuilder sb = new StringBuilder();
		sb.append("boss " + bossConfig.getName());
		sb.append(" 使用技能  " + skillConfig.getName());
		sb.append(" 对 " + allPlayer + " 造成 ");
		sb.append( hert + " 的伤害！");
		//System.out.println("-----------------------broadcast " + sb.toString());
		BroadcastService.broadToPlayer(players, sb.toString());
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
		//System.out.println("-&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&reduceHp---");
		Map<Player, Integer> player2skiId = new HashMap<>();
		for(Entry<Player, Map<Integer, Long>> enti : attPlayer.entrySet()) {
			Player p = enti.getKey();
			for(Entry<Integer, Long> ent : enti.getValue().entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				if(terminal < System.currentTimeMillis()) {
					player2skiId.put(p, skillId);  //记录已经过期了的技能效果
					continue;
				}
				int attack = skill2attack.get(new Integer(skillId));
				//玩家可以减少伤害的buff，比如护盾
				int redu = p.allReduce();
				attack -= redu;
				if(attack < 0) attack = 0;   //防止护盾的保护大于受到的伤害				
				p.addHpMp(-attack, 0); //加个负号，就变成减了
				//System.out.println("boss的持续技能对你造成伤害， 减少血量" + attack);
				p.getSession().sendMessage("boss的持续技能对你造成伤害， 减少血量" + attack);
			}	
		}
		delStaleDated(player2skiId);
	}
	/**
	 * 删除已经过期了的技能效果
	 * @param p2s
	 */
	private void delStaleDated(Map<Player, Integer> p2s) {
		for(Entry<Player, Integer> enti : p2s.entrySet()) {
			attPlayer.get(enti.getKey()).remove(new Integer(enti.getValue()));
		}
	}
	
	/**
	 * 添加有持续效果的技能
	 * @param player
	 * @param skillId 技能id
	 */
	private void continueAttack(Player player, int skillId) {
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attPlayer.containsKey(player)) attPlayer.put(player, new HashMap<Integer, Long>());
		if(!attPlayer.get(player).containsKey(new Integer(skillId))) {
			attPlayer.get(player).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//上一个技能的持续时间还没有结束，直接延长持续时间
			long term = attPlayer.get(player).get(new Integer(skillId)) + terminate;
			attPlayer.get(player).put(skillId, term);
		}
		//System.out.println("---------------continueAttack----------- " + attPlayer.toString());
	}
	/**
	 * 有持续效果的群攻技能
	 * @param skillId
	 */
	private void continueAllAttack(int skillId) {
		for(Player p : players) {
			if(!p.isAlive()) continue;
			continueAttack(p, skillId);
		}
	}
	
	
	/**
	 * 瞬时技能
	 * @param player 被攻击的玩家
	 * @param skillId 技能id
	 */
	private void instantAttack(Player player, int skillId) {
		int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
		player.addHpMp(-attack, 0);
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
		for(Player p : players) {
			if(!p.isAlive()) continue;
			instantAttack(p, skillId);
		}
	}
	
	/**
	 * 选出一个活着的玩家承受攻击
	 * @return -1代表全部玩家已经死亡
	 */
	public int nextAttackPlayer() {
		Random random = new Random();
		int nextone = random.nextInt(players.size());
		//去除死亡的玩家
		if(!players.get(nextone).isAlive())
			return firstNotDead(nextone);
		return nextone;
	}
	private int firstNotDead(int index) {
		int bound = index + players.size();
		for(int i = index; i < bound; i++) {
			i = ((i >= players.size()) ? (i - players.size()) : i);
			if(players.get(i).isAlive()) return i;
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
	 * 增加玩家攻击列表
	 * @param p
	 */
	public void addPlayer(Player p) {
		 players.add(p);
	}

	/**
	 * 删除boss攻击列表
	 * @param pid
	 */
	public void delPlayers(int pid) {
		//System.out.println("----------boss.delplayers, 前" + players.toString());
		for(Player pp : players) {
			if(pp.getId() == pid) {
				players.remove(pp);
				return;
			}
		}
	}

	@Override
	public String toString() {
		return "boss{id=" + bid
	          + ", name="+ Context.getCopysParse().getCopysConfById(bid).getName()
	          + ", players.size()=" + players.size()
	          + " }";
	}
	
}
