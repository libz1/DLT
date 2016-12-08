package entity;


/**
 * 用户数据类型实体类
 * @author xuky
 * @version 2016.10.24
 */
public class DataTypeUser implements Comparable<DataTypeUser> {

	int id;  // 主键信息
	int code;  // 重要字段
	int fcode;   
	int seq;  
	String dataname;  
	String datatype; 
	String defaultvalue; 
	String definevalue;
	String remark; 

	public DataTypeUser(){
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getFcode() {
		return fcode;
	}

	public void setFcode(int fcode) {
		this.fcode = fcode;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getDataname() {
		return dataname;
	}

	public void setDataname(String dataname) {
		this.dataname = dataname;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public String getDefinevalue() {
		return definevalue;
	}

	public void setDefinevalue(String definevalue) {
		this.definevalue = definevalue;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public int compareTo(DataTypeUser o) {
		// 鍙傝�http://www.cnblogs.com/wentiertong/archive/2011/03/07/1973698.html
		Integer i, j;
		i = this.getCode();
		j = o.getCode();
		return i.compareTo(j);
	}
	




}
