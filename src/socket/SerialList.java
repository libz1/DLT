package socket;

import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;

// ����ͨ�Ų������󼯺�
public class SerialList {

	private List<SerialParam> serialList = new ArrayList<SerialParam>();

	private volatile static SerialList uniqueInstance;
	
	public static SerialList getInstance() {
		if (uniqueInstance == null) {
			synchronized (SerialList.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new SerialList();
				}
			}
		}
		return uniqueInstance;
	}
	
	private SerialList(){
		
	}
	
	public String converToString() {
		// ��ΪChannel����Object���󣬵����޷�����
		return new Gson().toJson(serialList);
	}
	
	public SerialParam getByCode(String comm) {
		SerialParam channel_ = null;
		for (SerialParam channel : serialList)
			if (channel.getCOMM().equals(comm)) {
				channel_ = channel;
				break;
			}
		return channel_;
	}
	
	public void add(SerialParam serialParam) {
		SerialParam Channel_ = getByCode(serialParam.getCOMM());
		if (Channel_ == null)
			serialList.add(serialParam);
	}
	
	public List<SerialParam> getList() {
		return serialList;
	}
	
}
