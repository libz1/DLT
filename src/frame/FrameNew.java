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
		// ������ִ�е�Ŀ���ǣ�����ִ�еĴ���
		// ������getObject�����л�ʹ�ã�����������ε���
		DataValsToDeal = getFuncParas(code,null);
	}
	

	public FrameNew(Meter meter) {
		
//		// ɾ�����Ŀ���ֻ�ܽ���OAD����ĺϲ�
//		// 60 00 83 00 ,120001
//		if (meter.getFlag().equals(Constant.FLAG_DEL))
//			frame = AnalyData.getAPDUData(data_val_new,-1,false);
//		
//		System.out.println("FrameNew=>"+frame);
		
		// ��֯���ӱ��ģ����Զ���ϲ���һ��������
		if (meter.getFlag().equals(Constant.FLAG_INS))
			frame = getAPDUData(54,meter);
		
		// ��֯ɾ�����ģ�ɾ������ֻ����OAD���������ݺϲ� 
		if (meter.getFlag().equals(Constant.FLAG_DEL))
			frame = "60008300 " + getAPDUData(10,meter);
		
//		System.out.println("FrameNew=>"+frame);
	}
	
	public FrameNew() {
	}
	
	// xuky 2016.11.07 Ϊ���������Ч��
	public void setMeterData(Meter meter){
		if (meter.getFlag().equals(Constant.FLAG_INS))
			frame = getAPDUData(54,meter);
		
		// ��֯ɾ�����ģ�ɾ������ֻ����OAD���������ݺϲ� 
		if (meter.getFlag().equals(Constant.FLAG_DEL))
			frame = "60008300 " + getAPDUData(10,meter);
	}
	
	// ���ݶ�����Ϣ�õ�APDU����
	private String getAPDUData(int code,Object object){
		String ret = "";
		
		Object[][] data_val_new = getFuncParas(code,object);
		
		// 4��������֯���ĳ��򣬵õ�APDU��������
		// ���ӱ��Ŀ��Զ�����кϲ�������һ��OAD����
		frame = AnalyData.getAPDUData(data_val_new,-1,false);
		ret = frame;
		
		return ret;
	}
	
	public static Object[][] getFuncParasWithRoot(String OAD,Object object,String type){

		// xuky 2016.12.01
		// OAD ������ 31 04 02 01 , ���һ���ֽ�01����ʾֻ��ȡ�����ṹ���һ��������
		// �ȴ����������͵����ݣ��ṹ�����͵ĺ������д�������
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
			// xuky 2016.12.01������Ԫ��������Ϊ�㡣��ʾҪ�������е�����
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

		// xuky 2016.11.18 ��Ϊ��β�ѯ��code��һ�£����Խ��е���
		// ���ܴ��ڵ����⣬���ݶ�̬�仯�ˣ����������û�м�ʱ����
		if (Data_val == null || Data_val_Code != code ) {
			Data_val = new Object[60][30];
			DB.getInstance().addArray(code,type,Data_val,false,WithRoot);
			Data_val_Code = code;
		}
		
//		Object[][] data_val = new Object[60][30];
//		// 1����datatype_user�л�ȡЭ������
//		DB.getInstance().addArray(code,"Func",data_val,false);
		
		// xuky 2016.11.07 Ϊ����߲�ѯ���ݵ��ٶ�
		Object[][] data_val = Data_val;
		
		int arrayNum = 0;
		
		// 2������insert_arrayidx��Ϣ��������
		List<UseToSort> list = new ArrayList<UseToSort>();
		for( Object[] o :data_val ){
			if (o[0]!= null){
				arrayNum ++;
				String name = (String) o[9];
				if (name != null && !name.equals("")){
					// 3��������������䵽�б���
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
	
	// ����APDU���ĵõ�������Ϣ �ʺϴ���һԪ������
	public Object[] getObject(String data, Object object){
		//�����ı����ǱȽϳ���,���²�����Ҫ���أ����ڶ�������Ԫ��2��
//		0204120001020A5507050000000000011603160351F20902010906000000000000110411011601120000120000
//		020455070500000000000009001200011200010100
//
//		0204120002020A5507050000000000021603160351F20902010906000000000000110411021601120000120000
//		020455070500000000000009001200011200010100
		
		Object[] ret_object = new Object[2];
		int allRowCount = DataValsToDeal.length; 
		
		String restData = "";
		// 1byte���ͣ��������
		
		// �Խ�������  ���յ�����Ĺ̶���ʽ���н���
		
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
				//���ж����������
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
					System.out.println("getObject=> type"+type+"��Ҫ���崦����");
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
