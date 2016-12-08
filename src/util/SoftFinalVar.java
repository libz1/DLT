package util;

import java.util.Vector;

/**
 * 常量.
 * @author xuky
 * @version 2013.10.24
 */
public class SoftFinalVar {

	// 协议类型
	// 国网2005规约 PWD 2字节
	public final static String PROTOCOL_2005 = "01";
	// 国网376.1协议 PWD 16字节
	public final static String PROTOCOL_3761 = "02";
	// 广东协议
	public final static String PROTOCOL_GD = "10";
	// 东软协议
	public final static String PROTOCOL_EASTSOFT = "00";
	
	// 国网376.2协议
	public final static String PROTOCOL_3762 = "88";

	// 电表协议类型
	// 645-2007
	public final static String METERPROTOCOL_07 = "645-2007";
	// 645-1997
	public final static String METERPROTOCOL_97 = "645-1997";
	
	
	// 国网376.1协议 控制字
	// AFN AFN=00H 确认否认
	public final static String GW_CONFIRM = "00";
	// AFN AFN=01H 复位命令
	public final static String GW_RESET = "01";
	// AFN AFN=02H 链路接口检测
	public final static String GW_LINKTEST = "02";
	// AFN AFN=04H 设置参数
	public final static String GW_WRITE_PARAM = "04";
	// AFN AFN=05H 控制命令
	public final static String GW_CONTROL = "05";
	// AFN AFN=09H 请求终端配置
	public final static String GW_REQINFO = "09";
	// AFN AFN=0AH 查询参数
	public final static String GW_READ_PARAM = "0A";
	// AFN AFN=0CH 查询一类数据 当前数据
	public final static String GW_READ_CURDATA = "0C";
	// AFN AFN=0DH 查询二类数据 历史数据
	public final static String GW_READ_HISDATA = "0D";
	// AFN AFN=0DH 查询三类数据 告警数据
	public final static String GW_READ_ALMDATA = "0E";
	// AFN AFN=10H 数据转发
	public final static String GW_TRANSMIT = "10";

	// 广东协议 控制字
	// 控制字 读集中器、前置机当前数据
	public final static String GD_C_READ = "01";
	public final static String GD_C_READ_REPLY = "81";
	// 控制字 写对象参数
	public final static String GD_C_WRITE = "08";
	public final static String GD_C_WRITE_REPLY = "88";
	// 控制字 集中器抄收电表实时数据
	public final static String GD_C_READREALTIME = "11";
	public final static String GD_C_READREALTIME_REPLY = "91";
	// 控制字 集中器抄收日常综合数据
	public final static String GD_C_READDATA = "12";
	public final static String GD_C_READDATA_REPLY = "92";
	public final static String GD_C_READDATA_ERR = "D2";
	// 控制字 抄收电表表号
	public final static String GD_C_READMETER = "15";
	public final static String GD_C_READMETER_REPLY = "95";
	// 控制字 请求网络连接（终端是服务的模式）
	public final static String GD_C_RJ45CONNECT = "31";
	public final static String GD_C_RJ45CONNECT_OK = "B1";
	public final static String GD_C_RJ45CONNECT_ERR = "F1";
	//异常控制字
	public final static String GD_ERR_WRITE_REPLY = "C8";
	//东软扩充控制字
	public final static String GD_C_EXT = "39";
	public final static String GD_C_EXT_OK = "B9";
	public final static String GD_C_EXT_ERR = "F9";
	
	//前置机或是终端主动发起的报文
	public final static String GD_TER_LOGIN = "A1";
	public final static String GD_TER_LOGIN_REPLY = "21";
	public final static String GD_TER_LOGOUT = "A2";
	public final static String GD_TER_LOGOUT_REPLY = "22";
	public final static String GD_TER_TIMEOUT = "A3";
	public final static String GD_TER_TIMEOUT_REPLY = "23";
	
	public final static String GD_TER_LOCK = "A5"; //东软前置机自定内容：通道被锁
	
	// 通信参数
	public final static String COMM_TYPE_SOCKET = "1"; // socket
	public final static String COMM_TYPE_COM = "2"; // com


	public static Vector<String> getGDCommParamVector() {
		Vector<String> v = null;
		v = new Vector<String>();
		v.addElement("GPRS/CDMA");
		v.addElement("PSTN");
		v.addElement("RS232/RS485");
		v.addElement("CSD");
		v.addElement("Radio");
		v.addElement("短信唤醒(GPRS/CDMA)");
		return v;
	}
	
	public static String[] ProtocolType = {"国网376.1规约","国网2005规约","广东规约"};
	public static Vector<String> getVector(String aType) {
		Vector<String> v = null;
		v = new Vector<String>();
		if (aType.equals("ProtocolType"))
		{
			for (String str:ProtocolType)
				v.addElement(str);
		}
		return v;
	}
	
	

	public static String getCode(String aType, String data) {
		//"GDCommType"  广东协议通信方式  代码得到内容
		//"GDCommTypeCode"  广东协议通信方式  内容得到代码
		//"ErrNote"  广东协议  代码得到内容
		//ProtocolCode 终端协议类型  内容得到代码
		String retString = "";
		if (aType.equals("GDCommType")) {
			if (data.equals("GPRS/CDMA"))
				retString = "02";
			if (data.equals("PSTN"))
				retString = "03";
			if (data.equals("RS232/RS485"))
				retString = "06";
			if (data.equals("CSD"))
				retString = "07";
			if (data.equals("Radio"))
				retString = "08";
			if (data.equals("短信唤醒(GPRS/CDMA)"))
				retString = "09";
		}
		if (aType.equals("GDCommTypeCode")) {
			if (data.equals("02"))
				retString = "GPRS/CDMA";
			if (data.equals("03"))
				retString = "PSTN";
			if (data.equals("06"))
				retString = "RS232/RS485";
			if (data.equals("07"))
				retString = "CSD";
			if (data.equals("08"))
				retString = "Radio";
			if (data.equals("09"))
				retString = "短信唤醒(GPRS/CDMA)";
		}
		if (aType.equals("ErrNote")) {
			if (data.equals("00"))
				retString = "正确";
			if (data.equals("01"))
				retString = "中继命令无返回";
			if (data.equals("02"))
				retString = "设置内容非法";
			if (data.equals("03"))
				retString = "密码权限不足";
			if (data.equals("04"))
				retString = "无此项数据";
			if (data.equals("05"))
				retString = "命令时间失效";
			if (data.equals("11"))
				retString = "目标地址不存在";
			if (data.equals("12"))
				retString = "发送失败";
			if (data.equals("13"))
				retString = "短消息帧太长";		
		}
		if (aType.equals("ProtocolCode")) {
			if (data.equals("国网376.1规约"))
				retString = "02";
			if (data.equals("国网2005规约"))
				retString = "01";
			if (data.equals("广东规约"))
				retString = "10";
		}
		if (aType.equals("MeterTypeCode")) {
			if (data.equals("单相电子表"))
				retString = "1";
			if (data.equals("机械表"))
				retString = "2";
			if (data.equals("简易多功能表"))
				retString = "3";
			if (data.equals("多功能总表"))
				retString = "8";
		}
		if (aType.equals("MeterTypeNote")) {
			if (data.equals("01"))
				retString = "单相电子表";
			if (data.equals("02"))
				retString = "机械表";
			if (data.equals("03"))
				retString = "简易多功能表";
			if (data.equals("08"))
				retString = "多功能总表";
			if (data.equals("11"))
				retString = "单相电子表";
			if (data.equals("10"))
				retString = "简易多功能表";
			if (data.equals("09"))
				retString = "多功能总表";
		}
		if (aType.equals("MeterProtocolNote")) {
			if (data.equals("01"))
				retString = METERPROTOCOL_97;
			if (data.equals("02"))
				retString = METERPROTOCOL_97;
			if (data.equals("03"))
				retString = METERPROTOCOL_97;
			if (data.equals("08"))
				retString = METERPROTOCOL_97;
			if (data.equals("11"))
				retString = METERPROTOCOL_07;
			if (data.equals("10"))
				retString = METERPROTOCOL_07;
			if (data.equals("09"))
				retString = METERPROTOCOL_07;
		}
		
		if (aType.equals("Meter07_MeterType")) {
			if (data.equals("单相电子表"))
				retString = "11";
			if (data.equals("机械表"))
				retString = "2";
			if (data.equals("简易多功能表"))
				retString = "10";
			if (data.equals("多功能总表"))
				retString = "9";
		}
		if (aType.equals("Meter97_MeterType")) {
			if (data.equals("单相电子表"))
				retString = "1";
			if (data.equals("机械表"))
				retString = "2";
			if (data.equals("简易多功能表"))
				retString = "3";
			if (data.equals("多功能总表"))
				retString = "8";
		}
		
		if (aType.equals("FreezeTypeNote")) {
			if (data.equals("1"))
				retString = "当前";
			if (data.equals("2"))
				retString = "日冻结";
			if (data.equals("3"))
				retString = "月冻结";
		}
		if (aType.equals("FreezeType")) {
			if (data.equals("当前"))
				retString = "1";
			if (data.equals("日冻结"))
				retString = "2";
			if (data.equals("月冻结"))
				retString = "3";
		}		
		if (aType.equals("AFN=10H_Result")) {
			if (data.equals("00"))
				retString = "不能执行转发";
			if (data.equals("01"))
				retString = "转发接收超时";
			if (data.equals("02"))
				retString = "转发接收错误";
			if (data.equals("03"))
				retString = "转发接收确认";
			if (data.equals("04"))
				retString = "转发接收否认";
			if (data.equals("05"))
				retString = "转发接收数据";
		}		
		
		if (aType.equals("GDFunNote")) {
			retString = data; 
			if (data.equals("000001"))
				retString = "读当前数据";
			if (data.equals("000111"))
				retString = "实时写对象参数";
			if (data.equals("001000"))
				retString = "写对象参数";
			if (data.equals("010001"))
				retString = "集中器实时召测命令";
			if (data.equals("010010"))
				retString = "集中器抄收日常综合数据";
			if (data.equals("010011"))
				retString = "集中器抄收重点户负荷数据";
			if (data.equals("010100"))
				retString = "集中器对表拉合闸控制";
			if (data.equals("010101"))
				retString = "集中器抄收电表表号";
			if (data.equals("010110"))
				retString = "集中器抄收测量点其他数据";
		}		
		
		return retString;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
