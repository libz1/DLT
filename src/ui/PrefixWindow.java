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
 * ͨ�ŷ�����
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */

public class PrefixWindow extends BaseFrame implements Observer{
	
    private JTabbedPane jTabbedpane;// ���ѡ������  
    private static String[] tabNames = { "ͨ����·", "�շ�����" };
	private volatile static PrefixWindow uniqueInstance;
	
	public static PrefixWindow getInstance() {
		if (uniqueInstance == null) {
			synchronized (PrefixWindow.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
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
        // ����ǰ�û�ʾ��
        PrefixMain.getInstance();
        LogWindow.getInstance().lb_status.setText("ǰ�û�������ǰ�û������˿�"+SoftParameter.getInstance().getPrefix_port());
	}
    
    
    private void layoutComponents() {  
        int i = 0;  
        // ��һ����ǩ�µ�JPanel
        jTabbedpane = new JTabbedPane();// ���ѡ������
        JPanel jpanelFirst = new JPanel(new BorderLayout()); // ��layout  
        jTabbedpane.addTab(tabNames[i++], null, jpanelFirst, tabNames[0]);// �����һ��ҳ��  
        BaseFrame mainFrame = DevListWindow.getInstance();
		jpanelFirst.add(mainFrame.getPanel(),BorderLayout.CENTER);
//		mainFrame.getPanel().setBounds(0, 0, 800, 150);
  
        // �ڶ�����ǩ�µ�JPanel  
        JPanel jpanelSecond = new JPanel(new BorderLayout()); // ��layout  
        jTabbedpane.addTab(tabNames[i++], null, jpanelSecond, tabNames[1]);// �����һ��ҳ��
        
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
