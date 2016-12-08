package entity;

/**
 * @author xuky
 * @version 2016.08.29
 */
public class IFClass implements Comparable<IFClass> {
	
	int ic_id;
	int class_id;
	String class_name; 
	int seq;  
	int status;	
	
	public int getIc_id() {
		return ic_id;
	}


	public void setIc_id(int ic_id) {
		this.ic_id = ic_id;
	}


	public int getClass_id() {
		return class_id;
	}


	public void setClass_id(int class_id) {
		this.class_id = class_id;
	}


	public String getClass_name() {
		return class_name;
	}


	public void setClass_name(String class_name) {
		this.class_name = class_name;
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
	public int compareTo(IFClass o) {
		Integer i, j;
		i = this.getClass_id();
		j = o.getClass_id();
		return i.compareTo(j);
	}

}
