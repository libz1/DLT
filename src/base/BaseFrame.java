package base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.eastsoft.util.DebugSwing;


//����ͳһ����
public abstract class BaseFrame {
	
	public JFrame frame;
	public JPanel panel;
	protected String OPERATETYPE = "";
	
	public BaseFrame(){
		init0();
	}
	
	public void setCursor(int cursorType){
		frame.setCursor(cursorType);
	}
	
	public BaseFrame(String type){
		OPERATETYPE = type;
		init0();
	}

	// ���ȵĳ�ʼ������
	private void init0() {
		
		// ����panelΪ�����洰�ڴ�С����
		frame = new JFrame();
		
		Container c = frame.getContentPane();
		
		c.setLayout(new BorderLayout());
		
		panel = new JPanel(null);
		panel.setBackground(Color.white);
		panel.setVisible(true);
		// BorderLayout.CENTER�����Ŵ��ڴ�С���仯��
		c.add(panel, BorderLayout.CENTER);
		
		// �ں����showFrame�����У�����ʵ�ִ��ڵ��������

		// ģ��ģʽ�������������д�㷨ִ�й��̣�����init�ǳ���������Ҫ�������о���ʵ�֣�initComponent�ǹ������ݵĸ���ʵ�ֵľ��庯��
		try{
			init();
			initComponent(panel);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	// 1������������Ҫ�������о���ʵ�֣�������ĸ��־��������ֵ��
	protected abstract void init();

	// 2���ݹ鷽ʽ�����������еĿؼ�������������
	protected void initComponent(JComponent jComponent) {
		Component[] c = jComponent.getComponents();
		String class_name = "";
		for (Component co : c) {
			class_name = co.getClass().toString();
			//System.out.println("class_name=>"+class_name);
			if (class_name.indexOf("JButton") >= 0) {
				//��ťΪ���װ���
				co.setBackground(new Color(0, 114, 198));
				co.setForeground(Color.white);
			}
			if (class_name.indexOf("JLabel") >= 0
					|| class_name.indexOf("JTextField") >= 0
					|| class_name.indexOf("JComboBox") >= 0
					|| class_name.indexOf("JTable") >= 0
					|| class_name.indexOf("JTextArea") >= 0) {
				//����
				co.setForeground(new Color(0, 114, 198));
			}
			if (class_name.indexOf("JTable") >= 0) {
				// �����б�ı�ͷ����������ɫ
				((JTable)co).getTableHeader().setForeground(new Color(0, 114, 198));
			}
			
			if (class_name.indexOf("JScrollPane") >= 0
					|| class_name.indexOf("JPanel") >= 0
					|| class_name.indexOf("JTable") >= 0
					|| class_name.indexOf("JViewport") >= 0) {
				// �ݹ鷽ʽ�����ӿؼ���������
				initComponent((JComponent) co);
			}
			//System.out.println(class_name);
		}
	}

	public JPanel getPanel() {
		return panel;
	}
	
	public void showFrame(String title) {
		showFrame(title,0,0,0,0);
	}
	
	public void showFrame(String title,int x,int y,int input_width,int input_height) {
		int width = 800, height = 600; 
		if (frame != null)
			frame.dispose();
		
//		// ���ý�������Panel��λ�õ���Ϣ
//		getPanel().setBounds(0, 0, width, height);
		
		frame.setTitle(title);
//		frame.setLayout(null);
//		frame.add(getPanel());
		
		// xuky �Ӵ��ڹرյ�ʱ�򣬲��ر������ڣ������˳��󲻻����javaw����
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		if (x == 0){
			frame.setSize(width, height);
			DebugSwing.center(frame);
		}
		else{
//			frame.setSize(input_width, input_height);
			frame.setBounds(x, y, input_width, input_height);
		}
//		frame.pack();
		frame.setVisible(true);

	}
	
}
