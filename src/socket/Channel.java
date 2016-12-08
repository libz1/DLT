package socket;

// 终端通信对象
public class Channel {
	
	String addr = ""; // RemoteSocketAddress 
	String logAddr = ""; // 设备的逻辑地址
	String type = "0"; // 0=tcp,1=udp,2=com
	String ip = "";
	String port = "";  // tcpudp端口或com端口
	String connectTime = "";
	String recvTime = "";
	int heatTime = 180;
	
	/*
		建立连接，具备了：客户端ip、客户端Port、客户端通信对象、连接时间
		连接还有串口模式、多路串口

		显示一个无终端地址的连接，可能有多个无终端地址的连接（隐含的，使用RemoteSocketAddress作为索引）
		
		收到了登录报文，解析其中的终端地址、心跳周期，具备了：终端地址、最后接收报文时间、心跳周期
		收到了心跳报文，
		
		显示一个有终端地址的连接（正式的，使用终端地址做索引）
		
		软件必须解析得到终端地址SA，进行通信的凭据
		
		软件必须解析得到软件地址CA，进行通信的凭据
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
