package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.comm.SerialPort;

import util.Publisher;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;
import com.eastsoft.util.Debug;

import frame.Frame698;

public class SocketServer {

	public static void invoke(final Socket client) throws IOException {
		new Thread(new Runnable() {
			public void run() {
				// 向连接端列表插入数据
				ChannelList.getInstance().add(client);

				String[] s = { "refresh terminal list", "", "" };
				Publisher.getInstance().publish(s);

				String devAddr = client.getRemoteSocketAddress().toString();
				InputStream in = null;
				try {
					in = client.getInputStream();
					while (true) {
						try {
							// 按照byte流的模式读取数据
							String msg = readData(in, devAddr);
							System.out.println("SocketServer recv=>" + msg);
						} catch (IOException e) {
							// xuky 2016.08.10 如果接收数据出现错误，就退出
							System.out.println("SocketServer invoke=> 退出线程");
							break;
						}
						Debug.sleep(100);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					try {
						in.close();
						client.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	public static String readData(InputStream is, String devAddr)
			throws IOException {
		// 按照byte流的模式读取数据
		byte[] receiveByte = new byte[4096];
		int messageLength = is.read(receiveByte);
		byte[] currReceiveByte = new byte[messageLength];
		for (int i = 0; i < messageLength; i++)
			currReceiveByte[i] = receiveByte[i];

		String recvData = "";
		if (currReceiveByte != null)
			// byte转为字符串
			recvData = DataConvert.bytes2HexString(currReceiveByte);

		// 通信地址 通信内容
		String msg = "addr@" + devAddr + ";" + "msg@" + recvData;

		// 收到数据添加到RecvData单例对象中
		RecvData.getInstance().push(msg);
		return msg;
	}

	// 根据报文中的终端地址自动找到对应的socket对象，尚未进行异常处理
	public static void sendData(String sendData) {

		// 1、对发送的报文进行解析，得到报文类型信息(链路报文、用户报文)
		Frame698 frame689 = new Frame698(sendData);
		int choiseFlag = frame689.getAPDU().getChoiseFlag();

		String frameType = "user data";
		if (choiseFlag == 1 || choiseFlag == 129)
			frameType = "link data";

		// 3、得到报文的发送目标设备地址
		String sadata = frame689.getFrameAddr().getSAData();
		
		OutputStream os = null;
		try {
			// 4、根据设备地址找对应的物理通信对象，可能是网口也可能是串口
			Object object = ChannelObjsByLogiAddr.getInstance().get(sadata);
			
			Socket client = null;
			SerialPort serial = null;
			String obj_addr = "";
			
			if (object instanceof Socket) {
				client = (Socket) object;
				obj_addr = client.getRemoteSocketAddress().toString(); 
				os = client.getOutputStream();
			}
			if (object instanceof SerialPort) {
				serial = (SerialPort) object;
				obj_addr = serial.getName(); 
				os = serial.getOutputStream();
			}
			
			if (os != null){
				// 5、根据物理通信对象的地址信息，找通道对象
				Channel channel = ChannelList.getInstance().getByCode(obj_addr);
				if (channel != null) {
					// 修改通道对象的最近通信时间
					channel.setRecvTime(DateTimeFun.getDateTimeSSS());
					// 刷新界面中的终端通信列表
					String[] s1 = { "refresh terminal list", "", "" };
					Publisher.getInstance().publish(s1);
					
					// 2、向信息中心发布消息:发送了某种类型的某个报文
					String[] s = { "send frame", frameType, sendData };
					Publisher.getInstance().publish(s);
					
				}
				
				// 6、向物理通信对象中发送数据
				sendData(os, sendData);
			
			} else {
				String[] s1 = { "send frame", "设备" + sadata + "不在线" };
				Publisher.getInstance().publish(s1);
			}
				
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void sendData(Socket client, String sData) {
		OutputStream os = null;
		try {
			os = client.getOutputStream();
			sendData(os, sData);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// xuky 2016.08.10 关闭OutputStream会导致socket关闭，所以不执行
				// 参考 http://blog.csdn.net/justoneroad/article/details/6962567
				// os.close();
			} catch (Exception e) {
			}
		}
	}

	public static void sendData(OutputStream os, String sData) {
		// 发送数据
		try {
			byte[] byteData = new byte[sData.length() / 2];
			// 将16进制字符串转为Byte数组
			byteData = DataConvert.hexString2ByteArray(sData);
			System.out.println("sendData=>"+sData);
			os.write(byteData);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		// 监听端口为10000
		ServerSocket server = new ServerSocket(10000);
		while (true) {
			Socket socket = server.accept();
			invoke(socket);
		}
	}

}
