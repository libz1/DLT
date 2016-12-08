package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import ui.MainWindow;
import util.SoftParameter;

// ͨ��ǰ�÷�����
public class PrefixMain {

	private volatile static PrefixMain uniqueInstance;
	
	public static PrefixMain getInstance() {
		if (uniqueInstance == null) {
			synchronized (PrefixMain.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new PrefixMain();
				}
			}
		}
		return uniqueInstance;
	}

	private PrefixMain(){
		start();	
	}

	public void start() {
		// �������ݻ�����
		RecvData.getInstance();
		// ����������ݵ��߳�
		DealData.getInstance();
		// �ն�ͨ�Ŷ��󼯺�
		ChannelList.getInstance();

		// ����tcp����
		new Thread() {
			@Override
			public void run() {
				ServerSocket server;
				try {
					int port = SoftParameter.getInstance().getPrefix_port();
					server = new ServerSocket(port);

					while (true) {
						Socket socket = server.accept();
						SocketServer.invoke(socket);
					}

				} catch (Exception e) {
					System.out.println(this.getClass().getName() + "=>"
							+ e.getMessage());
					e.printStackTrace();
				}

			}
		}.start();
		
		
		// �������ڷ���
		new Thread() {
			@Override
			public void run() {
				// ���ݲ������еǼǵĶ�����ڴ������ڷ���
				SerialList list = SoftParameter.getInstance().getSerialList();
				for( SerialParam s:list.getList() ){
					new SerialServer(s);
				}
			}
		}.start();
	}
	
	/*
	private static void showLogWin(){
		int width = 800, height = 150;
		TestFrame mainFrame = TestFrame.getInstance();
		mainFrame.getPanel().setBounds(0, 0, width, height);
		
		JFrame frame = new JFrame();
		frame.setTitle("��־ҳ��");
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(120, 580, width, height);
		frame.setAlwaysOnTop(true); 
		
	}
	*/
	private static void showMainWin(){
		int width = 800, height = 600;
		MainWindow mainFrame = new MainWindow();
		mainFrame.getPanel().setBounds(0, 0, width, height);
		
		JFrame frame = new JFrame();
		frame.setTitle("698.45Э��Ӧ�����");
		frame.setLayout(null);
		frame.add(mainFrame.getPanel());
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(80, 10, width, height);
		//DebugSwing.center(frame);
		
	}

	public static void main(String[] args) throws IOException {
		//DebugSwing.center(frame);
		// ���ʵ�ֹر�һ������ʱ�����ر�����һ������
		//showMainWin();
		//showLogWin();
		PrefixMain prefixMain = new PrefixMain();
		prefixMain.start();
	}
}
