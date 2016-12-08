package socket;

import java.util.HashMap;
import java.util.Map;

// ͨ�Ŷ��󣬰��յ�ַ��Ϣ(socke��ַ�򴮿ں�)Ϊ��������
public class ChannelObjs {

	private Map<String, Object> Objs = new HashMap();
	
	private volatile static ChannelObjs uniqueInstance;
	
	public static ChannelObjs getInstance() {
		if (uniqueInstance == null) {
			synchronized (ChannelObjs.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new ChannelObjs();
				}
			}
		}
		return uniqueInstance;
	}
	
	private ChannelObjs(){
		
	}


	public void add(String addr, Object socket) {
		Objs.put(addr, socket);
	}
	
	public Object get(String addr) {
		return Objs.get(addr);
	}
	
	public void reMove(String addr) {
		Objs.remove(addr);
	}
	
	
	public Map<String, Object> getList() {
		return Objs;
	}
	public static void main(String[] args) {
	}
	
}