package entity;

public class Constant {
	public final static String ARCHIVE_SOFT = "1"; // ����й���ĵ����
	public final static String ARCHIVE_TER = "2"; // �������й���ĵ����
	public final static String ARCHIVE_RT = "3"; // ·���й���ĵ����

	public final static String FLAG_INS = "0"; // ������־
	public final static String FLAG_UPD = "1"; // �޸ı�־
	public final static String FLAG_DEL = "2"; // ɾ����־

	public static String[] getProtocolType(){
		String[] data = { "δ֪(0)","DL/T645-1997(1)","DL/T645-2007(2)","DL/T698.45(3)","CJ/T188-2004(4)" };
		return data;
	};

	public static String[] getPort(){
		String[] data = { "�ز�/΢����(F2090201)", "RS485(F2010201)" };
		return data;
	};
	
	public static String[] getType2(){
		String[] data = { "δ֪(0)","����(1)","��������(2)","��������(3)" };
		return data;
	};
	
	public static String[] getPortRate(){
		String[] data = { "300bps(0)","600bps(1)","1200bps(2)",
			    "2400bps(3)","4800bps(4)","7200bps(5)",
			    "9600bps(6)","19200bps(7)","38400bps(8)",
			    "57600bps(9)","115200bps(10)","����Ӧ(255)" };
		return data;
	};
	
	public static String[] getTaskType(){
		String[] data = { "��ͨ�ɼ�����(1)","�¼��ɼ�����(2)","͸������(3)",
				"�ϱ�����(4)","�ű�����(5)","ʵʱ��ط���(6)" };
		return data;
	};
	
	public static String[] getTaskPriority(){
		String[] data = { "��Ҫ(1)","��Ҫ(2)","��Ҫ(3)","����(4)" };
		return data;
	};
	
	public static String[] getTaskState(){
		String[] data = { "����(1)","ͣ��(2)" };
		return data;
	};
	
	public static String[] getIntervalType(){
		String[] data = { "ǰ�պ�(0)","ǰ�����(1)","ǰ�պ��(2)","ǰ����(3)" };
		return data;
	};
		
	

}
