package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DateTimeFun;
import com.eastsoft.util.DebugSwing;


/**
 * SQLite���ݲ���
 * 
 * @author xuky
 * @version 2016-08-16
 * 
 */
public class DB {

	private volatile static DB uniqueInstance;
	
	Object[][] datatype_basic_data;

	public static DB getInstance() {
		if (uniqueInstance == null) {
			synchronized (DB.class) {
				if (uniqueInstance == null) {
					// ˫�ؼ�����
					uniqueInstance = new DB();
				}
			}
		}
		return uniqueInstance;
	}

	static Connection conn = null;
	static Statement stat = null;

	private DB() {
		String fileName = "test.db";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);
			stat = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getOADInfo(String OI) {
		String ret = "��˵��";
		ResultSet rs;
		try {
			// rs =
			// stat.executeQuery("select name from OADData where OI='"+OI+"';");
			rs = stat
					.executeQuery("select object_name from def2_obj where oi='"
							+ OI + "';");
			while (rs.next()) {
				ret = rs.getString("object_name");
				break;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String[] getTypeInfo(String No) {
		String[] ret = { "��˵��", "0" };
		
		// xuky 2016.11.07 ��Ҫ���ж�������ݿ⽻�������ݲ�ѯ
		// ���Ե�������Ϊ��list�л�ȡ
		if (datatype_basic_data == null)
			getDatatype_basic_data();
		
		// ��list�л�ȡ
		for( Object[] o: datatype_basic_data ){
			String id = (String)o[2];
			if (DataConvert.String2Int(id) == DataConvert.String2Int(No)){
				ret[0] = (String)o[0];
				ret[1] = (String)o[1];
				break;
			}
		}
		
		return ret;
	}

	private void getDatatype_basic_data(){
		String sql = "select count(*) as rownum from datatype_basic";
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();
			
			// ��ʼ�������С
			datatype_basic_data = new Object[count][3];
			rs = stat.executeQuery("select typename,bytenum,datatypeid from datatype_basic");
			int i = 0;
			while (rs.next()) { // ����ѯ�������ݴ�ӡ����
				datatype_basic_data[i][0] = rs.getString("typename");
				datatype_basic_data[i][1] = rs.getString("bytenum");
				datatype_basic_data[i][2] = rs.getString("datatypeid");
				i++;
			}
			rs.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getDARInfo(String no) {
		String ret = "��˵��";
		ResultSet rs;
		try {
			rs = stat.executeQuery("select name from DARData where no='" + no
					+ "';");
			while (rs.next()) { // ����ѯ�������ݴ�ӡ����
				ret = rs.getString("Name");
				break;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	public String getTreeData(int code,int seq) {
		return getTreeData(code,seq,false);
	}
	public String getTreeData(int code,int seq,Boolean retWithFcode) {
		return getTreeData(code,seq,retWithFcode,false,false);
	}

	// �õ��ڵ� �ݹ�ķ�ʽ����ýڵ㼰�������ӽڵ��id��Ϣ
	// seq =-1��ʾ����seqԼ������
	// ���ݵ�˳���ǹ�����ȵ�  
	// isWide ʱΪ�������
	public String getTreeData(int code,int seq,Boolean retWithFcode,Boolean WithRoot, Boolean isWide) {
		
		if (code == 0){
			String msg = "���ܱ���datatype_user��code=0�ڵ�";
			DebugSwing.showMsg(msg);
			//System.out.println(msg);
			System.exit(0);
		} 
		String ret = "";
		String sql = "";
		
		
		if (seq == -1)
			sql = "select id,code,fcode,datatype from datatype_user where fcode=" + code +" order by seq";
		else
			// CHOICE���͵ģ����seqԼ������
			sql = "select id,code,fcode,datatype from datatype_user where fcode=" + code +" and seq="+seq+" order by seq";
		
		
		ResultSet rs;
		// xuky 2016.12.01 ԭ�ȵĴ����ǲ����ѲŽ��й�����ȵģ���Ϊʹ��ͬһ��Statementʱ��executeQuery�ᵼ��ǰ��ı�����
		Statement local_stat = null;
		try {
			local_stat = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int[] codes = new int[2000];
		int codes_num = -1;
		try {
			rs = local_stat.executeQuery(sql);
			while (rs.next()) {
				
				if (!retWithFcode)
					ret += rs.getString("id") + ";";
				else
					ret += rs.getString("id") + ":"+rs.getInt("fcode")+";";
				
				String datatype =rs.getString("datatype");
				if (datatype!=null)
					// xuky 2016.11.17 ����жϣ������WithRoot����Ҫ��array���б���
					if (WithRoot){
						if (datatype.indexOf("CHOICE")>=0||
								datatype.equals("SEQUENCE OF")){
							continue;
						}
						if (datatype.equals("array")){
							// xuky 2016.11.17  1���״�ִ�У������ľ����������
							// xuky 2016.11.17  2����Ҫ����ִ��
							//WithRoot = false;
						}
					}
					else{
						if (datatype.equals("array")||
								datatype.indexOf("CHOICE")>=0||
								datatype.equals("SEQUENCE OF")){
							// ��������顢ѡ�����͵����ݣ�����������������ӽڵ㣬�û�ѡ�������CHOICEʱ�����ֶ������˲���
							continue;
						}
					}
				int nextCode = rs.getInt("code");
				codes_num++;
				// 1�����ȼ�¼
				codes[codes_num] = nextCode;
				if (isWide){
					int[] code_wide = new int[1];
					code_wide[0] = nextCode; 
					ret += getTreeData(code_wide[0],-1,retWithFcode,WithRoot,isWide);
				} 
			}
			rs.close();
			if (codes_num == -1) {
				return ret;
			}
			if (!isWide){
				// 2��Ȼ����еݹ����
				for (int i = 0; i <= codes_num; i++) {
					ret += getTreeData(codes[i],-1,retWithFcode,WithRoot,isWide);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Object[][] getAllOADList(String type) {
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String table = "obj_attr";
			String column_idx = "attr_idx", column_name = "attr_name", column_spec = "readonly", column_id = "attr_id";
			String def_val_readonly = "1";
			if (type.equals("Func")) {
				table = "obj_meth";
				column_idx = "method_idx";
				column_name = "method_name";
				column_spec = "haspara";
				column_id = "method_id";
				def_val_readonly = "0";
			}
			String sql = "select  count(*) as rownum from ( "
					+ "select a.oi,'0' as no,'��'||a.oi||'��'||a.object_name as name, "
					+ "       "
					+ def_val_readonly
					+ " as readonly, 0 as order_no,0 as attr_id  "
					+ "       from def2_obj a, "
					+ table
					+ " b  "
					+ "       where a.oi = b.oi"
					+ "       group by a.oi,a.object_name "
					+ "union  "
					+ "select oi,CAST("
					+ column_idx
					+ " AS char) as no,"
					+ column_name
					+ " as name, "
					+ "       "
					+ column_spec
					+ " as readonly, "
					+ column_idx
					+ " as order_no,"
					+ column_id
					+ " as attr_id "
					+ "       from "
					+ table
					+ "     )  ;";
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();

			
			data1 = new Object[count][20];

			sql = "select  * from ( "
					+ "select 0 as selected,a.oi as oi,'0' as no,'��'||a.oi||'��'||a.object_name as name, "
					+ "       "
					+ def_val_readonly
					+ " as readonly, 0 as order_no,0 as attr_id ,0 as select_old "
					+ "       from def2_obj a, "
					+ table
					+ " b  "
					+ "       where a.oi = b.oi"
					+ "       group by a.oi,a.object_name "
					+ "union  "
					+ "select selected,oi,CAST("
					+ column_idx
					+ " AS char) as no,"
					+ column_name
					+ " as name, "
					+ "       "
					+ column_spec
					+ " as readpnly, "
					+ column_idx
					+ " as order_no,"
					+ column_id
					+ " as attr_id, selected as select_old"
					+ "       from "
					+ table + "     )  " + "order by oi,order_no ;";
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				int selected = rs.getInt("selected");
				if (selected == 1)
					data1[i][0] = true;
				else
					data1[i][0] = false;
				data1[i][1] = rs.getString("oi");
				data1[i][2] = rs.getString("no");
				data1[i][3] = rs.getString("name");
				int readonly = rs.getInt("readonly");
				if (readonly == 1)
					data1[i][4] = true;
				else
					data1[i][4] = false;
				data1[i][5] = rs.getInt("attr_id");
				data1[i][6] = rs.getInt("select_old");
				i++;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data1;
	}

	public void setObjAttrSelected(String val, String define, int id,
			String type) {
		String table = "obj_attr_value";
		if (type.equals("Func"))
			table = "obj_meth_para";
		String sql = "update " + table + " set datavalue='" + val
				+ "',definevalue='" + define + "' where id=" + id + ";";
		try {
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setObjAttrSelected(Vector v, String type) {
		int size = v.size();

		// ����Vector�е�Ԫ��
		int attr_id = 0, select_old;
		boolean selected = false;
		String sql = "";
		String table = "obj_attr", column = "attr_id";
		if (type.equals("Func")) {
			table = "obj_meth";
			column = "method_id";
		}
		for (int i = 0; i < v.size(); i++) {
			
			// ���ⷢ��������Ϣ
			String[] s = {"save progress","",""};
			s[2] = Util698.getPercent(i+1,v.size(),2);
			Publisher.getInstance().publish(s);
			
			attr_id = (int) ((Vector) v.elementAt(i)).elementAt(5);
			if (attr_id == 0)
				continue;
			select_old = (int) ((Vector) v.elementAt(i)).elementAt(6);
			selected = (boolean) ((Vector) v.elementAt(i)).elementAt(0);
			if ((selected && select_old == 0) || (!selected && select_old == 1)) {
				if (selected)
					sql = "update " + table + " set selected=1 where " + column
							+ "=" + attr_id + ";";
				else
					sql = "update " + table + " set selected=0 where " + column
							+ "=" + attr_id + ";";
				try {
					stat.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Object[][] getOADList(String type) {
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String table = "obj_attr";
			String column_idx = "attr_idx", column_name = "attr_name", column_spec = "readonly", column_id = "attr_id";
			String def_val_readonly = "1";
			if (type.equals("Func")) {
				table = "obj_meth";
				column_idx = "method_idx";
				column_name = "method_name";
				column_spec = "haspara";
				column_id = "method_id";
				def_val_readonly = "0";
			}
			String sql = "select  count(*) as rownum from ( "
					+ "select a.oi as oi,'0' as no,'��'||a.oi||'��'||a.object_name as name, "
					+ "       "
					+ def_val_readonly
					+ " as readonly, 0 as order_no,0 as attr_id  "
					+ "       from def2_obj a, "
					+ table
					+ " b  "
					+ "       where a.oi = b.oi and selected =1 "
					+ "       group by a.oi,a.object_name "
					+ "union  "
					+ "select oi,CAST("
					+ column_idx
					+ " AS char) as no,"
					+ column_name
					+ " as name, "
					+ "       "
					+ column_spec
					+ " as readonly, "
					+ column_idx
					+ " as order_no,"
					+ column_id
					+ " as attr_id"
					+ "       from "
					+ table
					+ "       where selected =1 " + "     )  ;";
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();

			// xuky 2016.09.01 ��ĳЩ����»���ִ�������Խ��
			//data1 = new Object[count][20];
			data1 = new Object[count][6];

			// String = "attr_idx",="attr_name", ="readonly", column_id =
			// "attr_id";
			sql = "select  * from ( "
					+ "select a.oi as oi,'0' as no,'��'||a.oi||'��'||a.object_name as name, "
					+ "       "
					+ def_val_readonly
					+ " as readonly, 0 as order_no,0 as attr_id  "
					+ "       from def2_obj a, "
					+ table
					+ " b  "
					+ "       where a.oi = b.oi and selected =1 "
					+ "       group by a.oi,a.object_name "
					+ "union  "
					+ "select oi,CAST("
					+ column_idx
					+ " AS char) as no,"
					+ column_name
					+ " as name, "
					+ "       "
					+ column_spec
					+ " as readonly, "
					+ column_idx
					+ " as order_no,"
					+ column_id
					+ " as attr_id"
					+ "       from "
					+ table
					+ "       where selected =1 "
					+ "     )  "
					+ "order by oi,order_no ;";
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				data1[i][0] = false;
				data1[i][1] = rs.getString("oi");
				data1[i][2] = rs.getString("no");
				data1[i][3] = rs.getString("name");
				int readonly = rs.getInt("readonly");
				if (readonly == 1)
					data1[i][4] = true;
				else
					data1[i][4] = false;
				data1[i][5] = rs.getInt("attr_id");
				i++;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data1;
	}

	public Object[][] getAttrValList(String attr_id, String type) {
		Object[][] data1 = null;
		String table = "obj_attr_value", column = "attr_id";
		if (type.equals("Func")) {
			table = "obj_meth_para";
			column = "method_id";
		}
		// ����ܵ�����
		String sql = "select count(*) as rownum from " + table + " where "
				+ column + " = " + attr_id + ";";
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();
			
			// ��ʼ�������С
			data1 = new Object[count][9];
			// �ݹ�ķ�ʽ��ôӸ��ڵ㵽ÿ���ӽڵ������  , ���ڵ��code=0,datatype=""
			
			// xuky 2016.08.01 ���ݵ�˳����������ȵ�
			// xuky 2016.09.13 ���ڵ��arrayidx=001
			String arrayidx_f = "001";
			int num = getAttrlVal(attr_id,type,data1,0,0,"",arrayidx_f,0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data1;
	}
	
	// ���ڵ����ӽڵ�
	private int getAttrlVal(String attr_id, String type,Object[][] data1,int i,int fcode_para,String datatype_f,String arrayidx_f ,int loop){
		loop++;
		int ret = i;
		String table = "obj_attr_value", column = "attr_id";
		if (type.equals("Func")) {
			table = "obj_meth_para";
			column = "method_id";
		}
		String sql = "";
		
		//  xuky 2016.09.13 ���Ժ������ݸ�ʽ����  ��Ϊ001 ����Ϊ001_001
		sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx,seq from "
				+ table + " where " + column + " = " + attr_id +" and fcode="+fcode_para+" and arrayidx like '"+arrayidx_f+"%'" 
				+" order by arrayidx,seq";
		
		/*  xuky 2016.09.13 ���Ժ������ݸ�ʽ����
		// ��֯�����ӽڵ��SQL��䣬���ܽ�����fcode��code�ĸ��ӹ�ϵ������Ҫ�ж�arrayidx������
		// 1�����ڵ�
		if (datatype_f.equals(""))
			sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx from "
					+ table + " where " + column + " = " + attr_id +" and fcode="+fcode_para
					+" order by seq, arrayidx;";
		// 2����������
		else if(datatype_f.equals("array") ||datatype_f.equals("SEQUENCE OF") ){
			// �������� ��Ϊnull ����Ϊ000...001...
			if (arrayidx_f==null)
				sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx from "
						+ table + " where " + column + " = " + attr_id +" and fcode="+fcode_para+" and length(arrayidx)=3 "
						+" order by seq, arrayidx;";
			else
				// N������ ��Ϊ000����Ϊ000,000...000,001...
				sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx from "
						+ table + " where " + column + " = " + attr_id +" and fcode="+fcode_para+" and substr(arrayidx,1,length(arrayidx)-4)= '"+arrayidx_f+"'" 
						+" order by seq, arrayidx;";
		}
		else{
			// 3���������ͣ���������������ɲ��֣����Ϊnull����Ϊnull
			if (arrayidx_f== null || arrayidx_f.equals("null"))
				sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx from "
						+ table + " where " + column + " = " + attr_id +" and fcode="+fcode_para+" and arrayidx is null "
						+" order by seq, arrayidx;";
			else
				// �������ͣ�������������ɲ��֣����Ϊ 000����Ϊ000
				sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx from "
						+ table + " where " + column + " = " + attr_id +" and fcode="+fcode_para+" and arrayidx='"+arrayidx_f+"'"
						+" order by seq, arrayidx;";
		}
		*/
		ResultSet rs;
		String datatype="",arrayidx="";
		int[] temp = new int[100];
		int j =0;
		int code=0;
		try {
			// ���Ƚ����л�ȡ�����ݱ��浽�����У�
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				temp[j] = rs.getInt("id");
				j++;
			}
			rs.close();
			
			String loop_str = "";
			for(int k=0;k<loop-1;k++)
				loop_str += "��";
			
			// Ȼ���ٽ�������ĵݹ���������ֱ����rs.next()ѭ���н��У��ᵼ��rs�������ݶ�ʧ
			for( int k=0;k<j;k++ ){
				int id_t = temp[k];
				sql = "select id,dataname,datavalue,datatype,definevalue,id,code,fcode,arrayidx,seq from "
						+ table + " where id="+id_t
						+" order by arrayidx ,seq";
				rs = stat.executeQuery(sql);
				while (rs.next()) {
					data1[i][0] = loop_str+rs.getString("dataname");
					datatype = rs.getString("datatype");
					data1[i][1] = datatype;
					data1[i][2] = rs.getString("datavalue");
					data1[i][3] = rs.getString("definevalue");
					int id = rs.getInt("id");
					data1[i][4] = id;
					code = rs.getInt("code");
					data1[i][6] = code;
					data1[i][7] = rs.getInt("fcode");
					arrayidx = rs.getString("arrayidx");
					data1[i][5] = arrayidx;
					data1[i][8] = rs.getInt("seq");
					i++;
					i = getAttrlVal(attr_id,type,data1,i,code,datatype,arrayidx,loop);
					ret = i;
				}
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	// ���ⲿ���ã��������ݿ⣬�޻�ȡ��������
	public int addArray(int id, String type) {
		return addArray(id,type,null,true,false);
	}

	// ���Ԫ��  ��datatype_use���ҵ������Ҫ���ӵĺ�������
	public int addArray(int id, String type,Object[][] data_input,Boolean DBOpFlag,Boolean WithRoot) {
		int code = 0, attr_id = 0, package_id = 0, max_id = 0;
		Object[][] data_new = null;
		
		if (!DBOpFlag){
			code = id;
			id = 0;
		}
			
		int add_firstID = id;
		// �����Ҫ���ӵ�����
		try {
			String table = "obj_attr_value", column = "attr_id";
			if (type.equals("Func")) {
				table = "obj_meth_para";
				column = "method_id";
			}
			String arrayidx = null, datavalue = "0", datatype="";
			String sql = "select code," + column
					+ " as attr_id,package_id,arrayidx,datavalue,datatype from " + table
					+ " where id =" + id + ";";
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				code = rs.getInt("code");
				attr_id = rs.getInt("attr_id");
				package_id = rs.getInt("package_id");
				arrayidx = rs.getString("arrayidx");
				datavalue = rs.getString("datavalue");
				datatype = rs.getString("datatype");
			}
			sql = "select max(id) as max_id from " + table + "; ";
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				max_id = rs.getInt("max_id");
			}
			rs.close();
			
			int array_idx_num = 0;
			
			int seq = -1;
			if (datatype.indexOf("CHOICE")>=0)
				// �����CHOISE���͵ģ������ӽڵ��ʱ����Ҫ���seqԼ������
				seq = DataConvert.String2Int(datavalue);
			else
				// ������������͵ģ����ӵĽڵ�idx��Ҫ����
				array_idx_num = DataConvert.String2Int(datavalue);
			
			//String adds = DB.getInstance().getTreeData(code,seq);
			
			// xuky 2016.09.13 ����Ҫ�и��ڵ���Ϣ����Ҫʹ�ø��ڵ��arrayidx��
			
			String adds = DB.getInstance().getTreeData(code,seq,true,WithRoot,true);
			
			if (WithRoot){
				String sql1 = "select id,fcode from datatype_user where code=" + code;
				int id1 = (int) getOneData(sql1,"id","int");
				int fcode1 = (int) getOneData(sql1,"fcode","int");
				adds = id1 + ":"+ + fcode1+ ";" + adds;
			}
			
			
			
			if (adds.equals("")){
				//��ʾû��������ݣ����ؼ���
				sql = "select code,fcode,seq,dataname,datatype,defaultvalue,definevalue,remark from datatype_user where code ="+code;
				rs = stat.executeQuery(sql);
				data_new = new Object[1][10]; 
				int arrayRow = 0;
				while (rs.next()) {
					data_new[arrayRow][0] = rs.getString("dataname");
					data_new[arrayRow][1] = rs.getString("datatype");							
					data_new[arrayRow][2] = rs.getString("defaultvalue");
					data_new[arrayRow][3] = rs.getString("definevalue");						
					data_new[arrayRow][4] = 0;							
					data_new[arrayRow][5] = "";					
					data_new[arrayRow][6] = rs.getString("code");							
					data_new[arrayRow][7] = rs.getInt("fcode");
					data_new[arrayRow][8] = rs.getInt("seq");							
					data_new[arrayRow][9] = rs.getString("remark");					
				}
				arraySetData(data_new,data_input);
				return 0;
			}
			
			Map<String, Object> codeAndArrayidx = new HashMap();
			
			String[] addsArray = adds.split(";");
			int i = 0;
			PreparedStatement prep = conn.prepareStatement("insert into "
					+ table + " values (?, ?, ?,?, ?, ?,?, ?, ?,?, ?);");
			Boolean firstLine = true;
			Boolean exeuteUpdate = false;
			String insert_arrayidx = arrayidx;
			
			data_new = new Object[addsArray.length][10]; 
			
			codeAndArrayidx.put(DataConvert.int2String(code), arrayidx+";"+array_idx_num);			
			
			int arrayRow = 0; 
			
			for (String adddata_id : addsArray) {
				
				String id_part1 = adddata_id.split(":")[0];
				int fcode = DataConvert.String2Int(adddata_id.split(":")[1]);
				
				if (adddata_id==null || adddata_id.equals("")) 
					continue;
				
				sql = "select code,fcode,seq,dataname,datatype,defaultvalue,definevalue,remark from datatype_user where id ="
						+ id_part1 + ";";
				rs = stat.executeQuery(sql);
				while (rs.next()) {
					i++;
					
					Object codeArrayidx = codeAndArrayidx.get(DataConvert.int2String(fcode));
					if (codeArrayidx!=null){
						// ���ڵ��arrayidx 
						insert_arrayidx = ((String)codeArrayidx).split(";")[0];
						// ��ǰ�ڵ��num
						array_idx_num = DataConvert.String2Int(((String)codeArrayidx).split(";")[1]);
						array_idx_num++;
						codeAndArrayidx.put(DataConvert.int2String(rs.getInt("fcode")), insert_arrayidx+";"+array_idx_num);
					}
					
					prep.setInt(1, max_id + i);
					prep.setInt(2, package_id);
					prep.setInt(3, attr_id);
					prep.setInt(4, rs.getInt("code"));
					prep.setInt(5, rs.getInt("fcode"));
					seq = rs.getInt("seq");
					prep.setInt(6, seq);
					if (firstLine) {
						// ��¼�׸����ӵ����ݵ�id���������Ӻ�Ķ�λ
						add_firstID = max_id + i;
						
						// ��������ʱ���׸�������ӡ���x��������Ϣ
						if (datatype.indexOf("CHOICE")<0){
							prep.setString(7,
									"��" + (DataConvert.String2Int(datavalue) + 1)
											+ "����" + rs.getString("dataname"));
						}
						else
							prep.setString(7, rs.getString("dataname"));
					} else
						prep.setString(7, rs.getString("dataname"));
					
					
					prep.setString(8, rs.getString("datatype"));
					prep.setString(9, rs.getString("defaultvalue"));
					prep.setString(10, rs.getString("definevalue"));

					// ��arrayidx =  ��arrayidx+xxx  xxx=0011-999
					
					/*
					if (datatype.indexOf("CHOICE") >= 0){
						String tmp = DataConvert.IntToBinString(seq);
						tmp = DataConvert.BinStr2String(tmp, 3);
						insert_arrayidx += "_"+tmp;
					}
					else{
						int t = DataConvert.String2Int(datavalue)+1;
						String tmp = DataConvert.IntToBinString(t);
						tmp = DataConvert.BinStr2String(tmp, 3);
						insert_arrayidx += "_"+tmp;
					}
					*/
					String tmp = DataConvert.IntToBinString(array_idx_num);
					tmp = DataConvert.BinStr2String(tmp, 3);
					insert_arrayidx += "_"+tmp;
					
					prep.setString(11, insert_arrayidx);
					//System.out.println("addArray=>"+rs.getString("dataname"));
					data_new[arrayRow][0] = rs.getString("dataname");
					data_new[arrayRow][1] = rs.getString("datatype");							
					data_new[arrayRow][2] = rs.getString("defaultvalue");
					data_new[arrayRow][3] = rs.getString("definevalue");						
					data_new[arrayRow][4] = max_id + i;							
					data_new[arrayRow][5] = insert_arrayidx;					
					data_new[arrayRow][6] = rs.getString("code");							
					data_new[arrayRow][7] = rs.getInt("fcode");
					data_new[arrayRow][8] = rs.getInt("seq");							
					data_new[arrayRow][9] = rs.getString("remark");					
					arrayRow++;
					
					codeAndArrayidx.put(DataConvert.int2String(rs.getInt("code")), insert_arrayidx+";0");
					/*
					if (!datatype.equals("CHOICE")){
						String tmp = "";
						if (datavalue.equals("0"))
							tmp = "000";
						else {
							int t = DataConvert.String2Int(datavalue);
							tmp = DataConvert.IntToBinString(t);
							tmp = DataConvert.BinStr2String(tmp, 3);
						}

						if (arrayidx == null)
							prep.setString(11, tmp);
						else {
							prep.setString(11, arrayidx + "," + tmp);
						}
					}
					else
						prep.setString(11, arrayidx);
					*/
					exeuteUpdate = true;
					prep.addBatch();
					
				}
				rs.close();
				
				firstLine = false;
			}
			
			// �ж��Ƿ���Ҫ��������   DBOpFlag�Ƿ���Ҫ�������ݿ���ز���
			if (exeuteUpdate && DBOpFlag){
				
				conn.setAutoCommit(false);
				prep.executeBatch();
				conn.setAutoCommit(true);
			}

			if (datatype.indexOf("CHOICE")<0 && DBOpFlag ){
				// ��������ֵ��һ
				sql = "update " + table + " set datavalue='"
						+ (DataConvert.String2Int(datavalue) + 1) + "' where id="
						+ id + ";";
				stat.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// ����ֱ�ӵĽ���data_input = data_new  ����ַ��ֵ��Ч��
		// ֻ�ܶ�data_input�����ݽ��е��� ���ڴ������޸���Ч��
		if (!DBOpFlag){
			arraySetData(data_new,data_input);
//			int i=0,j=0;
//			for( Object[] o :data_new ){
//				for( Object object: o ){
//					if (object!= null)
//						data_input[i][j] = object;
//					j++;
//				}
//				i++;
//				j = 0;
//			}
		}
		return add_firstID;
	}
	
	private void arraySetData(Object[][] data_new,Object[][] data_input){
		// ����ֱ�ӵĽ���data_input = data_new  ����ַ��ֵ��Ч��
		// ֻ�ܶ�data_input�����ݽ��е��� ���ڴ������޸���Ч��
		int i=0,j=0;
		for( Object[] o :data_new ){
			for( Object object: o ){
				if (object!= null)
					data_input[i][j] = object;
				j++;
			}
			i++;
			j = 0;
		}
	}

	public int delArray(int id, String type) {
		String table = "obj_attr_value", column = "attr_id";
		if (type.equals("Func")) {
			table = "obj_meth_para";
			column = "method_id";
		}
		String sql = "select " + column + " as attr_id,datavalue,arrayidx,datatype,code from " + table
				+ " where id =" + id + ";";
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			int attr_id = 0, code=0;
			String datavalue = "0", arrayidx = null, datatype = "";
			while (rs.next()) {
				attr_id = rs.getInt("attr_id");
				datavalue = rs.getString("datavalue");
				arrayidx = rs.getString("arrayidx");
				datatype = rs.getString("datatype");
				code = rs.getInt("code");
			}
			rs.close();
			
			if (datatype.equals("array") || datatype.equals("SEQUENCE OF")){
				// ��������ֵ�Ѿ�Ϊ0����ʾ��Ԫ���ˣ������������
				if (datavalue.equals("0"))
					return 0;
				int t = DataConvert.String2Int(datavalue);
				String s = DataConvert.BinStr2String(DataConvert.IntToBinString(t),
						3);
				String where_val = " where " + column + "=" + attr_id;
				// xuky 2016.09.13 
				//if (arrayidx == null)
				//	where_val += " and arrayidx like('" + s + "%')";
				//else
					//where_val += " and arrayidx like('" + arrayidx + "," + s + "%')";
				where_val += " and arrayidx like('" + arrayidx+ "_"+s+"%')";

				sql = "delete from " + table + where_val + ";";
				stat.executeUpdate(sql);

				// ��������ֵ��һ
				sql = "update " + table + " set datavalue='"
						+ (DataConvert.String2Int(datavalue) - 1) + "' where id="
						+ id + ";";
				stat.executeUpdate(sql);
			}
			else if (datatype.indexOf("CHOICE")>=0){
				// ɾ���Դ�Ϊ�������нڵ�  ע��arrayindx�Ĵ���
				Object[][] data1 = new Object[300][20];
				// �ݹ�ķ�ʽ��ôӸ��ڵ㵽ÿ���ӽڵ������ 
				int num = getAttrlVal(DataConvert.int2String(attr_id),type,data1,0,code,datatype,arrayidx,0);
 				
 				for(int i=0;i<num;i++){
 					int del_id = (int)data1[i][4];
 					
 					sql = "delete from " + table + " where id ="+del_id+";";
 					stat.executeUpdate(sql);
 				}
				// CHOICE��ֵΪ��
 				sql = "update " + table + " set datavalue=null where id="+ id + ";";
				stat.executeUpdate(sql);
			}
			else{
				System.out.println("������벻����");
				System.exit(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
	
	public String[] getBasicDataTypeInfo(String type) {
		String[] ret = {"",""};
		ResultSet rs;
		try {
			rs = stat.executeQuery("select datatypeid,bytenum from datatype_basic where typename='"+ type + "';");
			while (rs.next()) {
				ret[0] = rs.getString("datatypeid");
				ret[1] = rs.getString("bytenum");
				break;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	
	public Object[][] getDataTypeUserList(int id) {
			Object[][] data1 = null;
			ResultSet rs;
			try {
				String sql = "select  count(*) as rownum from datatype_user ";
				if (id != -1)
					sql += " where id="+id+";";
				else
					sql += ";";
				
				rs = stat.executeQuery(sql);
				int count = 0;
				while (rs.next()) {
					count = rs.getInt("rownum");
					break;
				}
				rs.close();
				
				
				data1 = new Object[count][20];
				sql = "select  * from datatype_user ";
				if (id != -1)
					sql += " where id="+id+";";
				else
					sql += ";";
				
				rs = stat.executeQuery(sql);
				int i = 0;
				while (rs.next()) {
					data1[i][0] = rs.getInt("id");
					data1[i][1] = rs.getInt("code");
					data1[i][2] = rs.getInt("fcode");
					data1[i][3] = rs.getInt("seq");
					data1[i][4] = rs.getString("dataname");
					data1[i][5] = rs.getString("datatype");
					data1[i][6] = rs.getString("defaultvalue");
					data1[i][7] = rs.getString("definevalue");
					data1[i][8] = rs.getString("remark");
					i++;
				}
				rs.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		return data1;
	}
	
	public int getDataTypeUserIDByCode(int code) {
		int ret = -1;
		String sql = "select id from datatype_user where code = "+code;
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				ret = rs.getInt("id");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public Object[][] getDataTypeUserListByIDs(String ids) {
		Object[][] data1 = null;
		ResultSet rs;
		
		String[] addsArray = ids.split(";");
		String sql = "";
		data1 = new Object[addsArray.length][20];
		try {
			int i = 0;
			for (String s : addsArray) {
				if (s==null || s.equals("")) 
					continue;
				sql = "select  * from datatype_user where id ="+s;
				rs = stat.executeQuery(sql);
				while (rs.next()) {
					data1[i][0] = rs.getInt("id");
					data1[i][1] = rs.getInt("code");
					data1[i][2] = rs.getInt("fcode");
					data1[i][3] = rs.getInt("seq");
					data1[i][4] = rs.getString("dataname");
					data1[i][5] = rs.getString("datatype");
					data1[i][6] = rs.getString("defaultvalue");
					data1[i][7] = rs.getString("definevalue");
					data1[i][8] = rs.getString("remark");
					i++;
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	return data1;
}
	
	
	public int getMaxId(String table,String column) {
		int max_id = 1;
		// String sql = "select max(id) as max_id from datatype_user; ";
		String sql = "select max("+column+") as max_id from "+table+"; ";
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				max_id = rs.getInt("max_id")+1;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max_id;
	}
	
	public int getDataTypeUserMaxCode() {
		int max_id = 1;
		String sql = "select max(code) as max_id from datatype_user; ";
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				max_id = rs.getInt("max_id")+1;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max_id;
	}
	
	public void addDataTypeUser(Object[] data) {
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into datatype_user"
					+ " values (?, ?, ?,?, ?, ?,?, ?, ?);");
			
			prep.setInt(1, (int)data[0]);
			prep.setInt(2, (int)data[1]);
			prep.setInt(3, (int)data[2]);
			prep.setInt(4, (int)data[3]);
			prep.setString(5, (String)data[4]);
			prep.setString(6, (String)data[5]);
			prep.setString(7, (String)data[6]);
			prep.setString(8, (String)data[7]);
			prep.setString(9, (String)data[8]);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addData(String table,Object obj) {
		// Ҫ��������ԵĴ�������������ݿ��е���ȫһ��
		
		PreparedStatement prep;
		try {
			String sql = "insert into " + table + " values (";
			
			List<Map> l = Util698.getFiledsInfo(obj);
			int colNum = l.size();
			
			// ��������ת����������
			Object[] data = new Object[colNum];
			
			for (int i=0;i<colNum;i++)
				sql = sql + "?,";
			sql = sql.substring(0, sql.length()-1)+")";
			prep = conn.prepareStatement(sql);
			
			int i = 1;
			for( Map<String, Object> info:l ){
				String name = info.get("name").toString();
				String type = info.get("type").toString();
				Object val = Util698.getFieldValueByName(name,obj);
				// ��������ת����������
				data[i-1] = val;
				if (type.toLowerCase().indexOf("int") >=0)
					prep.setInt(i, (int)val);
				if (type.indexOf("String") >=0)
					prep.setString(i, (String)val);
				i++;
			}
			//prep.setInt(1, (int)data[0]);
			//prep.setString(9, (String)data[8]);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			// xky 2016.11.10 ����Ҫ����һЩ������ݿ����
			if (table.equals("def4_meth") || table.equals("def3_attr") ){
				int object_id = (int)data[2], idx = (int)data[3];  
				dataSync(object_id,idx,table);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addAllData(String table,List objList) {
		// Ҫ��������ԵĴ�������������ݿ��е���ȫһ��
		if (objList == null || objList.size() == 0)
			return;
		PreparedStatement prep;
		try {
			String sql = "insert into " + table + " values (";
			
			List<Map> l = Util698.getFiledsInfo(objList.get(0));
			int colNum = l.size();
			for (int i=0;i<colNum;i++)
				sql = sql + "?,";
			sql = sql.substring(0, sql.length()-1)+")";
			prep = conn.prepareStatement(sql);
			
			for(Object obj: objList){
				int i = 1;
				for( Map<String, Object> info:l ){
					String name = info.get("name").toString();
					String type = info.get("type").toString();
					Object val = Util698.getFieldValueByName(name,obj);
					if (type.toLowerCase().indexOf("int") >=0)
						prep.setInt(i, (int)val);
					if (type.indexOf("String") >=0)
						prep.setString(i, (String)val);
					i++;
				}
				prep.addBatch();
			}
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void updateData(String table,Object obj) {
		// ����id��Ϣ���޸�ԭ������
		PreparedStatement prep;
		try {
			String sql = "update " + table +" set ";
			List<Map> l = Util698.getFiledsInfo(obj);
			
			// ��������ת����������
			Object[] data = new Object[l.size()];
			
			int i = 0;
			String idColname ="";
			for( Map<String, Object> info:l ){
				String name = info.get("name").toString();
				if (i == 0)
					idColname =name;
				else									
					sql = sql + name +"=?,";
				i++;
			}
			sql = sql.substring(0, sql.length()-1);
			sql = sql + " where "+idColname+"=?";
			
			//System.out.println("updateData=>"+sql);
			prep = conn.prepareStatement(sql);
			
			i = 0;
			int lastNum = l.size();
			for( Map<String, Object> info:l ){
				int row = i;
				if (i == 0)
					row = lastNum;
				String name = info.get("name").toString();
				String type = info.get("type").toString();
				Object val = Util698.getFieldValueByName(name,obj);
				//System.out.println("updateData=>"+name);
				if (type.toLowerCase().indexOf("int") >=0)
					prep.setInt(row, (int)val);
				if (type.indexOf("String") >=0)
					prep.setString(row, (String)val);
				
				// ��������ת����������
				data[i] = val;
						
				i++;
			}
			
			prep.addBatch();
					
			conn.setAutoCommit(false);
			int[] r= prep.executeBatch();
			conn.setAutoCommit(true);
			
			// xky 2016.11.10 ����Ҫ����һЩ������ݿ����
			if (table.equals("def4_meth") || table.equals("def3_attr") ){
				int object_id = (int)data[2], idx = (int)data[3];  
				dataSync(object_id,idx,table);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addObj(Object[] data) {
		
		int object_id = (int)data[0];
		int class_id = (int)data[3];

		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into def2_obj"
					+ " values (?, ?, ?,?, ?, ?);");
			prep.setInt(1, (int)data[0]);
			prep.setString(2, (String)data[1]);
			prep.setString(3, (String)data[2]);
			prep.setInt(4, (int)data[3]);
			prep.setInt(5, (int)data[4]);
			prep.setInt(6, (int)data[5]);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dataSyncByClass_id(object_id, class_id,"attr");
		dataSyncByClass_id(object_id, class_id,"method");
		
		
	}
	
	public void addIFClass(Object[] data) {
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into def1_ic"
					+ " values (?, ?, ?,?, ?);");
			prep.setInt(1, (int)data[0]);
			prep.setInt(2, (int)data[1]);
			prep.setString(3, (String)data[2]);
			prep.setInt(4, (int)data[3]);
			prep.setInt(5, (int)data[4]);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateDataTypeUser(Object[] data) {
		// ����id��Ϣ���޸�ԭ������
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("update datatype_user"
					+ " set code=?, fcode=?, seq=?, dataname=?, "
					+ " datatype=?, defaultvalue=?, definevalue=?, remark=? where id=?;");
			
			prep.setInt(1, (int)data[1]);
			prep.setInt(2, (int)data[2]);
			prep.setInt(3, (int)data[3]);
			prep.setString(4, (String)data[4]);
			prep.setString(5, (String)data[5]);
			prep.setString(6, (String)data[6]);
			prep.setString(7, (String)data[7]);
			prep.setString(8, (String)data[8]);
			prep.setInt(9, (int)data[0]);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			int[] r= prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	public void addAttr(Object[] data) {
		addAttr(data,"attr");
	}
	
	
	public void addAttr(Object[] data,String type) {
		String table = "def4_meth";
		if (type.equals("attr"))
			table = "def3_attr";
		PreparedStatement prep;
		try {
			if (type.equals("attr")){
				prep = conn.prepareStatement("insert into " + table
						+ " values (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?);");
				
				prep.setInt(1, (int)data[0]);
				prep.setInt(2, (int)data[1]);
				prep.setInt(3, (int)data[2]);
				prep.setInt(4, (int)data[3]);
				prep.setString(5, (String)data[4]);
				prep.setInt(6, (int)data[5]);
				prep.setInt(7, (int)data[6]);
				prep.setInt(8, (int)data[7]);
				prep.setInt(9, (int)data[8]);
				prep.setString(10, (String)data[9]);
				prep.setInt(11, (int)data[10]);
				
			}
			else{
				prep = conn.prepareStatement("insert into " + table
						+ " values (?, ?, ?,?, ?, ?, ?, ?, ?, ?);");
				
				prep.setInt(1, (int)data[0]);
				prep.setInt(2, (int)data[1]);
				prep.setInt(3, (int)data[2]);
				prep.setInt(4, (int)data[3]);
				prep.setString(5, (String)data[4]);
				prep.setInt(6, (int)data[7]);
				//prep.setInt(7, (int)data[6]);
				prep.setInt(7, (int)data[5]);
				prep.setInt(8, (int)data[10]);
				prep.setInt(9, (int)data[8]);
				prep.setString(10, (String)data[9]);
				
			}
			
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			int object_id = (int)data[2], idx = (int)data[3];  
			dataSync(object_id,idx,type);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateAttr(Object[] data) {
		updateAttr(data,"attr");
	}
	
	public void updateAttr(Object[] data,String type) {
		
		String table = "def4_meth";
		String index_field = "method_idx",data_filed = "haspara",data_id ="method_id",data_name="method_name";
		if (type.equals("attr")){
			table = "def3_attr";
			index_field = "attr_idx";
			data_filed = "readonly";
			data_id ="attr_id";
			data_name="attr_name";
		}
		
		
		
		// xuky 2016.09.14 ����ԭ�ȵ�����������ʲô ���ﶼ���е���
//		String sql = "select encode_datatype from "+table+" where "+data_id+"="+(int)data[0];
//		int encode_datatype = 0;
//		ResultSet rs;
//		try {
//			rs = stat.executeQuery(sql);
//			while (rs.next()) {
//				encode_datatype = rs.getInt("encode_datatype");
//			}
//			rs.close();
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
		// ����id��Ϣ���޸�ԭ������
		PreparedStatement prep;
		try {
			if (type.equals("attr")){
				prep = conn.prepareStatement("update "+table
						+ " set sort_id=?, host_id=?, "+index_field+"=?, "
						+ " "+data_name+"=?, encode_datatype=?, decode_datatype=?, "+data_filed+"=?, "
						+ " seq=?, remark=?, status=?"
						+ " where "+data_id+"=?;");
				
				prep.setInt(1, (int)data[1]);
				prep.setInt(2, (int)data[2]);
				prep.setInt(3, (int)data[3]);
				prep.setString(4, (String)data[4]);
				prep.setInt(5, (int)data[5]);
				prep.setInt(6, (int)data[6]);
				prep.setInt(7, (int)data[7]);
				prep.setInt(8, (int)data[8]);
				prep.setString(9, (String)data[9]);
				prep.setInt(10, (int)data[10]);
				prep.setInt(11, (int)data[0]);
				prep.addBatch();
			}
			else{
				prep = conn.prepareStatement("update "+table
						+ " set sort_id=?, host_id=?, "+index_field+"=?, "
						+ " "+data_name+"=?, encode_datatype=?, "+data_filed+"=?, "
						+ " seq=?, remark=?, status=?"
						+ " where "+data_id+"=?;");
				
				prep.setInt(1, (int)data[1]);
				prep.setInt(2, (int)data[2]);
				prep.setInt(3, (int)data[3]);
				prep.setString(4, (String)data[4]);
				prep.setInt(5, (int)data[5]);
				//prep.setInt(6, (int)data[6]);
				prep.setInt(6, (int)data[7]);
				prep.setInt(7, (int)data[8]);
				prep.setString(8, (String)data[9]);
				prep.setInt(9, (int)data[10]);
				prep.setInt(10, (int)data[0]);
				prep.addBatch();
				
			}
			
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			//System.out.println(r);
			conn.setAutoCommit(true);
			
			// xuky 2016.09.14 ����ԭ�ȵ�����������ʲô ���ﶼ���е���
			//if  (encode_datatype!= (int)data[5])
			{
				// ���������б仯 ��Ҫ�����������
				int  object_id = (int)data[2],attr_idx = (int)data[3];
				dataSync(object_id,attr_idx,type);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void deleteAttr(int object_id,int attr_idx) {
		deleteAttr(object_id,attr_idx,"attr");
	}
	
	public void deleteAttr(int object_id,int attr_idx,String type) {
		String table = "def4_meth",field = "method_idx";
		if (type.equals("attr")){
			table = "def3_attr";
			field = "attr_idx";
		}
		String sql = "delete from "+table+" where host_id="+object_id+" and "+field+"="+attr_idx;
		try {
			stat.executeUpdate(sql);
			dataSync(object_id,attr_idx,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	// ��obj_attr��obj_attr_val��������ͬ��
	public void dataSync(int object_id, int idx, String type){
		String sql = "";
		String table = "obj_meth",table_value = "obj_meth_para",table_def="def4_meth";
		String index_field = "method_idx",data_filed = "haspara",data_id ="method_id",data_name="method_name";
		if (type.equals("def3_attr")){
			table = "obj_attr";
			table_value = "obj_attr_value";
			table_def = "def3_attr"; 
			index_field = "attr_idx";
			data_filed = "readonly";
			data_id ="attr_id";
			data_name="attr_name";
		}
		ResultSet rs;
		try {
			
			// 1��ɾ��ԭ�ȵĶ������� ��������ֵ
			//sql = "delete from "+table_value+" where "+data_id+" in (select "+data_id+" from "+table+" where object_id="+object_id+" and "+index_field+" = "+idx+")";
			sql = "delete from "+table_value+" where exists (select 1 from "+table+" where object_id="+object_id+" and "+index_field+" = "+idx+" and "+table_value+"."+data_id+"="+table+"."+data_id+")";
			stat.executeUpdate(sql);
			sql = "delete from "+table+" where object_id="+object_id+" and "+index_field+" = "+idx;
			stat.executeUpdate(sql);
			
			
			// 2�����Ӷ�������--������
			
			if (type.equals("def3_attr")){
				sql = 
						"insert into  "+ table +				
						" select NULL as "+data_id+",0 as obj_package_id ,class_id ,object_id ,object_name ,oi ,"+index_field+" ,"+data_name+" ,encode_datatype , "+
						"	decode_datatype ,"+data_filed+" ,status ,seq ,remark ,1 as selected  "+
						" from( "+
						" select a.class_id as class_id, "+
						"	1 as package_id,a.object_id as object_id,a.oi as oi,a.object_name as object_name, "+
						"	b."+index_field+" as "+index_field+",b."+data_name+" as "+data_name+",b.encode_datatype as encode_datatype,b.decode_datatype as decode_datatype,b."+data_filed+" as "+data_filed+",b.seq as seq,b.remark as remark,b.status as status "+
						"	from def2_obj a, "+table_def+" b "+
						"	where a.object_id="+object_id+" and b."+index_field+" = "+idx+" and a.object_id = b.host_id and b.sort_id = 1 "+ 
						" union  "+
						" select a.class_id as class_id, "+
						"	1 as package_id,a.object_id as object_id,a.oi as oi,a.object_name as object_name, "+
						"	b."+index_field+" as "+index_field+",b."+data_name+" as "+data_name+",b.encode_datatype as encode_datatype,b.decode_datatype as decode_datatype,b."+data_filed+" as "+data_filed+",b.seq as seq,b.remark as remark,b.status as status "+
						"	from def2_obj a, "+table_def+" b "+
						"	where a.object_id="+object_id+" and b."+index_field+" = "+idx+" and a.class_id = b.host_id  and b.sort_id = 0 "+ 
						"  )   ";
			}
			else{
				sql = 
						"insert into  "+ table +				
						" select NULL as "+data_id+",0 as obj_package_id ,class_id ,object_id ,object_name ,"+index_field+" ,"+data_name+", "+data_filed+" ,encode_datatype , "+
						" oi ,status ,seq ,remark ,1 as selected  "+
						" from( "+
						" select a.class_id as class_id, "+
						"	1 as package_id,a.object_id as object_id,a.oi as oi,a.object_name as object_name, "+
						"	b."+index_field+" as "+index_field+",b."+data_name+" as "+data_name+",b.encode_datatype as encode_datatype,b."+data_filed+" as "+data_filed+",b.seq as seq,b.remark as remark,b.status as status "+
						"	from def2_obj a, "+table_def+" b "+
						"	where a.object_id="+object_id+" and b."+index_field+" = "+idx+" and a.object_id = b.host_id and b.sort_id = 1 "+ 
						" union  "+
						" select a.class_id as class_id, "+
						"	1 as package_id,a.object_id as object_id,a.oi as oi,a.object_name as object_name, "+
						"	b."+index_field+" as "+index_field+",b."+data_name+" as "+data_name+",b.encode_datatype as encode_datatype,b."+data_filed+" as "+data_filed+",b.seq as seq,b.remark as remark,b.status as status "+
						"	from def2_obj a, "+table_def+" b "+
						"	where a.object_id="+object_id+" and b."+index_field+" = "+idx+" and a.class_id = b.host_id  and b.sort_id = 0 "+ 
						"  )   ";
				
			}
			stat.executeUpdate(sql);
			
			// 3�����obj_attr_value����
			sql = "select "+data_id+" from "+table+" where object_id="+object_id+" and "+index_field+" = "+idx;
			rs = stat.executeQuery(sql);
			
			while (rs.next()) {
				int attr_in_deal = rs.getInt(data_id);
				DB.getInstance().buildAttrVal(attr_in_deal,type);
				
			}
			
			rs.close();	
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateObj(Object[] data) {
		// ����id��Ϣ���޸�ԭ������
		int object_id = (int)data[0];
		int class_id = (int)data[3];
		PreparedStatement prep;
		ResultSet rs;
		try {

			//xuky 2016.09.14 ����ԭ�ȵĽӿ�����ʲô�����ﶼ����ȫ���޸�
			String sql = "";
//			// ��¼ԭ�ȵ�class_id
//			String sql="select class_id from def2_obj where object_id="+object_id;
//			int class_id0 = 0;
//			rs = stat.executeQuery(sql);
//			while (rs.next()) {
//				class_id0 = rs.getInt("class_id");
//			}
//			rs.close();
			
			prep = conn.prepareStatement("update def2_obj"
					+ " set oi=?, object_name=?, class_id=?, "
					+ " seq=?, status=? where object_id=?;");
			
			prep.setString(1, (String)data[1]);
			prep.setString(2, (String)data[2]);
			prep.setInt(3, class_id);
			prep.setInt(4, (int)data[4]);
			prep.setInt(5, (int)data[5]);
			prep.setInt(6, object_id);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

			
			// �����������Ϣ�仯������Ҫ�޸����������Ϣ������ֵ��Ϣ
			//xuky 2016.09.14 ����ԭ�ȵĽӿ�����ʲô�����ﶼ����ȫ���޸�
			//if (class_id0 != class_id)
			{
				
				// 1��ɾ��ԭ�ȵĶ������� ��������ֵ ���󷽷�  ���󷽷�����
				//sql = "delete from obj_attr_value where attr_id in (select attr_id from obj_attr where object_id="+object_id+")";
				sql = "delete from obj_attr_value where exists (select 1 from obj_attr where object_id="+object_id+" and obj_attr_value.attr_id = obj_attr.attr_id)";
				stat.executeUpdate(sql);
				sql = "delete from obj_attr where object_id="+object_id;
				stat.executeUpdate(sql);
				
				//sql = "delete from obj_meth_para where method_id in (select method_id from obj_meth_para where object_id="+object_id+")";
				sql = "delete from obj_meth_para where exists (select 1 from obj_meth where object_id="+object_id+" and obj_meth_para.method_id = obj_meth.method_id)";
				stat.executeUpdate(sql);
				sql = "delete from obj_meth where object_id="+object_id;
				stat.executeUpdate(sql);
				
				dataSyncByClass_id(object_id, class_id,"attr");
				dataSyncByClass_id(object_id, class_id,"method");
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void dataSyncByClass_id(int object_id, int class_id,String type){
		String table = "def3_attr", idx_column = "attr_idx";
		if (type.equals("method")){
			table = "def4_meth";
			idx_column = "method_idx";
		}
		String sql = "select "+idx_column+" from "+table+" where sort_id = 0 and host_id="+class_id;
		int i= 0;
		int[] idxs = new int[50];
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				idxs[i] = rs.getInt(idx_column);
				i++;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int j=0;j<i;j++)
			dataSync(object_id,(int)idxs[j],type);

	}
	
	public void updateIFClass(Object[] data) {
		// ����id��Ϣ���޸�ԭ������
		
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("update def1_ic"
					+ " set class_id=?, class_name=?, "
					+ " seq=?, status=? where ic_id=?;");
			
			prep.setInt(1, (int)data[1]);
			prep.setString(2, (String)data[2]);
			prep.setInt(3, (int)data[3]);
			prep.setInt(4, (int)data[4]);
			prep.setInt(6, (int)data[0]);
			prep.addBatch();
					
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteAllData(int[] ids, String tablename,String colName) {
//		System.out.println("deleteAllData=>"+DateTimeFun.getDateTimeSSS() +" begin");
		// �����ɾ����id�б�Ϊ�գ����˳�
		if (ids== null || ids.length==0 )
			return;
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("delete from " +tablename +" where " + colName + "= ?");
			for (int id : ids) {
				prep.setInt(1, id);
				prep.addBatch();
			}
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("deleteAllData=>"+DateTimeFun.getDateTimeSSS() +" DBOPT end");
	}
	
	public void deleteData(int id, String tablename,String colName,Object obj) {
		
		String aDatime1 = DateTimeFun.getDateTimeSSS();
//		System.out.println("deleteData=>"+ aDatime1 +" begin");
		String sql = "delete from "+tablename+" where "+colName+"="+id;
		try {
			stat.execute("BEGIN TRANSACTION");
			int row = stat.executeUpdate(sql);
			stat.execute("COMMIT");
			row = row + 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (tablename.equals("def4_meth") || tablename.equals("def3_attr") ){
		
			List<Map> l = Util698.getFiledsInfo(obj);
			int colNum = l.size();
			
			// ��������ת����������
			Object[] data = new Object[colNum];
	
			int i = 1;
			for( Map<String, Object> info:l ){
				String name = info.get("name").toString();
				Object val = Util698.getFieldValueByName(name,obj);
				// ��������ת����������
				data[i-1] = val;
				i++;
			}
			int object_id = (int)data[2], idx = (int)data[3];  
			dataSync(object_id,idx,tablename);
		}
	}
	
	public void deleteDataTypeUser(int id) {
		String sql = "delete from datatype_user where id="+id+"; ";
		try {
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void deleteObj(int id) {
		String sql = "delete from def2_obj where object_id="+id+"; ";
		// ����Ҫɾ������ӱ�����
		try {
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void deleteIFClass(int id) {
		String sql = "delete from def1_ic where ic_id="+id+"; ";
		try {
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean isRecord(int attr_id) {
		String sql = "select count(*) as rownum from obj_attr_value where attr_id="+attr_id+" and dataname = 'RSDAndRCSD'; ";
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();
			
			if (count >= 1 ) 
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Object[][] getObjectList() {
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String sql = "select  count(*) as rownum from def2_obj ;";
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();

			data1 = new Object[count][20];

			sql = "select object_id,oi,object_name,class_id,seq,status from def2_obj order by oi; ";
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				data1[i][0] = rs.getInt("object_id");
				data1[i][1] = rs.getString("oi");
				data1[i][2] = rs.getString("object_name");
				data1[i][3] = rs.getInt("class_id");
				data1[i][4] = rs.getInt("seq");
				data1[i][5] = rs.getInt("status");
				i++;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data1;
	}
	
	public Object[][] getIFClassList() {
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String sql = "select  count(*) as rownum from def1_ic ;";	
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();

			data1 = new Object[count][20];

			sql = "select * from def1_ic order by class_id; ";
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				data1[i][0] = rs.getInt("ic_id");
				data1[i][1] = rs.getInt("class_id");
				data1[i][2] = rs.getString("class_name");
				data1[i][3] = rs.getInt("seq");
				data1[i][4] = rs.getInt("status");
				i++;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data1;
	}

	public Object[][] getObjectAttrListTest(int host_id, Boolean isObject) {
		String type = "attr";
		String table = "def4_meth",field="method_idx";
		if (type.equals("attr")){
			table = "def3_attr";
			field="attr_idx";
		} 
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String sql = "";
			if (isObject)
				sql = "select  count(*) as rownum from "+table+" where host_id="+host_id+" and sort_id = 1";
			else
				sql = "select  count(*) as rownum from "+table+" where host_id="+host_id+" and sort_id = 0";
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();
			
			
			data1 = new Object[count][20];
			
			if (isObject)
				sql = "select * from "+table+" where host_id="+host_id+" and sort_id = 1 order by "+field;
			else
				sql = "select * from "+table+" where host_id="+host_id+" and sort_id = 0 order by "+field;
			
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				// ����˴������˴��򣬻���Ҫ����AttrList(int host_id)���캯��
				
				// ����Ĵ��� ����ʵʱ�Ŀؼ������й�   
				// 1���������Դ���
				// 2�����ݿ��ֶδ���
				// 3��������ʾ����  �б���ʾ����ϸ��Ϣ��ʾ
				data1[i][0] = rs.getInt("attr_id");
				data1[i][1] = rs.getInt("sort_id");
				data1[i][2] = rs.getInt("host_id");
				data1[i][3] = rs.getInt("attr_idx");
				data1[i][4] = rs.getString("attr_name");
				data1[i][5] = rs.getInt("encode_datatype");
				data1[i][6] = rs.getInt("decode_datatype");		
				data1[i][7] = rs.getInt("readonly");
				data1[i][8] = rs.getInt("seq");
				data1[i][9] = rs.getString("remark");
				data1[i][10] = rs.getInt("status");
				i++;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data1;
	}
	
	public Object[][] getDataList( String table, String where, String order , Object obj) {
		// ��ȡ�����ֶ����� 
		return getDataList(table, where, order , obj, "");
	}
	
	public Object[][] getDataList( String table, String where, String order , Object obj,String columns) {
		Object[][] data1 = null;
		ResultSet rs;
		try {
			
			String sql = "select  count(*) as rownum from "+ table ;
			if (!where.equals(""))
				sql += " where " + where;
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();

			// ��ȡ���������б�
			List<Map> attr_list = Util698.getFiledsInfo(obj);
			
			data1 = new Object[count][attr_list.size()];
			
			if (columns.equals(""))
				columns = "*";
			
			sql = "select "+columns+" from "+table;
			if (!where.equals(""))
				sql += " where " + where;
			if (!order.equals(""))
				sql += " "+order;
			
			rs = stat.executeQuery(sql);
			int i = 0, j = 0;
			String name = "",type = "";
			while (rs.next()) {
				j = 0;
				if (columns.equals("*"))
					for( Map<String, Object> info:attr_list ){
						name = info.get("name").toString();
						type = info.get("type").toString();
						if (type.toLowerCase().indexOf("int") >= 0)
							data1[i][j] = rs.getInt(name);
						if (type.indexOf("String") >= 0)
							data1[i][j] = rs.getString(name);
						j++;
					}
				else{
					columns += ",";
					String[] array = columns.split(",");
					for(String str: array){
						Map<String, Object> info = Util698.getFiledsInfoByName(obj,str);
						//System.out.println("getDataList=>"+str);
						type = info.get("type").toString();
						if (type.toLowerCase().indexOf("int") >= 0)
							data1[i][j] = rs.getInt(str);
						if (type.indexOf("String") >= 0)
							data1[i][j] = rs.getString(str);
						j++;
					}
				}
				i++;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data1;
	}
	
	public Object[][] getObjectAttrList(int host_id, Boolean isObject) {
		return getObjectAttrList(host_id, isObject, "attr");
	}
	
	public Object[][] getObjectAttrList(int host_id, Boolean isObject, String type) {
		String table = "def4_meth",field="method_idx";
		if (type.equals("attr")){
			table = "def3_attr";
			field="attr_idx";
		} 
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String sql = "";
			if (isObject)
				sql = "select  count(*) as rownum from "+table+" where host_id="+host_id+" and sort_id = 1";
			else
				sql = "select  count(*) as rownum from "+table+" where host_id="+host_id+" and sort_id = 0";
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();
			
			
			data1 = new Object[count][20];
			
			if (isObject)
				sql = "select * from "+table+" where host_id="+host_id+" and sort_id = 1 order by "+field;
			else
				sql = "select * from "+table+" where host_id="+host_id+" and sort_id = 0 order by "+field;
			
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				// ����˴������˴��򣬻���Ҫ����AttrList(int host_id)���캯��
				if (type.equals("attr")){
					data1[i][0] = rs.getInt("attr_id");
					data1[i][1] = rs.getInt("attr_idx");
					data1[i][2] = rs.getString("attr_name");
					data1[i][3] = rs.getInt("readonly");
					
					data1[i][7] = rs.getInt("decode_datatype");		
				}
				else{
					data1[i][0] = rs.getInt("method_id");
					data1[i][1] = rs.getInt("method_idx");
					data1[i][2] = rs.getString("method_name");
					data1[i][3] = rs.getInt("haspara");
					data1[i][7] = 0;
				}
				
				data1[i][4] = rs.getInt("encode_datatype");
				
				data1[i][5] = rs.getInt("host_id");
				data1[i][6] = rs.getInt("sort_id");

				data1[i][8] = rs.getInt("seq");
				data1[i][9] = rs.getString("remark");
				data1[i][10] = rs.getInt("status");
				i++;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data1;
	}
	
	
	public Object[][] getObjectMethodList(int host_id, Boolean isObject) {
		Object[][] data1 = null;
		ResultSet rs;
		try {
			String sql = "";
			if (isObject)
				sql = "select  count(*) as rownum from def4_meth where host_id="+host_id+" and sort_id = 1";
			else
				sql = "select  count(*) as rownum from def4_meth where host_id="+host_id+" and sort_id = 0";
			rs = stat.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("rownum");
				break;
			}
			rs.close();
			

			data1 = new Object[count][20];

			if (isObject)
				sql = "select * from def4_meth where host_id="+host_id+" and sort_id = 1 order by method_idx;";
			else
				sql = "select * from def4_meth where host_id="+host_id+" and sort_id = 0 order by method_idx";
			
			rs = stat.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				
				data1[i][0] = rs.getInt("method_id");
				data1[i][1] = rs.getInt("method_idx");
				data1[i][2] = rs.getString("method_name");
				data1[i][3] = rs.getInt("haspara");
				data1[i][4] = rs.getInt("encode_datatype");
				data1[i][5] = rs.getInt("host_id");
				data1[i][6] = rs.getInt("sort_id");
				data1[i][7] = rs.getInt("seq");
				data1[i][8] = rs.getString("remark");
				data1[i][9] = rs.getInt("status");
				i++;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data1;
	}


	// ���д���ת����תΪ�����ַ�
	public Object[][] getDataname(Object[][] data_attr, int n) {
		for(int i=0;i<data_attr.length;i++)
			data_attr[i][n] = getDataname((int)data_attr[i][n]);
		return data_attr;
	}

	public String getDataname(int type_code){
		String ret = "";
		String sql ="select dataname from datatype_user where code=" + type_code;
		ResultSet rs;
		try {
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				ret = rs.getString("dataname");
			}
			rs.close();
			
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return ret;
	}
	public void buildAttrVal(int attr_id){
		buildAttrVal(attr_id,"attr");
	}

	// ����
	public void buildAttrVal(int attr_id,String type){
		String table = "obj_meth",table_val="obj_meth_para",field = "method_id"; 
		if (type.equals("attr")){
			table = "obj_attr";
			table_val = "obj_attr_value";
			field= "attr_id";
		}
		String sql ="select package_id,encode_datatype,b.id,b.datatype from "+table+" a,datatype_user b where "+field+"=" + attr_id+" and a.encode_datatype=b.code";
		ResultSet rs;
		
		int package_id=0,code=-1,max_id=0,rootData_id=0;
		String datatype = "";
		try {
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				package_id =rs.getInt("package_id");
				code =rs.getInt("encode_datatype");
				rootData_id =rs.getInt("id");
				datatype = rs.getString("datatype");
			}
			rs.close();
		
			// xuky 2016.09.04 code=-1,��ʾ�������� SQL�޲�ѯ���
			if (code == -1) 
				return;
			
			sql = "select max(id) as max_id from " + table_val + "; ";
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				max_id = rs.getInt("max_id");
			}
			rs.close();
			
			String adds = ""; 
			if (datatype.equals("array")||datatype.indexOf("CHOICE")>=0||
					datatype.equals("SEQUENCE OF")){
				// ��������ӽڵ�
			}
			else
				adds = DB.getInstance().getTreeData(code,-1);
			adds = rootData_id+";"+adds;
			String[] addsArray = adds.split(";");
			int i = 0;
			PreparedStatement prep = conn.prepareStatement("insert into "
					+ table_val + " values (?, ?, ?,?, ?, ?,?, ?, ?,?, ?);");
			Boolean firstLine = true;
			for (String s : addsArray) {
				if (s==null || s.equals("")) 
					continue;
				sql = "select code,fcode,seq,dataname,datatype,defaultvalue,definevalue from datatype_user where id ="
						+ s + ";";
				rs = stat.executeQuery(sql);
				while (rs.next()) {
					i++;
					prep.setInt(1, max_id + i);
					prep.setInt(2, package_id);
					prep.setInt(3, attr_id);
					prep.setInt(4, rs.getInt("code"));
					// xuky 2016.08.31 �����û�ѡ������ڵ���Ϊ��ʼ�ڵ㣬���ٴ�������fcodeΪ0����ʾΪroot
					if (firstLine) 
						prep.setInt(5, 0);
					else
						prep.setInt(5, rs.getInt("fcode"));
					prep.setInt(6, rs.getInt("seq"));
					prep.setString(7, rs.getString("dataname"));
					prep.setString(8, rs.getString("datatype"));
					prep.setString(9, rs.getString("defaultvalue"));
					prep.setString(10, rs.getString("definevalue"));
					// xuky 2016.09.13 Ĭ�ϵĸ��ڵ�array_idx=001
					//prep.setString(11, null);
					prep.setString(11, "001");
					prep.addBatch();
					firstLine = false;
				}
				rs.close();
				
			}
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
				e.printStackTrace();
		}
	}
	
	// �õ�OAD����Ӧ���������ƻ��Ƿ�������
	public String getOADData(String OAD, Boolean isOAD) {
		String ret = "", sql = "", value = "";
		
		if (isOAD){
			// �����Ա��л�ȡ����
			sql = "select attr_name from def3_attr where "+ 
			" host_id = (select object_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
			" and sort_id=1 "+
			" and attr_idx = "+DataConvert.hexString2Int(OAD.substring(4,6)); 
			// �����Ҳ����������ƵĶ�Ӧ  ������ҽӿ���������������
			value = (String)DB.getInstance().getOneData(sql,"attr_name","String");
			if (value == null || value.equals("null")){
				sql = "select attr_name from def3_attr where "+ 
						" host_id = (select class_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
						" and sort_id=0 "+
						" and attr_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));			
				value = (String)DB.getInstance().getOneData(sql,"attr_name","String");
			}
		}
		else{
			sql = "select method_name from def4_meth where "+ 
					" host_id = (select object_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
					" and sort_id=1 "+
					" and method_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));			
			value = (String)DB.getInstance().getOneData(sql,"method_name","String");
			if (value == null || value.equals("null")){
				sql = "select method_name from def4_meth where "+ 
						" host_id = (select class_id from def2_obj where oi = '"+ OAD.substring(0,4) +"') "+
						" and sort_id=0 "+
						" and method_idx = "+DataConvert.hexString2Int(OAD.substring(4,6));			
				value = (String)DB.getInstance().getOneData(sql,"method_name","String");
			}
		}
		ret = value;
		return ret;
	}
	
	
	public Object getOneData(String sql,String colName,String colType) {
		Object retdata = null;
		ResultSet rs;
		try {
			// ���Ƚ����л�ȡ�����ݱ��浽�����У�
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				if (colType.equals("int"))
					retdata = rs.getInt(colName);
				if (colType.equals("String"))
					retdata = rs.getString(colName);
			}
			rs.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retdata;
	}
	
	public int getTreeDataDeep(int code,int[] id,int pos){
		String sql = "select id,code from datatype_user where fcode=" + code +" order by seq";;
		ResultSet rs;
		int[] id_temp = new int[100];
		int[] code_temp = new int[100];
		int j = 0;
		try {
			// ���Ƚ����л�ȡ�����ݱ��浽�����У�
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				id_temp[j] = rs.getInt("id");
				code_temp[j] = rs.getInt("code");
				j++;
			}
			rs.close();
			
			// Ȼ���ٽ�������ĵݹ���������ֱ����rs.next()ѭ���н��У��ᵼ��rs�������ݶ�ʧ
			for( int k=0;k<j;k++ ){
				id[pos] = id_temp[k];
				pos++;
				pos = getTreeDataDeep(code_temp[k],id,pos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	
	public static void main(String[] args) {
		DB.getInstance().buildAttrVal(4731);
//		DB.getInstance().buildAttrVal(4729);

	}


}
