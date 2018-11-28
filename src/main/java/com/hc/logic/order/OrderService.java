package com.hc.logic.order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.frame.taskSchedule.OneTaskConsumer;
import com.hc.frame.taskSchedule.TaskProducer;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

import io.netty.channel.Channel;

@Component
public class OrderService {

	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	private TaskProducer taskproducer;
	private OneTaskConsumer consumer;
	
	@PostConstruct
	public void init() {
		taskproducer = new TaskProducer(queue);
		consumer = new OneTaskConsumer(queue);
		new Thread(consumer).start();
	}
	/**
	 * 将命令分发到不同的场景中
	 * @param order
	 * @param session
	 * @param args
	 */
	public void distributeOrder(Order order, Session session, String[] args) {
		//System.out.println("-------------分发命令---" + args[0]);
		Player player = session.getPlayer();
		DoOrder dOrder = new DoOrder(order, session, args);;	
		if(args[0].equals("register") || args[0].equals("job") || args[0].equals("login") || args[0].equals("transfer")) {
			dOrder.run();
			return;
		}
		player.addOrder(dOrder);
	}
	
	public Player hasAccount(Channel channel) {
		return Context.getOnlinPlayer().containChannel(channel);
	}
	
	public boolean aliveBeforeOrder(Session session) {
		if(session.getPlayer() == null) {
			return true;
		}
		if(!session.getPlayer().isAlive()) {
			session.sendMessage("您已经死亡，请先复活");
			return false;
		}
		return true;
	}

}
