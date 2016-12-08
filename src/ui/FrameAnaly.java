package ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import socket.SocketServer;
import util.Publisher;
import util.SoftParameter;
import util.Util698;
import base.BaseFrame;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DebugSwing;

import frame.Frame698;

/**
 * ������֯����������
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */
public class FrameAnaly extends BaseFrame implements Observer{

	private JButton btn_buildFrame, btn_analyFrame, btn_sendFrame, btn_analyRecvFrame;
	private JTextField txt_frame,txt_recvframe, txt_APDU, txt_terminalAddr; 
	private JTextArea txt_analy, txt_analyAPDU;
	private JLabel lb_frame, lb_recvframe, lb_analy, lb_terminalAddr, lb_APDU, lb_analyAPDU, lb_control;
	private JComboBox cb_dir, cb_prm, cb_fun;
	
	//private JScrollPane scroll_analy,scroll_analyAPDU;
	//private JCheckBox checkbox;
	
	private volatile static FrameAnaly uniqueInstance;
	public static FrameAnaly getInstance() {
		if (uniqueInstance == null) {
			synchronized (FrameAnaly.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new FrameAnaly();
				}
			}
		}
		return uniqueInstance;
	}

	/**
	 * ���캯��
	 */
	public FrameAnaly() {
		
		Publisher.getInstance().addObserver(this);

		btn_buildFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s_terminalAddr,s_APDU;
				s_terminalAddr = txt_terminalAddr.getText();
				s_APDU = txt_APDU.getText();
				
				// ��֯���Ĺ���
				Frame698 frame698 = new Frame698();
				// 1�������ն˵�ַ
				frame698.getFrameAddr().setSAData(s_terminalAddr);
				// 2������APDU��Ϣ
				frame698.getAPDU().init(s_APDU);
				// 3������ͨ����ز���  ��Frame698.FrameContro�Ĺ��캯���У��Ѿ�����ΪĬ�ϲ�����
				String str = (String)cb_dir.getSelectedItem();  // Ĭ��Ϊ0 
				frame698.getFrameControl().setDIRFlag(str.split(":")[0]);
				str = (String)cb_prm.getSelectedItem();  // Ĭ��Ϊ1  
				frame698.getFrameControl().setPRMFlag(str.split(":")[0]);
				str = (String)cb_fun.getSelectedItem(); // Ĭ��Ϊ3
				frame698.getFrameControl().setFunData(DataConvert.String2Int(str.split(":")[0]));
				// 4���õ����屨������
				String s = frame698.getFrame();
				txt_frame.setText(s);

				// �Զ����б��ļ�¼
		    	SoftParameter.getInstance().setSendFrame(txt_frame.getText());
		    	SoftParameter.getInstance().saveParam();
				
				// 5�����ͱ��ĵ���Ӧ�˿�
				//SocketServer.sendData(s_frame);
				
				// ������󣨴˵�¼���ڣ����Է����˱仯������֤ͨ����������Ҫ֪ͨ�۲��ߣ������ڣ����ڹ۲��߶���������ش������
				//setChanged();
				//notifyObservers(frame);
			}
		});
		
		btn_analyFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analyFrame(txt_frame);
			}
		});
		btn_analyRecvFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analyFrame(txt_recvframe);
			}
		});
		
		btn_sendFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s_frame;
				s_frame = txt_frame.getText();
				if (s_frame==null || s_frame.equals("")){
					
				} 
				else
					// 	ֱ�ӷ��ͼ��ɣ���sendData�н�����·ѡ��
					SocketServer.sendData(s_frame);
			}
		});
		
	}
	
	private void analyFrame(JTextField txt_framedata){
		String s_frame;
		s_frame = txt_framedata.getText();
		if (s_frame.equals(""))
			return;
		Frame698 frame698 = new Frame698(s_frame);
		String[] s = frame698.getTree();
		txt_analy.setText(s[0]);
		txt_analyAPDU.setText(s[1]);
		
		// ��궨λ����λ
		txt_analy.setSelectionStart(1);
		txt_analy.setSelectionEnd(1); 
		
		// ��궨λ����λ
		txt_analyAPDU.setSelectionStart(1);
		txt_analyAPDU.setSelectionEnd(1); 
		// ������󣨴˵�¼���ڣ����Է����˱仯������֤ͨ����������Ҫ֪ͨ�۲��ߣ������ڣ����ڹ۲��߶���������ش������
		//setChanged();
		//notifyObservers(frame);
	}

	/**
	 * �����ʼ��
	 */
	@Override
	protected void init() {
		
		btn_buildFrame = new JButton("��֯����");
		btn_analyFrame = new JButton("�������ͱ���");
		
		btn_analyRecvFrame = new JButton("�������ձ���");
		
		btn_sendFrame = new JButton("���ͱ���");
		
		lb_frame = new JLabel("���ͱ���", JLabel.CENTER);
		
		lb_recvframe = new JLabel("���ձ���", JLabel.CENTER);
		
		lb_analy = new JLabel("ҵ������", JLabel.CENTER);
		lb_terminalAddr = new JLabel("�ն˵�ַ", JLabel.CENTER);
		lb_APDU = new JLabel("APDU", JLabel.CENTER);
		lb_analyAPDU = new JLabel("���Ľ���", JLabel.CENTER);

		lb_control = new JLabel("��������Ϣ", JLabel.CENTER);
		
		Vector v = null;
		v = new Vector<String>();
		v.addElement("0:�ͻ�������");
		v.addElement("1:����������");
		cb_dir = new JComboBox(v);
		
		v = new Vector<String>();
		v.addElement("1:�ͻ�������");
		v.addElement("0:����������");
		cb_prm = new JComboBox(v);

		v = new Vector<String>();
		v.addElement("3:�û�����");
		v.addElement("1:��·����");
		cb_fun = new JComboBox(v);
		
		txt_frame = new JTextField(5000);
		txt_recvframe = new JTextField(5000);
		txt_APDU = new JTextField(5000);
		txt_terminalAddr = new JTextField(50);
		
		txt_analy = new JTextArea();
		txt_analyAPDU = new JTextArea();
		
		String terminalAddr = SoftParameter.getInstance().getSendTerminal();
		txt_terminalAddr.setText(terminalAddr);
		
		String sendFrame = SoftParameter.getInstance().getSendFrame();
		txt_frame.setText(sendFrame);
		
		String recvFrame = SoftParameter.getInstance().getRecvFrame();
		txt_recvframe.setText(recvFrame);
		
		String APDUData = SoftParameter.getInstance().getAPDUData();
		txt_APDU.setText(APDUData);
		
		//txt_analy.setLineWrap(true);// �����Զ����й���
		//txt_analyAPDU.setWrapStyleWord(true);// ������в����ֹ���
		
		//txt_analy.setLineWrap(true);// �����Զ����й���
		//txt_analyAPDU.setWrapStyleWord(true);// ������в����ֹ���

		
//		frame_data.addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent evt) {
//				jTextField1KeyPressed(evt);
//			}
//		});
		//checkbox = new JCheckBox("�����¼��Ϣ", true);
		

		panel.add(btn_buildFrame);
		panel.add(btn_analyFrame);
		panel.add(btn_sendFrame);
		panel.add(btn_analyRecvFrame);
		
		
		
		panel.add(lb_frame);
		panel.add(lb_recvframe);
		panel.add(lb_analy);
		panel.add(lb_terminalAddr);
		panel.add(lb_APDU);
		panel.add(lb_analyAPDU);
		
		panel.add(txt_frame);
		panel.add(txt_recvframe);
		panel.add(txt_analy);
		panel.add(txt_terminalAddr);
		panel.add(txt_APDU);
		panel.add(txt_analyAPDU);
		
		panel.add(cb_dir);
		panel.add(cb_prm);
		panel.add(cb_fun);
		panel.add(lb_control);
		
		
		int height = 30, lb_width = 80, inter_val = 5 ;
		int topX = 5, topY = 5;
		int x = topX ;
		int y = topY;
		int bt_width = 120;
		
		lb_control.setBounds(x, y, lb_width, height);
		cb_dir.setBounds(x+lb_width, y, lb_width+30, height);
		x = x + lb_width+120;
		cb_prm.setBounds(x, y, lb_width+30, height);
		x = x + lb_width+40;
		cb_fun.setBounds(x, y, lb_width+30, height);
		
		x = topX ;
		y =  y + height + inter_val;
		lb_terminalAddr.setBounds(x, y, lb_width, height);
		txt_terminalAddr.setBounds(x + lb_width, y, 480, height);
		y =  y + height + inter_val;
		lb_APDU.setBounds(x, y, lb_width, height);
		txt_APDU.setBounds(x + lb_width, y, 480, height);
		
		x = x + lb_width + 490 ;
		btn_buildFrame.setBounds(x, y, 190, height);
		
		x = topX;
		y =  y + height + inter_val;
		lb_frame.setBounds(x, y, lb_width, height);
		txt_frame.setBounds(x + lb_width, y, 680, height);

		y =  y + height + inter_val;
		lb_recvframe.setBounds(x, y, lb_width, height);
		txt_recvframe.setBounds(x + lb_width, y, 680, height);
		
		y =  y + height + inter_val;
		btn_analyFrame.setBounds(x , y, bt_width, height);
		
		x = x + bt_width + 10;
		btn_analyRecvFrame.setBounds(x, y, bt_width, height);
		
		x = x + bt_width + 10;
		btn_sendFrame.setBounds(x, y, bt_width, height);
		
		x = topX;
		y =  y + height + inter_val;
		JScrollPane scroll_analy = new JScrollPane(txt_analy);
		JScrollPane scroll_analyAPDU = new JScrollPane(txt_analyAPDU);

		
		lb_analy.setBounds(x, y, lb_width, height);
		//txt_analy.setBounds(x + lb_width, y, 480, height);
		
		scroll_analyAPDU.setBounds(x + lb_width, y, 680, height+140);
		
		panel.add(scroll_analy);
		
		y =  y + height + inter_val +140 ;
		lb_analyAPDU.setBounds(x, y, lb_width, height);
		
		scroll_analy.setBounds(x + lb_width, y, 680, height+140);
		panel.add(scroll_analyAPDU);
		
		//txt_analyAPDU.setBounds(x + lb_width, y, 480, height);
		//txt_terminalAddr.setText("000000000002");
//		txt_APDU.setText("05 01 00 40 01 02 00 00");
		//checkbox.setBounds(50, 200, 120, 30);
		
		txt_terminalAddr.addFocusListener(new FocusAdapter(){
		    @Override public void focusLost(FocusEvent e){
		        // ʧȥ����ʱ��������Ϊ������� ��¼�����Ϣ���б���
		    	SoftParameter.getInstance().setSendTerminal(txt_terminalAddr.getText());
		    	SoftParameter.getInstance().saveParam();
		    }
		});
		
		txt_frame.addFocusListener(new FocusAdapter(){
		    @Override public void focusLost(FocusEvent e){
		        // ʧȥ����ʱ��������Ϊ������� ��¼�����Ϣ���б���
		    	SoftParameter.getInstance().setSendFrame(txt_frame.getText());
		    	SoftParameter.getInstance().saveParam();
		    }
		});
		txt_recvframe.addFocusListener(new FocusAdapter(){
		    @Override public void focusLost(FocusEvent e){
		        // ʧȥ����ʱ��������Ϊ������� ��¼�����Ϣ���б���
		    	SoftParameter.getInstance().setRecvFrame(txt_recvframe.getText());
		    	SoftParameter.getInstance().saveParam();
		    }
		});
		txt_APDU.addFocusListener(new FocusAdapter(){
		    @Override public void focusLost(FocusEvent e){
		        // ʧȥ����ʱ��������Ϊ������� ��¼�����Ϣ���б���
		    	SoftParameter.getInstance().setAPDUData(txt_APDU.getText());
		    	SoftParameter.getInstance().saveParam();
		    }
		});
		
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
	
	private void jTextField1KeyPressed(KeyEvent evt) {
		if(evt.getKeyCode()==KeyEvent.VK_ENTER)
		{
//			JOptionPane.showMessageDialog(JTextFieldListener.this, "����˻س�"+jTextField1.getText(), "���������Ϣ", 1);
//			DebugSwing.showMsg("���˻س�");
			// ����س��Ժ󣬽������ƶ��������ֶ�
			
		}
	}
	
	public static void main(String[] args) {
		
		int width = 800, height = 600;
		FrameAnaly mainFrame = new FrameAnaly();
		mainFrame.getPanel().setBounds(0, 0, width, height);
		
		JFrame frame = new JFrame();
		frame.setTitle("698.45Э��Ӧ�����");
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DebugSwing.center(frame);

	}

	@Override
	public void update(Observable o, Object arg) {
		String[] s = (String[]) arg;
		String type = ""; 
		if (s[0].equals("recv frame") &&  s[1].equals("user data")){
			showData(arg);
		}
	}
	
	private synchronized void showData(Object arg){
		String[] s = (String[]) arg;
		String  frame = s[2];
		frame = frame.replaceAll(" ","");
		frame = frame.replaceAll(",","");
		frame = Util698.seprateString(frame, " ");
		txt_recvframe.setText(frame);
		
	}


}
