package socket;

// �ն�ͨ�Ŷ���
public class Channel {
	
	String addr = ""; // RemoteSocketAddress 
	String logAddr = ""; // �豸���߼���ַ
	String type = "0"; // 0=tcp,1=udp,2=com
	String ip = "";
	String port = "";  // tcpudp�˿ڻ�com�˿�
	String connectTime = "";
	String recvTime = "";
	int heatTime = 180;
	
	/*
		�������ӣ��߱��ˣ��ͻ���ip���ͻ���Port���ͻ���ͨ�Ŷ�������ʱ��
		���ӻ��д���ģʽ����·����

		��ʾһ�����ն˵�ַ�����ӣ������ж�����ն˵�ַ�����ӣ������ģ�ʹ��RemoteSocketAddress��Ϊ������
		
		�յ��˵�¼���ģ��������е��ն˵�ַ���������ڣ��߱��ˣ��ն˵�ַ�������ձ���ʱ�䡢��������
		�յ����������ģ�
		
		��ʾһ�����ն˵�ַ�����ӣ���ʽ�ģ�ʹ���ն˵�ַ��������
		
		�����������õ��ն˵�ַSA������ͨ�ŵ�ƾ��
		
		�����������õ������ַCA������ͨ�ŵ�ƾ��
	*/
	

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(String connectTime) {
		this.connectTime = connectTime;
	}

	public String getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}

	public int getHeatTime() {
		return heatTime;
	}

	public void setHeatTime(int heatTime) {
		this.heatTime = heatTime;
	}
	
	public String getLogAddr() {
		return logAddr;
	}

	public void setLogAddr(String logAddr) {
		this.logAddr = logAddr;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

}
