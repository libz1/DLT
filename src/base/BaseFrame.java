package base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.eastsoft.util.DebugSwing;


//用于统一调用
public abstract class BaseFrame {
	
	public JFrame frame;
	public JPanel panel;
	protected String OPERATETYPE = "";
	
	public BaseFrame(){
		init0();
	}
	
	public void setCursor(int cursorType){
		frame.setCursor(cursorType);
	}
	
	public BaseFrame(String type){
		OPERATETYPE = type;
		init0();
	}

	// 最先的初始化程序
	private void init0() {
		
		// 设置panel为可以随窗口大小调整
		frame = new JFrame();
		
		Container c = frame.getContentPane();
		
		c.setLayout(new BorderLayout());
		
		panel = new JPanel(null);
		panel.setBackground(Color.white);
		panel.setVisible(true);
		// BorderLayout.CENTER是随着窗口大小而变化的
		c.add(panel, BorderLayout.CENTER);
		
		// 在后面的showFrame函数中，将会实现窗口的相关设置

		// 模板模式开发：在这里编写算法执行过程，其中init是抽象函数，需要在子类中具体实现，initComponent是公共内容的父类实现的具体函数
		try{
			init();
			initComponent(panel);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	// 1、抽象函数，需要在子类中具体实现，对子类的各种具体变量赋值等
	protected abstract void init();

	// 2、递归方式将各个容器中的控件进行属性设置
	protected void initComponent(JComponent jComponent) {
		Component[] c = jComponent.getComponents();
		String class_name = "";
		for (Component co : c) {
			class_name = co.getClass().toString();
			//System.out.println("class_name=>"+class_name);
			if (class_name.indexOf("JButton") >= 0) {
				//按钮为蓝底白字
				co.setBackground(new Color(0, 114, 198));
				co.setForeground(Color.white);
			}
			if (class_name.indexOf("JLabel") >= 0
					|| class_name.indexOf("JTextField") >= 0
					|| class_name.indexOf("JComboBox") >= 0
					|| class_name.indexOf("JTable") >= 0
					|| class_name.indexOf("JTextArea") >= 0) {
				//蓝字
				co.setForeground(new Color(0, 114, 198));
			}
			if (class_name.indexOf("JTable") >= 0) {
				// 设置列表的表头部分字体颜色
				((JTable)co).getTableHeader().setForeground(new Color(0, 114, 198));
			}
			
			if (class_name.indexOf("JScrollPane") >= 0
					|| class_name.indexOf("JPanel") >= 0
					|| class_name.indexOf("JTable") >= 0
					|| class_name.indexOf("JViewport") >= 0) {
				// 递归方式对其子控件进行设置
				initComponent((JComponent) co);
			}
			//System.out.println(class_name);
		}
	}

	public JPanel getPanel() {
		return panel;
	}
	
	public void showFrame(String title) {
		showFrame(title,0,0,0,0);
	}
	
	public void showFrame(String title,int x,int y,int input_width,int input_height) {
		int width = 800, height = 600; 
		if (frame != null)
			frame.dispose();
		
//		// 设置界面所在Panel的位置等信息
//		getPanel().setBounds(0, 0, width, height);
		
		frame.setTitle(title);
//		frame.setLayout(null);
//		frame.add(getPanel());
		
		// xuky 子窗口关闭的时候，不关闭主窗口，而且退出后不会出现javaw残余
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		if (x == 0){
			frame.setSize(width, height);
			DebugSwing.center(frame);
		}
		else{
//			frame.setSize(input_width, input_height);
			frame.setBounds(x, y, input_width, input_height);
		}
//		frame.pack();
		frame.setVisible(true);

	}
	
}
