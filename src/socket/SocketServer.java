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
				// �����Ӷ��б��������
				ChannelList.getInstance().add(client);

				String[] s = { "refresh terminal list", "", "" };
				Publisher.getInstance().publish(s);

				String devAddr = client.getRemoteSocketAddress().toString();
				InputStream in = null;
				try {
					in = client.getInputStream();
					while (true) {
						try {
							// ����byte����ģʽ��ȡ����
							String msg = readData(in, devAddr);
							System.out.println("SocketServer recv=>" + msg);
						} catch (IOException e) {
							// xuky 2016.08.10 ����������ݳ��ִ��󣬾��˳�
							System.out.println("SocketServer invoke=> �˳��߳�");
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
		// ����byte����ģʽ��ȡ����
		byte[] receiveByte = new byte[4096];
		int messageLength = is.read(receiveByte);
		byte[] currReceiveByte = new byte[messageLength];
		for (int i = 0; i < messageLength; i++)
			currReceiveByte[i] = receiveByte[i];

		String recvData = "";
		if (currReceiveByte != null)
			// byteתΪ�ַ���
			recvData = DataConvert.bytes2HexString(currReceiveByte);

		// ͨ�ŵ�ַ ͨ������
		String msg = "addr@" + devAddr + ";" + "msg@" + recvData;

		// �յ�������ӵ�RecvData����������
		RecvData.getInstance().push(msg);
		return msg;
	}

	// ���ݱ����е��ն˵�ַ�Զ��ҵ���Ӧ��socket������δ�����쳣����
	public static void sendData(String sendData) {

		// 1���Է��͵ı��Ľ��н������õ�����������Ϣ(��·���ġ��û�����)
		Frame698 frame689 = new Frame698(sendData);
		int choiseFlag = frame689.getAPDU().getChoiseFlag();

		String frameType = "user data";
		if (choiseFlag == 1 || choiseFlag == 129)
			frameType = "link data";

		// 3���õ����ĵķ���Ŀ���豸��ַ
		String sadata = frame689.getFrameAddr().getSAData();
		
		OutputStream os = null;
		try {
			// 4�������豸��ַ�Ҷ�Ӧ������ͨ�Ŷ��󣬿���������Ҳ�����Ǵ���
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
				// 5����������ͨ�Ŷ���ĵ�ַ��Ϣ����ͨ������
				Channel channel = ChannelList.getInstance().getByCode(obj_addr);
				if (channel != null) {
					// �޸�ͨ����������ͨ��ʱ��
					channel.setRecvTime(DateTimeFun.getDateTimeSSS());
					// ˢ�½����е��ն�ͨ���б�
					String[] s1 = { "refresh terminal list", "", "" };
					Publisher.getInstance().publish(s1);
					
					// 2������Ϣ���ķ�����Ϣ:������ĳ�����͵�ĳ������
					String[] s = { "send frame", frameType, sendData };
					Publisher.getInstance().publish(s);
					
				}
				
				// 6��������ͨ�Ŷ����з�������
				sendData(os, sendData);
			
			} else {
				String[] s1 = { "send frame", "�豸" + sadata + "������" };
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
				// xuky 2016.08.10 �ر�OutputStream�ᵼ��socket�رգ����Բ�ִ��
				// �ο� http://blog.csdn.net/justoneroad/article/details/6962567
				// os.close();
			} catch (Exception e) {
			}
		}
	}

	public static void sendData(OutputStream os, String sData) {
		// ��������
		try {
			byte[] byteData = new byte[sData.length() / 2];
			// ��16�����ַ���תΪByte����
			byteData = DataConvert.hexString2ByteArray(sData);
			System.out.println("sendData=>"+sData);
			os.write(byteData);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		// �����˿�Ϊ10000
		ServerSocket server = new ServerSocket(10000);
		while (true) {
			Socket socket = server.accept();
			invoke(socket);
		}
	}

}
