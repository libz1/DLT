package frame;

import java.util.Arrays;

import util.*;

import com.eastsoft.util.DataConvert;

public class APDU {
	int choiseFlag = 5;
	int choiseFlag_1 = 1; // ����APDU�еڶ���choise

	// ���ȼ�
	int PIID_PRI = 0;
	// ���
	int PIID_NO = 0;
	// ACD��־
	int PIID_ACD = 0;

	// ����ҵ������ nextData
	String nextData = "";

	TreeNode dataNode = null;

	// ���ҵ�����ݴӽڵ���
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

		// ��·
		if (choiseFlag == 1) {
			setPIID(data.substring(2, 4));
			setNextData(data.substring(4, data.length()));

			try{
				// 2016.10.19 ����������ִ��룬��������������Ӱ����֯����
				int reqType = DataConvert.hexString2Int(nextData.substring(0, 2));
				int heartTime = DataConvert.hexString2Int(nextData.substring(2, 6));
				String reqDateTime = nextData.substring(6, nextData.length());
				reqDateTime = Util698.BCDDateTimeToNormal(reqDateTime);

				node = new TreeNode("��������(��������0,����1,�Ͽ�����2)", reqType);
				dataNode.addChild(node);
				node = new TreeNode("��������", heartTime);
				dataNode.addChild(node);
				node = new TreeNode("����ʱ��", reqDateTime);
				dataNode.addChild(node);

				// xuky ��buildResponseFrame����Ҫʹ����������� 
				choiseData = "reqType@"+reqType+";heartTime@"+heartTime+";reqDateTime@"+reqDateTime;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		// ��ȡ����
		if (choiseFlag == 5) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			
			try{
				// 2016.10.19 ����������ִ��룬��������������Ӱ����֯����
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
					// ��OAD
					s = new AnalyData().dealOAD(nextData, dataNode);
				}
				if (choiseFlag_1 == 2) {
					// ��OAD
					node = new TreeNode("SEQUENCE OF OAD", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfOAD(nextData, node);
				}
				if (choiseFlag_1 == 3) {
					// Record = OAD RSD RCSD
					s = new AnalyData().dealRecord(nextData, dataNode);
				}
				if (choiseFlag_1 == 4) {
					// ���Record
					node = new TreeNode("SEQUENCE OF GetRecord", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfRecord(nextData, node);
				}
				if (choiseFlag_1 == 5) {
					// ���ݿ����
					node = new TreeNode("���ݿ����", nextData.substring(0, 4));
					dataNode.addChild(node);
					s[1] = nextData.substring(4);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}

		// ��ȡ��Ӧ
		if (choiseFlag == 133) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));

			try{
				String[] s = { "", "" };

				if (choiseFlag_1 == 1) {
					// ��OAD
					s = new AnalyData().dealResultNormal(nextData, dataNode);
				}
				if (choiseFlag_1 == 2) {
					// ���OAD �� ����
					node = new TreeNode("SEQUENCE OF A-ResultNormal", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfResultNormal(nextData, node);
				}
				if (choiseFlag_1 == 3) {
					// ����Record �� ����
					s = new AnalyData().dealResultRecord(nextData, dataNode);
				}
				if (choiseFlag_1 == 4) {
					// ���Record �� ����
					node = new TreeNode("SEQUENCE OF A-ResultRecord", "");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfResultRecord(nextData, node);
				}
				if (choiseFlag_1 == 5) {
					// ĩ֡��־����֡��š���֡��Ӧ�����ݣ�
					int endFlag = DataConvert.hexString2Int(nextData
							.substring(0, 2));
					node = new TreeNode("ĩ֡��־", endFlag);
					dataNode.addChild(node);
					int part_no = DataConvert.hexString2Int(nextData
							.substring(2, 6));
					node = new TreeNode("��֡���", part_no);
					dataNode.addChild(node);
					int part_type = DataConvert.hexString2Int(nextData.substring(6,
							8));
					node = new TreeNode("��֡����", part_type);
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

		// ��������
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

		// ��������
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

		// ������Ӧ
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

		// ������Ӧ
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
		
		// ����
		if (choiseFlag == 9) {
			setChoiseFlag_1(DataConvert.hexString2Int(data.substring(2, 4)));
			setPIID(data.substring(4, 6));
			setNextData(data.substring(6, data.length()));
			
//			������������ʱʱ��
//			������������ʱʱ��
//			Ŀ���������ַ�����ʱ��ʹ�ö��Ž��зָ���  ProxyGetRequestRecordʱ��ֻȡ�õ�һ��
			// ProxyTransCommandRequest��Ҫ�������п���
			//
			

			try{
				String[] s = { "", "" };
				if (choiseFlag_1 == 1) {
//				������������ĳ�ʱʱ��   long-unsigned��
//				  �������ɸ��������Ķ������Զ�ȡ  SEQUENCE OF    (dealSeqOfProxyRead)
//				  { һ��Ŀ���������ַ         TSA��	����һ���������ĳ�ʱʱ��   long-unsigned��	���ɸ���������������       SEQUENCE OF OAD//					  }					}
					node = new TreeNode("������������ĳ�ʱʱ��"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyRead(nextData.substring(4), node);
					
				}
				if (choiseFlag_1 == 2) {
					node = new TreeNode("��������ĳ�ʱʱ��"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					s = new AnalyData().dealTSA(nextData.substring(4), dataNode);
					s = new AnalyData().dealOAD(s[1], dataNode);
					s = new AnalyData().dealRSD(s[1], dataNode);
					s = new AnalyData().dealCSD(s[1], dataNode);
				}
				if (choiseFlag_1 == 3) {
					node = new TreeNode("������������ĳ�ʱʱ��"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyRequest(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 4) {
					node = new TreeNode("������������ĳ�ʱʱ��"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxySetAndRead(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 5) {
					node = new TreeNode("������������ĳ�ʱʱ��"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyOMD(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 6) {
					node = new TreeNode("������������ĳ�ʱʱ��"+nextData.substring(0,4), DataConvert.hexString2Int(nextData.substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("SEQUENCE OF ","");
					dataNode.addChild(node);
					s = new AnalyData().dealSeqOfProxyOMDAndRead(nextData.substring(4), node);
				}
				if (choiseFlag_1 == 7) {
//					����ת���˿�                  OAD��
//					�˿�ͨ�ſ��ƿ�                COMDCB��
//					���յȴ����ĳ�ʱʱ�䣨�룩   long-unsigned��
//					���յȴ��ֽڳ�ʱʱ�䣨���룩 long-unsigned��
//					  ͸��ת������                 octet-string
					s = new AnalyData().dealOAD(nextData, dataNode);
					s = new AnalyData().dealType95(s[1], dataNode);
					
					node = new TreeNode("���յȴ����ĳ�ʱʱ�䣨�룩"+s[1].substring(0,4), DataConvert.hexString2Int(s[1].substring(0,4)));
					dataNode.addChild(node);
					
					node = new TreeNode("���յȴ��ֽڳ�ʱʱ�䣨���룩"+s[1].substring(4,8), DataConvert.hexString2Int(s[1].substring(4,8)));
					dataNode.addChild(node);
					
					s = new AnalyData().dealType09(s[1].substring(8),dataNode);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
		// ������Ӧ
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
//					����ת���˿�                  OAD��
//					͸��ת������ؽ��     TransResult
					s = new AnalyData().dealOAD(nextData, dataNode);
					s = new AnalyData().dealResultNormal(s[1], dataNode);
				}
				s = new AnalyData().dealTimeTag(s[1], dataNode);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}

		// �����ϱ� �ϱ�֪ͨ 
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
		// Ӧ�������ϱ� 
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
		// ���ȼ�
		PIID_PRI = DataConvert.BinStrToInt(tmp.substring(0, 1));
		// ACD��־
		PIID_ACD = DataConvert.BinStrToInt(tmp.substring(1, 2));
		// ���
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
		Arrays.sort(test); // ���ȶ���������
		int result = Arrays.binarySearch(test, choiseFlag);

		// ��·���ģ���choiseFlag_1����
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
		dataNode = new TreeNode("ҵ������", nextData);
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
