package frame;

import util.DB;
import util.Util698;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eastsoft.util.DataConvert;

import base.UseToSort;
import entity.Constant;
import entity.Meter;

public class FrameNew {
	String frame;
	Object[][] DataValsToDeal;
	static Object[][] Data_val;
	static int Data_val_Code = 0;
	
	
	
	public FrameNew(int code) {
		// 在这里执行的目的是，减少执行的次数
		// 后续的getObject函数中会使用，但是那里会多次调用
		DataValsToDeal = getFuncParas(code,null);
	}
	

	public FrameNew(Meter meter) {
		
//		// 删除报文可以只能进行OAD级别的合并
//		// 60 00 83 00 ,120001
//		if (meter.getFlag().equals(Constant.FLAG_DEL))
//			frame = AnalyData.getAPDUData(data_val_new,-1,false);
//		
//		System.out.println("FrameNew=>"+frame);
		
		// 组织增加报文，可以多个合并到一个数组中
		if (meter.getFlag().equals(Constant.FLAG_INS))
			frame = getAPDUData(54,meter);
		
		// 组织删除报文，删除报文只能在OAD级进行数据合并 
		if (meter.getFlag().equals(Constant.FLAG_DEL))
			frame = "60008300 " + getAPDUData(10,meter);
		
//		System.out.println("FrameNew=>"+frame);
	}
	
	public FrameNew() {
	}
	
	// xuky 2016.11.07 为了提高运行效率
	public void setMeterData(Meter meter){
		if (meter.getFlag().equals(Constant.FLAG_INS))
			frame = getAPDUData(54,meter);
		
		// 组织删除报文，删除报文只能在OAD级进行数据合并 
		if (meter.getFlag().equals(Constant.FLAG_DEL))
			frame = "60008300 " + getAPDUData(10,meter);
	}
	
	// 根据对象信息得到APDU报文
	private String getAPDUData(int code,Object object){
		String ret = "";
		
		Object[][] data_val_new = getFuncParas(code,object);
		
		// 4、调用组织报文程序，得到APDU报文内容
		// 增加报文可以多个进行合并，放在一个OAD后面
		frame = AnalyData.getAPDUData(data_val_new,-1,false);
		ret = frame;
		
		return ret;
	}
	
	public static Object[][] getFuncParasWithRoot(String OAD,Object object,String type){

		// xuky 2016.12.01
		// OAD 可能是 31 04 02 01 , 最后一个字节01，表示只是取数组或结构体的一部分数据
		// 先处理数组类型的数据，结构体类型的后续进行代码完善
		int attr_num = DataConvert.String2Int(OAD.substring(6,8));
		
		String sql = "";
		if (type.equals("Func")) 
			sql = "select encode_datatype from def4_meth where "+ 
					" host_id = (select object_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
					" and sort_id = 1 "+
					" and method_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));
		else
			sql = "select encode_datatype from def3_attr where "+ 
					" host_id = (select object_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
					" and sort_id = 1 "+
					" and attr_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));
		
		Object data = DB.getInstance().getOneData(sql,"encode_datatype","int");
		if (data == null || (int)data == -1 ){
			
			if (type.equals("Func")) 
				sql = "select encode_datatype from def4_meth where "+ 
						" host_id = (select class_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
						" and sort_id = 0 "+
						" and method_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));
			else
				sql = "select encode_datatype from def3_attr where "+ 
						" host_id = (select class_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
						" and sort_id = 0 "+
						" and attr_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));
			
			data = DB.getInstance().getOneData(sql,"encode_datatype","int");
			
			if (data == null || (int)data == -1 )
				return null;
		}
		
		if (attr_num !=0){
			// xuky 2016.12.01属性内元素索引不为零。表示要查找其中的数据
			sql = "select code from datatype_user where fcode = "+ data+ " and seq = " +attr_num;  
			data = DB.getInstance().getOneData(sql,"code","int");
			
			if (data == null || (int)data == -1 )
				return null;
		} 
		
		return getFuncParas((int)data,object,true,type);
	}
	
	private static Object[][] getFuncParas(int code,Object object){
		return getFuncParas(code,object,false,"Func");
	}
	
	private static Object[][] getFuncParas(int code,Object object,Boolean WithRoot,String type){

		// xuky 2016.11.18 因为多次查询的code不一致，所以进行调整
		// 可能存在的问题，数据动态变化了，这里的数据没有及时更新
		if (Data_val == null || Data_val_Code != code ) {
			Data_val = new Object[60][30];
			DB.getInstance().addArray(code,type,Data_val,false,WithRoot);
			Data_val_Code = code;
		}
		
//		Object[][] data_val = new Object[60][30];
//		// 1、从datatype_user中获取协议数据
//		DB.getInstance().addArray(code,"Func",data_val,false);
		
		// xuky 2016.11.07 为了提高查询数据的速度
		Object[][] data_val = Data_val;
		
		int arrayNum = 0;
		
		// 2、按照insert_arrayidx信息进行排序
		List<UseToSort> list = new ArrayList<UseToSort>();
		for( Object[] o :data_val ){
			if (o[0]!= null){
				arrayNum ++;
				String name = (String) o[9];
				if (name != null && !name.equals("")){
					// 3、将对象数据填充到列表中
					//data_val[][2] = xxx
					if (object != null){
						Object data = Util698.getFieldValueByName(name, object);
						if (data != null){
							String tmp = (String)data; 
							if (tmp.indexOf("(") >= 0 && tmp.indexOf(")") >= 0){
								tmp = tmp.split("\\(")[1];
								tmp = tmp.split("\\)")[0];
								data = tmp;
							}
						}
						o[2] = data;
					}
				}
					
				UseToSort useToSort = new UseToSort();
				useToSort.setIdx((String) o[5]);
				useToSort.setData(o);
				list.add(useToSort);
			}
		}
		Collections.sort(list);
		Object[][] data_val_new = new Object[arrayNum][30];
		int i = 0;
		for(UseToSort useToSort:list ){
			data_val_new[i] = useToSort.getData();
			i++;
		}
		return data_val_new;
	}
	
	public String getFrameNew() {
		return frame;
	}
	
	// 根据APDU报文得到对象信息 适合处理单一元素数据
	public Object[] getObject(String data, Object object){
		//给定的报文是比较长的,余下部分需要返回，放在对象数组元素2中
//		0204120001020A5507050000000000011603160351F20902010906000000000000110411011601120000120000
//		020455070500000000000009001200011200010100
//
//		0204120002020A5507050000000000021603160351F20902010906000000000000110411021601120000120000
//		020455070500000000000009001200011200010100
		
		Object[] ret_object = new Object[2];
		int allRowCount = DataValsToDeal.length; 
		
		String restData = "";
		// 1byte类型，后跟数据
		
		// 自解析数据  按照电表档案的固定格式进行解析
		
		Boolean flag_stop = false;
		String type = "", temp = "", typeData = "";
		temp = data;
		int rowNum = 0;
		while (!temp.equals("") && !flag_stop){
			type = DataConvert.int2String(DataConvert.hexString2Int(temp.substring(0,2)));
			temp = temp.substring(2);
			String[] t = DB.getInstance().getTypeInfo(type);
			int byteLen = 0; 
			if (t[1] == null || t[1].equals("0")){
				//自行定义解析程序
				if (t[0].equals("structure") || t[0].equals("array")){
					byteLen = 1; 
					typeData = temp.substring(0,byteLen*2);
					temp = temp.substring(byteLen*2);
//					System.out.println(rowNum + " getObject=> type"+type +" typeData " + typeData);
				}
				else if (type.equals("85") || type.equals("9")){
					//0705000000000001
					byteLen = DataConvert.hexString2Int(temp.substring(0,2))+1; 
					typeData = temp.substring(0,byteLen*2);
					if (type.equals("85"))
						typeData = typeData.substring(4);
					if (type.equals("9"))
						typeData = typeData.substring(2);
					temp = temp.substring(byteLen*2);
//					System.out.println(rowNum + " getObject=> type"+type +" typeData " + typeData);
				}
				else{
					System.out.println("getObject=> type"+type+"需要定义处理函数");
					return ret_object;
				}
			}
			else{
				byteLen = DataConvert.String2Int(t[1]); 
				typeData = temp.substring(0,byteLen*2);
				if (t[0].indexOf("unsigned")>=0)
					typeData = DataConvert.int2String(DataConvert.hexString2Int(typeData));
				temp = temp.substring(byteLen*2);
//				System.out.println(rowNum + " getObject=> type"+type +" typeData " + typeData);
			}
			String attr = (String) DataValsToDeal[rowNum][9];
			if ( attr!= null && !attr.equals("")){
//				System.out.println(" getObject=> type"+type+" attr "+attr +" val " + typeData);
				//Util698.getFieldValueByName(name, object);
				Util698.setFieldValueByName(attr, object, typeData);
			} 
			rowNum ++;
			if (rowNum >= allRowCount){
				flag_stop = true;
				restData = temp;
			}
		}
		
		ret_object[0] = object;
		ret_object[1] = restData;
		return ret_object;
	}
	
	

	public static void main(String[] args) {
		String data = 
				"0204120001020A5507050000000000011603160351F20902010906000000000000110411011601120000120000"+
				"020455070500000000000009001200011200010100"+
				"0204120002020A5507050000000000021603160351F20902010906000000000000110411021601120000120000"+
				"020455070500000000000009001200011200010100";
		new FrameNew(54).getFuncParas(54, null);
	}

}
