package entity;

//����ʱ��
public class TaskTime {
	int ID;
	int tasksetid; // �������ñ�id
	String beginhh; // ��ʼСʱ
	String beginmm; // ��ʼf����
	String endhh; // ����Сʱ
	String endmm; // ��������

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public int getTasksetid() {
		return tasksetid;
	}

	public void setTasksetid(int tasksetid) {
		this.tasksetid = tasksetid;
	}

	public String getBeginhh() {
		return beginhh;
	}

	public void setBeginhh(String beginhh) {
		this.beginhh = beginhh;
	}

	public String getBeginmm() {
		return beginmm;
	}

	public void setBeginmm(String beginmm) {
		this.beginmm = beginmm;
	}

	public String getEndhh() {
		return endhh;
	}

	public void setEndhh(String endhh) {
		this.endhh = endhh;
	}

	public String getEndmm() {
		return endmm;
	}

	public void setEndmm(String endmm) {
		this.endmm = endmm;
	}

}
