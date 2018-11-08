package com.hc.logic.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.hc.frame.Context;
import com.hc.logic.base.Constants;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.EmailEntity;
import com.hc.logic.domain.PlayerEntity;


public class Email {
	//邮箱内容。list中包含两部分：主题和 邮件所有内容
	private Stack<List<String>> emails = new Stack<>();
	//邮箱每页显示的邮件条数
	private final int PAGENUM = 3;
	//邮箱页面
	private int emailPage = 0;
	
	//在player中调用
	public Email(List<EmailEntity> lists) {
		for(EmailEntity ee : lists) {
			addEmail(ee.getContent());
		}
	}
	
	/**
	 * 分页显示邮箱内容
	 */
	public String displayEmail(int page) {
		Map<Integer, String> emailPage = aPage(page);
		List<Integer> keys = sortedKeys(emailPage.keySet());
		StringBuilder sb = new StringBuilder();
		sb.append("【邮箱】- - - - - - - - - - - - - - - - - - - - - - - -- - - - - - - -\n");
		sb.append("- - - - - - - - - -第【" + page + "】页- - - - - - - - - - - -- -  -- - - - -\n");
		for(int i : keys) {
			sb.append("【"+ i + "】" + emailPage.get(i) + "\n"); //需要减1
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -- - - - -");
		return sb.toString();
	}
	
	private List<Integer> sortedKeys(Set<Integer> keys){
		List<Integer> resu = new ArrayList<>();
		for(int i : keys)
			resu.add(i);
		Collections.sort(resu);
		return resu;
	}
	
	/**
	 * 获得第n页的邮件
	 * @param page
	 * @return map: key: 在邮箱list中的位置+1， value：相应的主题
	 */
	public Map<Integer, String> aPage(int page){
		//key: 在邮箱list中的位置+1， value：相应的主题
		Map<Integer, String> result = new HashMap<>();
		int start = PAGENUM * (page-1);
		int stop = PAGENUM + start;System.out.println("------------ 内容---- " + start +" " + stop);
		for(int i = start; i < stop; i++) {
			if(i >= emails.size()) break; //不足一页
			result.put(i+1, emails.get(i).get(0)); //只显示邮件主题
		}
		return result;
	}
	
	/**
	 * 读取邮件内容
	 * @param index 邮件编号
	 * @return
	 */
	public String readEmail(int index) {
		List<String> em = emails.get(index-1);
		String[] content= em.get(1).split(" ");
		if(Character.isDigit(content[0].charAt(0))) {
			return readGoodsEmail(content, index);
		}
		return readNormalEmail(content, index);
	}
	
	//读取道具邮件,content格式：1     目标玩家名 主题 各种道具
	//各种道具格式: gold:30;exp:20...
	private String readGoodsEmail(String[] content, int index) {
		int pId = Context.getWorld().getPlayerEntityByName(content[1]).getId();
		Player player = Context.getOnlinPlayer().getPlayerById(pId); //读取邮件的玩家肯定在线
		StringBuilder sb = new StringBuilder();
		sb.append("系统发给您奖励如下: \n");
		String[] items = content[3].split(";");
		for(int i = 0; i < items.length; i++) {
			String[] nameAmount = items[i].split(":");
			Constants.doReword(player, nameAmount[0], nameAmount[1]);
			sb.append(nameAmount[0] + " " + nameAmount[1] + "个");
		}
		//删除道具邮件
		delEmail(player, index);
		return sb.toString();
	}
	//读取普通邮件，content格式：email 玩家名 目标玩家名 主题  内容
	private String readNormalEmail(String[] content, int index) {
		PlayerEntity tpe = Context.getWorld().getPlayerEntityByName(content[2]);
		if(tpe == null) {
			Player player = Context.getWorld().getPlayerByName(content[2]);
			tpe = player.getPlayerEntity();
		}
		int pId = tpe.getId(); 
		//int pId = Context.getWorld().getPlayerEntityByName(content[2]).getId();
		Player player = Context.getOnlinPlayer().getPlayerById(pId); //读取邮件的玩家肯定在线
		StringBuilder sb = new StringBuilder();
		sb.append("玩家【" + content[1] + "】发送的信息如下: \n");
		for(int i = 4; i < content.length; i++) {
			sb.append(content[i] + " ");
		}
		return sb.toString();
	}
	
	/**
	 * 删除邮件, 并且删除数据库中的邮件
	 * @param index 邮件编号
	 */
	public String delEmail(Player player, int index) {
		String content = emails.get(index-1).get(1);
		emails.remove(index-1);
		removeEmail(player, content);
		return content;
	}
	
	//删除邮件
	private void removeEmail(Player player, String content) {
		PlayerEntity tpe = player.getPlayerEntity();
		for(EmailEntity ee : tpe.getEmails()) {
			if(ee.getContent().equals(content)) {
				tpe.delEmail(ee);
				return;
			}
		}
	}

	
	
	/**
	 * 页面是否有效
	 * @param page 
	 * @return
	 */
	public boolean isValiedPage(int page) {
		if(page < 1) return false;
		int start = PAGENUM * (page-1) + 1;
		int volum = emails.size();
		if(volum == 0 && page == 1) return true;  //当邮箱为空时，只可以显示第一页
		if(start > volum) return false;	
		//int playerPage = player.getPageNumber();
		if(page != emailPage && page != (emailPage + 1) && page != (emailPage - 1)) return false;
		return true;
	}
	
	/**
	 * 判断邮件的编号是否在当前页面
	 * @param index 玩家输入的邮件编号
	 * @return
	 */
	public boolean withinPage(int index) {
		for(int i : aPage(emailPage).keySet()) {
			if(i == index)
				return true;
		}
		return false;
	}

	public void setEmailPage(int emailPage) {
		this.emailPage = emailPage;
	}
	public int getEmailPage() {
		return emailPage;
	}

	/**
	 * 添加邮件
	 * @param subj 主题
	 * @param msg  内容
	 */
	public void addEmail(String subj, String msg) {
		List<String> list = new ArrayList<>();
		list.add(subj);
		list.add(msg);
		emails.push(list);
	}
	
	public Stack<List<String>> getEmail(){
		return emails;
	}
	
	/**
	 * 通过邮件所有内容添加邮件
	 * @param content
	 */
	public void addEmail(String content) {
		String[] con = content.split(" ");
		if(Character.isDigit(con[0].charAt(0))) {
			addEmail(con[2], content);
		}else {
			addEmail(con[3], content);
		}
	}
}
