package util;

import java.io.IOException;

import socket.SerialList;
import socket.SerialParam;

import com.eastsoft.fio.FileToRead;
import com.eastsoft.fio.FileToWrite;
import com.google.gson.Gson;

import base.BaseFrame;

/**
 * 软件变量.
 * <p>
 * 
 * @author xuky
 * @version 2016.09.18
 */
public class SoftParameter {
	
	// 前置机监听端口
	private int prefix_port = 20001;
	
	// 默认的通信终端地址
	private String sendTerminal = "000000000002";
	
	// 多路串口通信参数对象
	private SerialList serialList ;
	public SerialList getSerialList() {
		return serialList;
	}
	
	// 是否为代理模式 0 表示非代理模式，1表示代理模式
	private String isProxyModel = "0";
	
	// 整个代理请求超时时间  单位为秒 3分钟
	private int all_timeout = 60 * 3;
	// 单个代理请求超时时间  单位为秒 1分钟
	private int single_timeout = 60 * 1;
	//目标服务器地址（多个时，使用逗号进行分隔）  ProxyGetRequestRecord时。只取用第一个	
	private String targets = "";
	
	//当前正在解析处理的报文
	private String sendFrame = "";	
	private String recvFrame = "";	
	private String APDUData = "";	
	

	// 单例模式：静态变量 uniqueInstance 类的唯一实例
	private volatile static SoftParameter uniqueInstance;

	public static SoftParameter getInstance(String str) {
		if (uniqueInstance == null) {
			synchronized (SoftParameter.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Gson().fromJson(str,
							SoftParameter.class);
				}
			}
		}
		return uniqueInstance;
	}
	
	private SoftParameter(){
		
		SerialParam serialParam = new SerialParam();  
		serialParam.setCOMM("COM6");
		
		serialList = SerialList.getInstance();
		serialList.add(serialParam);
		
		serialParam = new SerialParam();  
		serialParam.setCOMM("COM8");
		serialList.add(serialParam);
	}

	public static SoftParameter getInstance() {
		if (uniqueInstance == null) {
			synchronized (SoftParameter.class) {
				if (uniqueInstance == null) {
					// 双重检查加锁
					String str = new FileToRead()
							.readLocalFile1("arc\\SoftParameter.json");
					uniqueInstance = new Gson().fromJson(str,
							SoftParameter.class);
				}
			}
		}
		return uniqueInstance;
	}

	public String saveParam() {
		String str = getParamString();
		FileToWrite.writeLocalFile1("arc\\SoftParameter.json", str);
		return str;
	}

	public String getParamString() {
		return new Gson().toJson(uniqueInstance);
	}

	// xuky 2014.09.10 调整为需要接收传入的字符串参数
	public void refresh(String str){
		uniqueInstance = null;
		getInstance(str);
	}
	
	public void init(){
		String str = new Gson().toJson(this);
		FileToWrite.writeLocalFile1("arc\\SoftParameter.json", str);
	}

    public int getPrefix_port() {
		return prefix_port;
	}

	public void setPrefix_port(int prefix_port) {
		this.prefix_port = prefix_port;
	}

	public String getSendTerminal() {
		return sendTerminal;
	}

	public void setSendTerminal(String sendTerminal) {
		this.sendTerminal = sendTerminal;
	}
	
	
	public int getAll_timeout() {
		return all_timeout;
	}

	public void setAll_timeout(int all_timeout) {
		this.all_timeout = all_timeout;
	}

	public int getSingle_timeout() {
		return single_timeout;
	}

	public void setSingle_timeout(int single_timeout) {
		this.single_timeout = single_timeout;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}
	
	

	public String getIsProxyModel() {
		return isProxyModel;
	}

	public void setIsProxyModel(String isProxyModel) {
		this.isProxyModel = isProxyModel;
	}
	

	public String getSendFrame() {
		return sendFrame;
	}

	public void setSendFrame(String sendFrame) {
		this.sendFrame = sendFrame;
	}
	

	public String getRecvFrame() {
		return recvFrame;
	}

	public void setRecvFrame(String recvFrame) {
		this.recvFrame = recvFrame;
	}

	public String getAPDUData() {
		return APDUData;
	}

	public void setAPDUData(String aPDUData) {
		APDUData = aPDUData;
	}

	public static void main(String[] args) throws IOException {
    	SoftParameter softParameter = new SoftParameter();
    	uniqueInstance = softParameter;
    	softParameter.saveParam();
    	//softParameter.init();
    }


}
