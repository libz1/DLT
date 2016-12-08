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
 * 接口类管理、对象管理界面
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
	final static String[] colNames_ifclass = { "ic_id", "类标识", "类名称" };
	final static String[] colNames_attr = { "attr_id", "编号", "属性名称", "只读", "数据类型","host_id" };
	final static String[] colNames_method = { "method_id", "编号", "方法名称", "有参数", "参数类型","host_id" };

	JButton[] buttonArr,buttonArr_attr,buttonArr_method;

	// 对象和接口类公用一个列表对象
	CommonObjectList objList;
	
	CommonObjectList attrList;
	CommonObjectList methodList;
	/**
	 * 构造函数
	 */
	public ObjectCRUD(String type) {
		super(type);
		// 在构造函数中对界面内容进行初始化
	}

	/**
	 * 界面初始化
	 */
	@Override
	protected void init() {

		init_table();
		
		// 功能按钮设置
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		buttonArr = new JButton[3];
		//Font font = new Font("宋体", Font.BOLD, 13);
		for (int i = 0; i < 3; i++) { // 通过一个循环,对按钮数组中的每一个按钮实例化.
			buttonArr[i] = new JButton();
			panel_buttons.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);
		}

		String txt = "对象";
		if (OPERATETYPE.equals("IFC"))
			txt = "接口类";
		buttonArr[0].setText("增加"+txt);
		buttonArr[1].setText("修改"+txt);
		buttonArr[2].setText("删除"+txt);
		
		buttonArr[0].setBounds(0, 0, 100, 30);
		buttonArr[1].setBounds(105, 0,100, 30);
		buttonArr[2].setBounds(210, 0, 100, 30);

		// 在界面中添加按钮区域、添加列表区域
		panel.add(panel_buttons);
		panel_buttons.setBounds(5, 5, 300, 50);
		// 功能按钮设置
		
		panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		buttonArr_attr = new JButton[3];
		//Font font = new Font("宋体", Font.BOLD, 13);
		for (int i = 0; i < 3; i++) { // 通过一个循环,对按钮数组中的每一个按钮实例化.
			buttonArr_attr[i] = new JButton();
			panel_buttons.add(buttonArr_attr[i]);
			buttonArr_attr[i].addActionListener(this);
		}

		buttonArr_attr[0].setText("增加属性");
		buttonArr_attr[1].setText("修改属性");
		buttonArr_attr[2].setText("删除属性");
		
		buttonArr_attr[0].setBounds(0, 0, 90, 30);
		buttonArr_attr[1].setBounds(100, 0, 90, 30);
		buttonArr_attr[2].setBounds(200, 0, 90, 30);

		// 在界面中添加按钮区域、添加列表区域
		panel.add(panel_buttons);
		panel_buttons.setBounds(320, 5, 300, 50);
		
		panel_buttons = new JPanel();
		panel_buttons.setLayout(null);
		buttonArr_method = new JButton[3];
		//Font font = new Font("宋体", Font.BOLD, 13);
		for (int i = 0; i < 3; i++) { // 通过一个循环,对按钮数组中的每一个按钮实例化.
			buttonArr_method[i] = new JButton();
			panel_buttons.add(buttonArr_method[i]);
			buttonArr_method[i].addActionListener(this);
		}

		buttonArr_method[0].setText("增加方法");
		buttonArr_method[1].setText("修改方法");
		buttonArr_method[2].setText("删除方法");
		
		buttonArr_method[0].setBounds(0, 0, 90, 30);
		buttonArr_method[1].setBounds(100, 0, 90, 30);
		buttonArr_method[2].setBounds(200, 0, 90, 30);

		// 在界面中添加按钮区域、添加列表区域
		panel.add(panel_buttons);
		panel_buttons.setBounds(320, 280, 300, 50);
		
	}

	private void init_table() {

		table_obj = new JTable();
		// 设置列表为单选模式
		table_obj.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		Object[][] data_obj = new Object[0][0];
		
		
		String[] colNames_obj1 = { "object_id", "对象标识", "对象名称", "接口类" };
		
		if (OPERATETYPE.equals("IFC"))
			colNames_obj = Util698.setArrayData(colNames_ifclass);
		else
			colNames_obj = Util698.setArrayData(colNames_obj1);
		
		model_objList = new DefaultTableModel(data_obj, colNames_obj) {
			public boolean isCellEditable(int row, int column) {
				return false;// 返回true表示能编辑，false表示不能编辑
			}
		};
		
		// xuky 2016.09.03 添加排序
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(
				model_objList);
		table_obj.setRowSorter(sorter);
		
		
		// 表格放置在滚动面板中
		JScrollPane scroll_obj = new JScrollPane(table_obj);
		panel.add(scroll_obj);
		scroll_obj.setBounds(5, 43, WINDOWWIDTH - 500, WINDOWHEIGHT - 90);
		table_obj.setModel(model_objList);

		// 添加表格的选中事件监听器
		ListSelectionModel model;
		model = table_obj.getSelectionModel();
		model.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				// 刷新对象属性、方法
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
					// 按下了空格键，弹窗进行搜索定位信息录入
					final JFrame subWin = new JFrame();
					subWin.setTitle("录入数据，查找定位");
					subWin.setLayout(null);
					subWin.setSize(320, 150);
					
					String[] columns = { "对象标识","对象名称" };
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
					
					subWin.add(buttonOk);
					subWin.add(buttonCancle);
					buttonOk.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// 根据录入的信息，进行定位 
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
								if (!oiname.equals("") && table_obj.getValueAt(i, 2).toString().indexOf(oiname)>=0){
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
		
		table_attr = new JTable();
		table_attr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Object[][] data_attrVals = new Object[0][0];
		model_attrList = new DefaultTableModel(data_attrVals, colNames_attr) {
			public boolean isCellEditable(int row, int column) {
				return false;// 返回true表示能编辑，false表示不能编辑
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
				return false;// 返回true表示能编辑，false表示不能编辑
			}
		};
		table_method.setModel(model_methodList);
		JScrollPane scroll_method = new JScrollPane(table_method);
		panel.add(scroll_method);
		scroll_method.setBounds(320, 320, 450, 230);
		

		// 显示对象列表数据
		refresh_ObjList();
	}

	private void setObjsTableColumnWidth() {
		// 调整列宽度
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
			
			// xuky 2016.09.03 比较特殊的情况，对于类的属性，host_id与class_id相对应
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
			
			// 调整列宽度
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
				// 参考http://blog.csdn.net/dancen/article/details/7379847
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
			
			// xuky 2016.09.03 比较特殊的情况，对于类的属性，host_id与class_id相对应
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
			
			// 调整列宽度
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
				// 参考http://blog.csdn.net/dancen/article/details/7379847
				table_method.getSelectionModel().setSelectionInterval(i, i);
				Rectangle rect = table_method.getCellRect(i, 0, true);
				table_method.scrollRectToVisible(rect);
				break;
			}
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().indexOf("修改") >=0
				|| e.getActionCommand().indexOf("增加") >=0 ) {
			
			// xuky 弹出一个窗口进行数据修改
			final String aType = e.getActionCommand();
			
			if (aType.equals("修改对象")||aType.equals("修改接口类"))
				if (table_obj.getRowCount() <= 0)
					return;
			if (aType.equals("修改属性"))
				if (table_attr.getRowCount() <= 0)
					return;
			if (aType.equals("修改方法"))
				if (table_method.getRowCount() <= 0)
					return;

			final JFrame subWin = new JFrame();
			subWin.setTitle("增加数据");
			if (aType.indexOf("修改")>=0)
				subWin.setTitle("修改数据");
			subWin.setLayout(null);
			subWin.setSize(760, 320);

			//对象编辑界面-控件内容
			String[] ObjColumns = { "对象标识","对象名称","接口类" };
			String[] IFClassColumns = { "类标识","类名称" };
			//属性编辑界面-控件内容
			String[] AttrColumns = { "编号","属性名称","只读","数据类型" };
			//方法编辑界面-控件内容
			String[] MethodColumns = { "编号","方法名称","有参数","数据类型" };

			
			// 设计模式：分离变化部分
			// 1\4-指定的字段信息
			String[] columns = null;
			if (aType.indexOf("对象") >=0)
				columns = ObjColumns;
			
			if (aType.indexOf("接口类") >=0)
				columns = IFClassColumns;
			
			if (aType.indexOf("属性") >=0)		
				columns = AttrColumns;
			
			if (aType.indexOf("方法") >=0)		
				columns = MethodColumns;
			
			// 1\4-创建明细显示对象控件
			final InfoClass info_pop = new InfoClass(columns);
			
			// 2\4-指定数据转换接口实现（数据转换含义- 窗口控件数据转对象属性、对象属性转窗口控件数据）
			if (aType.indexOf("对象") >=0)		
				info_pop.setTrans(new TransImplObj());
			if (aType.indexOf("接口类") >=0)		
				info_pop.setTrans(new TransImplIFClass());
			if (aType.indexOf("属性") >=0)		
				info_pop.setTrans(new TransImplAttr());
			if (aType.indexOf("方法") >=0)		
				info_pop.setTrans(new TransImplMethod());
			
			
			subWin.add(info_pop.panel);
			info_pop.panel.setBounds(5, 5, 750, 500);

			if (aType.equals("修改对象") || aType.equals("修改接口类") ) {
				int id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 0);
				// 3\4-向界面放置数据
				info_pop.trans.setData(objList.getOne(id));
			}
			if (aType.equals("修改属性")) {
				int id = (int) table_attr.getValueAt(table_attr.getSelectedRow(), 0);
				// 3\4-向界面放置数据
				info_pop.trans.setData(attrList.getOne(id));
			}
			if (aType.equals("修改方法")) {
				int id = (int) table_method.getValueAt(table_method.getSelectedRow(), 0);
				// 3\4-向界面放置数据
				info_pop.trans.setData(methodList.getOne(id));
			}
			
			// 弹出窗口中 需要有确认和取消按钮
			JButton buttonOk = new JButton("确定");
			JButton buttonCancle = new JButton("取消");
			buttonOk.setBackground(new Color(0, 114, 198));
			buttonCancle.setBackground(new Color(0, 114, 198));
			buttonOk.setForeground(Color.white);
			buttonCancle.setForeground(Color.white);
			buttonOk.setBounds(250, 240, 100, 30);
			buttonCancle.setBounds(355, 240, 100, 30);
			subWin.add(buttonOk);
			subWin.add(buttonCancle);

			// xuky 2016.08.28 以下代码未生效
			// 弹窗中的控件获得焦点
			info_pop.setFocus();

			// 确认按钮添加事件
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 修改鼠标类型，无效
					setCursor(Cursor.WAIT_CURSOR);
					// http://blog.csdn.net/ethanq/article/details/7200490
					int id = 0;
					if (aType.indexOf("对象")>=0 || aType.indexOf("接口类")>=0){
						Object d;
						if (aType.indexOf("增加")>=0) {
							// 4\4-从界面得到数据
							d = info_pop.trans.getData();
							Map<String, Object> infoMap = Util698.getFirstFiledsInfo(d);
							id = objList.getUseableID();
							//String fieldName, Object o, Object val,Object valType
							Util698.setFieldValueByName(infoMap.get("name").toString(),d,id);
							//d.setObject_id(id);
							objList.add(d);
						} 
						else if (aType.indexOf("修改")>=0) {
							// 4\4-从界面得到数据
							d = info_pop.trans.getDataWithID();
							Map<String, Object> infoMap = Util698.getFirstFiledsInfo(d);
							objList.update(d);
							id = (int)Util698.getFieldValueByName(infoMap.get("name").toString(),d);
//							id = d.getObject_id();  // 在refresh_List(id)中定位用
						}
						refresh_ObjList(id);
					}
					if (aType.indexOf("属性")>=0){
						Attr d;
						if (aType.indexOf("增加")>=0) {
							// 4\4-从界面得到数据
							d = (Attr)info_pop.trans.getData();
							id = attrList.getUseableID();
							d.setAttr_id(id);
							// 需要设置host_id为对象的id
							int host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 0);
							// xuky 接口类使用class_id作为 
							if (OPERATETYPE.equals("IFC")){
								host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 1);
								// xuky 2016.09.13 Sort_id=0表示接口类属性
								d.setSort_id(0);
							}
							else
								// xuky 2016.09.13 Sort_id=1表示对象属性
								d.setSort_id(1);
							d.setHost_id(host_id);
							attrList.add(d);
						} 
						else if (aType.indexOf("修改")>=0) {
							// 4\4-从界面得到数据
							d = (Attr)info_pop.trans.getDataWithID();
							attrList.update(d);
							id = d.getAttr_id();  // 在refresh_List(id)中定位用
						}
						refresh_AttrList(id);
					}
					if (aType.indexOf("方法")>=0){
						Method d;
						if (aType.indexOf("增加")>=0) {
							// 4\4-从界面得到数据
							d = (Method)info_pop.trans.getData();
							id = methodList.getUseableID();
							d.setMethod_id(id);
							// 需要设置host_id为对象的id
							int host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 0);
							// xuky 接口类使用class_id作为 
							if (OPERATETYPE.equals("IFC")){
								host_id = (int) table_obj.getValueAt(table_obj.getSelectedRow(), 1);
								// xuky 2016.09.13 Sort_id=0表示接口类属性
								d.setSort_id(0);
							}
							else
								// xuky 2016.09.13 Sort_id=1表示对象属性
								d.setSort_id(1);
							d.setHost_id(host_id);
							methodList.add(d);
						} 
						else if (aType.indexOf("修改")>=0) {
							// 4\4-从界面得到数据
							d = (Method)info_pop.trans.getDataWithID();
							methodList.update(d);
							id = d.getMethod_id();  // 在refresh_List(id)中定位用
						}
						refresh_MethodList(id);
					}
					subWin.dispose();
					setCursor(Cursor.DEFAULT_CURSOR);
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
					if (aType.indexOf("属性")>=0||aType.indexOf("方法")>=0){
						// 属性、方法的编辑窗口中有选择数据类型的弹窗，所以这里就不允许控制锁定前台了
					}
					else
						e.getWindow().toFront();
				}
			});
		}
		
		if (e.getActionCommand().equals("删除对象") || e.getActionCommand().equals("删除接口类") ) {
			int rowcount = model_objList.getRowCount();
			if (rowcount > 0) {
				// xuky 删除当前行
				int rowCount = table_obj.getRowCount(); 
				int deleterow = table_obj.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,
						"是否确认删除\"" + table_obj.getValueAt(deleterow, 1)+"-"+table_obj.getValueAt(deleterow, 2) + "\"",
						"删除提示", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					objList.delete((int) table_obj.getValueAt(deleterow, 0));
					// 定位到删除行的前一条deleterow-1
					// 此时为删除了最后一行数据 或删除了第一行
					if (rowCount == 1 || deleterow==0)
						refresh_ObjList();
					else{
						int id = (int)table_obj.getValueAt(deleterow-1, 0);
						refresh_ObjList(id);
					}
				}
			}
		}
		
		if (e.getActionCommand().equals("删除属性")) {
			int rowcount = model_attrList.getRowCount();
			if (rowcount > 0) {
				// xuky 删除当前行
				int rowCount = table_attr.getRowCount(); 
				int deleterow = table_attr.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,						
						"是否确认删除\"" + table_attr.getValueAt(deleterow, 1)+"-"+table_attr.getValueAt(deleterow, 2) + "\"",
						"删除提示", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					//attrList.delete((int) table_attr.getValueAt(deleterow, 5),(int) table_attr.getValueAt(deleterow, 1));
					attrList.delete((int) table_attr.getValueAt(deleterow, 0));
					// 定位到删除行的前一条deleterow-1
					// 此时为删除了最后一行数据 或删除了第一行
					if (rowCount == 1 || deleterow==0)
						refresh_AttrList();
					else{
						int id = (int)table_attr.getValueAt(deleterow-1, 0);
						refresh_AttrList(id);
					}
				}
			}
		}
		if (e.getActionCommand().equals("删除方法")) {
			int rowcount = model_methodList.getRowCount();
			if (rowcount > 0) {
				// xuky 删除当前行
				int rowCount = table_method.getRowCount(); 
				int deleterow = table_method.getSelectedRow();
				if (JOptionPane.showConfirmDialog(null,						
						"是否确认删除\"" + table_method.getValueAt(deleterow, 1)+"-"+table_method.getValueAt(deleterow, 2) + "\"",
						"删除提示", JOptionPane.OK_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					//methodList.delete((int) table_method.getValueAt(deleterow, 5),(int) table_method.getValueAt(deleterow, 1));
					methodList.delete((int) table_method.getValueAt(deleterow, 0));
					// 定位到删除行的前一条deleterow-1
					// 此时为删除了最后一行数据 或删除了第一行
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
				// 参考http://blog.csdn.net/dancen/article/details/7379847
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
		frame.setTitle("操作对象方法");
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(WINDOWWIDTH, WINDOWHEIGHT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DebugSwing.center(frame);
	}


}
