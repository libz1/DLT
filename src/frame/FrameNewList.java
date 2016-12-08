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
	 * @Description: FrameNewList��Ĺ��캯��
	 * @param meterList
	 *            ������
	 * @return ��
	 * 
	 */
	public FrameNewList(CommonObjectList meterList) {
//		System.out.println("FrameNewList=>"+DateTimeFun.getDateTimeSSS() +" 1");

		// ����MeterList�õ��������
		frameArr = new String[500];
		delFrameArr = new String[500];
		addFrameArr = new String[500];

		// ���ݰ��ղ������������
		//Collections.sort(meterList.getMeterList());

		if (meterList.size() == 0) {
			frameArrIndex = 0;
		} else {
			// ������ɾ�����ʵ�������֯Ϊ��������
			getframeArr(Constant.FLAG_DEL, meterList);
//			System.out.println("FrameNewList=>"+DateTimeFun.getDateTimeSSS() +" 2");

			// �������������ʵ�������֯Ϊ��������
			getframeArr(Constant.FLAG_INS, meterList);
//			System.out.println("FrameNewList=>"+DateTimeFun.getDateTimeSSS() +" 3");

			// ��ɾ��������������ӱ������� �ϲ�Ϊͬһ������
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
	 * @Description: ����ͬ���Ե����ݷ��õ������У����ݵ��������ͣ�������ɾ�����޸ģ�
	 *               ��������Զ����������ά����delFrameArr��
	 *               addFrameArr��delFrameArrIndex��addFrameArrIndex
	 * @param aType
	 *            ����������
	 * @param meterList
	 *            �����󼯺� *
	 * 
	 */
	private void getframeArr(String aType, CommonObjectList meterList) {
		//
		String[] frameArray = new String[500];
		int frameIndex = 0, aNum = 0, maxNum = 0;
		String frame = "";
		
		// ɾ�����ģ�һ֡�������ɵ������  7Byte
		if (aType.equals(Constant.FLAG_DEL))
			maxNum = 2;
		// ���ӱ��ģ�һ֡�������ɵ������  75Byte * N + 4 + 4  
		if (aType.equals(Constant.FLAG_INS))
			maxNum = 5;
		
		// xuky 2016.11.07 Ϊ���������Ч��
		FrameNew frame698 = new FrameNew();
		
		for (Object object : meterList.getList()) {
			Meter meter = (Meter)object;
			if (meter.getFlag().equals(aType)) {
				
				// �������1�����Ϊ������
//				FrameNew frame698 = new FrameNew(meter);//�������ı��Ķ���
				
				// xuky 2016.11.07 Ϊ���������Ч��
				frame698.setMeterData(meter);
				
				// ɾ�����������ӱ��ĵĳ��Ȳ�һ�£����Կ������ɵĵ��������ͬ
				aNum++;
				if (aNum < maxNum) { //С��10���
					frame = frame + frame698.getFrameNew() +", "; //��ȡ�ı�������������,�����������
				}
				if (aNum == maxNum) {
					// �������2���õ���Ӧ��Э�鱨��
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
			// ����07 01 00 ,60 00 83 00 ,120001,00
			// ���07 02 00 , xx,  60 00 83 00 ,120001,  ,60 00 83 00 ,120001 00
			data = "070200 " +num+" "+ data +" 00";
		}
		frameArray[aIndex] = data;
	}

	public int getFrameNum() {
		return frameArrIndex;
	}

	public String getFrame(int index) {
		// Ҫ����һ�����   ���û�к��ʵ������ˡ���������Ϊ��
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
