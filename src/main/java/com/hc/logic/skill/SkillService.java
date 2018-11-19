package com.hc.logic.skill;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.achieve.Achievement;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

@Component
public class SkillService {
	
	/**
	 * 解析命令
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length > 3 || args.length < 2) {
			session.sendMessage("命令参数不正确");
			return;
		}
		if(args.length == 3) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			//单个攻击: attackM 技能id 怪物id
			int skillId = Integer.parseInt(args[1]);
			int mid = Integer.parseInt(args[2]);
			attackAMonster( session, skillId, mid);
		}else {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("命令参数不正确");
				return;
			}
			//群体攻击: attackM 技能
			int skillId = Integer.parseInt(args[1]);
			attackAllMonster( session,  skillId);
		}
	}
	
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
			sb.append("; 防御力：" + sConfig.getProtect());
			sb.append("; 治疗：" + sConfig.getCure());
			sb.append("; 冷却时间: " + sConfig.getCd());
			sb.append("; 消耗法力：" + sConfig.getMp());
			sb.append("; 持续时间：" + sConfig.getContinueT() + "秒");
			sb.append("】" + "\n");
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - -" );
		session.sendMessage(sb.toString());
	}
	
	/**
	 * 单个攻击
	 * @param session
	 * @param skillId
	 * @param mid
	 */
	public void attackAMonster(Session session, int skillId, int mid) {
		System.out.println("------------进行单个攻击---------");
		Player player = session.getPlayer();		
		//玩家技能验证
		if(!skillValid(session, skillId)) return;
		if(Context.getSkillParse().getSkillConfigById(skillId).getScope() == 0) {
			session.sendMessage("此技能是全范围攻击！将攻击所有敌人");
			attackAllMonster( session, skillId);
			return;
		}
		//判断是否和怪物在同一场景
		if(!player.getScene().hasMonst(mid)) {
			session.sendMessage("没有这个怪物");
			return;
		}
		Monster monst = player.getScene().getMonsteById(mid);
		int isalive = monst.canAttack(skillId, player);
		//怪物已经死亡时，不能攻击
		if(isalive == -1) {
			session.sendMessage("怪物已经死亡，不能攻击");
			return;
		}
		//记录具有持续效果的技能的使用时间
		player.addContinueAtttib(skillId, monst);			
		//攻击怪物，就要被怪物攻击。
		player.getScene().addAttackPlayer(mid, player);			
		doAttack(session, monst, isalive);
		//更新技能和武器
		updateWeapon(player, skillId);

	}
	/**
	 * 群体攻击
	 * @param session
	 * @param skillId
	 */
	public void attackAllMonster(Session session, int skillId) {
		System.out.println("------------进行群体攻击---------");
		Player player = session.getPlayer();		
		//玩家技能验证
		if(!skillValid(session, skillId)) return;
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		if(skillConfig.getSummonBoss() != 0) {
			summonBoss(session, skillId);
			return;
		}
		if(skillConfig.getScope() != 0) {
			session.sendMessage("此技能为单个攻击，请指定要攻击的敌人");
			return;
		}
		boolean attacked = false;
		List<Monster> monsters = player.getScene().getMonsters();
		for(Monster monster : monsters) {
			int isalive = monster.canAttack( skillId, player);
			//怪物已经死亡时，不能攻击
			//if(!monst.isAlive()) {
			if(isalive == -1) {
				continue;
			}
			if(skillConfig.getAttack() < 1 && skillConfig.getAttack()>0) continue;  //释放技能时，只有攻击大于0才可以
			//记录具有持续效果的攻击技能的使用时间
			player.addContinueAtttib(skillId, monster);		
			doAttack(session, monster, isalive);
			//攻击怪物，就要被怪物攻击。只有攻击大于0才会收到怪物攻击
			if(monster.getHp() > 0) {
				player.getScene().addAttackPlayer(monster.getMonstId(), player);			
			}
			attacked = true;
		}
		//记录具有持续恢复血量和防御的技能使用时间
		player.addContinueRecov(skillId);
		if(!attacked) {
			session.sendMessage("所有怪物都死亡，不能攻击");
			return;
		}
		//更新技能和武器
		updateWeapon(player, skillId);

	}
	
	/**
	 * 召唤师召唤boss
	 * 打怪物
	 * @param session
	 * @param skillId
	 */
	public void summonBoss(Session session, int skillId) {
		//召唤师也要被怪物攻击
	    Player player = session.getPlayer();
	    for(Monster m : player.getScene().getMonsters()) {
			player.getScene().addAttackPlayer(m.getMonstId(), player);	
		}		
		System.out.println("进行召唤");
		SkillConfig skConfig = Context.getSkillParse().getSkillConfigById(skillId);
		Monster m = new Monster(skConfig.getSummonBoss());
		SummonBoss summonBoss = new SummonBoss(m, player.getScene().getMonsters(), player);
		summonBoss.exe(1, "summon" + m.getMonstId() + player.getId());
		session.sendMessage("召唤成功");
		//更新技能和武器
		updateWeapon(player, skillId);
	}
	
	/**
	 * 攻击怪物，
	 * @param session
	 * @param skillId：技能id，mId：怪物id
	 */
	public void doAttack(Session session, Monster monst, int isalive) {
		Player player = session.getPlayer();
		
		if(isalive == 1) {
			player.getScene().deleteAttackMonst(monst);
			//击杀怪物/boss获得相应奖励
			Context.getAwardService().obtainAward(player, monst);
			//杀掉怪物就需要验证是否达成某成就
			Achievement.getService(player, "KILLM", monst.getMonstId());
			//需要广播给当前场景的所有玩家
			String mesg = monst.getName() + "被玩家[" + player.getName() + "]击杀";
			BroadcastService.broadInScene(session, mesg);
			return;
		}
		//播放怪物血量
		String msg = monst.getName() + "被玩家[" + player.getName() +"]攻击，剩余血量为：" + monst.getHp();
		BroadcastService.broadInScene(session, msg);
	}
	
	
	
	/**
	 * 攻击玩家
	 * @param session 攻击者的session
	 * @param skillId 使用的技能的id
	 * @param tpName 目标玩家名
	 */
	public void attackPlayer(Session session, int skillId, String tpName) {
		Player player = session.getPlayer();
		Player tPlayer = player.getScene().getPlayerByName(tpName);
		//验证对手的pk对象是否是自己
		if((player.getPkTarget()==null) || (tPlayer.getPkTarget()==null) ||!player.getPkTarget().equals(tpName) || !tPlayer.getPkTarget().equals(player.getName())) {
			//System.out.println(player.getPkTarget() + ", " + tPlayer.getPkTarget());
			session.sendMessage("不是pk对象，不能攻击");
			return;
		}
		
		if(!skillValid(session, skillId)) return;
		
		if(!tPlayer.isAlive()) {
			session.sendMessage("玩家已死亡，不能攻击");
		}
		
		//使用技能后
		
		updateWeapon(player, skillId);
		
		//记录具有持续效果的技能的使用时间
		player.addContinueAttib(skillId, tPlayer);

		
		//对目标的伤害
		int hurt = player.AllAttack(skillId);
		//减少目标玩家血量，并返回实际减少的血量
		int reduce = tPlayer.attackPlayerReduce(hurt);  
		//目标玩家剩余血量
		int restHp = tPlayer.getHp();
		session.sendMessage("击中玩家【" + tpName + "】减少血量" + reduce +" 剩余血量:" + restHp);
		
		if(restHp <= 0) {
			//玩家死亡
			tPlayer.setHp(0);
			session.sendMessage("玩家【" + tpName + "】已死亡！");
			//tPlayer.getSession().sendMessage("您已被玩家【" + player.getName() + "】杀死！");
			tPlayer.setAlive(false);
			return;
		}
		tPlayer.setHp(restHp);
		tPlayer.getSession().sendMessage("您被玩家【" + player.getName() +"】击中，减少血量 :" + reduce
		                                 + "剩余血量: " + restHp);
	}
	
	/**
	 * 使用技能后，更新武器耐久，更新技能cd时间
	 * @param player
	 * @param skillId 技能id
	 */
	public void updateWeapon(Player player, int skillId) {
		System.out.println("---------updatewapon更新技能和武器");
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		//该技能对应的武器的物品id
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//减少法力
		int restMp = player.getMp() - skillConf.getMp();
		player.setMp(restMp);
		
		//更新技能cd
		player.updateCdById(skillId);
		System.out.println("-----=----更新cd时间---" + player.getCdTimeByid(skillId));
		
		//更新武器耐久度. wid==0表示此技能不需要武器
		if(wId != 0) player.minusContT(wId);

	}
	
	/**
	 * 使用技能前，验证玩家技能是否可以使用
	 * @param session
	 * @param skillId  技能id
	 * @return
	 */
	public boolean skillValid(Session session, int skillId) {
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		Player player = session.getPlayer();
        //判断玩家是否能使用技能
		if(!player.canUseSkill()) {
			session.sendMessage("受到敌人的技能影响，现在还不能使用技能");
			return false;
		}
		//判断玩家是否拥有这个技能,且有相应的武器
		if(!player.hasSkill(skillId)) {
			session.sendMessage("没有这个技能");
			return false;
		}
		
		//该技能对应的武器的物品id
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//判断该技能需要的武器是否存在
		if(wId!=0 &&!player.contEquip(wId)) {
			session.sendMessage("没有装备对应的武器，不能使用");
			return false;
		}
		//判断该技能所对应的武器的耐久度
		if(wId!=0 && player.restContiT(wId) < 1) {
			session.sendMessage("武器的耐久度过低，请修理后再使用");
			return false;
		}
		
		//判断该技能是否cd完
		if(!cdOut(player, skillId)) {
			session.sendMessage("技能没有冷却完，不能使用");
			return false;
		}
		
		//判断是否有足够的法力
		if(player.getMp() < skillConf.getMp()) {
			session.sendMessage("法力不够");
			return false;
		}

		return true;
	}
	
	/**
	 * 技能是否冷却完
	 * sId：技能id
	 * @return
	 */
	public boolean cdOut(Player player, int sId) {
	    long pTime = player.getCdTimeByid(sId).getTime();
	    long nTime = System.currentTimeMillis();
	    long diff = nTime - pTime;
	    long di = Context.getSkillParse().getSkillConfigById(sId).getCd() * 1000; //需要扩大1000倍，化为毫秒
	    System.out.println(pTime + "=--" + nTime +", "+ diff+ ", cd=" + di);
	    if(di > diff)
	    	return false;
	    return true;
	}
	


	
	
}
