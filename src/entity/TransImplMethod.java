package entity;

import javax.swing.JTextField;
import util.DB;
import com.eastsoft.util.DataConvert;

import base.TransBehavior;

/**
 * 
 * @author xuky
 * @version 2016.08.30
 * 
 */
public class TransImplMethod extends TransBehavior{
	
	//String[] AttrColumns = { "编号","属性名称","只读","数据类型" };

	@Override
	public void setData(Object object) {
		// 将传入的对象转变为界面中显示的数据
		Method obj = (Method)object;
		IDs[0] = DataConvert.int2String(obj.getMethod_id());
		// 显示在界面的数据
		((JTextField) component[0]).setText(DataConvert.int2String(obj.getMethod_idx()));
		((JTextField) component[1]).setText(obj.getMethod_name());
		((JTextField) component[2]).setText(DataConvert.int2String(obj.getHaspara()));
		
		// 特殊字段，text为显示值，tooltiptext为存储值
		String dataname = DB.getInstance().getDataname(obj.getEncode_datatype());
		((JTextField) component[3]).setText(dataname);
		((JTextField) component[3]).setToolTipText(DataConvert.int2String(obj.getEncode_datatype()));
		
		// 不显示在界面的数据，由标题信息AttrColumns的个数控制
		((JTextField) component[4]).setText(DataConvert.int2String(obj.getSort_id()));
		((JTextField) component[5]).setText(DataConvert.int2String(obj.getHost_id()));
		((JTextField) component[6]).setText(DataConvert.int2String(0));
		((JTextField) component[7]).setText(DataConvert.int2String(obj.getSeq()));
		((JTextField) component[8]).setText(DataConvert.int2String(obj.getStatus()));
		((JTextField) component[9]).setText(obj.getRemark());

		
	}

	@Override
	public Method getData() {
		// 将界面中显示的数据转变为对象 需要进行数据类型的转换
		Method d = new Method();
		d.setMethod_idx(DataConvert.String2Int(((JTextField) component[0]).getText()));
		d.setMethod_name(((JTextField) component[1]).getText());
		d.setHaspara(DataConvert.String2Int(((JTextField) component[2]).getText()));

		// 特殊字段，ToolTipText为存储值
		d.setEncode_datatype(DataConvert.String2Int(((JTextField) component[3]).getToolTipText()));
		
		d.setSort_id(DataConvert.String2Int(((JTextField) component[4]).getText()));
		d.setHost_id(DataConvert.String2Int(((JTextField) component[5]).getText()));
		//d.setDecode_datatype(DataConvert.String2Int(((JTextField) component[6]).getText()));
		d.setSeq(DataConvert.String2Int(((JTextField) component[0]).getText()));
		d.setStatus(DataConvert.String2Int(((JTextField) component[8]).getText()));
		d.setRemark(((JTextField) component[9]).getText());
		
		
		return d;
	}
	
	@Override
	public Method getDataWithID() {
		Method d = getData();
		d.setMethod_id(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
