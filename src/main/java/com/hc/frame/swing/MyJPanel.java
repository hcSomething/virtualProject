package com.hc.frame.swing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class MyJPanel extends JPanel{
	private JButton button;
	private JPanel panel;
	private JTextField in;
	private JTextArea out;
	private JScrollPane scro;
	
	//按键对应的action，它也是显示的关键
	CommandAction action;

	public MyJPanel() {
		setLayout(new BorderLayout());
		
		//输出栏
		out = new JTextArea();
		out.setEditable(false);
		//out.append("请输入服务器IP地址，端口号，以空格间隔");
		scro = new JScrollPane(out);  //只是提供一个可以出现滚动的面板
		
		
		add(scro, BorderLayout.CENTER); //将输出栏放在中间
		
		//按钮
		button = new JButton("输入");
		action = new CommandAction(this);
		button.addActionListener(action); //为按钮添加监听器
		
		//输入栏
		in = new JTextField();
		in.setEditable(true);
		
		//第二层panel：包含输入栏和按钮
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));
		panel.add(in);
		panel.add(button);
		
		add(panel, BorderLayout.SOUTH);  //将第二层panel放在底下
		
	}
	

	public JTextField getIn() {
		return in;
	}

	public void setIn(JTextField in) {
		this.in = in;
	}

	public JTextArea getOut() {
		return out;
	}

	public void setOut(JTextArea out) {
		this.out = out;
	}


	public CommandAction getAction() {
		return action;
	}



}
