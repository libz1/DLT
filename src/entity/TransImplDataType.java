package entity;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.eastsoft.util.DataConvert;

import base.TransBehavior;

/**
 * �������͹������
 * @author xuky
 * @version 2016.10.13
 * 
 */
public class TransImplDataType extends TransBehavior{

	//String[] ObjColumns = { "oi","��������","�ӿ���","˳���","״̬" };

	@Override
	public void setData(Object object) {
		// ������Ķ���ת��Ϊ��������ʾ������
		DataTypeUser obj = (DataTypeUser)object;
		
		IDs[0] = DataConvert.int2String(obj.getId());
		
		((JTextField) component[0]).setText(DataConvert.int2String(obj.getCode()));
		((JTextField) component[1]).setText(DataConvert.int2String(obj.getFcode()));
		((JTextField) component[2]).setText(DataConvert.int2String(obj.getSeq()));
		((JTextField) component[3]).setText(obj.getDataname());
		((JTextField) component[4]).setText(obj.getDatatype());
		((JTextField) component[5]).setText(obj.getDefaultvalue());
		((JTextArea) component[6]).setText(obj.getDefinevalue());
		((JTextField) component[7]).setText(obj.getRemark());
	}

	@Override
	public DataTypeUser getData() {
		// ����������ʾ������ת��Ϊ���� ��Ҫ�����������͵�ת��
		DataTypeUser d = new DataTypeUser();
		d.setCode(DataConvert.String2Int(((JTextField) component[0]).getText()));
		d.setFcode(DataConvert.String2Int(((JTextField) component[1]).getText()));
		d.setSeq(DataConvert.String2Int(((JTextField) component[2]).getText()));
		d.setDataname(((JTextField) component[3]).getText());
		d.setDatatype(((JTextField) component[4]).getText());
		d.setDefaultvalue(((JTextField) component[5]).getText());
		d.setDefinevalue(((JTextArea) component[6]).getText());
		d.setRemark(((JTextField) component[7]).getText());
		return d;
	}
	
	@Override
	public DataTypeUser getDataWithID() {
		DataTypeUser d = getData();
		d.setId(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
