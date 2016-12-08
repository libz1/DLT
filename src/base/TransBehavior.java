package base;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 动作接口抽象类，通过模板方式封装了部分
 * @author xuky
 * 
 * @version 2016.09.23
 * 
 */
public abstract class TransBehavior {
	
	// 存储用于显示的控件的指针信息
	public JComponent[] component  = new JComponent[15];
	// 存储ID信息
	public String[] IDs = {""}; 
	
	// 所有实现的setComponent代码是一致的
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
