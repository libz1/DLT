package util;

import java.util.ArrayList;
import java.util.List;

import com.eastsoft.util.DataConvert;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// ��Լ����-���ṹ
// �ο�http://fireinjava.iteye.com/blog/1782161
public class TreeNode {
	//private String id;
	//private String parentId;
	private String key;
	private String value;
	private String note;
	private List<TreeNode> children;
	
	private TreeNode(){
	}

	// ����json���ݹ����������
	public TreeNode(String jsonData){
		TreeNode node = new TreeNode();
		node = node.convetFromString(jsonData);
		this.key = node.key;
		this.value = node.value;
		this.children = node.children;
	}
	
	
	// �������ӽڵ����
	public TreeNode(String key,String value){
		this.key = key;
		// xuky 2016.10.19 ���֮ǰ�������Ѿ������˴�������
		if (value.indexOf(" ") < 0 && value.indexOf(":") < 0)
			this.value = Util698.seprateString(value," ");
		else
			this.value = value;
	}
	
	
	// �������ӽڵ����
	public TreeNode(String key,int value){
		this.key = key;
		this.value = DataConvert.int2String(value);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public void addChild(TreeNode chield) {
		if (children == null)
			children = new ArrayList<TreeNode>();
		children.add(chield);
		
	}

	// ����key�õ��ڵ�
	public TreeNode find_child(String key) {
		TreeNode node = null;
		
		if (this.key.equals(key))
			node = this;
		else{
			if (children == null)
				return node;
			for ( TreeNode t:children ){
				node = t.find_child(key);
				if (node != null)
					return node;
			}
		}
		return node;
	}
	
	// �������нڵ㣬ʹ���Ʊ����ʾ���
	public String loop(int n){
		String ret ="";
		n++;
		ret = key+"=>"+value +"\r\n";
		if (children == null)
			return ret;
		else
			for( TreeNode t:children ){
				for(int i=0;i<n;i++)
					ret += "��";
				ret += t.loop(n);
			}
			
		return ret;

	}
	
	// ����תΪjson����
	public String convetToString(){
		return new Gson().toJson(this);
	}

	// ��json����תΪ����
	public TreeNode convetFromString(String jsonData){
		return new Gson().fromJson(jsonData,
				new TypeToken<TreeNode>(){}.getType());		
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}
