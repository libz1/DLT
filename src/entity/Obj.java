package entity;

/**
 * @author xuky
 * @version 2016.08.29
 */
public class Obj implements Comparable<Obj> {

	int object_id; // 主键信息
	String oi;  // 重要字段
	String object_name; 
	int class_id;
	int seq;  
	int status;   

	public Obj(){
	}
	
	public int getObject_id() {
		return object_id;
	}

	public void setObject_id(int object_id) {
		this.object_id = object_id;
	}

	public String getOi() {
		return oi;
	}

	public void setOi(String oi) {
		this.oi = oi;
	}

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public int getClass_id() {
		return class_id;
	}

	public void setClass_id(int class_id) {
		this.class_id = class_id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int compareTo(Obj o) {
		Integer i, j;
		i = this.getObject_id();
		j = o.getObject_id();
		return i.compareTo(j);
	}

}
