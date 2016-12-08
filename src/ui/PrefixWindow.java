package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.eastsoft.util.DebugSwing;

import base.BaseFrame;
import socket.PrefixMain;
import util.SoftParameter;

/**
 * 通信服务器
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */

public class PrefixWindow extends BaseFrame implements Observer{
	
    private JTabbedPane jTabbedpane;// 存放选项卡的组件  
    private static String[] tabNames = { "通信链路", "收发报文" };
	private volatile static PrefixWindow uniqueInstance;
	
	public static PrefixWindow getInstance() {
		if (uniqueInstance == null) {
			synchronized (PrefixWindow.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new PrefixWindow();
				}
			}
		}
		return uniqueInstance;
	}
    
    private PrefixWindow() {
    }  

	@Override
	protected void init() {
        layoutComponents();  
        // 启动前置机示例
        PrefixMain.getInstance();
        LogWindow.getInstance().lb_status.setText("前置机开启，前置机监听端口"+SoftParameter.getInstance().getPrefix_port());
	}
    
    
    private void layoutComponents() {  
        int i = 0;  
        // 第一个标签下的JPanel
        jTabbedpane = new JTabbedPane();// 存放选项卡的组件
        JPanel jpanelFirst = new JPanel(new BorderLayout()); // 无layout  
        jTabbedpane.addTab(tabNames[i++], null, jpanelFirst, tabNames[0]);// 加入第一个页面  
        BaseFrame mainFrame = DevListWindow.getInstance();
		jpanelFirst.add(mainFrame.getPanel(),BorderLayout.CENTER);
//		mainFrame.getPanel().setBounds(0, 0, 800, 150);
  
        // 第二个标签下的JPanel  
        JPanel jpanelSecond = new JPanel(new BorderLayout()); // 无layout  
        jTabbedpane.addTab(tabNames[i++], null, jpanelSecond, tabNames[1]);// 加入第一个页面
        
        mainFrame = LogWindow.getInstance();
		jpanelSecond.add(mainFrame.getPanel(),BorderLayout.CENTER);
//		mainFrame.getPanel().setBounds(0, 0, 800, 150);
        
        panel.setLayout(new GridLayout(1, 1));
        panel.add(jTabbedpane);  
  
    }  

	public static void main(String[] args) {
	}


	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}



}
