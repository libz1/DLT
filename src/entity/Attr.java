package entity;

/**
 * ÊôĞÔ
 * @author xuky
 * @version 2016.08.29
 */
public class Attr {
	Integer attr_id;
	Integer sort_id;
	Integer host_id;
	Integer attr_idx;
	String attr_name;
	Integer encode_datatype;
	Integer decode_datatype;  
	Integer readonly;
	Integer seq;
	String remark;
	Integer status;
	public Integer getAttr_id() {
		return attr_id;
	}
	public void setAttr_id(Integer attr_id) {
		this.attr_id = attr_id;
	}
	public Integer getSort_id() {
		return sort_id;
	}
	public void setSort_id(Integer sort_id) {
		this.sort_id = sort_id;
	}
	public Integer getHost_id() {
		return host_id;
	}
	public void setHost_id(Integer host_id) {
		this.host_id = host_id;
	}
	public Integer getAttr_idx() {
		return attr_idx;
	}
	public void setAttr_idx(Integer attr_idx) {
		this.attr_idx = attr_idx;
	}
	public String getAttr_name() {
		return attr_name;
	}
	public void setAttr_name(String attr_name) {
		this.attr_name = attr_name;
	}
	public Integer getEncode_datatype() {
		return encode_datatype;
	}
	public void setEncode_datatype(Integer encode_datatype) {
		this.encode_datatype = encode_datatype;
	}
	public Integer getDecode_datatype() {
		return decode_datatype;
	}
	public void setDecode_datatype(Integer decode_datatype) {
		this.decode_datatype = decode_datatype;
	}
	public Integer getReadonly() {
		return readonly;
	}
	public void setReadonly(Integer readonly) {
		this.readonly = readonly;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
