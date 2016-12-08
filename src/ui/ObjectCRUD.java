package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

import com.eastsoft.util.DebugSwing;

import entity.Attr;
import entity.IFClass;
import entity.Method;
import entity.Obj;
import entity.TransImplAttr;
import entity.TransImplIFClass;
import entity.TransImplMethod;
import entity.TransImplObj;


/**
 * �ӿ����������������
 * 
 * @author xuky
 * @version 2016-08-29
 * 
 */
public class ObjectCRUD extends BaseFrame implements ActionListener{

	static int WINDOWWIDTH = 800;
	static int WINDOWHEIGHT = 600;
	
	private JTable table_obj, table_attr, table_method;
	DefaultTableModel model_objList, model_attrList, model_methodList;
	
	String[] colNames_obj;
	final static String[] colNames_ifclass = { "ic_id", "���ʶ", "������" };
	final static String[] colNames_attr = { "attr_id", "���", "��������", "ֻ��", "��������","host_id" };
	final static String[] colNames_method = { "method_id", "���", "��������", "�в���", "��������","host_id" };

	JButton[] buttonArr,buttonArr_attr,buttonArr_method;

	// ����ͽӿ��๫��һ���б����
	CommonObjectList objList;
	
	CommonObjectList attrList;
	CommonObjectList methodList;
	/**
	 * ���캯��
	 */
	public ObjectCRUD(String type) {
		super(type);
		// �ڹ��캯���жԽ������ݽ��г�ʼ��
	}

	/**
	 * �����ʼ��
	 */
	@Override
	protected void init() {

		init_table();
		
		// ���ܰ�ť����
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		buttonArr = new JButton[3];
		//Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < 3; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr[i] = new JButton();
			panel_buttons.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);
		}

		String txt = "����";
		if (OPERATETYPE.equals("IFC"))
			txt = "�ӿ���";
		buttonArr[0].setText("����"+txt);
		buttonArr[1].setText("�޸�"+txt);
		buttonArr[2].setText("ɾ��"+txt);
		
		buttonArr[0].setBounds(0, 0, 100, 30);
		buttonArr[1].setBounds(105, 0,100, 30);
		buttonArr[2].setBounds(210, 0, 100, 30);

		// �ڽ�������Ӱ�ť��������б�����
		panel.add(panel_buttons);
		panel_buttons.setBounds(5, 5, 300, 50);
		// ���ܰ�ť����
		
		panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		buttonArr_attr = new JButton[3];
		//Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < 3; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr_attr[i] = new JButton();
			panel_buttons.add(buttonArr_attr[i]);
			buttonArr_attr[i].addActionListener(this);
		}

		buttonArr_attr[0].setText("��������");
		buttonArr_attr[1].setText("�޸�����");
		buttonArr_attr[2].setText("ɾ������");
		
		buttonArr_attr[0].setBounds(0, 0, 90, 30);
		buttonArr_attr[1].setBounds(100, 0, 90, 30);
		buttonArr_attr[2].setBounds(200, 0, 90, 30);

		// �ڽ�������Ӱ�ť��������б�����
		panel.add(panel_buttons);
		panel_buttons.setBounds(320, 5, 300, 50);
		
		panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		buttonArr_method = new JButton[3];
		//Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < 3; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr_method[i] = new JButton();
			panel_buttons.add(buttonArr_method[i]);
			buttonArr_method[i].addActionListener(this);
		}

		buttonArr_method[0].setText("���ӷ���");
		buttonArr_method[1].setText("�޸ķ���");
		buttonArr_method[2].setText("ɾ������");
		
		buttonArr_method[0].setBounds(0, 0, 90, 30);
		buttonArr_method[1].setBounds(100, 0, 90, 30);
		buttonArr_method[2].setBounds(200, 0, 90, 30);

		// �ڽ�������Ӱ�ť��������б�����
		panel.add(panel_buttons);
		panel_buttons.setBounds(320, 280, 300, 50);
		
	}

	private void init_table() {

		table_obj = new JTable();
		// �����б�Ϊ��ѡģʽ
		table_obj.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		Object[][] data_obj = new Object[0][0];
		
		
		String[] colNames_obj1 = { "object_id", "�����ʶ", "��������", "�ӿ���" };
		
		if (OPERATETYPE.equals("IFC"))
			colNames_obj = Util698.setArrayData(colNames_ifclass);
		else
			colNames_obj = Util698.setArrayData(colNames_obj1);
		
		model_objList = new DefaultTableModel(data_obj, colNames_obj) {
			public boolean isCellEditable(int row, int column) {
				return false;// ����true��ʾ�ܱ༭��false��ʾ���ܱ༭
			}
		};
		
		// xuky 2016.09.03 �������
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(
				model_objList);
		table_obj.setRowSorter(sorter);
		
		
		// �������ڹ��������
		JScrollPane scroll_obj = new JScrollPane(table_obj);
		panel.add(scroll_obj);
		scroll_obj.setBounds(5, 43, WINDOWWIDTH - 500, WINDOWHEIGHT - 90);
		table_obj.setModel(model_objList);

		// ��ӱ���ѡ���¼�������
		ListSelectionModel model;
		model = table_obj.getSelectionModel();
		model.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				// ˢ�¶������ԡ�����
				refreshChildTable();
			}
		});

		
		table_obj.addKeyListener(new KeyListener(){

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
					
					String[] columns = { "�����ʶ","��������" };
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
					
					subWin.add(buttonOk);
					subWin.add(buttonCancle);
					buttonOk.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// ����¼�����Ϣ�����ж�λ 
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
								if (!oiname.equals("") && table_obj.getValueAt(i, 2).toString().indexOf(oiname)>=0){
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
		
		table_attr = new JTable();
		table_attr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Object[][] data_attrVals = new Object[0][0];
		model_attrList = new DefaultTableModel(data_attrVals, colNames_attr) {
			public boolean isCellEditable(int row, int column) {
				return false;// ����true��ʾ�ܱ༭��false��ʾ���ܱ༭
			}
		};
		table_attr.setModel(model_attrList);
		JScrollPane scroll_attr = new JScrollPane(table_attr);
		panel.add(scroll_attr);
		scroll_attr.setBounds(320, 43, 450, 230);
		
		
		table_method = new JTable();
		table_method.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Object[][] data_methodVals = new Object[0][0];
		model_methodList = new DefaultTableModel(data_methodVals, colNames_method) {
			public boolean isCellEditable(int row, int column) {
				return false;// ����true��ʾ�ܱ༭��false��ʾ���ܱ༭
			}
		};
		table_method.setModel(model_methodList);
		JScrollPane scroll_method = new JScrollPane(table_method);
		panel.add(scroll_method);
		scroll_method.setBounds(320, 320, 450, 230);
		

		// ��ʾ�����б�����
		refresh_ObjList();
	}

	private void setObjsTableColumnWidth() {
		// �����п��
		TableColumn col = table_obj.getColumnModel().getColumn(0);
		col.setMinWidth(0);
		col.setMaxWidth(0);
		
		col = table_obj.getColumnModel().getColumn(1);
		col.setMinWidth(60);
		col.setMaxWidth(60);

//		col = table_obj.getColumnModel().getColumn(2);
//		col.setMinWidth(55);
//		col.setMaxWidth(55);

		if (OPERATETYPE.equals("IFC")){
			
		}
		else{
			col = table_obj.getColumnModel().getColumn(3);
			col.setMinWidth(50);
			col.setMaxWidth(50);
			
		}
	}

	private void refreshChildTable() {
		refresh_AttrList();
		refresh_MethodList();
	}
	
	private void refresh_AttrList(){
		int row = table_obj.getSelectedRow();
		if (row >= 0) {
			int object_id = (int) table_obj.getValueAt(row, 0);
			
			// xuky 2016.09.03 �Ƚ���������������������ԣ�host_id��class_id���Ӧ
			if (OPERATETYPE.equals("IFC"))
				object_id = (int) table_obj.getValueAt(row, 1);
			
			Boolean isObject = true;
			if (OPERATETYPE.equals("IFC"))
				isObject = false;
			Object[][] data_attr = (Object[][]) DB.getInstance()
					.getObjectAttrList(object_id,isObject);
			//attrList = new AttrList(object_id,isObject);
			
			NewObjAction newobj_act = new NewObjAction(){
				public Object getNewObject() {	return new Attr(); }
			};
			attrList = new CommonObjectList<Attr>(newobj_act,"def3_attr","host_id,attr_idx");
			
			
			data_attr = (Object[][]) DB.getInstance().getDataname(data_attr,4);
			model_attrList.setDataVector(data_attr, colNames_attr);
			
			// �����п��
			TableColumn col = table_attr.getColumnModel().getColumn(0);
			col.setMinWidth(0);
			col.setMaxWidth(0);

			col = table_attr.getColumnModel().getColumn(5);
			col.setMinWidth(0);
			col.setMaxWidth(0);
			
		}
		if (table_attr.getRowCount() > 0)
			table_attr.setRowSelectionInterval(0, 0);
		
	}
	
	private void refresh_AttrList(int attr_id){
		refresh_AttrList();
		int rowNum = table_attr.getRowCount();
		for (int i = 0; i < rowNum; i++) {
			if ((int) table_attr.getValueAt(i, 0) == attr_id) {
				// �ο�http://blog.csdn.net/dancen/article/details/7379847
				table_attr.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table_attr.getCellRect(i, 0, true);
				table_attr.scrollRectToVisible(rect);
				break;
			}
		}
	}
	private void refresh_MethodList(){
		int row = table_obj.getSelectedRow();
		if (row >= 0) {
			int object_id = (int) table_obj.getValueAt(row, 0);
			
			// xuky 2016.09.03 �Ƚ���������������������ԣ�host_id��class_id���Ӧ
			if (OPERATETYPE.equals("IFC"))
				object_id = (int) table_obj.getValueAt(row, 1);
			
			Boolean isObject = true;
			if (OPERATETYPE.equals("IFC"))
				isObject = false;
			Object[][] data_method = (Object[][]) DB.getInstance()
					.getObjectMethodList(object_id,isObject);
			//methodList = new MethodList(object_id,isObject);
			
			NewObjAction newobj_act = new NewObjAction(){
				public Object getNewObject() {	return new Method(); }
			};
			methodList = new CommonObjectList<Attr>(newobj_act,"def4_meth","host_id,method_idx");
			
			data_method = (Object[][]) DB.getInstance().getDataname(data_method,4);
			model_methodList.setDataVector(data_method, colNames_method);
			
			// �����п��
			TableColumn col = table_method.getColumnModel().getColumn(0);
			col.setMinWidth(0);
			col.setMaxWidth(0);

			col = table_method.getColumnModel().getColumn(5);
			col.setMinWidth(0);
			col.setMaxWidth(0);
		}
		if (table_method.getRowCount() > 0)
			table_method.setRowSelectionInterval(0, 0);
	}
	
	private void refresh_MethodList(int method_id){
		refresh_MethodList();
		int rowNum = table_method.getRowCount();
		for (int i = 0; i < rowNum; i++) {
			if ((int) table_method.getValueAt(i, 0) == method_id) {
				// �ο�http://blog.csdn.net/dancen/article/details/7379847
				table_method.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table_method.getCellRect(i, 0, true);
				table_method.scrollRectToVisible(rect);
				break;
			}
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().indexOf("�޸�") >=0
				|| e.getActionCommand().indexOf("����") >=0 ) {
			
			// xuky ����һ�����ڽ��������޸�
			final String aType = e.getActionCommand();
			
			if (aType.equals("�޸Ķ���")||aType.equals("�޸Ľӿ���"))
				if (table_obj.getRowCount() <= 0)
					return;
			if (aType.equals("�޸�����"))
				if (table_attr.getRowCount() <= 0)
					return;
			if (aType.equals("�޸ķ���"))
				if (table_method.getRowCount() <= 0)
					return;

			final JFrame subWin = new JFrame();
			subWin.setTitle("��������");
			if (aType.indexOf("�޸�")>=0)
				subWin.setTitle("�޸�����");
			subWin.setLayout(null);
			subWin.setSize(760, 320);

			//����༭����-�ؼ�����
			String[] ObjColumns = { "�����ʶ","��������","�ӿ���" };
			String[] IFClassColumns = { "���ʶ","������" };
			//���Ա༭����-�ؼ�����
			String[] AttrColumns = { "���","��������","ֻ��","��������" };
			//�����༭����-�ؼ�����
			String[] MethodColumns = { "���","��������","�в���","��������" };

			
			// ���ģʽ������仯����
			// 1\4-ָ�����ֶ���Ϣ
			String[] columns = null;
			if (aType.indexOf("����") >=0)
				columns = ObjColumns;
			
			if (aType.indexOf("�ӿ���") >=0)
				columns = IFClassColumns;
			
			if (aType.indexOf("����") >=0)		
				columns = AttrColumns;
			
			if (aType.indexOf("����") >=0)		
				columns = MethodColumns;
			
			// 1\4-������ϸ��ʾ����ؼ�
			final InfoClass info_pop = new InfoClass(columns);
			
			// 2\4-ָ������ת���ӿ�ʵ�֣�����ת������- ���ڿؼ�����ת�������ԡ���������ת���ڿؼ����ݣ�
			if (aType.indexOf("����") >=0)		
				info_pop.setTrans(new TransImplObj());
			if (aType.indexOf("�ӿ���") >=0)		
				info_pop.setTrans(new TransImplIFClass());
			if (aType.indexOf("����") >=0)		
				info_pop.setTrans(new TransImplAttr());
			if (aType.indexOf("����") >=0)		
				info_pop.setTrans(new TransImplMethod());
			
			
			subWin.add(info_pop.panel);
			info_pop.panel.setBounds(5, 5, 750, 500);

			if (aType.equals("�޸Ķ���") || aType.equals("�޸Ľӿ���") ) {
				int id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 0);
				// 3\4-������������
				info_pop.trans.setData(objList.getOne(id));
			}
			if (aType.equals("�޸�����")) {
				int id = (int) table_attr.getValueAt(table_attr.getSelectedRow(), 0);
				// 3\4-������������
				info_pop.trans.setData(attrList.getOne(id));
			}
			if (aType.equals("�޸ķ���")) {
				int id = (int) table_method.getValueAt(table_method.getSelectedRow(), 0);
				// 3\4-������������
				info_pop.trans.setData(methodList.getOne(id));
			}
			
			// ���������� ��Ҫ��ȷ�Ϻ�ȡ����ť
			JButton buttonOk = new JButton("ȷ��");
			JButton buttonCancle = new JButton("ȡ��");
			buttonOk.setBackground(new Color(0, 114, 198));
			buttonCancle.setBackground(new Color(0, 114, 198));
			buttonOk.setForeground(Color.white);
			buttonCancle.setForeground(Color.white);
			buttonOk.setBounds(250, 240, 100, 30);
			buttonCancle.setBounds(355, 240, 100, 30);
			subWin.add(buttonOk);
			subWin.add(buttonCancle);

			// xuky 2016.08.28 ���´���δ��Ч
			// �����еĿؼ���ý���
			info_pop.setFocus();

			// ȷ�ϰ�ť����¼�
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// �޸�������ͣ���Ч
					setCursor(Cursor.WAIT_CURSOR);
					// http://blog.csdn.net/ethanq/article/details/7200490
					int id = 0;
					if (aType.indexOf("����")>=0 || aType.indexOf("�ӿ���")>=0){
						Object d;
						if (aType.indexOf("����")>=0) {
							// 4\4-�ӽ���õ�����
							d = info_pop.trans.getData();
							Map<String, Object> infoMap = Util698.getFirstFiledsInfo(d);
							id = objList.getUseableID();
							//String fieldName, Object o, Object val,Object valType
							Util698.setFieldValueByName(infoMap.get("name").toString(),d,id);
							//d.setObject_id(id);
							objList.add(d);
						} 
						else if (aType.indexOf("�޸�")>=0) {
							// 4\4-�ӽ���õ�����
							d = info_pop.trans.getDataWithID();
							Map<String, Object> infoMap = Util698.getFirstFiledsInfo(d);
							objList.update(d);
							id = (int)Util698.getFieldValueByName(infoMap.get("name").toString(),d);
//							id = d.getObject_id();  // ��refresh_List(id)�ж�λ��
						}
						refresh_ObjList(id);
					}
					if (aType.indexOf("����")>=0){
						Attr d;
						if (aType.indexOf("����")>=0) {
							// 4\4-�ӽ���õ�����
							d = (Attr)info_pop.trans.getData();
							id = attrList.getUseableID();
							d.setAttr_id(id);
							// ��Ҫ����host_idΪ�����id
							int host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 0);
							// xuky �ӿ���ʹ��class_id��Ϊ 
							if (OPERATETYPE.equals("IFC")){
								host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 1);
								// xuky 2016.09.13 Sort_id=0��ʾ�ӿ�������
								d.setSort_id(0);
							}
							else
								// xuky 2016.09.13 Sort_id=1��ʾ��������
								d.setSort_id(1);
							d.setHost_id(host_id);
							attrList.add(d);
						} 
						else if (aType.indexOf("�޸�")>=0) {
							// 4\4-�ӽ���õ�����
							d = (Attr)info_pop.trans.getDataWithID();
							attrList.update(d);
							id = d.getAttr_id();  // ��refresh_List(id)�ж�λ��
						}
						refresh_AttrList(id);
					}
					if (aType.indexOf("����")>=0){
						Method d;
						if (aType.indexOf("����")>=0) {
							// 4\4-�ӽ���õ�����
							d = (Method)info_pop.trans.getData();
							id = methodList.getUseableID();
							d.setMethod_id(id);
							// ��Ҫ����host_idΪ�����id
							int host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 0);
							// xuky �ӿ���ʹ��class_id��Ϊ 
							if (OPERATETYPE.equals("IFC")){
								host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 1);
								// xuky 2016.09.13 Sort_id=0��ʾ�ӿ�������
								d.setSort_id(0);
							}
							else
								// xuky 2016.09.13 Sort_id=1��ʾ��������
								d.setSort_id(1);
							d.setHost_id(host_id);
							methodList.add(d);
						} 
						else if (aType.indexOf("�޸�")>=0) {
							// 4\4-�ӽ���õ�����
							d = (Method)info_pop.trans.getDataWithID();
							methodList.update(d);
							id = d.getMethod_id();  // ��refresh_List(id)�ж�λ��
						}
						refresh_MethodList(id);
					}
					subWin.dispose();
					setCursor(Cursor.DEFAULT_CURSOR);
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
					if (aType.indexOf("����")>=0||aType.indexOf("����")>=0){
						// ���ԡ������ı༭��������ѡ���������͵ĵ�������������Ͳ������������ǰ̨��
					}
					else
						e.getWindow().toFront();
				}
			});
		}
		
		if (e.getActionCommand().equals("ɾ������") || e.getActionCommand().equals("ɾ���ӿ���") ) {
			int rowcount = model_objList.getRowCount();
			if (rowcount > 0) {
				// xuky ɾ����ǰ��
				int rowCount = table_obj.getRowCount(); 
				int deleterow = table_obj.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,
						"�Ƿ�ȷ��ɾ��\"" + table_obj.getValueAt(deleterow, 1)+"-"+table_obj.getValueAt(deleterow, 2) + "\"",
						"ɾ����ʾ", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					objList.delete((int) table_obj.getValueAt(deleterow, 0));
					// ��λ��ɾ���е�ǰһ��deleterow-1
					// ��ʱΪɾ�������һ������ ��ɾ���˵�һ��
					if (rowCount == 1 || deleterow==0)
						refresh_ObjList();
					else{
						int id = (int)table_obj.getValueAt(deleterow-1, 0);
						refresh_ObjList(id);
					}
				}
			}
		}
		
		if (e.getActionCommand().equals("ɾ������")) {
			int rowcount = model_attrList.getRowCount();
			if (rowcount > 0) {
				// xuky ɾ����ǰ��
				int rowCount = table_attr.getRowCount(); 
				int deleterow = table_attr.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,						
						"�Ƿ�ȷ��ɾ��\"" + table_attr.getValueAt(deleterow, 1)+"-"+table_attr.getValueAt(deleterow, 2) + "\"",
						"ɾ����ʾ", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					//attrList.delete((int) table_attr.getValueAt(deleterow, 5),(int) table_attr.getValueAt(deleterow, 1));
					attrList.delete((int) table_attr.getValueAt(deleterow, 0));
					// ��λ��ɾ���е�ǰһ��deleterow-1
					// ��ʱΪɾ�������һ������ ��ɾ���˵�һ��
					if (rowCount == 1 || deleterow==0)
						refresh_AttrList();
					else{
						int id = (int)table_attr.getValueAt(deleterow-1, 0);
						refresh_AttrList(id);
					}
				}
			}
		}
		if (e.getActionCommand().equals("ɾ������")) {
			int rowcount = model_methodList.getRowCount();
			if (rowcount > 0) {
				// xuky ɾ����ǰ��
				int rowCount = table_method.getRowCount(); 
				int deleterow = table_method.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,						
						"�Ƿ�ȷ��ɾ��\"" + table_method.getValueAt(deleterow, 1)+"-"+table_method.getValueAt(deleterow, 2) + "\"",
						"ɾ����ʾ", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					//methodList.delete((int) table_method.getValueAt(deleterow, 5),(int) table_method.getValueAt(deleterow, 1));
					methodList.delete((int) table_method.getValueAt(deleterow, 0));
					// ��λ��ɾ���е�ǰһ��deleterow-1
					// ��ʱΪɾ�������һ������ ��ɾ���˵�һ��
					if (rowCount == 1 || deleterow==0)
						refresh_MethodList();
					else{
						int id = (int)table_method.getValueAt(deleterow-1, 0);
						refresh_MethodList(id);
					}
				}
			}
		}
		
	}
	private void refresh_ObjList() {
		if (OPERATETYPE.equals("IFC")){
			NewObjAction newobj_act = new NewObjAction(){
				public Object getNewObject() {	return new IFClass(); }
			};
			
			objList = new CommonObjectList<IFClass>(newobj_act,"def1_ic","class_id");
			
			Object[][] data1 = DB.getInstance().getIFClassList();
			model_objList.setDataVector(data1, colNames_obj);
		}
		else{
			//objList = new ObjList();
			
			NewObjAction newobj_act = new NewObjAction(){
				public Object getNewObject() {	return new Obj(); }
			};
			objList = new CommonObjectList<Obj>(newobj_act,"def2_obj","oi");
			
			Object[][] data1 = DB.getInstance().getObjectList();
			model_objList.setDataVector(data1, colNames_obj);
		}
		
		if (table_obj.getRowCount() > 0){
			table_obj.getSelectionModel().setSelectionInterval(0,0); 
			table_obj.setRowSelectionInterval(0, 0);
		}
		//table_obj.editCellAt(1, 1);		
		setObjsTableColumnWidth();
		table_obj.requestFocus();
	}
	private void refresh_ObjList(int id) {
		refresh_ObjList();

		int rowNum = table_obj.getRowCount();
		for (int i = 0; i < rowNum; i++) {
			if ((int) table_obj.getValueAt(i, 0) == id) {
				// �ο�http://blog.csdn.net/dancen/article/details/7379847
				table_obj.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table_obj.getCellRect(i, 0, true);
				table_obj.scrollRectToVisible(rect);
				break;
			}
		}

	}

	public static void main(String[] args) {

		ObjectCRUD mainFrame = new ObjectCRUD("");
		//ObjectCRUD mainFrame = new ObjectCRUD("IFC");

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
