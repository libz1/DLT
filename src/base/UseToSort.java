package base;

// ���ڶ����������������Ϊ�ο������е�ĳ���ַ���Ϣ
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

	// ����ʾ������
	//List<UseToSort> list = new ArrayList<UseToSort>();
	//Collections.sort(list);
	
}
