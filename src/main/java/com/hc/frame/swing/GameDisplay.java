package com.hc.frame.swing;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GameDisplay {
	
	private MFrame frame;
	
	public void startGame() {
		
		EventQueue.invokeLater(()->{
			frame = new MFrame();
			frame.setTitle("–Èƒ‚”Œœ∑");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(500, 350);
			frame.setVisible(true);
			
		});
	}

	public CommandAction getCommandAction() {
		return frame.getMyJPanel().getAction();
	}


	
	
}

class MFrame extends JFrame{
	private MyJPanel myJPanel;
	//JTextField text = myJPanel.getIn();
	public MFrame() {
		
		myJPanel = new MyJPanel();
		add(myJPanel);
	}
	
	public MyJPanel getMyJPanel() {
		return myJPanel;
	}


	
}

