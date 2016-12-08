package base;

// 用于对数组进行排序，排序为参考数组中的某个字符信息
public class UseToSort implements Comparable<UseToSort>{
	String idx;
	Object[] data;
	
	public String getIdx() {
		return idx;
	}
	public void setIdx(String idx) {
		this.idx = idx;
	}
	public Object[] getData() {
		return data;
	}
	public void setData(Object[] data) {
		this.data = data;
	}

	@Override
	public int compareTo(UseToSort o) {
		String i, j;
		i = this.getIdx();
		j = o.getIdx();
		return i.compareTo(j);
	}

	// 排序示例代码
	//List<UseToSort> list = new ArrayList<UseToSort>();
	//Collections.sort(list);
	
}
