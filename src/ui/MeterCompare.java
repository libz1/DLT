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
 * �����ͬ����
 * 
 * @author xuky
 * @version 2016-10-24
 * 
 */

public class MeterCompare extends BaseFrame implements ActionListener,Observer {

	private JTable table;
	private DefaultTableModel defaultModel;
	public JButton[] buttonArr;
	// ���������ϸ��Ϣ�ؼ����������ݶ��գ�����һ��������������һ�����ն˵����
	private InfoClass meterInfoFrame, meterInfoFrame1;
	JLabel[] labelArr;
	protected JCheckBox checkbox;
	
	int WINDOWWIDTH,WINDOWHEIGHT;

	// ����б� �ֱ��ǣ�����洢�ĵ���б����ն˶�ȡ�ĵ���б�׼�����ն˷��͵ĵ���б�
	private CommonObjectList meters_Software, meters_Terminal, meters_Send;

	private String funState = ""; // �������־

	// �ı���������
	FrameNewList frameNew_List;

	// ͬ���Ľ��ȱ���
	int synIndex = 0;
	
	// ���е����Ϣƥ��ʱ����Ҫ�޳����ֶ���Ϣ
	String excepColums;

	@Override
	protected void init() {
		
		Publisher.getInstance().addObserver(this);
		
		WINDOWWIDTH = 800;
		WINDOWHEIGHT=600;
		
		excepColums = "id,archiveType,flag,optTime,meterType,usrMeterType,addr,gisInfo,note";
		
		// ���ܰ�ť����
		JPanel buttons = new JPanel(null);
		buttonArr = new JButton[15];
		Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < 15; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr[i] = new JButton();
			//buttonArr[i].setForeground(Color.white);
			//buttonArr[i].setBackground(new Color(0, 114, 198));
			buttonArr[i].setFont(font);

			buttons.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);

		}

		buttonArr[0].setText("���V�ն�");
		buttonArr[3].setText("����ͬ��");
		buttonArr[4].setText("����ͬ��");
		buttonArr[5].setText("��ֻ�ӱ�");
		buttonArr[0].setBounds(5, 5, 100, 20);

		buttonArr[3].setBounds(110, 5, 100, 20);
		buttonArr[4].setBounds(215, 5, 100, 20);
//		buttonArr[5].setBounds(350, 5, 100, 20);

		// �ڽ�������Ӱ�ť��������б�����
		panel.add(buttons);
		buttons.setBounds(5, 2, 770, 28);
		
		String[] colNames_info1 = { "�������","ͨ�ŵ�ַ","������;code:PortRate","��Լ����;code:ProtocolType",
				"�˿�;code:Port","ͨ������","���ʸ���",
				"�û�����","���߷�ʽ;code:Type2","���ѹ","�����","�ɼ�����ַ","�ʲ���","PT","CT" };
		String[] colNames_info = Util698.setArrayData(colNames_info1);
		
		// �ڽ����з��� �ն���ϸ��Ϣ��ʾ�ؼ�
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
		font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < 15; i++) { // ͨ��һ��ѭ��,��ǩť�����е�ÿһ����ťʵ����.
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
		//labelArr[3].setText("���V�ն� ���ݱȽ�");

		labelArr[4].setBounds(530, 430, 700, 20);
		labelArr[4].setText("");
		labelArr[5].setBounds(530, 460, 600, 20);
		labelArr[5].setText("");
		
		labelArr[6].setBounds(460, 5, 200, 20);
		labelArr[6].setText("�ӱ���");
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

		// ���б�����ʾ���ֶ���Ϣ������ʾ������Ϣ��
		String[] colNames = { "ID1", "ID2", "�������", 
				"��ַ-1", "��-1", "Э��-1","������-1","�˿�-1","����-1","����-1","�û�����-1","����-1","��ѹ-1","����-1","PT-1","CT-1","�ʲ���-1",
				"��ַ-2", "��-2", "Э��-2","������-2","�˿�-2","����-2","����-2","�û�����-2","����-2","��ѹ-2","����-2","PT-2","CT-2","�ʲ���-2","�б�" };

		// ������ʵ������
		String[][] data1 = new String[0][32];

		defaultModel = new DefaultTableModel(data1, colNames);
		table = new JTable(defaultModel);

		// ���ñ��Ĵ�С
		table.setPreferredScrollableViewportSize(new Dimension(300, 80));
		table.setRowHeight(15);

		// xuky �޸�table�е�������ɫ��Ŀǰ���ڵ����⣬�޷��޸ı�������������ɫ
		table.setForeground(new Color(0, 114, 198));

		// important xuky �޸�ָ���е���ɫ,��ָ���е�����Ϊָ������ʱ����ʾ�ر����ɫ
		DefaultTableCellRenderer dtc = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {

				if (table.getValueAt(row, 31).equals("����"))
					setForeground(Color.GREEN);
				else
					setForeground(new Color(0, 114, 198));

				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);

			}
		};
		// ÿһ�ж�����Ϊ�в���ı��ɫ-��ɫ
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(dtc);
		}

		// ��ӱ���ѡ���¼�������
		ListSelectionModel model;
		model = table.getSelectionModel();
		model.addListSelectionListener(new SelectRowListener());

		// xuky �����غ�����У�Ȼ������ǰ�����
		TableColumnModel columnModel = table.getColumnModel();
		
		TableColumn column = columnModel.getColumn(0);
		column.setMinWidth(0);
		column.setMaxWidth(0);

		// ����ʾ ID��Ϣ
		column = columnModel.getColumn(1);
		column.setMinWidth(0);
		column.setMaxWidth(0);
		
		//����ʾ������Ϣ
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
		

		// �������ڹ��������
		JScrollPane scroll = new JScrollPane(table);
		panel.add(scroll);
		// 123
		scroll.setBounds(5, 30, 765,170 );

		// xuky Ĭ��ѡ�е�һ��
		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}
		
		showTableData(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);

	}


	//����б�ؼ�������.
	private void showTableData(String aType1, String aType2) {
		
		while (defaultModel.getRowCount() > 0) {
			defaultModel.removeRow(defaultModel.getRowCount() - 1);
		}

		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		meters_Software = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='1'","order by measureNo");
		meters_Terminal = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='2'","order by measureNo");
		
//		String[] colNames = { "ID1", "ID2", "�������", 
//				"��ַ-1", "��-1", "Э��-1","������-1","�˿�-1","����-1","����-1","�û�����-1","����-1","��ѹ-1","����-1","PT-1","CT-1","�ʲ���-1",
//				"��ַ-2", "��-2", "Э��-2","������-2","�˿�-2","����-2","����-2","�û�����-2","����-2","��ѹ-2","����-2","PT-2","CT-2","�ʲ���-2","�б�" };
		
		// ����1�ݵ�����ӵ��б���
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
			
			data.add("����");
			defaultModel.addRow(data);
		}

		// ����2�ݵ�����ӵ��б���
		for (Object object : meters_Terminal.getList()) {
			Meter meter = (Meter)object;
			String no1 = meter.getMeasureNo();
			Boolean insert_flag = true;
			int insert_row = defaultModel.getRowCount();
			int equals = 0;

			// �������е����ݣ�����Ƿ���Ժϲ���һ������ʾ
			for (int i = 0; i < defaultModel.getRowCount(); i++) {
				String no2 = (String) defaultModel.getValueAt(i, 2);
				if (DataConvert.String2Int(no1) == DataConvert.String2Int(no2)) {
					// ��������Ϣ��ȣ�������һ����ʾ
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
				
					// ��Ҫ�ж����������Ƿ����
					int id1 = (int) defaultModel.getValueAt(i, 0);
					Meter meter1 = (Meter)meters_Software.getOne(id1);
					
					if (Util698.objEquals(meter1, meter,excepColums))
						defaultModel.setValueAt("���", i, 31);
				
					insert_flag = false;

					break;
				}
				if (DataConvert.String2Int(no1) < DataConvert.String2Int(no2)) {
					// ������ֲ������С���б��еĲ�����ţ�����Ҫִ�в������
					insert_flag = true;
					insert_row = i;
					break;
				}
			}
			// ��������
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
				
				data.add("����");
				defaultModel.insertRow(insert_row, data);
			}

		}

		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}

	}

	//��ActionListener�ӿڵ�ʵ�ֺ��� ��Ӧ��ť�ĵ��
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// �������ܰ�ť�ĵ���¼�
		if (e.getActionCommand().equals("���V�ն�")) {
			labelArr[3].setText(e.getActionCommand() + " ���ݱȽ�");
			showTableData(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);
			funState = "���V�ն�";
		}
		
		if (e.getActionCommand().equals("����ͬ��")) {
				// JOptionPane.OK_OPTION
				if (JOptionPane.showConfirmDialog(null, "�Ƿ�ȷ�����ն˵ĵ�����������ĵ�����",
						"ϵͳ��ʾ", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					// �����ݽ��з���ͬ��
					reverseSync(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);
				}
		}

		if (e.getActionCommand().equals("����ͬ��")) {
			// ��MeterPrameterWindow����ִ����� �ű�
			syncToTerminal();
		}
	}
		
	//���������ͬ������������
	public Boolean syncToTerminal() {
		
		Boolean confirm = false;
		if (JOptionPane.showConfirmDialog(null, "�Ƿ�ȷ��������еĵ��������ն˵ĵ�����", "ϵͳ��ʾ",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

			// �����ݽ�������ͬ�� Sync�������޸�meters_Send������
			Sync(Constant.ARCHIVE_SOFT, Constant.ARCHIVE_TER);
			confirm = true;

		}
		return confirm;
	}
	
	//����ͬ����������ͬ�����豸�У�
	private void Sync(String aType1, String aType2) {
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" begin");

		// ����ͬ��
		// ��ɾ��������ġ���ͬ�ģ������ӵ�ԭ�򣬽���Ҫ���������ݹ���Ϊ1�����ϣ�����ʵ���շ����Ĳ���
		// meters_Send�����ǡ�����������Ҫά�����ն���Ϣ��
	
		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		meters_Send = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","archiveType='9999'","");

		Meter meter1 = null;

//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 1");
		
		// ��������м�¼���ն˵�������б��� ������ɾ����������
		for (Object object : meters_Terminal.getList()) {//�նˣ���δʵ���նˣ������ϴε���ͬ��ʱ���洢��json�ļ��еı���
			Meter meter = (Meter)object;
			String no = meter.getMeasureNo();
			Boolean delFlag = true;
			for (int i = 0; i < meters_Software.size(); i++) {
				meter1 = (Meter) meters_Software.getList().get(i);
				String no1 = meter1.getMeasureNo();
				if (DataConvert.String2Long(no) == DataConvert.String2Long(no1)) {
					if (Util698.objEquals(meter1,meter,excepColums))
						delFlag = false; // ֻ��������������ȵ�����£�������ɾ���ն��ж�Ӧ�����������
					break;
				}
			}
			
			// �Ҳ��������ݲ���ȣ�����Ҫɾ��
			if (delFlag) {
				// ����ɾ����ǣ�Ϊ��֯������
				meter.setFlag(Constant.FLAG_DEL);
				// �������ݿ��в�������
				meters_Send.add(meter,true);
			}
		}
		
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 2");
		// xuky  ���meters_Terminal������
		meters_Terminal.deleteByCode(Constant.FLAG_DEL+",","getFlag,",true);

		// ��������д洢���ն˵������б������������� 
		for (Object object : meters_Software.getList()) {
			Meter meter = (Meter)object;
			String no = meter.getMeasureNo();
			Boolean find = false;
			
			//meters_Terminal �б���������������������� 
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
				
				// ������е��ն���Ϣд�������ն˶���
				Util698.objClone(meter,meter_new,"");
				// �������ӱ�ǣ�Ϊ��֯������
				meter_new.setFlag(Constant.FLAG_INS);
				// ���ӵ��б��У����ǲ��������ݿ���ز���
				meters_Send.add(meter_new,true);
			}
		}
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 3");
		// System.out.println("��ӵ��ն��еĵ��"+meters_Send.converMeterListToString());
		// ����meters_Send �õ���Ӧ��ɾ�������ӱ���
		// ��Ҫ���Ƕ��ɾ����������ӵ����
		// ���������ϵĽ�����ز���
		// ���û����������ʾ
		
		 frameNew_List = new FrameNewList(meters_Send);  //������Ҫ�·�����б����
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 4");
		
		 synIndex = 0;
		 sendSyncData();
		 
//		System.out.println("Sync=>"+DateTimeFun.getDateTimeSSS() +" 5");
		 
	}
	
	private void sendSyncData(){
		if (frameNew_List == null)
			return;
		
		if (synIndex < frameNew_List.getSize()){
			// ��֯���Ĺ���
			Frame698 frame698 = new Frame698();
			String s_APDU = frameNew_List.getFrame(synIndex);
			frame698.getAPDU().init(s_APDU);
			String send_frame = frame698.getFrame();
//			System.out.println("Sync send_frame=> 1 "+send_frame);
			synIndex++;

			// ��ʾ������Ϣ
			String msg = "ͬ����ʼ..."
					  + DataConvert.int2String(synIndex) + "/" +
					  DataConvert.int2String(frameNew_List.getFrameNum());
			labelArr[6].setText(msg);
			
			// 	ֱ�ӷ��ͼ��ɣ���sendData�н�����·ѡ��
			SocketServer.sendData(send_frame);
		}
		else
			DebugSwing.showMsg("ͬ�����ķ�����ɣ����ѯ�ն˵���������½������ݱȶ�");
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
			
			
			// �����ն˺�Ļظ�����
			//68 1A 00 C3 05 11 11 11 11 11 11 00 52 55 87 01 00 60 00 80 00 00 00 00 00 92 07 16
			Frame698 frame698 = new Frame698(frame);
			if (frame698.getaPDU().getChoiseFlag()==135 && frame698.getaPDU().getChoiseFlag_1()==1){
				String data = frame698.getaPDU().getNextData();
				if (data.substring(0, 8).equals("60008000"))
					if (data.substring(8, 10).equals("00")){
						//DAR=00��ʾ�ɹ�  ��������ͺ���ͬ������
						sendSyncData();
					}
			}
			
			// ɾ���ն˺�Ļظ�����
			//68 21 00 C3 05 11 11 11 11 11 11 00 84 84 87 02 00 02 60 00 83 00 00 00 60 00 83 00 00 00 00 00 62 31 16
			if (frame698.getaPDU().getChoiseFlag()==135 && frame698.getaPDU().getChoiseFlag_1()==2){
				String data = frame698.getaPDU().getNextData();
				// 0,2�Ǹ�����Ϣ
				if (data.substring(2, 10).equals("60008300"))
					if (data.substring(10, 12).equals("00")){
						//DAR=00��ʾ�ɹ�  ��������ͺ���ͬ������
						sendSyncData();
					}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

	//����ͬ����������ͬ��������У����д洢��
	private void reverseSync(String aType1, String aType2) {
		// ����ͬ��
		// ����ͬ�� ʹ���ն˵�����Ϣ�޸����������Ϣ���������ݱ���
		// ���������Ϊģ�� �����ӡ��޸ġ�ɾ������ //
		// ��� ��������
		
		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};

		// �Ƚ����ݵ����Ĳ���
		meters_Software = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='1'","order by measureNo");
		meters_Terminal = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='2'","order by measureNo");

		Meter meter1 = null;

		// ��������������б��������������޸ĺͱ�ǡ�ɾ����
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

		// ���������ɾ����ǵ����ݣ����н���ɾ��
		meters_Software.deleteByCode(Constant.FLAG_DEL+",","getFlag,",true);

		// �����ն˵������б�����������������
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
		// ���ݱ���
		//meters_Software.saveMeters(TerminalID);

		// �����е����ݽ��и���
		showTableData(aType1, aType2);
		
		DebugSwing.showMsg("����ͬ����ɣ�");

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
