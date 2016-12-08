package frame;
import util.*;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;


public class Frame698 {
	String beginByte = "68";
	String endByte = "16";
	
	// 报文长度
	int frameL = 0;
	
	// 控制字对象  默认为 主站发出的应用层报文
	String controlData = "43";
	FrameControl frameControl = new FrameControl(controlData);
	
	// 地址对象	
	FrameAddr frameAddr = new FrameAddr();
	String addrData = "";

	// 帧头校验
	String HCSData = "";
	
	// 目前未考虑分帧情况 分帧回复时“block为最近一次收到正确的帧序号”
	APDU aPDU = new APDU();
	String userData = "";

	// 帧尾校验
	String FCSData = "";
	
	String allFrameData = "";
	
	public Frame698(){
	}
	
	public Frame698(String sa,String ca,String apdu){
		
	}

	public Frame698(String data){
		try {
			data = data.replaceAll(" ", "");
			
			allFrameData = data;
		
			frameL = DataConvert.hexString2Int(DataConvert.reverseString(data.substring(2,6)));

			controlData = data.substring(6,8);
			frameControl = new FrameControl(controlData);
			
			String af = data.substring(8,10);
			frameAddr = new FrameAddr();
			frameAddr.setAFFlag(af);
			int addrLen = frameAddr.getAddrLen();
			int addrEndPos = 10+addrLen*2; 
			String sa = DataConvert.reverseString(data.substring(10,addrEndPos));
			frameAddr.setSAData(sa);
			String ca = data.substring(addrEndPos,addrEndPos+2);
			frameAddr.setCAData(ca);

			HCSData = data.substring(addrEndPos+2,addrEndPos+6);
			
			//userData可能只是一部分数据
			userData = data.substring(addrEndPos+6,data.length()-6);
			if (frameControl.getPartFlag().equals("0")){
				aPDU = new APDU(userData);
			}
			
			FCSData = data.substring(data.length()-6,data.length()-2);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	// 根据此对象内容得到对应的报文字符串
	// 后续还会有分帧功能
	public String getFrame(){
		String ret = "";
		controlData = frameControl.getString();
		addrData = frameAddr.getString();

		// 得到用户数据 此部分功能较为复杂，可以是单个APDU，或分帧片段
		userData = aPDU.getString();
		
		build_len();
		build_HCSData();
		build_FCSData();
		
		ret = beginByte + DataConvert.reverseString(DataConvert.int2HexString(frameL, 4));
		ret = ret + controlData + addrData +HCSData + userData + FCSData + endByte;
		ret = ret.replaceAll(" ", "");
		ret = ret.replaceAll(",", "");
		ret = Util698.seprateString(ret, " ");
		return ret;
	}
	
	private void build_len() {
		// 有了用户数据，就可以计算len 
		// 变长部分加定长  定长  CS *2 + 1 c 
		
		/*
		起始字符（68H）   	--
		长度域L          2 
		控制域C			1
		地址域A			addrData
		帧头校验HCS		2
		链路用户数据		userData
		帧校验FCS			2
		结束字符（16H）	--
		*/
		
		//68 1E 00 81 05 11 11 11 11 11 11 00 9E 76 01 01 00 01 2C 07 E0 09 0D 02 00 1F 13 00 00 37 FB 16
		
		//68 
		//1E 00 
		//81 
		//05 11 11 11 11 11 11 00 
		//9E 76 
		//01 01 00 01 2C 07 E0 09 0D 02 00 1F 13 00 00 
		//37 FB 
		//16
		
		// addrData 和userData为了显示的美观，会添加一些空格和","，所以在计算实际长度时需要处理
		String a = addrData;
		a = a.replaceAll(" ", "");
		a = a.replaceAll(",", "");
		
		String u = userData;
		u = u.replaceAll(" ", "");
		u = u.replaceAll(",", "");

		frameL = a.length() /2 +  u.length()/2 + (2+1+2+2);
	}
	
	private String getHead(){
		return DataConvert.reverseString(DataConvert.int2HexString(frameL, 4)) +
				controlData + addrData;
	}
	
	private void build_HCSData() {
		// 有了L，就可以计算HCS
		String head = getHead();
		HCSData = DataConvert.reverseString(Util698.getCS(head));
	}
	private void build_FCSData() {
		String all = getHead() + HCSData + userData;
		FCSData = DataConvert.reverseString(Util698.getCS(all));
	}
	
	public APDU getAPDU() {
		return aPDU;
	}

	public void setAPDU(APDU aPDU) {
		this.aPDU = aPDU;
	}
	
	public String getControlData() {
		return controlData;
	}


	public void setControlData(String controlData) {
		frameControl = new FrameControl(controlData);
		this.controlData = controlData;
	}
	
	public FrameAddr getFrameAddr() {
		return frameAddr;
	}

	public void setFrameAddr(FrameAddr frameAddr) {
		this.frameAddr = frameAddr;
	}
	

	public void setCSAddr(String serverAddr,String clientAddr,int serverAddrType) {
		frameAddr.setCSAddr(serverAddr,clientAddr,serverAddrType);
	}
	
	
	
	
	public String getBeginByte() {
		return beginByte;
	}

	public void setBeginByte(String beginByte) {
		this.beginByte = beginByte;
	}

	public String getEndByte() {
		return endByte;
	}

	public void setEndByte(String endByte) {
		this.endByte = endByte;
	}

	public int getFrameL() {
		return frameL;
	}

	public void setFrameL(int frameL) {
		this.frameL = frameL;
	}

	public FrameControl getFrameControl() {
		return frameControl;
	}

	public void setFrameControl(FrameControl frameControl) {
		this.frameControl = frameControl;
	}

	public String getAddrData() {
		return addrData;
	}

	public void setAddrData(String addrData) {
		this.addrData = addrData;
	}

	public String getHCSData() {
		return HCSData;
	}

	public void setHCSData(String hCSData) {
		HCSData = hCSData;
	}

	public APDU getaPDU() {
		return aPDU;
	}

	public void setaPDU(APDU aPDU) {
		this.aPDU = aPDU;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getFCSData() {
		return FCSData;
	}

	public void setFCSData(String fCSData) {
		FCSData = fCSData;
	}

	public String getAllFrameData() {
		return allFrameData;
	}

	public void setAllFrameData(String allFrameData) {
		this.allFrameData = allFrameData;
	}

	public static String buildResponseFrame(Frame698 frame698){
		String recvTime = DateTimeFun.getDateTimeSSS();
		
		// 测试用例2 ，组织“建立连接”的响应报文
		Frame698 frame698_1 = new Frame698();
		// 1、主站回复链路
		frame698_1.setControlData("01");
		// 2、从前面的对象中得到终端地址
		frame698_1.setCSAddr(frame698.getFrameAddr().getSAData(),"10",0);
		// 3、组织apduData数据
		String apduData = "80";  // 这里的80表示结果可信
		// yyyy-mm-dd hh:mm:ss:sss -> yyyymmddhhmmssSSSS（Hex 2Byte）
		String reqTime = frame698.getAPDU().getChoiseData().split(";")[2].split("@")[1];
		String nowTime = DateTimeFun.getDateTimeSSS();
		apduData = apduData + Util698.NormalToBCDDateTime(reqTime)+
				Util698.NormalToBCDDateTime(recvTime)+
				Util698.NormalToBCDDateTime(nowTime);
		// 3.1、从前一报文中得到PIID信息 无ACD
		String piid = frame698.getAPDU().getPIIDnoACD();
		// 3.2 构造APDU对象
		APDU aPDU = new APDU(129,piid,apduData);
		frame698_1.setAPDU(aPDU);
		// 4、得到发送报文
		String send = frame698_1.getFrame();
//		System.out.println("buildFrame Resp=>"+send);
		String offi = "68 2D 00 01 05 07 09 19 05 16 20 10 CS CS 81 00 80 20 16 05 19 08 05 00 00 89 20 16 05 19 08 05 01 02 5F 20 16 05 19 08 05 02 02 DA CS CS 16";
		offi = offi.replaceAll(" ", "");
//		System.out.println("buildFrame Offi=>"+offi);
		
		return send;
	}
	
	private static String buildNewReqFrame(String terminalAddr, int frameNo, int APDU_type, String apduData){
		
		Frame698 frame698_1 = new Frame698();
		
		// 1、控制字	主站   0100011  = 23
		//01 00 011 = 23(Hex)
		//01:客户机发起的请求 00 03:用户数据
		
		//00 00 011 = 03(Hex)
		//00:客户机对服务器上报的响应 00 03:用户数据
		
		frame698_1.setControlData("23");
		
		// 2、地址信息  
		String softWareAddr = "10";  // 软件地址
		frame698_1.setCSAddr(terminalAddr,softWareAddr,0); //单地址
		
		// 3、组织apduData数据
		String piid = "00"+DataConvert.IntToBinString(frameNo, 6);
		piid = DataConvert.binStr2HexString(piid, 2);
		// 3.2 构造APDU对象

		
		APDU aPDU = new APDU(APDU_type,piid,apduData);
		frame698_1.setAPDU(aPDU);
		
		// 4、得到发送报文
		String send = frame698_1.getFrame();
		System.out.println("buildFrame Resp=>"+send);
		
		return send;
	}

	// 得到报文的文本解析信息
	private String getContext() {
		String ret = "";
		ret += "【报文】"+allFrameData +"\r\n"; 
		ret += "【报文解析】" +"\r\n"; 
		ret += "长度域L=>"+ frameL +"\r\n"; 
		ret += "控制域C=>"+ controlData +"\r\n";
		ret += "--------"+"传输方向位：DIR(0客户机发出;1服务器发出)=>"+ frameControl.getDIRFlag() +"\r\n";
		ret += "--------"+"启动标志位：PRM(1由客户机发起;0由服务器发起)=>"+ frameControl.getPRMFlag() +"\r\n";
		ret += "--------"+"分帧标志位(0完整;1分帧)=>"+ frameControl.getPartFlag() +"\r\n";
		ret += "--------"+"功能码(1链路管理;3用户数据)=>"+ frameControl.getFunData() +"\r\n";
		ret += "地址域A=>"+ addrData +"\r\n";
		ret += "--------"+"服务器地址字节数=>"+ frameAddr.getAddrLen() +"\r\n";
		ret += "--------"+"服务器地址类别(0单地址;1通配地址;2组地址;3广播地址)=>"+ frameAddr.getAddrType() +"\r\n";
		ret += "--------"+"服务器地址=>"+ frameAddr.getSAData() +"\r\n";
		ret += "--------"+"客户机地址=>"+ frameAddr.getCAData() +"\r\n";
		ret += "帧头校验HCS=>"+HCSData +"\r\n";
		ret += "链路用户数据=>"+userData +"\r\n";
		ret += "--------"+"APDU类型("+new AnalyData().getAPDUType(aPDU.getChoiseFlag())+")=>"+ aPDU.getChoiseFlag() +"\r\n";
		ret += "--------"+"APDU子类型=>"+ aPDU.getChoiseFlag_1() +"\r\n";
		ret += "--------"+"PIID-ACD=>"+ aPDU.getPIID() +"\r\n";
		ret += "--------"+"--------"+"服务优先级(0一般;1高级)=>"+ aPDU.getPIID_PRI() +"\r\n";
		ret += "--------"+"--------"+"请求访问ACD(0不请求;1请求)=>"+ aPDU.getPIID_ACD() +"\r\n";
		ret += "--------"+"--------"+"服务序号=>"+ aPDU.getPIID_NO() +"\r\n";
		ret += "--------"+"其他数据=>"+ aPDU.getNextData() +"\r\n";
		ret += "--------"+"其他数据解析=>"+ aPDU.getChoiseData() +"\r\n";
		ret += "帧校验FCS=>"+FCSData +"\r\n";
		
		return ret;
		// 组织报文所需参数：终端地址（服务器地址）、 方向：软件下发\终端回复、操作类型：读取5\设置6\操作7
		//操作个数：读取=单个1、多个2、单个记录型3、多个记录型4、数据块5
		//操作个数：设置=单个1、多个2、设置后读取多个3
		//操作个数：操作=单个1、多个2、操作后读取多个3
		
		//软件下发
	}
	
	public String[] getTree() {
		
		
		// 用于组件各个部分数据
		getFrame();
		
		
		String[] ret = {"",""};
		TreeNode analy_frame = new TreeNode("整体报文",allFrameData);
		
		TreeNode analy_dataLen = new TreeNode("长度域L",frameL);
		analy_frame.addChild(analy_dataLen);
		
		TreeNode analy_control = new TreeNode("控制域C",controlData);
		analy_frame.addChild(analy_control);
		
		TreeNode analy_control_dir = new TreeNode("传输方向位：DIR(0客户机发出;1服务器发出)",frameControl.getDIRFlag());
		TreeNode analy_control_prm = new TreeNode("启动标志位：PRM(1由客户机发起;0由服务器发起)",frameControl.getPRMFlag());
		TreeNode analy_control_part = new TreeNode("分帧标志位(0完整;1分帧)",frameControl.getPartFlag());
		TreeNode analy_control_fun = new TreeNode("功能码(1链路管理;3用户数据)",frameControl.getFunData());
		analy_control.addChild(analy_control_dir);
		analy_control.addChild(analy_control_prm);
		analy_control.addChild(analy_control_part);
		analy_control.addChild(analy_control_fun);
		
		TreeNode analy_addr = new TreeNode("地址域A",addrData);
		analy_frame.addChild(analy_addr);
		
		TreeNode analy_addr_len = new TreeNode("服务器地址字节数",frameAddr.getAddrLen());
		TreeNode analy_addr_type = new TreeNode("服务器地址类别(0单地址;1通配地址;2组地址;3广播地址)",frameAddr.getAddrType());
		TreeNode analy_addr_sa = new TreeNode("服务器地址",frameAddr.getSAData());
		TreeNode analy_addr_ca = new TreeNode("客户机地址",frameAddr.getCAData());
		analy_addr.addChild(analy_addr_len);
		analy_addr.addChild(analy_addr_type);
		analy_addr.addChild(analy_addr_sa);
		analy_addr.addChild(analy_addr_ca);
		
		TreeNode analy_hcs = new TreeNode("帧头校验HCS",HCSData);
		analy_frame.addChild(analy_hcs);

		TreeNode analy_userdata = new TreeNode("链路报文",userData);
		analy_frame.addChild(analy_userdata);
		
		TreeNode analy_userdata_apdutype = new TreeNode("APDU类型("+new AnalyData().getAPDUType(aPDU.getChoiseFlag())+")",aPDU.getChoiseFlag());
		TreeNode analy_userdata_apdutype1 = new TreeNode("APDU子类型("+new AnalyData().getAPDUType1(aPDU.getChoiseFlag(),aPDU.getChoiseFlag_1())+")",aPDU.getChoiseFlag_1());
		analy_userdata.addChild(analy_userdata_apdutype);
		analy_userdata.addChild(analy_userdata_apdutype1);
		
		TreeNode analy_userdata_piidacd = new TreeNode("PIID-ACD",aPDU.getPIID());
		analy_userdata.addChild(analy_userdata_piidacd);
		
		TreeNode analy_userdata_piidacd_pri = new TreeNode("服务优先级(0一般;1高级)",aPDU.getPIID_PRI());
		TreeNode analy_userdata_piidacd_acd = new TreeNode("请求访问ACD(0不请求;1请求)",aPDU.getPIID_ACD());
		TreeNode analy_userdata_piidacd_no = new TreeNode("服务序号",aPDU.getPIID_NO());
		analy_userdata_piidacd.addChild(analy_userdata_piidacd_pri);
		analy_userdata_piidacd.addChild(analy_userdata_piidacd_acd);
		analy_userdata_piidacd.addChild(analy_userdata_piidacd_no);

		
		// 报文数据与APDU数据分开展示
		// 业务数据nextData
		//analy_userdata.addChield(aPDU.dataNode);
		
		TreeNode analy_fcs = new TreeNode("帧校验FCS",FCSData);
		analy_frame.addChild(analy_fcs);


		
		TreeNode analy_userdata_next = new TreeNode("业务数据",aPDU.getNextData());
		analy_userdata.addChild(analy_userdata_next);
		
//		analy_userdata_apdutype.addChild(analy_userdata_apdutype);
		
//		System.out.println(analy_frame.loop(0));
//		System.out.println("Json-整体报文:\r\n"+analy_frame.convetToString());		
		ret[0] = analy_frame.loop(0);
		
		analy_userdata.addChild(aPDU.dataNode);
//		System.out.println(analy_userdata.loop(0));
//		System.out.println("Json数据-链路报文:\r\n"+analy_userdata.convetToString());		
		
		// xuky 2016.10.31 仅仅展示业务数据
		//ret[1] = analy_userdata.loop(0);
		String msg = new AnalyData().getAPDUType(aPDU.getChoiseFlag());
		TreeNode data = aPDU.dataNode;
		data.setKey(msg+"-"+data.getKey());
				
		ret[1] = aPDU.dataNode.loop(0);
		
		//TreeNode node = new TreeNode(jsonData);
		
		//System.out.println("Json数据:\r\n"+node.convetToString());
		

		return ret;
	}
	
	// 123
	private String getJSon() {
	    //JSON 规则：数据在键值对中"",""\数据由逗号分隔,\花括号保存对象{}\方括号保存数组[]
		String ret = "{";
		ret += "\"报文\":\""+allFrameData +"\"\r\n"; 
		ret += ",\"报文解析\":{\r\n";
		
		ret += "\"长度域L\":\""+ frameL +"\"\r\n"; 
		ret += ",\"控制域C\":\""+ controlData +"\"\r\n"; 
		
		ret += "}}\r\n";
		
		ret += "--------"+"传输方向位：DIR(0客户机发出;1服务器发出)=>"+ frameControl.getDIRFlag() +"\r\n";
		ret += "--------"+"启动标志位：PRM(1由客户机发起;0由服务器发起)=>"+ frameControl.getPRMFlag() +"\r\n";
		ret += "--------"+"分帧标志位(0完整;1分帧)=>"+ frameControl.getPartFlag() +"\r\n";
		ret += "--------"+"功能码(1链路管理;3用户数据)=>"+ frameControl.getFunData() +"\r\n";
		ret += "地址域A=>"+ addrData +"\r\n";
		ret += "--------"+"服务器地址字节数=>"+ frameAddr.getAddrLen() +"\r\n";
		ret += "--------"+"服务器地址类别(0单地址;1通配地址;2组地址;3广播地址)=>"+ frameAddr.getAddrType() +"\r\n";
		ret += "--------"+"服务器地址=>"+ frameAddr.getSAData() +"\r\n";
		ret += "--------"+"客户机地址=>"+ frameAddr.getCAData() +"\r\n";
		ret += "帧头校验HCS=>"+HCSData +"\r\n";
		ret += "链路用户数据=>"+userData +"\r\n";
		ret += "--------"+"APDU类型("+new AnalyData().getAPDUType(aPDU.getChoiseFlag())+")=>"+ aPDU.getChoiseFlag() +"\r\n";
		ret += "--------"+"APDU子类型=>"+ aPDU.getChoiseFlag_1() +"\r\n";
		ret += "--------"+"PIID-ACD=>"+ aPDU.getPIID() +"\r\n";
		ret += "--------"+"--------"+"服务优先级(0一般;1高级)=>"+ aPDU.getPIID_PRI() +"\r\n";
		ret += "--------"+"--------"+"请求访问ACD(0不请求;1请求)=>"+ aPDU.getPIID_ACD() +"\r\n";
		ret += "--------"+"--------"+"服务序号=>"+ aPDU.getPIID_NO() +"\r\n";
		ret += "--------"+"其他数据=>"+ aPDU.getNextData() +"\r\n";
		ret += "--------"+"其他数据解析=>"+ aPDU.getChoiseData() +"\r\n";
		ret += "帧校验FCS=>"+FCSData +"\r\n";
		//123
		
		return ret;
		// 组织报文所需参数：终端地址（服务器地址）、 方向：软件下发\终端回复、操作类型：读取5\设置6\操作7
		//操作个数：读取=单个1、多个2、单个记录型3、多个记录型4、数据块5
		//操作个数：设置=单个1、多个2、设置后读取多个3
		//操作个数：操作=单个1、多个2、操作后读取多个3
		
		//软件下发
	}
	
	public static void main(String[] args) {

		
		// 测试用例4 ，建立应用连接报文
		// 除PIID以外的数据
		/*
		String terminalAddr = "123456789"; 
		String apduData = "00 10 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 04 00 04 00 01 04 00 00 00 00 64 00 00";
		int frameNo = 23; //0~63报文序号
		int APDU_type = 2; //2=建立应用连接请求;5=读取请求;6=设置请求;7=操作请求
		buildNewReqFrame(terminalAddr,frameNo,APDU_type,apduData);
		*/
		
		String recv = "",send,recvTime;
		Frame698 frame698 = new Frame698();
		
		//frame698.getFrameAddr().setSAData("00 00 00 00 00 02");
		//frame698.getAPDU().init("05 01 00 40 01 02 00 00 ");
		
		//String s = frame698.getFrame();
		//recv = s;
		
		
		//681700430502000000000010485E0501004001020000ED0316
		//68 17 00 43 05 02 00 00 00 00 00 10 5E 48 05 01 00 40 01 02 00 00 62 E6 16
		
		// 客户端  发起  地址=000000000002 APDU=0501004001020000
		
		// 链路数据
		//recv = "68 1D 00 81 05 07 09 19 05 16 20 00 CS CS 01 ED 00 00 B4 20 16 05 19 08 05 00 00 A4 CS CS 16";
		
		// 读 单OAD
		//recv= "681700430502000000000010485E0501004001020000ED0316";
		
		
		// 读  多OAD
		//recv= "681700430502000000000010485E05020202200002002001020000ED0316";
		// 写 单OAD
		//recv ="68 1B 00 43 05 02 00 00 00 00 00 10 B7 BC 06 01 00 40 02 02 00 09 02 01 23 00 14 1A 16 ";
		String apdu = "";
		
		// 写 单OAD
		//apdu = "06 01 02 40 00 02 00 1C 20 16 01 20 16 27 11 00";
		
		// 写 单OAD 响应
		//apdu = "86 01 02 40 00 02 00 00 00 00";
		
		// 写 多OAD
		//apdu = "06 02 03 02 40 01 02 00 55 06 00 00 00 00 00 01 40 00 02 00 1C 20 16 01 20 16 27 11 00";
		
		// 写 多OAD 响应
		//apdu = "86 02 03 02 40 01 02 00 00 40 00 02 00 00 00 00";
		
		// 读 单OAD
		//apdu = "05 01 01 40 01 02 00 00";
		
		// 读 单OAD 响应
		//apdu = "85 01 01 40 01 02 00 01 55 06 12 34 56 78 90 12 00 00";
		
		// 读 多OAD
		//apdu = "05 02 02 02 20 00 02 00 20 01 02 00 00";
		
		// 读 多OAD 响应
		//apdu = "85 02 02 02 20 00 02 00 01 01 03 12 09 6D 12 09 6D 12 09 6D 20 01 02 00 01 01 03 05 00 00 03 E8 05 00 00 03 E8 05 00 00 03 E8 00 00";
		
		
		// 读单Record 
		//apdu = "05 03 03 50 04 02 00 01 20 21 02 00 1C 20 16 01 20 00 00 00 02 00 20 21 02 00 00 00 10 02 00 00";
		
		// 读多Record 
		//apdu = "05 04 03 "+"02"+"50 04 02 00 01 20 21 02 00 1C 20 16 01 20 00 00 00 02 00 20 21 02 00 00 00 10 02 00 "+
		//		"50 04 02 00 01 20 21 02 00 1C 20 16 01 20 00 00 00 02 00 20 21 02 00 00 00 10 02 00 00";
		
		// 读单Record 响应 
		apdu = "85 03 03 50 04 02 00 02 00 20 21 02 00 00 00 10 02 00 01 01 1C 20 16 01 20 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 00 00";
		
		// 读单Record 复杂 多表 日冻结 多数据项 
		//apdu = "05 03 04 60 12 03 00 05 20 16 01 20 00 00 00 03 05 06 04 00 00 00 01 21 06 04 00 00 00 01 22 06 04 00 00 00 01 23 06 04 00 00 00 01 24 06 04 00 00 00 01 25 05 00 40 01 02 00 00 60 40 02 00 00 60 41 02 00 00 60 42 02 00 01 50 04 02 00 02 00 10 02 00 00 20 02 00 00";
		
		// 读单Record 复杂 多表 日冻结 多数据项 响应 
		//apdu = "85 03 04 60 12 03 00 05 00 40 01 02 00 00 60 40 02 00 00 60 41 02 00 00 60 42 02 00 01 50 04 02 00 02 00 10 02 00 00 20 02 00 01 05 09 05 00 00 00 01 21 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 22 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 23 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 24 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 25 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 00 00";

		// 分帧读 响应  
		//apdu = "85 05 08 00 00 01 01 01 60 00 02 00 01 01 03 02 04 12 00 01 02 08 55 06 04 00 00 00 22 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 08 55 06 04 00 00 00 22 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 08 55 06 04 00 00 00 22 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 00 00";
		
		//apdu = "85 05 08 00 00 01 01 02 60 00 02 00 01 01 03 02 04 12 00 01 02 08 55 06 04 00 00 00 22 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 08 55 06 04 00 00 00 22 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 08 55 06 04 00 00 00 22 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 " + 
		//"60 00 02 00 01 01 03 02 04 12 00 01 02 08 55 06 04 00 00 00 22 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 08 55 06 04 00 00 00 22 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 08 55 06 04 00 00 00 22 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 00 00";
		
		// 设置后 读取
		//apdu = "06 03 04 01 45 00 02 0B 12 01 68 45 00 02 0B 03 00";
		
		// 设置后 读取 响应
		//apdu = "86 03 04 01 45 00 02 0B 00 45 00 02 0B 01 12 01 68 00 00";
		
		// 操作 单
		//apdu = "07 01 05 00 10 01 00 0F 00 00";
		
		// 操作 单 响应
		//apdu = "87 01 05 00 10 01 00 00 00 00 00";

		// 操作 多  后读
		//apdu = "07 03 07 01 00 10 01 00 0F 00 00 10 02 00 00 00";

		// 操作 多  后读  响应
		//apdu = "87 03 07 01 00 10 01 00 00 00 00 10 02 00 01 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 00 00";
		
		// 操作 单  添加普通采集方案  采集当前数据
		//apdu = "07 01 05 60 14 7F 00 01 01 02 06 11 01 12 00 01 02 02 11 00 00 01 04 5B 00 00 10 02 00 5B 00 00 20 02 00 5B 00 20 00 02 00 5B 00 20 01 02 00 5C 01 00 16 01 00";

		// 操作 单  添加普通采集方案  采集当前数据  响应
		//apdu = "87 01 05 60 14 7F 00 00 00 00 00";
		
		// 操作 单  添加普通采集方案  采集冻结数据
		//apdu = "07 01 05 60 14 7F 00 01 01 02 06 11 01 12 00 20 02 02 11 02 00  01  01 5B 01 50 04 02 00 04 00 10 02 00 00 20 02 00 00 30 02 00 00 40 02 00 5C 01 00 16 02 00";
		
		String begin = "68 1B 00 43 05 02 00 00 00 00 00 10 B7 BC";
		String end = "14 1A 16";
		recv = begin + apdu + end;
		
		//recv ="681E0043050200000000001073B70101000000B42016051908050000A4986316";
		
		
		// 写 多OAD
		//recv ="68 1B 00 43 05 02 00 00 00 00 00 10 B7 BC 06 01 00 40 02 02 00 09 02 01 23 00 14 1A 16 ";
		frame698 = new Frame698(recv);
		//System.out.println(frame698.getJSon());
		frame698.getTree();
		//System.out.println();
		

		// 测试用例1 ，解析建立连接报文
		//recv = "68 1D 00 81 05 07 09 19 05 16 20 00 CS CS 01 ED 00 00 B4 20 16 05 19 08 05 00 00 A4 CS CS 16";
		//frame698 = new Frame698(recv);
		//System.out.println(frame698.getContext());		
		
		/*
		frame698.getFrameAddr().setSAData("12345678");
		send = frame698.getFrame();
		System.out.println("Send=>"+send);
		System.out.println("choiseData=>"+frame698.getAPDU().getChoiseData());
		recvTime = DateTimeFun.getDateTimeSSS();

		
		buildResponseFrame(frame698);

		
		
		 
		// 测试用例3 ，解析心跳报文
		recv = "68 1D 00 81 05 07 09 19 05 16 20 00 CS CS 01 01 01 00 B4 20 16 05 19 08 05 00 01 C3 CS CS 16";
		recv = recv.replaceAll(" ", "");
		System.out.println("Recv=>"+recv);
		frame698 = new Frame698(recv);
		send = frame698.getFrame();
		System.out.println("Send=>"+send);
		System.out.println("choiseData=>"+frame698.getAPDU().getChoiseData());
		recvTime = DateTimeFun.getDateTimeSSS();
		*/
	}

	

}
