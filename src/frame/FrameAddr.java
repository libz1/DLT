package frame;
import util.SoftParameter;
import util.Util698;

import com.eastsoft.util.DataConvert;


public class FrameAddr {

	//0表示单地址，1表示通配地址，2表示组地址，3表示广播地址
	String AFFlag = "0";
	
	String SAData = "";
	
	String CAData = "00";
	int addrLen = 0;
	
	// 0表示单地址，1表示通配地址，2表示组地址，3表示广播地址
	int addrType = 0;
	
	public FrameAddr(){
		setSAData(SoftParameter.getInstance().getSendTerminal());
	}
	
	public String getString(){
		String ret = "";
		String tmp = DataConvert.IntToBinString(addrType, 2)+"00"+DataConvert.IntToBinString(addrLen-1,4);
		ret = DataConvert.binStr2HexString(tmp, 2);
		
		if (!Util698.isEven(SAData)){
			// 奇数位 
			SAData = SAData + "F";
		}
		// 默认为位数与长度相匹配
		ret = ret + DataConvert.reverseString(SAData) + CAData;
				
		return ret;
	}
	
	public String getAFFlag() {
		return AFFlag;
	}


	public void setAFFlag(String aFFlag) {
		if (!aFFlag.equals("")){
			String tmp = DataConvert.hexString2BinString(aFFlag, 8);
			addrLen = DataConvert.BinStrToInt(tmp.substring(4,8))+1;
			addrType = DataConvert.BinStrToInt(tmp.substring(0,2));
		}
		AFFlag = aFFlag;
	}

	// 根据AFFlag解析得到
	public int getAddrLen() {
		return addrLen;
	}


	public String getSAData() {
		return SAData;
	}

	public void setSAData(String sAData) {
		sAData = sAData.replace(" ", "");
		// 处理因补位带来的F
		if (sAData.substring(sAData.length()-1,sAData.length()).toUpperCase().equals("F"))
			SAData = sAData.substring(0,sAData.length()-1);
		else
			SAData = sAData;
		
		if (SAData.length()/2*2 != SAData.length())
			addrLen = SAData.length() /2 +1;
		else
			addrLen = SAData.length() /2;
	}

	public String getCAData() {
		return CAData;
	}



	public void setCAData(String cAData) {
		CAData = cAData;
	}

	public void setCSAddr(String serverAddr, String clientAddr,
			int serverAddrType) {
		addrType = serverAddrType;
		CAData = clientAddr;
		
		SAData = serverAddr;
		if (SAData.length()/2*2!=SAData.length())
			addrLen = SAData.length()/2+1;
		else
			addrLen = SAData.length()/2;
		
		String tmp = DataConvert.IntToBinString(addrType, 3)+"00"+DataConvert.IntToBinString(addrLen-1, 4);
		
		AFFlag = DataConvert.binStr2HexString(tmp, 2);
		
	}
	
	public int getAddrType() {
		return addrType;
	}

	public void setAddrType(int addrType) {
		this.addrType = addrType;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
