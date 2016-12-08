package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
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


import base.BaseFrame;
import base.CommonObjectList;
import base.InfoClass;

import com.eastsoft.fio.ReadWriteExcel;
import com.eastsoft.util.DebugSwing;

import entity.Constant;
import entity.Meter;
import entity.TransImplMeter;
import util.DB;
import util.Util698;
import util.Util698.NewObjAction;

/**
 * �������͹���
 * 
 * @author xuky
 * @version 2016.10.13
 * 
 */
public class MeterCRUD extends BaseFrame implements ActionListener {

	public JTable table;
	DefaultTableModel defaultModel;
	public JButton[] buttonArr;
	
	private JTextField txt_search;
	public JButton bt_search;
	
	CommonObjectList<Meter> meterList;

	// �����б��е���ʾ�ֶΡ�������ϸ��Ϣ���е���ʾ�ֶ�
	String[] colNames_table, colNames_info;
	
	// ��ϸ���ݶ���1���б��·���ʾ��ϸ�����ã�2��������������ʾ��ϸ������
	InfoClass info_detail;
	
	// list������
	NewObjAction newobj_act;
	
	// table�ؼ�����ʾ�����ݶ�Ӧ���ֶ������б�
	String table_columns, export_columns;
	
	@Override
	protected void init() {
		
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
		
		
		defaultModel = new DefaultTableModel(null, colNames_table);
		table = new JTable(defaultModel);

		// ˢ������
		refresh_List();
		
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

		buttonArr[0].setText("����");
		buttonArr[1].setText("�޸�");
		buttonArr[2].setText("ɾ��");
		buttonArr[3].setText("����ɾ��");
		
		buttonArr[4].setText("����");
		buttonArr[5].setText("����");

		buttonArr[3].setVisible(true);

		buttonArr[0].setBounds(5, 15, 75, 30);
		buttonArr[1].setBounds(5+75+5, 15, 75, 30);
		buttonArr[2].setBounds(5+75*2+5*2, 15, 75, 30);
		buttonArr[3].setBounds(5+75*3+5*3, 15, 90, 30);
		
		//bt_search.setBounds(560, 20, 100, 30);
		
		
		buttonArr[4].setBounds(460+105, 20, 80, 30);
		buttonArr[5].setBounds(460+105+80+5, 20, 80, 30);
		panel.add(buttonArr[4]);
		panel.add(buttonArr[5]);
		
		// �ڽ�������Ӱ�ť��������б�����
		panel.add(panel_buttons);
		panel.add(scroll);
		panel_buttons.setBounds(5, 5, 420, 50);
		scroll.setBounds(5, 70, 800 - 30, 600 - 400);
		
		// xuky Ĭ��ѡ�е�һ��
		if (table.getRowCount() > 0) {
			table.setRowSelectionInterval(0, 0);
		}

	}

	private void setTableColumnWidth() {
		//String[] colNames_table1 = { "ID", "�������","ͨ�ŵ�ַ","������","��Լ����","�˿�","�û�����","���߷�ʽ","�ɼ�����ַ" };		// �����п��
		
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		
	}
	
	private void importFromExcel() {
		
		// 123
		String fileName = DebugSwing.fileChoose();
		String[][] data = null;
		if (!fileName.equals("")) {
			data = ReadWriteExcel.excel2StringArray(fileName);
//			System.out.println("importFromExcel=>"+DateTimeFun.getDateTimeSSS() +" begin");
			meterList.deleteAll();
//			System.out.println("importFromExcel=>"+DateTimeFun.getDateTimeSSS() +" deleteAll end");
			meterList.converFormStringArray(data);
//			System.out.println("importFromExcel=>"+DateTimeFun.getDateTimeSSS() + " converFormStringArray end");
			meterList.addAll();
//			System.out.println("importFromExcel=>"+DateTimeFun.getDateTimeSSS() + " addAll end");
			//meters = new MeterList(TerminalID, data);
		}
		refresh_List();
		DebugSwing.showMsg("�������ݳɹ���");
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
			final InfoClass info_pop = new InfoClass(colNames_info);
			info_pop.setTrans(new TransImplMeter());
			subWin.add(info_pop.panel);
			info_pop.panel.setBounds(5, 5, 760, 500);
			
			if (aType == "�޸�") {
				int s = (int) table.getValueAt(table.getSelectedRow(), 0);
				info_pop.setData(meterList.getOne(s));
			}
			
			if (aType == "����") {

				// ��ֹ��������Ϊ�յ����
				Meter newMeter = new Meter(); 
				// ����ǰ�����֮ǰ������  
				//info_pop.clearComponent();
				info_pop.setData(newMeter);
				
//				// code�Զ�����
//				String code = DataConvert.int2String(DB.getInstance().getDataTypeUserMaxCode());
//				((JTextField)dataTypeUserInfo_pop.component[0]).setText(code); // max_code++
//				
//					if (cb_opType.getSelectedItem().toString().indexOf("ͬ��") >= 0){
//						//����ͬ�����ݣ�fcode=ͬ��fcode,seq=ͬ��seq+1, ����structure defaultvalue+1
//						int row = table.getSelectedRow();
//						if (row >= 0) {
//							//System.out.println("row=>" + row);
//							int fcode = (int) table.getValueAt(row, 2);
//							((JTextField)dataTypeUserInfo_pop.component[1]).setText(DataConvert.int2String(fcode));
//							int seq = (int) table.getValueAt(row, 3)+1;
//							((JTextField)dataTypeUserInfo_pop.component[2]).setText(DataConvert.int2String(seq));
//						}
//					}
//					else{
//						//�����¼����ݣ�fcode=ͬ��code,seq=�¼�seq+1, ͬ��structure defaultvalue+1
//						int row = table.getSelectedRow();
//						if (row >= 0) {
//							//System.out.println("row=>" + row);
//							code = DataConvert.int2String((int) table.getValueAt(row, 1));
//							((JTextField)dataTypeUserInfo_pop.component[1]).setText(code);
//							int seq = 1;
//							((JTextField)dataTypeUserInfo_pop.component[2]).setText(DataConvert.int2String(seq));
//						}
//					}
			}
			
			// ���������� ��Ҫ��ȷ�Ϻ�ȡ����ť
			JButton buttonOk = new JButton("ȷ��");
			buttonOk.setBackground(new Color(0, 114, 198));
			buttonOk.setForeground(Color.white);
			buttonOk.setBounds(250+100, 240, 100, 30);
			subWin.add(buttonOk);
			
			JButton buttonCancle = new JButton("ȡ��");
			buttonCancle.setBackground(new Color(0, 114, 198));
			buttonCancle.setForeground(Color.white);
			buttonCancle.setBounds(355+100, 240, 100, 30);
			subWin.add(buttonCancle);

			// xuky 2016.08.28 ���´���δ��Ч
			// �����еĿؼ���ý���
			info_pop.setFocus();

			// ȷ�ϰ�ť����¼�
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// http://blog.csdn.net/ethanq/article/details/7200490
					int id = 0;
					Meter d;
					if (aType == "����") {
						// 1���жϸ��������Ƿ�ϸ�
						// 2������һ��Ψһ��ID
						id = meterList.getUseableID();
						// 3���������е�������ӵ�������
						//d = dataTypeUserInfo_pop.getDataNoID();
						d = (Meter) info_pop.trans.getData();
						d.setArchiveType(Constant.ARCHIVE_SOFT);
						d.setID(id);
						// 4��terminal������ӵ������б��� �����浽���ݿ���
						meterList.add(d);
					} else {
						// �޸�ԭ�ȵ�����
						// 1���жϸ��������Ƿ�ϸ�
						// 3���������е�������ӵ�������
						// ��Ҫ��ԭ�ȵ�ID�ָ�
						//d = dataTypeUserInfo_pop.getDataWithID();
						d = (Meter)info_pop.trans.getDataWithID();
						id = d.getID();
						// 4��terminal�����滻ԭ�ȵĶ���
						meterList.update(d);
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
						"�Ƿ�ȷ��ɾ��\"" + table.getValueAt(deleterow, 1) +" "+ table.getValueAt(deleterow, 2) + "\"",
						"ɾ����ʾ", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					
					meterList.delete((int) table.getValueAt(deleterow, 0));
					// ��λ��ɾ���е�ǰһ��deleterow-1
					if (rowCount != deleterow  ){
						int id = 0;
						if (deleterow == 0)
							id = (int)table.getValueAt(0, 0);
						else
							id = (int)table.getValueAt(deleterow-1, 0);
						refresh_List(id);
					}
				}
			}
		}
		
		if (e.getActionCommand().equals("����ɾ��")) {
			
			int num =  table.getSelectedRowCount();
			if (num == 0){
				DebugSwing.showMsg("�밴סshit�������ж�������ѡ��Ȼ����ִ�д�����ɾ��");
				return;
			}
			if (JOptionPane.showConfirmDialog(null,
					"�Ƿ�ȷ������ɾ��"+num+"������?",
					"ɾ����ʾ", JOptionPane.OK_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
				
				int[] rows = table.getSelectedRows();
				
				// ��ɾ��ǰȷ��ɾ������ж�λ��Ϣ
				int id = 0;
				if (rows[0] == 0){
					if (rows[num-1] == table.getRowCount()-1)
						id = 0;
					else
						id = (int) table.getValueAt(rows[num-1]+1, 0);
				}
				else{
					id = (int) table.getValueAt(rows[0]-1, 0);
				}
				
				int[] ids = new int[num];
//				for( int i = num-1;i>=0;i-- ){
//					meterList.delete((int) table.getValueAt(rows[i], 0));
//				}
				for( int i = 0;i<num;i++ ){
					ids[i] = (int) table.getValueAt(rows[i], 0);
				}
				meterList.deleteAll(ids);
				refresh_List(id);
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
		meterList = new CommonObjectList<Meter>(newobj_act,"meter","meterCode","terminalID='1' and archiveType='1'","order by measureNo");
		meterList.setExportColmunNames(colNames_info);
		meterList.setExportColmuns(export_columns);
		
		
		Object[][] data1 = DB.getInstance().getDataList("meter","terminalID='1' and archiveType='1'","order by measureNo",new Meter(),table_columns);
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
		BaseFrame mainFrame = new MeterCRUD();
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
