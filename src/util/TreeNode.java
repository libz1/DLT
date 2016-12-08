package util;

import java.util.ArrayList;
import java.util.List;

import com.eastsoft.util.DataConvert;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// 规约数据-树结构
// 参考http://fireinjava.iteye.com/blog/1782161
public class TreeNode {
	//private String id;
	//private String parentId;
	private String key;
	private String value;
	private String note;
	private List<TreeNode> children;
	
	private TreeNode(){
	}

	// 根据json数据构造对象数据
	public TreeNode(String jsonData){
		TreeNode node = new TreeNode();
		node = node.convetFromString(jsonData);
		this.key = node.key;
		this.value = node.value;
		this.children = node.children;
	}
	
	
	// 构造无子节点对象
	public TreeNode(String key,String value){
		this.key = key;
		// xuky 2016.10.19 如果之前的数据已经进行了处理，无需
		if (value.indexOf(" ") < 0 && value.indexOf(":") < 0)
			this.value = Util698.seprateString(value," ");
		else
			this.value = value;
	}
	
	
	// 构造无子节点对象
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

	// 根据key得到节点
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
	
	// 遍历所有节点，使用制表符表示深度
	public String loop(int n){
		String ret ="";
		n++;
		ret = key+"=>"+value +"\r\n";
		if (children == null)
			return ret;
		else
			for( TreeNode t:children ){
				for(int i=0;i<n;i++)
					ret += "－";
				ret += t.loop(n);
			}
			
		return ret;

	}
	
	// 对象转为json数据
	public String convetToString(){
		return new Gson().toJson(this);
	}

	// 由json数据转为对象
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
