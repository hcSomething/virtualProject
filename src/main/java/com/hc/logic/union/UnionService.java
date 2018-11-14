package com.hc.logic.union;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;

@Component
public class UnionService {

	/**
	 * 解析命令
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length < 2|| args.length > 4) {
			session.sendMessage("命令参数错误");
			return;
		}
		if(args.length == 2) {  //退出工会
			if(args[1].equals("d")) {
				quitUnion(session);
				return;
			}
			if(args[1].equals("f")) {
				allCandidate(session);
				return;
			}
			if(args[1].equals("s")) {
				unionState(session);
				return;
			}
			if(args[1].equals("e")) {
				allUnion(session);
				return;
			}
			session.sendMessage("命令参数错误");
			return;
		}
		if(args.length == 4) { //从工会仓库获得物品
			if(!args[2].equals("gold") && !OrderVerifyService.isDigit(args[2])) {
				session.sendMessage("命令参数错误");
				return;
			}
			if(!OrderVerifyService.isDigit(args[3])) {
				session.sendMessage("命令参数错误");
				return;
			}
			getGoodsFromUnion(session, args);
			return;
		}
		if(args.length != 3) {
			session.sendMessage("命令参数错误");
		}
		
		if(args[1].equals("c")) {  //创建工会
			establishUnion(session, args);
			return;
		}
		if(args[1].equals("m")) {  //解散工会
			dissolveUnion(session, args);
			return;
		}
		if(args[1].equals("r")) {  //申请加入公会
			enterUnion(session, args);
			return;
		}
		if(args[1].equals("a")) {  //同意加入公会申请
			agreeEnter(session, args);
			return;
		}
		if(args[1].equals("j")) {  //拒绝加入公会申请
			rejectEnter(session, args);
			return;
		}
		if(args[1].equals("t")) { // 提升玩家的公会职位
			upTitle(session, args);
			return;
		}
		if(args[1].equals("d")) {  //降低玩家的公会职位
			downTitle(session, args);
			return;
		}
		if((OrderVerifyService.isDigit(args[1]) || args[1].equals("gold")) && OrderVerifyService.isDigit(args[1])) {
			donateGoods(session, args);  //捐献物品/金币到工会仓库
			return;
		}
		session.sendMessage("命令参数错误");
	}
	
	/**
	 * 创建工会
	 * @param session
	 * @param args: union c 工会名
	 */
	public void establishUnion(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() != null) {
			session.sendMessage("创建工会失败，你以及在一个工会中");
			return;
		}
		boolean sess = Context.getWorld().createUnion(args[2], player.getName());
		if(sess) {
			player.enterUnion(args[2], 4);
			session.sendMessage("创建工会成功");
		}else {
			session.sendMessage("创建工会失败，可能是因为重名");
		}
	}
	/**
	 * 获得所有公会的信息
	 * @param session
	 */
	public void allUnion(Session session) {
		List<UnionEntity> ulist = Context.getWorld().getUnionEntity();
		StringBuilder sb = new StringBuilder();
		sb.append("所有的公会如下：\n");
		for(UnionEntity ue : ulist) {
			String tn = Context.getUnionParse().getUCByid(ue.getGrade()).getName();
			sb.append(ue.getName() + " " + tn + "\n");
		}
		session.sendMessage(sb.toString());
	
	}
	
	/**
	 * 解散工会
	 * @param session
	 * @param args: union m 工会名
	 */
	public void dissolveUnion(Session session, String[] args){
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("不存在这个工会");
			return;
		}
		boolean secc = player.getUnion().dissolveUnion(player.getUnionName(), player.getName());
		if(secc) {
			session.sendMessage("解散工会成功");
		}else {
			session.sendMessage("解散工会失败，你不是这个工会的会长");
		}
	}
	
	/**
	 * 申请加入工会
	 * @param session
	 * @param args： union r 工会名
	 */
	public void enterUnion(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() != null) {
			session.sendMessage("你已经在工会中，不能加入工会");
			return;
		}
		UnionEntity ue = Context.getWorld().getUnionEntityByName(args[2]);
		if(ue == null) {
			session.sendMessage("不存在这个工会");
			return;
		}
		boolean secc = ue.getUnion().enterUion(args[2], player);
		if(secc) {
			session.sendMessage("申请加入工会成功，等待工会审查");
		}else {
			session.sendMessage("加入工会失败, 工会不存在，或者工会已满");
		}
	}
	
	/**
	 * 申请退出
	 * @param session
	 */
	public void quitUnion(Session session) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("你不在工会中，不需要退出");
			return;
		}
		boolean secc = player.getUnion().quitUnion(player);
		if(secc) {
			session.sendMessage("退出工会成功");
		}else {
			session.sendMessage("退出工会失败，只剩您一个人的工会不能退出，您是会长也不能退出");
		}
	}
	
	/**
	 * 允许加入工会
	 * @param session
	 * @param args: union a 玩家名
	 */
	public void agreeEnter(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean secc = player.getUnion().agreeEnter(player, args[2]);
		if(secc) {
			session.sendMessage("允许加入工会成功");
			Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
			if(tp != null ) tp.getSession().sendMessage("恭喜你已加入工会[" + player.getUnionName() + "]");
		}else {
			session.sendMessage("加入工会失败，可能玩家已经加入别的工会，或者工会已满员");
		}
	}
	
	/**
	 * 拒绝加入工会申请
	 * @param session
	 * @param args: union j 玩家名
	 */
	public void rejectEnter(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean sec = player.getUnion().rejectEnter(player, args[2]);
		if(!sec) {
			session.sendMessage("没有这个申请人");
			return;
		}
		session.sendMessage("已拒绝玩家[" + args[2] +"]的加入申请");
		Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
		if(tp != null)
			tp.getSession().sendMessage("工会[" + player.getUnionName() + "]已拒绝您的申请");
	}
	
	/**
	 * 提升下级的职位
	 * @param session
	 * @param args:  union t 被提升者名
	 */
	public void upTitle(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean secc = player.getUnion().titleUp(player, args[2], 1);
		if(secc) {
			PlayerEntity pe = Context.getWorld().getPlayerEntityByName(args[2]);
			int tit = pe.getUnionTitle();
			session.sendMessage("提升成功，被提升者当前等级为：" + Context.getTitlParse().getTCByid(tit).getName());
			Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
			if(tp != null) {
				tp.getSession().sendMessage("您的工会职位提升了，现在您的职位是：" + Context.getTitlParse().getTCByid(tit).getName());
			}
		}else {
			session.sendMessage("提升工会等级失败。肯对方已不在工会里，或者您的职位和他和他一样或比他小");
		}
	}
	
	/**
	 * 降低玩家的工会等级
	 * @param session
	 * @param args: union d 被降低者名
	 */
	public void downTitle(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean secc = player.getUnion().titleUp(player, args[2], -1);
		if(secc) {
			PlayerEntity pe = Context.getWorld().getPlayerEntityByName(args[2]);
			int tit = pe.getUnionTitle();
			session.sendMessage("降职成功，被降职者当前等级为：" + Context.getTitlParse().getTCByid(tit).getName());
			Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
			if(tp != null) {
				tp.getSession().sendMessage("您的工会职位降低了，现在您的职位是：" + tp.getUnionTitle());
			}
		}
	}
	
	/**
	 * 捐赠物品/金币到工会仓库
	 * @param session
	 * @param args: union 物品id 数量
	 *                    gold   数量
	 */
	public void donateGoods(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("您还不在工会中,请先加入工会");
			return;
		}
		boolean secc = player.getUnion().donateGoods(player, args[1], Integer.parseInt(args[2]));
		if(secc) {
			session.sendMessage("捐赠成功");
		}else {
			session.sendMessage("捐赠失败，要么是仓库没有那么多空地方，要么是您没有那么多物品");
		}
	}
	
	/**
	 * 查看所有申请加入者
	 * @param session
	 */
	public void allCandidate(Session session) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("您还不在工会中,请先加入工会");
			return;
		}
		List<String> names = player.getUnion().getCandidate();
		if(names.size() > 0) session.sendMessage("所有申请人如下：" + names.toString());
		else session.sendMessage("现在还没有申请人");
	}
	
	/**
	 * 从工会仓库中取物品
	 * @param session
	 * @param args： union g 物品id 数量
	 *                       gold  数量
	 */
	public void getGoodsFromUnion(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("您还不在工会中,请先加入工会");
			return;
		}
		boolean sec = player.getUnion().obtainGoods(player, args[2], Integer.parseInt(args[3]));
		if(sec) {
			session.sendMessage("从仓库中获得物品成功");
		}else {
			session.sendMessage("获取物品失败，可能是仓库中没有这么多物品，或者你的背包容量不够");
		}
	}
	
	/**
	 * 查看公会状态
	 * @param session
	 */
	public void unionState(Session session) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("您还不在工会中,请先加入工会");
			return;
		}
		player.getUnion().unionState(session);
	}
	
	/**
	 * 验证是否在工会中，以及职位是否符合要求
	 * @param session
	 * @param player
	 * @return
	 */
	private boolean unionATitle(Session session, Player player) {
		if(player.getUnion() == null) {
			session.sendMessage("您还不在工会中,请先加入工会");
			return false;
		}
		if(player.getUnionTitle() < 3) {
			session.sendMessage("您的职位太低，没有这项操作的权限");
			return false;
		}
		return true;
	}
	
}
