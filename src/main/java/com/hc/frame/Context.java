package com.hc.frame;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hc.frame.taskSchedule.TaskProducer;
import com.hc.logic.base.Session;
import com.hc.logic.base.World;
import com.hc.logic.basicService.AwardService;
import com.hc.logic.basicService.GoodsService;
import com.hc.logic.basicService.MonsterService;
import com.hc.logic.basicService.NpcService;
import com.hc.logic.basicService.SkillService;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.chat.ChatService;
import com.hc.logic.chat.EmailService;
import com.hc.logic.chat.WorldChat;
import com.hc.logic.chat.WorldChatObservable;
import com.hc.logic.copys.CopyService;
import com.hc.logic.copys.Party;
import com.hc.logic.pk.PkService;
import com.hc.logic.pk.TwoPlayerPk;
import com.hc.logic.xmlParser.CopysParse;
import com.hc.logic.xmlParser.GoodsParse;
import com.hc.logic.xmlParser.LevelParse;
import com.hc.logic.xmlParser.SceneParse;
import com.hc.logic.xmlParser.SkillParse;
import com.hc.login.store.Store;
import com.hc.login.store.StoreService;

import io.netty.channel.Channel;
/**
 * 所有实体的初始化地方，后期可用spring管理
 * 
 * 放在这里的类是业务逻辑进行，或者是实体类，
 * 
 * @author hc
 *
 */
@Component
public class Context implements ApplicationContextAware{



	//
	
	//*********************所有场景,这些以后可以用配置文件*************************
	//出生地
	//private static BornPlace bornPlace; 
	//新手村
	//private static VillageOfFreshman villageOfFreshman;
	private ApplicationContext context;
	
	//*************配置文件****************
    private static SceneParse sceneParse;   //场景
    private static SkillParse skillParse;   //技能
    private static LevelParse levelParse;   //等级
    private static GoodsParse goodsParse;   //物品，包括丹药和装备
    private static CopysParse copysParse;   //副本
	
	//*************************************************

	//世界
	private static World world;
	
    //在线玩家
	private static OnlinePlayer onlinPlayer;
    
	//所有客户端的channel和session的对应,
	private static ConcurrentHashMap<Channel, Session> channel2Session = new ConcurrentHashMap<>();
	
	//一个周期性调用的线程池
	private static TaskProducer taskProducer;
	
	//传送服务
	private static TransferService transferService;
	//npc服务
	private static NpcService npcService;
	//怪物服务
	private static MonsterService monsterService;
	//技能服务
	private static SkillService skillService;
	//物品服务
	private static GoodsService goodsService;
	//副本服务
	private static CopyService copyService;
	//奖励服务
	private static AwardService awardService;
	//商店服务
	private static StoreService storeService;
	//商店
	private static Store store;
	//世界聊天频道通知器
	private static WorldChatObservable worldChatObservable;
	//世界聊天
	private static WorldChat worldChat;
	//聊天服务
	private static ChatService chatService;
	//邮件服务
	private static EmailService emailService;
	//pk服务
	private static PkService pkService;
	//两个玩家pk
	private static TwoPlayerPk twoPlayerPK;
	//组队服务
	private static Party party;
	
	
	//玩家id
	private static AtomicInteger pID = new AtomicInteger(1008);

    
	
	/**
	private Context() {
		System.out.println("这里是context的构造方法");
	}
	public static Context instance = new Context();
	public static Context getInstance() {
		return instance;
	}
	*/
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}


	
	



	public static SkillParse getSkillParse() {
		return skillParse;
	}
	@Autowired
	@Qualifier("skillParse")
	public void setSkillParse(SkillParse skillParse) {
		Context.skillParse = skillParse;
	}

	public static LevelParse getLevelParse() {
		return levelParse;
	}
	@Autowired
	@Qualifier("levelParse")
	public void setLevelParse(LevelParse levelParse) {
		Context.levelParse = levelParse;
	}

	public static GoodsParse getGoodsParse() {
		return goodsParse;
	}
	@Autowired
	@Qualifier("goodsParse")
	public void setGoodsParse(GoodsParse goodsParse) {
		Context.goodsParse = goodsParse;
	}

	public static World getWorld() {
		return world;
	}
	@Autowired
	public void setWorld(World world) {
		Context.world = world;
	}

	public static OnlinePlayer getOnlinPlayer() {
		return onlinPlayer;
	}
	@Autowired
	public void setOnlinPlayer(OnlinePlayer onlinPlayer) {
		Context.onlinPlayer = onlinPlayer;
	}

	public static TaskProducer getTaskProducer() {
		return taskProducer;
	}
	@Autowired
	public void setTaskProducer(TaskProducer taskProducer) {
		Context.taskProducer = taskProducer;
	}
	
	public static SceneParse getSceneParse() {
		return sceneParse;
	}
	@Autowired
	@Qualifier("sceneParse")
	public void setSceneParse(SceneParse sceneParse) {
		Context.sceneParse = sceneParse;
	}

	public static TransferService getTransferService() {
		return transferService;
	}
	@Autowired
	public void setTransferService(TransferService transferService) {
		Context.transferService = transferService;
	}

	public static NpcService getNpcService() {
		return npcService;
	}
	@Autowired
	public void setNpcService(NpcService npcService) {
		Context.npcService = npcService;
	}

	public static MonsterService getMonsterService() {
		return monsterService;
	}
	@Autowired
	public void setMonsterService(MonsterService monsterService) {
		Context.monsterService = monsterService;
	}
	
	public static SkillService getSkillService() {
		return skillService;
	}
	@Autowired
	public void setSkillService(SkillService skillService) {
		Context.skillService = skillService;
	}

	public static GoodsService getGoodsService() {
		return goodsService;
	}
	@Autowired
	public void setGoodsService(GoodsService goodsService) {
		Context.goodsService = goodsService;
	}

	public static CopysParse getCopysParse() {
		return copysParse;
	}
	@Autowired
	public void setCopysParse(CopysParse copysParse) {
		Context.copysParse = copysParse;
	}
	
	public static CopyService getCopyService() {
		return copyService;
	}
	@Autowired
	public void setCopyService(CopyService copyService) {
		Context.copyService = copyService;
	}
	
	public static AwardService getAwardService() {
		return awardService;
	}
	@Autowired
	public void setAwardService(AwardService awardService) {
		Context.awardService = awardService;
	}

	public static StoreService getStoreService() {
		return storeService;
	}
	@Autowired
	public void setStoreService(StoreService storeService) {
		Context.storeService = storeService;
	}

	public static Store getStore() {
		return store;
	}
	@Autowired
	public void setStore(Store store) {
		Context.store = store;
	}

	public static WorldChatObservable getWorldChatObservable() {
		return worldChatObservable;
	}
	@Autowired
	public void setWorldChatObservable(WorldChatObservable worldChatObservable) {
		Context.worldChatObservable = worldChatObservable;
	}

	public static WorldChat getWorldChat() {
		return worldChat;
	}
	@Autowired
	public void setWorldChat(WorldChat worldChat) {
		Context.worldChat = worldChat;
	}
	public static ChatService getChatService() {
		return chatService;
	}
	@Autowired
	public void setChatService(ChatService chatService) {
		Context.chatService = chatService;
	}

	public static EmailService getEmailService() {
		return emailService;
	}
	@Autowired
	public void setEmailService(EmailService emailService) {
		Context.emailService = emailService;
	}

	public static PkService getPkService() {
		return pkService;
	}
	@Autowired
	public void setPkService(PkService pkService) {
		Context.pkService = pkService;
	}

	public static TwoPlayerPk getTwoPlayerPK() {
		return twoPlayerPK;
	}
	@Autowired
	public void setTwoPlayerPK(TwoPlayerPk twoPlayerPK) {
		Context.twoPlayerPK = twoPlayerPK;
	}

	public static Party getParty() {
		return party;
	}
	@Autowired
	public void setParty(Party party) {
		Context.party = party;
	}




	
	
	
	



	public static ConcurrentHashMap<Channel, Session> getSession2Channel() {
		return channel2Session;
	}
	public static Session getSessionByChannel(Channel channel) {
		return channel2Session.get(channel);
	}
	public static void addChannel2Session(Channel channel, Session session) {
		channel2Session.put(channel, session);
	}
	public static void deleteChannel2Session(Channel channel) {
		channel2Session.remove(channel);
	}
	
	public static void channelToString() {
		if(channel2Session.size() > 1) {
			System.out.println("c2s  " + channel2Session.toString());
		}

	}
	public static int getpID() {
		return pID.getAndIncrement();
	}
	public static void setpID(int pID) {
		Context.pID = new AtomicInteger(pID);
	}

	

	
	
}
