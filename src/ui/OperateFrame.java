package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import util.DB;
import util.Publisher;
import util.SoftParameter;
import util.Util698;
import base.BaseFrame;
import base.InfoClass;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;
import com.eastsoft.util.Debug;
import com.eastsoft.util.DebugSwing;

import frame.APDU;
import frame.AnalyData;

/**
 * ��ȡ���������ԣ�������������
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */
public class OperateFrame extends BaseFrame implements Observer{
	static int WINDOWWIDTH = 800;

	static int WINDOWHEIGHT = 600;
	
	private JPanel panel_changeVisible, panel_showData;
	private JTable table_attrList, table_attrValList;
	private DefaultTableModel model_attrList, model_attrValList;
	private JLabel lb_val, lb_define, lb_opType, lb_oadNum, lb_frame;
	private JTextField txt_val, txt_oadnum, txt_frame;
	private JTextArea txt_define;
	private JButton btn_read, btn_write,  btn_writenandread, btn_chg_oadvisible, btn_refresh,
			btn_set_attrVal, btn_writeOneItem;
	private JButton btn_visible_save, btn_visible_cancel, btn_val_save;
	private JButton btn_addArray, btn_delArray;
	private JComboBox<String> cb_opType; // ��������
	private JCheckBox chk_select;
	
	
	// private JPanel panel_add, panel_del;
	JProgressBar progressbar;

	// ����ѡ�������б�
	static final String[] colNames_attr = { "ѡ��", "oi", "���", "����", "ֻ��",
			"attr_id" };
	
	// ���������б�
	static final String[] colNames_attrVal = { "����", "����", "��ֵ", "definevalue", "id",
			"arrayidx","code","fcode","seq" };
	
	// ����ȫ�������б�
	static final String[] colNames_attr_all = { "��ʾ��", "oi", "���", "����", "ֻ��",
			"attr_id", "select_old" };

	// private JScrollPane scroll_analy,scroll_analyAPDU;
	// private JCheckBox checkbox;

	/**
	 * ���캯��
	 */
	public OperateFrame(String type) {
		super(type);
		Publisher.getInstance().addObserver(this);
	}

	
	/**
	 * �����ʼ��
	 */
	@Override
	protected void init() {

		panel_changeVisible = new JPanel(null);
		panel_showData = new JPanel(null);

		// /////////////////////////
		lb_val = new JLabel("��ֵ", JLabel.CENTER);
		lb_define = new JLabel("����", JLabel.CENTER);
		txt_val = new JTextField(50);
		txt_define = new JTextArea();

		init_table();

		panel.add(lb_val);
		panel.add(lb_define);
		panel.add(txt_val);
		panel.add(txt_define);

		lb_val.setBounds(300, 350, 60, 30);
		txt_val.setBounds(360, 350, 300, 30);
		lb_define.setBounds(300, 390, 60, 30);
		
		txt_define.setLineWrap(true);// �����Զ����й���
		txt_define.setWrapStyleWord(true);// ������в����ֹ���
		JScrollPane scroll_define = new JScrollPane(txt_define);
		scroll_define.setBounds(360, 390, 410, 130);
		panel.add(scroll_define);

		btn_val_save = new JButton("����");
		btn_val_save.setBounds(670, 350, 100, 30);
		panel.add(btn_val_save);

		txt_frame = new JTextField(5000);
		panel.add(txt_frame);
		txt_frame.setBounds(360, 520, 410, 30);

		lb_frame = new JLabel("APDU", JLabel.CENTER);
		lb_frame.setBounds(300, 520, 60, 30);
		panel.add(lb_frame);
		
		
		if (OPERATETYPE.equals("Func")){
			btn_read = new JButton("����");
			btn_write = new JButton("�������ȡ");
			btn_writenandread = new JButton("���ú��ȡ");
			btn_writeOneItem = new JButton("���õ���");
			btn_writenandread.setVisible(false);
			btn_writeOneItem.setVisible(false);
		}
		else{
			btn_read = new JButton("��ȡ");
			btn_write = new JButton("����");
			btn_writenandread = new JButton("���ú��ȡ");
			btn_writeOneItem = new JButton("���õ���");			
		}

		panel.add(btn_write);
		
		panel.add(btn_read);
		panel.add(btn_writenandread);
		panel.add(btn_writeOneItem);
		

		// btn_read.setBounds(0,0,100,30);

		lb_opType = new JLabel("��������", JLabel.CENTER);
		String txt = "";
		if (OPERATETYPE.equals("Func"))
			txt = "ÿ֡��������";
		else
			txt = "ÿ֡OAD����";
		lb_oadNum = new JLabel(txt, JLabel.CENTER);
		

		Vector<String> v = null;
		v = new Vector<String>();
		v.addElement("����(��ǰ��)");
		v.addElement("ѡ����");
		v.addElement("ȫ��(�ޱ��0)");
		cb_opType = new JComboBox<String>(v);
		
		txt_oadnum = new JTextField(50);
		txt_oadnum.setText("5");

		panel.add(cb_opType);
		panel.add(lb_opType);

		panel.add(lb_oadNum);
		panel.add(txt_oadnum);

		lb_opType.setBounds(20, 5, 60, 30);
		cb_opType.setBounds(100, 5, 120, 30);

		btn_read.setBounds(230, 5, 95, 30);
		btn_write.setBounds(327, 5, 95, 30);
		btn_writenandread.setBounds(424, 5, 95, 30);
		btn_writeOneItem.setBounds(521, 5, 95, 30);
		
		lb_oadNum.setBounds(620, 5, 100, 30);
		txt_oadnum.setBounds(720, 5, 60, 30);

		panel_showData.setBounds(5, 500, 300, 50);

		btn_chg_oadvisible = new JButton("������ʾ����");
		btn_chg_oadvisible.setBounds(130, 0, 120, 30);
		
		
		btn_refresh = new JButton("ˢ������");
		btn_refresh.setBounds(0, 0, 120, 30);

		// �˹���Ӧ���ڶ��������
		//btn_set_attrVal = new JButton("��������ֵ");
		//panel_showData.add(btn_set_attrVal);
		//btn_set_attrVal.setBounds(125, 0, 125, 30);

		panel_showData.add(btn_chg_oadvisible);
		panel_showData.add(btn_refresh);

		panel.add(panel_showData);

		panel_changeVisible.setBounds(5, 500, 300, 60);

		chk_select = new JCheckBox("ѡ��", false);
		chk_select.setBounds(0, 0, 70, 30);
		panel_changeVisible.add(chk_select);

		btn_visible_save = new JButton("����");
		btn_visible_cancel = new JButton("ȡ��");
		
		progressbar = new JProgressBar();
        progressbar.setOrientation(JProgressBar.HORIZONTAL);
	    progressbar.setMinimum(0);
	       progressbar.setMaximum(100);
	       progressbar.setValue(0);
	       progressbar.setStringPainted(true);
	       progressbar.setPreferredSize(new Dimension(300, 20));
	       progressbar.setBorderPainted(true);
	       //progressbar.setBackground(Color.pink);
	       
	    progressbar.setValue(0);  
	       
		btn_visible_save.setBounds(70, 0, 100, 30);
		btn_visible_cancel.setBounds(180, 0, 100, 30);
		progressbar.setBounds(0, 32, 280, 25);

		panel_changeVisible.add(btn_visible_save);
		panel_changeVisible.add(btn_visible_cancel);
		
		panel_changeVisible.add(progressbar);

		panel.add(panel_changeVisible);
		panel_changeVisible.setVisible(false);

		btn_visible_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null,
						"��������桱��֮ǰ�������޸Ľ����޸ĵ����ݿ��У���ȷ��", "������ʾ",
						JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

					new Thread() {
						public void run() {
							DB.getInstance().setObjAttrSelected(
									model_attrList.getDataVector(),OPERATETYPE);
						}
					}.start();
				}
			}
		});

		btn_visible_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null,
						"�����ȡ������֮ǰ�������޸Ľ�����Ч����ȷ��", "������ʾ",
						JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					refreshTable_obj_attr(false);
					panel_changeVisible.setVisible(false);
					panel_showData.setVisible(true);
				}
			}
		});

		chk_select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = model_attrList.getRowCount();
				if (chk_select.isSelected()) {
					// �޸��б�����������Ϊѡ��״̬
					for (int i = 0; i < row; i++) {
						model_attrList.setValueAt(true, i, 0);
					}
					// ����˴��룬����ʵ������ͬ��
					// table_obj_attr.revalidate();
				} else {
					for (int i = 0; i < row; i++) {
						model_attrList.setValueAt(false, i, 0);
					}
				}
			}
		});

		btn_val_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table_attrValList.getRowCount() == 0)
					return;
				int row = table_attrValList.getSelectedRow();
				int data_id = (int) table_attrValList.getValueAt(row, 4);
				String val = txt_val.getText();
				String define = txt_define.getText();
				
				model_attrValList.setValueAt(val, row, 2);
				model_attrValList.setValueAt(define, row, 3);
				DB.getInstance().setObjAttrSelected(val, define, data_id, OPERATETYPE);

			}
		});

		btn_read.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txt_frame.setText("");

				// ���������˳�
				int row = table_attrList.getRowCount();
				if (row <= 0)
					return;
				
				APDU aPDU = new APDU();
				aPDU.setChoiseFlag(5);
				
				if (OPERATETYPE.equals("Func"))
					aPDU.setChoiseFlag(7);
				
				// 1 ����ģʽ				
				if (SoftParameter.getInstance().getIsProxyModel().equals("1"))
					aPDU.setChoiseFlag(9);
				
				String nextData = "";
				String apdu = aPDU.getString();

				if (cb_opType.getSelectedItem().toString().indexOf("��") >= 0) {
					int currLine = table_attrList.getSelectedRow();
					
					// �ж����Ե����ݣ������RSD��RCSD��Ϊ����¼������
					int attr_id = (int) table_attrList.getValueAt(currLine, 5);
					Boolean isRecord = DB.getInstance().isRecord(attr_id);
					
					// ����
					aPDU.setChoiseFlag_1(1);
					String oi = (String) table_attrList.getValueAt(currLine, 1);
					String no = (String) table_attrList.getValueAt(currLine, 2);
					// OI + ����ֵ + ��00�� = OAD
					String OAD = oi + DataConvert.String2HexString(no, 2) + "00";
					OAD = Util698.seprateString(OAD, " ");
					nextData = OAD;

					// 2 ����ģʽ���Ĵ������
					if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
						aPDU.setChoiseFlag_1(1);
						nextData = getProxyData("01, " +OAD);
					}
					
					if (OPERATETYPE.equals("Func")){
						
						String paraData = getParaData(false);
						// xuky 2016.10.31 OMD+data ��dataʱ�̣�ʹ��00��null
						if (paraData.equals(""))
							paraData = "00";
						nextData += ","+paraData;
						
						if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
							aPDU.setChoiseFlag_1(5);
							if (OAD.indexOf("FF FF 01 00") >=0){
								aPDU.setChoiseFlag_1(7);
								// �������͸��ת���������� ���⴦��   ȥ��ǰ���structure���� 
								// xuky 2016.11.10 �����ľ������  ȥ��OAD������51��ȥ��COMDCB������5F������������뱣�� 
								// 0205,51F2090201,5F 03 02 08 01 00 ,12003C,120064,090468006816
								nextData = paraData.substring(7);
								nextData = nextData.substring(0,9)+ nextData.substring(11);
								// F2090201, 03 02 08 01 00 ,12003C,120064,090468006816
								// xuky 2016.11.15 ����Ҫ��������ȥ��12*2 ��09���ʹ���
								nextData = nextData.substring(0,26)+ nextData.substring(28);
								nextData = nextData.substring(0,31)+ nextData.substring(33);
								nextData = nextData.substring(0,36)+ nextData.substring(38);
							}
							else
								nextData = getProxyData("01, " +OAD+" ," + paraData);
						}
					}
					else{
						if (isRecord){
							aPDU.setChoiseFlag_1(3);
							
							String RSDAndRCSD = getParaData(isRecord);
							nextData += " ,"+RSDAndRCSD;
							
							if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
								aPDU.setChoiseFlag_1(2);
								nextData = getProxyData(OAD+" ,"+ RSDAndRCSD, true);
							}
							
						}
					}
				}
				
				
				if (cb_opType.getSelectedItem().toString().indexOf("ѡ��") >= 0) {
					String selected = "";
					aPDU.setChoiseFlag_1(2);
					
					if (SoftParameter.getInstance().getIsProxyModel().equals("1"))
						aPDU.setChoiseFlag_1(1);
					
					for (int i = 0; i < row; i++) {
						if ((Boolean) table_attrList.getValueAt(i, 0)) {
							selected += (String) table_attrList
									.getValueAt(i, 1)
									+ '@'
									+ (String) table_attrList.getValueAt(i, 2)
									+ ";";
						}
					}
					String[] s = selected.split(";");
					String OAD = DataConvert.int2HexString(s.length, 2)+", ";
					for (String str : s) {
						String[] t = str.split("@");
						String tmp = t[0]
								+ DataConvert.String2HexString(t[1], 2) + "00";
						tmp = Util698.seprateString(tmp, " ");
						OAD += tmp+", ";
					}
					// ȥ������ָ��õĶ����ַ�
					OAD = OAD.substring(0,OAD.length()-2);
					nextData = OAD;
					
					// ����ģʽ���Ĵ������
					if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
						aPDU.setChoiseFlag_1(1);
						nextData = getProxyData(OAD);
					}
					
				}
				nextData += " ,00";  //���ʱ���ǩ��Ϣ
				aPDU.setNextData(nextData);
				apdu = aPDU.getString();
				txt_frame.setText(apdu);
			}
		});
		
		// �����Խ�����������
		btn_write.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txt_frame.setText("");

				// ���������˳�
				int row = table_attrList.getRowCount();
				if (row <= 0)
					return;
				
				APDU aPDU = new APDU();
				aPDU.setChoiseFlag(6);
				
				// 1 ����ģʽ				
				if (SoftParameter.getInstance().getIsProxyModel().equals("1"))
					aPDU.setChoiseFlag(9);
				
				String nextData = "";
				String apdu = aPDU.getString();

				if (cb_opType.getSelectedItem().toString().indexOf("��") >= 0) {
					
					int currLine = table_attrList.getSelectedRow();
					
					// �ж����Ե����ݣ������RSD��RCSD��Ϊ����¼������
					int attr_id = (int) table_attrList.getValueAt(currLine, 5);
					Boolean isRecord = DB.getInstance().isRecord(attr_id);
					
					// ����
					aPDU.setChoiseFlag_1(1);
					String oi = (String) table_attrList.getValueAt(currLine, 1);
					String no = (String) table_attrList.getValueAt(currLine, 2);
					// OI + ����ֵ + ��00�� = OAD
					
					String OAD = oi + DataConvert.String2HexString(no, 2) + "00";
					OAD = Util698.seprateString(OAD, " ");
					nextData = OAD;
					
					String paraData = getParaData(false);
					nextData += " ,"+paraData;
					
					// 2 ����ģʽ���Ĵ������
					if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
						aPDU.setChoiseFlag_1(3);
						nextData = getProxyData("01, " +OAD+" ," + paraData);
					}
					
				}
				if (cb_opType.getSelectedItem().toString().indexOf("ѡ��") >= 0) {
					String selected = "";
					aPDU.setChoiseFlag_1(2);
					for (int i = 0; i < row; i++) {
						if ((Boolean) table_attrList.getValueAt(i, 0)) {
							selected += (String) table_attrList
									.getValueAt(i, 1)
									+ '@'
									+ (String) table_attrList.getValueAt(i, 2)
									+ ";";
						}
					}
					String[] s = selected.split(";");
					nextData = DataConvert.int2HexString(s.length, 2);
					for (String str : s) {
						String[] t = str.split("@");
						nextData += t[0]
								+ DataConvert.String2HexString(t[1], 2) + "00";
					}
					
					
					// ��δ��ɣ���Ҫ��ѡ�еĶ�������������ȡ�����
					// getParaDataֻ�ǶԵ�ǰ�н��д���
				}
				
				nextData += " ,00";  //���ʱ���ǩ��Ϣ
				aPDU.setNextData(nextData);
				apdu = aPDU.getString();
				txt_frame.setText(apdu);
			}
		});
		

		// �Ե������Խ������� 
		btn_writeOneItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txt_frame.setText("");

				// ���������˳�
				int row = table_attrList.getRowCount();
				if (row <= 0)
					return;
				
				APDU aPDU = new APDU();
				aPDU.setChoiseFlag(6);
				
				String nextData = "";
				String apdu = aPDU.getString();

				if (cb_opType.getSelectedItem().toString().indexOf("��") >= 0) {
					
					int currLine = table_attrList.getSelectedRow();
					
					
					
					// ���� 
					aPDU.setChoiseFlag_1(1);
					String oi = (String) table_attrList.getValueAt(currLine, 1);
					String no = (String) table_attrList.getValueAt(currLine, 2);
					// OI + ����ֵ + ��00�� = OAD
					
					//123
					//table_attrValList
					// .getValueAt(currLine, 8);
					
					int valRow = table_attrValList.getSelectedRow(); 
					if ( valRow <= 0)
						return;
					int seq = (int)table_attrValList.getValueAt(valRow, 8);
					
					// seq -1 ����д���
					nextData = oi + DataConvert.String2HexString(no, 2) + DataConvert.int2HexString(seq-1, 2);
					nextData = Util698.seprateString(nextData, " ") ;
					
					String data = getParaData(false,seq);
					nextData += ","+data;
					
					nextData += ",00";  //���ʱ���ǩ��Ϣ
					
					aPDU.setNextData(nextData);
					apdu = aPDU.getString();
				}
				if (cb_opType.getSelectedItem().toString().indexOf("ѡ��") >= 0) {
					String selected = "";
					aPDU.setChoiseFlag_1(2);
					for (int i = 0; i < row; i++) {
						if ((Boolean) table_attrList.getValueAt(i, 0)) {
							selected += (String) table_attrList
									.getValueAt(i, 1)
									+ '@'
									+ (String) table_attrList.getValueAt(i, 2)
									+ ";";
						}
					}
					String[] s = selected.split(";");
					nextData = DataConvert.int2HexString(s.length, 2);
					for (String str : s) {
						String[] t = str.split("@");
						nextData += t[0]
								+ DataConvert.String2HexString(t[1], 2) + "00";
					}
					aPDU.setNextData(nextData);
					apdu = aPDU.getString();
				}
				txt_frame.setText(apdu);
			}
		});
		

		btn_chg_oadvisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshTable_obj_attr(true);
				panel_showData.setVisible(false);
				panel_changeVisible.setVisible(true);
			}
		});
		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshTable_obj_attr(false);
			}
		});

//		btn_set_attrVal.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				refreshTable_obj_attr(false);
//			}
//		});

	}
	
	private String getProxyData(String OAD){
		return getProxyData(OAD, false);
	}
	
	
	private String getProxyData(String OAD, Boolean isRecord){
		String ret = "";
		
		// ���峬ʱ
		if (!isRecord)
			ret = DataConvert.int2HexString(SoftParameter.getInstance().getAll_timeout(),4) +" ,";
		
		String[] proxyTargets = SoftParameter.getInstance().getTargets().split(",");
		int proxyNum = proxyTargets.length;
		// Ŀ�����
		if (!isRecord)
			ret += DataConvert.int2HexString(proxyNum, 2) +" ,";
		
		for(String target: proxyTargets ){
			if (isRecord){
				// ������ʱ
				ret += DataConvert.int2HexString(SoftParameter.getInstance().getSingle_timeout(),4) +" ,";
				// Ŀ���ַ
				ret += new AnalyData().getTSA(target) +" ,";
				// OAD ��Ϣ
				ret += OAD +", ";
				break;
			}
			else{
				// Ŀ���ַ
				ret += new AnalyData().getTSA(target) +" ,";
				// ������ʱ
				ret += DataConvert.int2HexString(SoftParameter.getInstance().getSingle_timeout(),4) +" ,";
				// OAD ��Ϣ
				ret += OAD +", ";
			}
		}
		ret = ret.substring(0,ret.length()-2);
		return ret;
	}
	
	private void refreshTable_attr(int id) {
		refresh_attrValList();
		int rowNum = table_attrValList.getRowCount();
		for (int i = 0; i < rowNum; i++) {
			if ((int) table_attrValList.getValueAt(i, 4) == id) {
				// xuky 2016��08.25 ������ĳ�У�������Ϊtable������scrollpanle�У���ʱ����Ч
				// table_attr.setRowSelectionInterval(i, i);

				// �ο�http://blog.csdn.net/dancen/article/details/7379847
				table_attrValList.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table_attrValList.getCellRect(i, 0, true);
				table_attrValList.scrollRectToVisible(rect);
				break;
			}
		}
	}

	private void refresh_attrValList() {
		int row = table_attrList.getSelectedRow();
		if (row >= 0) {
			// attr_id
			String tempString = DataConvert.int2String((int) table_attrList
					.getValueAt(row, 5));
			
			// getAttrValList �Ĳ����� attr_id��method_id
			Object[][] data_attr = DB.getInstance().getAttrValList(tempString,OPERATETYPE);

			model_attrValList.setDataVector(data_attr, colNames_attrVal);

			setTableColumnWidth();

			txt_val.setText("");
			txt_define.setText("");

			if (table_attrValList.getRowCount() > 0)
				table_attrValList.setRowSelectionInterval(0, 0);
		}
	}

	private void refreshTable_obj_attr(Boolean showAll) {
		Object[][] data_obj_attr = null;
		if (!showAll) {
			data_obj_attr = DB.getInstance().getOADList(OPERATETYPE);
			if (OPERATETYPE.equals("Func"))
				colNames_attr[4] = "�в���";
			else
				colNames_attr[4] = "ֻ��";
			model_attrList.setDataVector(data_obj_attr, colNames_attr);
		} else {
			data_obj_attr = DB.getInstance().getAllOADList(OPERATETYPE);
			model_attrList.setDataVector(data_obj_attr, colNames_attr_all);

			TableColumn col = table_attrList.getColumnModel().getColumn(6);
			col.setMinWidth(0);
			col.setMaxWidth(0);
		}

		// ��������Ϊcheckbox�����ڽ���ѡ�����
		TableColumn col = table_attrList.getColumnModel().getColumn(0);
		col.setCellEditor(table_attrList.getDefaultEditor(Boolean.class));
		col.setCellRenderer(table_attrList.getDefaultRenderer(Boolean.class));
		// readonly��Ϊ�����ڽ�������չʾ
		col = table_attrList.getColumnModel().getColumn(4);
		col.setCellEditor(table_attrList.getDefaultEditor(Boolean.class));
		col.setCellRenderer(table_attrList.getDefaultRenderer(Boolean.class));

		// oi�����أ����ڽ��б�����֯��
		col = table_attrList.getColumnModel().getColumn(1);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		// attr_id�����أ����ڽ��б�����֯��
		col = table_attrList.getColumnModel().getColumn(5);
		col.setMinWidth(0);
		col.setMaxWidth(0);

		// �����п��
		col = table_attrList.getColumnModel().getColumn(0);
		col.setMinWidth(40);
		col.setMaxWidth(40);

		col = table_attrList.getColumnModel().getColumn(2);
		col.setMinWidth(40);
		col.setMaxWidth(40);

		col = table_attrList.getColumnModel().getColumn(4);
		col.setMinWidth(40);
		col.setMaxWidth(40);

		if (table_attrList.getRowCount() > 0)
			table_attrList.setRowSelectionInterval(0, 0);
	}

	private void init_table() {

		// ��Swing�д��� TreeTable
		// http://blog.itpub.net/818/viewspace-847843/
		
		table_attrList = new JTable();
		table_attrValList = new JTable();

		// �����б�Ϊ��ѡģʽ
		table_attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_attrValList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		Object[][] data_attrs = new Object[0][0];
		Object[][] data_attrVals = new Object[0][0];

		if (OPERATETYPE.equals("Func"))
			colNames_attr[4] = "�в���";
		else
			colNames_attr[4] = "ֻ��";
		
		model_attrList = new DefaultTableModel(data_attrs, colNames_attr) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0 ||column == 3) // column == 3���������п��Ա༭����Ϊ�˿�����ʾȫ������Ϣ 
					return true;// ����true��ʾ�ܱ༭��false��ʾ���ܱ༭
				else
					return false;// ����true��ʾ�ܱ༭��false��ʾ���ܱ༭
			}
		};
		table_attrList.setModel(model_attrList);
		refreshTable_obj_attr(false);

		model_attrValList = new DefaultTableModel(data_attrVals, colNames_attrVal) {
			public boolean isCellEditable(int row, int column) {
				return false;// ����true��ʾ�ܱ༭��false��ʾ���ܱ༭
			}
		};
		table_attrValList.setModel(model_attrValList);

		setTableColumnWidth();

		// ���ñ��Ĵ�С
		// table_obj_attr.setPreferredScrollableViewportSize(new Dimension(300,
		// 80));
		// �����и�
		table_attrList.setRowHeight(20);

		// �������ڹ��������
		JScrollPane scroll_obj_attr = new JScrollPane(table_attrList);

		JScrollPane scroll_attr = new JScrollPane(table_attrValList);

		// ��ӱ���ѡ���¼�������
		ListSelectionModel model;
		model = table_attrList.getSelectionModel();
		model.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				// xuky 2016.08.24 ����ᴥ�������¼���
				// �ο���http://bbs.csdn.net/topics/60106593��
				// ����Ϊ�����������¼�����һ����ԭ��ѡ�����ʧȥ��ѡ�����ԣ��ڶ�������ѡ����еõ���ѡ�������
				// ʵ�ʹ۲���ListSelectionEvent e�������һ��Ϊtrue,
				if (e.getValueIsAdjusting())
					return;
				
				// ˢ�¶������Եľ�������
				setCursor(Cursor.WAIT_CURSOR);
				refresh_attrValList();
				setCursor(Cursor.DEFAULT_CURSOR);
			}
		});
		
		table_attrList.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar()==KeyEvent.VK_SPACE){
					// �����˿ո������������������λ��Ϣ¼��
					final JFrame subWin = new JFrame();
					subWin.setTitle("¼�����ݣ����Ҷ�λ");
					subWin.setLayout(null);
					subWin.setSize(320, 150);
					
					String[] columns = { "�����ʶ","����" };
					final InfoClass info_pop = new InfoClass(columns,1);
					subWin.add(info_pop.panel);
					info_pop.panel.setBounds(0, 0, 320, 100);
					
					// ���������� ��Ҫ��ȷ�Ϻ�ȡ����ť
					JButton buttonOk = new JButton("ȷ��");
					buttonOk.setBackground(new Color(0, 114, 198));
					buttonOk.setForeground(Color.white);
					buttonOk.setBounds(30, 75, 100, 30);

					JButton buttonCancle = new JButton("ȡ��");
					buttonCancle.setBackground(new Color(0, 114, 198));
					buttonCancle.setForeground(Color.white);
					buttonCancle.setBounds(140, 75, 100, 30);
					
					final JTable table_obj = table_attrList;
					
					subWin.add(buttonOk);
					subWin.add(buttonCancle);
					buttonOk.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// ����¼�����Ϣ�����ж�λ 123
							String oi = info_pop.textFieldArr[0].getText();
							String oiname = info_pop.textFieldArr[1].getText();
							subWin.dispose();
							
							int rowNum = table_obj.getRowCount();
							if (table_obj.getSelectedRow() == table_obj.getRowCount() -1)
								return;
							
							for (int i = table_obj.getSelectedRow()+1; i < rowNum; i++) {
								if (!oi.equals("") && table_obj.getValueAt(i, 1).toString().equals(oi)){
									// �ο�http://blog.csdn.net/dancen/article/details/7379847
									table_obj.getSelectionModel().setSelectionInterval(i, i);
									Rectangle rect = table_obj.getCellRect(i, 0, true);
									table_obj.scrollRectToVisible(rect);
									break;
								}
								if (!oiname.equals("") && table_obj.getValueAt(i, 3).toString().indexOf(oiname)>=0){
									// �ο�http://blog.csdn.net/dancen/article/details/7379847
									table_obj.getSelectionModel().setSelectionInterval(i, i);
									Rectangle rect = table_obj.getCellRect(i, 0, true);
									table_obj.scrollRectToVisible(rect);
									break;
								}
							}
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
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		});

		// ��ӱ���ѡ���¼�������
		model = table_attrValList.getSelectionModel();
		model.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				int row = table_attrValList.getSelectedRow();
				if (row >= 0) {
					String tempString = (String) table_attrValList.getValueAt(row, 2);
					txt_val.setText(tempString);
					tempString = (String) table_attrValList.getValueAt(row, 3);
					txt_define.setText(tempString);

					String datatype = (String) table_attrValList.getValueAt(row, 1);
					// ���ѡ�е��ǽṹ������飬�������û���������������
					if (datatype.equals("array")
							|| datatype.equals("structure"))
						txt_val.setEditable(false);
					else
						txt_val.setEditable(true);

					if (datatype.equals("array") || datatype.equals("SEQUENCE OF") ) {
						btn_delArray.setVisible(true);
						btn_addArray.setVisible(true);
					}
					else {
						btn_addArray.setVisible(false);
						btn_delArray.setVisible(false);
					}
					
					if (datatype.indexOf("CHOICE")>=0) {
						//�ж��Ƿ�����ӽڵ㣬���ӽڵ㣬��������Ԫ�أ����ӽڵ㣬����ɾ��Ԫ��
						btn_addArray.setVisible(true);
						btn_delArray.setVisible(false);
						
						int code = (int) table_attrValList.getValueAt(row, 6);
						String arrayidx = (String) table_attrValList.getValueAt(row, 5);
						for(int i=0;i<table_attrValList.getRowCount();i++){
							// xuky 2016.09.13���ж�CHOICE�Ƿ����ӽڵ�
							
							if ((int) table_attrValList.getValueAt(i, 7)==code
									&& ((String) table_attrValList.getValueAt(i, 5)).indexOf(arrayidx+"_")>=0){
								//���ӽڵ㣬����ɾ��Ԫ��
								btn_addArray.setVisible(false);
								btn_delArray.setVisible(true);
								break;
							}
							
							/*
							if (arrayidx == null && table_attrValList.getValueAt(i, 5) == null){
								if ((int) table_attrValList.getValueAt(i, 7)==code){
									//���ӽڵ㣬����ɾ��Ԫ��
									btn_addArray.setVisible(false);
									btn_delArray.setVisible(true);
									break;
									
								}
							}
							
							if ((int) table_attrValList.getValueAt(i, 7)==code
									&& ((String) table_attrValList.getValueAt(i, 5)).equals(arrayidx)){
								//���ӽڵ㣬����ɾ��Ԫ��
								btn_addArray.setVisible(false);
								btn_delArray.setVisible(true);
								break;
							}
							*/
						}
					}					
				}
			}
		});

		panel.add(scroll_obj_attr);
		panel.add(scroll_attr);

		scroll_obj_attr.setBounds(5, 43, WINDOWWIDTH - 500,
				WINDOWHEIGHT - 150);

		btn_addArray = new JButton("����Ԫ��");

		btn_delArray = new JButton("����Ԫ��");

		panel.add(btn_addArray);
		panel.add(btn_delArray);

		btn_addArray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table_attrValList.getSelectedRow();
				if (row >= 0) {
					
					// ����Ƕ�CHOISE���͵��������ݣ�������ǰ���ú�data
					String datatype = (String) table_attrValList.getValueAt(row, 1);
					String data = (String) table_attrValList.getValueAt(row, 2);
					if (datatype.equals("CHOICE")){
						if (data == null || data.equals("")){
							DebugSwing.showMsg("��д���ݲ����棬Ȼ����ִ�д˲���");
							return;
						}
					}
					setCursor(Cursor.WAIT_CURSOR);
					int id = (int) table_attrValList.getValueAt(row, 4);
					String msg = (String) table_attrValList.getValueAt(row, 0);
					msg = msg.replaceAll("��", "");
					if (JOptionPane.showConfirmDialog(null, "׼���ԡ�" + msg
							+ "������Ԫ�أ���ȷ��", "������ʾ", JOptionPane.OK_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						id = DB.getInstance().addArray(id,OPERATETYPE);
						// ˢ���б��е����ݣ��Ҷ�λ���������׸�����
						refreshTable_attr(id);
					}
					setCursor(Cursor.DEFAULT_CURSOR);
				}
			}

		});

		btn_delArray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table_attrValList.getSelectedRow();
				if (row >= 0) {
					int id = (int) table_attrValList.getValueAt(row, 4);
					String msg = (String) table_attrValList.getValueAt(row, 0);
					msg = msg.replaceAll("��", "");
					if (JOptionPane.showConfirmDialog(null, "׼���ԡ�" + msg
							+ "������Ԫ�أ���ȷ��", "������ʾ", JOptionPane.OK_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						
						// ����Ƕ�CHOISE���͵�ɾ�����ݣ���Ҫ���ݸ��ӹ�ϵ��ɾ�����������ӽڵ�
						DB.getInstance().delArray(id,OPERATETYPE);
						// ˢ���б��е����ݣ��ұ�����ԭ����
						refreshTable_attr(id);
					}
				}
			}
		});

		btn_addArray.setBounds(320, 315, 150, 30);

		btn_delArray.setBounds(480, 315, 150, 30);

		btn_delArray.setVisible(false);
		btn_addArray.setVisible(false);

		scroll_attr.setBounds(320, 43, 450, 270);

	}
	
	private void setTableColumnWidth(){
		// �����п��
		TableColumn col = table_attrValList.getColumnModel().getColumn(0);
		col.setMinWidth(230);
		col.setMaxWidth(230);
		
		col = table_attrValList.getColumnModel().getColumn(1);
		col.setMinWidth(110);
		col.setMaxWidth(150);
		
		col = table_attrValList.getColumnModel().getColumn(2);
		col.setMinWidth(110);
		col.setMaxWidth(110);
		
		col = table_attrValList.getColumnModel().getColumn(3);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		col = table_attrValList.getColumnModel().getColumn(4);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		col = table_attrValList.getColumnModel().getColumn(5);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		col = table_attrValList.getColumnModel().getColumn(6);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		col = table_attrValList.getColumnModel().getColumn(7);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		col = table_attrValList.getColumnModel().getColumn(8);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		
	}
	
	private String getParaData(Boolean isRecord) {
		return getParaData(isRecord, -1);
	}
	
	
	// ��ȡִ�к����Ĳ�����Ϣ
	private String getParaData(Boolean isRecord, int seq) {
		String ret = "";
		//Boolean haveSep = true;
		int row = table_attrList.getSelectedRow();
		
		if (row >= 0) {
			String attr_id = DataConvert.int2String((int) table_attrList
				.getValueAt(row, 5));
			Object[][] data_attr = DB.getInstance().getAttrValList(attr_id,OPERATETYPE);
			ret = new AnalyData().getAPDUData(data_attr, seq, isRecord);
		}
		
		return ret;
	}
	
	/**
	 * ���ÿؼ��Ŀ�������
	 * 
	 * @param visible
	 *            <code>Boolean</code> ����ѡ��
	 */
	public void setVisible(Boolean visible) {
		panel.setVisible(visible);
	}


	@Override
	public void update(Observable o, Object arg) {
		String[] s = (String[]) arg;
		if (s[0].equals("save progress")){
			String data = s[2]; // ����λС���İٷֱ���Ϣ
			//System.out.println("save progress=>"+data);
			String data_int = data.split("\\.")[0];
			progressbar.setValue(DataConvert.String2Int(data_int));
			if (data.equals("100")||data.equals("100.00")){
				// �б��е�ѡ����Ϣ��Ҫ���浽���ݿ���
				panel_changeVisible.setVisible(false);
				panel_showData.setVisible(true);
				try{
					refreshTable_obj_attr(false);
				}
				catch (Exception e){
					e.printStackTrace();
				}
				progressbar.setValue(0);
			}
		}
	}
	
	public static void main(String[] args) {

		// ��д����
		OperateFrame mainFrame = new OperateFrame("");
		// ִ�к���
		//OperateFrame mainFrame = new OperateFrame("Func");
		
		mainFrame.getPanel().setBounds(0, 0, WINDOWWIDTH, WINDOWHEIGHT);

		JFrame frame = new JFrame();
		frame.setTitle("�������󷽷�");
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(WINDOWWIDTH, WINDOWHEIGHT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DebugSwing.center(frame);
	}
}
