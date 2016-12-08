package entity;

/**
 * @author xuky
 * @version 2016.08.31
 */
public class Method {
	int method_id;
	int sort_id;
	int host_id;
	int method_idx;
	String method_name;
	int encode_datatype;
	int haspara;
	int seq;
	String remark;
	int status;
	public int getMethod_id() {
		return method_id;
	}
	public void setMethod_id(int method_id) {
		this.method_id = method_id;
	}
	public int getSort_id() {
		return sort_id;
	}
	public void setSort_id(int sort_id) {
		this.sort_id = sort_id;
	}
	public int getHost_id() {
		return host_id;
	}
	public void setHost_id(int host_id) {
		this.host_id = host_id;
	}
	public int getMethod_idx() {
		return method_idx;
	}
	public void setMethod_idx(int method_idx) {
		this.method_idx = method_idx;
	}
	public String getMethod_name() {
		return method_name;
	}
	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}
	public int getEncode_datatype() {
		return encode_datatype;
	}
	public void setEncode_datatype(int encode_datatype) {
		this.encode_datatype = encode_datatype;
	}
	public int getHaspara() {
		return haspara;
	}
	public void setHaspara(int haspara) {
		this.haspara = haspara;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	

}
