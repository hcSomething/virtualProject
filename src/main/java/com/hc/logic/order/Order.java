package com.hc.logic.order;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Login;
import com.hc.logic.base.Register;
import com.hc.logic.base.Session;
import com.hc.logic.base.Teleport;
import com.hc.logic.basicService.EnterWorld;
import com.hc.logic.basicService.MonsterService;
import com.hc.logic.basicService.NpcService;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.basicService.SkillService;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.creature.Player;

import io.netty.channel.Channel;

/**
 * 这个类通过枚举的方式，将玩家输入的命令分发到不同的业务逻辑中
 * @author hc
 *
 */
public enum Order {

	REGISTER("register", "注册"){
		//这个方法将注册命令分发到注册逻辑中
		@Override
		public void doService(String[] args, Session session) {
			//有两个参数，用户名、密码
			if(!OrderVerifyService.twoString(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			String playerName = args[1];
			String password = args[2];
			new Register(playerName, password).register(session);;
		}
	},
	LOGIN("login", "登陆"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoString(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			//登陆时，玩家输入用户名和密码
			//System.out.println("这里是login");
			new Login(args[1], args[2]).login(session);
		}
	},
	ENTERWORLD("enterWorld", "进入世界"){
		@Override
		public void doService(String[] args, Session session) {
			new EnterWorld().enterWorld(session);;
		}
	},
	MAPINFORMATION("mapInfo", "当前是哪个地图"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			Scene scene = session.getPlayer().getScene();
			session.sendMessage(scene.getDescribe());
		} 
	},
	EVERYTHINGINTHISMAP("allthing", "返回当前地图的所有东西"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			Scene scene = session.getPlayer().getScene();
			session.sendMessage("所有生物" + scene.getCreatures());
			session.sendMessage("所有玩家" + scene.getPlayers() + "\n");
			//更改了传送的方式
			session.sendMessage("所有可传送目标：" + scene.allTransportableScene());
		} 
	},
	TRANSFER("transfer", "进行传送"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			int sceneId = session.getPlayer().getSceneId();
			Teleport t = new TransferService();
			int tSceneId = Integer.parseInt(args[1]);
			if(!Context.getWorld().getSceneById(sceneId).hasTelepId(tSceneId)) {
				session.sendMessage("没有这个传送阵，不能传送");
				return;
			}
			t.transfer(session.getPlayer(), sceneId, tSceneId);
			session.sendMessage("欢迎来到" + Context.getWorld().getSceneById(tSceneId).getName());
		} 
	},
	NPCTALK("npcTalk", "和npc对话"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			NpcService npcS = new NpcService();
			int nId = Integer.parseInt(args[1]);
			//验证npc是否在同一场景
			if(!npcS.isOneScene(session, nId)) {
				session.sendMessage("没有这个npc");
				return;
			}
			npcS.introduce(session, nId);
			npcS.task(session, nId);
		}
	},
	DMONST("dMonst", "获得怪物详细信息"){
		@Override
		public void doService(String[] args, Session session) {	
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			new MonsterService().mDescribe(session, Integer.parseInt(args[1]));;
		}
	},
	ALLSKILL("allSkill", "所有技能"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			SkillService skillSer = new SkillService();
			skillSer.getAllSkill(session);
		}
	},
	ATTACKM("attackM", "攻击怪物"){
	    @Override
	    public void doService(String[] args, Session session) {
	    	if(!OrderVerifyService.twoInt(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
	    	SkillService skillSer = new SkillService();
	    	skillSer.doAttack(session, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	    }
	},
	PSTATE("pState", "玩家状态"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			session.getPlayer().pState();
		}
	},
	BAG("bag", "查看背包"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			session.getPlayer().getBagService().dispBag(session);
		}
	},
	ADDGOOD("addGood", "添加物品"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			boolean inserted = session.getPlayer().addGoods(gId, amount);
			if(inserted) {
				session.sendMessage("添加物品成功");
				return;
			}
			
		}
	},
	DELGOOD("delGood", "删除或使用物品"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			session.getPlayer().delGoods(gId, amount);
		}
	},
	EQUIP("equip", "穿着装备"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			session.getPlayer().addEquip(Integer.parseInt(args[1]));
		}
	},
	DEQUIP("dEquip", "卸下装备"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			session.getPlayer().deletEquip(Integer.parseInt(args[1]));
		}
	},
	LSKILL("lSkill", "学习技能"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			boolean learnIt = session.getPlayer().addSkill(Integer.parseInt(args[1]));
			if(learnIt) session.sendMessage("学习技能成功");
		}
	};
	
	
	
	private String key;
	private String value;

	
	private Order(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * 
	 * @param st 第一个是命令名，后面的是参数，可能有也可能没有
	 */
	public static void getService(String[] st, Session session) {
		//System.out.println("--order--: " + session);
		//如果没有登陆，那么玩家就没在在线玩家列表，就不能用别的指令
		//这些可以用session存储状态来实现。
		System.out.println("getService");
		boolean isLoged = true;
		if(!st[0].equals("login") && !st[0].equals("register")) {	
		    Player pp = session.getPlayer();
		    if(pp == null) {
		    	session.sendMessage("请登陆");
		    	return;
		    }
		    isLoged = false;
		    
			for(Player p : Context.getOnlinPlayer().getOnlinePlayers()) {
				if(p.getName().equals(pp.getName())) {
					isLoged = true;
					break;
				}
				
			}

		}
		
		
		if(st[0].equals("register")) isLoged = true;
		
		if(!isLoged) {
			session.sendMessage("请登陆");
			return;
		}
		
		
		
		for(Order or : Order.values()) {	
			if(st[0].equals(or.getKey())) {
				or.doService(st, session);
				break;
			}
		}
		//session.sendMessage("没有这命令");
		//System.out.println("getService后");
	}
	
	public abstract void doService(String[] args, Session session);

	
	
	
	
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



}
