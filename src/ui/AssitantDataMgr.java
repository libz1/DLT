package ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.eastsoft.util.DebugSwing;

import base.BaseFrame;

/**
 * �������ݹ���
 * 
 * @author xuky
 * @version 2016-10-13
 * 
 */

public class AssitantDataMgr extends BaseFrame{
	
	
    private JTabbedPane jTabbedpane;// ���ѡ������
    
	private volatile static AssitantDataMgr uniqueInstance;
	public static AssitantDataMgr getInstance() {
		if (uniqueInstance == null) {
			synchronized (AssitantDataMgr.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
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
        String[] tabNames = { "��������", "�ն˵����","�����ͬ��","��������","�ɼ���������" };
        jTabbedpane = new JTabbedPane();// ���ѡ������
        
        BaseFrame mainFrame = new MeterCRUD();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame.panel, tabNames[0]);// �����һ��ҳ��
        mainFrame.getPanel().setBounds(0, 0, 800, 600);
  
        BaseFrame mainFrame1 = new MeterOfTermCRUD();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame1.panel, tabNames[1]);// �����һ��ҳ��
        mainFrame1.getPanel().setBounds(0, 0, 800, 600);
        
        BaseFrame mainFrame2 = new MeterCompare();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame2.panel, tabNames[2]);// �����һ��ҳ��
        mainFrame2.getPanel().setBounds(0, 0, 800, 600);
        
        BaseFrame mainFrame3 = new SoftParaManager();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame3.panel, tabNames[3]);// �����һ��ҳ��
        mainFrame3.getPanel().setBounds(0, 0, 800, 600);
        
        BaseFrame mainFrame4 = new TaskSetCRUD();
        jTabbedpane.addTab(tabNames[i++], null, mainFrame4.panel, tabNames[4]);// �����һ��ҳ��
        mainFrame4.getPanel().setBounds(0, 0, 800, 600);
        
        // ��������
        panel.setLayout(new GridLayout(1, 1));
        panel.add(jTabbedpane);  
  
    }  

	public static void main(String[] args) {
	}

}
