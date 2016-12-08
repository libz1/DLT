package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DebugSwing;

import base.BaseFrame;
import base.CommonObjectList;
import base.InfoClass;
import entity.Constant;
import entity.Meter;
import entity.TransImplMeter;
import frame.Frame698;
import frame.FrameNewList;
import socket.SocketServer;
import util.Publisher;
import util.Util698;
import util.Util698.NewObjAction;

/**
 * 电表档案同步类
 * 
 * @author xuky
 * @version 2016-10-24
 * 
 */

public class MeterCompare extends BaseFrame implements ActionListener,Observer {

	private JTable table;
	private DefaultTableModel defaultModel;
	public JButton[] buttonArr;
	// 两个电表详细信息控件，用于数据对照，例如一个是软件电表档案、一个是终端电表档案
	private InfoClass meterInfoFrame, meterInfoFrame1;
	JLabel[] labelArr;
	protected JCheckBox checkbox;
	
	int WINDOWWIDTH,WINDOWHEIGHT;

	// 电表列表 分别是：软件存储的电表列表、从终端读取的电表列表、准备向终端发送的电表列表
	private CommonObjectList meters_Software, meters_Terminal, meters_Send;

	private String funState = ""; // 功能码标志

	// 四表电表档案集合
	FrameNewList frameNew_List;

	// 同步的进度变量
	int synIndex = 0;
	
	// 进行电表信息匹配时，需要剔除的字段信息
	String excepColums;

	@Override
	protected void init() {
		
		Publisher.getInstance().addObserver(this);
		
		WINDOWWIDTH = 800;
		WINDOWHEIGHT=600;
		
		excepColums = "id,archiveType,flag,optTime,meterType,usrMeterType,addr,gisInfo,note";
		
		// 功能按钮设置
		JPanel buttons = new JPanel(null);
		buttonArr = new JButton[15];
		Font font = new Font("宋体", Font.BOLD, 13);
		for (int i = 0; i < 15; i++) { // 通过一个循环,对按钮数组中的每一个按钮实例化.
			buttonArr[i] = new JButton();
			//buttonArr[i].setForeground(Color.white);
			//buttonArr[i].setBackground(new Color(0, 114, 198));
			buttonArr[i].setFont(font);

			buttons.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);

		}

		buttonArr[0].setText("软件V终端");
		buttonArr[3].setText("正向同步");
		buttonArr[4].setText("反向同步");
		buttonArr[5].setText("单只加表");
		buttonArr[0].setBounds(5, 5, 100, 20);

		buttonArr[3].setBounds(110, 5, 100, 20);
		buttonArr[4].setBounds(215, 5, 100, 20);
//		buttonArr[5].setBounds(350, 5, 100, 20);

		// 在界面中添加按钮区域、添加列表区域
		panel.add(buttons);
		buttons.setBounds(5, 2, 770, 28);
		
		String[] colNames_info1 = { "配置序号","通信地址","波特率;code:PortRate","规约类型;code:ProtocolType",
				"端口;code:Port","通信密码","费率个数",
				"用户类型","接线方式;code:Type2","额定电压","额定电流","采集器地址","资产号","PT","CT" };
		String[] colNames_info = Util698.setArrayData(colNames_info1);
		
		// 在界面中放置 终端详细信息显示控件
		//meterInfoFrame = new MeterInfoFrame();
		meterInfoFrame = new InfoClass(colNames_info,3);
		meterInfoFrame.setTrans(new TransImplMeter());
		panel.add(meterInfoFrame.panel);
		meterInfoFrame.panel.setBounds(20, 200, 800,210);
		

		meterInfoFrame1 = new InfoClass(colNames_info,3);
		meterInfoFrame1.setTrans(new TransImplMeter());
		panel.add(meterInfoFrame1.panel);
		meterInfoFrame1.panel.setBounds(20, 370, 800,210);

		labelArr = new JLabel[15];
		font = new Font("宋体", Font.BOLD, 13);
		for (int i = 0; i < 15; i++) { // 通过一个循环,标签钮数组中的每一个按钮实例化.
			labelArr[i] = new JLabel();
			labelArr[i].setForeground(new Color(0, 114, 198));
			labelArr[i].setFont(font);
			panel.add(labelArr[i]);
		}
		labelArr[0].setBounds(80, WINDOWHEIGHT - 360+125, 600, 10);
		labelArr[0].setText("----------------------------------------------------------------------");

		labelArr[1].setBounds(5, WINDOWHEIGHT - 385-10-8, 600, 20);
		labelArr[1].setText("[1]");

		labelArr[2].setBounds(5, WINDOWHEIGHT - 245+5, 600, 20);
		labelArr[2].setText("[2]");

		//labelArr[3].setBounds(530, WINDOWHEIGHT - 365, 600, 20);
		//labelArr[3].setText("软件V终端 数据比较");

		labelArr[4].setBounds(530, 430, 700, 20);
		labelArr[4].setText("");
		labelArr[5].setBounds(530, 460, 600, 20);
		labelArr[5].setText("");
		
		labelArr[6].setBounds(460, 5, 200, 20);
		labelArr[6].setText("加表结果");
		buttons.add(labelArr[6]);
		
		showTable();

	}

	private class SelectRowListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			int id = 0;
			if (table.getSelectedRow() >= 0) {
				id = (int) table.getValueAt(table.getSelectedRow(), 0);
				meterInfoFrame.setData(meters_Software.getOne(id));
				
				id = (int) table.getValueAt(table.getSelectedRow(), 1);
				meterInfoFrame1.setData(meters_Terminal.getOne(id));
			}
		}
	}


	private void showTable() {

		// 在列表中显示的字段信息（仅显示部分信息）
		String[] colNames = { "ID1", "ID2", "测量点号", 
				"地址-1", "采-1", "协议-1","波特率-1","端口-1","密码-1","费率-1","用户类型-1","接线-1","电压-1","电流-1","PT-1","CT-1","资产号-1",
				"地址-2", "采-2", "协议-2","波特率-2","端口-2","密码-2","费率-2","用户类型-2","接线-2","电压-2","电流-2","PT-2","CT-2","资产号-2","判别" };

		// 数据无实际内容
		String[][] data1 = new String[0][32];

		defaultModel = new DefaultTableModel(data1, colNames);
		table = new JTable(defaultModel);

		// 设置表格的大小
		table.setPreferredScrollableViewportSize(new Dimension(300, 80));
		table.setRowHeight(15);

		// xuky 修改table中的字体颜色，目前存在的问题，无法修改标题栏的字体颜色
		table.setForeground(new Color(0, 114, 198));

		// important xuky 修改指定行的颜色,当指定行的内容为指定数据时，显示特别的颜色
		DefaultTableCellRenderer dtc = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {

				if (table.getValueAt(row, 31).equals("不等"))
					setForeground(Color.GREEN);
				else
					setForeground(new Color(0, 114, 198));

				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);

			}
		};
		// 每一列都设置为有差异的标记色-绿色
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(dtc);
		}

		// 添加表格的选中事件监听器
		ListSelectionModel model;
		model = table.getSelectionModel();
		model.addListSelectionListener(new SelectRowListener());

		// xuky 先隐藏后面的列，然后隐藏前面的列
		TableColumnModel columnModel = table.getColumnModel();
		
		TableColumn column = columnModel.getColumn(0);
		column.setMinWidth(0);
		column.setMaxWidth(0);

		// 不显示 ID信息
		column = columnModel.getColumn(1);
		column.setMinWidth(0);
		column.setMaxWidth(0);
		
		//不显示以下信息
		for(int i=8;i<=16;i++){
			column = columnModel.getColumn(i);
			column.setMinWidth(0);
			column.setMaxWidth(0);
		}
		for(int i=22;i<=30;i++){
			column = columnModel.getColumn(i);
			column.setMinWidth(0);
			column.setMaxWidth(0);
		}
		

		// 表格放置在滚动面板中
		JScrollPane scroll = new JScrollPane(table);
		panel.add(scroll);
		// 123
		scroll.setBounds(5, 30, 765,170 );

		// xuky 默认选中第一行
		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}
		
		showTableData(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);

	}


	//填充列表控件的数据.
	private void showTableData(String aType1, String aType2) {
		
		while (defaultModel.getRowCount() > 0) {
			defaultModel.removeRow(defaultModel.getRowCount() - 1);
		}

		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		meters_Software = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='1'","order by measureNo");
		meters_Terminal = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='2'","order by measureNo");
		
//		String[] colNames = { "ID1", "ID2", "测量点号", 
//				"地址-1", "采-1", "协议-1","波特率-1","端口-1","密码-1","费率-1","用户类型-1","接线-1","电压-1","电流-1","PT-1","CT-1","资产号-1",
//				"地址-2", "采-2", "协议-2","波特率-2","端口-2","密码-2","费率-2","用户类型-2","接线-2","电压-2","电流-2","PT-2","CT-2","资产号-2","判别" };
		
		// 将第1份档案添加到列表中
		//for (Meter meter : meters_Software.getMeterList()) {
		for (Object object : meters_Software.getList()) {
			Meter meter = (Meter)object;
			Vector<Object> data = new Vector<Object>();
			data.add(meter.getID());
			data.add(0);
			data.add(meter.getMeasureNo());
			
			data.add(meter.getMeterCode());
			data.add(meter.getCollectCode());
			data.add(meter.getProtocolType());
			data.add(meter.getPortRate());
			data.add(meter.getPort());
			data.add(meter.getPwd());
			data.add(meter.getFee());
			data.add(meter.getType1());
			data.add(meter.getType2());
			data.add(meter.getRatedVoltage());
			data.add(meter.getRatedCurrent());
			data.add(meter.getPT());
			data.add(meter.getCT());
			data.add(meter.getAssetsNo());
			for(int i=1;i<=14;i++){
				data.add("");
			}
			
			data.add("不等");
			defaultModel.addRow(data);
		}

		// 将第2份档案添加到列表中
		for (Object object : meters_Terminal.getList()) {
			Meter meter = (Meter)object;
			String no1 = meter.getMeasureNo();
			Boolean insert_flag = true;
			int insert_row = defaultModel.getRowCount();
			int equals = 0;

			// 遍历表中的数据，检查是否可以合并在一行中显示
			for (int i = 0; i < defaultModel.getRowCount(); i++) {
				String no2 = (String) defaultModel.getValueAt(i, 2);
				if (DataConvert.String2Int(no1) == DataConvert.String2Int(no2)) {
					// 测量点信息相等，可以在一行显示
					defaultModel.setValueAt(meter.getID(), i, 1);
					
					defaultModel.setValueAt(meter.getMeterCode(), i, 17);
					defaultModel.setValueAt(meter.getCollectCode(), i, 18);
					defaultModel.setValueAt(meter.getProtocolType(), i, 19);
					defaultModel.setValueAt(meter.getPortRate(), i, 20);
					defaultModel.setValueAt(meter.getPort(), i, 21);
					defaultModel.setValueAt(meter.getPwd(), i, 22);
					defaultModel.setValueAt(meter.getFee(), i, 23);
					defaultModel.setValueAt(meter.getType1(), i, 24);
					defaultModel.setValueAt(meter.getType2(), i, 25);
					defaultModel.setValueAt(meter.getRatedVoltage(), i, 26);
					defaultModel.setValueAt(meter.getRatedCurrent(), i, 27);
					defaultModel.setValueAt(meter.getPT(), i, 28);
					defaultModel.setValueAt(meter.getCT(), i, 29);
					defaultModel.setValueAt(meter.getAssetsNo(), i, 30);
				
					// 需要判断两个对象是否相等
					int id1 = (int) defaultModel.getValueAt(i, 0);
					Meter meter1 = (Meter)meters_Software.getOne(id1);
					
					if (Util698.objEquals(meter1, meter,excepColums))
						defaultModel.setValueAt("相等", i, 31);
				
					insert_flag = false;

					break;
				}
				if (DataConvert.String2Int(no1) < DataConvert.String2Int(no2)) {
					// 如果发现测量点号小于列表中的测量点号，则需要执行插入操作
					insert_flag = true;
					insert_row = i;
					break;
				}
			}
			// 插入数据
			if (insert_flag) {
				Vector<Object> data = new Vector<Object>();
				data.add(0);
				data.add(meter.getID());
				data.add(meter.getMeasureNo());
				
				for(int i=1;i<=14;i++){
					data.add("");
				}
				
				data.add(meter.getMeterCode());
				data.add(meter.getCollectCode());
				data.add(meter.getProtocolType());
				data.add(meter.getPortRate());
				data.add(meter.getPort());
				data.add(meter.getPwd());
				data.add(meter.getFee());
				data.add(meter.getType1());
				data.add(meter.getType2());
				data.add(meter.getRatedVoltage());
				data.add(meter.getRatedCurrent());
				data.add(meter.getPT());
				data.add(meter.getCT());
				data.add(meter.getAssetsNo());
				
				data.add("不等");
				defaultModel.insertRow(insert_row, data);
			}

		}

		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}

	}

	//对ActionListener接口的实现函数 相应按钮的点击
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// 各个功能按钮的点击事件
		if (e.getActionCommand().equals("软件V终端")) {
			labelArr[3].setText(e.getActionCommand() + " 数据比较");
			showTableData(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);
			funState = "软件V终端";
		}
		
		if (e.getActionCommand().equals("反向同步")) {
				// JOptionPane.OK_OPTION
				if (JOptionPane.showConfirmDialog(null, "是否确认用终端的档案覆盖软件的档案！",
						"系统提示", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					// 对数据进行反向同步
					reverseSync(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);
				}
		}

		if (e.getActionCommand().equals("正向同步")) {
			// 在MeterPrameterWindow类中执行相关 脚本
			syncToTerminal();
		}
	}
		
	//将软件档案同步到集中器中
	public Boolean syncToTerminal() {
		
		Boolean confirm = false;
		if (JOptionPane.showConfirmDialog(null, "是否确认用软件中的档案覆盖终端的档案！", "系统提示",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

			// 对数据进行正向同步 Sync函数会修改meters_Send的数据
			Sync(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);
			confirm = true;

		}
		return confirm;
	}
	
	//正向同步（将数据同步到设备中）
	private void Sync(String aType1, String aType2) {
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" begin");

		// 正向同步
		// 先删除（多余的、不同的）后增加的原则，将需要操作的数据构造为1个集合，进行实际收发报文操作
		// meters_Send集合是“最终所有需要维护的终端信息”
	
		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		meters_Send = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","archiveType='9999'","");

		Meter meter1 = null;

//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 1");
		
		// 按照软件中记录的终端电表档案进行遍历 ，用于删除多余数据
		for (Object object : meters_Terminal.getList()) {//终端，并未实际终端，而是上次档案同步时，存储在json文件中的表档案
			Meter meter = (Meter)object;
			String no = meter.getMeasureNo();
			Boolean delFlag = true;
			for (int i = 0; i < meters_Software.size(); i++) {
				meter1 = (Meter) meters_Software.getList().get(i);
				String no1 = meter1.getMeasureNo();
				if (DataConvert.String2Long(no) == DataConvert.String2Long(no1)) {
					if (Util698.objEquals(meter1,meter,excepColums))
						delFlag = false; // 只有在两者数据相等的情况下，才无需删除终端中对应测量点的数据
					break;
				}
			}
			
			// 找不到、数据不相等，都需要删除
			if (delFlag) {
				// 设置删除标记，为组织报文用
				meter.setFlag(Constant.FLAG_DEL);
				// 不向数据库中插入数据
				meters_Send.add(meter,true);
			}
		}
		
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 2");
		// xuky  清除meters_Terminal的数据
		meters_Terminal.deleteByCode(Constant.FLAG_DEL+",","getFlag,",true);

		// 按照软件中存储的终端档案进行遍历，用于增加 
		for (Object object : meters_Software.getList()) {
			Meter meter = (Meter)object;
			String no = meter.getMeasureNo();
			Boolean find = false;
			
			//meters_Terminal 中保留了所有无需操作的数据 
			for (Object object2 : meters_Terminal.getList()) {
				Meter meter2 = (Meter)object2;
				String no1 = meter2.getMeasureNo();
				if (DataConvert.String2Long(no) == DataConvert.String2Long(no1)) {
					find = true;
					break;
				}
			}
			if (!find) {
				Meter meter_new = new Meter();
				
				// 将软件中的终端信息写入新增终端对象
				Util698.objClone(meter,meter_new,"");
				// 设置增加标记，为组织报文用
				meter_new.setFlag(Constant.FLAG_INS);
				// 增加到列表中，但是不进行数据库相关操作
				meters_Send.add(meter_new,true);
			}
		}
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 3");
		// System.out.println("添加到终端中的电表："+meters_Send.converMeterListToString());
		// 根据meters_Send 得到对应的删除和增加报文
		// 需要考虑多次删除、多次增加的情况
		// 批处理、不断的进行相关操作
		// 对用户进行相关提示
		
		 frameNew_List = new FrameNewList(meters_Send);  //创建需要下发表计列表对象
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 4");
		
		 synIndex = 0;
		 sendSyncData();
		 
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 5");
		 
	}
	
	private void sendSyncData(){
		if (frameNew_List == null)
			return;
		
		if (synIndex < frameNew_List.getSize()){
			// 组织报文过程
			Frame698 frame698 = new Frame698();
			String s_APDU = frameNew_List.getFrame(synIndex);
			frame698.getAPDU().init(s_APDU);
			String send_frame = frame698.getFrame();
//			System.out.println("Sync send_frame=> 1 "+send_frame);
			synIndex++;

			// 显示进度信息
			String msg = "同步开始..."
					  + DataConvert.int2String(synIndex) + "/" +
					  DataConvert.int2String(frameNew_List.getFrameNum());
			labelArr[6].setText(msg);
			
			// 	直接发送即可，在sendData中进行链路选择
			SocketServer.sendData(send_frame);
		}
		else
			DebugSwing.showMsg("同步报文发送完成，请查询终端电表档案后重新进行数据比对");
	}
	
	@Override
	public void update(Observable o, Object arg) {
		String[] s = (String[]) arg;
		if (s[0].equals("recv frame") &&  s[1].equals("user data"))
			dealRecvData(arg);
	}
	
	private synchronized void dealRecvData(Object arg){
		String[] s = (String[]) arg;
		String  frame = s[2];
		frame = frame.replaceAll(" ","").replaceAll(",","");
		//frame = Util698.seprateString(frame, " ");
		try{
			
			
			// 增加终端后的回复报文
			//68 1A 00 C3 05 11 11 11 11 11 11 00 52 55 87 01 00 60 00 80 00 00 00 00 00 92 07 16
			Frame698 frame698 = new Frame698(frame);
			if (frame698.getaPDU().getChoiseFlag()==135 && frame698.getaPDU().getChoiseFlag_1()==1){
				String data = frame698.getaPDU().getNextData();
				if (data.substring(0, 8).equals("60008000"))
					if (data.substring(8, 10).equals("00")){
						//DAR=00表示成功  则继续发送后续同步报文
						sendSyncData();
					}
			}
			
			// 删除终端后的回复报文
			//68 21 00 C3 05 11 11 11 11 11 11 00 84 84 87 02 00 02 60 00 83 00 00 00 60 00 83 00 00 00 00 00 62 31 16
			if (frame698.getaPDU().getChoiseFlag()==135 && frame698.getaPDU().getChoiseFlag_1()==2){
				String data = frame698.getaPDU().getNextData();
				// 0,2是个数信息
				if (data.substring(2, 10).equals("60008300"))
					if (data.substring(10, 12).equals("00")){
						//DAR=00表示成功  则继续发送后续同步报文
						sendSyncData();
					}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

	//反向同步（将数据同步到软件中，进行存储）
	private void reverseSync(String aType1, String aType2) {
		// 反向同步
		// 反向同步 使用终端档案信息修改软件档案信息，最终数据保存
		// 以软件档案为模板 ，增加、修改、删除数据 //
		// 软件 档案保存
		
		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};

		// 比较两份档案的差异
		meters_Software = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='1'","order by measureNo");
		meters_Terminal = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='2'","order by measureNo");

		Meter meter1 = null;

		// 按照软件档案进行遍历，用来进行修改和标记“删除”
		List list = meters_Software.getList();
		System.out.println("reverseSync=> 1");
//		int num = 1;
		for (Object object : list) {
//			System.out.println("reverseSync=> for "+ num);
//			num++;
			Meter meter = (Meter)object;
			String no = meter.getMeasureNo();
			Boolean del = true;
			for (int i = 0; i < meters_Terminal.size(); i++) {
				Object obj = meters_Terminal.getList().get(i);
				meter1 = (Meter)obj;
				String no1 = meter1.getMeasureNo();
				if (DataConvert.String2Long(no) == DataConvert.String2Long(no1)) {
					if (!Util698.objEquals(meter1, meter,excepColums)){
						Util698.objClone(meter1, meter, "ID,archiveType");
						
//						meter.setMeasureNo(meter1.getMeasureNo());
//						meter.setMeterCode(meter1.getMeterCode());
//						meter.setCollectCode(meter1.getCollectCode());
//						meter.setProtocolType(meter1.getProtocolType());
//						meter.setPortRate(meter1.getPortRate());
//						meter.setPort(meter1.getPort());
//						meter.setPwd(meter1.getPwd());
//						meter.setFee(meter1.getFee());
//						meter.setType1(meter1.getType1());
//						meter.setType2(meter1.getType2());
//						meter.setRatedVoltage(meter1.getRatedVoltage());
//						meter.setRatedCurrent(meter1.getRatedCurrent());
//						meter.setPT(meter1.getPT());
//						meter.setCT(meter1.getCT());
//						meter.setAssetsNo(meter1.getAssetsNo());
						meters_Software.update(meter);
					}
					del = false;
					break;
				}
			}
			if (del)
				meter.setFlag(Constant.FLAG_DEL);
		}

		// 对于添加了删除标记的数据，集中进行删除
		meters_Software.deleteByCode(Constant.FLAG_DEL+",","getFlag,",true);

		// 按照终端档案进行遍历，用来进行增加
		for (Object object : meters_Terminal.getList()) {
			Meter meter = (Meter)object;
			String no = meter.getMeasureNo();
			Boolean find = false;
			for (Object obj : meters_Software.getList()) {
				Meter meter2 = (Meter)obj;
				String no1 = meter2.getMeasureNo();
				if (DataConvert.String2Long(no) == DataConvert.String2Long(no1)) {
					find = true;
					break;
				}
			}
			if (!find) {
				Meter meter_new = new Meter();
				
				Util698.objClone(meter, meter_new, "ID");
				meter_new.setID(meters_Software.getUseableID());
				meter_new.setArchiveType(Constant.ARCHIVE_SOFT);
				
//				meter_new.setMeasureNo(meter.getMeasureNo());
//				meter_new.setMeterCode(meter.getMeterCode());
//				meter_new.setCollectCode(meter.getCollectCode());
//				meter_new.setProtocolType(meter.getProtocolType());
//				meter_new.setPortRate(meter.getPortRate());
//				meter_new.setPort(meter.getPort());
//				meter_new.setPwd(meter.getPwd());
//				meter_new.setFee(meter.getFee());
//				meter_new.setType1(meter.getType1());
//				meter_new.setType2(meter.getType2());
//				meter_new.setRatedVoltage(meter.getRatedVoltage());
//				meter_new.setRatedCurrent(meter.getRatedCurrent());
//				meter_new.setPT(meter.getPT());
//				meter_new.setCT(meter.getCT());
//				meter_new.setAssetsNo(meter.getAssetsNo());
				meters_Software.add(meter_new);
			}
		}
		// 数据保存
		//meters_Software.saveMeters(TerminalID);

		// 界面中的数据进行更新
		showTableData(aType1, aType2);
		
		DebugSwing.showMsg("反向同步完成！");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TerminalCRUD terminalCRUD = new TerminalCRUD();
		MeterCompare meterCompare = new MeterCompare();
		JFrame frame = new JFrame();
		frame.setLayout(null);
		frame.add(meterCompare.panel);
		meterCompare.panel.setBounds(5, 5, 700, 700);
		meterCompare.panel.setVisible(true);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}



}
