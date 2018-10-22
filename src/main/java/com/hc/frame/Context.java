package com.hc.frame;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.hc.frame.handlers.ServerHandler;
import com.hc.frame.taskSchedule.TaskProducer;
import com.hc.logic.base.Register;
import com.hc.logic.base.Session;
import com.hc.logic.base.World;
import com.hc.logic.creature.*;
import com.hc.logic.order.Order;
import com.hc.logic.xmlParser.GoodsParse;
import com.hc.logic.xmlParser.LevelParse;
import com.hc.logic.xmlParser.SceneParse;
import com.hc.logic.xmlParser.SkillParse;

import io.netty.channel.Channel;
/**
 * 所有实体的初始化地方，后期可用spring管理
 * 
 * 放在这里的类是业务逻辑进行，或者是实体类，
 * 
 * @author hc
 *
 */
public class Context {



	//
	
	//*********************所有场景,这些以后可以用配置文件*************************
	//出生地
	//private static BornPlace bornPlace; 
	//新手村
	//private static VillageOfFreshman villageOfFreshman;
	
	//*************配置文件****************
    private static SceneParse sceneParse;   //场景
    private static SkillParse skillParse;   //技能
    private static LevelParse levelParse;   //等级
    private static GoodsParse goodsParse;   //物品，包括丹药和装备
	
	//*************************************************

	//世界
	private static World world;
	//在线玩家
	private static OnlinePlayer onlinPlayer;
	//所有客户端的channel和session的对应,
	private static ConcurrentHashMap<Channel, Session> channel2Session = new ConcurrentHashMap<>();
	//private static ConcurrentHashMap<Session, Channel> session2Channel = new ConcurrentHashMap<>();
	//一个周期性调用的线程池
	private static TaskProducer taskProducer;
	//玩家id
	private static AtomicInteger pID = new AtomicInteger(1008);

    
	
	
	
	
	
	
	/**
	 * 只有一个Context实例
	 */
	private static Context instance = new  Context();
	
	private Context() {
		
	}
	public static Context getInstance() {
		return instance;
	}
	

	/**
	 * 初始化方法
	 * 有了spring之后，就不需要了
	 * @return
	 */
	public static void initialize() {
		
		instance.setSceneParse(new SceneParse());
		instance.setSkillParse(new SkillParse());
		instance.setWorld(World.getInstance());
	    //instance.setVillageOfFreshman(new VillageOfFreshman());
	    //instance.setBornPlace(new BornPlace());
	    instance.setOnlinPlayer(new OnlinePlayer());
	    instance.setTaskProducer(new TaskProducer());
	    instance.setLevelParse(new LevelParse());
	    instance.setGoodsParse(new GoodsParse());
	    
	}
	
	
	
	
	
	

/**
	public static BornPlace getBornPlace() {
		return bornPlace;
	}
	public void setBornPlace(BornPlace bornPlace) {
		this.bornPlace = bornPlace;
	}
	public static VillageOfFreshman getVillageOfFreshman() {
		return villageOfFreshman;
	}
	public void setVillageOfFreshman(VillageOfFreshman villageOfFreshman) {
		this.villageOfFreshman = villageOfFreshman;
	}
*/
	public static World getWorld() {
		return world;
	}
	public void setWorld(World world) {
		this.world = world;
	}
	public static OnlinePlayer getOnlinPlayer() {
		return onlinPlayer;
	}
	public void setOnlinPlayer(OnlinePlayer onlinPlayer) {
	   this.onlinPlayer = onlinPlayer;
	}
	
	public static ConcurrentHashMap<Channel, Session> getSession2Channel() {
		return channel2Session;
	}
	public static Session getSessionByChannel(Channel channel) {
		/**
		if(channel2Session.size() > 1) {
			System.out.println("c2s  " + channel2Session.toString());
			System.out.println("s2c  " + session2Channel.toString());
		}
		*/
		return channel2Session.get(channel);
	}
	public static void addChannel2Session(Channel channel, Session session) {
		channel2Session.put(channel, session);
		//session2Channel.put(session, channel);
		//System.out.println("channel2Session的大小：" + channel2Session.size()+ "  相反 " + session2Channel.size());
	}
	public static void deleteChannel2Session(Channel channel) {
		channel2Session.remove(channel);
	}
	public static TaskProducer getTaskProducer() {
		return taskProducer;
	}
	public void setTaskProducer(TaskProducer taskProducer) {
		Context.taskProducer = taskProducer;
	}
	
	
	public static void channelToString() {
		if(channel2Session.size() > 1) {
			System.out.println("c2s  " + channel2Session.toString());
			//System.out.println("s2c  " + session2Channel.toString());
		}

	}
	public static int getpID() {
		return pID.getAndIncrement();
	}
	public static void setpID(int pID) {
		Context.pID = new AtomicInteger(pID);
	}
	public static SceneParse getSceneParse() {
		return sceneParse;
	}
	public void setSceneParse(SceneParse sceneParse) {
		Context.sceneParse = sceneParse;
	}
	public static SkillParse getSkillParse() {
		return skillParse;
	}
	public void setSkillParse(SkillParse skillParse) {
		Context.skillParse = skillParse;
	}
	public static LevelParse getLevelParse() {
		return levelParse;
	}
	public void setLevelParse(LevelParse levelParse) {
		Context.levelParse = levelParse;
	}
	public static GoodsParse getGoodsParse() {
		return goodsParse;
	}
	public void setGoodsParse(GoodsParse goodsParse) {
		Context.goodsParse = goodsParse;
	}

	
	
}
