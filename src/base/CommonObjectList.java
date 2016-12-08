package base;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.DB;
import util.Util698;
import util.Util698.NewObjAction;

import com.eastsoft.util.DebugSwing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import entity.Meter;

/**
 * 与具体对象无关的集合及操作
 * @author xuky
 * @version 2016.10.18
 * @param <T>
 * @param <T>
 * @param <T>
 */
public class CommonObjectList<T> {
	
	// 对应的数据库表名
	String tableName;
	
	// 主键名
	String idColName;
	
	// 重要字段
	String codeColName;
	
	// 创建数组元素对象的外部事件
	NewObjAction newobj_act;
	
	// 具体的实例化的对象
	Object object;

	// 存储数据的集合
	private List objList = new ArrayList<T>();
	
	// 导出数据用字段信息：字段显示信息、字段名称
	String[] exportColmunNames;
	String exportColmuns;
	

	// 测试用
	private CommonObjectList(){
		
	}

	// 构造函数中，需要传入创建对象的事件
	public CommonObjectList(NewObjAction newobj_act,String tableName,String idColName,String codeColName) {
		this.newobj_act = newobj_act; 
		this.tableName = tableName;
		this.idColName = idColName;
		this.codeColName = codeColName;
	}
	
	public CommonObjectList(NewObjAction newobj_act,String tableName,String codeColName,String where,String order) {
		init(newobj_act,tableName,codeColName,where,order);
	}
	
	public CommonObjectList(NewObjAction newobj_act,String tableName,String codeColName) {
		init(newobj_act,tableName,codeColName,"","");
	}
	
	// 将数组元素转为集合对象内容
	public void setDataFromArray(Object[][] data){
		objList = Util698.array2ObjList(data,newobj_act );
	}
	
	private void init(NewObjAction newobj_act,String tableName,String codeColName,String where,String order){
		this.newobj_act = newobj_act; 
		this.tableName = tableName;
		this.codeColName = codeColName;
		object = newobj_act.getNewObject();
		// 获取对象的首个属性信息，作为主键字段信息
		Map<String, Object> fileld = Util698.getFirstFiledsInfo(object);
		this.idColName = fileld.get("name").toString();
		Object[][] data = DB.getInstance().getDataList(tableName,where,order,object);
		setDataFromArray(data);
	}
	

	// 将字符串转为集合，需要给定类型信息
	public void converFormString(String s,Type type) {
		if (s == null || s.equals(""))
			objList = new ArrayList<T>();
		objList = new Gson().fromJson(s,type);
	}
	
	// 将集合转为字符串
	public String converToString() {
		return new Gson().toJson(objList);
	}

	public List<T> getList() {
		return objList;
	}
	
	// 根据主键id，获取一个对象
	public Object getOne(int id) {
		return getByCode(id,getIDString());
	}
	
	// 根据字段信息，获取一个对象
	public Object getByCode(int code,String getter) {
		Object object = null;
		try {
			for (Object o : objList){
	            Method method = o.getClass().getMethod(getter, new Class[] {});
	            Object value = method.invoke(o, new Object[] {});  
	            if ((int)value == code){
				    object = o;
				    break;
	            } 
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}  
		return object;
	}
	
	// 根据字段信息，获取一个对象  
	// 存在需要进行两个字段关联比较的需求
	public Object getByCode(String code,String getter) {
		Object object = null;
		try {
			for (Object o : objList){
				String value = (String) Util698.getObjectAttrs(o,getter);
				if (value.equals(code)){
				    object = o;
				    break;
				}
//	            Method method = o.getClass().getMethod(getter, new Class[] {});
//	            Object value = method.invoke(o, new Object[] {});  
//	            if (  ((String)value).equals(code) ){
//				    object = o;
//				    break;
//	            }
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}  
		return object;
	}
	
	// id主键信息，必须是数值类型的，不然max取值会出现错误
	public int getUseableID() {
		int tempID = DB.getInstance().getMaxId(tableName,idColName);
		return tempID;
	}
	
	public void addAll() {
//		for( int i = objList.size()-1;i>=0;i-- ){
//			Object o = objList.get(i); 
//			addWithParam(o,false,false);
//		}
		DB.getInstance().addAllData(tableName,objList);
	}
	
	public void addAll(List objList) {
//		for( int i = objList.size()-1;i>=0;i-- ){
//			Object o = objList.get(i); 
//			addWithParam(o,false,false);
//		}
		DB.getInstance().addAllData(tableName,objList);
	}
	
	public String add(Object o) {
		return addWithParam(o,true,false);
	}
	
	public String add(Object o,Boolean noDBOp) {
		return addWithParam(o,true,noDBOp);
	}

	// Boolean addtoList 表示是否需要添加到list集合中
	public String addWithParam(Object o, Boolean addtoList,Boolean noDBOp) {
		String ret = "";
		if (addtoList ){
			if (!noDBOp){
				// 查找重复的数据，不是根据主键信息查找，是根据重要字段信息查找
				// 可能存在多个字段的情况。则将数据统统转为字符串后进行匹配查找
				String getter = getCodeColString();
				String value = (String) Util698.getObjectAttrs(o,getter);
		        Object tmp = null ;
		       	tmp = getByCode(value,getter);
				if (tmp != null){
					// 出现了关键数据重复的问题，这样的数据是不允许添加的，应该提示用户，进行修改
					ret = "关键数据重复，请检查! 关键数据字段名:"+getter +"关键数据内容:"+value ;
					DebugSwing.showMsg(ret);
					return ret;
				}
			}
			objList.add((T) o);
		}
		//Object[] data = Util698.obj2Array(tmp);
		if (!noDBOp)
			DB.getInstance().addData(tableName,o);
		return ret;
	}
	
	// 删除多个数据
	public void deleteByCode(String code,String getter,Boolean noDBOp) {
		Object object = null;
		try {
//			for (Object o : objList){
			// 因为涉及到删除数据，所以需要倒序对集合进行处理
			for (int i=objList.size()-1;i>=0;i--){
				Object o = objList.get(i);
				String value = (String) Util698.getObjectAttrs(o,getter);
				if (value.equals(code)){
					object = o;
					objList.remove(o);
					if (!noDBOp){
						int id = (int) Util698.getObjectAttr(object,getIDString());
						DB.getInstance().deleteData(id,tableName,idColName,object);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	
	
	public void delete(int ID) {
		Object deleteObject = null;
		String getter =  getIDString();
		for (Object o : objList){
	        Object value = Util698.getObjectAttr(o,getter);
			if (value == null)
				value = 0;
			if ((value).equals(ID)) {
				deleteObject = o;
				objList.remove(o);
				break;
			}
		}
		DB.getInstance().deleteData(ID,tableName,idColName,deleteObject);
	}
	
	public void deleteAll(int[] ids) {
		DB.getInstance().deleteAllData(ids,tableName,idColName);
	}
	
	public void deleteAll() {
//		System.out.println("deleteAll=>"+DateTimeFun.getDateTimeSSS() +" begin");

//		for( int i = objList.size()-1;i>=0;i-- ){
//			Object o = objList.get(i); 
//			objList.remove(o);
//			Map<String, Object> info = Util698.getFirstFiledsInfo(o); 
//			int ID = (int) Util698.getFieldValueByName(info.get("name").toString(), o);
//			System.out.println("deleteAll=>"+DateTimeFun.getDateTimeSSS() +" listOPT end");
//			DB.getInstance().deleteData(ID,tableName,idColName);
//			System.out.println("deleteAll=>"+DateTimeFun.getDateTimeSSS() +" DBOPT end");
//		}
		
		// xuky 2016.11.07 批量进行SQL操作和单个进行，耗时相差极大
		int[] ids = new int[objList.size()];
		for( int i = objList.size()-1;i>=0;i-- ){
			Object o = objList.get(i); 
			objList.remove(o);
			Map<String, Object> info = Util698.getFirstFiledsInfo(o); 
			int ID = (int) Util698.getFieldValueByName(info.get("name").toString(), o);
			ids[i] = ID;
//			System.out.println("deleteAll=>"+DateTimeFun.getDateTimeSSS() +" listOPT end");
//			DB.getInstance().deleteData(ID,tableName,idColName);
//			System.out.println("deleteAll=>"+DateTimeFun.getDateTimeSSS() +" DBOPT end");
		}
		DB.getInstance().deleteAllData(ids,tableName,idColName);
		
	}
	
	private String getIDString(){
        String firstLetter = idColName.substring(0, 1).toUpperCase();  
        String getter = "get" + firstLetter + idColName.substring(1);
        return getter;
	}
	
	//
	private String getCodeColString(){
		String firstLetter = "", getter = "";
		
		if (codeColName.indexOf(",") < 0)
			codeColName += ","; 
			
			// 有,分隔，表示有多个字段
			String[] tmp = codeColName.split(",");
			for ( String str: tmp){
				firstLetter = str.substring(0, 1).toUpperCase();
				getter += "get" + firstLetter + str.substring(1)+",";
			}
//		}
//		else{
//	        firstLetter = codeColName.substring(0, 1).toUpperCase();  
//	        getter = "get" + firstLetter + codeColName.substring(1);
//		}
        return getter;
	}
	
	public void update(Object object) {
        String getter = getIDString();
        Object value2 = Util698.getObjectAttr(object,getter);
        Object value1 = null;
        
		for (int i= 0;i<objList.size() ;i++ ){
			Object o = objList.get(i);
	        value1 = Util698.getObjectAttr(o,getter);
			if (value1.equals(value2)) {
				objList.set(i, object);
				break;
			}
		}
        
		// xuky 2016.11.11 如果进行remove和add操作，会导致objList的次序变化，则外部进行遍历时会出现错误ConcurrentModificationException
//		for (Object o : objList){
//	        value1 = Util698.getObjectAttr(o,getter);
//			if (value1.equals(value2)) {
//				objList.remove(o);
//				objList.add((T) object);
//				break;
//			}
//		}
		DB.getInstance().updateData(tableName,object);
	}
	
	public int size() {
		return objList.size();
	}
	
	
	public void setExportColmunNames(String[] exportColmunNames) {
		this.exportColmunNames = exportColmunNames;
	}

	public void setExportColmuns(String exportColmuns) {
		this.exportColmuns = exportColmuns+",";
	}

	public String[][] getStringArray() {
		String[][] data = null;
		int aRow = objList.size();
		if (aRow != 0) {
			Object object = objList.get(0);
			int aCol = exportColmunNames.length;
			data = new String[aRow + 1][aCol];

			String[] colmuns = exportColmuns.split(","); 
			// 第一行是对象属性信息
			for (int j = 0; j < aCol; j++)
				data[0][j] = exportColmunNames[j];

			for (int i = 0; i < aRow; i++) {
				for (int j = 0; j < aCol; j++) {

					// 得到获取对象属性的方法名称
					String getter = Util698.getGetter(colmuns[j]);

					object = objList.get(i);
					try {
						// 动态 反射调用相关获取对象属性的函数
						data[i + 1][j] = (String) Util698.getObjectAttr(object, getter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return data;
	}
	
	public void converFormStringArray(String[][] data) {
		// 接收外部的String Array数据，得到对象列表
		String[] colmuns = exportColmuns.split(",");
		
		if (data == null || data.length == 0)
			return;
		else {
			int aRow = data.length;
			int aCol = data[0].length;
			int id = getUseableID();
			for (int i = 0; i < aRow; i++) {
				Object object = newobj_act.getNewObject();

				// 设置ID信息
				Map<String, Object> infoMap = Util698.getFirstFiledsInfo(object);
				Util698.setFieldValueByName(infoMap.get("name").toString(),object,id);
				
				// 不能使用getUseableID，因为此时数据还没有添加到数据库中
				id++;
				
				for (int j = 0; j < aCol; j++) {
					String setter = Util698.getSetter(colmuns[j]);

					try {
						// 注意 new Class[] {String.class} 其中的String.class表示
						// fun(String.class);
						Util698.setFieldValueByName(colmuns[j], object, data[i][j]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// meter.
				}
				objList.add(object);
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		
		CommonObjectList c = new CommonObjectList();
		c.codeColName = "code";
		c.getCodeColString();
		c.codeColName = "code,index";
		c.getCodeColString();
		
		
		//  准备的数据newobj_act
		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		
		CommonObjectList meterList = new CommonObjectList(newobj_act,"meter","ID","meterCode");
		
		//  准备的数据data
		//Object[][] data = DB.getInstance().getMeterList("1", new Meter());
		Object[][] data = DB.getInstance().getDataList("meter","terminalID='1'"," order by measureNo",new Meter());
		
		meterList.setDataFromArray(data);
		
		List<Meter> meterlist =  meterList.getList();
		for (Meter m:meterlist){
			System.out.println(m.getMeterCode());
		}
		
		String str = meterList.converToString();
		System.out.println(str);
		
		CommonObjectList meterList1 = new CommonObjectList(newobj_act,"meter","ID","meterCode");
		
		//  准备的数据type
		Type type = new TypeToken<List<Meter>>(){}.getType();
		meterList1.converFormString(str,type);
		
		meterlist =  meterList.getList();
		for (Meter m:meterlist){
			System.out.println(m.getMeterCode());
		}
		str = meterList.converToString();
		System.out.println(str);
		
		int ID = 1;
		Meter m = (Meter) meterList.getOne(ID);
		if (m!= null){
			System.out.println(m.getMeterCode());
			
	        Method method;
			try {
				method = m.getClass().getMethod("getMeterCode", new Class[] {});
		        Object value = method.invoke(m, new Object[] {});
		        System.out.println(value.getClass().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		// 添加电表数据
        Meter o = new Meter();
        o.setMeterCode("5");
        
        int id =  meterList.getUseableID();
        o.setID(id);
        o.setTerminalID("1");
        
		meterList.add(o);
		
		//meterList.delete(3);
		
		
        o = new Meter();
        o.setMeterCode("6");
        o.setID(2);
        o.setTerminalID("1");
        
		meterList.update(o);
		
	}

	
	
}
