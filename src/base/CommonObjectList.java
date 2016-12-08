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
 * ���������޹صļ��ϼ�����
 * @author xuky
 * @version 2016.10.18
 * @param <T>
 * @param <T>
 * @param <T>
 */
public class CommonObjectList<T> {
	
	// ��Ӧ�����ݿ����
	String tableName;
	
	// ������
	String idColName;
	
	// ��Ҫ�ֶ�
	String codeColName;
	
	// ��������Ԫ�ض�����ⲿ�¼�
	NewObjAction newobj_act;
	
	// �����ʵ�����Ķ���
	Object object;

	// �洢���ݵļ���
	private List objList = new ArrayList<T>();
	
	// �����������ֶ���Ϣ���ֶ���ʾ��Ϣ���ֶ�����
	String[] exportColmunNames;
	String exportColmuns;
	

	// ������
	private CommonObjectList(){
		
	}

	// ���캯���У���Ҫ���봴��������¼�
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
	
	// ������Ԫ��תΪ���϶�������
	public void setDataFromArray(Object[][] data){
		objList = Util698.array2ObjList(data,newobj_act );
	}
	
	private void init(NewObjAction newobj_act,String tableName,String codeColName,String where,String order){
		this.newobj_act = newobj_act; 
		this.tableName = tableName;
		this.codeColName = codeColName;
		object = newobj_act.getNewObject();
		// ��ȡ������׸�������Ϣ����Ϊ�����ֶ���Ϣ
		Map<String, Object> fileld = Util698.getFirstFiledsInfo(object);
		this.idColName = fileld.get("name").toString();
		Object[][] data = DB.getInstance().getDataList(tableName,where,order,object);
		setDataFromArray(data);
	}
	

	// ���ַ���תΪ���ϣ���Ҫ����������Ϣ
	public void converFormString(String s,Type type) {
		if (s == null || s.equals(""))
			objList = new ArrayList<T>();
		objList = new Gson().fromJson(s,type);
	}
	
	// ������תΪ�ַ���
	public String converToString() {
		return new Gson().toJson(objList);
	}

	public List<T> getList() {
		return objList;
	}
	
	// ��������id����ȡһ������
	public Object getOne(int id) {
		return getByCode(id,getIDString());
	}
	
	// �����ֶ���Ϣ����ȡһ������
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
	
	// �����ֶ���Ϣ����ȡһ������  
	// ������Ҫ���������ֶι����Ƚϵ�����
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
	
	// id������Ϣ����������ֵ���͵ģ���Ȼmaxȡֵ����ִ���
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

	// Boolean addtoList ��ʾ�Ƿ���Ҫ��ӵ�list������
	public String addWithParam(Object o, Boolean addtoList,Boolean noDBOp) {
		String ret = "";
		if (addtoList ){
			if (!noDBOp){
				// �����ظ������ݣ����Ǹ���������Ϣ���ң��Ǹ�����Ҫ�ֶ���Ϣ����
				// ���ܴ��ڶ���ֶε������������ͳͳתΪ�ַ��������ƥ�����
				String getter = getCodeColString();
				String value = (String) Util698.getObjectAttrs(o,getter);
		        Object tmp = null ;
		       	tmp = getByCode(value,getter);
				if (tmp != null){
					// �����˹ؼ������ظ������⣬�����������ǲ�������ӵģ�Ӧ����ʾ�û��������޸�
					ret = "�ؼ������ظ�������! �ؼ������ֶ���:"+getter +"�ؼ���������:"+value ;
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
	
	// ɾ���������
	public void deleteByCode(String code,String getter,Boolean noDBOp) {
		Object object = null;
		try {
//			for (Object o : objList){
			// ��Ϊ�漰��ɾ�����ݣ�������Ҫ����Լ��Ͻ��д���
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
		
		// xuky 2016.11.07 ��������SQL�����͵������У���ʱ����
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
			
			// ��,�ָ�����ʾ�ж���ֶ�
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
        
		// xuky 2016.11.11 �������remove��add�������ᵼ��objList�Ĵ���仯�����ⲿ���б���ʱ����ִ���ConcurrentModificationException
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
			// ��һ���Ƕ���������Ϣ
			for (int j = 0; j < aCol; j++)
				data[0][j] = exportColmunNames[j];

			for (int i = 0; i < aRow; i++) {
				for (int j = 0; j < aCol; j++) {

					// �õ���ȡ�������Եķ�������
					String getter = Util698.getGetter(colmuns[j]);

					object = objList.get(i);
					try {
						// ��̬ ���������ػ�ȡ�������Եĺ���
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
		// �����ⲿ��String Array���ݣ��õ������б�
		String[] colmuns = exportColmuns.split(",");
		
		if (data == null || data.length == 0)
			return;
		else {
			int aRow = data.length;
			int aCol = data[0].length;
			int id = getUseableID();
			for (int i = 0; i < aRow; i++) {
				Object object = newobj_act.getNewObject();

				// ����ID��Ϣ
				Map<String, Object> infoMap = Util698.getFirstFiledsInfo(object);
				Util698.setFieldValueByName(infoMap.get("name").toString(),object,id);
				
				// ����ʹ��getUseableID����Ϊ��ʱ���ݻ�û����ӵ����ݿ���
				id++;
				
				for (int j = 0; j < aCol; j++) {
					String setter = Util698.getSetter(colmuns[j]);

					try {
						// ע�� new Class[] {String.class} ���е�String.class��ʾ
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
		
		
		//  ׼��������newobj_act
		NewObjAction newobj_act =  new NewObjAction(){
			public Object getNewObject() {	return new Meter(); }
		};
		
		CommonObjectList meterList = new CommonObjectList(newobj_act,"meter","ID","meterCode");
		
		//  ׼��������data
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
		
		//  ׼��������type
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
		
		
		// ��ӵ������
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
