package frame;

import java.lang.reflect.Method;

import util.*;

import com.eastsoft.util.DataConvert;
import com.eastsoft.util.DebugSwing;

public class AnalyData {

	TreeNode node = null;
	String desc = "";
	Object[][] analyMsgList = null;
	int currentMsgLine = -1;
	
	public AnalyData( Object[][] analyMsgList,int currentMsgLine  ){
		this.analyMsgList = analyMsgList;
		this.currentMsgLine = currentMsgLine;
	}
	
	public AnalyData( ){
	}
	
	// 解析单个DATA
	public String[] dealDATA(String data, TreeNode n) {
		String ret[] = { "", "" };

		// 从报文中得到数据类型，从基本类型表中找到数据类型的定义信息
		int type = DataConvert.hexString2Int(data.substring(0, 2));
		String tmp = DataConvert.BinStr2String(DataConvert.IntToBinString(type),2);
		// 得到类型的描述信息
		String typeName = DB.getInstance().getTypeInfo(tmp)[0];

		// 增加
//		TreeNode dealdata_node = new TreeNode("Data", "");
//		n.addChild(dealdata_node);
		
		// 增加
		String key = typeName+"("+type+"-0x"+data.substring(0, 2)+")";
		
		String msg[] = dealMsg(); 
		key = key + msg[0];
		String codeVal = msg[1]; 
		
		
		TreeNode treenode = new TreeNode(key, "");
		n.addChild(treenode);
		
		// 根据数据类型调用相关函数进行处理
		String[] s = dealDataWithType(type, data.substring(2), treenode);
//		if (s[2].equals("1")){
//			if (!codeVal.equals("")){
//				// 需要进行键值对对照转换
//				String val = getVal( codeVal,s[0] );
//				treenode.setValue(s[0]+"-"+val);
//			}
//			else
//				treenode.setValue(s[0]);
//		}
		
		// xuky 2016.12.05 无需进行《if (s[2].equals("1"))》的判断
		if (!codeVal.equals("")){
			// 需要进行键值对对照转换
			String val = getVal( codeVal,s[0] );
			treenode.setValue(s[0]+"-"+val);
		}
		else
			treenode.setValue(s[0]);
		
		ret[1] = s[1];
		return ret;
	}
	
	private String getVal(String data,String code){
		
		if (code.indexOf("-") >= 0)
			code = code.split("-")[0];

		String ret = "";
		try{
			ret = getValByComma(data,code);
		}
		catch(Exception e){
			try{
				ret = getValBySemicolon(data,code);
			}
			catch(Exception e1 ){
				try{
					ret = getValByComma1(data,code);
				}
				catch(Exception e2){
					e2.printStackTrace();
				}
			}
			if (ret.equals("")){
				try{
					ret = getValByComma1(data,code);
				}
				catch(Exception e2){
//					e2.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	private String getValBySemicolon(String data,String code){
		// 0："分"状态；1："合"状态
		String ret = "";
		code = DataConvert.int2String(DataConvert.String2Int(code));
		data = data.replaceAll("；", ";");
		data = data.replaceAll("：", ":");
		String[] list = data.split(";"); 
		for(String str: list){
			String[] tmp = str.split(":");  
			if (tmp[0].equals(code)){
				return tmp[1];
			}
		}
		return ret;
	}
	
	//无电能表[0]NULL，全部用户地址[1]NULL，一组用户类型[2]SEQUENCEOFunsigned，一组用户地址[3]SEQUENCEOFTSA，一组配置序号[4]SEQUENCEOFlong-unsigned，一组用户类型区间[5]SEQUENCEOFRegion，一组用户地址区间[6]SEQUENCEOFRegion，一组配置序号区间[7]SEQUENCEOFRegion
	private String getValByComma1(String data,String code){
		String ret = "";
		code = DataConvert.int2String(DataConvert.String2Int(code));
		data = data.replaceAll("，", ",");
		data = data.replaceAll(" ", "");
		String[] list = data.split(","); 
		for(String str: list){
			String[] tmp = str.split("\\[");  
			if (tmp[1].split("\\]")[0].equals(code)){
				return tmp[0];
			}
		}
		return ret;
	}

	
	
	//未知(0)，DL/T645-1997（1），DL/T645-2007（2），DL/T698.45（3），CJ/T188-2004（4）
	private String getValByComma(String data,String code){
		String ret = "";
		code = DataConvert.int2String(DataConvert.String2Int(code));
		data = data.replaceAll("，", ",");
		data = data.replaceAll("（", "(");
		data = data.replaceAll("）", ")");
		data = data.replaceAll(" ", "");
		String[] list = data.split(","); 
		for(String str: list){
			String[] tmp = str.split("\\(");  
			if (tmp[1].split("\\)")[0].equals(code)){
				return tmp[0];
			}
		}
		return ret;
	}
	
	
	private String[] dealMsg() {
		String ret[] = {"",""};
		
		if (currentMsgLine ==-1)
			return ret;
		if (analyMsgList == null)
			return ret;
		try{
			String type = (String) analyMsgList[currentMsgLine][1];
//			System.out.println("dealMsg=> "+analyMsgList[currentMsgLine][0]);
//			System.out.println("dealMsg=> "+analyMsgList[currentMsgLine][3]);
			
			String codeVal = "";
			if (analyMsgList[currentMsgLine][3] != null){
				try{
					codeVal = (String)analyMsgList[currentMsgLine][3];
				}
				catch(Exception e){
//					e.printStackTrace();
					int n =  (int)analyMsgList[currentMsgLine][3];
					codeVal = DataConvert.int2String(n);
				}
			}
			
			if (type.equals("enum"))
				ret[1] = codeVal;
			
			// xuky 2016.12.01 发现非enum类型的数据，也存在键值对的数据情况【开关量单元 状态ST——0：“分”状态；1：“合”状态】
			if (codeVal.indexOf(";")>=0 || codeVal.indexOf("；")>=0)
				ret[1] = codeVal;
			
			// xuky 2016.12.02 发现非enum类型的数据，也存在键值对的数据情况【无电能表[0]NULL，全部用户地址[1]NULL，一组用户类型[2]SEQUENCEOFunsigned，一组用户地址[3]SEQUENCEOFTSA，一组配置序号[4]SEQUENCEOFlong-unsigned，一组用户类型区间[5]SEQUENCEOFRegion，一组用户地址区间[6]SEQUENCEOFRegion，一组配置序号区间[7]SEQUENCEOFRegion】
			if (codeVal.indexOf(",")>=0 || codeVal.indexOf("，")>=0)
				ret[1] = codeVal;
			
			ret[0] = "【"+(String) analyMsgList[currentMsgLine][0]+"】";
			
			if  (((String) analyMsgList[currentMsgLine][0]).equals("数据")){
				ret = ret;
			}
			if (analyMsgList[currentMsgLine][1].equals("array")){
				// 需要对其中的当前数量进行初始化赋值，
				analyMsgList[currentMsgLine][3] = 0;
			}
			
//			System.out.println("dealMsg=>"+ret);
			currentMsgLine++;
			
//			// 数据到达末尾了，需要进行调整
//			if (currentMsgLine == analyMsgList.length){
//				String array_idx = (String) analyMsgList[currentMsgLine-1][5];
//				String nextStr = array_idx.substring(0, 7);
//				int nextLine = getNextNode(nextStr);
//				if (nextLine != -1)
//					currentMsgLine = nextLine;
//				else{
////					DebugSwing.showMsg("数据分析指针异常，需要开发人员进行调整");
//				}
//			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			return ret;
		}

		return ret ;
	}

	// 采用反射技术，调用动态函数
	public String[] dealDataWithType(int type, String data, TreeNode n) {
		String ret[] = { "", "","" };

		String tmp = DataConvert.BinStr2String(DataConvert.IntToBinString(type),2);
		String typeNum = DB.getInstance().getTypeInfo(tmp)[1];
		
		// xuky 2016.11.23 使用自定义的程序进行type28类型数据的解析
		if (type == 28)
			typeNum = "0";
		
		if (typeNum == null || typeNum.equals("0")){
			// 如果在数据库中未配置，需要自行编写解析程序进行处理
			try {
				AnalyData analyData_instance = new AnalyData( analyMsgList,currentMsgLine );
				String fun = "dealType"	+ DataConvert
						.BinStr2String(DataConvert.IntToBinString(type), 2);
				Method m = (Method) analyData_instance.getClass().getMethod(fun,
						new Class[] { String.class, TreeNode.class });
				String[] result = (String[]) m.invoke(analyData_instance, new Object[] { data, n });
				ret[0] = result[0];
				ret[1] = result[1];
			} catch (Exception e) {
				System.out.println("dealDataWithType_new=>"+e.getMessage());
				String str = DataConvert.int2HexString(type, 2) + data;
				str = Util698.seprateString(str , " ");
				String msg = "数据类型=>" + type + "，无解析代码，请维护代码后重新执行";
				System.out.println("正在处理数据=>" + str);
				System.out.println(msg);
				DebugSwing.showMsg(msg);
			}
		}
		else{
			// 如果从数据库中可以找到，且指定了数据长度，则直接取出即可
			int len = DataConvert.String2Int(typeNum)*2; 
			//node = new TreeNode("【DB】"+t[0], data.substring(0, len));
			//n.addChield(node);
			ret[0] = data.substring(0,len);
			ret[1] = data.substring(len);
			ret[2] = "1";
			// (2-0x02)
			if (type == 5 || type == 6 || (type >= 15 && type <= 18) || type == 20 || type == 21 )
				ret[0] = DataConvert.int2String(DataConvert.hexString2Int(ret[0]))+"-0x"+Util698.seprateString(ret[0]," ");
		}
		return ret;
	}
	
	public String[] dealWithNum(String data, TreeNode n, String fun) {
		return dealWithNum(data, n, fun, true);
	}
	
	private void setMsgNum(int num){
//		[0]name
//		[1]type
//		[2]总数量
//		[3]当前数量
//		[4]id
//		[5]array_index
		if (currentMsgLine ==-1)
			return ;
		if (analyMsgList == null)
			return ;
		try{
			// 前一个元素  对数组类型的数据进行循环总数赋值
			analyMsgList[currentMsgLine-1][2] = num;
		}
		catch(Exception e){
			return;
		}
		
	}
	
	// 按照个数进行循环的反射调用
	public String[] dealWithNum(String data, TreeNode n, String fun, Boolean isArrary) {
		String ret[] = { "", "" };
		String[] s = { "", "" };
		
		// 得到需要循环执行的次数信息
		int num = DataConvert.hexString2Int(data.substring(0, 2));
		
		setMsgNum(num);
		
		// 在tree中显示循环次数信息
		n.setValue("共"+num+"个");
		
		String restData = data.substring(2);
		Method method = null;
		try {
			// 反射调用指定函数
			AnalyData analyData_instance = new AnalyData( analyMsgList,currentMsgLine );
			method = (Method) analyData_instance.getClass().getMethod(fun,
					new Class[] { String.class, TreeNode.class });
			for (int i = 0; i < num; i++) {
				
				TreeNode tn = new TreeNode("序号", i+1);
				n.addChild(tn);
				
//				System.out.println("dealWithNum=> "+fun + " "+(i+1));
				
				s = (String[]) method.invoke(analyData_instance, new Object[] {
						restData, tn });
				restData = s[1];
				
				// 在此需要调整msg指针位置：向上找到最近的一个arrary或seq 类型数据
				// 后续考虑：修改其数量信息，进行其他处理
				
				// 仅仅对数组类型的数据进行处理 调整指针位置，因为需要多次进行循环
				if (isArrary)
					dealLineOfArrary();
			}
			if (num == 0 && isArrary)
				dealLineOfArrary();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		ret[0] = DataConvert.int2String(num);
		ret[1] = restData;
		return ret;
	}
	
	private void dealLineOfArrary(){
		for(int i= currentMsgLine-1;i>=0;i--){
			if (((String)analyMsgList[i][1]).equals("array")){
				
				// 需要增加判断，如果当前arrary的数量达到了上限，就需要跳到外面  同级的下一个，如果结束了，向上判断处理
				// 所以需要记录总的循环次数，当前的执行次数
				if (analyMsgList[i][3] == null || analyMsgList[i][3].equals("") )
					analyMsgList[i][3] = 1;
				else
					analyMsgList[i][3] = (int)analyMsgList[i][3] +1;
				
				int a = 0,b = 0; 
				
				try{
					a = (int)analyMsgList[i][3];
					b = (int)analyMsgList[i][2];
				}
				catch(Exception e){
					try{
						b = DataConvert.String2Int((String)analyMsgList[i][2]);
					}
					catch(Exception e1){
						e1.printStackTrace();
					}
				}
				
				if ( a >= b ){
					String array_idx = (String)analyMsgList[i][5];
					// 推算下一个节点 null_000_001_002
					int next = DataConvert.String2Int(array_idx.substring(array_idx.length()-3))+1;
					String nextStr = array_idx.substring(0, array_idx.length()-3)+DataConvert.hexString2String(DataConvert.int2HexString(next, 4), 3);
					int nextLine = getNodeByArrayidx(nextStr);
					if (nextLine == -1){
						nextStr = array_idx.substring(0, array_idx.length()-4);
						nextLine = getNodeByArrayidx(nextStr);
						currentMsgLine = nextLine;
					}
					else
						currentMsgLine = nextLine;
				}
//				else if (a > b){
//					// 适用于  “OMD(上报方案集)=>60 1C 7F 00”的数据解析过程
//					// 需要找上一级节点 上一级可能是struture 则需要继续向上
//					// 上一级可能是数组，需要判断数组是否数量达到要求，是，则需要继续向上 123
//					String array_idx = (String)analyMsgList[i][5];
//					String nextStr = array_idx.substring(0, array_idx.length()-4);
//					int nextLine = getNodeByArrayidx(nextStr);
//					if (analyMsgList[nextLine][1].equals("structure")){
//						nextStr = nextStr.substring(0, nextStr.length()-4);
//						nextLine = getNodeByArrayidx(nextStr);
//						if (nextLine != -1)
//							currentMsgLine = nextLine+1;
//					} 
//				}
				else{
					// 回到数组的起点 ，即数组根节点的后续多一个节点
					currentMsgLine = i+1;
				}
				break;
			}
		}
	}
	
	public int getNodeByArrayidx(String array_idx){
		int i=0;
		for (Object[] o:analyMsgList){
			if (((String)o[5]).equals(array_idx))
				return i;
			i++;
		}
		return -1;
	}
	
	// 数组对象   多次进行数据处理  01数组后一般是一个结构体 会出现递归调用的情况
	public String[] dealType01(String data, TreeNode n) {
		return dealWithNum(data, n, "dealDATA",true);
	}
	// 结构体对象  结构体后一般是多个具体数据
	public String[] dealType02(String data, TreeNode n) {
		return dealWithNum(data, n, "dealDATA",false);
	}
	
	public String[] dealSeqOfResultNormal(String data, TreeNode n) {
		return dealWithNum(data, n, "dealResultNormal");		
	}
	
	
	public String[] dealType96(String data, TreeNode n) {
		return dealWithNum(data, n, "dealCSD");
	}
	
	// RSD
	public String[] dealType90(String data, TreeNode n) {
		return dealRSD(data, n);
	}

	
	// bit-string
	public String[] dealType04(String data, TreeNode n) {
		String ret[] = { "", "" };
		// bit-string = 04 08 53  04类型 8bit ,内容是53
		int bitSize = DataConvert.hexString2Int(data.substring(0, 2)); 
		int byteSize = bitSize/8; 
		String str = data.substring(2,2+byteSize*2);
		str = DataConvert.hexString2BinString(str, bitSize);
		node = new TreeNode("bit-strig(SIZE("+bitSize+"))", str);
		n.addChild(node);
		ret[1] = data.substring(2+byteSize*2);
		return ret;
	}
	
	public String[] dealType95(String data, TreeNode n) {
		String ret[] = { "", "" };
		// 68 27 00 43 05 08 00 00 00 00 00 10 D0 24 06 01 00 F2 0B 02 00 01 01 02 02 0A 04 31 32 33 34 5F 03 02 08 01 00 00 AC 80 16 
		// COMDCB  = 5F X1 X2 X3 X4 X5 , 5F 是类型 
		node = new TreeNode("COMDCB", data.substring(0,10));
		n.addChild(node);
		ret[1] = data.substring(10);
		return ret;
	}
	
	// visible-string
	public String[] dealType10(String data, TreeNode n) {
		String ret[] = { "", "" };
		// visible-string = 0A 05 43 4D 4E 45 54   0A类型 05长度 43 4D 4E 45 54 = CMNET 
		int byteSize = DataConvert.hexString2Int(data.substring(0, 2)); 
		String str = data.substring(2,2+byteSize*2);
		str = DataConvert.asciiHex2String(str);
		node = new TreeNode("visible-string", str);
		n.addChild(node);
		ret[1] = data.substring(2+byteSize*2);
		return ret;
	}

	public String[] dealType85(String data, TreeNode n) {
		return dealType09(data, n);
	}
	
	public String[] dealType91(String data, TreeNode n) {
		return dealCSD(data, n);
	}
	
	public String[] dealType92(String data, TreeNode n) {
		return dealMS(data, n);
	}

	public String[] dealType28(String data, TreeNode n) {
		String ret[] = { "", "" };
		// DataTimeBCD[28] = [7Byte]  07 E0 09 10 17 3B 3B
		String tmp = data.substring(0, 14);
		String t = DataConvert.hexString2String(tmp.substring(0, 4), 4);
		t += "-" + DataConvert.hexString2String(tmp.substring(4, 6), 2);
		t += "-" + DataConvert.hexString2String(tmp.substring(6, 8), 2);
		t += " " + DataConvert.hexString2String(tmp.substring(8, 10), 2);
		t += ":" + DataConvert.hexString2String(tmp.substring(10, 12), 2);
		t += ":" + DataConvert.hexString2String(tmp.substring(12), 2);
		node = new TreeNode("DataTimeBCD", t+"-0x"+Util698.seprateString(tmp, " "));
		n.addChild(node);
		ret[1] = data.substring(14);
		return ret;
	}
	
	public String[] dealType17(String data, TreeNode n) {
		String ret[] = { "", "" };
		// unsigned[17] = [1Byte]

		TreeNode node = new TreeNode("unsigned", data.substring(0, 2));
		n.addChild(node);

		ret[1] = data.substring(2);
		return ret;
	}
	
	public String[] dealType22(String data, TreeNode n) {
		String ret[] = { "", "" };
		// enum[16] = [1Byte]

		TreeNode node = new TreeNode("enum", data.substring(0, 2));
		n.addChild(node);

		ret[1] = data.substring(2);
		return ret;
	}
	
	public String[] dealType18(String data, TreeNode n) {
		String ret[] = { "", "" };
		// long-unsigned[18] = [2Byte]

		TreeNode node = new TreeNode("", data.substring(0, 4));
		n.addChild(node);

		ret[1] = data.substring(4);
		return ret;
	}

	

	/*
	public String[] dealType15(String data, TreeNode n) {
		String ret[] = { "", "" };
		// integer[15] = [1Byte]

		TreeNode node = new TreeNode("integer", data.substring(0, 2));
		n.addChield(node);

		ret[1] = data.substring(2);
		return ret;
	}

	



	public String[] dealType05(String data, TreeNode n) {
		String ret[] = { "", "" };
		// double-long[18] = [4Byte]
		int byteLen = 4;
		TreeNode node = new TreeNode("long-unsigned", data.substring(0,
				byteLen * 2));
		n.addChield(node);

		ret[1] = data.substring(byteLen * 2);
		return ret;
	}

	public String[] dealType06(String data, TreeNode n) {
		String ret[] = { "", "" };
		// double-long[18] = [4Byte]
		int byteLen = 4;
		TreeNode node = new TreeNode("double-long-unsigned", data.substring(0,
				byteLen * 2));
		n.addChield(node);

		ret[1] = data.substring(byteLen * 2);
		return ret;
	}
	*/
	public String[] dealType84(String data, TreeNode n) {
		String ret[] = { "", "" };

		// [84] = [3Byte ]
		// 54 03 00 01
		
		int len = 3;
		TreeNode node = new TreeNode("长度"+len+"Byte", data.substring(0, len * 2));
		n.addChild(node);

//		单位  ENUMERATED
//		   {		       秒      （0），		       分      （1），		       时      （2），		       日      （3），		       月      （4），
//		       年      （5）		    }，
//		    间隔值  long-unsigned
		
//		node = new TreeNode("内容", );
//		n.addChild(node);

		ret[1] = data.substring(len * 2);
		return ret;
	}
	

	public String[] dealType09(String data, TreeNode n) {
		String ret[] = { "", "" };

		// [09] = [1Byte datalen=n][ nByte data ]

		int len = DataConvert.hexString2Int(data.substring(0, 2));
		TreeNode node = new TreeNode("octet-string 长度"+len+"Byte",data.substring(2, 2 + len * 2) );
		n.addChild(node);

//		TreeNode node1 = new TreeNode("内容", );
//		node.addChild(node1);

		ret[1] = data.substring(2 + len * 2);
		return ret;
	}

	public String[] dealDAR(String data, TreeNode t) {
		String ret[] = { "", "" };
		int tmp = DataConvert.hexString2Int(data.substring(0, 2));
		String type = DataConvert.int2String(tmp);
		node = new TreeNode("DAR("+DB.getInstance().getDARInfo(type)+")",tmp );
		t.addChild(node);
		ret[1] = data.substring(2);
		return ret;
	}

	public String[] dealOAD(String data, TreeNode t) {
		String ret[] = { "", "" };
		ret[0] = data.substring(0, 8);
		ret[1] = data.substring(8);
		String info=DB.getInstance().getOADInfo(data.substring(0, 4));
		
		String OAD = ret[0];
		String attr_name = DB.getInstance().getOADData(OAD,true);
		
		// 用于进行辅助分析的数组
		analyMsgList =  FrameNew.getFuncParasWithRoot(ret[0],null,"Attr");
		currentMsgLine = 0;
		
		node = new TreeNode("OAD("+info+" "+ attr_name +")", ret[0]);
		t.addChild(node);
		return ret;
	}
	

	public String[] dealType81(String data, TreeNode t) {
		return dealOAD(data, t);
	}

	// 解析结果在ret[0]中，余下的部分在ret[1]
	// 解析单个OMD
	public String[] dealOMD(String data,TreeNode t) {
		String ret[] = { "", "" };
		ret[0] = data.substring(0, 8);
		ret[1] = data.substring(8);
		
		String info=DB.getInstance().getOADInfo(data.substring(0, 4));
		
		String OAD = ret[0];
		
		String method_name = DB.getInstance().getOADData(OAD,false);
		
		// 用于进行辅助分析的数组
		analyMsgList =  FrameNew.getFuncParasWithRoot(ret[0],null,"Func");
		currentMsgLine = 0;

		node = new TreeNode("OMD("+info+" "+ method_name +")", ret[0]);
		
		t.addChild(node);

		return ret;
	}

	public String[] dealSeqOfOAD(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOAD");		
	}

	public String[] dealRecord(String data, TreeNode t) {
		
		String ret[] = { "", "" };

		String[] s = dealOAD(data, t);
		s = dealRSD(s[1], t);
		
		node = new TreeNode("SEQUENCE OF CSD(RCSD)","");
		t.addChild(node);
		s = dealSeqOfCSD(s[1], node);

		ret[1] = s[1];
		return ret;
	}

	public String[] dealRSD(String data, TreeNode t) {
		String ret[] = { "", "" };
		String s[] = { "", "" };

		int type = DataConvert.hexString2Int(data.substring(0, 2));
		TreeNode rsd_node = new TreeNode("RSD类型", type);
		t.addChild(rsd_node);
		String restData = data.substring(2);
		switch (type) {
		case 0:
			s[1] = restData;
			break;
		case 1:
			s = dealOADWithData(restData, rsd_node);
			break;
		case 2:
			// Selector2
			s = dealRSDSelector2(restData, rsd_node);
			break;
		case 3:
			// SEQUENCE OF Selector2
			s = dealSeqOfRSDSelector2(restData, rsd_node);
			break;
		case 4:
		case 5:
			// 采集存储时间
			s = dealType28(restData, rsd_node);
			// 电能表集合 MS
			s = dealMS(s[1], rsd_node);
			break;
		case 6:
		case 7:
		case 8:
			// 采集存储时间起始值 date_time_s
			s = dealType28(restData, rsd_node);
			// 采集存储时间结束值 date_time_s
			s = dealType28(s[1], rsd_node);
			//  时间间隔            TI
			s = dealType84(s[1], rsd_node);
			// 电能表集合 MS
			s = dealMS(s[1], rsd_node);
			break;
		case 9:
			s = dealType17(restData, rsd_node);
			break;
		case 10:
			s = dealType17(restData, rsd_node);
			// 电能表集合 MS
			s = dealMS(s[1], rsd_node);
			break;
		default:
			break;
		}

		ret[1] = s[1];
		return ret;
	}

	// 处理 RSD数据类型 的Selector2  
	public String[] dealRSDSelector2(String data, TreeNode n) {
		String ret[] = { "", "" };
		String[] s = dealOADWithData(data, n);
		s = dealDATA(s[1], n);
		s = dealDATA(s[1], n);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealSeqOfRSDSelector2(String data, TreeNode n) {
		return dealWithNum(data, n, "dealRSDSelector2");	
	}
	
	
	
	public String[] dealMS(String data, TreeNode t) {
		String ret[] = { "", "" };
		String s[] = { "", "" };

		int type = DataConvert.hexString2Int(data.substring(0, 2));
		TreeNode n = new TreeNode("MS类型", type);
		t.addChild(n);

		switch (type) {
		case 0:
			// null xuky 2016.08.15 根据报文举例。认为null是数据，占用1byte，00 null 
			s[1] = data.substring(2);
			
			// 2Byte以后  null为有数据
			//s[1] = data.substring(4);
			break;
		case 1:
			//s[1] = data.substring(4);
			s[1] = data.substring(2);
			break;
		case 2:
			// SEQUENCE OF unsigned
			s[1] = data.substring(2);
			break;
		case 3:
			// SEQUENCE OF TSA
			// TSA∷=octet-string(SIZE(2…17)) 见5.1.4.2.1　。
			//
			TreeNode treenode = new TreeNode("SEQUENCE OF TSA","");
			n.addChild(node);
			s = dealSeqOfTSA(data.substring(2), treenode);
			break;
		default:
			break;
		}

		ret[1] = s[1];
		return ret;
	}

	public String[] dealSeqOfTSA(String data, TreeNode n) {
		return dealWithNum(data, n, "dealTSA");		
	}

	public String[] dealTSA(String data, TreeNode t) {
		String ret[] = { "", "" };

		// 06 04 00 00 00 01 21
		int len = DataConvert.hexString2Int(data.substring(2, 4)) + 1;

		node = new TreeNode("TSA", data.substring(0, 2 + 2 + len * 2));
		t.addChild(node);

		ret[1] = data.substring(2 + 2 + len * 2);
		return ret;
	}

	// RCSD
	public String[] dealSeqOfCSD(String data, TreeNode n) {
		return dealWithNum(data, n, "dealCSD");
	}

	public String[] dealCSD(String data, TreeNode t) {
		return new AnalyData().newDealCSD(data, t);
	}
	
	
	public String[] newDealCSD(String data, TreeNode t) {
		String ret[] = { "", "" };
		String s[] = { "", "" };

		int type = DataConvert.hexString2Int(data.substring(0, 2));
		TreeNode n = new TreeNode("CSD类型", type);
		t.addChild(n);
		String restData = data.substring(2);

		if (type == 0) {
			// OAD
			// 123
			s = dealOAD(restData, n);
		} else {
			// ROAD
			s = dealROAD(restData, n);
		}

		ret[1] = s[1];
		return ret;
	}

	public String[] dealROAD(String data, TreeNode t) {
		String ret[] = { "", "" };

		String s[] = dealOAD(data, t);

		node = new TreeNode("SEQUENCE OF OAD","");
		t.addChild(node);
		s = dealSeqOfOAD(s[1], node);

		ret[1] = s[1];
		return ret;
	}

	public String[] dealSeqOfOADWithDAR(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOADWithDAR");		
	}

	public String[] dealOADWithDAR(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealOAD(data, t);
		s = dealDAR(s[1], t);
		ret[1] = s[1];
		return ret;
	}

	public String[] dealSeqOfResultRecord(String data, TreeNode n) {
		return dealWithNum(data, n, "dealResultRecord");		
	}

	// 解析结果在ret[0]中，余下的部分在ret[1]
	public String[] dealFollowReport(String data, TreeNode t) {
		String ret[] = { "", "" };

		String flag = data.substring(0, 2);
		if (flag.equals("00")) {
			node = new TreeNode("跟随上报", flag);
			t.addChild(node);
			ret[1] = data.substring(2);
		} else {
			
			String flag1 = data.substring(2, 4);
			String[] s = {"",""};
			if (flag1.equals("01")){
				node = new TreeNode("跟随上报(SEQUENCE OF A-ResultNormal)", "");
				t.addChild(node);
				s = dealSeqOfResultNormal(data, node);
			}
			else{
				node = new TreeNode("跟随上报(SEQUENCE OF A-ResultRecord)", "");
				t.addChild(node);
				s = dealSeqOfResultRecord(data, node);
			}
			ret[1] = s[1];
		}

		return ret;
	}

	// 解析单个ResultNormal RON ResultOfNormal
	public String[] dealResultNormal(String data, TreeNode t) {

		String ret[] = { "", "" };

		String[] s = dealOAD(data, t);
		
		s = dealResult(s[1], t);

		//int result = DataConvert.hexString2Int(s[1].substring(0, 2));

		if (s[0] == "00") {
			s = dealDAR(s[1], node);
		} else
			// 需要根据前面的OAD进行struct类型的解析
			s = dealDATA(s[1], node);
		ret[1] = s[1];
		return ret;
	}

	private String[] dealResult(String data, TreeNode t) {
		String ret[] = { "", "" };
		int tmp = DataConvert.hexString2Int(data.substring(0, 2));
		String type = DataConvert.int2String(tmp);
		String msg = "DAR";
		if (tmp == 1)
			msg = "DATA";
		node = new TreeNode("Get-Result("+msg+")",type );
		t.addChild(node);
		ret[0] = type;
		ret[1] = data.substring(2);
		return ret;
	}

	// 解析结果在ret[0]中，余下的部分在ret[1]
	// 解析多个OAD,多个OAD使用";"分隔
	public String[] dealSeqOfOADWithData(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOADWithData");		
	}

	public String[] dealOADWithData(String data, TreeNode t) {
		String ret[] = { "", "" };

		String[] s = dealOAD(data, t);

		// dealDATA 准备作为子节点，所以需要将父节点作为参数传递
		s = dealDATA(s[1], t);
		ret[1] = s[1];
		return ret;
	}

	public String[] dealTimeTag(String data, TreeNode t) {
		String ret[] = { "", "" };

		// TimeTag OPTIONAL =
		// 00 + 无后续
		// 01 + 发送时标（DateTimeBCD[28]-14字节）+允许传输延时时间（TI[84]）
		// TI[84] = ENUMERATED-1 字节 + long-unsigned[18]-2字节
		// {16比特位正整数（Unsigned16）}

		// TimeTag = OPTIONAL([28][84])
		// [84] = [ENUMERATED][18]

		// OPTIONAL = 00|01...

		// OPTIONAL
		String flag = data.substring(0, 2);
		if (flag.equals("00")) {
			node = new TreeNode("时间标签", flag);
			t.addChild(node);
			ret[1] = data.substring(2);
		} else {
			// 发送时标 DateTimeBCD， 14
			// 允许传输延时时间 TI 3
			node = new TreeNode("时间标签", data.substring(2, 2 + 17 * 2));
			t.addChild(node);
			ret[1] = data.substring(2 + 17 * 2);
		}

		return ret;
	}

	public String[] dealType00(String data, TreeNode n) {
		String ret[] = { "", "" };
		ret[1] = data;
		return ret;
	}

	public String[] dealSeqOfOMDWithDARAndDataAndRead(String data,TreeNode n) {
		return dealWithNum(data, n, "dealOMDWithDARAndDataAndRead");		
	}
	
	
	public String[] dealOMDWithDARAndDataAndRead(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealOMD(data,t);
		s = dealDAR(s[1], t);
		s = dealDATA(s[1], t);
		s = dealResultNormal(s[1], t);
		ret[1] = s[1];
		return ret;
	}


	public String[] dealSeqOfOMDWithDARAndData(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOMDWithDARAndData");
	}

	public String[] dealOMDWithDARAndData(String data, TreeNode t) {
		String ret[] = { "", "" };

		String[] s = dealOMD(data,t);

		s = dealDAR(s[1], t);
		// xuky 2016.08.16 可能有问题，Data 的定义是Data  OPTIONAL  与一般定义不同    
		s = dealDATA(s[1], t);
		ret[1] = s[1];
		return ret;
	}

	public String[] dealSeqOfOMDWithDataAndRead(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOMDWithDataAndRead");		
	}

	public String[] dealOMDWithDataAndRead(String data, TreeNode t) {
		String ret[] = { "", "" };

		String[] s = dealOMDWithData(data, t);

		s = dealOAD(s[1], t);

		node = new TreeNode("延时读取时间", s[1].substring(0, 2));
		t.addChild(node);

		ret[1] = s[1].substring(2);
		return ret;
	}

	public String[] dealOMDWithData(String data, TreeNode t) {
		String ret[] = { "", "" };

		String[] s = dealOMD(data,t);
		
		s = dealDATA(s[1], t);
		ret[1] = s[1];
		return ret;
	}

	public String[] dealSeqOfOMDWithData(String data, TreeNode t) {
		String ret[] = { "", "" };
		int num = DataConvert.hexString2Int(data.substring(0, 2));
		TreeNode numNode = new TreeNode("个数", num);
		t.addChild(numNode);

		String restData = data.substring(2);
		for (int i = 0; i < num; i++) {
			String[] s = dealOMDWithData(restData, numNode);
			ret[0] += s[0] + ";";
			restData = s[1];
		}
		ret[0] = ret[0].substring(0, ret[0].length() - 1);
		ret[1] = restData;
		return ret;
	}

	public String[] dealSeqOfOADWithDARAndData(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOADWithDARAndData");		
	}

	public String[] dealOADWithDARAndData(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealOADWithDAR(data, t);
		s = dealResultNormal(s[1], t);
		ret[1] = s[1];
		return ret;
	}

	// 解析结果在ret[0]中，余下的部分在ret[1]
	// 解析多个OAD,多个OAD使用";"分隔
	public String[] dealSeqOfOADWithDataAndRead(String data, TreeNode n) {
		return dealWithNum(data, n, "dealOADWithDataAndRead");		
	}

	public String[] dealOADWithDataAndRead(String data, TreeNode t) {
		String ret[] = { "", "" };

		String[] s = dealOADWithData(data, t);

		s = dealOAD(s[1], t);

		node = new TreeNode("延时读取时间", s[1].substring(0, 2));
		t.addChild(node);

		ret[1] = s[1].substring(2);
		return ret;
	}

	// 解析单个Result
	public String[] dealResultRecord(String data, TreeNode t) {

		String ret[] = { "", "" };

		String[] s = dealOAD(data, t);

		// RCSD
		node = new TreeNode("SEQUENCE OF CSD(RCSD)","");
		t.addChild(node);
		s = dealSeqOfCSD(s[1], node);

		int result = DataConvert.hexString2Int(s[1].substring(0, 2));
		if (result == 0) {
			s = dealDAR(s[1].substring(2), t);
		} else {
			// 此数据解析时，需要参考前面dealSeqOfCSD得到的个数
			// 6.3.8.2.3　GetResponseRecord数据类型 
			node = new TreeNode("SEQUENCE OF A-RecordRow","");
			t.addChild(node);
			s = dealSeqOfRecordRow(DataConvert.String2Int(s[0]),
					s[1].substring(2), node);
		}

		ret[1] = s[1];
		return ret;
	}

	
	// 根据前面的个数信息，在此进行多个数据项目的循环处理 
	// 6.3.8.2.3　GetResponseRecord数据类型
	public String[] dealSeqOfRecordRow(int numInOne, String data,TreeNode t1) {
		String ret[] = { "", "" };
		String s[] = { "", "" };

		int num = DataConvert.hexString2Int(data.substring(0, 2));
		TreeNode t = new TreeNode("RecordRow个数", num);
		t1.addChild(t);
		String restData = data.substring(2);
		for (int i = 0; i < num; i++) {

			TreeNode n = new TreeNode("CSD个数", numInOne);
			t.addChild(n);

			for (int j = 0; j < numInOne; j++) {
				s = dealDATA(restData, n);
				restData = s[1];
			}
		}

		ret[1] = s[1];
		return ret;
	}

	public String[] dealSeqOfRecord(String data, TreeNode n) {
		return dealWithNum(data, n, "dealRecord");		
	}

	public String getAPDUType(int choiseFlag) {
		String data = "1:预连接请求;129:预连接响应;2:建立应用连接请求;3:断开应用连接请求;"+
				"5:读取请求;6:设置请求;7:操作请求;8:上报应答;"+
				"9:代理请求;130:建立应用连接响应;131:断开应用连接响应;132:断开应用连接通知;"+
				"133:读取响应;134:设置响应;135:操作响应;136:上报通知;"+
				"137:代理响应;16:安全请求;144:安全响应";
		String f = DataConvert.int2String(choiseFlag);
		String ret = "";
		for( String s:data.split(";")){
			if (s.split(":")[0].equals(f)){
				ret = s.split(":")[1];
				break;
			}
		}
		return ret;
	}

	public String getAPDUType1(int choiseFlag,int choiseFlag1) {
		String data = "5_1:读取一个对象属性请求;5_2:读取若干个对象属性请求;5_3:读取一个记录型对象属性请求;5_4:读取若干个记录型对象属性请求;5_5:读取分帧响应的下一个数据块请求;"+
					"133_1:读取一个对象属性的响应;133_2:读取若干个对象属性的响应;133_3:读取一个记录型对象属性的响应;133_4:读取若干个记录型对象属性的响应;133_5:分帧响应一个数据块;";
		String f = DataConvert.int2String(choiseFlag)+"_"+DataConvert.int2String(choiseFlag1);
		String ret = "";
		for( String s:data.split(";")){
			if (s.split(":")[0].equals(f)){
				ret = s.split(":")[1];
				break;
			}
		}
		return ret;
	}
	
	public String getAPDUData(Object[][] data_attr,int seq, Boolean isRecord){
		String ret = "";
		String dataType="",datavalue="",dataname="";
		for(Object[] s:data_attr){
			
			if (s[0] == null)
				break;

			// seq != -1表示无须判断
			if (seq != -1)
				// 判断是否所需的seq
				if ((int)s[8] != seq) 
						continue;
			
			dataname  = (String)s[0];
			dataType = (String)s[1];
			datavalue = (String)s[2];
			
			// xuky 2016.09.02 如果是CSD中的OAD信息，数据前是没有类型标识0x51的
			// xuky 2016.09.13 此行代码不适用与 添加普通采集方案  所以屏蔽
			//if (dataname.indexOf("CSD") >= 0){
			//	isRecord = true;
			//}
			// xuky 2016.09.13 添加对dataType.indexOf("NULL")的判断，类型为NULL时。无需填写数据
			//System.out.println("getAPDUData=> dataname "+dataname);
			
			if (dataType.indexOf("NULL") <0 && (datavalue==null || datavalue.equals(""))){
				if (dataType.equals("enum") || dataType.toLowerCase().indexOf("unsigned")>=0 )
					datavalue = "0";
				else if (dataType.toLowerCase().indexOf("string")>=0)
					datavalue = "";
				else{
					String msg = "请填写并保存数据=>"+ dataname.replaceAll("－", "") ;
					DebugSwing.showMsg(msg);
					System.out.println(msg);
					return "";
					}
			}
			
			// 返回信息为datatypeid,bytenum
			String[] type = DB.getInstance().getBasicDataTypeInfo(dataType);
			
			if (type[0].equals("")){
				//采集当前数据[0] NULL，采集上第N次[1] unsigned，按冻结时标采集[2] NULL，按时标间隔采集[3] TI
				//NULL，填写NULL或0；上第N次，填写N的值，如：第1次填写1；TI用英文半角逗号隔开，15分钟如：1,15
				// NULL 1字节 00;N 2字节 0x11+1字节;TI 4字节0x54+ENU+2字节 
				if (dataType.equals("Data")){
					if (datavalue.toUpperCase().indexOf("NULL")>=0){
						ret += "00";
						ret += ",";
					}
					else{
						if (datavalue.indexOf(",")>=0){
							// TI
							ret += "54"+"0"+datavalue.split(",")[0]+DataConvert.String2HexString(datavalue.split(",")[1], 4);
							ret += ",";
						}
						else{
							// 直接就是报文内容
							datavalue = datavalue.replaceAll(" ", "");
							ret += Util698.seprateString(datavalue," ");
							ret += ",";
							/*
							int n = DataConvert.String2Int(datavalue); 
							if ( n==0){
								// NULL
								ret += "00";
								ret += ",";
							}
							else{
								ret += "11"+DataConvert.int2HexString(n, 2);
								ret += ",";
							}
							*/
						}
					}
				}
				else if (dataType.indexOf("CHOICE")>=0){
					if (dataname.indexOf("CSD")>=0){
						if (isRecord)
							ret += DataConvert.String2HexString(datavalue, 2);
						else
							ret += "5B"+DataConvert.String2HexString(datavalue, 2);
						ret += ",";
					}
					else if (dataname.indexOf("MS")>=0){
						if (isRecord)
							ret += DataConvert.String2HexString(datavalue, 2);
						else
							ret += "5C"+DataConvert.String2HexString(datavalue, 2);
						ret += ",";
					}
					else if (dataname.indexOf("RSD")>=0){
						// 比较特殊，数据中没有对应的RSD编码
						ret += DataConvert.String2HexString(datavalue, 2);
						ret += ",";
					}
					else{
						String msg = "1未找到类型定义（需告知开发人员）=>"+dataType + " dataname=>"+ dataname ;
						DebugSwing.showMsg(msg);
						System.out.println(msg);
						return "";
						//System.exit(0);
					}
				}
				else if (dataType.equals("SEQUENCE")){
					//无动于衷  无需数据
				}
				else if (dataType.equals("*NULL")){
					// xuky 2016.09.13 普通采集方案中 软件组织得到的报文为无数据 所以注释以下代码
					// 无数据
					// xuky 2016.09.14 普通采集方案中 软件组织得到的报文为无数据   如果无数据，需要调整dealMS程序
					// 与李方沟通，保持
					// NULL也是数据
					// xuky 2016.10.13 王文娟 反馈 普通采集方案中 软件组织得到的报文为无数据   调整dealMS程序
					// 无数据
					//ret += "00";
					//ret += ",";
				}
				else if (dataType.equals("NULL")){
					ret += "00";
					ret += ",";
				}
				else if (dataType.equals("SEQUENCE OF")){
					ret += DataConvert.String2HexString(datavalue, 2);
					ret += ",";
				}					
				else if (dataType.indexOf("TSA")>=0){
					// xuky 2016.08.28 测试用临时编码
					if (isRecord){
						if (!Util698.isEven(datavalue))
							datavalue = "0"+datavalue;
						int size = datavalue.length()/2; 
						ret += DataConvert.int2HexString(size, 2)+datavalue;
						ret += ",";
					}
					else{
//						if (!Util698.isEven(datavalue))
//							datavalue = datavalue + "F";
//						int size = datavalue.length()/2; 
//						ret += "55"+DataConvert.int2HexString(size+1, 2)+DataConvert.int2HexString(size-1, 2)+datavalue;
						ret += "55"+ getTSA(datavalue);
						ret += ",";
					}
				}					
				else if (dataType.toLowerCase().equals("bit-string(size(8))")){
					// xuky 2016.08.28 测试用临时编码
					ret += "0408"+DataConvert.binStr2HexString(datavalue,2);
					ret += ",";
				}
				else if (dataType.indexOf("-")>=0){
					// xuky 2016.09.13无数据，其站位作用
				}
				else if (dataType.indexOf("date_time_s")>=0){
					//2016-08-08 08:08:08
					// 1C 07 E0 08 08 08 08 08
					ret += "1C"+DataConvert.String2HexString(datavalue.substring(0, 4), 4);
					ret += DataConvert.String2HexString(datavalue.substring(5, 7), 2);
					ret += DataConvert.String2HexString(datavalue.substring(8, 10), 2);
					ret += DataConvert.String2HexString(datavalue.substring(11, 13), 2);
					ret += DataConvert.String2HexString(datavalue.substring(14, 16), 2);
					ret += DataConvert.String2HexString(datavalue.substring(17, 19), 2);
					ret += ",";
				}
				else if (dataType.indexOf("COMDCB")>=0){
					//3,2,8,1,0
					String[] array = datavalue.split(",");
					ret += "5F ";
					for(String str: array){
						ret += DataConvert.String2HexString(str, 2) +" ";
					}
					ret += ",";
				}
				else{
					// 未找到类型定义 ，直接退出
					String msg = "2未找到类型定义（需告知开发人员）=>"+dataType;
					DebugSwing.showMsg(msg);
					System.out.println(msg);
				}
			}
			else{
				int bytenum = -1;
				if (type[1]!=null)
					bytenum = DataConvert.String2Int(type[1]);
				if (bytenum ==-1){
					//bytenum为空时，需要自行解析数据						
					if ("array,structure".indexOf(dataType)>=0){
						// 数据类型+数据内容
						if (isRecord && dataType.equals("structure")) {
							// 此为虚拟节点，不出现内容
						}
						else{
							ret += DataConvert.String2HexString(type[0], 2);
							ret += DataConvert.String2HexString(datavalue, 2);
							ret += ",";
						}
					}
					else if (dataType.equals("octet-string")){
						int dataLen = 0;
						if (datavalue.indexOf("\u002E") >0){
							// 129.0.0.1
							dataLen = 4;
							String[] str = datavalue.split("\\.");
							datavalue = "";
							for(String t:str)
								datavalue += DataConvert.String2HexString(t, 2); 
						}
						else{
							if (!Util698.isEven(datavalue))
								datavalue = "0" + datavalue;
							dataLen = datavalue.length() /2 ; 
						}
						ret += "09" + DataConvert.int2HexString(dataLen, 2)+datavalue;
						ret += ",";
					}
					else if (dataType.indexOf("visible-string")>=0){
						int dataLen = datavalue.length();
						datavalue = DataConvert.string2ASCIIHexString(datavalue, dataLen);
						ret += "0A" + DataConvert.int2HexString(dataLen, 2)+datavalue;
						ret += ",";
					}
					else if (dataType.indexOf("TI")>=0){
						
						if (!isRecord)
							ret += "54";
						
						if ((datavalue.indexOf(",")) >=0){
							ret += "0"+datavalue.split(",")[0];
							ret += DataConvert.String2HexString(datavalue.split(",")[1],4);
						}
						else
							ret += datavalue;
						
						ret += ",";
					}
					else if (dataType.indexOf("COMDCB")>=0){
						//3,2,8,1,0
						String[] array = datavalue.split(",");
						ret += "5F ";
						for(String str: array){
							ret += DataConvert.String2HexString(str, 2) +" ";
						}
						ret += ",";
					}
					else if (dataType.indexOf("TSA")>=0){
						//55 07 05 00 00 00 00 00 01
						ret += "55 ";
						if (!Util698.isEven(datavalue))
							datavalue = "0"+datavalue;
						int size = datavalue.length()/2+1; 
						ret += DataConvert.int2HexString(size, 2)+DataConvert.int2HexString(size-1, 2)+datavalue;
						ret += ",";
						
						ret += ",";
					}
					else{
						String msg = "需要自行定义解析函数（需告知开发人员）=>"+dataType;
						DebugSwing.showMsg(msg);
						System.out.println(msg);
					}
				}
				else{
					if (isRecord){
						if (dataType.toLowerCase().indexOf("bcd")>=0 || dataType.indexOf("OAD")>=0 ){
							if (dataType.equals("datetimeBCD")){
								if ((datavalue.indexOf(":")) >=0){
									// yyyy-mm-dd hh:mm:ss 格式转换
									String temp = DataConvert.String2HexString(datavalue.substring(0, 4),4);
									temp += DataConvert.String2HexString(datavalue.substring(5, 7),2);
									temp += DataConvert.String2HexString(datavalue.substring(8, 10),2);
									temp += DataConvert.String2HexString(datavalue.substring(11, 13),2);
									temp += DataConvert.String2HexString(datavalue.substring(14, 16),2);
									temp += DataConvert.String2HexString(datavalue.substring(17, 19),2);
									ret += temp;
									ret += ",";
								}
								else{
									// 可以是直接自行编辑好的数据
									ret += datavalue;
									ret += ",";
								}
							}
							else{
								ret += datavalue;
								ret += ",";
							}
						}
						else{
							ret += DataConvert.String2HexString(datavalue,bytenum*2 );
							ret += ",";
						}
					}
					else{
						
						// 数据前需要添加数据标识
						
						// 2016.09.13 设置为对*OAD的判断，无51数据标识
						if (dataType.indexOf("*OAD")<0)
							ret += DataConvert.String2HexString(type[0], 2);
						
						if (dataType.toLowerCase().indexOf("bcd")>=0 || dataType.indexOf("OAD")>=0){
							ret += datavalue;
							ret += ",";
						}
						else{
							ret += DataConvert.String2HexString(datavalue,bytenum*2 );
							ret += ",";
						}
					}
				}
			}
			//System.out.println("ret=>"+ret);
			
		}
		// 去掉最后的一个,
		if (ret.length() > 0)
			if (ret.substring(ret.length()-1, ret.length()).equals(","))
				ret = ret.substring(0, ret.length()-1);
		
		return ret;
	}

	public String[] dealProxyRead(String data, TreeNode t) {
		String ret[] = { "", "" };
//		一个目标服务器地址         TSA，
//		代理一个服务器的超时时间   long-unsigned，
//		若干个对象属性描述符       SEQUENCE OF OAD
		String[] s = dealTSA(data, t);
		node = new TreeNode("代理一个服务器的超时时间", DataConvert.hexString2Int(s[1].substring(0,4)));
		t.addChild(node);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOAD(s[1].substring(4), node);
		ret[1] = s[1];
		return ret;
	}
	
	public String getTSA(String datavalue){
		String ret = "";
		if (!Util698.isEven(datavalue))
			datavalue = datavalue + "F";
		int size = datavalue.length()/2; 
		ret = DataConvert.int2HexString(size+1, 2)+DataConvert.int2HexString(size-1, 2)+datavalue;
		return ret;
	}
	
	public String[] dealProxyReadResp(String data, TreeNode t) {
		String ret[] = { "", "" };
//		一个目标服务器地址         TSA，
//		若干个对象属性描述符及其结果  SEQUENCE OF (OAD+Get-Result)
		
		String[] s = dealTSA(data, t);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点\
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfResultNormal(s[1], node);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxyRequest(String data, TreeNode t) {
		String ret[] = { "", "" };
//		一个目标服务器地址         TSA，
//		代理一个服务器的超时时间   long-unsigned，
//		若干个对象属性描述符       SEQUENCE OF OAD Data
		String[] s = dealTSA(data, t);
		node = new TreeNode("代理一个服务器的超时时间", DataConvert.hexString2Int(s[1].substring(0,4)));
		t.addChild(node);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOADWithData(s[1].substring(4), node);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxySetAndRead(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealTSA(data, t);
		node = new TreeNode("代理一个服务器的超时时间", DataConvert.hexString2Int(s[1].substring(0,4)));
		t.addChild(node);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOADWithDataAndRead(s[1].substring(4), node);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxyOMD(String data, TreeNode t) {
		String ret[] = { "", "" };
		//一个目标服务器地址		
		String[] s = dealTSA(data, t);
		
		node = new TreeNode("代理一个服务器的超时时间", DataConvert.hexString2Int(s[1].substring(0,4)));
		t.addChild(node);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOMDWithData(s[1].substring(4), node);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxyOMDAndRead(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealTSA(data, t);
		node = new TreeNode("代理一个服务器的超时时间", DataConvert.hexString2Int(s[1].substring(0,4)));
		t.addChild(node);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOMDWithDataAndRead(s[1].substring(4), node);
		ret[1] = s[1];
		return ret;
	}
	
	
	public String[] dealProxySetResp(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealTSA(data, t);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOADWithDAR(s[1], t);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxySetAndReadResp(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealTSA(data, t);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOADWithDARAndData(s[1], t);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxyOMDResp(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealTSA(data, t);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOMDWithDARAndData(s[1], t);
		ret[1] = s[1];
		return ret;
	}
	
	public String[] dealProxyOMDAndReadResp(String data, TreeNode t) {
		String ret[] = { "", "" };
		String[] s = dealTSA(data, t);
		
		// xuky 2016.11.08 如果后面紧跟着一个dealSeqOfXXX，必须在这里首先添加一个根节点
		node = new TreeNode("SEQUENCE OF ","");
		t.addChild(node);
		
		s = dealSeqOfOMDWithDARAndDataAndRead(data, t);
		ret[1] = s[1];
		return ret;
	}
	
	
	
	public String[] dealSeqOfProxyRead(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyRead");		
	}
	
	public String[] dealSeqOfProxyReadResp(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyReadResp");		
	}
	
	public String[] dealSeqOfProxyRequest(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyRequest");		
	}
	
	public String[] dealSeqOfProxySetAndRead(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxySetAndRead");		
	}
	public String[] dealSeqOfProxyOMD(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyOMD");		
	}
	
	public String[] dealSeqOfProxyOMDAndRead(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyOMDAndRead");		
	}
	
	public String[] dealSeqOfProxySetResp(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxySetResp");		
	}
	
	public String[] dealSeqOfProxySetAndReadResp(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxySetAndReadResp");		
	}
	
	public String[] dealSeqOfProxyOMDResp(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyOMDResp");		
	}
	
	public String[] dealSeqOfProxyOMDAndReadResp(String data, TreeNode n) {
		return dealWithNum(data, n, "dealProxyOMDAndReadResp");		
	}
			
		
	public void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
