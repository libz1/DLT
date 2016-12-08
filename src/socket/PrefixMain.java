package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import ui.MainWindow;
import util.SoftParameter;

// 通信前置服务器
public class PrefixMain {

	private volatile static PrefixMain uniqueInstance;
	
	public static PrefixMain getInstance() {
		if (uniqueInstance == null) {
			synchronized (PrefixMain.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
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
		// 接收数据缓冲区
		RecvData.getInstance();
		// 处理接收数据的线程
		DealData.getInstance();
		// 终端通信对象集合
		ChannelList.getInstance();

		// 启动tcp服务
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
		
		
		// 启动串口服务
		new Thread() {
			@Override
			public void run() {
				// 根据参数表中登记的多个串口创建串口服务
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
		frame.setTitle("日志页面");
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
		frame.setTitle("698.45协议应用软件");
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
		// 如何实现关闭一个窗口时，不关闭另外一个窗口
		//showMainWin();
		//showLogWin();
		PrefixMain prefixMain = new PrefixMain();
		prefixMain.start();
	}
}
