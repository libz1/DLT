package socket;

import java.util.LinkedList;
import java.util.Queue;


//  接收数据缓冲区 (单例模式，所以收到的数据都放在这里)
public class RecvData {
	private volatile static RecvData uniqueInstance;
	
	Queue<String> recvData = new LinkedList<String>();
	
	public static RecvData getInstance() {
		if (uniqueInstance == null) {
			synchronized (RecvData.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
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
