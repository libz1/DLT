package util;

import java.util.Observable;

// ������  �۲���ģʽ
public class Publisher extends Observable{

	// ����ģʽ
	private volatile static Publisher uniqueInstance;
	public static Publisher getInstance() {
		if (uniqueInstance == null) {
			synchronized (Publisher.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new Publisher();
				}
			}
		}
		return uniqueInstance;
	}
	
	private Publisher(){
	}
	
	public void publish(final Object[] data){
		// 1-3��DealData�еķ��ͱ���
		//String[] s = {"recv frame","link data",frame};
		//Publisher.getInstance().publish(s);
		
		// DB.setObjAttrSelected�еķ��ͽ�����Ϣ
		//String[] s = {"save progress","",""};
		//s[2] = Util698.getPercent(i+1,v.size(),2);
		//Publisher.getInstance().publish(s);
		
		// 2-3��Publisher 
		
		// 3-3�������� 
		// public class LogWindow extends BaseFrame implements Observer
		
		// 1)���ж�����
		// Publisher.getInstance().addObserver(uniqueInstance);
		//
		// 2)����ʵ�ֵĽӿڣ����ն�������
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
		// 3)�Զ������ݵ�ҵ����  ��Ҫ����
		// ��ע�⣬��Ҫʹ�� synchronizedm����ֹ���ֳ�ͻ
//		private synchronized void showData(Object arg){
		

		// xuky 2016.11.04 ���߳���ִ�п������쳣
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
