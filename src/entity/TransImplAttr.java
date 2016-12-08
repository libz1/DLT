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
public class TransImplAttr extends TransBehavior{
	
	//String[] AttrColumns = { "���","��������","ֻ��","��������" };

	@Override
	public void setData(Object object) {
		// ������Ķ���ת��Ϊ��������ʾ������
		Attr obj = (Attr)object;
		IDs[0] = DataConvert.int2String(obj.getAttr_id());
		
		// ��ʾ�ڽ��������
		((JTextField) component[0]).setText(DataConvert.int2String(obj.getAttr_idx()));
		((JTextField) component[1]).setText(obj.getAttr_name());
		((JTextField) component[2]).setText(DataConvert.int2String(obj.getReadonly()));
		
		// �����ֶΣ�textΪ��ʾֵ��tooltiptextΪ�洢ֵ
		String dataname = DB.getInstance().getDataname(obj.getEncode_datatype());
		((JTextField) component[3]).setText(dataname);
		
		((JTextField) component[3]).setToolTipText(DataConvert.int2String(obj.getEncode_datatype()));
		
		// ����ʾ�ڽ�������ݣ��ɱ�����ϢAttrColumns�ĸ�������
		((JTextField) component[4]).setText(DataConvert.int2String(obj.getSort_id()));
		((JTextField) component[5]).setText(DataConvert.int2String(obj.getHost_id()));
		((JTextField) component[6]).setText(DataConvert.int2String(obj.getDecode_datatype()));
		((JTextField) component[7]).setText(DataConvert.int2String(obj.getSeq()));
		((JTextField) component[8]).setText(DataConvert.int2String(obj.getStatus()));
		((JTextField) component[9]).setText(obj.getRemark());

		
	}

	@Override
	public Attr getData() {
		// ����������ʾ������ת��Ϊ���� ��Ҫ�����������͵�ת��
		Attr d = new Attr();
		
		String tmp = ((JTextField) component[0]).getText();
		tmp = ((JTextField) component[1]).getText();
		tmp = ((JTextField) component[2]).getText();
		tmp = ((JTextField) component[3]).getText();
		tmp = ((JTextField) component[4]).getText();
		
		d.setAttr_idx(DataConvert.String2Int(((JTextField) component[0]).getText()));
		d.setAttr_name(((JTextField) component[1]).getText());
		d.setReadonly(DataConvert.String2Int(((JTextField) component[2]).getText()));

		// �����ֶΣ�ToolTipTextΪ�洢ֵ
		String datatype = ((JTextField) component[3]).getToolTipText();
		if (datatype==null || datatype.equals(""))
			d.setEncode_datatype(0);
		else
			d.setEncode_datatype(DataConvert.String2Int(datatype));
		
		tmp = ((JTextField) component[4]).getText();
		d.setSort_id(DataConvert.String2Int(((JTextField) component[4]).getText()));
		d.setHost_id(DataConvert.String2Int(((JTextField) component[5]).getText()));
		d.setDecode_datatype(DataConvert.String2Int(((JTextField) component[6]).getText()));
		d.setSeq(DataConvert.String2Int(((JTextField) component[0]).getText()));
		d.setStatus(DataConvert.String2Int(((JTextField) component[8]).getText()));
		d.setRemark(((JTextField) component[9]).getText());
		
		
		return d;
	}
	
	@Override
	public Attr getDataWithID() {
		Attr d = getData();
		d.setAttr_id(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
