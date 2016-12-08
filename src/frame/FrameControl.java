package frame;
import com.eastsoft.util.DataConvert;


public class FrameControl {
	//DIR=0表示此帧是由客户机发出的；DIR=1表示此帧是由服务器发出的
	String DIRFlag = "0";
	
	//PRM=1表示此帧是由客户机发起的；PRM=0表示此帧是由服务器发起的
	String PRMFlag = "1";
	
	// 分帧标志 
	String partFlag = "0";
	
	// 1=链路管理;3=用户数据
	int funData = 3;
	
	public FrameControl(String data){
		if (!data.equals("")){
			//81 = 10000001
			String tmp = DataConvert.hexString2BinString(data, 8); 
			DIRFlag = tmp.substring(0,1);
			PRMFlag = tmp.substring(1,2);
			partFlag = tmp.substring(2,3);
			funData = DataConvert.BinStrToInt(tmp.substring(5,8));
		}
	}
	
	public String getString(){
		String ret = "";
		String tmp = DIRFlag + PRMFlag + partFlag + "00" + DataConvert.IntToBinString(funData, 3);
		ret = DataConvert.binStr2HexString(tmp, 2);
		
		return ret;
	}
	
	
	
	public String getDIRFlag() {
		return DIRFlag;
	}

	public void setDIRFlag(String dIRFlag) {
		DIRFlag = dIRFlag;
	}

	public String getPRMFlag() {
		return PRMFlag;
	}

	public void setPRMFlag(String pRMFlag) {
		PRMFlag = pRMFlag;
	}

	public String getPartFlag() {
		return partFlag;
	}

	public void setPartFlag(String partFlag) {
		this.partFlag = partFlag;
	}

	public int getFunData() {
		return funData;
	}

	public void setFunData(int funData) {
		this.funData = funData;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
