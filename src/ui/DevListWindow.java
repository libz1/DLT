package ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import base.BaseFrame;
import socket.Channel;
import socket.ChannelList;
import util.Publisher;

/**
 * 通信设备列表
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */
public class DevListWindow extends BaseFrame implements Observer{
	private JTable table_List;
	private DefaultTableModel model_List;
	static String[] colNames = {"地址","设备地址","类型","连接时间","通信时间","心跳周期"};
	private JTextArea txt_frame;
	private JLabel lb_analy;
	public JLabel lb_status;
	
	private volatile static DevListWindow uniqueInstance;
	
	public static DevListWindow getInstance() {
		if (uniqueInstance == null) {
			synchronized (DevListWindow.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new DevListWindow();
					// 加入Publisher
					Publisher.getInstance().addObserver(uniqueInstance);
				}
			}
		}
		return uniqueInstance;
	}

	private DevListWindow(){
		
	}
	

	private void init_table(){
		table_List = new JTable();
		
		// 设置列表为单选模式
		table_List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		Object[][] data_attrs = new Object[0][0];
		
		model_List = new DefaultTableModel(data_attrs, colNames) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0)
					return true;// 返回true表示能编辑，false表示不能编辑
				else
					return false;// 返回true表示能编辑，false表示不能编辑
			}
		};
		table_List.setModel(model_List);
		table_List.setRowHeight(20);
		refreshTable();

		panel.setLayout(new BorderLayout());

		// 表格放置在滚动面板中
		JScrollPane scroll_obj_attr = new JScrollPane(table_List);
		panel.add(scroll_obj_attr,BorderLayout.CENTER);
//		scroll_obj_attr.setBounds(5, 5, 800, 100);
		
		
	}
	
	@Override
	public void update(Observable o, Object arg) {
		String[] s = (String[]) arg;
		if (s[0].equals("refresh terminal list")){
			refreshTable();
		}
	}


	private synchronized void refreshTable() {
		int num = ChannelList.getInstance().getChannelList().size();
		Object[][] data_obj_attr = new Object[num][6];
		//data_obj_attr = DB.getInstance().getOADList(OPERATETYPE);
		int i = 0;
		String type= ""; 
    	for (Channel c:ChannelList.getInstance().getChannelList()){
    		data_obj_attr[i][0] = c.getAddr();
    		data_obj_attr[i][1] = c.getLogAddr();
    		type = c.getType();
    		if (type.equals("0"))
    			data_obj_attr[i][2] = "TCP";
    		else if (type.equals("1"))
    			data_obj_attr[i][2] = "UDP";
    		else
    			data_obj_attr[i][2] = "COM";
    		data_obj_attr[i][3] = c.getConnectTime();
    		data_obj_attr[i][4] = c.getRecvTime();
    		data_obj_attr[i][5] = c.getHeatTime();
    		i++;
    	}
    	try{
//    		System.out.println("refreshTable=> begin");
    		model_List.setDataVector(data_obj_attr, colNames);
//    		System.out.println("refreshTable=> end");
    	}
    	catch(Exception e){
    		System.out.println("model_List.setDataVector err=>");
    		e.printStackTrace();
    	}
	}
	
	/**
	 * 界面初始化
	 */
	@Override
	protected void init() {
		

//		lb_analy = new JLabel("收发报文", JLabel.CENTER);
//		txt_frame = new JTextArea();
//		lb_status = new JLabel("");
//		
//		lb_analy.setForeground(new Color(0, 114, 198));
//		txt_frame.setForeground(new Color(0, 114, 198));
//		lb_status.setForeground(new Color(0, 114, 198));
//		
//		//panel.add(lb_analy);
//		//panel.add(txt_frame);
//		//panel.add(lb_status);
//		
//		int height = 30, lb_width = 80, inter_val = 5 ;
//		int topX = 5, topY = 5;
//		int x = topX ;
//		int y = topY;
//		int bt_width = 120;
//		
//		lb_analy.setBounds(x, y, lb_width, height);
//		
//		JScrollPane scroll_analy = new JScrollPane(txt_frame);
//		
//		scroll_analy.setBounds(x + lb_width, y, 680, height+50);
//		
//		//panel.add(scroll_analy);
//		
//		lb_status.setBounds(x + lb_width, y+height+50, 300, 30);
//		lb_status.setText("前置机状态，前置机监听端口");
//		
		init_table();
		
		
	}


	
	
	public static void main(String[] args) {
		
	}



}
