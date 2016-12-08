package util;

import java.util.Observable;

// 出版者  观察者模式
public class Publisher extends Observable{

	// 单例模式
	private volatile static Publisher uniqueInstance;
	public static Publisher getInstance() {
		if (uniqueInstance == null) {
			synchronized (Publisher.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new Publisher();
				}
			}
		}
		return uniqueInstance;
	}
	
	private Publisher(){
	}
	
	public void publish(final Object[] data){
		// 1-3、DealData中的发送报文
		//String[] s = {"recv frame","link data",frame};
		//Publisher.getInstance().publish(s);
		
		// DB.setObjAttrSelected中的发送进度信息
		//String[] s = {"save progress","",""};
		//s[2] = Util698.getPercent(i+1,v.size(),2);
		//Publisher.getInstance().publish(s);
		
		// 2-3、Publisher 
		
		// 3-3、订阅者 
		// public class LogWindow extends BaseFrame implements Observer
		
		// 1)进行定订阅
		// Publisher.getInstance().addObserver(uniqueInstance);
		//
		// 2)必须实现的接口，接收订阅数据
//		@Override
//		public void update(Observable o, Object arg) {
//			
//			String[] s = (String[]) arg;
//			String type = ""; 
//			if (s[0].equals("recv frame") || s[0].equals("send frame")){
//				showData(arg);
//			}
//		}
//		
		// 3)对订阅数据的业务处理  需要加锁
		// 请注意，需要使用 synchronizedm，防止出现冲突
//		private synchronized void showData(Object arg){
		

		// xuky 2016.11.04 在线程中执行可能有异常
		new Thread() {
			public void run() {
				deal(data);
			}
		}.start();
		
	}
	
	private synchronized void deal(Object[] data){
		setChanged();
		notifyObservers(data);
	}

}
