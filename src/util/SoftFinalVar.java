package util;

import java.util.Vector;

/**
 * ����.
 * @author xuky
 * @version 2013.10.24
 */
public class SoftFinalVar {

	// Э������
	// ����2005��Լ PWD 2�ֽ�
	public final static String PROTOCOL_2005 = "01";
	// ����376.1Э�� PWD 16�ֽ�
	public final static String PROTOCOL_3761 = "02";
	// �㶫Э��
	public final static String PROTOCOL_GD = "10";
	// ����Э��
	public final static String PROTOCOL_EASTSOFT = "00";
	
	// ����376.2Э��
	public final static String PROTOCOL_3762 = "88";

	// ���Э������
	// 645-2007
	public final static String METERPROTOCOL_07 = "645-2007";
	// 645-1997
	public final static String METERPROTOCOL_97 = "645-1997";
	
	
	// ����376.1Э�� ������
	// AFN AFN=00H ȷ�Ϸ���
	public final static String GW_CONFIRM = "00";
	// AFN AFN=01H ��λ����
	public final static String GW_RESET = "01";
	// AFN AFN=02H ��·�ӿڼ��
	public final static String GW_LINKTEST = "02";
	// AFN AFN=04H ���ò���
	public final static String GW_WRITE_PARAM = "04";
	// AFN AFN=05H ��������
	public final static String GW_CONTROL = "05";
	// AFN AFN=09H �����ն�����
	public final static String GW_REQINFO = "09";
	// AFN AFN=0AH ��ѯ����
	public final static String GW_READ_PARAM = "0A";
	// AFN AFN=0CH ��ѯһ������ ��ǰ����
	public final static String GW_READ_CURDATA = "0C";
	// AFN AFN=0DH ��ѯ�������� ��ʷ����
	public final static String GW_READ_HISDATA = "0D";
	// AFN AFN=0DH ��ѯ�������� �澯����
	public final static String GW_READ_ALMDATA = "0E";
	// AFN AFN=10H ����ת��
	public final static String GW_TRANSMIT = "10";

	// �㶫Э�� ������
	// ������ ����������ǰ�û���ǰ����
	public final static String GD_C_READ = "01";
	public final static String GD_C_READ_REPLY = "81";
	// ������ д�������
	public final static String GD_C_WRITE = "08";
	public final static String GD_C_WRITE_REPLY = "88";
	// ������ ���������յ��ʵʱ����
	public final static String GD_C_READREALTIME = "11";
	public final static String GD_C_READREALTIME_REPLY = "91";
	// ������ �����������ճ��ۺ�����
	public final static String GD_C_READDATA = "12";
	public final static String GD_C_READDATA_REPLY = "92";
	public final static String GD_C_READDATA_ERR = "D2";
	// ������ ���յ����
	public final static String GD_C_READMETER = "15";
	public final static String GD_C_READMETER_REPLY = "95";
	// ������ �����������ӣ��ն��Ƿ����ģʽ��
	public final static String GD_C_RJ45CONNECT = "31";
	public final static String GD_C_RJ45CONNECT_OK = "B1";
	public final static String GD_C_RJ45CONNECT_ERR = "F1";
	//�쳣������
	public final static String GD_ERR_WRITE_REPLY = "C8";
	//�������������
	public final static String GD_C_EXT = "39";
	public final static String GD_C_EXT_OK = "B9";
	public final static String GD_C_EXT_ERR = "F9";
	
	//ǰ�û������ն���������ı���
	public final static String GD_TER_LOGIN = "A1";
	public final static String GD_TER_LOGIN_REPLY = "21";
	public final static String GD_TER_LOGOUT = "A2";
	public final static String GD_TER_LOGOUT_REPLY = "22";
	public final static String GD_TER_TIMEOUT = "A3";
	public final static String GD_TER_TIMEOUT_REPLY = "23";
	
	public final static String GD_TER_LOCK = "A5"; //����ǰ�û��Զ����ݣ�ͨ������
	
	// ͨ�Ų���
	public final static String COMM_TYPE_SOCKET = "1"; // socket
	public final static String COMM_TYPE_COM = "2"; // com


	public static Vector<String> getGDCommParamVector() {
		Vector<String> v = null;
		v = new Vector<String>();
		v.addElement("GPRS/CDMA");
		v.addElement("PSTN");
		v.addElement("RS232/RS485");
		v.addElement("CSD");
		v.addElement("Radio");
		v.addElement("���Ż���(GPRS/CDMA)");
		return v;
	}
	
	public static String[] ProtocolType = {"����376.1��Լ","����2005��Լ","�㶫��Լ"};
	public static Vector<String> getVector(String aType) {
		Vector<String> v = null;
		v = new Vector<String>();
		if (aType.equals("ProtocolType"))
		{
			for (String str:ProtocolType)
				v.addElement(str);
		}
		return v;
	}
	
	

	public static String getCode(String aType, String data) {
		//"GDCommType"  �㶫Э��ͨ�ŷ�ʽ  ����õ�����
		//"GDCommTypeCode"  �㶫Э��ͨ�ŷ�ʽ  ���ݵõ�����
		//"ErrNote"  �㶫Э��  ����õ�����
		//ProtocolCode �ն�Э������  ���ݵõ�����
		String retString = "";
		if (aType.equals("GDCommType")) {
			if (data.equals("GPRS/CDMA"))
				retString = "02";
			if (data.equals("PSTN"))
				retString = "03";
			if (data.equals("RS232/RS485"))
				retString = "06";
			if (data.equals("CSD"))
				retString = "07";
			if (data.equals("Radio"))
				retString = "08";
			if (data.equals("���Ż���(GPRS/CDMA)"))
				retString = "09";
		}
		if (aType.equals("GDCommTypeCode")) {
			if (data.equals("02"))
				retString = "GPRS/CDMA";
			if (data.equals("03"))
				retString = "PSTN";
			if (data.equals("06"))
				retString = "RS232/RS485";
			if (data.equals("07"))
				retString = "CSD";
			if (data.equals("08"))
				retString = "Radio";
			if (data.equals("09"))
				retString = "���Ż���(GPRS/CDMA)";
		}
		if (aType.equals("ErrNote")) {
			if (data.equals("00"))
				retString = "��ȷ";
			if (data.equals("01"))
				retString = "�м������޷���";
			if (data.equals("02"))
				retString = "�������ݷǷ�";
			if (data.equals("03"))
				retString = "����Ȩ�޲���";
			if (data.equals("04"))
				retString = "�޴�������";
			if (data.equals("05"))
				retString = "����ʱ��ʧЧ";
			if (data.equals("11"))
				retString = "Ŀ���ַ������";
			if (data.equals("12"))
				retString = "����ʧ��";
			if (data.equals("13"))
				retString = "����Ϣ̫֡��";		
		}
		if (aType.equals("ProtocolCode")) {
			if (data.equals("����376.1��Լ"))
				retString = "02";
			if (data.equals("����2005��Լ"))
				retString = "01";
			if (data.equals("�㶫��Լ"))
				retString = "10";
		}
		if (aType.equals("MeterTypeCode")) {
			if (data.equals("������ӱ�"))
				retString = "1";
			if (data.equals("��е��"))
				retString = "2";
			if (data.equals("���׶๦�ܱ�"))
				retString = "3";
			if (data.equals("�๦���ܱ�"))
				retString = "8";
		}
		if (aType.equals("MeterTypeNote")) {
			if (data.equals("01"))
				retString = "������ӱ�";
			if (data.equals("02"))
				retString = "��е��";
			if (data.equals("03"))
				retString = "���׶๦�ܱ�";
			if (data.equals("08"))
				retString = "�๦���ܱ�";
			if (data.equals("11"))
				retString = "������ӱ�";
			if (data.equals("10"))
				retString = "���׶๦�ܱ�";
			if (data.equals("09"))
				retString = "�๦���ܱ�";
		}
		if (aType.equals("MeterProtocolNote")) {
			if (data.equals("01"))
				retString = METERPROTOCOL_97;
			if (data.equals("02"))
				retString = METERPROTOCOL_97;
			if (data.equals("03"))
				retString = METERPROTOCOL_97;
			if (data.equals("08"))
				retString = METERPROTOCOL_97;
			if (data.equals("11"))
				retString = METERPROTOCOL_07;
			if (data.equals("10"))
				retString = METERPROTOCOL_07;
			if (data.equals("09"))
				retString = METERPROTOCOL_07;
		}
		
		if (aType.equals("Meter07_MeterType")) {
			if (data.equals("������ӱ�"))
				retString = "11";
			if (data.equals("��е��"))
				retString = "2";
			if (data.equals("���׶๦�ܱ�"))
				retString = "10";
			if (data.equals("�๦���ܱ�"))
				retString = "9";
		}
		if (aType.equals("Meter97_MeterType")) {
			if (data.equals("������ӱ�"))
				retString = "1";
			if (data.equals("��е��"))
				retString = "2";
			if (data.equals("���׶๦�ܱ�"))
				retString = "3";
			if (data.equals("�๦���ܱ�"))
				retString = "8";
		}
		
		if (aType.equals("FreezeTypeNote")) {
			if (data.equals("1"))
				retString = "��ǰ";
			if (data.equals("2"))
				retString = "�ն���";
			if (data.equals("3"))
				retString = "�¶���";
		}
		if (aType.equals("FreezeType")) {
			if (data.equals("��ǰ"))
				retString = "1";
			if (data.equals("�ն���"))
				retString = "2";
			if (data.equals("�¶���"))
				retString = "3";
		}		
		if (aType.equals("AFN=10H_Result")) {
			if (data.equals("00"))
				retString = "����ִ��ת��";
			if (data.equals("01"))
				retString = "ת�����ճ�ʱ";
			if (data.equals("02"))
				retString = "ת�����մ���";
			if (data.equals("03"))
				retString = "ת������ȷ��";
			if (data.equals("04"))
				retString = "ת�����շ���";
			if (data.equals("05"))
				retString = "ת����������";
		}		
		
		if (aType.equals("GDFunNote")) {
			retString = data; 
			if (data.equals("000001"))
				retString = "����ǰ����";
			if (data.equals("000111"))
				retString = "ʵʱд�������";
			if (data.equals("001000"))
				retString = "д�������";
			if (data.equals("010001"))
				retString = "������ʵʱ�ٲ�����";
			if (data.equals("010010"))
				retString = "�����������ճ��ۺ�����";
			if (data.equals("010011"))
				retString = "�����������ص㻧��������";
			if (data.equals("010100"))
				retString = "�������Ա�����բ����";
			if (data.equals("010101"))
				retString = "���������յ����";
			if (data.equals("010110"))
				retString = "���������ղ�������������";
		}		
		
		return retString;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
