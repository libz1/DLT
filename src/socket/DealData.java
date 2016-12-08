package socket;

import util.Publisher;
import util.Util698;

import com.eastsoft.util.Debug;

import frame.Frame698;


// 处理接收数据的线程
public class DealData {
	private volatile static DealData uniqueInstance;
	
	public static DealData getInstance() {
		if (uniqueInstance == null) {
			synchronized (DealData.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new DealData();
				}
			}
		}
		return uniqueInstance;
	}
	
	
	private DealData(){
		new Thread() {
			@Override
			public void run() { 
				while (true){
					String msg = RecvData.getInstance().pop();
					if (!msg.equals("")){
						System.out.println("DealData=>"+msg);
						// 判断格式
						String frame = msg.split(";")[1].split("@")[1];
						if (Util698.checkFrameType(frame).equals("698.45")){

							// 如果是普通报文，需要解析得到设备地址，
							String socketAddr = msg.split(";")[0].split("@")[1];;
							
							Frame698 frame689 = new Frame698(frame);
							int choiseFlag = frame689.getAPDU().getChoiseFlag();
							String sadata = frame689.getFrameAddr().getSAData();
							if (choiseFlag==1 || choiseFlag==129){
								//链路报文
								
								// 1、向发布者推送消息
								String[] s = {"recv frame","link data",frame};
								Publisher.getInstance().publish(s);
								
								
								// 2、如果是，解析得到心跳周期信息、请求类型：建立、心跳、断开，更新终端列表
								String choiseData = frame689.getAPDU().getChoiseData();
								String data = "socketAddr@"+socketAddr + ";"+ choiseData+";logaddr@"+sadata;
								ChannelList.getInstance().change(data);
								
								// 3、组织回复链路报文  并发送
								String respData = Frame698.buildResponseFrame(frame689);
								SocketServer.sendData(respData);
								
							}
							else{
								// 普通报文
								
								// 1、向发布者推送消息
								String[] s = {"recv frame","user data",frame};
								Publisher.getInstance().publish(s);
								
								// 2、更新终端列表
								String data = "socketAddr@"+socketAddr + ";logaddr@"+sadata;
								ChannelList.getInstance().change(data);
								//System.out.println("channels converToString=>"+ChannelList.getInstance().converToString());

							}
							
			        		String[] s1 = {"refresh terminal list","",""};
			        		Publisher.getInstance().publish(s1);
							
							//System.out.println("ChannelList=>"+ChannelList.getInstance().converToString());							
							
						}
					}
					Debug.sleep(200);
				}
			}

		}.start();
	}
	
	
	public static void main(String[] args) {

	}

}
