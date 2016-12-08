package frame;
import util.*;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;


public class Frame698 {
	String beginByte = "68";
	String endByte = "16";
	
	// ���ĳ���
	int frameL = 0;
	
	// �����ֶ���  Ĭ��Ϊ ��վ������Ӧ�ò㱨��
	String controlData = "43";
	FrameControl frameControl = new FrameControl(controlData);
	
	// ��ַ����	
	FrameAddr frameAddr = new FrameAddr();
	String addrData = "";

	// ֡ͷУ��
	String HCSData = "";
	
	// Ŀǰδ���Ƿ�֡��� ��֡�ظ�ʱ��blockΪ���һ���յ���ȷ��֡��š�
	APDU aPDU = new APDU();
	String userData = "";

	// ֡βУ��
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
			
			//userData����ֻ��һ��������
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
	
	
	// ���ݴ˶������ݵõ���Ӧ�ı����ַ���
	// ���������з�֡����
	public String getFrame(){
		String ret = "";
		controlData = frameControl.getString();
		addrData = frameAddr.getString();

		// �õ��û����� �˲��ֹ��ܽ�Ϊ���ӣ������ǵ���APDU�����֡Ƭ��
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
		// �����û����ݣ��Ϳ��Լ���len 
		// �䳤���ּӶ���  ����  CS *2 + 1 c 
		
		/*
		��ʼ�ַ���68H��   	--
		������L          2 
		������C			1
		��ַ��A			addrData
		֡ͷУ��HCS		2
		��·�û�����		userData
		֡У��FCS			2
		�����ַ���16H��	--
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
		
		// addrData ��userDataΪ����ʾ�����ۣ������һЩ�ո��","�������ڼ���ʵ�ʳ���ʱ��Ҫ����
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
		// ����L���Ϳ��Լ���HCS
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
		
		// ��������2 ����֯���������ӡ�����Ӧ����
		Frame698 frame698_1 = new Frame698();
		// 1����վ�ظ���·
		frame698_1.setControlData("01");
		// 2����ǰ��Ķ����еõ��ն˵�ַ
		frame698_1.setCSAddr(frame698.getFrameAddr().getSAData(),"10",0);
		// 3����֯apduData����
		String apduData = "80";  // �����80��ʾ�������
		// yyyy-mm-dd hh:mm:ss:sss -> yyyymmddhhmmssSSSS��Hex 2Byte��
		String reqTime = frame698.getAPDU().getChoiseData().split(";")[2].split("@")[1];
		String nowTime = DateTimeFun.getDateTimeSSS();
		apduData = apduData + Util698.NormalToBCDDateTime(reqTime)+
				Util698.NormalToBCDDateTime(recvTime)+
				Util698.NormalToBCDDateTime(nowTime);
		// 3.1����ǰһ�����еõ�PIID��Ϣ ��ACD
		String piid = frame698.getAPDU().getPIIDnoACD();
		// 3.2 ����APDU����
		APDU aPDU = new APDU(129,piid,apduData);
		frame698_1.setAPDU(aPDU);
		// 4���õ����ͱ���
		String send = frame698_1.getFrame();
//		System.out.println("buildFrame Resp=>"+send);
		String offi = "68 2D 00 01 05 07 09 19 05 16 20 10 CS CS 81 00 80 20 16 05 19 08 05 00 00 89 20 16 05 19 08 05 01 02 5F 20 16 05 19 08 05 02 02 DA CS CS 16";
		offi = offi.replaceAll(" ", "");
//		System.out.println("buildFrame Offi=>"+offi);
		
		return send;
	}
	
	private static String buildNewReqFrame(String terminalAddr, int frameNo, int APDU_type, String apduData){
		
		Frame698 frame698_1 = new Frame698();
		
		// 1��������	��վ   0100011  = 23
		//01 00 011 = 23(Hex)
		//01:�ͻ������������ 00 03:�û�����
		
		//00 00 011 = 03(Hex)
		//00:�ͻ����Է������ϱ�����Ӧ 00 03:�û�����
		
		frame698_1.setControlData("23");
		
		// 2����ַ��Ϣ  
		String softWareAddr = "10";  // �����ַ
		frame698_1.setCSAddr(terminalAddr,softWareAddr,0); //����ַ
		
		// 3����֯apduData����
		String piid = "00"+DataConvert.IntToBinString(frameNo, 6);
		piid = DataConvert.binStr2HexString(piid, 2);
		// 3.2 ����APDU����

		
		APDU aPDU = new APDU(APDU_type,piid,apduData);
		frame698_1.setAPDU(aPDU);
		
		// 4���õ����ͱ���
		String send = frame698_1.getFrame();
		System.out.println("buildFrame Resp=>"+send);
		
		return send;
	}

	// �õ����ĵ��ı�������Ϣ
	private String getContext() {
		String ret = "";
		ret += "�����ġ�"+allFrameData +"\r\n"; 
		ret += "�����Ľ�����" +"\r\n"; 
		ret += "������L=>"+ frameL +"\r\n"; 
		ret += "������C=>"+ controlData +"\r\n";
		ret += "--------"+"���䷽��λ��DIR(0�ͻ�������;1����������)=>"+ frameControl.getDIRFlag() +"\r\n";
		ret += "--------"+"������־λ��PRM(1�ɿͻ�������;0�ɷ���������)=>"+ frameControl.getPRMFlag() +"\r\n";
		ret += "--------"+"��֡��־λ(0����;1��֡)=>"+ frameControl.getPartFlag() +"\r\n";
		ret += "--------"+"������(1��·����;3�û�����)=>"+ frameControl.getFunData() +"\r\n";
		ret += "��ַ��A=>"+ addrData +"\r\n";
		ret += "--------"+"��������ַ�ֽ���=>"+ frameAddr.getAddrLen() +"\r\n";
		ret += "--------"+"��������ַ���(0����ַ;1ͨ���ַ;2���ַ;3�㲥��ַ)=>"+ frameAddr.getAddrType() +"\r\n";
		ret += "--------"+"��������ַ=>"+ frameAddr.getSAData() +"\r\n";
		ret += "--------"+"�ͻ�����ַ=>"+ frameAddr.getCAData() +"\r\n";
		ret += "֡ͷУ��HCS=>"+HCSData +"\r\n";
		ret += "��·�û�����=>"+userData +"\r\n";
		ret += "--------"+"APDU����("+new AnalyData().getAPDUType(aPDU.getChoiseFlag())+")=>"+ aPDU.getChoiseFlag() +"\r\n";
		ret += "--------"+"APDU������=>"+ aPDU.getChoiseFlag_1() +"\r\n";
		ret += "--------"+"PIID-ACD=>"+ aPDU.getPIID() +"\r\n";
		ret += "--------"+"--------"+"�������ȼ�(0һ��;1�߼�)=>"+ aPDU.getPIID_PRI() +"\r\n";
		ret += "--------"+"--------"+"�������ACD(0������;1����)=>"+ aPDU.getPIID_ACD() +"\r\n";
		ret += "--------"+"--------"+"�������=>"+ aPDU.getPIID_NO() +"\r\n";
		ret += "--------"+"��������=>"+ aPDU.getNextData() +"\r\n";
		ret += "--------"+"�������ݽ���=>"+ aPDU.getChoiseData() +"\r\n";
		ret += "֡У��FCS=>"+FCSData +"\r\n";
		
		return ret;
		// ��֯��������������ն˵�ַ����������ַ���� ��������·�\�ն˻ظ����������ͣ���ȡ5\����6\����7
		//������������ȡ=����1�����2��������¼��3�������¼��4�����ݿ�5
		//��������������=����1�����2�����ú��ȡ���3
		//��������������=����1�����2���������ȡ���3
		
		//����·�
	}
	
	public String[] getTree() {
		
		
		// �������������������
		getFrame();
		
		
		String[] ret = {"",""};
		TreeNode analy_frame = new TreeNode("���屨��",allFrameData);
		
		TreeNode analy_dataLen = new TreeNode("������L",frameL);
		analy_frame.addChild(analy_dataLen);
		
		TreeNode analy_control = new TreeNode("������C",controlData);
		analy_frame.addChild(analy_control);
		
		TreeNode analy_control_dir = new TreeNode("���䷽��λ��DIR(0�ͻ�������;1����������)",frameControl.getDIRFlag());
		TreeNode analy_control_prm = new TreeNode("������־λ��PRM(1�ɿͻ�������;0�ɷ���������)",frameControl.getPRMFlag());
		TreeNode analy_control_part = new TreeNode("��֡��־λ(0����;1��֡)",frameControl.getPartFlag());
		TreeNode analy_control_fun = new TreeNode("������(1��·����;3�û�����)",frameControl.getFunData());
		analy_control.addChild(analy_control_dir);
		analy_control.addChild(analy_control_prm);
		analy_control.addChild(analy_control_part);
		analy_control.addChild(analy_control_fun);
		
		TreeNode analy_addr = new TreeNode("��ַ��A",addrData);
		analy_frame.addChild(analy_addr);
		
		TreeNode analy_addr_len = new TreeNode("��������ַ�ֽ���",frameAddr.getAddrLen());
		TreeNode analy_addr_type = new TreeNode("��������ַ���(0����ַ;1ͨ���ַ;2���ַ;3�㲥��ַ)",frameAddr.getAddrType());
		TreeNode analy_addr_sa = new TreeNode("��������ַ",frameAddr.getSAData());
		TreeNode analy_addr_ca = new TreeNode("�ͻ�����ַ",frameAddr.getCAData());
		analy_addr.addChild(analy_addr_len);
		analy_addr.addChild(analy_addr_type);
		analy_addr.addChild(analy_addr_sa);
		analy_addr.addChild(analy_addr_ca);
		
		TreeNode analy_hcs = new TreeNode("֡ͷУ��HCS",HCSData);
		analy_frame.addChild(analy_hcs);

		TreeNode analy_userdata = new TreeNode("��·����",userData);
		analy_frame.addChild(analy_userdata);
		
		TreeNode analy_userdata_apdutype = new TreeNode("APDU����("+new AnalyData().getAPDUType(aPDU.getChoiseFlag())+")",aPDU.getChoiseFlag());
		TreeNode analy_userdata_apdutype1 = new TreeNode("APDU������("+new AnalyData().getAPDUType1(aPDU.getChoiseFlag(),aPDU.getChoiseFlag_1())+")",aPDU.getChoiseFlag_1());
		analy_userdata.addChild(analy_userdata_apdutype);
		analy_userdata.addChild(analy_userdata_apdutype1);
		
		TreeNode analy_userdata_piidacd = new TreeNode("PIID-ACD",aPDU.getPIID());
		analy_userdata.addChild(analy_userdata_piidacd);
		
		TreeNode analy_userdata_piidacd_pri = new TreeNode("�������ȼ�(0һ��;1�߼�)",aPDU.getPIID_PRI());
		TreeNode analy_userdata_piidacd_acd = new TreeNode("�������ACD(0������;1����)",aPDU.getPIID_ACD());
		TreeNode analy_userdata_piidacd_no = new TreeNode("�������",aPDU.getPIID_NO());
		analy_userdata_piidacd.addChild(analy_userdata_piidacd_pri);
		analy_userdata_piidacd.addChild(analy_userdata_piidacd_acd);
		analy_userdata_piidacd.addChild(analy_userdata_piidacd_no);

		
		// ����������APDU���ݷֿ�չʾ
		// ҵ������nextData
		//analy_userdata.addChield(aPDU.dataNode);
		
		TreeNode analy_fcs = new TreeNode("֡У��FCS",FCSData);
		analy_frame.addChild(analy_fcs);


		
		TreeNode analy_userdata_next = new TreeNode("ҵ������",aPDU.getNextData());
		analy_userdata.addChild(analy_userdata_next);
		
//		analy_userdata_apdutype.addChild(analy_userdata_apdutype);
		
//		System.out.println(analy_frame.loop(0));
//		System.out.println("Json-���屨��:\r\n"+analy_frame.convetToString());		
		ret[0] = analy_frame.loop(0);
		
		analy_userdata.addChild(aPDU.dataNode);
//		System.out.println(analy_userdata.loop(0));
//		System.out.println("Json����-��·����:\r\n"+analy_userdata.convetToString());		
		
		// xuky 2016.10.31 ����չʾҵ������
		//ret[1] = analy_userdata.loop(0);
		String msg = new AnalyData().getAPDUType(aPDU.getChoiseFlag());
		TreeNode data = aPDU.dataNode;
		data.setKey(msg+"-"+data.getKey());
				
		ret[1] = aPDU.dataNode.loop(0);
		
		//TreeNode node = new TreeNode(jsonData);
		
		//System.out.println("Json����:\r\n"+node.convetToString());
		

		return ret;
	}
	
	// 123
	private String getJSon() {
	    //JSON ���������ڼ�ֵ����"",""\�����ɶ��ŷָ�,\�����ű������{}\�����ű�������[]
		String ret = "{";
		ret += "\"����\":\""+allFrameData +"\"\r\n"; 
		ret += ",\"���Ľ���\":{\r\n";
		
		ret += "\"������L\":\""+ frameL +"\"\r\n"; 
		ret += ",\"������C\":\""+ controlData +"\"\r\n"; 
		
		ret += "}}\r\n";
		
		ret += "--------"+"���䷽��λ��DIR(0�ͻ�������;1����������)=>"+ frameControl.getDIRFlag() +"\r\n";
		ret += "--------"+"������־λ��PRM(1�ɿͻ�������;0�ɷ���������)=>"+ frameControl.getPRMFlag() +"\r\n";
		ret += "--------"+"��֡��־λ(0����;1��֡)=>"+ frameControl.getPartFlag() +"\r\n";
		ret += "--------"+"������(1��·����;3�û�����)=>"+ frameControl.getFunData() +"\r\n";
		ret += "��ַ��A=>"+ addrData +"\r\n";
		ret += "--------"+"��������ַ�ֽ���=>"+ frameAddr.getAddrLen() +"\r\n";
		ret += "--------"+"��������ַ���(0����ַ;1ͨ���ַ;2���ַ;3�㲥��ַ)=>"+ frameAddr.getAddrType() +"\r\n";
		ret += "--------"+"��������ַ=>"+ frameAddr.getSAData() +"\r\n";
		ret += "--------"+"�ͻ�����ַ=>"+ frameAddr.getCAData() +"\r\n";
		ret += "֡ͷУ��HCS=>"+HCSData +"\r\n";
		ret += "��·�û�����=>"+userData +"\r\n";
		ret += "--------"+"APDU����("+new AnalyData().getAPDUType(aPDU.getChoiseFlag())+")=>"+ aPDU.getChoiseFlag() +"\r\n";
		ret += "--------"+"APDU������=>"+ aPDU.getChoiseFlag_1() +"\r\n";
		ret += "--------"+"PIID-ACD=>"+ aPDU.getPIID() +"\r\n";
		ret += "--------"+"--------"+"�������ȼ�(0һ��;1�߼�)=>"+ aPDU.getPIID_PRI() +"\r\n";
		ret += "--------"+"--------"+"�������ACD(0������;1����)=>"+ aPDU.getPIID_ACD() +"\r\n";
		ret += "--------"+"--------"+"�������=>"+ aPDU.getPIID_NO() +"\r\n";
		ret += "--------"+"��������=>"+ aPDU.getNextData() +"\r\n";
		ret += "--------"+"�������ݽ���=>"+ aPDU.getChoiseData() +"\r\n";
		ret += "֡У��FCS=>"+FCSData +"\r\n";
		//123
		
		return ret;
		// ��֯��������������ն˵�ַ����������ַ���� ��������·�\�ն˻ظ����������ͣ���ȡ5\����6\����7
		//������������ȡ=����1�����2��������¼��3�������¼��4�����ݿ�5
		//��������������=����1�����2�����ú��ȡ���3
		//��������������=����1�����2���������ȡ���3
		
		//����·�
	}
	
	public static void main(String[] args) {

		
		// ��������4 ������Ӧ�����ӱ���
		// ��PIID���������
		/*
		String terminalAddr = "123456789"; 
		String apduData = "00 10 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 04 00 04 00 01 04 00 00 00 00 64 00 00";
		int frameNo = 23; //0~63�������
		int APDU_type = 2; //2=����Ӧ����������;5=��ȡ����;6=��������;7=��������
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
		
		// �ͻ���  ����  ��ַ=000000000002 APDU=0501004001020000
		
		// ��·����
		//recv = "68 1D 00 81 05 07 09 19 05 16 20 00 CS CS 01 ED 00 00 B4 20 16 05 19 08 05 00 00 A4 CS CS 16";
		
		// �� ��OAD
		//recv= "681700430502000000000010485E0501004001020000ED0316";
		
		
		// ��  ��OAD
		//recv= "681700430502000000000010485E05020202200002002001020000ED0316";
		// д ��OAD
		//recv ="68 1B 00 43 05 02 00 00 00 00 00 10 B7 BC 06 01 00 40 02 02 00 09 02 01 23 00 14 1A 16 ";
		String apdu = "";
		
		// д ��OAD
		//apdu = "06 01 02 40 00 02 00 1C 20 16 01 20 16 27 11 00";
		
		// д ��OAD ��Ӧ
		//apdu = "86 01 02 40 00 02 00 00 00 00";
		
		// д ��OAD
		//apdu = "06 02 03 02 40 01 02 00 55 06 00 00 00 00 00 01 40 00 02 00 1C 20 16 01 20 16 27 11 00";
		
		// д ��OAD ��Ӧ
		//apdu = "86 02 03 02 40 01 02 00 00 40 00 02 00 00 00 00";
		
		// �� ��OAD
		//apdu = "05 01 01 40 01 02 00 00";
		
		// �� ��OAD ��Ӧ
		//apdu = "85 01 01 40 01 02 00 01 55 06 12 34 56 78 90 12 00 00";
		
		// �� ��OAD
		//apdu = "05 02 02 02 20 00 02 00 20 01 02 00 00";
		
		// �� ��OAD ��Ӧ
		//apdu = "85 02 02 02 20 00 02 00 01 01 03 12 09 6D 12 09 6D 12 09 6D 20 01 02 00 01 01 03 05 00 00 03 E8 05 00 00 03 E8 05 00 00 03 E8 00 00";
		
		
		// ����Record 
		//apdu = "05 03 03 50 04 02 00 01 20 21 02 00 1C 20 16 01 20 00 00 00 02 00 20 21 02 00 00 00 10 02 00 00";
		
		// ����Record 
		//apdu = "05 04 03 "+"02"+"50 04 02 00 01 20 21 02 00 1C 20 16 01 20 00 00 00 02 00 20 21 02 00 00 00 10 02 00 "+
		//		"50 04 02 00 01 20 21 02 00 1C 20 16 01 20 00 00 00 02 00 20 21 02 00 00 00 10 02 00 00";
		
		// ����Record ��Ӧ 
		apdu = "85 03 03 50 04 02 00 02 00 20 21 02 00 00 00 10 02 00 01 01 1C 20 16 01 20 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 00 00";
		
		// ����Record ���� ��� �ն��� �������� 
		//apdu = "05 03 04 60 12 03 00 05 20 16 01 20 00 00 00 03 05 06 04 00 00 00 01 21 06 04 00 00 00 01 22 06 04 00 00 00 01 23 06 04 00 00 00 01 24 06 04 00 00 00 01 25 05 00 40 01 02 00 00 60 40 02 00 00 60 41 02 00 00 60 42 02 00 01 50 04 02 00 02 00 10 02 00 00 20 02 00 00";
		
		// ����Record ���� ��� �ն��� �������� ��Ӧ 
		//apdu = "85 03 04 60 12 03 00 05 00 40 01 02 00 00 60 40 02 00 00 60 41 02 00 00 60 42 02 00 01 50 04 02 00 02 00 10 02 00 00 20 02 00 01 05 09 05 00 00 00 01 21 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 22 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 23 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 24 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 09 05 00 00 00 01 25 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 1C 20 16 01 20 00 00 00 01 02 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 00 00";

		// ��֡�� ��Ӧ  
		//apdu = "85 05 08 00 00 01 01 01 60 00 02 00 01 01 03 02 04 12 00 01 02 08 55 06 04 00 00 00 22 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 08 55 06 04 00 00 00 22 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 08 55 06 04 00 00 00 22 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 00 00";
		
		//apdu = "85 05 08 00 00 01 01 02 60 00 02 00 01 01 03 02 04 12 00 01 02 08 55 06 04 00 00 00 22 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 08 55 06 04 00 00 00 22 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 08 55 06 04 00 00 00 22 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 " + 
		//"60 00 02 00 01 01 03 02 04 12 00 01 02 08 55 06 04 00 00 00 22 21 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 02 02 08 55 06 04 00 00 00 22 22 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 02 04 12 00 03 02 08 55 06 04 00 00 00 22 23 16 03 16 03 51 F2 09 02 01 09 06 00 00 00 00 00 00 11 04 11 04 16 01 02 04 55 06 00 00 00 00 00 00 09 06 00 00 00 00 00 00 12 00 01 12 00 01 01 00 00 00";
		
		// ���ú� ��ȡ
		//apdu = "06 03 04 01 45 00 02 0B 12 01 68 45 00 02 0B 03 00";
		
		// ���ú� ��ȡ ��Ӧ
		//apdu = "86 03 04 01 45 00 02 0B 00 45 00 02 0B 01 12 01 68 00 00";
		
		// ���� ��
		//apdu = "07 01 05 00 10 01 00 0F 00 00";
		
		// ���� �� ��Ӧ
		//apdu = "87 01 05 00 10 01 00 00 00 00 00";

		// ���� ��  ���
		//apdu = "07 03 07 01 00 10 01 00 0F 00 00 10 02 00 00 00";

		// ���� ��  ���  ��Ӧ
		//apdu = "87 03 07 01 00 10 01 00 00 00 00 10 02 00 01 01 05 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 06 00 00 00 00 00 00";
		
		// ���� ��  �����ͨ�ɼ�����  �ɼ���ǰ����
		//apdu = "07 01 05 60 14 7F 00 01 01 02 06 11 01 12 00 01 02 02 11 00 00 01 04 5B 00 00 10 02 00 5B 00 00 20 02 00 5B 00 20 00 02 00 5B 00 20 01 02 00 5C 01 00 16 01 00";

		// ���� ��  �����ͨ�ɼ�����  �ɼ���ǰ����  ��Ӧ
		//apdu = "87 01 05 60 14 7F 00 00 00 00 00";
		
		// ���� ��  �����ͨ�ɼ�����  �ɼ���������
		//apdu = "07 01 05 60 14 7F 00 01 01 02 06 11 01 12 00 20 02 02 11 02 00  01  01 5B 01 50 04 02 00 04 00 10 02 00 00 20 02 00 00 30 02 00 00 40 02 00 5C 01 00 16 02 00";
		
		String begin = "68 1B 00 43 05 02 00 00 00 00 00 10 B7 BC";
		String end = "14 1A 16";
		recv = begin + apdu + end;
		
		//recv ="681E0043050200000000001073B70101000000B42016051908050000A4986316";
		
		
		// д ��OAD
		//recv ="68 1B 00 43 05 02 00 00 00 00 00 10 B7 BC 06 01 00 40 02 02 00 09 02 01 23 00 14 1A 16 ";
		frame698 = new Frame698(recv);
		//System.out.println(frame698.getJSon());
		frame698.getTree();
		//System.out.println();
		

		// ��������1 �������������ӱ���
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

		
		
		 
		// ��������3 ��������������
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
