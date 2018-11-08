package com.hc.logic.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.frame.OnlinePlayer;
import com.hc.frame.Scene;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.config.TelepConfig;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.xmlParser.MonstParse;
import com.hc.logic.xmlParser.NpcParse;
import com.hc.logic.xmlParser.SceneParse;
import com.hc.logic.xmlParser.TelepParse;

/**
 * 世界
 * @author hc
 *
 */
@DependsOn(value="sceneParse")
@Component
public class World implements ApplicationContextAware{
  //
	//所有场景：（sceneId, scene），现在都是从配置文件中加载
	private  Map<Integer, Scene> sceneResource = new HashMap<>();
	//所有副本：(sceneId, (playerid, copys))。通过某个副本中的player的id可以唯一确定一个副本。
	private Map<Integer, Map<Integer, Copys>> allCopys = new HashMap<>();
	//所有注册的玩家，
	private List<Player> allRegisteredPlayer = new ArrayList<>();
	//所有玩家实体，在启动时，从数据库加载
	private List<PlayerEntity> allPlayerEntity = new ArrayList<>();
	//任务调度线程的标识。格式：key：copys+副本id+玩家id, value: 对应的future
	private ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();
	//所有副本实体
	private List<CopyEntity> copyEntitys = new ArrayList<>();
	
	ApplicationContext context;
	
	public World() {
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
	/**
	 * 初始化操作，在World bean构造时被调用
	 */
	@PostConstruct
	private void init() {
		//System.out.println("这里是world的init方法");
		String hql = "from PlayerEntity";
		allPlayerEntity = new PlayerDaoImpl().find(hql);
		
		//从数据库中加载玩家数据后，设置最大id
		int maxId = getMaxId();
		Context.setpID(maxId);
		configAllScene();
	}
	
	
	/**
	 * 初始化所有场景,即从配置文件中加载场景配置
	 * 加载的场景都缓存在Wolrd类的sceneResource字段中
	 * 加载场景配置文件时，也要加载场景中的怪物，npc和传送阵等实体。
	 * 
	 * bornPlace和VillageOfFrashman等硬编码场景可以删掉了
	 */
	public void configAllScene() {
	    SceneParse sceneP = context.getBean("sceneParse", SceneParse.class);
		List<SceneConfig> sParseList = sceneP.getAllSceneConfig();
		Scene scene = null;
		for(SceneConfig sConfig : sParseList) {
			scene = new Scene();
			scene.setId(sConfig.getSceneId());
			scene.setName(sConfig.getName());
			scene.setDescribe(sConfig.getDescription());
			//设置creature
			
			for(int i: sConfig.getNpcs()) {  //npc
				NpcParse mp = sceneP.getNpcs();
				NpcConfig mc = mp.getNpcConfigById(i);  //需要在NpcParse中增加方法
				scene.addCreatures(mc);  
			}
			
			//设置传送阵
			for(int i : sConfig.getTeleports()) {
				TelepParse tp = sceneP.getTeleps();
				TelepConfig tc = tp.getTelepConfigById(i);
				scene.addTeleport(tc.getDescription()); //场景中存放的只有传送阵的描述
			}
			//将所有场景信息放入sceneResource字段中
			addSceneResource(scene.getId(), scene);
		}
	}
	
	/**
	 *  创建一个新副本, 多人
	 * @param copyId  副本id
	 * @param players 需要进入此副本的所有玩家
	 */
	public void createCopy(int copyId, List<Player> players, int bossIndex) {
		CopysConfig copysConfig = Context.getCopysParse().getCopysConfById(copyId);
		Copys nCopys = new Copys(copyId, copysConfig.getName(), copysConfig.getDescription(), players, bossIndex);
	    addCopys(copyId, players.get(0).getId(), nCopys);  //默认是players的第一个的id作为标识
	}
	/**
	 * 所有玩家都断线后，第一个重连的玩家，创建副本
	 * @param copyId
	 * @param player
	 * @param bossIndex  
	 * @param sponsId   发起者的id
	 */
	public void creatCopy(int copyId, List<Player> players, int bossIndex, int sponsId) {
		CopysConfig copysConfig = Context.getCopysParse().getCopysConfById(copyId);
		Copys nCopys = new Copys(copyId, copysConfig.getName(), copysConfig.getDescription(), players, bossIndex);
		addCopys(copyId, sponsId, nCopys);
	}
		
	/**
	 * 创建副本
	 * @param copyId
	 * @param player
	 * @param bossIndex
	 * @param a
	 */
	public void createCopy(int copyId, Player player, int bossIndex) {
		List<Player> players = new ArrayList<>();
		players.add(player);  //添加发起者 
		System.out.println("--------------------createCopy--team=" + player.getTeammate());
		if(player.getTeammate().size() > 0) {
			for(String pn : player.getTeammate()) {
				players.add(Context.getOnlinPlayer().getPlayerByName(pn));
			}
		}
		createCopy(copyId, players, bossIndex);
	}

	
	
	/**
	 * v当玩家请求进入副本时，创建一个副本
	 * @param copyId    副本id，也就是sceneid
	 * @param playerId  需要进入副本的某个玩家的中playerId
	 * @param copys
	 */
	private void addCopys(int copyId, int playerId, Copys copys) {
		if(allCopys.containsKey(new Integer(copyId))) {
			allCopys.get(new Integer(copyId)).put(playerId, copys); 
			return;
		}		
		Map<Integer, Copys> cop = new HashMap<>();
		cop.put(playerId, copys);
		allCopys.put(copyId, cop);	
	}
	/**
	 * 通过副本id和玩家列表，删除对应的副本, 多人
	 * @param copyId
	 * @param players 要删除的副本中的所有玩家
	 */
	public void delCopys(int copyId, List<Player> players) {
		for(Player p : players) {
			if(!delCopys(copyId, p.getName())) continue;
			return;
		}
	}
	/**
	 * 通过副本id和玩家，删除对应的副本,
	 * 还有删除玩家实体
	 * @param copyId
	 * @param player
	 * @return
	 */
	public boolean delCopys(int copyId, String pname) {
		//System.out.println("------------world.delCopys正在删除副本缓存和实体");
		int pid = getPlayerEntityByName(pname).getId();
		Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
		if(pCopy == null) return false;
		Copys copy = pCopy.remove(new Integer(pid));
		CopyEntity cpe = delCopyEntity(copy.getId());
		copy = null;
		return true;
	}
	/**
	 * 通过副本id，玩家列表，获得相应的副本
	 * @param copyId  副本id
	 * @param players 玩家列表
	 * @return
	 */
	public Copys getCopysBy(int copyId, List<Player> players) {
		for(Player p : players) {
			Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
			if(pCopy == null) continue;
			return pCopy.get(new Integer(p.getId()));
		}
		return null;
	}
	/**
	 * 根据副本id和玩家id，获得相应的Copys。只有发起者的id才能得到队友的副本
	 * @param copyId 副本id
	 * @param playerId 玩家id
	 * @return
	 */
	public Copys getCopysByAPlayer(int copyId, int playerId) {
		Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
		if(pCopy == null) return null;
		return pCopy.get(new Integer(playerId));
	}
	/**
	 * 停止副本线程
	 * @param player
	 * @return
	 */
	public void delCopyThread(Player player) {
		CopyEntity copyEntity = player.getCopEntity(); 
		if(copyEntity == null) return;
		int copyId = copyEntity.getCopyId();
		int pid = player.getId();
		delCopyThread(pid, copyId);
		/**
		System.out.println("---------删除副本线程------");
		CopyEntity copyEntity = player.getCopEntity(); 
		if(copyEntity == null) return;
		int copyId = copyEntity.getCopyId();
		System.out.println("---------future----" + futureMap.size() + ", " + player.getId());
		Future future = futureMap.remove("copys"+copyId+player.getId());
		System.out.println("---------future-后---" + futureMap.size());
		if(future == null) return;	
		for(int bossId : Context.getCopysParse().getCopysConfById(copyEntity.getCopyId()).getBosses()) {
			delBossThread(player.getId(), bossId);
		}	
		future.cancel(true);
		*/
	}
	
	public void delCopyThread(int pid, int copyId) {
		System.out.println("---------删除副本线程------");
		System.out.println("---------future----" + futureMap.size() + ", " + pid);
		String iden = "copys"+copyId + pid;
		Future future = futureMap.remove(iden);
		System.out.println("------futuremap=" + futureMap.toString());
		System.out.println("=------iden = " + iden);
		System.out.println("---------future-后---" + futureMap.size() + ", " + (future==null));
		if(future == null) return;	
		for(int bossId : Context.getCopysParse().getCopysConfById(copyId).getBosses()) {
			delBossThread(pid, bossId);
		}	
		future.cancel(true);
	}
	/**
	 * 停止boss线程
	 * @param playerId
	 * @param bossId
	 */
	public void delBossThread(int playerId, int bossId) {
		Future future = futureMap.remove("boss"+bossId+playerId);
		if(future == null) return;
		future.cancel(true);
		System.out.println("boss线程 pid=" + playerId + ", bossId=" + bossId + " 已停止");
	}
	

	public Scene getSceneById(int sceneId) {
		return sceneResource.get(sceneId);
	}
	
	public void addSceneResource(int sceneId, Scene scene) {
		sceneResource.put(sceneId, scene);
	}
	public Player getPlayerByName(String name) {
		for(Player player : allRegisteredPlayer) {
			if(player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	/**
	 * 通过玩家id获得player
	 * @param id
	 * @return
	 */
	public Player getPlayerById(int id) {  
		for(Player player : allRegisteredPlayer) {
			if(player.getId() == id){
				return player;
			}
		}
		return null;
	}
	public void addAllRegisteredPlayer(Player player) {
		this.allRegisteredPlayer.add(player);
	}
	public List<Player> getPlayers(){
		return allRegisteredPlayer;
	}

	public void addPlayerEntity(PlayerEntity playerEntity) {
		this.allPlayerEntity.add(playerEntity);
	}

	public PlayerEntity getPlayerEntityByName(String name) {
		for(PlayerEntity p : allPlayerEntity) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 更新缓存
	 * @param pe
	 */
	public void updatePlayerEntity(PlayerEntity pe) {
		for(PlayerEntity p: allPlayerEntity) {
			if(p.getId() == pe.getId() ) {
				allPlayerEntity.remove(p);
				break;
			}
		}
		allPlayerEntity.add(pe);
	}
	/**
	 * 获得数据库中，最大的id
	 * 从而在服务器，关闭，开启的时候，获得一个目前最大的id，来给新注册的玩家用
	 */
	public int getMaxId() {
		int result = 0;
		for(PlayerEntity p : allPlayerEntity) {
			if(p.getId() > result)
				result = p.getId();
		}
		return result + 1;
	}

	public ConcurrentHashMap<String, Future> getFutureMap() {
		return futureMap;
	}

	public List<CopyEntity> getCopyEntitys() {
		return copyEntitys;
	}
	public void addCopyEntity(CopyEntity ce) {
		this.copyEntitys.add(ce);
	}
	/**
	 * 删除copeEntity缓存
	 * @param cid
	 */
	public CopyEntity delCopyEntity(int cid) {
		for(CopyEntity cpe : copyEntitys) {
			if(cpe.getCopyId() == cid) {
				copyEntitys.remove(cpe);
				return cpe;
			}
		}
		return null;
	}
	public CopyEntity getCopyEntityById(int copyId) {
		for(CopyEntity cpe : copyEntitys) {
			if(cpe.getCopyId() == copyId) {
				return cpe;
			}
		}
		return null;
	}
	
	
}
