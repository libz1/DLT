package socket;

import util.Publisher;
import util.Util698;

import com.eastsoft.util.Debug;

import frame.Frame698;


// ����������ݵ��߳�
public class DealData {
	private volatile static DealData uniqueInstance;
	
	public static DealData getInstance() {
		if (uniqueInstance == null) {
			synchronized (DealData.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
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
						// �жϸ�ʽ
						String frame = msg.split(";")[1].split("@")[1];
						if (Util698.checkFrameType(frame).equals("698.45")){

							// �������ͨ���ģ���Ҫ�����õ��豸��ַ��
							String socketAddr = msg.split(";")[0].split("@")[1];;
							
							Frame698 frame689 = new Frame698(frame);
							int choiseFlag = frame689.getAPDU().getChoiseFlag();
							String sadata = frame689.getFrameAddr().getSAData();
							if (choiseFlag==1 || choiseFlag==129){
								//��·����
								
								// 1���򷢲���������Ϣ
								String[] s = {"recv frame","link data",frame};
								Publisher.getInstance().publish(s);
								
								
								// 2������ǣ������õ�����������Ϣ���������ͣ��������������Ͽ��������ն��б�
								String choiseData = frame689.getAPDU().getChoiseData();
								String data = "socketAddr@"+socketAddr + ";"+ choiseData+";logaddr@"+sadata;
								ChannelList.getInstance().change(data);
								
								// 3����֯�ظ���·����  ������
								String respData = Frame698.buildResponseFrame(frame689);
								SocketServer.sendData(respData);
								
							}
							else{
								// ��ͨ����
								
								// 1���򷢲���������Ϣ
								String[] s = {"recv frame","user data",frame};
								Publisher.getInstance().publish(s);
								
								// 2�������ն��б�
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
