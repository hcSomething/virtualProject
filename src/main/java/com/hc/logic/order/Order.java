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
			scene.allThing(session);
		} 
	},
	TRANSFER("transfer", "进行传送"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			int targetId = Integer.parseInt(args[1]);
			int sceneId = session.getPlayer().getSceneId();
			Context.getTransferService().allTransfer(targetId, sceneId, session);	
		} 
	},
	NPCTALK("npcTalk", "和npc对话"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			NpcService npcS = Context.getNpcService();
			int nId = Integer.parseInt(args[1]);
			//验证npc是否在同一场景，当在副本中时也没有这个npc
			if((session.getPlayer().getSceneId() == 0) || !npcS.isOnScene(session, nId)) {
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
			Context.getMonsterService().mDescribe(session, Integer.parseInt(args[1]));;
		}
	},
	ALLSKILL("allSkill", "所有技能"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("命令参数不正确");
				return;
			}
			SkillService skillSer = Context.getSkillService();
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
	    	SkillService skillSer = Context.getSkillService();
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
	DELGOOD("delGood", "删除物品"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			boolean deleted = session.getPlayer().delGoods(gId, amount);
			if(deleted) session.sendMessage("删除成功");
			else session.sendMessage("删除失败，请检查是否有这么多物品");
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
	ALLEQUIP("allEquip", "所有已穿着装备"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			String equips = Context.getGoodsService().allEquips(session.getPlayer().getPlayerEntity());
			session.sendMessage(equips);
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
	},
	USEGOOD("useGood", "使用恢复类物品"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			boolean used = session.getPlayer().addRecoverHpMp(gId);
			if(used) session.sendMessage("使用成功");
			else session.sendMessage("使用失败！可能不属于药品，或者此药品已用完");
		}
	},
	ECOPY("eCopy", "请求进入副本"){
		@Override 
		public void doService(String[] args, Session session) {
			//现在默认是只有一个玩家进入副本
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int copyId = Integer.parseInt(args[1]);
			//判断是否可以进入副本中
			if(!Context.getCopyService().canEnterCopy(session.getPlayer())) return;
			boolean entered = Context.getCopyService().enterCopy(copyId, session.getPlayer(), session, 0);		
		}
	},
	STORE("store", "查询商店商品"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int page = Integer.parseInt(args[1]);
			Context.getStoreService().lookStore(session, page);
		}
	},
	BUY("buy", "购买商品"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			int gid = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			Context.getStoreService().validBuyGood(session, gid, amount);
		}
	},
	ALLCHAT("allchat", "全服聊天"){
		@Override 
		public void doService(String[] args, Session session) {
			Context.getChatService().decOrder(session, args);
		}
	},
	CHAT("chat", "私聊"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.threePara(args)) {
				session.sendMessage("参数格式不正确，请重新输入");
				return;
			}
			//目标玩家的id
			int tPlaId = Context.getWorld().getPlayerEntityByName(args[1]).getId();
			Context.getChatService().privateChat(session, tPlaId, args);
		}
	},
	EMAIL("email", "邮箱系统"){
		@Override 
		public void doService(String[] args, Session session) {
			Context.getEmailService().descOrder(session, args);
		}
	},
	PK("pk", "进行pk"){
		@Override 
		public void doService(String[] args, Session session) {
			Context.getPkService().desOrder(session, args);
		}
	},
	ATTACKP("attackP", "攻击玩家"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoString(args) || !OrderVerifyService.isDigit(args[1])){
				session.sendMessage("命令参数不正确");
				return;
			}
			int skillId = Integer.parseInt(args[1]);
			Context.getSkillService().attackPlayer(session, skillId, args[2]);
		}
	},
	PARTY("group", "进行组队"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.threePara(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			Context.getParty().desOrder(session, args);
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
