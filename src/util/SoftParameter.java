package util;

import java.io.IOException;

import socket.SerialList;
import socket.SerialParam;

import com.eastsoft.fio.FileToRead;
import com.eastsoft.fio.FileToWrite;
import com.google.gson.Gson;

import base.BaseFrame;

/**
 * �������.
 * <p>
 * 
 * @author xuky
 * @version 2016.09.18
 */
public class SoftParameter {
	
	// ǰ�û������˿�
	private int prefix_port = 20001;
	
	// Ĭ�ϵ�ͨ���ն˵�ַ
	private String sendTerminal = "000000000002";
	
	// ��·����ͨ�Ų�������
	private SerialList serialList ;
	public SerialList getSerialList() {
		return serialList;
	}
	
	// �Ƿ�Ϊ����ģʽ 0 ��ʾ�Ǵ���ģʽ��1��ʾ����ģʽ
	private String isProxyModel = "0";
	
	// ������������ʱʱ��  ��λΪ�� 3����
	private int all_timeout = 60 * 3;
	// ������������ʱʱ��  ��λΪ�� 1����
	private int single_timeout = 60 * 1;
	//Ŀ���������ַ�����ʱ��ʹ�ö��Ž��зָ���  ProxyGetRequestRecordʱ��ֻȡ�õ�һ��	
	private String targets = "";
	
	//��ǰ���ڽ�������ı���
	private String sendFrame = "";	
	private String recvFrame = "";	
	private String APDUData = "";	
	

	// ����ģʽ����̬���� uniqueInstance ���Ψһʵ��
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
					// ˫�ؼ�����
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

	// xuky 2014.09.10 ����Ϊ��Ҫ���մ�����ַ�������
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
