package socket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.comm.SerialPort;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;
import com.google.gson.Gson;

// 终端通信对象集合
public class ChannelList {

	private List<Channel> channelList = new ArrayList<Channel>();

	private volatile static ChannelList uniqueInstance;
	
	public static ChannelList getInstance() {
		if (uniqueInstance == null) {
			synchronized (ChannelList.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					uniqueInstance = new ChannelList();
				}
			}
		}
		return uniqueInstance;
	}
	
	private ChannelList(){
		
	}
	
	public String converToString() {
		// 因为Channel中有Object对象，导致无法导出
		return new Gson().toJson(channelList);
	}
	
	public Channel getByCode(String addr) {
		Channel channel_ = null;
		for (Channel channel : channelList)
			if (channel.getAddr().equals(addr)) {
				channel_ = channel;
				break;
			}
		return channel_;
	}
	
	public void add(Channel channel) {
		Channel Channel_ = getByCode(channel.getAddr());
		if (Channel_ == null)
			channelList.add(channel);
	}
	
	public void add(Socket socket ) {
		Channel channel = new Channel();
		String addr = socket.getRemoteSocketAddress().toString();
		channel.setAddr(addr);
		add(channel);
		
		ChannelObjs.getInstance().add(addr,socket);
	}
	
	public void add(SerialPort sPort ) {
		Channel channel = new Channel();
		String addr = sPort.getName();
		channel.setAddr(addr);
		channel.setType("2");
		add(channel);
		
		ChannelObjs.getInstance().add(addr,sPort);
	}
	
	public List<Channel> getChannelList() {
		return channelList;
	}
	
	public void change(String data) {
		String addr = data.split(";")[0].split("@")[1];
		
		// 根据终端地址得到channel对象
		Channel channel = getByCode(addr);
		if (data.indexOf("reqType")>=0){
			String reqType = data.split(";")[1].split("@")[1];
			String heartTime = data.split(";")[2].split("@")[1];
			String reqDateTime = data.split(";")[3].split("@")[1];
			String sadata = data.split(";")[4].split("@")[1];
			channel.setRecvTime(reqDateTime);
			channel.setHeatTime(DataConvert.String2Int(heartTime));
			channel.setLogAddr(sadata);
			ChannelObjs co = ChannelObjs.getInstance();
			ChannelObjsByLogiAddr col = ChannelObjsByLogiAddr.getInstance();
			col.add(sadata, co.get(addr));
//			System.out.println("ChannelObjs=>"+co);
//			System.out.println("ChannelObjsByLogiAddr=>"+col);
			if (reqType.equals("0"))
				channel.setConnectTime(reqDateTime);
		}
		else{
			String sadata = data.split(";")[1].split("@")[1];
			channel.setLogAddr(sadata);
			String time  = DateTimeFun.getDateTimeSSS();
			channel.setRecvTime(time);
			ChannelObjsByLogiAddr.getInstance().add(sadata, ChannelObjs.getInstance().get(addr));
		}
	}
}
