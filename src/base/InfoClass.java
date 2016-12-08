package base;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DebugSwing;

import entity.Constant;
import ui.DataTypeUserCRUD;
import util.Util698;


// ����ͳһ����
// ��ϸ���ڣ����˵�����֡����¼��� ������¼���Ϊֻ�����ԣ����Ӱ�ť���е���ѡ��
public class InfoClass {
	
	// ����ת��������ȡΪ�ӿ�
	public TransBehavior trans;
	public static JPanel panel;
	protected static int ID; // xuky �洢ԭ�ȵ�ID��Ϣ���ڱ������ݵ�ʱ����Ҫʹ��
	
	protected JLabel[] labelArr;
	public JTextField[] textFieldArr;
	protected JTextArea[] textAreaArr;
	public JComponent[] component;
	
	private JTextField selectData;

	// ÿ����ʾ�ֶ�����
	private int COLUMNNUM = 2;
	
	public InfoClass(String[] ObjColumns){
		init(ObjColumns,0,0);
	}
	
	public InfoClass(String[] ObjColumns, int txtWidth,int inputWidth){
		init(ObjColumns,txtWidth,inputWidth);
	}
	
	// �û����������趨ÿ����ʾ��������Ĭ��Ϊ���У���ÿ����ʾ�����ֶε����ݣ���ĳЩ�������Ҫÿ��ֻ��ʾһ���ֶε�����
	public InfoClass(String[] ObjColumns,int colNum){
		// һ�е�����
		COLUMNNUM = colNum;
		init(ObjColumns,0,0);
	}
	
	public void setTrans(TransBehavior transBehavior) {
		this.trans = transBehavior;
		// ע�����´��룬���� this.trans
		this.trans.setComponent(component);
	}
	
	public void setData(Object object) {
		trans.setData(object);
	}
	
	public void clearComponent() {
		trans.clearComponent();
	}
	
	public void setBounds(int x,int y,int width,int height){
		panel.setBounds(x, y, width,height);
	}
	
	// ������Σ������ֶ���Ϣ������֯������ʾ���ݣ� ������ʾ��Ϣ+�ؼ�����д�ı���ֻ���ı���+����ѡ��ť��
	// ObjColumns �����ⶨ��
	//colNames_info = { "code", "��code", "˳���", "��������", "��������;noButton", "Ĭ��ֵ",
	//		"���ݶ���;TextArea", "��ע" }
	//  ��������;noButton  ��ʾ  �����������ֶΣ����ǲ���Ҫѡ��ť
	// ���ݶ���;TextArea ��ʾ ���ݶ����ֶ���һ��TextArea���͵����ݣ���ؼ����������ͣ�����ʾ������������루�����ˣ�û������
	
	protected void init(String[] ObjColumns,int txtWidth,int inputWidth) {
		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(null);
		panel.setBackground(Color.white);
		panel.setVisible(true);

		labelArr = new JLabel[15];
		textFieldArr = new JTextField[15];
		textAreaArr = new JTextArea[15];
		component = new JComponent[15];

		Font font = new Font("����", Font.BOLD, 14);
		for (int i = 0; i < 15; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			labelArr[i] = new JLabel();
			labelArr[i].setForeground(new Color(0, 114, 198));
			labelArr[i].setFont(font);
			panel.add(labelArr[i]);

			if ((i == 400)) {
				// ����һ��Vector����
				Vector<String> v = null;
				if (i == 400) {
					v = new Vector<String>();
					v.addElement("1");
					v.addElement("2");
				}
				component[i] = new JComboBox(v);
			} else {
				String colName = "";
				if (i < ObjColumns.length)
					colName = ObjColumns[i];
				
				
				if (colName.indexOf("TextArea") >= 0){
					textAreaArr[i] = new JTextArea();
					component[i] = textAreaArr[i];
					textAreaArr[i].setLineWrap(true);// �����Զ����й���
					textAreaArr[i].setWrapStyleWord(true);// ������в����ֹ���
				}
				else if (colName.indexOf(";code:") >= 0){
					// ������;code:PortRateType
					Vector<String> v = new Vector<String>();
					String name = colName.split(":")[1];
					String[] array = (String[]) Util698.getFieldValueByName(name, new Constant());
					for( String s: array )
						v.addElement(s);
					component[i] = new JComboBox<String>(v);
				} 
				else{
					textFieldArr[i] = new JTextField();
					component[i] = textFieldArr[i];
				}
				
			}
			component[i].setForeground(new Color(0, 114, 198));
			component[i].setBackground(Color.white);
			component[i].setFont(font);
			panel.add(component[i]);
		}
	
	
		int manNum = ObjColumns.length;
		
		String name = "";
		for(int i=0;i<manNum;i++){
			name = ObjColumns[i];
			if (name.indexOf(";") >= 0)
				name = name.split(";")[0];
			labelArr[i].setText(name);
		}

		// xuky
		// �����ֶ����� �Զ��õ����� ���֣�¼��� �������б������͵�
		// ok ���Ƿ�Ϊ���н�����ʾ ����Ϊ������ ���ϵ���
		// ok ÿ��˵�����ֺ���ʾ���ֶ��ǹ̶���� ���������
		// ������������ ��ν������� ������ι���
		// err���ݲ�֧�֣��������������������ʾ�ģ�����������Ϊֻ��״̬

		// ѭ���Ĵ��� ������2����ȡ��
		int iNum = manNum / COLUMNNUM;
		if (iNum * COLUMNNUM < manNum) {
			iNum++;
		}
		int HEIGHT = 30; // ���ָ߶�
		int INTERVALX = 10; // �м���
		int INTERVALY = 3; // �м���
		int WEIGHTLABEL = 70; // ˵��������ʾ���
		int WEIGHTTEXT = 220; // �ı������ʾ���
		int TOPX = 5, TOPY = 5; // ��ʼ����
		int x = TOPX, y = TOPY; // ���ƿؼ�������
		int f = 1; // �ֶ����
		
		
		if (txtWidth != 0)
			WEIGHTLABEL = txtWidth; 
		if (inputWidth != 0)
			WEIGHTTEXT = inputWidth; 
		
		if (COLUMNNUM == 3){
			INTERVALX = 5;
			WEIGHTLABEL = 70;
			WEIGHTTEXT = 175;
		}
		
		for (int i = 1; i <= iNum; i++)// ����ѭ��
		{
			// ��X����ظ�����ʼλ��
			x = TOPX;
			for (int j = 1; j <= COLUMNNUM; j++) {
				// ÿ���������У�����ѭ��
				if (f <= manNum) // ѭ���ܴ���ҪС���ܵ��ֶ�����
				{
					// ����˵�����ֵ�λ��
					labelArr[f - 1].setBounds(x, y, WEIGHTLABEL, HEIGHT);
					// �����ı����λ��
					
					String colName = "";
					if (f-1 < ObjColumns.length)
						colName = ObjColumns[f-1];
					
					
					//if (f == 7){
					if (colName.indexOf("TextArea") >= 0){
						// ��7�бȽ����⣬��һ�������ı���
						JScrollPane scroll = new JScrollPane(component[f - 1]);
						scroll.setBounds(x + WEIGHTLABEL, y, WEIGHTTEXT,HEIGHT*4);
						panel.add(scroll);
						component[f - 1].setBounds(0, 0, WEIGHTTEXT,HEIGHT*4);
					}
					else
						component[f - 1].setBounds(x + WEIGHTLABEL, y, WEIGHTTEXT,HEIGHT);
					
					if (ObjColumns[f-1].equals("��������")){
						// ���һ����ť��
						// ���һ��ר������ʾ���ı�����ʾ��ֵ��Ϣ ����ڿؼ���setToolTipText������
						//	������Ϣ��Ȼ����component[f - 1]�й���ȡʹ��
						//((JTextField)component[f - 1]).setToolTipText("");
						selectData = (JTextField) component[f - 1];
						selectData.setEditable(false); // ������ֱ��¼������
						JButton btn_select = new JButton("ѡ��");
						btn_select.setBounds(x + WEIGHTLABEL+WEIGHTTEXT+2, y, 80,HEIGHT);
						panel.add(btn_select);
						btn_select.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								popWin();
							}
						});
					}
					// ѭ����������
					f++;
				}
				// ÿ��ѭ���Ժ��޸�X����
				x = x + WEIGHTLABEL + WEIGHTTEXT + INTERVALX * 2;
			}
			// ÿ��ѭ���Ժ��޸�y����
			y = y + HEIGHT + INTERVALY;
		}
		
		textFieldArr[0].requestFocus();
	}
	
	public void setFocus(){
	}
	
	private void popWin() {

		// xuky ����һ�����ڽ��������޸�

		final JFrame subWin = new JFrame();
		subWin.setLayout(null);
		int WINDOWWIDTH = 800,WINDOWHEIGHT=600;
		subWin.setSize(WINDOWWIDTH, WINDOWHEIGHT);
		
		
		// ����������������ѡ��
		final DataTypeUserCRUD chooseDataType = new DataTypeUserCRUD();
		
		
		chooseDataType.DisableEdit();
		
		if (selectData.getToolTipText()==null || selectData.getToolTipText().equals("")){
			// do nothing 
		}  
		else
			// ѡ�ж�Ӧ��
			chooseDataType.selectRow(DataConvert.String2Int(selectData.getToolTipText()));
		
		subWin.add(chooseDataType.panel);
		chooseDataType.panel.setBounds(5, 5, WINDOWWIDTH - 20,
				WINDOWHEIGHT - 20);
		
		// ����ѡ��ť��ִ���¼�����
		chooseDataType.buttonArr[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = chooseDataType.table.getSelectedRow();
				if (row >= 0) {
					//System.out.println("row=>" + row);
					String datatype = (String) chooseDataType.table.getValueAt(row, 4);
					String t = selectData.getText();
					t = t + " ";
					selectData.setText(datatype);
					
					String code = DataConvert.int2String((int) chooseDataType.table.getValueAt(row, 1));
					selectData.setToolTipText(code);
				}
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
