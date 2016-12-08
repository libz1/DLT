package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;


import util.DB;
import util.Util698;
import util.Util698.NewObjAction;
import base.BaseFrame;
import base.CommonObjectList;
import base.InfoClass;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DebugSwing;

import entity.DataTypeUser;
import entity.TransImplDataType;

/**
 * �������͹���
 * 
 * @author xuky
 * @version 2016.10.13
 * 
 */
public class DataTypeUserCRUD extends BaseFrame implements ActionListener {

	public JTable table;
	DefaultTableModel defaultModel;
	public JButton[] buttonArr;
	
	private JTextField txt_search;
	public JButton bt_search;
	JComboBox<String> cb_opType;
	
	CommonObjectList<DataTypeUser> dataTypeUserList;

	// �����б��е���ʾ�ֶΡ�������ϸ��Ϣ���е���ʾ�ֶ�
	String[] colNames_table, colNames_info;
	
	// ��ϸ���ݶ���1���б��·���ʾ��ϸ�����ã�2��������������ʾ��ϸ������
	InfoClass info_detail;
	
	// list������
	NewObjAction newobj_act;
	
	@Override
	protected void init() {
		
		// ����ע�⣺��������е�ǰ������ĸ�ֵ���룬��Ҫ����֮ǰ���ඨ�������  
		// ��ֹ�����磺public JTable table = null;
		// ��ֹ�����磺String[] colNames_info1 = { "code", "��code", "˳���", "��������", "��������;noButton", "Ĭ��ֵ",		"���ݶ���;TextArea", "��ע" };
		
		// ���ⶨ�� �� "��������;noButton"��"���ݶ���;TextArea"��"������;code:PortRateType"
		
		String[] colNames_info1 = { "code", "��code", "˳���", "��������", "��������;noButton", "Ĭ��ֵ",
				"���ݶ���;TextArea", "��ע" };
		colNames_info = Util698.setArrayData(colNames_info1);

		String[] colNames_table1 = { "ID", "code", "��code", "˳���", "��������", "��������", "Ĭ��ֵ",
				"���ݶ���", "��ע" };
		colNames_table = Util698.setArrayData(colNames_table1);
		
		info_detail = new InfoClass(colNames_info);
		info_detail.setTrans(new TransImplDataType());
		// �ڽ����з��� �ն���ϸ��Ϣ��ʾ�ؼ�
		panel.add(info_detail.panel);
		info_detail.panel.setBounds(10, 600 - 270, 780, 500);

		txt_search= new JTextField(100);
		panel.add(txt_search);
		txt_search.setBounds(450, 20, 100, 30);

		bt_search= new JButton("���Ҷ�λ");
		panel.add(bt_search);
		bt_search.setBounds(560, 20, 100, 30);
		bt_search.addActionListener(this);
		
		
		//dataTypeUserList = new DataTypeUserList("FromDB");
		
		// 
		newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new DataTypeUser(); }
		};
		dataTypeUserList = new CommonObjectList<DataTypeUser>(newobj_act,"datatype_user","code,fcode");
		
		
		Object[][] data1 = DB.getInstance().getDataTypeUserList(-1);
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
		buttonArr = new JButton[4];
		Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < 4; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr[i] = new JButton();
			buttonArr[i].setForeground(Color.white);
			buttonArr[i].setBackground(new Color(0, 114, 198));
			buttonArr[i].setFont(font);
			panel_buttons.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);
		}

		buttonArr[0].setText("����");
		buttonArr[1].setText("�޸�");
		buttonArr[2].setText("ɾ��");
		buttonArr[3].setText("ѡ��");

		buttonArr[3].setVisible(false);

		buttonArr[0].setBounds(110, 15, 100, 30);
		buttonArr[1].setBounds(215, 15, 100, 30);
		buttonArr[2].setBounds(320, 15, 100, 30);
		buttonArr[3].setBounds(5, 15, 100, 30);

		// �ڽ�������Ӱ�ť��������б�����
		panel.add(panel_buttons);
		panel.add(scroll);
		panel_buttons.setBounds(5, 5, 420, 50);
		scroll.setBounds(5, 70, 800 - 30, 600 - 350);
		
		Vector<String> v = null;
		v = new Vector<String>();
		v.addElement("ͬ������");
		v.addElement("�¼�����");
		cb_opType = new JComboBox<String>(v);
		cb_opType.setBounds(5, 15, 100, 30);
		panel_buttons.add(cb_opType);
		
		// xuky Ĭ��ѡ�е�һ��
		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}

	}

	private void setTableColumnWidth() {
		// �����п��
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		
		col = table.getColumnModel().getColumn(1);
		col.setMinWidth(40);
		col.setMaxWidth(40);
		col = table.getColumnModel().getColumn(2);
		col.setMinWidth(50);
		col.setMaxWidth(50);
		col = table.getColumnModel().getColumn(3);
		col.setMinWidth(40);
		col.setMaxWidth(40);

		col = table.getColumnModel().getColumn(4);
		col.setMinWidth(250);
		col.setMaxWidth(250);
		col = table.getColumnModel().getColumn(5);
		col.setMinWidth(180);
		col.setMaxWidth(180);
		
		col = table.getColumnModel().getColumn(6);
		col.setMinWidth(50);
		col.setMaxWidth(50);
		
		
		col = table.getColumnModel().getColumn(7);
		col.setMinWidth(80);
		col.setMaxWidth(80);
		col = table.getColumnModel().getColumn(8);
		col.setMinWidth(50);
		col.setMaxWidth(50);
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		
		if (e.getActionCommand().equals("���Ҷ�λ")) {
			
			String txt = txt_search.getText();
			
			if (txt.equals("")){
				refresh_List();
				return;
			}
			Boolean find = false;
			
			for(int i=table.getSelectedRow()+1;i<table.getRowCount();i++){
				String dataname = (String)table.getValueAt(i, 4);
				dataname = dataname.toUpperCase();
				txt = txt.toUpperCase();
				if ( dataname.indexOf(txt) >= 0){
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
		
		
		if (e.getActionCommand().equals("�޸�")
				|| e.getActionCommand().equals("����")) {

			// xuky ����һ�����ڽ��������޸�
			final String aType = e.getActionCommand();

			final JFrame subWin = new JFrame("��������");
			if (aType == "�޸�") {
				if (table.getRowCount() <= 0)
					return;
				subWin.setTitle("�޸�����");
			}
			
			subWin.setLayout(null);
			subWin.setSize(750, 320);

			// ֱ��ʹ���������е���ϸ���󴰿�
			final InfoClass dataTypeUserInfo_pop = info_detail;
			subWin.add(info_detail.panel);
			info_detail.panel.setBounds(5, 5, 760, 500);
			
			if (aType == "�޸�") {
				int s = (int) table.getValueAt(table.getSelectedRow(), 0);
				info_detail.setData(dataTypeUserList.getOne(s));
			}
			
			if (aType == "����") {
				
				// ����ǰ�����֮ǰ������
				info_detail.clearComponent();
				
				// code�Զ�����
				String code = DataConvert.int2String(DB.getInstance().getDataTypeUserMaxCode());
				((JTextField)dataTypeUserInfo_pop.component[0]).setText(code); // max_code++
				
					if (cb_opType.getSelectedItem().toString().indexOf("ͬ��") >= 0){
						//����ͬ�����ݣ�fcode=ͬ��fcode,seq=ͬ��seq+1, ����structure defaultvalue+1
						int row = table.getSelectedRow();
						if (row >= 0) {
							//System.out.println("row=>" + row);
							int fcode = (int) table.getValueAt(row, 2);
							((JTextField)dataTypeUserInfo_pop.component[1]).setText(DataConvert.int2String(fcode));
							int seq = (int) table.getValueAt(row, 3)+1;
							((JTextField)dataTypeUserInfo_pop.component[2]).setText(DataConvert.int2String(seq));
						}
					}
					else{
						//�����¼����ݣ�fcode=ͬ��code,seq=�¼�seq+1, ͬ��structure defaultvalue+1
						int row = table.getSelectedRow();
						if (row >= 0) {
							//System.out.println("row=>" + row);
							code = DataConvert.int2String((int) table.getValueAt(row, 1));
							((JTextField)dataTypeUserInfo_pop.component[1]).setText(code);
							int seq = 1;
							((JTextField)dataTypeUserInfo_pop.component[2]).setText(DataConvert.int2String(seq));
						}
					}
			}
			
			// ���������� ��Ҫ��ȷ�Ϻ�ȡ����ť
			JButton buttonOk = new JButton("ȷ��");
			buttonOk.setBackground(new Color(0, 114, 198));
			buttonOk.setForeground(Color.white);
			buttonOk.setBounds(250, 240, 100, 30);
			subWin.add(buttonOk);
			
			JButton buttonCancle = new JButton("ȡ��");
			buttonCancle.setBackground(new Color(0, 114, 198));
			buttonCancle.setForeground(Color.white);
			buttonCancle.setBounds(355, 240, 100, 30);
			subWin.add(buttonCancle);

			// xuky 2016.08.28 ���´���δ��Ч
			// �����еĿؼ���ý���
			dataTypeUserInfo_pop.setFocus();

			// ȷ�ϰ�ť����¼�
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// http://blog.csdn.net/ethanq/article/details/7200490
					int id = 0;
					DataTypeUser d;
					if (aType == "����") {
						// 1���жϸ��������Ƿ�ϸ�
						// 2������һ��Ψһ��ID
						id = dataTypeUserList.getUseableID();
						// 3���������е�������ӵ�������
						//d = dataTypeUserInfo_pop.getDataNoID();
						d = (DataTypeUser) dataTypeUserInfo_pop.trans.getData();
						d.setId(id);
						// 4��terminal������ӵ������б��� �����浽���ݿ���
						dataTypeUserList.add(d);
					} else {
						// �޸�ԭ�ȵ�����
						// 1���жϸ��������Ƿ�ϸ�
						// 3���������е�������ӵ�������
						// ��Ҫ��ԭ�ȵ�ID�ָ�
						//d = dataTypeUserInfo_pop.getDataWithID();
						d = (DataTypeUser)dataTypeUserInfo_pop.trans.getDataWithID();
						id = d.getId();
						// 4��terminal�����滻ԭ�ȵĶ���
						dataTypeUserList.update(d);
					}
					// 5�������еĶ���ͬ��������Ķ����б���
					refresh_List(id);
					subWin.dispose();
				}
			});

			// ȡ����ť����¼�
			buttonCancle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					subWin.dispose();
				}
			});

			DebugSwing.center(subWin); // xuky ���ھ���
			subWin.setVisible(true);

			// xuky �����Ĵ����������㣬������λ��������
			subWin.addWindowFocusListener(new WindowFocusListener() {
				public void windowGainedFocus(WindowEvent e) {
				}

				public void windowLostFocus(WindowEvent e) {
					e.getWindow().toFront();
				}
			});

		}

		if (e.getActionCommand().equals("ɾ��")) {

			int rowcount = defaultModel.getRowCount();
			if (rowcount > 0) {
				// xuky ɾ����ǰ��
				int rowCount = table.getRowCount(); 
				int deleterow = table.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,
						"�Ƿ�ȷ��ɾ��\"" + table.getValueAt(deleterow, 4) + "\"",
						"ɾ����ʾ", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					dataTypeUserList.delete((int) table.getValueAt(deleterow, 0));
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
		//dataTypeUserList = new DataTypeUserList("FromDB");
		//dataTypeUserList = new ObjectList(newobj_act,"datatype_user","id");
		dataTypeUserList = new CommonObjectList<DataTypeUser>(newobj_act,"datatype_user","code,fcode");

		Object[][] data1 = DB.getInstance().getDataTypeUserList(-1);
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
			
			panel.add(info_detail.panel);
			info_detail.setBounds(10, 600 - 270, 780, 500);
			
			if (e.getValueIsAdjusting())
				return;

			int row = table.getSelectedRow();
			if (row >= 0) {
				int id = (int) table.getValueAt(row, 0);
				DataTypeUser dtu = (DataTypeUser) dataTypeUserList.getOne(id);
				info_detail.setData(dtu);
			}
		}
	}
	
	// ������������ѡ�񴰿�ʱ������ʾ���ӵȰ�ť
	public void DisableEdit() {
		buttonArr[0].setVisible(false);
		buttonArr[1].setVisible(false);
		buttonArr[2].setVisible(false);
		cb_opType.setVisible(false);
		buttonArr[3].setVisible(true);		
	}

	public static void main(String[] args) {
		BaseFrame mainFrame = new DataTypeUserCRUD();
		mainFrame.getPanel().setBounds(0, 0, 800, 600);
		JFrame frame = new JFrame();
		// frame.setTitle(buttonName);
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(800, 600);
		frame.setVisible(true);
		DebugSwing.center(frame);
	}


}
