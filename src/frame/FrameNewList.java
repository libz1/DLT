package frame;

import com.eastsoft.util.DataConvert;
import base.CommonObjectList;
import entity.Constant;
import entity.Meter;

public class FrameNewList {

	// private List<FrameGD_894E> frameGD_894EList = new
	// ArrayList<FrameGD_894E>();

	private String[] frameArr;
	private String[] delFrameArr;
	private String[] addFrameArr;
	private int frameArrIndex = 0;
	private int delFrameArrIndex = 0;
	private int addFrameArrIndex = 0;

	/**
	 * 
	 * @Title: FrameNewList
	 * @Description: FrameNewList类的构造函数
	 * @param meterList
	 *            电表对象
	 * @return 无
	 * 
	 */
	public FrameNewList(CommonObjectList meterList) {
//		System.out.println("FrameNewList=>"+DateTimeFun.getDateTimeSSS() +" 1");

		// 根据MeterList得到这个对象
		frameArr = new String[500];
		delFrameArr = new String[500];
		addFrameArr = new String[500];

		// 数据按照测量点进行排序
		//Collections.sort(meterList.getMeterList());

		if (meterList.size() == 0) {
			frameArrIndex = 0;
		} else {
			// 将所有删除性质的数据组织为报文数组
			getframeArr(Constant.FLAG_DEL, meterList);
//			System.out.println("FrameNewList=>"+DateTimeFun.getDateTimeSSS() +" 2");

			// 将所有增加性质的数据组织为报文数组
			getframeArr(Constant.FLAG_INS, meterList);
//			System.out.println("FrameNewList=>"+DateTimeFun.getDateTimeSSS() +" 3");

			// 将删除报文数组和增加报文数组 合并为同一个数组
			frameArrIndex = 0;
			for (int i = 0; i < delFrameArrIndex; i++) {
				frameArr[frameArrIndex] = delFrameArr[i];
				frameArrIndex++;
			}
			for (int i = 0; i < addFrameArrIndex; i++) {
				frameArr[frameArrIndex] = addFrameArr[i];
				frameArrIndex++;
			}
			
//			for (String str: frameArr) {
//				if (str!=null)
//					System.out.println("FrameNewList=>"+str);
//			}
		}
	}

	/**
	 * 
	 * @Title: getframeArr
	 * @Description: 将相同属性的数据放置到数组中（根据电表操作类型：新增、删除、修改）
	 *               对类的属性对象进行数据维护：delFrameArr、
	 *               addFrameArr、delFrameArrIndex、addFrameArrIndex
	 * @param aType
	 *            电表操作类型
	 * @param meterList
	 *            电表对象集合 *
	 * 
	 */
	private void getframeArr(String aType, CommonObjectList meterList) {
		//
		String[] frameArray = new String[500];
		int frameIndex = 0, aNum = 0, maxNum = 0;
		String frame = "";
		
		// 删除报文，一帧可以容纳电表数量  7Byte
		if (aType.equals(Constant.FLAG_DEL))
			maxNum = 2;
		// 增加报文，一帧可以容纳电表数量  75Byte * N + 4 + 4  
		if (aType.equals(Constant.FLAG_INS))
			maxNum = 5;
		
		// xuky 2016.11.07 为了提高运行效率
		FrameNew frame698 = new FrameNew();
		
		for (Object object : meterList.getList()) {
			Meter meter = (Meter)object;
			if (meter.getFlag().equals(aType)) {
				
				// 处理过程1：入参为电表对象
//				FrameNew frame698 = new FrameNew(meter);//单个电表的报文对象
				
				// xuky 2016.11.07 为了提高运行效率
				frame698.setMeterData(meter);
				
				// 删除报文与增加报文的长度不一致，所以可以容纳的电表数量不同
				aNum++;
				if (aNum < maxNum) { //小于10电表
					frame = frame + frame698.getFrameNew() +", "; //获取的报文数据域内容,不带电表数量
				}
				if (aNum == maxNum) {
					// 处理过程2：得到对应的协议报文
					frame = frame + frame698.getFrameNew()+", ";
					fillAndAddFrame(aType, frameArray,frame, aNum, frameIndex);
					frameIndex++;
					aNum = 0;
					frame = "";
				}
			}
		}
		
		if (aNum != 0) {
			fillAndAddFrame(aType, frameArray,frame, aNum, frameIndex);
			frameIndex++;
		}

		if (aType.equals(Constant.FLAG_DEL)) {
			delFrameArr = frameArray;
			delFrameArrIndex = frameIndex;
		}

		if (aType.equals(Constant.FLAG_INS)) {
			addFrameArr = frameArray;
			addFrameArrIndex = frameIndex;
		}
	}
	
	private void fillAndAddFrame(String aType,String[] frameArray,
			String data, int aNum, int aIndex) {
		String num = DataConvert.int2HexString(Integer.valueOf(aNum), 2);
		num =  DataConvert.reverseString(num);
		if (aType.equals(Constant.FLAG_INS)){
			data = "070100 " + "60008000 " + "01" + num + " "+ data +" 00";
			// 07 01 00 ,60 00 80 00 ,0103,0204,120001,020A,550705100000002221,1603,1602,51F2090201,0906000000000000,1104,1101,1601,1200DC,12000A,0204,550705000000000000,0906000000000000,120001,120001,0100,0204,120001,020A,550705100000002221,1603,1602,51F2090201,0906000000000000,1104,1101,1601,1200DC,12000A,0204,550705000000000000,0906000000000000,120001,120001,0100,0204,120001,020A,550705100000002221,1603,1602,51F2090201,0906000000000000,1104,1101,1601,1200DC,12000A,0204,550705000000000000,0906000000000000,120001,120001,0100,00
		}
		if (aType.equals(Constant.FLAG_DEL)){
			// 单个07 01 00 ,60 00 83 00 ,120001,00
			// 多个07 02 00 , xx,  60 00 83 00 ,120001,  ,60 00 83 00 ,120001 00
			data = "070200 " +num+" "+ data +" 00";
		}
		frameArray[aIndex] = data;
	}

	public int getFrameNum() {
		return frameArrIndex;
	}

	public String getFrame(int index) {
		// 要给定一个组号   如果没有合适的数据了。返回数据为空
		if (frameArrIndex == 0)
			return "";
		if (index > frameArrIndex - 1)
			return "";
		return frameArr[index];

	}

	public int getSize() {
		return frameArrIndex;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
