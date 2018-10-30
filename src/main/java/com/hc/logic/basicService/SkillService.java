package com.hc.logic.basicService;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Player;

@Component
public class SkillService {
	
	
	/**
	 * 发送给客户端，当前玩家所有技能信息
	 * @param session
	 */
	public void getAllSkill(Session session) {
		Player player = session.getPlayer();
		List<Integer> skills = player.getSkills();
		StringBuilder sb = new StringBuilder();
		sb.append("所有技能：- - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
		for(int sId : skills) {
			SkillConfig sConfig = Context.getSkillParse().getSkillConfigById(sId);
			sb.append("【");
			sb.append(sConfig.getName() + ": ");
			sb.append(sConfig.getDescription());
			sb.append("; 攻击力：" + sConfig.getAttack() );
			sb.append("; 冷却时间: " + sConfig.getCd());
			sb.append("; 消耗法力：" + sConfig.getMp());
			sb.append("; 持续时间：" + sConfig.getContinueT() + "秒");
			sb.append("】" + "\n");
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
		session.sendMessage(sb.toString());
	}
	
	/**
	 * 攻击怪物，
	 * @param session
	 * @param skillId：技能id，mId：怪物id
	 */
	public void doAttack(Session session, int skillId, int mId) {
		Player player = session.getPlayer();
		
		//判断玩家是否拥有这个技能,且有相应的武器
		if(!player.hasSkill(skillId)) {
			session.sendMessage("没有这个技能");
			return;
		}
		
		//该技能对应的武器的物品id
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//判断该技能需要的武器是否存在
		if(!player.contEquip(wId)) {
			session.sendMessage("没有装备对应的武器，不能使用");
			return;
		}
		//判断该技能所对应的武器的耐久度
		if(player.restContiT(wId) < 1) {
			session.sendMessage("武器的耐久度过低，请修理后再使用");
			return;
		}
		
		//判断该技能是否cd完
		if(!cdOut(player, skillId)) {
			session.sendMessage("技能没有冷却完，不能使用");
			return;
		}
		
		//判断是否和怪物在同一场景
		if(!player.getScene().hasMonst(mId)) {
			session.sendMessage("没有这个怪物");
			return;
		}
		
		
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		MonstConfig monstConf = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		
		//怪物已经死亡时，不能攻击
		if(!monstConf.isAlive()) {
			session.sendMessage("怪物已经死亡，不能攻击");
			return;
		}
		
		//判断是否有足够的法力
		if(player.getMp() < skillConf.getMp()) {
			session.sendMessage("法力不够");
			return;
		}
		
		//使用技能后------
		
		//减少法力
		int restMp = player.getMp() - skillConf.getMp();
		player.setMp(restMp);
		//记录具有持续效果的技能的使用时间
		player.addReduceAtt(skillId);
		
		//更新技能cd
		player.updateCdById(skillId);
		//攻击怪物，就要被怪物攻击。
		player.getScene().addAttackPlayer(mId, player);
		
		//更新武器耐久度
		player.minusContT(wId);
		
		//计算玩家攻击后，怪物剩余血量，玩家的攻击力需要叠加：buff，技能等等
		int restHp = monstConf.getHp() - player.AllAttack(skillId);
		session.sendMessage("击中" + monstConf.getName());
		if(restHp < 0) {
			//怪物死亡
			monstConf.setHp(0);
			monstConf.setAlive(false);
			player.getScene().deleteAttackMonst(mId); //怪物死亡后，就不能攻击玩家
			//击杀怪物/boss获得相应奖励
			Context.getAwardService().obtainAward(player, monstConf);
			//需要广播给当前场景的所有玩家
			String mesg = monstConf.getName() + "死亡";
			BroadcastService.broadInScene(session, mesg);
			return;
		}
		monstConf.setHp(restHp);
		//播放怪物血量
		session.sendMessage(monstConf.getName() + "的血量为：" + monstConf.getHp());
	}
	
	/**
	 * 技能是否冷却完
	 * sId：技能id
	 * @return
	 */
	public boolean cdOut(Player player, int sId) {
	    long pTime = player.getCdTimeByid(sId).getTime();
	    long nTime = new Date().getTime();
	    long diff = nTime - pTime;
	    long di = Context.getSkillParse().getSkillConfigById(sId).getCd() * 1000; //需要扩大1000倍，化为毫秒
	    //System.out.println(pTime + "=--" + nTime);
	    if(di > diff)
	    	return false;
	    return true;
	}
	


	
	
}
