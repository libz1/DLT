package test;

import java.util.List;

public class Tree {
	public Tree(int parent, int id, String str) {
		this.parent = parent;
		this.id = id;
		this.str = str;
	}

	int parent;// Ê÷µÄ¸ùÊ÷
	int id;
	String str;

	// StringBuffer blank = new StringBuffer();
	void show(List<Tree> trees, StringBuffer blank) {
		blank.append("      ");
		System.out.println(blank + str);
		for (int i = 0; i < trees.size(); i++) {
			Tree t = trees.get(i);
			if (t.parent == id) {
				t.show(trees, blank);
			}
		}
	}

}
