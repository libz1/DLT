package util;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;

import entity.Attr;
import entity.Constant;

/**
 * 698.45协议常用处理函数
 * @author xuky
 * @version 2016.09.12
 */
public class Util698 {
	
	// 从BCD格式的日期时间数据得到常规
	static public String BCDDateTimeToNormal(String data){
		String ret = "";
		// 07E0090D02001F130000
		// 07E0 09 0D 02 00 1F 13 0000
		if (data.length() == 20){
			ret = DataConvert.hexString2String(data.substring(0,4),4)+"-"
					+DataConvert.hexString2String(data.substring(4,6),2)+"-"
					+DataConvert.hexString2String(data.substring(6,8),2)+" "
					//+DataConvert.hexString2String(data.substring(8,10),2)+":"  // 周次信息
					+DataConvert.hexString2String(data.substring(10,12),2)+":"
					+DataConvert.hexString2String(data.substring(12,14),2)+":"
					+DataConvert.hexString2String(data.substring(14,16),2)+":"
					+DataConvert.hexString2String(data.substring(16),3);
		}
		return ret;
	}
	
	// 从常规日期时间数据得到BCD格式
	static public String NormalToBCDDateTime(String data){
		String ret = "";
		if (data.length() == 23){
			//yyyy-mm-dd hh:mm:ss:sss
			//ret = data.replaceAll(" ","" );
			//ret = ret.replaceAll(":", "");
			//ret = ret.replaceAll("-", "");
			//String tmp = ret.substring(ret.length()-3,ret.length());
			//tmp = DataConvert.String2HexString(tmp,4);
			//ret = ret.substring(0, ret.length()-3)+ tmp;
			
			String tmp = getWeekOfDate(data);
			ret = DataConvert.String2HexString(data.substring(0, 4),4)
			+ DataConvert.String2HexString(data.substring(5, 7),2)
			+ DataConvert.String2HexString(data.substring(8, 10),2)
			+ DataConvert.String2HexString(tmp,2)
			+ DataConvert.String2HexString(data.substring(11, 13),2)
			+ DataConvert.String2HexString(data.substring(14, 16),2)			
			+ DataConvert.String2HexString(data.substring(17, 19),2)			
			+ DataConvert.String2HexString(data.substring(20),2)
			;
			// day_of_week：0表示周日，1~6分别表示周一到周六
		}
		return ret;
	} 
	
	public static String getWeekOfDate(String dateStr) {
		  //String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		  String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(DateTimeFun.string2Date(dateStr, "yyyy-MM-dd HH:mm:SSS"));
		  int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		  return weekDaysCode[intWeek];
	} 
	
	// 对字符串添加分隔符
	static public String seprateString(String data,String str){
		// 添加分隔符前，先清理原先的分隔符数据
		data = data.replaceAll(" ", "");
		data = data.replaceAll(",", "");
		String ret = "";
		if (data.length() >= 2){
			String tmp = data;
			while( tmp.length()>=2 ){
				ret += tmp.substring(0, 2)+str;
				tmp = tmp.substring(2);
			}
			ret += tmp;
		}
		else
			ret = data;
		return ret;
	}
	
	static int[] fcstab = {
		0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
		0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
		0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
		0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
		0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
		0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
		0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
		0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
		0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
		0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
		0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
		0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
		0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
		0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
		0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
		0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
		0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
		0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
		0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
		0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
		0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
		0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
		0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
		0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
		0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
		0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
		0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
		0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
		0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
		0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
		0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
		0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78
		};
	
	
	static private int compuCS(int fcs,byte[] frame,int len){
		int i = 0,c,d;
		while (i < len){
			c = (fcs ^ frame[i]) & 0xFF;
			d = fcstab[c];
			fcs = (fcs >> 8) ^ d ;
			i++;
		}
		return fcs;
	}
	
	static public String getCS(String data){
		data = data.replaceAll(",", "");
		data = data.replaceAll(" ", "");
		int fcs = 0xFFFF;
		byte[] frame = DataConvert.hexString2ByteArray(data); 
		int len = frame.length;
		int cs = compuCS( fcs,frame,len );
		cs = cs ^ 0xFFFF;
		return DataConvert.int2HexString(cs, 4);
	}
	

	static public String getPercent(int minNum ,int maxNum, int dec){
	    int num1 = minNum;  
        int num2 = maxNum;
        // 参考http://blog.csdn.net/macwhirr123/article/details/7552806
        // 创建一个数值格式化对象  
        NumberFormat numberFormat = NumberFormat.getInstance();  
        // 设置精确到小数点后2位  
        numberFormat.setMaximumFractionDigits(dec);  
        String result = numberFormat.format((float) num1 / (float) num2 * 100);  
        return result;
	}
	
	static public Boolean isEven(String data){
		Boolean ret = false;
		int addrLen = data.length();
		if (addrLen / 2 * 2 == addrLen){
			// 偶数长度
			ret = true;
		}
		return ret;
	}
	
	static public String checkFrameType(String msg) {
		String ret = "";
		msg = msg.replaceAll(" ", "");
		msg = msg.replaceAll(",", "");
		if (msg.substring(0, 2).equals("68") && msg.substring(msg.length()-2, msg.length()).equals("16") )
			ret = "698.45";
		
		return ret;
	}
	
	
	// 删除指定目录下，文件名中包含某个数据的文件
	public static void deleteFiles(String sPath,String context) {  
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return ;  
	    }  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()){
	        	String filename = files[i].getAbsolutePath();
	        	if (filename.indexOf(context)>=0)
	        		deleteFile(filename);  
	        }       	
	    }  
	}  	

    public static void deleteFile(String sPath) {  
    	File file = new File(sPath);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
        }  
    }  
	

    // 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
    // 参考http://blog.csdn.net/linshutao/article/details/7693625
    public static List getFiledsInfo(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	List<Map> list = new ArrayList();
       	Map<String, Object> infoMap=null;
       	
       	String type = "";
    	for(int i=0;i<fields.length;i++){
    		infoMap = new HashMap<String, Object>();
    		type = fields[i].getType().toString();
    		// xuky 2016.11.15 
    		if (type.toLowerCase().indexOf("list") <0){
        		infoMap.put("type", fields[i].getType());
        		infoMap.put("name", fields[i].getName());
        		infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
        		list.add(infoMap);
    		} 
    	}
    	return list;
    }
    
    // 获取第一个属性信息
    public static Map<String, Object> getFirstFiledsInfo(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	Map<String, Object> infoMap = new HashMap<String, Object>();
   		infoMap.put("type", fields[0].getType());
   		infoMap.put("name", fields[0].getName());
   		infoMap.put("value", getFieldValueByName(fields[0].getName(), o));
    	return infoMap;
    }
    
    // 根据属性名称获取属性信息
    public static Map<String, Object> getFiledsInfoByName(Object o,String name){
    	Field[] fields=o.getClass().getDeclaredFields();
       	Map<String, Object> infoMap = new HashMap<String, Object>();
       	for( Field f:fields ){
       		if (f.getName().toLowerCase().equals(name.toLowerCase())){
       	   		infoMap.put("type", f.getType());
       	   		infoMap.put("name", f.getName());
       	   		infoMap.put("value", getFieldValueByName(f.getName(), o));
       	   		break;
       		}
       	}
    	return infoMap;
    }

    // 得到对象的属性内容
    public static Object getObjectAttr(Object o, String getter){
        Object value = null;
		try {
			Method method = o.getClass().getMethod(getter, new Class[] {});
	        value = method.invoke(o, new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
    }
    
    // 得到多个对象的属性内容，各个属性内容转为字符串，使用,进行分隔
    public static String getObjectAttrs(Object o, String getter){
		// 有,分隔，表示有多个字段
    	String ret = "";
		String[] tmp = getter.split(",");
		for ( String str: tmp){
	        Object value = null;
			try {
				Method method = o.getClass().getMethod(str, new Class[] {});
		        value = method.invoke(o, new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value == null)
				ret += ",";
			else{
		        String type = value.getClass().toString();
		        if (type.indexOf("String") >= 0)
					ret += (String)value+",";
		        
		        if (type.toLowerCase().indexOf("int") >= 0)
					ret += DataConvert.int2String((int)value)+",";
			}
		}
    	
		return ret;
    }
    
    public static String getGetter(String attrName){
        String firstLetter = attrName.substring(0, 1).toUpperCase();  
        String getter = "get" + firstLetter + attrName.substring(1);  
    	return getter;
    }
    
    public static String getSetter(String attrName){
        String firstLetter = attrName.substring(0, 1).toUpperCase();  
        String getter = "set" + firstLetter + attrName.substring(1);  
    	return getter;
    }

    // 根据对象的属性名获取对象的属性值
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {  
            String firstLetter = fieldName.substring(0, 1).toUpperCase();  
            String getter = "get" + firstLetter + fieldName.substring(1);
            
            Method method = o.getClass().getMethod(getter, new Class[] {});  
            Object value = method.invoke(o, new Object[] {});  
            return value;  
        } catch (Exception e) {  
            e.printStackTrace();
            return null; 
        }  
    }
    
    // 根据对象的属性名设置对象的属性值
    // 需要有函数名称、函数参数（因为存在重载情况，通过参数类型进行函数判断）
    public static void setFieldValueByName(String fieldName, Object o, Object val) {
        try {  
            String firstLetter = fieldName.substring(0, 1).toUpperCase();  
            String getter = "set" + firstLetter + fieldName.substring(1);
            
            Map<String, Object> info = getFiledsInfoByName(o,fieldName);
            Object valType = info.get("type");
            //Method[] methods = o.getClass().getMethods();
            //System.out.println(methods);
            Method method = o.getClass().getMethod(getter, new Class[] {(Class) valType});  
            method.invoke(o, new Object[] {val});  
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }
    
    // 判断两个对象是否相等  属性列表相同、属性类型相同、属性值相同
    public static Boolean objEquals(Object obj1,Object obj2,String excepColumns){
    	Boolean equas = false;
    	
    	// 如果有一个对象为空，另外一个不为空，则判断为不等
    	if (obj1 == null && obj2 != null)
    		return false;
    	if (obj2 == null && obj1 != null)
    		return false;
    	
    	excepColumns = ","+excepColumns.toLowerCase()+",";
    	List<Map> infoList = getFiledsInfo(obj1);
    	String name,type;
    	Object val1,val2;
    	int attrNo = 0;
    	equas = true;
    	for( Map<String, Object> info:infoList ){
			name = info.get("name").toString();
			// 排除例外字段  例如id，meter.archiveType等
			if (excepColumns.indexOf(","+name.toLowerCase()+",") < 0 ){
				type = info.get("type").toString();
				val1 = Util698.getFieldValueByName(name,obj1);
				val2 = Util698.getFieldValueByName(name,obj2);
				
				// 需要处理数据为空的情况
				if (val1==null && val2!=null)
					return false;
				if (val2==null && val1!=null)
					return false;
				if (val1!=null && val2!=null)
					if (!val1.equals(val2)){
						return false; 
	    		} 
			}
    		attrNo ++;
    	}
    	return equas;
    }
    
    // 两个对象进行属性赋值  excepColumns为例外属性，例外属性不进行赋值操作
    public static Boolean objClone(Object sourceObj,Object targetObj,String excepColumns){
    	Boolean result = false;
    	
     	// 如果有一个对象为空，另外一个不为空，则无法继续操作  即在外部进行对象构建
    	if (sourceObj == null && targetObj != null)
    		return false;
    	if (targetObj == null && sourceObj != null)
    		return false;
    	
    	excepColumns = ","+excepColumns.toLowerCase()+",";
    	List<Map> sourceObjecAtrrList = getFiledsInfo(sourceObj);
    	String name;
    	Object val1;
    	result = true;
    	for( Map<String, Object> info:sourceObjecAtrrList ){
			name = info.get("name").toString();
			// 排除例外字段  例如id，meter.archiveType等
			if (excepColumns.indexOf(","+name.toLowerCase()+",") < 0 ){
				val1 = Util698.getFieldValueByName(name,sourceObj);
				Util698.setFieldValueByName(name, targetObj, val1);
			}
    	}
    	return result;
    }
    

    // 对象属性转为数组内容
    public static Object[] obj2Array(Object obj){
		List<Map> l = getFiledsInfo(obj);
    	int attrNum = l.size();
    	Object[] data = new Object[attrNum];
    	
		int i = 0;
		for( Map<String, Object> info:l ){
			String name = info.get("name").toString();
			data[i] = getFieldValueByName(name,obj);
			i++;
		}
    	return data;
    };
    
    // 将二维数组转为对象列表 
    public static <T> List<T> array2ObjList(Object[][] data, NewObjAction newobjact){
    	List<T> retList = new ArrayList<T>();
		for (Object[] o:data){
			
			// 此代码与实际对象关联紧密。需要调整
			//Attr d = new Attr();
			//setObjVal(d,o);
			
			// 将变化部分进行剥离  自定义对象中实现代码 通过回调函数，实现动态创建对象
			Object obj = newobjact.getNewObject();
			setObjVal(obj,o);
			
			// 将对象添加到列表中
			retList.add((T) obj);
		}
		return retList; 
    }  
    
    public interface NewObjAction {
    	Object getNewObject();
    }

    
    private static void setObjVal(Object obj,Object objVal[]){
		List<Map> l = getFiledsInfo(obj);
		int i = 0;
		for( Map<String, Object> info:l ){
			String name = info.get("name").toString();
			//System.out.println("setObjVal=>"+name);
			if (objVal[i] == null)
				setFieldValueByName(name,obj,objVal[i]);
			else{
				// 数据类型可能是int，可能是Integer
				if (info.get("type").toString().toLowerCase().indexOf("int") >= 0)
					setFieldValueByName(name,obj,(int)objVal[i]);
				if (info.get("type").toString().indexOf("String") >= 0)
					setFieldValueByName(name,obj,(String)objVal[i]);
			}
			i++;
		}
    }
        
	public static String[] setArrayData( String[] data ){
		String[] ret = new String[ data.length ];
		int i = 0;
		for( String s:data ){
			ret[i] = s;
			i++;
		}
		return ret;
	}
	
	
	// 判断添加的数据是否符合要求，如果不符合使用默认数据
	public static String getCodeData(String data,String type){
		String ret = "";
		// 1、进行数据处理，可能失败，则直接使用原值
		try{
			data = DataConvert.int2String(DataConvert.String2Int(data));			
		}
		catch(Exception e){
			// 不进行打印输出 屏蔽此错误信息
			//e.printStackTrace();
		}
		
		// 2、整体进行匹配判断
		String[] array = (String[]) Util698.getFieldValueByName(type, new Constant());
		// 判断数据是否为数组的一个元素  
		// 参考http://blog.csdn.net/maxracer/article/details/8439195
		List<String> tempList = Arrays.asList(array);
		if (tempList.contains(data))
			ret = data;
		else{
			// 3、数据两边加上左右括号，进行部分匹配判断
			data = "("+data+")";
			String findStr = "";
			for( String str: tempList ){
				if (str.indexOf(data) >= 0){
					findStr = str;
					break;
				}
			}
			if (!findStr.equals(""))
				ret = findStr;
			else
				ret = Constant.getProtocolType()[0];
		}
		
		return ret;
	}
	
    
	public static <T> void main(String[] args) {
		/*
		//27577   0x9446  37958 
		//10273
		int fcs = 0xFFFF;
		String data = "1700430502000000000010";
		byte[] frame = DataConvert.hexString2ByteArray(data); 
		//int[] frame = {0x17,0x00};
		int len = frame.length;
		int cs = compuCS( fcs,frame,len );
		cs = cs ^ 0xFFFF;
		System.out.println("cs=>"+DataConvert.int2HexString(cs, 4));
		
		System.out.println("getCS=>"+getCS(data));
		*/
		
		/*
		// 创建一个对象 对象get方法需要有，建议自动生成
		Attr a = new Attr();
		a.setAttr_id(1);
		a.setAttr_idx(1);
		
		// 得到并打印输出对象的属性类型、名称、内容
		List<Map> l = getFiledsInfo(a);
		for( Map<String, Object> info:l ){
			System.out.println("type:"+info.get("type")+" name:"+info.get("name")+" value:"+info.get("value") );
		}
		
		// 对象的第一个属性，可以视为特殊id字段，用于进行匹配，第二个字段也是特殊字段，内容匹配
		*/
		
		
		System.out.println(getWeekOfDate("2016-10-14 08:37:00:111"));

		
		// 将数据库中的数据转为对象列表 中间转换介质为object数组
		// 使用了反射技术、使用了call back技术 
		NewObjAction newobj_act =  new NewObjAction(){
			@Override
			public Object getNewObject() {
				Attr d = new Attr();
				return d;
			}
		};
		
		Object[][] data = DB.getInstance().getObjectAttrListTest(9999,true);
		List<T> resultList = array2ObjList(data,newobj_act );
		for (T t: resultList){
			System.out.println(((Attr)t).getAttr_id());
		}
	}
	

}
