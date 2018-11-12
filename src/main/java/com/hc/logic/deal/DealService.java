package com.hc.logic.deal;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.creature.Player;

@Component
public class DealService {

	private final String[] DEAL_INIT = {"deal", "nu","我想和你进行交易，可以吗？"};
	private final String[] DEAL_ACC = {"deal", "ac","我已经同意你的交易请求，可以开始进行交易了，请先发送你想交易的物品"};
	
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
			if(args[1].equals("b")) {
				//同意进行交易
				acceptDeal(session, args);
				return;
			}else if(args[1].equals("gold") || OrderVerifyService.isDigit(args[1])) {
				//展示物品
				if(!OrderVerifyService.isDigit(args[2])) {
					session.sendMessage("命令参数不正");
					return;
				}
				showGoods(session, args);
				return;
			}
			session.sendMessage("命令参数不正确");
			return;
		}
		if(args[1].equals("a")) {
			//同意交换
			hopeDeal(session);
		}else if(args[1].equals("r")) {
			//拒绝交换
			notHopeDeal(session);
		}else if(OrderVerifyService.isDigit(args[1]) && Integer.parseInt(args[1]) == 0) {
			//结束交易
			stopDeal(session);
		}else {
			//发起交易
			initDeal(session, args);
		}
	}
	
	/**
	 * 发起交易
	 * @param session
	 * @param args: deal 目标玩家名
	 */
	public void initDeal(Session session, String[] args) {
		Player player = session.getPlayer();
		String tName = args[1];  //目标玩家名
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName); //目标玩家
		if(player.getDeal() != null) {
			session.sendMessage("您已经在交易状态，请先结束交易状态，再发起交易请求");
			return;
		}
		if(tPlayer == null) {
			session.sendMessage("目标玩家不在线，不能发起交易");
			return;
		}
		if(tPlayer.getSceneId() != player.getSceneId()) {
			session.sendMessage("发起交易失败，只能和同一场景中的玩家进行交易");
			return;
		}
		if(tPlayer.getDeal() != null) {
			session.sendMessage("对方在交易中，不能发起交易请求");
			return;
		}
		player.beginDeal(tName);
		Context.getChatService().privateChat(session, tPlayer.getId(), DEAL_INIT);
	}
	
	/**
	 * 同意进行交易
	 * @param session
	 * @param args：deal 1 发起者名字
	 */
	public void acceptDeal(Session session, String[] args) {
		Player player = session.getPlayer();
		String spName = args[2];  //发起者名字
		Player sponsor = Context.getOnlinPlayer().getPlayerByName(spName);
		if(sponsor == null) {
			session.sendMessage("发起交易的玩家可能断线了，进行交易失败");
			return;
		}
		if(player.getDeal() != null) {
			session.sendMessage("您处在交易状态，不能同意交易请求");
			return;
		}
		Deal ddel = sponsor.getDeal();
		if(ddel == null) {
			session.sendMessage("对方已取消交易，交易停止");
			return;
		}
		if(!isSponsor(session, sponsor)) return;
		player.accDeal(ddel);
		player.getDeal().setSpName(spName);
		session.sendMessage("交易配对成功，你们可以开始交易了！现在就可以展示物品了");
		sponsor.getSession().sendMessage("对方同意和你进行交易，交易开始，可以开始展示物品了！");
	}
	
	/**
	 * 放入进行交易的物品
	 * @param session
	 * @param args: deal 物品id 数量
	 *              deal gold   数量
	 */
	public void showGoods(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getDeal() == null) {
			session.sendMessage("需要先进行交易配对，才能进行物品展示");
			return;
		}
		String counterp = player.getDeal().getCounter(player.getName());
		Player counterPlayer = Context.getOnlinPlayer().getPlayerByName(counterp);  //交易方
		//判断是否处于交易状态
		if(player.getDeal().getSpName() == null || player.getDeal().getTpName() == null) {
			session.sendMessage("您还未进入交易状态，不能放置交易物品！");
			return;
		}
		if(counterPlayer == null) {
			session.sendMessage("对方已经不在线，交易停止");
			stopDeal(player, counterPlayer);
			return;
		}
		if(!Context.getGoodsService().goodsEnough(player.getPlayerEntity(), args[1], Integer.parseInt(args[2]))) {
			session.sendMessage("没有这个物品，或者数量不够");
			return;
		}
		session.sendMessage("放置物品成功。您希望交易的物品为：");
		String msg = "";
		if(!OrderVerifyService.isDigit(args[1])) {
			msg = "金币  " + args[2] + "个";
		}else { 
			String gName = Context.getGoodsParse().getGoodsConfigById(Integer.parseInt(args[1])).getName();
			msg = gName + " " +args[2] + "个";
		}
		session.sendMessage(msg);	
		counterPlayer.getSession().sendMessage("对方希望交易的物品为: " + msg);
		player.getDeal().showGoods(player.getName(), args);
	}
	
	/**
	 * 对双方进行交易的物品满意，同意交换
	 * @param session
	 */
	public void hopeDeal(Session session) {
		Player player = session.getPlayer();
		if(player.getDeal() == null) {
			session.sendMessage("需要先进行交易配对，才能进行交易");
			return;
		}
		String countName = player.getDeal().getCounter(player.getName());
		Player countPlayer = Context.getOnlinPlayer().getPlayerByName(countName);
		if(countPlayer == null) {
			session.sendMessage("对方断线了，交易停止");
			stopDeal(player, countPlayer);
			return;
		}
		if(!player.getDeal().isReadyVerify()) {
			session.sendMessage("请先完成物品展示，再进行确认");
			return;
		}
		player.getDeal().hopeDeal(player.getName());
		session.sendMessage("您已经同意进行交换了");
		countPlayer.getSession().sendMessage("玩家[" +player.getName()+"]已经同意交换了");
		
		//当所有玩家都同意后，自动进行交换。
		if(player.getDeal().isReadyChange()) {
			player.getDeal().exchangeGoods();
			session.sendMessage("交易成功");
			countPlayer.getSession().sendMessage("交易成功");
		}
	}
	
	/**
	 * 看不上对方的物品，拒绝交易
	 * @param session
	 */
	public void notHopeDeal(Session session) {
		Player player = session.getPlayer();
		Deal deal = player.getDeal();
		if(deal == null) {
			session.sendMessage("请先放置物品，才能取消交易");
			return;
		}
		if(!deal.isReadyVerify()) {
			session.sendMessage("请先完成物品展示，才可以拒绝交易");
			return;
		}
		deal.notHopeDeal(player.getName());  //只要一方拒绝交易，自动清空交易内容和交易状态
		session.sendMessage("您已经拒绝了此次交易");
		String counName = deal.getCounter(player.getName());
		Player countp = Context.getOnlinPlayer().getPlayerByName(counName);
		if(countp == null) return;
		countp.getSession().sendMessage("对方对此次交易不满意，已经拒绝了交易，可以重新展示物品");
	}
	
	/**
	 * 停止交易
	 * @param session
	 */
	public void stopDeal(Session session) {
		Player player = session.getPlayer();
		Deal deal = player.getDeal();
		if(deal == null) {
			session.sendMessage("还没有进入交易，也就不能停止交易");
			return;
		}
		player.stopDeal();
		session.sendMessage("退出交易成功");
		String countName = deal.getCounter(player.getName());
		if(countName != null) {
			Player cp = Context.getOnlinPlayer().getPlayerByName(countName);
			if(cp != null) {
				cp.stopDeal();
				cp.getSession().sendMessage("对方退出了交易");
			}
		}
	}
	
	/**
	 * 停止交易
	 * @param p1
	 * @param p2
	 */
	public void stopDeal(Player p1, Player p2) {
		if(p1 != null) p1.stopDeal();
		if(p2 != null) p2.stopDeal();
	}
	

	
	
	/**
	 * 是否是发起交易者
	 * @param spName
	 * @return
	 */
	private boolean isSponsor(Session session, Player sponsor) {
		Player pp = session.getPlayer();
		String counterName = sponsor.getDeal().getTpName();
		if(!pp.getName().equals(counterName)) {
			session.sendMessage("需要先发起交易请求，才能够同意交易");
			return false;
		}
		return true;
	}
	
	
}
