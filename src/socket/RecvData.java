package socket;

import java.util.LinkedList;
import java.util.Queue;


//  �������ݻ����� (����ģʽ�������յ������ݶ���������)
public class RecvData {
	private volatile static RecvData uniqueInstance;
	
	Queue<String> recvData = new LinkedList<String>();
	
	public static RecvData getInstance() {
		if (uniqueInstance == null) {
			synchronized (RecvData.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new RecvData();
				}
			}
		}
		return uniqueInstance;
	}
	
	private RecvData(){
		
	}
	
	public void push(String data){
		recvData.offer(data);
		//System.out.println("Queue recvData=>"+recvData.size());
	}
	
	public String pop(){
		String str = recvData.poll() ;
		if (str==null) str = "";
		return str;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
