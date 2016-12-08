package entity;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import com.eastsoft.util.DataConvert;

import base.TransBehavior;

/**
 * 电表数据转换对象
 * @author xuky
 * @version 2016.10.13
 * 
 */
public class TransImplTaskSet extends TransBehavior{

	@Override
	public void setData(Object object) {
		
		if (object == null){
			clearComponent();
			return;
		}
		// 将传入的对象转变为界面中显示的数据
		TaskSet obj = (TaskSet)object;
		
		IDs[0] = DataConvert.int2String(obj.getID());
		
		((JTextField) component[0]).setText(obj.getFreq());
		((JComboBox) component[1]).setSelectedItem(obj.getTaskType());
		((JTextField) component[2]).setText(DataConvert.int2String(obj.getSchemeID()));
		((JTextField) component[3]).setText(obj.getBeginTime());
		((JTextField) component[4]).setText(obj.getEndTime());
		((JTextField) component[5]).setText(obj.getDelay());
		((JComboBox) component[6]).setSelectedItem(obj.getTaskPriority());
		((JComboBox) component[7]).setSelectedItem(obj.getTaskState());
		((JTextField) component[8]).setText(DataConvert.int2String(obj.getPreScriptID()));
		((JTextField) component[9]).setText(DataConvert.int2String(obj.getAfterScriptID()));
		((JComboBox) component[10]).setSelectedItem(obj.getIntervalType());
	}

	@Override
	public TaskSet getData() {
		// 将界面中显示的数据转变为对象 需要进行数据类型的转换
		TaskSet obj = new TaskSet();
		
		obj.setFreq(((JTextField)component[0]).getText());
		obj.setTaskType(((JComboBox) component[1]).getSelectedItem().toString());
		obj.setSchemeID(DataConvert.String2Int(((JTextField)component[2]).getText()));
		obj.setBeginTime(((JTextField)component[3]).getText());
		obj.setEndTime(((JTextField)component[4]).getText());
		obj.setDelay(((JTextField)component[5]).getText());
		obj.setTaskPriority(((JComboBox) component[6]).getSelectedItem().toString());
		obj.setTaskState(((JComboBox) component[7]).getSelectedItem().toString());
		obj.setPreScriptID(DataConvert.String2Int(((JTextField)component[8]).getText()));
		obj.setAfterScriptID(DataConvert.String2Int(((JTextField)component[9]).getText()));
		obj.setIntervalType(((JComboBox) component[10]).getSelectedItem().toString());
		
		return obj;
	}
	
	@Override
	public TaskSet getDataWithID() {
		TaskSet d = getData();
		d.setID(DataConvert.String2Int(IDs[0]));
		return d;
	}

}
