package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.eastsoft.util.DataConvert;

import base.BaseFrame;
import util.SoftParameter;

/**
 * ������в�����������
 * 
 */
public class SoftParaManager extends BaseFrame implements ActionListener {

//	private JFrame frame;

	JButton[] buttonArr;
	JLabel[] labelArr;
	JTextField[] textFieldArr;
	JComboBox[] comboBoxArr;
	public JFrame subWin;
	private SoftParameter softParam;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("����")){
			saveData();
		}
		
//		if (e.getActionCommand().equals("�˳�")){
//			dispose();
//		}
//		
//		if (e.getActionCommand().equals("��������")){
//			saveData();
//			if (SoftParameter.getInstance().connneTerminal(SoftParaManager.class.getName()))
//				DebugSwing.showMsg("���ӳɹ�");
//		}		
		
	}

	private void saveData(){
		softParam.setPrefix_port(DataConvert.String2Int(textFieldArr[0].getText()));
		softParam.setSendTerminal(textFieldArr[1].getText());
		
		softParam.setIsProxyModel(textFieldArr[2].getText());
		softParam.setAll_timeout(DataConvert.String2Int(textFieldArr[3].getText()));
		softParam.setSingle_timeout(DataConvert.String2Int(textFieldArr[4].getText()));
		softParam.setTargets(textFieldArr[5].getText());
		
//		if (comboBoxArr[0].getSelectedIndex() == 0) 
//			softParam.setCOMM_TYPE("1");
//		else
//			softParam.setCOMM_TYPE("2");
//		
//		softParam.setCOMM_SOCKET_ADDR(textFieldArr[1].getText());
//		softParam.setCOMM_SOCKET_PORT(DataConvert.String2Int(textFieldArr[2].getText()));
//		softParam.setCOMM_COM_COM((String)comboBoxArr[3].getSelectedItem());
//		
//		softParam.setCOMM_COM_BAUDRATE(DataConvert.String2Int(textFieldArr[4].getText()));
//		softParam.setCOMM_COM_DATABIT(DataConvert.String2Int(textFieldArr[5].getText()));
//		softParam.setCOMM_COM_STOPBIT(DataConvert.String2Int(textFieldArr[6].getText()));
//		
//		
//		if (comboBoxArr[7].getSelectedIndex() == 0)
//				softParam.setCOMM_COM_PARITY(SerialPort.PARITY_NONE);
//		if (comboBoxArr[7].getSelectedIndex() == 1)
//			softParam.setCOMM_COM_PARITY(SerialPort.PARITY_ODD);
//		if (comboBoxArr[7].getSelectedIndex() == 2)
//			softParam.setCOMM_COM_PARITY(SerialPort.PARITY_EVEN);
//		
//		softParam.setCOMM_COM_RECEIVETIMEOUT(DataConvert.String2Int(textFieldArr[8].getText()));
//		
//		softParam.setPW_3761(textFieldArr[9].getText());
//		softParam.setPW_2005(textFieldArr[10].getText());
//		softParam.setPW_GD_LOWLEVEL(textFieldArr[11].getText());
//		softParam.setPW_GD_HIGHTLEVEL(textFieldArr[12].getText());
//		
//		softParam.setCOMM_SOCKET_UDPPORT(DataConvert.String2Int(textFieldArr[13].getText()));
//		
//		softParam.setPW_EASTSOFT(textFieldArr[14].getText());
//
		softParam.saveParam();
	};

	
	private void showData(){
		softParam = SoftParameter.getInstance();
		String str = DataConvert.int2String(softParam.getPrefix_port());
		textFieldArr[0].setText(str);
		textFieldArr[1].setText(softParam.getSendTerminal());
		
		textFieldArr[2].setText(softParam.getIsProxyModel());
		
		textFieldArr[3].setText(DataConvert.int2String(softParam.getAll_timeout()));
		textFieldArr[4].setText(DataConvert.int2String(softParam.getSingle_timeout()));
		textFieldArr[5].setText(softParam.getTargets());
		
//		if (softParam.getCOMM_TYPE().equals("1"))
//			comboBoxArr[0].setSelectedIndex(0);
//		else 
//			comboBoxArr[0].setSelectedIndex(1);
//		
//		comboBoxArr[3].setSelectedItem(softParam.getCOMM_COM_COM());
//		
//		textFieldArr[4].setText(DataConvert.int2String(softParam.getCOMM_COM_BAUDRATE()));
//		textFieldArr[5].setText(DataConvert.int2String(softParam.getCOMM_COM_DATABIT()));
//		textFieldArr[6].setText(DataConvert.int2String(softParam.getCOMM_COM_STOPBIT()));
//		
//		if (softParam.getCOMM_COM_PARITY()==SerialPort.PARITY_NONE)
//			comboBoxArr[7].setSelectedIndex(0);
//		if (softParam.getCOMM_COM_PARITY()==SerialPort.PARITY_ODD)
//			comboBoxArr[7].setSelectedIndex(1);
//		if (softParam.getCOMM_COM_PARITY()==SerialPort.PARITY_EVEN)
//			comboBoxArr[7].setSelectedIndex(2);
//		
//		textFieldArr[8].setText(DataConvert.int2String(softParam.getCOMM_COM_RECEIVETIMEOUT()));
//		
//		textFieldArr[9].setText(softParam.getPW_3761());
//		
//		textFieldArr[10].setText(softParam.getPW_2005());
//		textFieldArr[11].setText(softParam.getPW_GD_LOWLEVEL());
//		textFieldArr[12].setText(softParam.getPW_GD_HIGHTLEVEL());
//		textFieldArr[13].setText(DataConvert.int2String(softParam.getCOMM_SOCKET_UDPPORT()));
//		textFieldArr[14].setText(softParam.getPW_EASTSOFT());
		
	};

	@Override
	protected void init() {

//		frame = new JFrame("�����������");
//		frame.setLayout(null);
//		frame.setSize(800, 600);
//		DebugSwing.center(frame);
		int aNum = 20;

		buttonArr = new JButton[aNum];
		Font font = new Font("����", Font.BOLD, 13);
		for (int i = 0; i < aNum; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			buttonArr[i] = new JButton();
			buttonArr[i].setForeground(Color.white);
			buttonArr[i].setBackground(new Color(0, 114, 198));
			buttonArr[i].setFont(font);
			panel.add(buttonArr[i]);
			buttonArr[i].addActionListener(this);
		}
		
		labelArr = new JLabel[aNum];
		for (int i = 0; i < aNum; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			labelArr[i] = new JLabel();
			labelArr[i].setForeground(new Color(0, 114, 198));
			labelArr[i].setBackground(Color.white);
			labelArr[i].setFont(font);
			panel.add(labelArr[i]);
		}
		
		textFieldArr = new JTextField[aNum];
		for (int i = 0; i < aNum; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			textFieldArr[i] = new JTextField();
			textFieldArr[i].setForeground(new Color(0, 114, 198));
			textFieldArr[i].setBackground(Color.white);
			textFieldArr[i].setFont(font);
			if (i < 9) {
				panel.add(textFieldArr[i]);
			}
			
		}
		
		comboBoxArr = new JComboBox[aNum];
		for (int i = 0; i < aNum; i++) { // ͨ��һ��ѭ��,�԰�ť�����е�ÿһ����ťʵ����.
			comboBoxArr[i] = new JComboBox();
			comboBoxArr[i].setForeground(new Color(0, 114, 198));
			comboBoxArr[i].setBackground(Color.white);
			comboBoxArr[i].setFont(font);
			panel.add(comboBoxArr[i]);
		}
		
		buttonArr[0].setBounds(15, 15, 150, 30);
//		buttonArr[1].setBounds(170, 15, 150, 30);
//		buttonArr[2].setBounds(450, 70, 150, 30);
		
		buttonArr[0].setText("����");
//		buttonArr[1].setText("�˳�");
//		buttonArr[2].setText("��������");


		int HEIGHT = 27;
		int INTERVAL = 8;
		int txt_WEIGHT = 270;
		int input_WEIGHT = 300;
		int TOPX = 15;
		int TOPY = 70;

		int x = TOPX;
		int y = TOPY;
		
		int aMax = 6;
		for (int i = 0;i<aMax;i++){
			
			labelArr[i].setBounds(x, y, txt_WEIGHT, HEIGHT);
//			if ((i == 0) || (i == 3)|| (i == 7)) {
//				Vector<String> v = new Vector<String>();
//				if (i==0){
//					v.addElement("����RJ45");
//					v.addElement("����Serial");					
//				}
//				if (i==3){
//					for(int j = 1 ;j<=15;j++)
//						v.addElement("COM"+DataConvert.int2String(j));
//				}
//				if (i==7){
//					v.addElement("�� None");
//					v.addElement("��У�� Odd");					
//					v.addElement("żУ�� Even");					
//				}
//				comboBoxArr[i].setModel(new DefaultComboBoxModel(v));
//				comboBoxArr[i].setBounds(x+txt_WEIGHT+INTERVAL, y, input_WEIGHT, HEIGHT);				
//			} 
//			else
//			{
//				if (i==9)
//					textFieldArr[i].setBounds(x+txt_WEIGHT+INTERVAL, y, input_WEIGHT+100, HEIGHT);
//				else
//				textFieldArr[i].setBounds(x+txt_WEIGHT+INTERVAL, y, input_WEIGHT, HEIGHT);
//			}
//			
			textFieldArr[i].setBounds(x+txt_WEIGHT+INTERVAL, y, input_WEIGHT, HEIGHT);

			y = y + HEIGHT + INTERVAL;
			
		}

		labelArr[0].setText("ǰ�û������˿�");
		labelArr[1].setText("ͨ���ն˵�ַ");
		labelArr[2].setText("����ģʽ(0Ϊ����ģʽ��1Ϊ����ģʽ)");
		labelArr[3].setText("������������ʱʱ��(��λ����)");
		labelArr[4].setText("������������ʱʱ��(��λ����)");
		labelArr[5].setText("Ŀ���������ַ(���ʱ��ʹ��','�ָ�)");
		showData();
		
		panel.setVisible(true);

	}

	public static void main(String[] args) {
		
	}


}
