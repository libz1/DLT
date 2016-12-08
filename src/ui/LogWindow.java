package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import util.Publisher;
import util.Util698;
import base.BaseFrame;

import com.eastsoft.util.*;

import frame.Frame698;

/**
 * ��־��Ϣ��ʾ������
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */
public class LogWindow extends BaseFrame implements Observer {
	private JTextArea txt_frame;
	private JLabel lb_analy;
	public JLabel lb_status;

	private volatile static LogWindow uniqueInstance;

	public static LogWindow getInstance() {
		if (uniqueInstance == null) {
			synchronized (LogWindow.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new LogWindow();
					Publisher.getInstance().addObserver(uniqueInstance);
				}
			}
		}
		return uniqueInstance;
	}

	@Override
	public void update(Observable o, Object arg) {

		String[] s = (String[]) arg;
		String type = "";
		if (s[0].equals("recv frame") || s[0].equals("send frame")) {
			showData(arg);
		}
	}

	private synchronized void showData(Object arg) {
		String[] s = (String[]) arg;
		String type = "";
		if (s[0].equals("recv frame"))
			type = "�� ";
		if (s[0].equals("send frame"))
			type = "�� ";
		if (s[1].equals("user data"))
			type = "Ӧ�ñ���  " + type;
		if (s[1].equals("link data"))
			type = "��·����  " + type;

		String msg = "";
		if (s[1].indexOf("������") >= 0)
			msg = s[1] + "\r\n";
		else {
			String frame = s[2];
			frame = frame.replaceAll(" ", "");
			frame = frame.replaceAll(",", "");
			frame = Util698.seprateString(frame, " ");
			msg = "[" + DateTimeFun.getTimeSSS() + "]" + type + frame + "\r\n";
		}
		txt_frame.setText(msg + txt_frame.getText().toString());
		// jt.requestFocus();
		txt_frame.select(0, 0);

	}

	/**
	 * �����ʼ��
	 */
	@Override
	protected void init() {
		Boolean border = true;
		if (border) {
			
			// BorderLayout(int hgap, int vgap) :  
			// ����һ������ָ�������hgapΪ�����࣬vgapΪ�����ࣩ���ı߿򲼾֡�
			panel.setLayout(new BorderLayout(10,10));
			
			lb_analy = new JLabel("�շ�����");
			panel.add(lb_analy, BorderLayout.NORTH);
			txt_frame = new JTextArea();
			JScrollPane scroll_analy = new JScrollPane(txt_frame);
			panel.add(scroll_analy, BorderLayout.CENTER);
			lb_status = new JLabel("ǰ�û�״̬��ǰ�û������˿�");
			panel.add(lb_status, BorderLayout.SOUTH);
			
		} else {
			lb_analy = new JLabel("�շ�����", JLabel.CENTER);
			txt_frame = new JTextArea();
			lb_status = new JLabel("");

			lb_analy.setForeground(new Color(0, 114, 198));
			txt_frame.setForeground(new Color(0, 114, 198));
			lb_status.setForeground(new Color(0, 114, 198));

			panel.add(lb_analy);
			panel.add(txt_frame);
			panel.add(lb_status);

			int height = 30, lb_width = 80, inter_val = 5;
			int topX = 5, topY = 5;
			int x = topX;
			int y = topY;
			int bt_width = 120;

			lb_analy.setBounds(x, y, lb_width, height);

			JScrollPane scroll_analy = new JScrollPane(txt_frame);
//			JScrollPane scroll_analy = new JScrollPane();
//			scroll_analy.setViewportView(txt_frame);
//			scroll_analy.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//			scroll_analy.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			scroll_analy.setBounds(x + lb_width, y, 680, height + 50);

			panel.add(scroll_analy);

			lb_status.setBounds(x + lb_width, y + height + 50, 300, 30);
			lb_status.setText("ǰ�û�״̬��ǰ�û������˿�");

		}

	}

	public static void main(String[] args) {
	}

}
