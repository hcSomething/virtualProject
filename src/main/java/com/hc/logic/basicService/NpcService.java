package com.hc.logic.basicService;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.TaskConfig;
import com.hc.logic.creature.Player;

@Component
public class NpcService {
	
	/**
	 * 解析npc相关的命令
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length < 2 || args.length > 4) {
			session.sendMessage("命令参数错误");
			return;
		}
		if(!OrderVerifyService.isDigit(args[1])) {
			session.sendMessage("命令参数错误");
			return;
		}
		//验证npc是否在同一场景，当在副本中时也没有这个npc
		if(!isOnScene(session, Integer.parseInt(args[1]))) {
			return;
		}
		if(args.length == 3) {
			if(OrderVerifyService.isDigit(args[2])) {
				buyGoodsFromNpc(session, args);
				return;
			}
			enterCopys(session, args);
			return;
		}
		if(args.length == 2) {
			introduce(session, Integer.parseInt(args[1]));
			return;
		}
		//参数长度为4的命令
		if(!OrderVerifyService.isDigit(args[3])) {
			session.sendMessage("命令参数错误");
			return;
		}
		if(args[2].equals("r")) { //玩家接任务
			receiveTask(session, args);
		}
		if(args[2].equals("c")) {  //玩家提交任务
			completeTask(session, args);
		}
	}
	
	/**
	 * npc和player是否在同一场景
	 * 参数 id：npc的id
	 */
	public boolean isOnScene(Session session, int id) {
		Player player = session.getPlayer();
		if(player.getSceneId() == 0) {
			session.sendMessage("现在在副本中，没有npc");
			return false;
		}
		List<Integer> nSId = Context.getSceneParse().getSceneById(session.getPlayer().getSceneId()).getNpcs();
		for(int ii : nSId) {
			if(ii == id)
				return true;
		}
		session.sendMessage("当前场景中没有该npc");
		return false;
	}

	/**
	 * 通过npc进入副本
	 * @param session
	 * @param args: npc npcId c
	 */
	public void enterCopys(Session session, String[] args) {
		if(!Context.getCopyService().canEnterCopy(session.getPlayer())) return;
		int npcId = Integer.parseInt(args[1]);
		int copyId = Context.getSceneParse().getNpcs().getNpcConfigById(npcId).getCopyId();
		Context.getCopyService().enterCopy(copyId, session.getPlayer(), session, 0);
	}
	
	/**
	 * 从npc处卖东西
	 * @param session
	 * @param args: npc npcId 物品id。默认一次只能买一个
	 */
	public void buyGoodsFromNpc(Session session, String[] args) {
		int npcid = Integer.parseInt(args[1]);
		int goodsid = Integer.parseInt(args[2]);
		if(Context.getSceneParse().getNpcs().getNpcConfigById(npcid).getGoodId() != goodsid) {
			session.sendMessage("没有相应的物品可以卖");
			return;
		}
		boolean secc = Context.getStore().buyGood(session.getPlayer(), goodsid, 1);  //在npc处买东西，默认一次只能买一个
		if(secc) {
			session.sendMessage("购买成功");
		}else {
			session.sendMessage("购买失败，请检查是否有足够的金币");
		}
	}
	/**
	 * 完整的介绍当前npc
	 * @param id npc的id
	 */
	public void introduce(Session session, int id) {
		NpcConfig nc = Context.getSceneParse().getNpcs().getNpcConfigById(id);
		TaskConfig rtc = Context.getTaskParse().getTaskConfigByid(nc.getReceive());
		TaskConfig ctc = Context.getTaskParse().getTaskConfigByid(nc.getCheckout());
		StringBuilder sb = new StringBuilder();
		sb.append(nc.getName() + ": " + nc.getDescription() + "\n");
		if(rtc != null) {
			sb.append("可发放的任务：" + rtc.getName() + "\n");
		}else {
			sb.append("没有可发放的任务" + "\n");
		}
		if(ctc != null) {
			sb.append("可验收的任务：" + ctc.getName());
		}else {
			sb.append("没有可验收的任务");
		}
		if(nc.getGoodId() != 0) {
			String name = Context.getGoodsParse().getGoodsConfigById(nc.getGoodId()).getName();
			sb.append("\n可卖的物品：" + name);
		}
		if(nc.getCopyId() > 0) {
			String name = Context.getCopysParse().getCopysConfById(nc.getCopyId()).getName();
			sb.append("\n可进入的副本：" + name +"【" + nc.getCopyId() + "】");
		}
		session.sendMessage(sb.toString());
	}
	
	/**
	 * 接取当前npc的任务
	 * @param session
	 * @param args: npc npcId 任务id
	 */
	public void receiveTask(Session session, String[] args) {
		Player player = session.getPlayer();
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(Integer.parseInt(args[1]));
		if(npcC.getReceive() != Integer.parseInt(args[3])) {
			session.sendMessage("当前npc没有这个任务");
			return;
		}
		boolean sec = player.getPlayerTasks().addTask(Integer.parseInt(args[3]));
		if(sec) session.sendMessage("接受任务成功");
		else session.sendMessage("接受任务失败，以前已经完成过或者条件不符合");
	}
	
	/**
	 * 提交任务，获得奖励
	 * 需要检查任务是否完成
	 * @param session
	 * @param args：npc npcId 任务id
	 */
	public void completeTask(Session session, String[] args) {
		Player player = session.getPlayer();
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(Integer.parseInt(args[1]));
		if(npcC.getCheckout() != Integer.parseInt(args[3])) {
			session.sendMessage("当前npc不能接收这个任务");
			return;
		}
		boolean sec = player.getPlayerTasks().getTaskAward(player, Integer.parseInt(args[3]));
		if(sec) {
			session.sendMessage("\n 任务完成！");			
		}else {
			session.sendMessage("任务未完成！");
		}
	}
}
