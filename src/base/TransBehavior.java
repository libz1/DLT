package base;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * �����ӿڳ����࣬ͨ��ģ�巽ʽ��װ�˲���
 * @author xuky
 * 
 * @version 2016.09.23
 * 
 */
public abstract class TransBehavior {
	
	// �洢������ʾ�Ŀؼ���ָ����Ϣ
	public JComponent[] component  = new JComponent[15];
	// �洢ID��Ϣ
	public String[] IDs = {""}; 
	
	// ����ʵ�ֵ�setComponent������һ�µ�
	public void setComponent(JComponent[] component) {
		int i = 0;
		for(JComponent c:component){
			this.component[i] =c;
			i++;
		}
	}
	
	public void clearComponent() {
		for(JComponent c:component){
			String class_name = c.getClass().toString();
			if (class_name.indexOf("JTextField") >= 0)
				((JTextField) c).setText("");
			if (class_name.indexOf("JTextArea") >= 0)
				((JTextArea) c).setText("");
		}
	}

	public abstract void setData(Object object);	

	public abstract Object getData();

	public abstract Object getDataWithID();

}
