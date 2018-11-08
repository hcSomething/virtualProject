package com.hc.logic.chat;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.dao.impl.UpdateTask;
import com.hc.logic.domain.EmailEntity;
import com.hc.logic.domain.PlayerEntity;

@Component
public class EmailService {
	
	/**
	 * 解析邮箱系统命令
	 * @param session
	 * @param args
	 */
	public void descOrder(Session session, String[] args) {
		Email email = session.getPlayer().getEmail();
		if(args.length == 1){
			session.sendMessage("欢迎进入邮箱");
			lookEmails(session, 1);
			return;
		}
		if((args.length > 1 && email.getEmailPage() == 0) ) {
			session.sendMessage("请先进入邮箱系统");
			return;
		}else if(args.length == 2) {
			if(!pisDigit(args[1])) {
				session.sendMessage("参数类型错误，请重新输入");
				return;
			}
			if(Integer.parseInt(args[1]) == 0) {
				email.setEmailPage(0);
				session.sendMessage("退出邮箱系统");
				return;
			}
			//查看邮件列表
			lookEmails(session, Integer.parseInt(args[1]));
		}else if(args.length == 3 ) {
			if(!pisDigit(args[2])) {
				session.sendMessage("参数类型错误，请重新输入");
				return;
			}
			if(args[1].equals("r")) {
				//阅读邮件内容
				readEmail(session, Integer.parseInt(args[2]));
			}else if(args[1].equals("d")){
				//删除邮件
				delEmail(session, Integer.parseInt(args[2])); 
			}
		}else if(args.length > 3){
			//发送邮件
			sendNormalEmail(session, args[1], orderContent(args));
		}
		
	}
	
	/**
	 * 查看邮箱的某一页
	 * 邮箱中的邮件是倒序的
	 * @param session
	 * @param page
	 */
	public void lookEmails(Session session, int page) {
		Email emails = session.getPlayer().getEmail();
		if(!emails.isValiedPage(page)) {
			session.sendMessage("跳转失败！邮箱没有这么页面，或者玩家不在相邻页面");
			return;
		}
		String dis = emails.displayEmail(page);
		emails.setEmailPage(page);
		session.sendMessage(dis);
	}
	
	/**
	 * 查看当前页面的第n条邮件
	 * 如果这条邮件是道具邮件，则获得道具，并删除邮件
	 * @param session
	 * @param index 邮件编号
	 */
	public void readEmail(Session session, int index) {
		Email emails = session.getPlayer().getEmail();
		if(emails.getEmailPage() == 0) {
			session.sendMessage("当前还不再邮箱，请先进入邮箱！");
			return;
		}
		if(!emails.withinPage(index)) {
			session.sendMessage("您要查看的邮件不在当前列表中，请检查参数！");
			return;
		}

		String mesg = emails.readEmail(index);
		session.sendMessage(mesg);
	}
	
	/**
	 * 发送邮件
	 * @param session
	 * @param targetPlayer  目标玩家名
	 * @param subjcont 主题+空格+内容
	 */
	public void sendNormalEmail(Session session, String targetPlayer, String subjcont) {
		if(subjcont.equals("") || (subjcont.split(" ").length < 2)) {
			session.sendMessage("邮件主题或内容不能为空");
			return;
		}
		System.out.println("sendNormalEmail--------------" + subjcont);
		if((Context.getWorld().getPlayerEntityByName(targetPlayer) == null) 
				&& (Context.getWorld().getPlayerByName(targetPlayer) == null)) {
			session.sendMessage("你所要发送的玩家不存在！");
			return;
		}
		if(session.getPlayer().getName().equals(targetPlayer)) {
			session.sendMessage("不能发邮件给自己");
			return;
		}
		String content = "email" + " " + session.getPlayer().getName() + " " 
		              +  targetPlayer + " " + subjcont;
		System.out.println("sendNormalEmail--------------content: " + content);
		createEmail(targetPlayer, content);
		session.sendMessage("发送成功");
	}
	
	/**
	 * 发送道具邮件，一般由系统发
	 * @param tPName 目标玩家名
	 * @param subjcont 内容。格式：gold:30;exp:20;1:10
	 */
	public void sendGoodsEmail(String tPName, String subjcont) {
		String content = 1 + " " + tPName + " [award] " + subjcont; //自动添加主题
		createEmail(tPName, content);
	}
	
	
	//构造邮件，只更离线玩家的数据库
	private void createEmail(String tPName , String content) {
		PlayerEntity tpe = Context.getWorld().getPlayerEntityByName(tPName);
		if(tpe == null) {
			Player player = Context.getWorld().getPlayerByName(tPName);
			tpe = player.getPlayerEntity();
		}
		EmailEntity emiE = new EmailEntity(content, tpe);
		tpe.getEmails().add(emiE);
		Player tPlayer = Context.getOnlinPlayer().getPlayerById(tpe.getId());
		if(tPlayer != null) {
			//目标玩家在线, 需要更新缓存
			System.out.println("-------------createEmail---------目标玩家在线" );
			tPlayer.getEmail().addEmail(content);
		}
		System.out.println("-------------createEmail()---------" + tpe.getEmails().toString());
		//更新数据库，只有不在线的目标玩家才需要立即更新
		if(tPlayer == null) {
			//new UpdateTask(tpe);
			System.out.println("**********************************进行更新了吗--前");
			new PlayerDaoImpl().update(tpe);
			System.out.println("**********************************进行更新了吗--后");
		}
	}
	
	/**
	 * 删除邮件
	 * @param session
	 * @param index 邮件编号
	 */
	public void delEmail(Session session, int index) {
		Email emails = session.getPlayer().getEmail();
		if(emails.getEmailPage() == 0) {
			session.sendMessage("当前还不再邮箱，请先进入邮箱！");
			return;
		}
		if(!emails.withinPage(index)) {
			session.sendMessage("您要查看的邮件不在当前列表中，请检查参数！");
			return;
		}
		String content = emails.delEmail(session.getPlayer(), index);
		//session.getPlayer().getEmail().delEmail(index);  //删除缓存中的
		session.sendMessage("删除成功");
	}
	
	/**
	 * 验证是否是数字
	 * @param msg
	 * @return
	 */
	private boolean pisDigit(String msg) {
		for(int i = 0; i < msg.length(); i++) {
			if(!Character.isDigit(msg.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 整理邮件内容
	 * @param args
	 * @return 返回的格式：主题+空格+内容
	 */
	private String orderContent(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append(args[2] + " ");
		for(int i = 3; i < args.length; i++) {
			sb.append(args[i] + " ");
		}
		System.out.println("------------orderContent " + sb.toString());
		return sb.toString();
	}
}
