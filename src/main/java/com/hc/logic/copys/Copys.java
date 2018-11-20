package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * 副本场景
 * @author hc
 *
 */
public class Copys extends Scene{
	//所有boss的id
	private List<Integer> allBoss = new ArrayList<>();
	//本副本中，所有已生成的活着的boss
	private List<Boss> bosses = new ArrayList<>();
	//开启此副本的时间
	private long openTime;
	//从哪个boss开始。初始进入时0。
	private int bossIndex = 0;
	private CopyEntity copyEntity;
	//多人副本中，剩余的玩家
	private List<Player> availablePlayer;
    
    public Copys(int id, String name, String desc, List<Player> players, int bossInd) {
    	super(2);
    	this.id = id;  //副本di
    	this.name = name;
    	this.describe = desc;
    	this.players = new ArrayList<>(players);   //进入当前副本的玩家列表
    	this.availablePlayer = new ArrayList<>(players); //进入的玩家都是可攻击对象
    	System.out.println("------------copys中的所有玩家---" + players.toString());
    	this.allBoss = Context.getCopysParse().getCopysConfById(id).getBosses();
    	this.bossIndex = bossInd;
    	//默认生成一只boss
    	//if(bossIndex >= bosses.size()) complete();
    	if(bosses.size() == 0) createBoss(allBoss.get(bossIndex)); 
    	this.openTime = System.currentTimeMillis();  //副本开启时，记录开启时间
    	//exe(2, "copys"+id + players.get(0).getId()); //启动一个周期性调度器，周期2秒
    	Context.getTaskConsume().exe(2, "copys"+id + players.get(0).getId(), this);
    }
    
    
	//这个方法会被自动周期性调用
    @Override
    public void run() {
    	//System.out.println("-------------------aaaaaaaaa");
    	//玩家每秒恢复的血量和法力
    	//recoverHpMp();
    	super.letPlayerProgress();
    	//刷新boss
    	//System.out.println("刷新boss列表" + System.currentTimeMillis());
    	haveAvailableBoss();
    	//判断在副本中待的时间
    	if(isTimeOut()) {
    		//超时，则强制离开副本
    		complete();
    	}
    }
    
    @Override
    public void attackPlayer() {
    	//什么都不做，只是重写Scece中的怪物攻击，使得boss攻击不是在scene中进行的
    }
    
    @Override
    public void allThing(Session session) {
		session.sendMessage("所有boss: " + bossNameList());
		session.sendMessage("所有玩家" + getPlayers() + "\n");
		int scecid = Context.getCopysParse().getCopysConfById(id).getPlace();
		String name = Context.getSceneParse().getSceneById(scecid).getName();
		session.sendMessage("所有可传送目标：" + name + "【" + scecid + "】");
    }
    
    @Override
    public boolean hasMonst(int mId) {
    	for(int i : allBoss) {
    		if(i == mId)
    			return true;
    	}
    	return false;
    }
    
    @Override
    public void addAttackPlayer(int mId, Player p) {
    	//boss攻击玩家的方式和怪物不一样，在Boss中实现
    	//对于不主动攻击玩家的boss，玩家首次攻击boss时，boss就开始攻击
    	for(Boss boss : bosses) {
    		if(boss.getId() == mId) {
    			boss.setAttackNow(true);
    		}
    	}
    }
    
    /**
     * 返回当前所有的boss的名称列表
     * @return
     */
    public String bossNameList() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("【");
    	//System.out.println("bossNameLis---------------- " + bosses.size());
    	for(Boss boss : bosses) {
    		String name = Context.getSceneParse().getMonsters().getMonstConfgById(boss.getId()).getName();
    		sb.append(name);
    		sb.append(", ");
    	}
    	if(sb.length() > 1) sb.deleteCharAt(sb.length()-1);
    	sb.append("】");
    	return sb.toString();
    }
    
    /**
     *    通过boss id来生成一个boss, 生成的boss会自动根据自己的配置进行相应的攻击活动
     *    也就是说，boss的攻击是由其自己决定的，不是在场景中设置的。
     * @param id
     */
    public Boss createBoss(int bid) {
    	Boss boss = new Boss(bid, players);
    	bosses.add(boss);
    	//启动Boss的周期性调度线程，1秒
    	//boss.exe(1, "boss"+bid+players.get(0).getId());
    	Context.getTaskConsume().exe(1, "boss"+bid+players.get(0).getId(), boss);
    	return boss;
    }
    
    /**
     *    当怪物死亡时，从boss列表中删除
     * @param id  boss的id
     * @return  
     */
    public boolean delBoss(int id) {
    	for(Boss boss : bosses) {
    		if(boss.getId() == id) {
    			bosses.remove(boss);
    			//一个boss死亡后，需要停止该boss的线程
    			Context.getWorld().delBossThread(players.get(0).getId(), id);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     *   ww验证此副本是否还有剩余开启时间
     *   @param id 此副本id
     * @return  超時   true
     */
    public boolean isTimeOut() {
    	long current = System.currentTimeMillis();
    	int continueT = Context.getCopysParse().getCopysConfById(id).getContinueT();
        long allTime = continueT * 60 * 1000; //此副本设定的开启时间。从分钟化为毫秒
        if((current - openTime) >= allTime) { 
        	return true;
        }
        return false;
    }
    
    /**
     * 验证当前boss是否死亡。死亡后，是否还有boss生成；如果有则生成怪物。
     * 
     */
    public void haveAvailableBoss() {
    	int boId = 1000;
    	int bossId = allBoss.get(bossIndex);
    	//System.out.println("-------haveavailableBosos------" + bossId + ", " + bossIndex);
    	//System.out.println("------getMonstById=null " + getMonsteById(bossId));
    	if(!getMonsteById(bossId).isAlive()) {
			//若boss已死亡， 从列表中删除
			delBoss(bossId);
			//是否还有怪物可以生成
			boId = Context.getCopysParse().getCopysConfById(id).moreBoss(bossId);
			bossIndex++;  
			players.get(0).getCopEntity().setBossindex(bossIndex);  //更新实体类
			//System.out.println("-----------------haveAvailableBoss--boId=" + boId);
			if(boId != -1) {
				createBoss(boId);
				return;
			}
		}
    	//已经没有怪物了，副本完成退出
    	if(boId == -1) {
    		//System.out.println("-----------------haveAvailableBoss--没有boss了");
    		obtainAward();
        	complete();
    	}
    }
    
    /**
     *  如果是成功完成副本，则获得奖励
     *  若是超时离开副本，则没有奖励
     */
    public void obtainAward() {
    	String award = Context.getCopysParse().getCopysConfById(id).getsRewords();
    	//发送奖励邮件
    	for(Player p : players) {
    		Context.getEmailService().sendGoodsEmail(p.getName(), award);
    	}
    }
    
    /**
     * 当副本中的boss全部消灭了，那么副本就完成了
     */
    public void complete() {
    	System.out.println("------------------------complete ");
    	//自动进行传送
    	int targetId = Context.getCopysParse().getCopysConfById(id).getPlace();
    	TransferService transferService = Context.getTransferService();
    	//清除所有在副本完成后还没有重连的玩家
    	offLinePla(players.get(0));
    	for(int i = (players.size()-1); i >=0 ; i--) {
    		Player player = players.get(i);
    		System.out.println("-----------------complete--进行传送" + players.toString());
    		transferService.transferCopy(player, id); //从副本中传送出来   		
    	}
    }
    
    /**
     * 副本完成时，更新断线未重连玩家的数据库
     * 所有不是主动离开副本的玩家都会得到奖励
     * @param player 任意一个还在副本中的玩家
     */
    public void offLinePla(Player player) {
    	System.out.println("--------断线未重连---名字--前 " + player.getName());
    	String award = Context.getCopysParse().getCopysConfById(id).getsRewords();
    	//List<PlayerEntity> pes = player.getCopEntity().getPlayers();
		String hql = "select ce.players from CopyEntity ce where sponsor "
				+ "like : name";
		List<PlayerEntity> pes = new PlayerDaoImpl().find(hql, player.getSponserNmae());
		
    	System.out.println("--------断线未重连---名字--中 " + pes.toString());
    	int place = Context.getCopysParse().getCopysConfById(id).getPlace();
    	for(PlayerEntity pe : pes) {
    		if(getPlayerByName(pe.getName()) == null) {
    			if(pe.getCopyEntity() != null) {
    				System.out.println("--------断线未重连---名字 " + pe.getName());
    				//表示是断线未重连
    				pe.setSceneId(place);
    				pe.setCopyEntity(null);
    				new PlayerDaoImpl().update(pe);
    				Context.getWorld().updatePlayerEntity(pe); //更新缓存
    				//Context.getEmailService().sendGoodsEmail(pe.getName(), award);
    				System.out.println("-----------断线未重连的玩家pe=" + pe.toString());
    			}
    		}
    	}
    	
    }

	public int getBossIndex() {
		return bossIndex;
	}

	public void setBossIndex(int bossIndex) {
		this.bossIndex = bossIndex;
	}
	
	@Override
	public Monster getMonsteById(int bid) {
		if(monsters.size() == 0) initMonst();
		for(Monster mon : monsters) {
			if(mon.getMonstId() == bid) {
				return mon;
			}
		}
		return null;
	}
	
	public void initMonst() {
		for(int bid : allBoss) {
			Monster boss = new Monster(bid);
			monsters.add(boss);
		}
		//System.out.println("---copys中的intitMonst--------------------------" + monsters.toString());
	}
    
	@Override
	public String toString() {
		return "copy {id=" + id
		      + ", name=" + name
		      +"}";
	}


	public CopyEntity getCopyEntity() {
		return copyEntity;
	}


	public void setCopyEntity(CopyEntity copyEntity) {
		this.copyEntity = copyEntity;
	}


	/**
	 * 副本中是否还要玩家
	 * @return
	 */
	public boolean haveAvailablePlayer() {
		return availablePlayer.size() > 0;
	}
	/**
	 * 玩家离开副本时，从剩余玩家中删除
	 * @param id
	 */
	public void delPlayer(int id) {
		for(Player p : availablePlayer) {
			if(p.getId() == id) {
				availablePlayer.remove(p);
				players.remove(p);  //主动传送出去的要减少场景中玩家数量
				break;
			}
		}
		for(Boss bs : bosses) {
			bs.delPlayers(id);
		}
	}
	public void playerComeback(Player player) {
		System.out.println("-----players--" + players.toString());
		System.out.println("-----availablePlayer--" + availablePlayer.toString());

		this.availablePlayer.add(player);
		this.players.add(player);
		for(Boss bs : bosses) {
			bs.addPlayer(player);
		}
		System.out.println("-----players-后-" + players.toString());
		System.out.println("-----availablePlayer-后-" + availablePlayer.toString());
		
	}

    
	
}
