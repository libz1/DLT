package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;


import base.BaseFrame;
import base.CommonObjectList;
import base.InfoClass;

import com.eastsoft.fio.ReadWriteExcel;
import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DebugSwing;

import entity.Constant;
import entity.Meter;
import entity.TransImplMeter;
import frame.Frame698;
import frame.FrameNew;
import socket.SocketServer;
import util.DB;
import util.Publisher;
import util.Util698;
import util.Util698.NewObjAction;

/**
 * �������͹���
 * 
 * @author xuky
 * @version 2016.10.13
 * 
 */
public class MeterOfTermCRUD extends BaseFrame implements ActionListener,Observer {

	public JTable table;
	DefaultTableModel defaultModel;
	public JButton[] buttonArr;
	
	private JTextField txt_search;
	public JButton bt_search;
	private JLabel lb_msg;
	
	CommonObjectList<Meter> meterList;

	// �����б��е���ʾ�ֶΡ�������ϸ��Ϣ���е���ʾ�ֶ�
	String[] colNames_table, colNames_info;
	
	// ��ϸ���ݶ���1���б��·���ʾ��ϸ�����ã�2��������������ʾ��ϸ������
	InfoClass info_detail;
	
	// list������
	NewObjAction newobj_act;
	
	// table�ؼ�����ʾ�����ݶ�Ӧ���ֶ������б�
	String table_columns, export_columns;
	
	// ���������Ϣ
	int MeterCount = 0, currentMeter = 0;
	
	@Override
	protected void init() {
		
		Publisher.getInstance().addObserver(this);
		
		// ����ע�⣺��������е�ǰ������ĸ�ֵ���룬��Ҫ����֮ǰ���ඨ�������  
		// ��ֹ�����磺public JTable table = null;
		// ��ֹ�����磺String[] colNames_info1 = { "code", "��code", "˳���", "��������", "��������;noButton", "Ĭ��ֵ",		"���ݶ���;TextArea", "��ע" };
		
		String[] colNames_info1 = { "�������","ͨ�ŵ�ַ","������;code:PortRate","��Լ����;code:ProtocolType",
				"�˿�;code:Port","ͨ������","���ʸ���",
				"�û�����","���߷�ʽ;code:Type2","���ѹ","�����","�ɼ�����ַ","�ʲ���","PT","CT" };
		
		export_columns = "measureNo,meterCode,portRate,protocolType,port,pwd,fee,type1,type2,ratedVoltage,ratedCurrent,assetsNo,collectCode,PT,CT";

		colNames_info = Util698.setArrayData(colNames_info1);

		// ע��colNames_table1��Ҫ��columns��ƥ��
		String[] colNames_table1 = { "ID", "�������","ͨ�ŵ�ַ","������","��Լ����","�˿�","�û�����","���߷�ʽ","�ɼ�����ַ" };
		table_columns = "ID,measureNo,meterCode,portRate,protocolType,port,type1,type2,collectCode";
		colNames_table = Util698.setArrayData(colNames_table1);
		
		info_detail = new InfoClass(colNames_info);
		info_detail.setTrans(new TransImplMeter());
		// �ڽ����з��� �ն���ϸ��Ϣ��ʾ�ؼ�
		panel.add(info_detail.panel);
		info_detail.panel.setBounds(10, 600 - 270-50, 780, 500);

		txt_search= new JTextField(100);
		panel.add(txt_search);
		txt_search.setBounds(350, 20, 100, 30);

		bt_search= new JButton("���Ҷ�λ");
		panel.add(bt_search);
		bt_search.setBounds(460, 20, 100, 30);
		bt_search.addActionListener(this);
		
		
		//dataTypeUserList = new DataTypeUserList("FromDB");
		
		// 
		newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		meterList = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='2'","order by measureNo");
		
		meterList.setExportColmunNames(colNames_info);
		meterList.setExportColmuns(export_columns);
		
		
		Object[][] data1 = DB.getInstance().getDataList("meter","terminalID='1' and archiveType='2'","order by measureNo",new Meter(),table_columns);
		defaultModel = new DefaultTableModel(data1, colNames_table);
		table = new JTable(defaultModel);
		// ���ñ��Ĵ�С
		// table.setPreferredScrollableViewportSize(new Dimension(300, 80));
		table.setRowHeight(20);
		// xuky? �޸�table�е�������ɫ��Ŀǰ���ڵ����⣬�޷��޸ı�������������ɫ
		table.setForeground(new Color(0, 114, 198));
		// �������ڹ��������
		JScrollPane scroll = new JScrollPane(table);

		// xuky 2013.10.25 ����������
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(
				defaultModel);
		table.setRowSorter(sorter);

		// ��ӱ���ѡ���¼�������
		ListSelectionModel model;
		model = table.getSelectionModel();
		model.addListSelectionListener(new SelectRowListener());

		setTableColumnWidth();

		// ���ܰ�ť����
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		panel_buttons.setBackground(Color.white);
		int buttionNum = 6;
		buttonArr = new JButton[buttionNum];
		Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < buttionNum; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr[i] = new JButton();
			buttonArr[i].setForeground(Color.white);
			buttonArr[i].setBackground(new Color(0, 114, 198));
			buttonArr[i].setFont(font);
			panel_buttons.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);
		}

		buttonArr[0].setText("��ѯ�ն˵����");
		buttonArr[1].setText("����ն˵����");
		//buttonArr[2].setText("ɾ��");
		//buttonArr[3].setText("ѡ��");
		
//		buttonArr[4].setText("����");
//		buttonArr[5].setText("����");

		buttonArr[3].setVisible(false);

		buttonArr[0].setBounds(110-105, 15, 150, 30);
		buttonArr[1].setBounds(110-105 + 5 + 150, 15, 150, 30);
		
		//buttonArr[2].setBounds(320-105-40, 15, 80, 30);
		//buttonArr[3].setBounds(5, 15, 80, 30);
		
		//bt_search.setBounds(560, 20, 100, 30);
		
		
//		buttonArr[4].setBounds(460+105, 20, 80, 30);
//		buttonArr[5].setBounds(460+105+80+5, 20, 80, 30);
//		panel.add(buttonArr[4]);
//		panel.add(buttonArr[5]);
		
		// �ڽ�������Ӱ�ť��������б�����
		panel.add(panel_buttons);
		panel.add(scroll);
		panel_buttons.setBounds(5, 5, 420, 50);
		scroll.setBounds(5, 70, 800 - 30, 600 - 400);
		
		// xuky Ĭ��ѡ�е�һ��
		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}
		
		lb_msg = new JLabel("�������:0");
		lb_msg.setHorizontalAlignment(SwingConstants.LEFT);
		lb_msg.setBounds(460+105, 20, 300, 30);
		panel.add(lb_msg);
	}

	private void setTableColumnWidth() {
		//String[] colNames_table1 = { "ID", "�������","ͨ�ŵ�ַ","������","��Լ����","�˿�","�û�����","���߷�ʽ","�ɼ�����ַ" };		// �����п��
		
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		
	}
	
	private void importFromExcel() {
		
		String fileName = DebugSwing.fileChoose();
		String[][] data = null;
		if (!fileName.equals("")) {
			data = ReadWriteExcel.excel2StringArray(fileName);
			meterList.deleteAll();
			meterList.converFormStringArray(data);
			meterList.addAll();
			//meters = new MeterList(TerminalID, data);
		}
		refresh_List();
		//meters.saveMeters(TerminalID);
		//setTableData(TerminalID);
	}

	
	private void export2Excel() {
		// ����ǰ�������ݽ���һ�����ã����ղ���������չʾ
		//setTableData(TerminalID);
		
		String filePath = DebugSwing.directorChoose();
		if (!filePath.equals("")) {
			String fileName = filePath + "�����.xls";

			// ��������ת��Ϊ�ַ�������
			String[][] data = meterList.getStringArray();

			// ���ַ�������ת��Ϊexcel�ļ�
			String ret = ReadWriteExcel.stringArray2Excel(data, fileName);

			// ֮ǰ���ϵĺ�����������е����ݵ���Ϊexcel�ļ�
			// String ret = ReadWriteUtil.table2Excel(colNames,
			// defaultModel,fileName);

			// ���ݷ��ؽ���������û�����
			if (ret.equals(""))
				DebugSwing.showMsg("�����������\"" + fileName + "\"�ɹ���");
			else
				DebugSwing.showMsg("���������ʧ�ܣ�" + ret);
		}

	}
	
	
	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		
		if (e.getActionCommand().equals("����")) {
			if (JOptionPane.showConfirmDialog(null,
					"�Ƿ���ⲿ���������������������Ḳ�ǵ�ǰ�ĵ��������!",
					"������ʾ", JOptionPane.OK_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) 
				importFromExcel();
		}
		
		if (e.getActionCommand().equals("����")) {
			export2Excel();
		}
		
		if (e.getActionCommand().equals("���Ҷ�λ")) {
			
			String txt = txt_search.getText();
			
			if (txt.equals("")){
				refresh_List();
				return;
			}
			Boolean find = false;
			
			for(int i=table.getSelectedRow()+1;i<table.getRowCount();i++){
				String data = (String)table.getValueAt(i, 2);
				data = data.toUpperCase();
				txt = txt.toUpperCase();
				if ( data.indexOf(txt) >= 0){
					table.getSelectionModel().setSelectionInterval(i, i);
					Rectangle rect = table.getCellRect(i, 0, true);
					table.scrollRectToVisible(rect);
					find = true; 
					break;
				}
			}
			
			if (!find){
				try{
					int code = DataConvert.String2Int(txt);
					int id = DB.getInstance().getDataTypeUserIDByCode(code);
					if (id != -1){
						int[] ids = new int[200];
						int num = DB.getInstance().getTreeDataDeep(code,ids,0);
						String adds = "";
						for(int i=0;i<num;i++)
							adds += ids[i]+";";
						adds = DataConvert.int2String(id)+";"+adds;

						Object[][] data1 = DB.getInstance().getDataTypeUserListByIDs(adds);
						refresh_List(data1);
					}
				}
				catch (Exception excep){
					excep.printStackTrace();
				}
				
			}		
			
		}
		if (e.getActionCommand().equals("��ѯ�ն˵����")) {
			
			// ɾ�����ݿ��е��ն˵������Ϣ
			meterList.deleteAll();
			// ˢ�½����е�����
			refresh_List();
			
			// ���Ͳ�ѯ���ģ����Ȳ�ѯ�������
			// ���Ͳ�ѯ�����Ϣ���ģ��ȴ���������
			//
			Frame698 frame698 = new Frame698();
			String s_APDU = "05 01 00 ,60 00 03 00 ,00";
			frame698.getAPDU().init(s_APDU);
			String send_frame = frame698.getFrame();
			// 	ֱ�ӷ��ͼ��ɣ���sendData�н�����·ѡ��
			SocketServer.sendData(send_frame);
			
			currentMeter = 0;
		}
				
		if (e.getActionCommand().equals("����ն˵����")) {
			if (JOptionPane.showConfirmDialog(null,
					"�Ƿ�ȷ��ɾ���ն��е����е������",
					"ɾ����ʾ", JOptionPane.OK_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
				// ɾ�����ݿ��е��ն˵������Ϣ
				meterList.deleteAll();
				// ˢ�½����е�����
				refresh_List();
				
				Frame698 frame698 = new Frame698();
				String s_APDU = "07 01 00 ,60 00 86 00 ,00,00";

				frame698.getAPDU().init(s_APDU);
				String send_frame = frame698.getFrame();
				// 	ֱ�ӷ��ͼ��ɣ���sendData�н�����·ѡ��
				SocketServer.sendData(send_frame);
				
			}
			
		}
		
		
		
		if (e.getActionCommand().equals("ɾ��")) {

			int rowcount = defaultModel.getRowCount();
			if (rowcount > 0) {
				// xuky ɾ����ǰ��
				int rowCount = table.getRowCount(); 
				int deleterow = table.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,
						"�Ƿ�ȷ��ɾ��\"" + table.getValueAt(deleterow, 1) +" "+ table.getValueAt(deleterow, 2) + "\"",
						"ɾ����ʾ", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					meterList.delete((int) table.getValueAt(deleterow, 0));
					// ��λ��ɾ���е�ǰһ��deleterow-1
					if (rowCount != deleterow){
						int id = (int)table.getValueAt(deleterow-1, 0);
						refresh_List(id);
					}
				}
			}
		}
	}

	private void refresh_List(int id) {
		refresh_List();

		int rowNum = table.getRowCount();
		for (int i = 0; i < rowNum; i++) {
			if ((int) table.getValueAt(i, 0) == id) {
				// xuky 2016��08.25 ������ĳ�У�������Ϊtable������scrollpanle�У���ʱ����Ч
				// table_attr.setRowSelectionInterval(i, i);

				// �ο�http://blog.csdn.net/dancen/article/details/7379847
				table.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table.getCellRect(i, 0, true);
				table.scrollRectToVisible(rect);
				break;
			}
		}
	}
	
	
	public void selectRow(int code){
		int rowNum = table.getRowCount();
		for (int i = 0; i < rowNum; i++) {
			if ((int) table.getValueAt(i, 1) == code) {
				// xuky 2016��08.25 ������ĳ�У�������Ϊtable������scrollpanle�У���ʱ����Ч
				// table_attr.setRowSelectionInterval(i, i);

				// �ο�http://blog.csdn.net/dancen/article/details/7379847
				table.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table.getCellRect(i, 0, true);
				table.scrollRectToVisible(rect);
				break;
			}
		}
		
	}
	
	private void refresh_List() {
		newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		meterList = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='2'","order by measureNo");
		meterList.setExportColmunNames(colNames_info);
		meterList.setExportColmuns(export_columns);
		
		
		Object[][] data1 = DB.getInstance().getDataList("meter","terminalID='1' and archiveType='2'","order by measureNo",new Meter(),table_columns);
		refresh_List(data1);
	}
	

	private void refresh_List(Object[][] data1) {
		defaultModel.setDataVector(data1, colNames_table);
		if (table.getRowCount() > 0)
			table.setRowSelectionInterval(0, 0);
		setTableColumnWidth();
	}

	private class SelectRowListener implements ListSelectionListener {
		// ���ø��ڲ��������������¼�Դ�������¼� ���ڴ����¼�����ģ�黯
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			//panel.add(info_detail.panel);
			//info_detail.setBounds(10, 600 - 270-50, 780, 500);
			setDetailData();
		}
	}
	
	private void setDetailData(){
		int row = table.getSelectedRow();
		if (row >= 0) {
			int id = (int) table.getValueAt(row, 0);
			Meter dtu = (Meter) meterList.getOne(id);
			info_detail.setData(dtu);
		}
	}
	
	// ������������ѡ�񴰿�ʱ������ʾ���ӵȰ�ť
	public void DisableEdit() {
		buttonArr[0].setVisible(false);
		buttonArr[1].setVisible(false);
		buttonArr[2].setVisible(false);
		buttonArr[3].setVisible(true);		
	}
	
	public static void main(String[] args) {
		BaseFrame mainFrame = new MeterOfTermCRUD();
		mainFrame.getPanel().setBounds(0, 0, 800, 600);
		JFrame frame = new JFrame();
		// frame.setTitle(buttonName);
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(800, 600);
		frame.setVisible(true);
		DebugSwing.center(frame);
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
			Frame698 frame698 = new Frame698(frame);
			
			if (frame698.getaPDU().getChoiseFlag()==135 && frame698.getaPDU().getChoiseFlag_1()==1){
				String data = frame698.getaPDU().getNextData();
				// 60008600 00 00 00 00
				if (data.substring(0, 8).equals("60008600")){
					if (data.substring(8, 10).equals("00")){
						DebugSwing.showMsg("ɾ���ն˵�����ɹ���");
					}
				}
			}
			
			if (frame698.getaPDU().getChoiseFlag()==133 && frame698.getaPDU().getChoiseFlag_1()==1){
				String data = frame698.getaPDU().getNextData();
				if (data.substring(0, 8).equals("60000300")){
					if (data.substring(8, 10).equals("01")){
						String temp = data.substring(12, 16);
						MeterCount = DataConvert.hexString2Int(temp);
						lb_msg.setText("���������"+MeterCount);
						if (MeterCount >= 0){
							// �������������㣬����Ҫִ�г������������
							// �����漰����֡��γ��������
							// �������Ľ�������浽���ݿ��У�չʾ�ڽ�����
							
							// ��ȡȫ�������
							Frame698 frame698_send = new Frame698();
							String s_APDU = "05 01 00 60 00 02 00 00 ";
							//               05 01 00 ,60 00 03 00 ,00
							frame698_send.getAPDU().init(s_APDU);
							String send_frame = frame698_send.getFrame();
							// 	ֱ�ӷ��ͼ��ɣ���sendData�н�����·ѡ��
							SocketServer.sendData(send_frame);
						} 
						else{
							// ����������Ϊ�㣬����Ҫ������ݿ��е��ն˵������Ϣ
						}
					}
				}
				
				
				if (data.substring(0, 8).equals("60000200")){
					if (data.substring(8, 10).equals("01")){
						
						dealMeterData(data.substring(12));
						
//						int arrayNum = DataConvert.hexString2Int(data.substring(12, 14));
//						String temp = data.substring(14);
//						int i = 0;
//						for( i=0;i<arrayNum;i++){
//							lb_msg.setText("���������"+MeterCount + " ��ǰ��������"+(i+1));
//							Object[] o = FrameNew.getObject(54,temp);
//							Meter meter = (Meter)o[0];
//							meter.setArchiveType(Constant.ARCHIVE_TER);
//							int id = meterList.getUseableID();
//							meter.setID(id);
//							// Ӧ�ý���meter��ӵ����ݿ���
//							meterList.add(meter);
//							temp = (String)o[1];
//						}
//						// һ��ˢ�½����е�����
//						refresh_List();
//						if (MeterCount <= (i+1))
//							DebugSwing.showMsg("��ȡ�ն˵�����ɹ���");
					}
				}
				
			}
			

			if (frame698.getaPDU().getChoiseFlag()==133 && frame698.getaPDU().getChoiseFlag_1()==5){
				String data = frame698.getaPDU().getNextData();
				// ��֡��������
				//00 00 01 01 01 60 00 02 00 01 01 1E 02 04 12 00 01 02 0A 55 07 05 00 00 00 00 00 01 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 0A 55 07 05 00 00 00 00 00 02 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 0A 55 07 05 00 00 00 00 00 03 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 04 02 0A 55 07 05 00 00 00 00 00 04 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 05 02 0A 55 07 05 00 00 00 00 00 05 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 06 02 0A 55 07 05 00 00 00 00 00 06 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 07 02 0A 55 07 05 00 00 00 00 00 07 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 08 02 0A 55 07 05 00 00 00 00 00 08 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 09 02 0A 55 07 05 00 00 00 00 00 09 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 0A 02 0A 55 07 05 00 00 00 00 00 10 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 0B 02 0A 55 07 05 00 00 00 00 00 11 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 0C 02 0A 55 07 05 00 00 00 00 00 12 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 0D 02 0A 55 07 05 00 00 00 00 00 13 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 0E 02 0A 55 07 05 00 00 00 00 00 14 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 0F 02 0A 55 07 05 00 00 00 00 00 15 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 10 02 0A 55 07 05 00 00 00 00 00 16 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 11 02 0A 55 07 05 00 00 00 00 00 17 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 12 02 0A 55 07 05 00 00 00 00 00 18 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 13 02 0A 55 07 05 00 00 00 00 00 19 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 14 02 0A 55 07 05 00 00 00 00 00 20 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 15 02 0A 55 07 05 00 00 00 00 00 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 16 02 0A 55 07 05 00 00 00 00 00 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 17 02 0A 55 07 05 00 00 00 00 00 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 18 02 0A 55 07 05 00 00 00 00 00 24 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 19 02 0A 55 07 05 00 00 00 00 00 25 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 1A 02 0A 55 07 05 00 00 00 00 00 26 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 1B 02 0A 55 07 05 00 00 00 00 00 27 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 1C 02 0A 55 07 05 00 00 00 00 00 28 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 1D 02 0A 55 07 05 00 00 00 00 00 29 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 02 04 12 00 1E 02 0A 55 07 05 00 00 00 00 00 30 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 01 16 01 12 00 00 12 00 00 02 04 55 07 05 00 00 00 00 00 00 09 00 12 00 01 12 00 01 01 00 00 00 
//				��ĩ֡��־=>0
//						����֡���=>1
//						����֡����(SEQUENCE OF A-ResultNormal)=>1
//						�������=>1
//						������OAD(�ɼ��������ñ�)=>60 00 02 00 
//						������Get-Result(DATA)=>1
//						��������Data=>
//						����������array(1-0x01)=>30
				String endFlag = data.substring(0,2);
				String frameNo = data.substring(2,6);
				dealMeterData(data.substring(22));
				if (endFlag.equals("00")){
					// ����������������
					// 05 05 00 +frameNo+ 00
					// ��ȡȫ�������
					Frame698 frame698_send = new Frame698();
					String s_APDU = "05 05 00" +frameNo+ "00";
					frame698_send.getAPDU().init(s_APDU);
					String send_frame = frame698_send.getFrame();
					// 	ֱ�ӷ��ͼ��ɣ���sendData�н�����·ѡ��
					SocketServer.sendData(send_frame);
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void dealMeterData(String data){
		int arrayNum = DataConvert.hexString2Int(data.substring(0, 2));
		String temp = data.substring(2);
		int i = 0;
		FrameNew frameNew = new FrameNew(54);
		List addMeterList = new ArrayList<Meter>();
		int id = meterList.getUseableID();
		for( i=0;i<arrayNum;i++){
			currentMeter ++;
			lb_msg.setText("���������"+MeterCount + " ��ǰ��������"+currentMeter);
			
			// frameNew.getObject�������������ݣ��õ��������ݣ��õ����µı�������
			Meter meter = new Meter();
			Object[] o = frameNew.getObject(temp,meter);
			meter = (Meter)o[0];
			meter.setArchiveType(Constant.ARCHIVE_TER);
			meter.setID(id);
			id ++;
			// Ӧ�ý���meter��ӵ����ݿ���
			//meterList.add(meter);
			addMeterList.add(meter);
			temp = (String)o[1];
		}
		// xuky 2016.11.06 ������ӵ������
		meterList.addAll(addMeterList);
		// һ��ˢ�½����е�����
		refresh_List();
		if (MeterCount <= currentMeter)
			DebugSwing.showMsg("��ȡ�ն˵�����ɹ���");
		
	}

}
