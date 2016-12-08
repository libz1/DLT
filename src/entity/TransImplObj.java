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

	//String[] ObjColumns = { "oi","��������","�ӿ���","˳���","״̬" };

	@Override
	public void setData(Object object) {
		// ������Ķ���ת��Ϊ��������ʾ������
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
		// ����������ʾ������ת��Ϊ���� ��Ҫ�����������͵�ת��
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
