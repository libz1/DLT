package entity;

import base.CommonObjectList;
import util.Util698;
import util.Util698.NewObjAction;

//任务配置单元  taskset表
public class TaskSet {
	int ID;// 任务id
	String freq;// 执行频率 TI
	String taskType; // 任务类型
						// 普通采集方案(1),事件采集方案(2),透明方案(3),上报方案(4),脚本方案(5),实时监控方案(6)
	int schemeID; // 采集方案编号 当方案类型为脚本时，方案编号为脚本id
	String beginTime; // 开始时间
	String endTime;// 结束时间
	String delay; // 延时 TI
	String taskPriority; // 执行优先级 首要（1），必要（2），需要（3），可能（4）
	String taskState; // 状态 {正常（1），停用（2）
	int preScriptID;// 任务开始前脚本
	int afterScriptID;// 任务完成后脚本
	// 任务运行时段
	String intervalType; // 类型 前闭后开 （0）， 前开后闭 （1）， 前闭后闭 （2）， 前开后开 （3）

	// 时段表
	CommonObjectList<TaskTime> taskTimeList;
	
	
	public TaskSet(){
		freq = "3,1"; // 间隔为：1日
		taskType = "普通采集方案(1)"; 
		taskPriority = "需要(3)";
		taskState = "正常(1)";
		intervalType = "前闭后闭 (2)";
		
	}

	// 从数据库中获取时段表信息
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
