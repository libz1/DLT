package socket;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

//通信对象，按照终端地址信息为索引储存
public class ChannelObjsByLogiAddr {

	private Map<String, Object> Objs = new HashMap();
	
	private volatile static ChannelObjsByLogiAddr uniqueInstance;
	
	public static ChannelObjsByLogiAddr getInstance() {
		if (uniqueInstance == null) {
			synchronized (ChannelObjsByLogiAddr.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new ChannelObjsByLogiAddr();
				}
			}
		}
		return uniqueInstance;
	}
	
	private ChannelObjsByLogiAddr(){
		
	}

	public void add(String logAddr, Object socket) {
		Objs.put(logAddr, socket);
	}
	
	public Object get(String logAddr) {
		return Objs.get(logAddr);
	}
	
	public void reMove(String logAddr) {
		Objs.remove(logAddr);
	}
	
	
	public Map<String, Object> getList() {
		return Objs;
	}
	public static void main(String[] args) {
	}
	
}