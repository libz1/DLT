package socket;

import java.io.IOException;
import java.io.InputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import util.Publisher;

import com.eastsoft.util.Debug;

public class SerialServer {

	public SerialServer(SerialParam s) {
		SerialPort sPort = null;
		try {
			// 启动对某个串口的监听
			String str = s.getCOMM();
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(str);
			sPort = (SerialPort) portId.open("shipment", 1000);
			SerialParam param = s;
			// 设置串口通信参数
			sPort.setSerialPortParams(((SerialParam) param).getBaudRate(),
					((SerialParam) param).getDataBit(),
					((SerialParam) param).getStopBit(),
					((SerialParam) param).getParity());
			invoke(sPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void invoke(final SerialPort sPort) throws IOException {
		// 启动线程，接收数据  发送数据代码在SocketServer中
		new Thread(new Runnable() {
			public void run() {
				ChannelList.getInstance().add(sPort);

				String[] s = { "refresh terminal list", "", "" };
				Publisher.getInstance().publish(s);

				String devAddr = sPort.getName();
				InputStream in = null;
				try {
					in = sPort.getInputStream();
					while (true) {
						// 按照byte流的模式读取数据
						try {
							String msg = SocketServer.readData(in, devAddr);
							System.out.println("SerialServer recv=>" + msg);
						} catch (IOException e) {
							// xuky 2016.08.10 如果接收数据出现错误，就退出
							System.out.println("SerialServer invoke=> 退出线程");
							break;
						}
						Debug.sleep(100);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					try {
						in.close();
						sPort.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) throws IOException {
		//new SerialServer();
	}

}
