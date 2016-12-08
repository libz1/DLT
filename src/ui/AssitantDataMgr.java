package ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.eastsoft.util.DebugSwing;

import base.BaseFrame;

/**
 * 辅助数据管理
 * 
 * @author xuky
 * @version 2016-10-13
 * 
 */

public class AssitantDataMgr extends BaseFrame{
	
	
    private JTabbedPane jTabbedpane;// 存放选项卡的组件
    
	private volatile static AssitantDataMgr uniqueInstance;
	public static AssitantDataMgr getInstance() {
		if (uniqueInstance == null) {
			synchronized (AssitantDataMgr.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new AssitantDataMgr();
				}
			}
		}
		return uniqueInstance;
	}
	
    private AssitantDataMgr() {
    }  
	
	
    
	@Override
	public void init() {
        layoutComponents();  
	}
    
  
    private void layoutComponents() {  
        int i = 0;
        String[] tabNames = { "软件电表档案", "终端电表档案","电表档案同步","参数设置","采集任务配置" };
        jTabbedpane = new JTabbedPane();// 存放选项卡的组件
        
        BaseFrame mainFrame = new MeterCRUD();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame.panel, tabNames[0]);// 加入第一个页面
        mainFrame.getPanel().setBounds(0, 0, 800, 600);
  
        BaseFrame mainFrame1 = new MeterOfTermCRUD();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame1.panel, tabNames[1]);// 加入第一个页面
        mainFrame1.getPanel().setBounds(0, 0, 800, 600);
        
        BaseFrame mainFrame2 = new MeterCompare();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame2.panel, tabNames[2]);// 加入第一个页面
        mainFrame2.getPanel().setBounds(0, 0, 800, 600);
        
        BaseFrame mainFrame3 = new SoftParaManager();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame3.panel, tabNames[3]);// 加入第一个页面
        mainFrame3.getPanel().setBounds(0, 0, 800, 600);
        
        BaseFrame mainFrame4 = new TaskSetCRUD();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame4.panel, tabNames[4]);// 加入第一个页面
        mainFrame4.getPanel().setBounds(0, 0, 800, 600);
        
        // 公共代码
        panel.setLayout(new GridLayout(1, 1));
        panel.add(jTabbedpane);  
  
    }  

	public static void main(String[] args) {
	}

}
