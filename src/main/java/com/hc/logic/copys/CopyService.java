package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.domain.PlayerEntity;


@Component
public class CopyService {

	/**
	 * 验证是否可以进入副本
	 * @param player
	 * @return
	 */
	public boolean canEnterCopy(Player player) {
		int copId = player.getCopEntity().getCopyId();
		CopysConfig copyConfig = Context.getCopysParse().getCopysConfById(copId);
		boolean hasComp = player.getCopEntity().getBossindex() >= copyConfig.getBosses().size();
		//删除超时副本数据库信息
		if(isTimeOut(player) || hasComp) {
			player.getPlayerEntity().setNeedDel(true);
			new CopyPersist(player.getPlayerEntity()).delCopys(player.getPlayerEntity());
			player.getPlayerEntity().setNeedDel(true);
			return true;
		}
		//判断是否已经在副本中
		Session session = player.getSession();
		if(player.getCopEntity() != null) {
			session.sendMessage("已经在副本中，不能同时进入多个副本");
			return false;
		}
		return true;
	}
	
    /**
     *   验证此副本是否还有剩余开启时间
     *   @param id 此副本id
     * @return  超時   true
     */
    public boolean isTimeOut(Player player) {
    	if(player.getCopEntity() == null) return false;
    	long openTime = player.getCopEntity().getFirstEnterTime();
    	int copId = player.getCopEntity().getCopyId();
    	long current = System.currentTimeMillis();
    	int continueT = Context.getCopysParse().getCopysConfById(copId).getContinueT();
        long allTime = continueT * 60 * 1000; //此副本设定的开启时间。从分钟化为毫秒
        if((current - openTime) >= allTime) { 
        	return true;
        }
        return false;
    }
	
	
	/**
	 * 请求进入副本, 创建副本
	 * @param copyId 副本id
	 * @return
	 */
	public boolean enterCopy(int copyId, Player player, Session session, int bossIndex) {
		CopysConfig copyConf = Context.getCopysParse().getCopysConfById(copyId);
		if(copyConf == null) {
			session.sendMessage("没有这个副本");
			return false;
		}
		//验证进入该副本的次数（待续）
		
		//验证当前场景是否可以进入此副本.当玩家的sceneId=0,一定可以进入副本
		boolean canEnter = (copyConf.getPlace() == player.getSceneId() || player.getSceneId()==0 );
		if(!canEnter) {
			session.sendMessage("当前场景不能进入副本: " + copyConf.getName());
			return false;
		}
		//等级限制
		boolean leveS = (player.getLevel() >= copyConf.getCondition());
		if(!leveS) {
			session.sendMessage("等级不够，不能进入副本: " + copyConf.getName());
			return false;
		}
		//创建副本
		Context.getWorld().createCopy(copyId, player, bossIndex);
		//进行传送。
		Context.getTransferService().copyTransfer(player, copyId);
		return true;
	}
	
	
	
	

}
