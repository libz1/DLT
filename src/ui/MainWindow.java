package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import util.DB;
import util.Util698;
import base.BaseFrame;

import com.eastsoft.util.DebugSwing;

/**
 * 程序按钮控件.
 * <p>
 * 显示其他功能的入口
 * 
 * @author xuky
 * @version 2016.08.24
 */
public class MainWindow {

	private JPanel panel; // 对外展示的控件
	private JButton[] buttonArr;
	private JRadioButton[] rdbuttonArr;
	private ButtonGroup buttonGroup;
	private JLabel lb_ver;
	
	String  ver = "0.29";
	
	int width = 800, height = 600;

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonName = e.getActionCommand();
			
//			buttonArr[0].setText("报文组织解析");
//			buttonArr[1].setText("对象属性读取、设置");
//			buttonArr[2].setText("对象函数调用");
//			buttonArr[3].setText("数据类型管理");
//			buttonArr[4].setText("接口类管理");
//			buttonArr[5].setText("对象管理");
//			buttonArr[6].setText("内置前置机");
			
			BaseFrame mainFrame = null;
			if (buttonName.equals("对象属性读取、设置")) {
				new OperateFrame("").showFrame(buttonName);
			}
			if (buttonName.equals("对象方法调用")) {
				new OperateFrame("Func").showFrame(buttonName);;
			}
			if (buttonName.equals("数据类型管理")) {
				new DataTypeUserCRUD().showFrame(buttonName);;
			}
			if (buttonName.equals("对象管理")) {
				new ObjectCRUD("").showFrame(buttonName);;
			}
			if (buttonName.equals("接口类管理")) {
				new ObjectCRUD("IFC").showFrame(buttonName);;
			}
			
			if (buttonName.equals("通信服务器")) {
				PrefixWindow.getInstance().showFrame(buttonName,120,510,800,200);
			}
			else if (buttonName.equals("辅助数据管理及参数设置")) {
				AssitantDataMgr.getInstance().showFrame(buttonName);
			}
			else if (buttonName.equals("报文组织解析")) {
				FrameAnaly.getInstance().showFrame(buttonName);
			}
			else if (mainFrame!=null){
//				// 除“通信服务器”以外的其他的功能界面在这里控制显示
//				mainFrame.getPanel().setBounds(0, 0, width, height);
//				JFrame frame = new JFrame();
//				frame.setTitle(buttonName);
//				frame.setLayout(null);
//				frame.add(mainFrame.getPanel());
//				frame.setSize(width, height);
//				frame.setVisible(true);
//				DebugSwing.center(frame);
			}

			// ------------xuky 以下几个模块公用相同的类
			// 之前是进行的多个new ,导致的问题是，类中的button多次注册 。一次点击会触发多个重复的响应
			if (buttonName.equals("对象参数读取、设置")) {
				// TerminalParameterWindow.getInstance().showFrame();
				// TerminalParameterWindow.getInstance().showOneModule(buttonName);
			}

			if (buttonName.equals("对象函数调用")) {
				// new MeterParameterWindow();
			}

		}
	}

	public MainWindow() {
		// 在构造函数中对界面内容进行初始化
		init();
		
	}

	private void showFlowWorkOrder() {
		panel.setVisible(false);

		for (int i = 9; i <= 16; i++) {
			buttonArr[i].setVisible(true);
		}
		buttonArr[1].setVisible(false);
		buttonArr[5].setVisible(false);

		int HEIGHT = 65;
		int INTERVAL = 5;
		int INTERVALY = 25;
		int WEIGHT = 220;
		int TOPX = 15;
		int TOPY = 50;

		int[] order = { 0, 3, 14, 13, 2, 6, 9, 10, 11, 12, 4, 15, 16, 7, 8 };
		int aNum = order.length;
		int j = aNum / 3;
		if (j * 3 != aNum)
			j++;

		int x = TOPX;
		int y = TOPY;

		int aNo = 0;
		for (int i = 0; i < j; i++) {
			x = TOPX;
			aNo = i * 3;
			if (aNo < aNum)
				buttonArr[order[aNo]].setBounds(x, y, WEIGHT, HEIGHT);
			x = x + WEIGHT + INTERVAL;
			aNo = i * 3 + 1;
			if (aNo < aNum)
				buttonArr[order[aNo]].setBounds(x, y, WEIGHT, HEIGHT);
			x = x + WEIGHT + INTERVAL;
			aNo = i * 3 + 2;
			if (aNo < aNum)
				buttonArr[order[aNo]].setBounds(x, y, WEIGHT, HEIGHT);
			y = y + HEIGHT + INTERVALY;
		}

		panel.setVisible(true);
	}

	private void showFlowTargerObject() {
		panel.setVisible(false);

		for (int i = 9; i <= 16; i++) {
			buttonArr[i].setVisible(true);
		}
		buttonArr[1].setVisible(false);
		buttonArr[5].setVisible(false);

		int HEIGHT = 60;
		int INTERVAL = 2;
		int INTERVALY = 20;
		int WEIGHT = 220;
		int TOPX = 15;
		int TOPY = 50;

		// 行1 列1
		int x = TOPX;
		int y = TOPY;
		buttonArr[0].setBounds(x, y, WEIGHT, HEIGHT);

		// 行1 列2
		x = x + WEIGHT + INTERVAL;
		// Font font = new Font("宋体", Font.BOLD, 12);
		// buttonArr[9].setFont(font);

		buttonArr[10].setBounds(x + WEIGHT, y, WEIGHT, HEIGHT / 2);
		buttonArr[9].setBounds(x, y, WEIGHT, HEIGHT / 2);
		buttonArr[11].setBounds(x, y + HEIGHT / 2, WEIGHT, HEIGHT / 2);
		buttonArr[12].setBounds(x + WEIGHT, y + HEIGHT / 2, WEIGHT, HEIGHT / 2);

		// 行2 列1
		x = TOPX;
		y = y + HEIGHT + INTERVAL;
		buttonArr[3].setBounds(x, y, WEIGHT * 3 + INTERVAL, HEIGHT);

		// ////////////////////////////////////////////////////////

		// 行3 列1
		x = TOPX;
		y = y + HEIGHT + INTERVALY;
		// buttonArr[5].setBounds(x, y, WEIGHT, HEIGHT);
		buttonArr[13].setBounds(x, y, WEIGHT, HEIGHT / 2);
		buttonArr[14].setBounds(x + WEIGHT, y, WEIGHT, HEIGHT / 2);
		buttonArr[15].setBounds(x, y + HEIGHT / 2, WEIGHT, HEIGHT / 2);
		buttonArr[16].setBounds(x + WEIGHT, y + HEIGHT / 2, WEIGHT, HEIGHT / 2);

		// 行3 列2
		x = x + WEIGHT * 2 + INTERVAL;
		buttonArr[4].setBounds(x, y, WEIGHT, HEIGHT);

		// 行3 列3
		x = TOPX;
		y = y + HEIGHT + INTERVAL;
		buttonArr[2].setBounds(x, y, WEIGHT * 3 + INTERVAL, HEIGHT);

		// ////////////////////////////////////////////////////////

		// 行3 列1
		x = TOPX;
		y = y + HEIGHT + INTERVALY;
		buttonArr[6].setBounds(x, y, WEIGHT * 3 + INTERVAL, HEIGHT);

		// 行4 列1
		x = TOPX;
		y = y + HEIGHT + INTERVALY;
		buttonArr[7].setBounds(x, y, WEIGHT * 2, HEIGHT);

		x = x + WEIGHT * 2 + INTERVAL;
		buttonArr[8].setBounds(x, y, WEIGHT, HEIGHT);

		panel.setVisible(true);
	}

	private void showFlowFun() {
		panel.setVisible(false);
		int HEIGHT = 73;
		int INTERVAL = 2;
		int WEIGHT = 220;
		int TOPX = 55;
		int TOPY = 50;

		for (int i = 9; i <= 16; i++) {
			buttonArr[i].setVisible(false);
		}

		// 左1
		int x = TOPX;
		int y = TOPY;
		buttonArr[0].setBounds(x, y, WEIGHT, HEIGHT * 2);

		// 左2
		y = y + HEIGHT * 2 + INTERVAL;
		buttonArr[1].setBounds(x, y, WEIGHT, HEIGHT);
		buttonArr[1].setVisible(true);

		// 左3
		y = y + HEIGHT + INTERVAL;
		buttonArr[2].setBounds(x, y, WEIGHT, HEIGHT * 2);

		// 左4
		y = y + HEIGHT * 2 + INTERVAL;
		buttonArr[7].setBounds(x, y, WEIGHT * 2, HEIGHT);

		// 中1
		y = TOPY;
		x = TOPX + WEIGHT + INTERVAL;
		buttonArr[3].setBounds(x, y, WEIGHT, HEIGHT);

		// 右1
		x = x + WEIGHT + INTERVAL;
		buttonArr[4].setBounds(x, y, WEIGHT, HEIGHT);

		// 中2
		x = TOPX + WEIGHT + INTERVAL;
		y = y + HEIGHT + INTERVAL;
		buttonArr[5].setBounds(x, y, WEIGHT * 2 + INTERVAL, HEIGHT * 3);
		buttonArr[5].setVisible(true);

		// 中3
		y = y + HEIGHT * 3 + INTERVAL;
		buttonArr[6].setBounds(x, y, WEIGHT * 2 + INTERVAL, HEIGHT);

		// 中4
		y = y + HEIGHT + INTERVAL;
		x = x + WEIGHT + INTERVAL;
		buttonArr[8].setBounds(x, y, WEIGHT, HEIGHT);

		panel.setVisible(true);
	}

	private void init() {
		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(null);
		panel.setBackground(Color.white);
		panel.setVisible(true);

		
		lb_ver = new JLabel("ver "+ver, JLabel.CENTER);
		lb_ver.setBounds(700, 500, 80, 60);
		panel.add(lb_ver);
		
		// button = new JButton("弹出新窗口");
		ButtonListener buttonListener = new ButtonListener();

		buttonArr = new JButton[25];
		Font font = new Font("宋体", Font.BOLD, 14);
		for (int i = 0; i < 25; i++) { // 通过一个循环,对按钮数组中的每一个按钮实例化.
			buttonArr[i] = new JButton();
			buttonArr[i].setForeground(Color.white);
			buttonArr[i].setFont(font);
			panel.add(buttonArr[i]);
			buttonArr[i].addActionListener(buttonListener);
		}

		buttonArr[0].setText("报文组织解析");
		buttonArr[1].setText("数据类型管理");
		buttonArr[2].setText("对象管理");
		buttonArr[3].setText("对象属性读取、设置");
		buttonArr[4].setText("对象方法调用");
		buttonArr[5].setText("通信服务器");
		buttonArr[6].setText("接口类管理");
		buttonArr[7].setText("辅助数据管理及参数设置");

		buttonArr[0].setBackground(new Color(247, 175, 47));
		buttonArr[1].setBackground(new Color(169, 51, 254));
		buttonArr[2].setBackground(new Color(223, 89, 71));
		buttonArr[3].setBackground(new Color(8, 152, 249));
		buttonArr[4].setBackground(new Color(247, 175, 47));
		buttonArr[5].setBackground(new Color(0, 72, 190));
		buttonArr[6].setBackground(new Color(123, 213, 55));
		buttonArr[7].setBackground(new Color(8, 152, 249));
		buttonArr[8].setBackground(new Color(125, 130, 156));
		// new Color(125, 130, 156)灰色

		buttonArr[9].setBackground(buttonArr[1].getBackground());
		buttonArr[10].setBackground(buttonArr[1].getBackground());
		buttonArr[11].setBackground(buttonArr[1].getBackground());
		buttonArr[12].setBackground(buttonArr[1].getBackground());

		buttonArr[13].setBackground(buttonArr[5].getBackground());
		buttonArr[14].setBackground(buttonArr[5].getBackground());
		buttonArr[15].setBackground(buttonArr[5].getBackground());
		buttonArr[16].setBackground(buttonArr[5].getBackground());

		font = new Font("宋体", Font.BOLD, 14);
		buttonGroup = new ButtonGroup();
		rdbuttonArr = new JRadioButton[4];
		for (int i = 0; i < 4; i++) { // 通过一个循环,对按钮数组中的每一个按钮实例化.
			if (i == 0) {
				rdbuttonArr[i] = new JRadioButton("", true);
			} else {
				rdbuttonArr[i] = new JRadioButton("", false);
			}

			rdbuttonArr[i].setFont(font);
			rdbuttonArr[i].setForeground(new Color(0, 114, 198));
			buttonGroup.add(rdbuttonArr[i]);
			// xuky 2016.08.26 不显示rdbutton
			//panel.add(rdbuttonArr[i]);

			rdbuttonArr[i].addActionListener(buttonListener);

		}

		rdbuttonArr[0].setText("按功能展示");
		rdbuttonArr[1].setText("按操作对象");
		rdbuttonArr[2].setText("按操作流程分类");
		// rdbuttonArr[3].setText("按协议分类");

		rdbuttonArr[0].setBounds(20, 5, 126, 30);
		rdbuttonArr[1].setBounds(145, 5, 126, 30);
		rdbuttonArr[2].setBounds(270, 5, 135, 30);
		// rdbuttonArr[3].setBounds(405, 5, 135, 30);

		showFlowFun();

	}

	public void setBounds(int x, int y, int width, int height) {
		panel.setBounds(x, y, width, height);
	}

	public static void main(String[] args) {
		int WINDOWWIDTH = 800, WINDOWHEIGHT = 600;
		MainWindow mainFrame = new MainWindow();
		mainFrame.panel.setBounds(0, 0, WINDOWWIDTH, WINDOWHEIGHT);
		JFrame frame = new JFrame();
		
		
		frame.addWindowListener(new WindowAdapter() {  
	        public void windowClosing(WindowEvent e) {  
	        	super.windowClosing(e);
	        	DB.getInstance().close();
	        	// xuky 2016.09.02 关闭前删除运行sqlite产生的临时文件
	        	Util698.deleteFiles(System.getProperty("user.dir"),"etilqs_");
	         }  
        });

		// xuky 2016.09.02 启动前删除运行sqlite产生的临时文件
		Util698.deleteFiles(System.getProperty("user.dir"),"etilqs_");
		
		frame.setLayout(null);
		frame.add(mainFrame.panel);
		frame.setSize(WINDOWWIDTH, WINDOWHEIGHT);
		frame.setTitle("698.45协议应用软件");
		DebugSwing.center(frame);
		frame.setVisible(true);
		
		// 关闭此frame时，关闭整个应用程序
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
 	
	
	public JPanel getPanel() {
		return panel;
	}

}
