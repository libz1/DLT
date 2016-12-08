package frame;
import com.eastsoft.util.DataConvert;


public class FrameControl {
	//DIR=0��ʾ��֡���ɿͻ��������ģ�DIR=1��ʾ��֡���ɷ�����������
	String DIRFlag = "0";
	
	//PRM=1��ʾ��֡���ɿͻ�������ģ�PRM=0��ʾ��֡���ɷ����������
	String PRMFlag = "1";
	
	// ��֡��־ 
	String partFlag = "0";
	
	// 1=��·����;3=�û�����
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
