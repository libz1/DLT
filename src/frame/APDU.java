package frame;

import java.util.Arrays;

import util.*;

import com.eastsoft.util.DataConvert;

public class APDU {
	int choiseFlag = 5;
	int choiseFlag_1 = 1; // 部分APDU有第二个choise

	// 优先级
	int PIID_PRI = 0;
	// 序号
	int PIID_NO = 0;
	// ACD标志
	int PIID_ACD = 0;

	// 保存业务数据 nextData
	String nextData = "";

	TreeNode dataNode = null;

	// 添加业务数据从节点用
	TreeNode node = null;

	String choiseData = "";

	String timeTag = "";
	String followReport = "";

	public APDU() {
	}

	public int choiseFlagToType(int flag) {
		int type = 1;
		if (flag == 1 || flag == 129)
			type = 0;

		if (flag >= 2 && flag <= 9)
			if (flag != 4)
				type = 1;

		if (flag >= 130 && flag <= 137)
			type = 2;

		if (flag == 16 || flag == 144)
			type = 3;

		return type;
	}

	public APDU(int choiseFlag, String piid, String data) {
		setChoiseFlag(choiseFlag);
		setPIID(piid);

		setNextData(data.replaceAll(" ", ""));
	}

	public APDU(String data) {
		try {
			init(data);
		} catch (Exception e) {
			System.out.println("APDU.init Error=>" + e.getMessage());
		}

	}

	public void init(String data) {

		data = data.replaceAll(" ", "");
		data = data.replaceAll(",", "");
		setChoiseFlag(DataConvert.hexString2Int(data.substring(0, 2)));

		// 链路
		if (choiseFlag == 1) {
			setPIID(data.substring(2, 4));
			setNextData(data.substring(4, data.length()));

			try{
				// 2016.10.19 隔离解析部分代码，如果这里出错，不会影响组织报文
				int reqType = DataConvert.hexString2Int(nextData.substring(0, 2));
				int heartTime = DataConvert.hexString2Int(nextData.substring(2, 6));
				String reqDateTime = nextData.substring(6, nextData.length());
				reqDateTime = Util698.BCDDateTimeToNormal(reqDateTime);

				node = new TreeNode("请求类型(建立连接0,心跳1,断开连接2)", reqType);
				dataNode.addChild(node);
				node = new TreeNode("心跳周期", heartTime);
				dataNode.addChild(node);
				node = new TreeNode("请求时间", reqDateTime);
				dataNode.addChild(node);

				// xuky 在buildResponseFrame中需要使用这里的数据 
				choiseData = "reqType@"+reqType+";heartTime@"+heartTime+";reqDateTime@"+reqDateTime;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		// 读取请求
		if (choiseFlag == 5) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			
			try{
				// 2016.10.19 隔离解析部分代码，如果这里出错，不会影响组织报文
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
					// 单OAD
					s = new AnalyData().dealOAD(nextData, dataNode);
				}
				if (choiseFlag_1 == 2) {
					// 多OAD
					node = new TreeNode("SEQUENCE OF OAD", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOAD(nextData, node);
				}
				if (choiseFlag_1 == 3) {
					// Record = OAD RSD RCSD
					s = new AnalyData().dealRecord(nextData, dataNode);
				}
				if (choiseFlag_1 == 4) {
					// 多个Record
					node = new TreeNode("SEQUENCE OF GetRecord", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfRecord(nextData, node);
				}
				if (choiseFlag_1 == 5) {
					// 数据块序号
					node = new TreeNode("数据块序号", nextData.substring(0, 4));
					dataNode.addChild(node);
					s[1] = nextData.substring(4);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}

		// 读取响应
		if (choiseFlag == 133) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));

			try{
				String[] s = { "", "" };

				if (choiseFlag_1 == 1) {
					// 单OAD
					s = new AnalyData().dealResultNormal(nextData, dataNode);
				}
				if (choiseFlag_1 == 2) {
					// 多个OAD 、 数据
					node = new TreeNode("SEQUENCE OF A-ResultNormal", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfResultNormal(nextData, node);
				}
				if (choiseFlag_1 == 3) {
					// 单个Record 、 数据
					s = new AnalyData().dealResultRecord(nextData, dataNode);
				}
				if (choiseFlag_1 == 4) {
					// 多个Record 、 数据
					node = new TreeNode("SEQUENCE OF A-ResultRecord", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfResultRecord(nextData, node);
				}
				if (choiseFlag_1 == 5) {
					// 末帧标志、分帧序号、分帧响应（数据）
					int endFlag = DataConvert.hexString2Int(nextData
							.substring(0, 2));
					node = new TreeNode("末帧标志", endFlag);
					dataNode.addChild(node);
					int part_no = DataConvert.hexString2Int(nextData
							.substring(2, 6));
					node = new TreeNode("分帧序号", part_no);
					dataNode.addChild(node);
					int part_type = DataConvert.hexString2Int(nextData.substring(6,
							8));
					node = new TreeNode("分帧类型", part_type);
					dataNode.addChild(node);
					if (part_type == 0) {
						// DAR
						node.setKey(node.getKey() + "(DAR)");
						s = new AnalyData().dealDAR(nextData.substring(8), node);
					} else if (part_type == 1) {
						node.setKey(node.getKey() + "(SEQUENCE OF A-ResultNormal)");
						s = new AnalyData().dealSeqOfResultNormal(nextData.substring(8),
								node);
					} else if (part_type == 2) {
						node.setKey(node.getKey() + "(SEQUENCE OF A-ResultRecord)");
						s = new AnalyData().dealSeqOfResultRecord(nextData.substring(8),
								node);
					}
				}
				s = new AnalyData().dealFollowReport(s[1], dataNode);
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		// 设置请求
		if (choiseFlag == 6) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));

			try{
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
					s = new AnalyData().dealOADWithData(nextData, dataNode);
				}
				if (choiseFlag_1 == 2) {
					node = new TreeNode("SEQUENCE OF OAD+DATA", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOADWithData(nextData, node);
				}
				if (choiseFlag_1 == 3) {
					node = new TreeNode("SEQUENCE OF OAD+DATA+Read", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOADWithDataAndRead(nextData, node);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}

		// 操作请求
		if (choiseFlag == 7) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			try{
				
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
					s = new AnalyData().dealOMDWithData(nextData, dataNode);
				}
				if (choiseFlag_1 == 2) {
					node = new TreeNode("SEQUENCE OF OMD+DATA", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOMDWithData(nextData, node);
				}
				if (choiseFlag_1 == 3) {
					node = new TreeNode("SEQUENCE OF OMD+DATA+Read", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOMDWithDataAndRead(nextData, node);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		// 设置响应
		if (choiseFlag == 134) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));

			try{
				String[] s = { "", "" };
				switch (choiseFlag_1) {
				case 1:
					s = new AnalyData().dealOADWithDAR(nextData, dataNode);
					break;
				case 2:
					node = new TreeNode("SEQUENCE OF OAD+DAR", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOADWithDAR(nextData, node);
					break;
				case 3:
					node = new TreeNode("SEQUENCE OF OAD+DAR+A-ResultNormal", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOADWithDARAndData(nextData, node);
					break;
				default:
					break;
				}
				s = new AnalyData().dealFollowReport(s[1], dataNode);
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}

		// 操作响应
		if (choiseFlag == 135) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			try{
				String[] s = { "", "" };
				switch (choiseFlag_1) {
				case 1:
					s = new AnalyData().dealOMDWithDARAndData(nextData, dataNode);
					break;
				case 2:
					node = new TreeNode("SEQUENCE OF OMD+DAR+DATA", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOMDWithDARAndData(nextData, node);
					break;
				case 3:
					node = new TreeNode("SEQUENCE OF OMD+DAR+DATA+A-ResultNormal",
							"");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOMDWithDARAndDataAndRead(nextData,
							dataNode);
					break;
				default:
					break;
				}
				s = new AnalyData().dealFollowReport(s[1], dataNode);
				s = new AnalyData().dealTimeTag(s[1], dataNode);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		// 代理
		if (choiseFlag == 9) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			
//			整个代理请求超时时间
//			单个代理请求超时时间
//			目标服务器地址（多个时，使用逗号进行分隔）  ProxyGetRequestRecord时。只取用第一个
			// ProxyTransCommandRequest需要单独进行开发
			//
			

			try{
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
//				整个代理请求的超时时间   long-unsigned，
//				  代理若干个服务器的对象属性读取  SEQUENCE OF    (dealSeqOfProxyRead)
//				  { 一个目标服务器地址         TSA，	代理一个服务器的超时时间   long-unsigned，	若干个对象属性描述符       SEQUENCE OF OAD//					  }					}
					node = new TreeNode("整个代理请求的超时时间"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyRead(nextData.substring(4), node);
					
				}
				if (choiseFlag_1 == 2) {
					node = new TreeNode("代理请求的超时时间"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					s = new AnalyData().dealTSA(nextData.substring(4), dataNode);
					s = new AnalyData().dealOAD(s[1], dataNode);
					s = new AnalyData().dealRSD(s[1], dataNode);
					s = new AnalyData().dealCSD(s[1], dataNode);
				}
				if (choiseFlag_1 == 3) {
					node = new TreeNode("整个代理请求的超时时间"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyRequest(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 4) {
					node = new TreeNode("整个代理请求的超时时间"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxySetAndRead(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 5) {
					node = new TreeNode("整个代理请求的超时时间"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyOMD(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 6) {
					node = new TreeNode("整个代理请求的超时时间"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyOMDAndRead(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 7) {
//					数据转发端口                  OAD，
//					端口通信控制块                COMDCB，
//					接收等待报文超时时间（秒）   long-unsigned，
//					接收等待字节超时时间（毫秒） long-unsigned，
//					  透明转发命令                 octet-string
					s = new AnalyData().dealOAD(nextData, dataNode);
					s = new AnalyData().dealType95(s[1], dataNode);
					
					node = new TreeNode("接收等待报文超时时间（秒）"+s[1].substring(0,4), DataConvert.hexString2Int(s[1].substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("接收等待字节超时时间（毫秒）"+s[1].substring(4,8), DataConvert.hexString2Int(s[1].substring(4,8)));
					dataNode.addChild(node);
					
					s = new AnalyData().dealType09(s[1].substring(8),dataNode);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
		// 代理响应
		if (choiseFlag == 137) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));

			try{
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyReadResp(nextData, node);
					
				}
				if (choiseFlag_1 == 2) {
					s = new AnalyData().dealTSA(nextData, dataNode);
					s = new AnalyData().dealResultRecord(nextData, dataNode);
					
				}
				if (choiseFlag_1 == 3) {
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxySetResp(nextData, node);
				}
				if (choiseFlag_1 == 4) {
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxySetAndReadResp(nextData, node);
				}
				if (choiseFlag_1 == 5) {
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyOMDResp(nextData, node);
				}
				if (choiseFlag_1 == 6) {
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyOMDAndReadResp(nextData, node);
				}
				if (choiseFlag_1 == 7) {
//					数据转发端口                  OAD，
//					透明转发命令返回结果     TransResult
					s = new AnalyData().dealOAD(nextData, dataNode);
					s = new AnalyData().dealResultNormal(s[1], dataNode);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}

		// 主动上报 上报通知 
		if (choiseFlag == 136) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			try{
				String[] s = { "", "" };
				switch (choiseFlag_1) {
				case 1:
					// SEQUENCE OF A-ResultNormal
					node = new TreeNode("SEQUENCE OF A-ResultNormal", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfResultNormal(nextData, node);
					break;
				case 2:
					// SEQUENCE OF A-ResultRecord
					node = new TreeNode("SEQUENCE OF A-ResultRecord", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfResultRecord(nextData, node);
					break;
				default:
					break;
				}
				s = new AnalyData().dealFollowReport(s[1], dataNode);
				s = new AnalyData().dealTimeTag(s[1], dataNode);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		// 应答主动上报 
		if (choiseFlag == 8) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			try{
				String[] s = { "", "" };
				switch (choiseFlag_1) {
				case 1:
					node = new TreeNode("SEQUENCE OF OAD", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOAD(nextData, dataNode);
					break;
				case 2:
					node = new TreeNode("SEQUENCE OF OAD", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOAD(nextData, node);
					break;
				default:
					break;
				}
				s = new AnalyData().dealFollowReport(s[1], dataNode);
				s = new AnalyData().dealTimeTag(s[1], dataNode);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
	}

	private void setPIID(String data) {
		String tmp = DataConvert.hexString2BinString(data, 8);
		// 优先级
		PIID_PRI = DataConvert.BinStrToInt(tmp.substring(0, 1));
		// ACD标志
		PIID_ACD = DataConvert.BinStrToInt(tmp.substring(1, 2));
		// 序号
		PIID_NO = DataConvert.BinStrToInt(tmp.substring(2, 8));
	}

	public String getPIID() {
		String ret = DataConvert.IntToBinString(PIID_PRI, 1)
				+ DataConvert.IntToBinString(PIID_ACD, 1)
				+ DataConvert.IntToBinString(PIID_NO, 6);
		ret = DataConvert.binStr2HexString(ret, 2);
		return ret;
	}

	public String getPIIDnoACD() {
		String ret = DataConvert.IntToBinString(PIID_PRI, 1) + "0"
				+ DataConvert.IntToBinString(PIID_NO, 6);
		ret = DataConvert.binStr2HexString(ret, 2);
		return ret;
	}

	public String getString() {
		String ret = "";

		int[] test = { 1, 129, 2, 3, 130, 131, 132 };
		Arrays.sort(test); // 首先对数组排序
		int result = Arrays.binarySearch(test, choiseFlag);

		// 链路报文，无choiseFlag_1部分
		String first_part = "";
		if (result >= 0)
			first_part = DataConvert.int2HexString(choiseFlag, 2) + getPIID();
		else
			first_part = DataConvert.int2HexString(choiseFlag, 2)
					+ DataConvert.int2HexString(choiseFlag_1, 2) + getPIID();
		first_part = Util698.seprateString(first_part, " ");
		ret = first_part +"," + nextData;
		
		return ret;
	}

	public int getChoiseFlag() {
		return choiseFlag;
	}

	public void setChoiseFlag(int choiseFlag) {
		// choiseType = choiseFlagToType(choiseFlag);
		this.choiseFlag = choiseFlag;
	}

	public String getNextData() {
		return nextData;
	}

	public void setNextData(String nextData) {
		this.nextData = nextData;
		dataNode = new TreeNode("业务数据", nextData);
	}

	public String getChoiseData() {
		return choiseData;
	}

	public void setChoiseData(String choiseData) {
		this.choiseData = choiseData;
	}

	public String getTimeTag() {
		return timeTag;
	}

	public void setTimeTag(String timeTag) {
		this.timeTag = timeTag;
	}

	public String getFollowReport() {
		return followReport;
	}

	public void setFollowReport(String followReport) {
		this.followReport = followReport;
	}

	public int getChoiseFlag_1() {
		return choiseFlag_1;
	}

	public void setChoiseFlag_1(int choiseFlag_1) {
		this.choiseFlag_1 = choiseFlag_1;
	}

	public int getPIID_PRI() {
		return PIID_PRI;
	}

	public void setPIID_PRI(int pIID_PRI) {
		PIID_PRI = pIID_PRI;
	}

	public int getPIID_NO() {
		return PIID_NO;
	}

	public void setPIID_NO(int pIID_NO) {
		PIID_NO = pIID_NO;
	}

	public int getPIID_ACD() {
		return PIID_ACD;
	}

	public void setPIID_ACD(int pIID_ACD) {
		PIID_ACD = pIID_ACD;
	}

	public static void main(String[] args) {

	}

}
