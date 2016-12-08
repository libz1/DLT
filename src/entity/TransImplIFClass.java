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
public class TransImplIFClass extends TransBehavior{

	//String[] ObjColumns = { "class_id","������","˳���","״̬" };

	@Override
	public void setData(Object object) {
		// ������Ķ���ת��Ϊ��������ʾ������
		IFClass obj = (IFClass)object;
		IDs[0] = DataConvert.int2String(obj.getIc_id());
		((JTextField) component[0]).setText(DataConvert.int2String(obj.getClass_id()));
		((JTextField) component[1]).setText(obj.getClass_name());
		((JTextField) component[2]).setText(DataConvert.int2String(obj.getSeq()));
		((JTextField) component[3]).setText(DataConvert.int2String(obj.getStatus()));
	}

	@Override
	public IFClass getData() {
		// ����������ʾ������ת��Ϊ���� ��Ҫ�����������͵�ת��
		IFClass d = new IFClass();
		d.setClass_id(DataConvert.String2Int(((JTextField) component[0]).getText()));
		d.setClass_name(((JTextField) component[1]).getText());
		d.setSeq(DataConvert.String2Int(((JTextField) component[2]).getText()));
		d.setStatus(DataConvert.String2Int(((JTextField) component[3]).getText()));
		return d;
	}
	
	@Override
	public IFClass getDataWithID() {
		IFClass d = getData();
		d.setIc_id(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
