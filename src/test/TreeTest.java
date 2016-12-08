package test;

import java.util.ArrayList;
import java.util.List;

import util.TreeNode;

import com.google.gson.Gson;

public class TreeTest {
	public static void main(String[] args) {
		
		TreeNode frame = new TreeNode("报文解析","681700430502000000000010485E05020202200002002001020011ED0316");
		
		TreeNode dataL = new TreeNode("长度域L","23");
		
		TreeNode control = new TreeNode("控制域C","43");
		frame.addChild(dataL);
		frame.addChild(control);
		
		TreeNode control_DIR = new TreeNode("传输方向位","1");
		TreeNode control_PRM = new TreeNode("启动标志位","1");
		TreeNode control_PartFlag = new TreeNode("分帧标志位","1");
		
		control.addChild(control_DIR);
		control.addChild(control_PRM);
		control.addChild(control_PartFlag);
		
		System.out.println(new Gson().toJson(frame));		
		
		/*
		List<Tree> trees = new ArrayList<Tree>();

		int id = 1;
		Tree t1 = new Tree(0, id++, "我是根树");
		Tree t2 = new Tree(0, id++, "我是第二个根树");
		Tree t3 = new Tree(1, id++, "我是子树");

		trees.add(t1);
		trees.add(t2);
		trees.add(t3);

		Tree t4 = new Tree(1, id++, "树根你好");
		Tree t5 = new Tree(4, id++, "我不是树根");
		Tree t6 = new Tree(5, id++, "我才是树根");
		trees.add(t4);
		trees.add(t5);
		trees.add(t6);

		show(trees);

		// 不适合做
		System.out.println(new Gson().toJson(trees));
		
		*/
	}

	public static void show(List<Tree> trees) {
		for (int i = 0; i < trees.size(); i++) {
			Tree t = trees.get(i);
			if (t.parent == 0) {
				StringBuffer blank = new StringBuffer();
				t.show(trees, blank);
			}
		}
	}

}
