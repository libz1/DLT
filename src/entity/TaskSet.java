package entity;

import base.CommonObjectList;
import util.Util698;
import util.Util698.NewObjAction;

//�������õ�Ԫ  taskset��
public class TaskSet {
	int ID;// ����id
	String freq;// ִ��Ƶ�� TI
	String taskType; // ��������
						// ��ͨ�ɼ�����(1),�¼��ɼ�����(2),͸������(3),�ϱ�����(4),�ű�����(5),ʵʱ��ط���(6)
	int schemeID; // �ɼ�������� ����������Ϊ�ű�ʱ���������Ϊ�ű�id
	String beginTime; // ��ʼʱ��
	String endTime;// ����ʱ��
	String delay; // ��ʱ TI
	String taskPriority; // ִ�����ȼ� ��Ҫ��1������Ҫ��2������Ҫ��3�������ܣ�4��
	String taskState; // ״̬ {������1����ͣ�ã�2��
	int preScriptID;// ����ʼǰ�ű�
	int afterScriptID;// ������ɺ�ű�
	// ��������ʱ��
	String intervalType; // ���� ǰ�պ� ��0���� ǰ����� ��1���� ǰ�պ�� ��2���� ǰ���� ��3��

	// ʱ�α�
	CommonObjectList<TaskTime> taskTimeList;
	
	
	public TaskSet(){
		freq = "3,1"; // ���Ϊ��1��
		taskType = "��ͨ�ɼ�����(1)"; 
		taskPriority = "��Ҫ(3)";
		taskState = "����(1)";
		intervalType = "ǰ�պ�� (2)";
		
	}

	// �����ݿ��л�ȡʱ�α���Ϣ
	public void init_tasktimeFromDB() {
		NewObjAction newobj_act = new NewObjAction() {
			public Object getNewObject() {
				return new TaskTime();
			}
		};
		taskTimeList = new CommonObjectList<TaskTime>(newobj_act, "tasktime", "beginhh,beginmm", "tasksetid=" + ID,
				"order by beginhh,beginmm");
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String data) {
		taskType = Util698.getCodeData(data, "taskType");
	}

	public int getSchemeID() {
		return schemeID;
	}

	public void setSchemeID(int schemeID) {
		this.schemeID = schemeID;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getTaskPriority() {
		return taskPriority;
	}

	public void setTaskPriority(String data) {
		taskPriority = Util698.getCodeData(data, "taskPriority");
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String data) {
		taskState = Util698.getCodeData(data, "taskState");
	}

	public int getPreScriptID() {
		return preScriptID;
	}

	public void setPreScriptID(int preScriptID) {
		this.preScriptID = preScriptID;
	}

	public int getAfterScriptID() {
		return afterScriptID;
	}

	public void setAfterScriptID(int afterScriptID) {
		this.afterScriptID = afterScriptID;
	}

	public String getIntervalType() {
		return intervalType;
	}

	public void setIntervalType(String data) {
		intervalType = Util698.getCodeData(data, "intervalType");
	}

	public CommonObjectList<TaskTime> getTaskTimeList() {
		return taskTimeList;
	}

	public void setTaskTimeList(CommonObjectList<TaskTime> taskTimeList) {
		this.taskTimeList = taskTimeList;
	}

}
