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
 * 读取、设置属性，操作方法界面
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
	private JComboBox<String> cb_opType; // 操作类型
	private JCheckBox chk_select;
	
	
	// private JPanel panel_add, panel_del;
	JProgressBar progressbar;

	// 对象选中属性列表
	static final String[] colNames_attr = { "选中", "oi", "编号", "名称", "只读",
			"attr_id" };
	
	// 属性内容列表
	static final String[] colNames_attrVal = { "名称", "类型", "数值", "definevalue", "id",
			"arrayidx","code","fcode","seq" };
	
	// 对象全部属性列表
	static final String[] colNames_attr_all = { "显示项", "oi", "编号", "名称", "只读",
			"attr_id", "select_old" };

	// private JScrollPane scroll_analy,scroll_analyAPDU;
	// private JCheckBox checkbox;

	/**
	 * 构造函数
	 */
	public OperateFrame(String type) {
		super(type);
		Publisher.getInstance().addObserver(this);
	}

	
	/**
	 * 界面初始化
	 */
	@Override
	protected void init() {

		panel_changeVisible = new JPanel(null);
		panel_showData = new JPanel(null);

		// /////////////////////////
		lb_val = new JLabel("数值", JLabel.CENTER);
		lb_define = new JLabel("含义", JLabel.CENTER);
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
		
		txt_define.setLineWrap(true);// 激活自动换行功能
		txt_define.setWrapStyleWord(true);// 激活断行不断字功能
		JScrollPane scroll_define = new JScrollPane(txt_define);
		scroll_define.setBounds(360, 390, 410, 130);
		panel.add(scroll_define);

		btn_val_save = new JButton("保存");
		btn_val_save.setBounds(670, 350, 100, 30);
		panel.add(btn_val_save);

		txt_frame = new JTextField(5000);
		panel.add(txt_frame);
		txt_frame.setBounds(360, 520, 410, 30);

		lb_frame = new JLabel("APDU", JLabel.CENTER);
		lb_frame.setBounds(300, 520, 60, 30);
		panel.add(lb_frame);
		
		
		if (OPERATETYPE.equals("Func")){
			btn_read = new JButton("操作");
			btn_write = new JButton("操作后读取");
			btn_writenandread = new JButton("设置后读取");
			btn_writeOneItem = new JButton("设置单个");
			btn_writenandread.setVisible(false);
			btn_writeOneItem.setVisible(false);
		}
		else{
			btn_read = new JButton("读取");
			btn_write = new JButton("设置");
			btn_writenandread = new JButton("设置后读取");
			btn_writeOneItem = new JButton("设置单个");			
		}

		panel.add(btn_write);
		
		panel.add(btn_read);
		panel.add(btn_writenandread);
		panel.add(btn_writeOneItem);
		

		// btn_read.setBounds(0,0,100,30);

		lb_opType = new JLabel("操作类型", JLabel.CENTER);
		String txt = "";
		if (OPERATETYPE.equals("Func"))
			txt = "每帧方法个数";
		else
			txt = "每帧OAD个数";
		lb_oadNum = new JLabel(txt, JLabel.CENTER);
		

		Vector<String> v = null;
		v = new Vector<String>();
		v.addElement("单项(当前行)");
		v.addElement("选中项");
		v.addElement("全部(无编号0)");
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

		btn_chg_oadvisible = new JButton("调整显示内容");
		btn_chg_oadvisible.setBounds(130, 0, 120, 30);
		
		
		btn_refresh = new JButton("刷新数据");
		btn_refresh.setBounds(0, 0, 120, 30);

		// 此功能应该在对象管理中
		//btn_set_attrVal = new JButton("调整属性值");
		//panel_showData.add(btn_set_attrVal);
		//btn_set_attrVal.setBounds(125, 0, 125, 30);

		panel_showData.add(btn_chg_oadvisible);
		panel_showData.add(btn_refresh);

		panel.add(panel_showData);

		panel_changeVisible.setBounds(5, 500, 300, 60);

		chk_select = new JCheckBox("选择", false);
		chk_select.setBounds(0, 0, 70, 30);
		panel_changeVisible.add(chk_select);

		btn_visible_save = new JButton("保存");
		btn_visible_cancel = new JButton("取消");
		
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
						"如果“保存”，之前所做的修改将会修改到数据库中，请确认", "操作提示",
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
						"如果“取消”，之前所做的修改将会无效，请确认", "操作提示",
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
					// 修改列表中所有数据为选中状态
					for (int i = 0; i < row; i++) {
						model_attrList.setValueAt(true, i, 0);
					}
					// 无需此代码，可以实现数据同步
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

				// 无数据则退出
				int row = table_attrList.getRowCount();
				if (row <= 0)
					return;
				
				APDU aPDU = new APDU();
				aPDU.setChoiseFlag(5);
				
				if (OPERATETYPE.equals("Func"))
					aPDU.setChoiseFlag(7);
				
				// 1 代理模式				
				if (SoftParameter.getInstance().getIsProxyModel().equals("1"))
					aPDU.setChoiseFlag(9);
				
				String nextData = "";
				String apdu = aPDU.getString();

				if (cb_opType.getSelectedItem().toString().indexOf("单") >= 0) {
					int currLine = table_attrList.getSelectedRow();
					
					// 判断属性的内容，如果有RSD和RCSD，为读记录型数据
					int attr_id = (int) table_attrList.getValueAt(currLine, 5);
					Boolean isRecord = DB.getInstance().isRecord(attr_id);
					
					// 单个
					aPDU.setChoiseFlag_1(1);
					String oi = (String) table_attrList.getValueAt(currLine, 1);
					String no = (String) table_attrList.getValueAt(currLine, 2);
					// OI + 属性值 + “00” = OAD
					String OAD = oi + DataConvert.String2HexString(no, 2) + "00";
					OAD = Util698.seprateString(OAD, " ");
					nextData = OAD;

					// 2 代理模式报文处理过程
					if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
						aPDU.setChoiseFlag_1(1);
						nextData = getProxyData("01, " +OAD);
					}
					
					if (OPERATETYPE.equals("Func")){
						
						String paraData = getParaData(false);
						// xuky 2016.10.31 OMD+data 无data时刻，使用00，null
						if (paraData.equals(""))
							paraData = "00";
						nextData += ","+paraData;
						
						if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
							aPDU.setChoiseFlag_1(5);
							if (OAD.indexOf("FF FF 01 00") >=0){
								aPDU.setChoiseFlag_1(7);
								// 代理操作透明转发请求命令 特殊处理   去掉前面的structure数据 
								// xuky 2016.11.10 与王文娟商议后  去掉OAD类型码51、去掉COMDCB类型码5F、后面的类型码保留 
								// 0205,51F2090201,5F 03 02 08 01 00 ,12003C,120064,090468006816
								nextData = paraData.substring(7);
								nextData = nextData.substring(0,9)+ nextData.substring(11);
								// F2090201, 03 02 08 01 00 ,12003C,120064,090468006816
								// xuky 2016.11.15 还需要继续处理，去掉12*2 、09类型代码
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
				
				
				if (cb_opType.getSelectedItem().toString().indexOf("选中") >= 0) {
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
					// 去掉后面分隔用的多余字符
					OAD = OAD.substring(0,OAD.length()-2);
					nextData = OAD;
					
					// 代理模式报文处理过程
					if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
						aPDU.setChoiseFlag_1(1);
						nextData = getProxyData(OAD);
					}
					
				}
				nextData += " ,00";  //添加时间标签信息
				aPDU.setNextData(nextData);
				apdu = aPDU.getString();
				txt_frame.setText(apdu);
			}
		});
		
		// 对属性进行整体设置
		btn_write.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txt_frame.setText("");

				// 无数据则退出
				int row = table_attrList.getRowCount();
				if (row <= 0)
					return;
				
				APDU aPDU = new APDU();
				aPDU.setChoiseFlag(6);
				
				// 1 代理模式				
				if (SoftParameter.getInstance().getIsProxyModel().equals("1"))
					aPDU.setChoiseFlag(9);
				
				String nextData = "";
				String apdu = aPDU.getString();

				if (cb_opType.getSelectedItem().toString().indexOf("单") >= 0) {
					
					int currLine = table_attrList.getSelectedRow();
					
					// 判断属性的内容，如果有RSD和RCSD，为读记录型数据
					int attr_id = (int) table_attrList.getValueAt(currLine, 5);
					Boolean isRecord = DB.getInstance().isRecord(attr_id);
					
					// 单个
					aPDU.setChoiseFlag_1(1);
					String oi = (String) table_attrList.getValueAt(currLine, 1);
					String no = (String) table_attrList.getValueAt(currLine, 2);
					// OI + 属性值 + “00” = OAD
					
					String OAD = oi + DataConvert.String2HexString(no, 2) + "00";
					OAD = Util698.seprateString(OAD, " ");
					nextData = OAD;
					
					String paraData = getParaData(false);
					nextData += " ,"+paraData;
					
					// 2 代理模式报文处理过程
					if (SoftParameter.getInstance().getIsProxyModel().equals("1")){
						aPDU.setChoiseFlag_1(3);
						nextData = getProxyData("01, " +OAD+" ," + paraData);
					}
					
				}
				if (cb_opType.getSelectedItem().toString().indexOf("选中") >= 0) {
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
					
					
					// 尚未完成，需要对选中的多个方法，逐个获取其参数
					// getParaData只是对当前行进行处理
				}
				
				nextData += " ,00";  //添加时间标签信息
				aPDU.setNextData(nextData);
				apdu = aPDU.getString();
				txt_frame.setText(apdu);
			}
		});
		

		// 对单个属性进行设置 
		btn_writeOneItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txt_frame.setText("");

				// 无数据则退出
				int row = table_attrList.getRowCount();
				if (row <= 0)
					return;
				
				APDU aPDU = new APDU();
				aPDU.setChoiseFlag(6);
				
				String nextData = "";
				String apdu = aPDU.getString();

				if (cb_opType.getSelectedItem().toString().indexOf("单") >= 0) {
					
					int currLine = table_attrList.getSelectedRow();
					
					
					
					// 单个 
					aPDU.setChoiseFlag_1(1);
					String oi = (String) table_attrList.getValueAt(currLine, 1);
					String no = (String) table_attrList.getValueAt(currLine, 2);
					// OI + 属性值 + “00” = OAD
					
					//123
					//table_attrValList
					// .getValueAt(currLine, 8);
					
					int valRow = table_attrValList.getSelectedRow(); 
					if ( valRow <= 0)
						return;
					int seq = (int)table_attrValList.getValueAt(valRow, 8);
					
					// seq -1 后进行处理
					nextData = oi + DataConvert.String2HexString(no, 2) + DataConvert.int2HexString(seq-1, 2);
					nextData = Util698.seprateString(nextData, " ") ;
					
					String data = getParaData(false,seq);
					nextData += ","+data;
					
					nextData += ",00";  //添加时间标签信息
					
					aPDU.setNextData(nextData);
					apdu = aPDU.getString();
				}
				if (cb_opType.getSelectedItem().toString().indexOf("选中") >= 0) {
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
		
		// 整体超时
		if (!isRecord)
			ret = DataConvert.int2HexString(SoftParameter.getInstance().getAll_timeout(),4) +" ,";
		
		String[] proxyTargets = SoftParameter.getInstance().getTargets().split(",");
		int proxyNum = proxyTargets.length;
		// 目标个数
		if (!isRecord)
			ret += DataConvert.int2HexString(proxyNum, 2) +" ,";
		
		for(String target: proxyTargets ){
			if (isRecord){
				// 单个超时
				ret += DataConvert.int2HexString(SoftParameter.getInstance().getSingle_timeout(),4) +" ,";
				// 目标地址
				ret += new AnalyData().getTSA(target) +" ,";
				// OAD 信息
				ret += OAD +", ";
				break;
			}
			else{
				// 目标地址
				ret += new AnalyData().getTSA(target) +" ,";
				// 单个超时
				ret += DataConvert.int2HexString(SoftParameter.getInstance().getSingle_timeout(),4) +" ,";
				// OAD 信息
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
				// xuky 2016。08.25 滚动到某行，但是因为table放在了scrollpanle中，有时会无效
				// table_attr.setRowSelectionInterval(i, i);

				// 参考http://blog.csdn.net/dancen/article/details/7379847
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
			
			// getAttrValList 的参数是 attr_id或method_id
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
				colNames_attr[4] = "有参数";
			else
				colNames_attr[4] = "只读";
			model_attrList.setDataVector(data_obj_attr, colNames_attr);
		} else {
			data_obj_attr = DB.getInstance().getAllOADList(OPERATETYPE);
			model_attrList.setDataVector(data_obj_attr, colNames_attr_all);

			TableColumn col = table_attrList.getColumnModel().getColumn(6);
			col.setMinWidth(0);
			col.setMaxWidth(0);
		}

		// 设置首列为checkbox，用于进行选择控制
		TableColumn col = table_attrList.getColumnModel().getColumn(0);
		col.setCellEditor(table_attrList.getDefaultEditor(Boolean.class));
		col.setCellRenderer(table_attrList.getDefaultRenderer(Boolean.class));
		// readonly列为，用于进行数据展示
		col = table_attrList.getColumnModel().getColumn(4);
		col.setCellEditor(table_attrList.getDefaultEditor(Boolean.class));
		col.setCellRenderer(table_attrList.getDefaultRenderer(Boolean.class));

		// oi列隐藏，用于进行报文组织用
		col = table_attrList.getColumnModel().getColumn(1);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		// attr_id列隐藏，用于进行报文组织用
		col = table_attrList.getColumnModel().getColumn(5);
		col.setMinWidth(0);
		col.setMaxWidth(0);

		// 调整列宽度
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

		// 在Swing中创建 TreeTable
		// http://blog.itpub.net/818/viewspace-847843/
		
		table_attrList = new JTable();
		table_attrValList = new JTable();

		// 设置列表为单选模式
		table_attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_attrValList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		Object[][] data_attrs = new Object[0][0];
		Object[][] data_attrVals = new Object[0][0];

		if (OPERATETYPE.equals("Func"))
			colNames_attr[4] = "有参数";
		else
			colNames_attr[4] = "只读";
		
		model_attrList = new DefaultTableModel(data_attrs, colNames_attr) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0 ||column == 3) // column == 3允许名称列可以编辑，是为了可以显示全部的信息 
					return true;// 返回true表示能编辑，false表示不能编辑
				else
					return false;// 返回true表示能编辑，false表示不能编辑
			}
		};
		table_attrList.setModel(model_attrList);
		refreshTable_obj_attr(false);

		model_attrValList = new DefaultTableModel(data_attrVals, colNames_attrVal) {
			public boolean isCellEditable(int row, int column) {
				return false;// 返回true表示能编辑，false表示不能编辑
			}
		};
		table_attrValList.setModel(model_attrValList);

		setTableColumnWidth();

		// 设置表格的大小
		// table_obj_attr.setPreferredScrollableViewportSize(new Dimension(300,
		// 80));
		// 设置行高
		table_attrList.setRowHeight(20);

		// 表格放置在滚动面板中
		JScrollPane scroll_obj_attr = new JScrollPane(table_attrList);

		JScrollPane scroll_attr = new JScrollPane(table_attrValList);

		// 添加表格的选中事件监听器
		ListSelectionModel model;
		model = table_attrList.getSelectionModel();
		model.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				// xuky 2016.08.24 这里会触发两次事件，
				// 参考《http://bbs.csdn.net/topics/60106593》
				// 是因为触发了两次事件。第一次是原本选择的行失去了选择属性，第二次是新选择的行得到了选择的属性
				// 实际观察了ListSelectionEvent e的情况，一次为true,
				if (e.getValueIsAdjusting())
					return;
				
				// 刷新对象属性的具体内容
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
					// 按下了空格键，弹窗进行搜索定位信息录入
					final JFrame subWin = new JFrame();
					subWin.setTitle("录入数据，查找定位");
					subWin.setLayout(null);
					subWin.setSize(320, 150);
					
					String[] columns = { "对象标识","名称" };
					final InfoClass info_pop = new InfoClass(columns,1);
					subWin.add(info_pop.panel);
					info_pop.panel.setBounds(0, 0, 320, 100);
					
					// 弹出窗口中 需要有确认和取消按钮
					JButton buttonOk = new JButton("确定");
					buttonOk.setBackground(new Color(0, 114, 198));
					buttonOk.setForeground(Color.white);
					buttonOk.setBounds(30, 75, 100, 30);

					JButton buttonCancle = new JButton("取消");
					buttonCancle.setBackground(new Color(0, 114, 198));
					buttonCancle.setForeground(Color.white);
					buttonCancle.setBounds(140, 75, 100, 30);
					
					final JTable table_obj = table_attrList;
					
					subWin.add(buttonOk);
					subWin.add(buttonCancle);
					buttonOk.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// 根据录入的信息，进行定位 123
							String oi = info_pop.textFieldArr[0].getText();
							String oiname = info_pop.textFieldArr[1].getText();
							subWin.dispose();
							
							int rowNum = table_obj.getRowCount();
							if (table_obj.getSelectedRow() == table_obj.getRowCount() -1)
								return;
							
							for (int i = table_obj.getSelectedRow()+1; i < rowNum; i++) {
								if (!oi.equals("") && table_obj.getValueAt(i, 1).toString().equals(oi)){
									// 参考http://blog.csdn.net/dancen/article/details/7379847
									table_obj.getSelectionModel().setSelectionInterval(i, i);
									Rectangle rect = table_obj.getCellRect(i, 0, true);
									table_obj.scrollRectToVisible(rect);
									break;
								}
								if (!oiname.equals("") && table_obj.getValueAt(i, 3).toString().indexOf(oiname)>=0){
									// 参考http://blog.csdn.net/dancen/article/details/7379847
									table_obj.getSelectionModel().setSelectionInterval(i, i);
									Rectangle rect = table_obj.getCellRect(i, 0, true);
									table_obj.scrollRectToVisible(rect);
									break;
								}
							}
						}
					});
					// 取消按钮添加事件
					buttonCancle.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							subWin.dispose();
						}
					});
					
					DebugSwing.center(subWin); // xuky 窗口居中
					subWin.setVisible(true);

					// xuky 弹出的窗口锁定焦点，不允许定位到主窗口
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

		// 添加表格的选中事件监听器
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
					// 如果选中的是结构体或数组，不允许用户调整其数据内容
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
						//判断是否存在子节点，无子节点，可以增加元素，有子节点，可以删除元素
						btn_addArray.setVisible(true);
						btn_delArray.setVisible(false);
						
						int code = (int) table_attrValList.getValueAt(row, 6);
						String arrayidx = (String) table_attrValList.getValueAt(row, 5);
						for(int i=0;i<table_attrValList.getRowCount();i++){
							// xuky 2016.09.13　判断CHOICE是否有子节点
							
							if ((int) table_attrValList.getValueAt(i, 7)==code
									&& ((String) table_attrValList.getValueAt(i, 5)).indexOf(arrayidx+"_")>=0){
								//有子节点，可以删除元素
								btn_addArray.setVisible(false);
								btn_delArray.setVisible(true);
								break;
							}
							
							/*
							if (arrayidx == null && table_attrValList.getValueAt(i, 5) == null){
								if ((int) table_attrValList.getValueAt(i, 7)==code){
									//有子节点，可以删除元素
									btn_addArray.setVisible(false);
									btn_delArray.setVisible(true);
									break;
									
								}
							}
							
							if ((int) table_attrValList.getValueAt(i, 7)==code
									&& ((String) table_attrValList.getValueAt(i, 5)).equals(arrayidx)){
								//有子节点，可以删除元素
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

		btn_addArray = new JButton("增加元素");

		btn_delArray = new JButton("减少元素");

		panel.add(btn_addArray);
		panel.add(btn_delArray);

		btn_addArray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table_attrValList.getSelectedRow();
				if (row >= 0) {
					
					// 如果是对CHOISE类型的增加数据，必须提前设置好data
					String datatype = (String) table_attrValList.getValueAt(row, 1);
					String data = (String) table_attrValList.getValueAt(row, 2);
					if (datatype.equals("CHOICE")){
						if (data == null || data.equals("")){
							DebugSwing.showMsg("填写数据并保存，然后再执行此操作");
							return;
						}
					}
					setCursor(Cursor.WAIT_CURSOR);
					int id = (int) table_attrValList.getValueAt(row, 4);
					String msg = (String) table_attrValList.getValueAt(row, 0);
					msg = msg.replaceAll("－", "");
					if (JOptionPane.showConfirmDialog(null, "准备对“" + msg
							+ "”增加元素，请确认", "操作提示", JOptionPane.OK_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						id = DB.getInstance().addArray(id,OPERATETYPE);
						// 刷新列表中的数据，且定位到新增的首个数据
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
					msg = msg.replaceAll("－", "");
					if (JOptionPane.showConfirmDialog(null, "准备对“" + msg
							+ "”减少元素，请确认", "操作提示", JOptionPane.OK_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						
						// 如果是对CHOISE类型的删除数据，需要根据父子关系，删除其下所有子节点
						DB.getInstance().delArray(id,OPERATETYPE);
						// 刷新列表中的数据，且保持在原先行
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
		// 调整列宽度
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
	
	
	// 获取执行函数的参数信息
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
	 * 设置控件的可视属性
	 * 
	 * @param visible
	 *            <code>Boolean</code> 可视选项
	 */
	public void setVisible(Boolean visible) {
		panel.setVisible(visible);
	}


	@Override
	public void update(Observable o, Object arg) {
		String[] s = (String[]) arg;
		if (s[0].equals("save progress")){
			String data = s[2]; // 含两位小数的百分比信息
			//System.out.println("save progress=>"+data);
			String data_int = data.split("\\.")[0];
			progressbar.setValue(DataConvert.String2Int(data_int));
			if (data.equals("100")||data.equals("100.00")){
				// 列表中的选中信息需要保存到数据库中
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

		// 读写属性
		OperateFrame mainFrame = new OperateFrame("");
		// 执行函数
		//OperateFrame mainFrame = new OperateFrame("Func");
		
		mainFrame.getPanel().setBounds(0, 0, WINDOWWIDTH, WINDOWHEIGHT);

		JFrame frame = new JFrame();
		frame.setTitle("操作对象方法");
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(WINDOWWIDTH, WINDOWHEIGHT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DebugSwing.center(frame);
	}
}
