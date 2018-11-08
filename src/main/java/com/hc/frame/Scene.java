package com.hc.frame;
import java.util.*;
import java.util.Map.Entry;

import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.base.Session;
import com.hc.logic.config.LevelConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.creature.*;

public class Scene extends TaskConsume{

	//这个场景的描述
	protected String describe;
	//场景id
	protected int id;
	//
	protected String name;
	//当前场景内的所有npc, 不包括玩家
	protected List<LiveCreature> creatures = new ArrayList<>();
	//所有怪物
	protected List<Monster> monsters = new ArrayList<>();
	//当前场景内的所有玩家
	protected List<Player> players = new ArrayList<>();
	//当前场景的所有传送阵,列表中存放的是目标场景id的集合
	protected List<String> teleports = new ArrayList<>();
	//当前场景，所有的传送阵id
	protected List<Integer> telepIds = new ArrayList<>();
	
	//当前场景，被攻击玩家列表: key:怪物id, value:每个怪物可以攻击的玩家
	protected Map<Integer, List<Player>> attackPlayers = new HashMap<>();
	

	public Scene() {
		exe(5, "scene"+id); //启动一个周期性调度器，周期20秒
	}
	
	public Scene(int interval) {
		System.out.println("scene的构造方法" + interval);
	}
	
	
	//这个方法会被自动周期性调用
    @Override
    public void execute() {
    	//玩家每秒恢复的血量和法力
    	recoverHpMp();
    	//怪物攻击
    	attackPlayer();
    	
    }
		
	/**
	 * 每秒恢复血量、法力
	 */
	public void recoverHpMp() {
		//LevelConfig lc = Context.getLevelParse().getLevelConfigById(level);
		for(Player p : players) {
			LevelConfig lc = Context.getLevelParse().getLevelConfigById(p.getLevel());
			int mhp = lc.getuHp();  //从配置文件中获得每秒增加的血量
			int mmp = lc.getuMp(); //从配置文件中获得每秒增加的法力
			//p.addHpMp(mhp, mmp);
			
			//使用恢复类丹药后，在一段时间内恢复血量和蓝量，每个玩家都不同
			int[] recorHpMp = p.allRecover();  //返回长度为2的数组，第一个是恢复的血量，第二个是恢复的法力

			mhp += recorHpMp[0];
			mmp += recorHpMp[1];
			
			p.addHpMp(mhp, mmp);
		}
	}

	/**
	 * 攻击玩家, 
	 * 其实就是减少玩家的血量
	 * 每个怪物，每次只能选一个玩家进行攻击，这里选择第一个攻击它的玩家
	 */
	public void attackPlayer() {
		for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
			int mId = enti.getKey();
			List<Player> attackP = enti.getValue();
			if(attackP.isEmpty()) return;
			Player pp = attackP.get(0); //每次只攻击第一个攻击它的玩家
			int dHp = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getAttack();
			
			//玩家可以减少伤害的buff，比如护盾
			pp.attackPlayerReduce(dHp);
			
			String name = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getName();
			pp.getSession().sendMessage("正在被 " + name + " 攻击，减少血量：" + dHp);

		}

	}
	
	
	//场景中能加入玩家，就要删除玩家。
	public void deletePlayer(Player player) {
		this.players.remove(player);
	}
	
	/**
	 * 根据用户名获得玩家
	 */
	public Player getPlayerByName(String name) {
		for(Player p : players) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	
	/**
	 * 增加可以被某个怪物攻击的玩家
	 * 加入之后，就一只会遭受攻击，只有在怪物死亡、玩家离开当前场景，才不会被攻击
	 * @param mId : 怪物id 
	 * @param p ： 怪物可以攻击的玩家
	 */
	public void addAttackPlayer(int mId, Player p) {
		if(!attackPlayers.containsKey(mId)) {
			attackPlayers.put(mId, new ArrayList<Player>());
		}
		//不重复添加。一个怪物列表中，不能有重复的玩家
		if(attackPlayers.get(mId).contains(p)) return;
		attackPlayers.get(mId).add(p);
	}
	/**
	 * 当一个玩家去到别的场景时，怪物就攻击不到，从怪物的攻击列表中删除
	 * @param p
	 */
	public void deleteAttackPlayer(Player p) {
		System.out.println(" 当一个玩家去到别的场景时，怪物就攻击不到，从怪物的攻击列表中删除");
		//boolean find = false;
		for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
			int mId = enti.getKey();
			List<Player> attackP = enti.getValue();
			for(int j = 0; j < attackP.size(); j++) {
				if(attackP.get(j).getName().equals(p.getName())) {
					attackP.remove(p);
				}
				if(attackP.isEmpty()) {
					attackPlayers.remove(mId);
				}
				break;
			}
		}
		
	}
	/**
	 * 当怪物被击杀，就不能攻击玩家了，需要删除
	 * @param mId
	 */
	public void deleteAttackMonst() {
		System.out.println("================这里被击杀了");
		attackPlayers.clear();
	}


	/**
	 * 判断当前场景是否有这个传送阵id
	 * @param id
	 * @return
	 */
	public boolean hasTelepId(int id) {
		for(int ii : telepIds) {
			if(ii == id) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 所有当前场景可以传送到的场景
	 * @return
	 */
	public String allTransportableScene() {
		return teleports.toString();
	}
	
	/**
	 * 当前场景是否存在这个怪物
	 * @param mid 怪物id
	 */
	public boolean hasMonst(int mId) {
		for(LiveCreature ii : creatures) {
			if(ii.getcId() == mId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 返回客户端当前场景的所有信息
	 * @param session
	 */
	public void allThing(Session session) {
		session.sendMessage("所有npc" + getCreatures() + "");
		session.sendMessage("所有怪物" + getMonsters() + "\n");
		session.sendMessage("所有玩家" + getPlayers() + "\n");
		//更改了传送的方式
		session.sendMessage("所有可传送目标：" + allTransportableScene());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//************get,set方法**************
	
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<LiveCreature> getCreatures() {
		return creatures;
	}

	public void addCreatures(LiveCreature creature) {
		this.creatures.add(creature);
	}

	public void addMonste(Monster monst) {
		this.monsters.add(monst);
	}
	public Monster getMonsteById(int id) {
		for(Monster mon : monsters) {
			if(mon.getMonstId() == id) {
				return mon;
			}
		}
		return null;
	}
	public void initMonster() {
		SceneConfig sConfig = Context.getSceneParse().getSceneById(id);
		for(int i : sConfig.getMonsts()) {
			Monster monst = new Monster(i);
			addMonste(monst);
		}
	}
	public List<Monster> getMonsters() {
		if(monsters.size() == 0) initMonster();
		return monsters;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
	}
	
	public List<String> getTeleport() {
		return teleports;
	}

	public void addTeleport(String te) {
		teleports.add(te);
		String sid = te.substring(te.length()-2, te.length()-1);
		telepIds.add(Integer.parseInt(sid));
	}


	public String getName() {
		return name;
	}
	public void setName(String n) {
		this.name = n;
	}

	public List<Integer> getTelepIds() {
		return telepIds;
	}
	

}
