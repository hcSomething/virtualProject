package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

/**
 * 组队
 * @author hc
 *
 */
@Component
public class Party {
	
	private final String[] TEAM_INIT = {"team", "nu","我在邀请你组队，请尽快同意"};
	private final String[] TEAM_ACC = {"team", "nu","我已经同意你的组队邀请了"};

	/**
	 * 解析组队命令
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length != 3) {
			session.sendMessage("参数错误");
			return;
		}
		if(args[1].equals("1")) {
			//同意组队
			acceptParty(session, args);
			return;
		}else if(args[1].equals("n")) {
			//拒绝组队
			rejectParty(session, args[2]);
		}else {
			//发起组队
			creatParty(session, args);
		}
	}
	
	/**
	 * 发起组队
	 * @param session
	 * @param args: group name1 name2
	 */
	public void creatParty(Session session, String[] args) {
		List<Player> players = new ArrayList<>();
		Player player = session.getPlayer();  //发起者
		for(int i = 1; i < args.length ; i++) {
			Player p = Context.getOnlinPlayer().getPlayerByName(args[i]);
			if(p == null) {
				rejectParty(session, session.getPlayer().getName());
				session.sendMessage("组队失败，不能邀请不在线的玩家【" + args[i] + "】组队！");
				return;
			}
			if(p.getSceneId() != player.getSceneId()) {
				rejectParty(session, session.getPlayer().getName());
				session.sendMessage("组队失败，只能邀请同一场景中的玩家进行组队");
				return;
			}
			if(player.getName().equals(args[i])) {
				session.sendMessage("不能和自己组队！");
				return;
			}
			players.add(p);
		}
		for(Player pp : players) {
			Context.getChatService().privateChat(session, pp	.getId(), TEAM_INIT);
		}
		player.clearTeammate();
		player.addTeammate(players);
		System.out.println("------发起组队------" + players);
		//session.getPlayer().setInParty(true);		
		player.setSponserNmae(player.getName());
	}
	
	/**
	 * 同意组队
	 * @param session
	 * @param args: group 1 name。name是发起者的名字
	 */
	public void acceptParty(Session session, String[] args) {
		String tName = args[2];
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName);
		if(tPlayer == null) {
			session.sendMessage("目标玩家不在线，不能组队");
			return;
		}
		if(!tPlayer.teamContain(session.getPlayer().getName())) {
			session.sendMessage("需要邀请才能同意，或者队伍已取消");
			return;
		}
		if(!isSponser(session, tName)) {
			return;
		}
		Context.getChatService().privateChat(session, tPlayer.getId(), TEAM_ACC);
		session.getPlayer().setSponserNmae(tName);		
		tPlayer.acTeam(session.getPlayer().getName());
	}
	
	/**
	 * 拒绝组队
	 * @param session
	 * @param args：group r 发起者名。
	 */
	public void rejectParty(Session session, String tName) {
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName);
		if(!isSponser(session, tName)) return;
		List<String> pNames = tPlayer.getTeammate();
		for(String pn : pNames) {
			//通知所有已经同意组队的玩家取消组队
			Player p = Context.getOnlinPlayer().getPlayerByName(pn);
			p.getSession().sendMessage("组队取消了");
			p.setSponserNmae(null);
		}
		//清空发起者的组队状态
		tPlayer.getSession().sendMessage("由于有玩家拒绝组队邀请或者邀请的玩家不在线，本次组队失败！");
		tPlayer.clearTeammate();
		tPlayer.setSponserNmae(null);
	}
	

	/*
	 * 验证tName是否是发起者的名字
	 */
	private boolean isSponser(Session session, String tName) {
		//验证目标玩家是否是发起者
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName);  //发起组队的人一定在线
		if(tPlayer == null) {
			session.sendMessage("玩家不在线，请检查参数是否正确，也可能取消组队了");
			return false;
		}
		if(!tPlayer.contPlaName(session.getPlayer().getName())) {
			session.sendMessage("需要对方邀请才能同意组队，或者输入的玩家名错误");
			return false;
		}
		return true;
	}
}
