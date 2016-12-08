package entity;

import javax.swing.JTextField;
import com.eastsoft.util.DataConvert;

import base.TransBehavior;

/**
 * 
 * @author xuky
 * @version 2016.08.30
 * 
 */
public class TransImplObj extends TransBehavior{

	//String[] ObjColumns = { "oi","对象名称","接口类","顺序号","状态" };

	@Override
	public void setData(Object object) {
		// 将传入的对象转变为界面中显示的数据
		Obj obj = (Obj)object;
		IDs[0] = DataConvert.int2String(obj.getObject_id());
		
		((JTextField) component[0]).setText(obj.getOi());
		((JTextField) component[1]).setText(obj.getObject_name());
		((JTextField) component[2]).setText(DataConvert.int2String(obj.getClass_id()));
		((JTextField) component[3]).setText(DataConvert.int2String(obj.getSeq()));
		((JTextField) component[4]).setText(DataConvert.int2String(obj.getStatus()));
	}

	@Override
	public Obj getData() {
		// 将界面中显示的数据转变为对象 需要进行数据类型的转换
		Obj d = new Obj();
		d.setOi(((JTextField) component[0]).getText());
		d.setObject_name(((JTextField) component[1]).getText());
		d.setClass_id(DataConvert.String2Int(((JTextField) component[2]).getText()));
		d.setSeq(DataConvert.String2Int(((JTextField) component[3]).getText()));
		d.setStatus(DataConvert.String2Int(((JTextField) component[4]).getText()));
		return d;
	}
	
	@Override
	public Obj getDataWithID() {
		Obj d = getData();
		d.setObject_id(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
