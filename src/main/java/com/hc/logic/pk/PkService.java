package com.hc.logic.pk;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

@Component
public class PkService {

	private final String[] PK_INIT = {"pk", "nu","我想和你pk，敢来吗-)"};
	private final String[] PK_ACPT = {"pk", "nu", "我接受你的pk邀请了，来吧"};
	
	/**
	 * 解析命令
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length < 2 || args.length > 3) {
			session.sendMessage("参数格式不正确");
			return;
		}
		if(args.length == 2) {
			if(args[1].equals("g")) {
				//认输
				giveUp(session);
			}else {
				//发起pk邀请
				initPK(session, args);
			}
		}else if(args.length == 3) {
			//接受pk邀请
			acceptPK(session, args);
		}
	}
	
	/**
	 * 发起pk
	 * @param session
	 * @param args: pk 目标玩家名
	 */
	public void initPK(Session session, String[] args) {
		String tName = args[1];
		Player player = session.getPlayer();
		Player tPlayer = player.getScene().getPlayerByName(tName);
		if(tPlayer == null) {
			session.sendMessage("目标玩家不在当前场景不能pk！");
			return;
		}
		if(tPlayer.isInPK() || player.isInPK()) {
			session.sendMessage("您在pk状态下，或对方在pk状态下，不能pk！");
			return;
		}
		Context.getChatService().privateChat(session, tPlayer.getId(), PK_INIT);
		player.setPkTarget(tName);
	}
	
	/**
	 * 接受pk邀请
	 * @param session
	 * @param args： pk 1  目标玩家名
	 */
	public void acceptPK(Session session, String[] args) {
		Player player = session.getPlayer();
		Player tPlayer = player.getScene().getPlayerByName(args[2]);
		if(tPlayer == null) {
			session.sendMessage("这个玩家不在当前场景中，请检查玩家名是否输入正确");
			return;
		}
		if(tPlayer.isInPK()) {
			session.sendMessage("对方已经在pk中，接受pk失败");
			return;
		}
		if((tPlayer.getPkTarget() == null) || (!tPlayer.getPkTarget().equals(player.getName()))) {
			session.sendMessage("要先邀请，才能接受");
			return;
		}
		player.setInPK(true);
		tPlayer.setInPK(true);
		Context.getChatService().privateChat(session, tPlayer.getId(), PK_ACPT);
		player.setPkTarget(tPlayer.getName());
		tPlayer.setPkTarget(player.getName());
	}
	
	/**
	 * 认输。
	 * @param session : 一般是认输方的session
	 */
	public void giveUp(Session session) {
		Player failP = session.getPlayer();
		Player winP = failP.getScene().getPlayerByName(failP.getPkTarget());
		session.sendMessage("您已输掉这场pk");
		winP.getSession().sendMessage("对方已输，您在这场pk中胜利了");
		Context.getTwoPlayerPK().geReward(winP, failP);
		winP.setInPK(false);
		winP.setPkTarget(null);
		failP.setInPK(false);
		failP.setPkTarget(null);
	}
	
	/**
	 * 玩家死亡后，就输了
	 * @param player 输的玩家
	 */
	public void deadFailed(Player player) {
		giveUp(player.getSession());
	}
}
