package entity;

import com.eastsoft.util.DataConvert;
import util.Util698;

/**
 * �����.
 * 
 * @author xuky
 * @version 2013.10.24
 */
public class Meter implements Comparable<Meter> {

	private int ID;    // ������Ϣ
	private String archiveType; // ��������
	private String flag; // ɾ����־
	private String optTime; // ��Ϣ����ʱ��

	private String terminalID; // �ն�ID
	private String meterCode; // ���ͨ�ŵ�ַ    // ��Ҫ�ֶ�
	private String measureNo; // �������   �������
	private String collectCode; // �ɼ�����ַ 
	private String protocolType; // ���Э��  ��Լ����

	private String meterType; // ������� 01:������ӱ�02 ��е��,03:���׶๦�ܱ�,08:�๦���ܱ�
	private String usrMeterType; // �û����ܱ��־

	private String port; // �˿ں�  �˿�
	private String type1; // ����š�С���  �û�����
	private String type2; // С���  ���߷�ʽ
	private String fee; // ���ʺ�  ���ʸ���

	private String addr; // �������λ��
	private String gisInfo; // ������λ�þ�γ������
	private String note; // ��ע��Ϣ
	
	private String portRate; // ������
	private String pwd;// ͨ������
	private String ratedVoltage;// ���ѹ
	private String ratedCurrent;// �����
	private String assetsNo;// �ʲ���
	private String PT;// PT
	private String CT;// CT

	// exportAttributes.length������Ҫ�������ݵĸ���

	public Meter() {
		protocolType = "DL/T698.45(3)";
		port = "�ز�/΢����(F2090201)";
		type1 = "1";
		type2 = "����(1)";
		meterType = "������ӱ�";
		usrMeterType = "��";
		fee = "4";
		portRate = "2400bps(3)";
		PT = "1";
		CT = "1";
		pwd = "000000000000";
		terminalID = "1";
		archiveType = "1";
		ratedVoltage = "0";
		ratedCurrent = "0";
	}

	public void setProtocolType(String data) {
		protocolType = Util698.getCodeData(data,"protocolType");
	}

	public void setPort(String data) {
		port = Util698.getCodeData(data,"port");
	}

	public void setType2(String data) {
		type2 = Util698.getCodeData(data,"type2");
	}

	public void setPortRate(String data) {
		portRate = Util698.getCodeData(data,"portRate");;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}

	public void setUsrMeterType(String usrMeterType) {
		this.usrMeterType = usrMeterType;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}

	public String getTerminalID() {
		return terminalID;
	}

	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}

	public String getMeterCode() {
		return meterCode;
	}

	public void setMeterCode(String meterCode) {
		// �Ե���ַ���и�ʽ������һ����12λ��ǰ�����0
		this.meterCode = DataConvert.fillWith0(meterCode, 12);
	}

	public String getMeasureNo() {
		return measureNo;
	}

	public void setMeasureNo(String measureNo) {
		// �Բ�����Ž��и�ʽ������һ����4λ��ǰ�����0
		this.measureNo = DataConvert.fillWith0(measureNo, 4);
	}

	public String getCollectCode() {
		return collectCode;
	}

	public void setCollectCode(String collectCode) {
		this.collectCode = DataConvert.fillWith0(collectCode, 12);
	}

	public String getProtocolType() {
		return protocolType;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getGisInfo() {
		return gisInfo;
	}

	public void setGisInfo(String gisInfo) {
		this.gisInfo = gisInfo;
	}

	public String getArchiveType() {
		return archiveType;
	}

	public void setArchiveType(String archiveType) {
		this.archiveType = archiveType;
	}

	public String getOptTime() {
		return optTime;
	}

	public void setOptTime(String optTime) {
		this.optTime = optTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getUsrMeterType() {
		return usrMeterType;
	}

	public String getMeterType() {
		return meterType;
	}

	@Override
	public int compareTo(Meter o) {
		// �ο�http://www.cnblogs.com/wentiertong/archive/2011/03/07/1973698.html
		// Java��List������
		Integer i, j;
		i = DataConvert.String2Int(this.getMeasureNo());
		j = DataConvert.String2Int(o.getMeasureNo());
		return i.compareTo(j);
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPort() {
		return port;
	}

	public String getType1() {
		return type1;
	}

	public String getType2() {
		return type2;
	}

	public String getFee() {
		return fee;
	}
	
	public String getPortRate() {
		return portRate;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getRatedVoltage() {
		return ratedVoltage;
	}

	public void setRatedVoltage(String ratedVoltage) {
		this.ratedVoltage = ratedVoltage;
	}

	public String getRatedCurrent() {
		return ratedCurrent;
	}

	public void setRatedCurrent(String ratedCurrent) {
		this.ratedCurrent = ratedCurrent;
	}

	public String getAssetsNo() {
		return assetsNo;
	}

	public void setAssetsNo(String assetsNo) {
		this.assetsNo = assetsNo;
	}

	public String getPT() {
		return PT;
	}

	public void setPT(String pT) {
		PT = pT;
	}

	public String getCT() {
		return CT;
	}

	public void setCT(String cT) {
		CT = cT;
	}


	public static void main(String[] args) {
//		String s = "123456"; 
//		
//		System.out.println(DataConvert.reverseString(DataConvert.reverseString(s)));
		 Meter meter = new  Meter();
		 String data = "3";
		 meter.setProtocolType(data);
		 System.out.println(meter.getProtocolType());
		 data = "DL/T645-2007(2)";
		 meter.setProtocolType(data);
		 System.out.println(meter.getProtocolType());
	}

}
