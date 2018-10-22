package com.hc.logic.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.config.TelepConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
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
public class World {

	//所有场景：（sceneId, scene），现在都是从配置文件中加载
	private  Map<Integer, Scene> sceneResource = new HashMap<>();
	//所有注册的玩家，
	private List<Player> allRegisteredPlayer = new ArrayList<>();
	//所有玩家实体，在启动时，从数据库加载
	private List<PlayerEntity> allPlayerEntity = new ArrayList<>();
	//所有的物品，包括装备
	//private List<GoodsEntity> allGoodsEntity = new ArrayList<>();
	//所有的玩家的所有装备，无论是在背包中，还是穿上了
	//private List<Equip> allEquip = new ArrayList<>();
	

	
	/**
	 * 只能有一个World实例
	 */
	private static World instance = new World();
	private World() {
		init();
	}
	public static World getInstance() {
		return instance;
	}
	
	/**
	 * 初始化操作，在服务器启动时调用，
	 */
	private void init() {
		String hql = "from PlayerEntity";
		allPlayerEntity = new PlayerDaoImpl().find(hql);
		
		//allGoodsEntity = new PlayerDaoImpl().find("from GoodsEntity");
		
		
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
		SceneParse sceneP = Context.getSceneParse();
		List<SceneConfig> sParseList = sceneP.getAllSceneConfig();
		Scene scene = null;
		for(SceneConfig sConfig : sParseList) {
			scene = new Scene();
			scene.setId(sConfig.getSceneId());
			scene.setName(sConfig.getName());
			scene.setDescribe(sConfig.getDescription());
			//设置creature
			for(int i : sConfig.getMonsts()) { //怪物
				MonstParse mp = sceneP.getMonsters();
				MonstConfig mc = mp.getMonstConfgById(i);
				scene.addCreatures(mc);
			}
			
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
	

	
	
	
	public Scene getSceneById(int sceneId) {
		return sceneResource.get(sceneId);
	}

	public Map<Integer, Scene> getSceneResource() {
		return sceneResource;
	}

	public void addSceneResource(int sceneId, Scene scene) {
		sceneResource.put(sceneId, scene);
		//System.out.println("******" + scene.getName() + " && " + scene.getId() + " ** " + scene.getDescribe());
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
		System.out.println("getPlayerByid ---" + allRegisteredPlayer.toString());
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
	
}
