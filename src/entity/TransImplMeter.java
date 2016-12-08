package entity;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import com.eastsoft.util.DataConvert;

import base.TransBehavior;

/**
 * �������ת������
 * @author xuky
 * @version 2016.10.13
 * 
 */
public class TransImplMeter extends TransBehavior{

	//String[] ObjColumns = { "oi","��������","�ӿ���","˳���","״̬" };

	@Override
	public void setData(Object object) {
		
		if (object == null){
			clearComponent();
			return;
		}
		// ������Ķ���ת��Ϊ��������ʾ������
		Meter obj = (Meter)object;
		
		IDs[0] = DataConvert.int2String(obj.getID());
		
		((JTextField) component[0]).setText(obj.getMeasureNo());
		((JTextField) component[1]).setText(obj.getMeterCode());
		((JComboBox) component[2]).setSelectedItem(obj.getPortRate());
		((JComboBox) component[3]).setSelectedItem(obj.getProtocolType());
		((JComboBox) component[4]).setSelectedItem(obj.getPort());
		((JTextField) component[5]).setText(obj.getPwd());
		((JTextField) component[6]).setText(obj.getFee());  
		((JTextField) component[7]).setText(obj.getType1()); 
		
		((JComboBox) component[8]).setSelectedItem(obj.getType2());
		
		((JTextField) component[9]).setText(obj.getRatedVoltage());
		((JTextField) component[10]).setText(obj.getRatedCurrent());
		((JTextField) component[11]).setText(obj.getCollectCode());
		((JTextField) component[12]).setText(obj.getAssetsNo());
		((JTextField) component[13]).setText(obj.getPT()); 
		((JTextField) component[14]).setText(obj.getCT());
	}

	@Override
	public Meter getData() {
		// ����������ʾ������ת��Ϊ���� ��Ҫ�����������͵�ת��
		Meter obj = new Meter();
		obj.setMeasureNo(((JTextField)component[0]).getText());
		obj.setMeterCode(((JTextField)component[1]).getText());
		obj.setPortRate(((JComboBox) component[2]).getSelectedItem().toString());
		obj.setProtocolType(((JComboBox) component[3]).getSelectedItem().toString());
		obj.setPort(((JComboBox) component[4]).getSelectedItem().toString());
		
		obj.setPwd(((JTextField)component[5]).getText());
		obj.setFee(((JTextField)component[6]).getText());
		obj.setType1(((JTextField)component[7]).getText());
		obj.setType2(((JComboBox) component[8]).getSelectedItem().toString());
		
		obj.setRatedVoltage(((JTextField)component[9]).getText());
		obj.setRatedCurrent(((JTextField)component[10]).getText());
		obj.setCollectCode(((JTextField)component[11]).getText());
		
		obj.setAssetsNo(((JTextField)component[12]).getText());
		obj.setPT(((JTextField)component[13]).getText());
		obj.setCT(((JTextField)component[14]).getText());
		
		return obj;
	}
	
	@Override
	public Meter getDataWithID() {
		Meter d = getData();
		d.setID(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
