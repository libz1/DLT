package socket;

import javax.comm.SerialPort;

/**
 * 串口通信参数类 
 * @author xuky
 * @version 2013.11.20
 */
public class SerialParam {

	String COMM;
	int baudRate;
	int dataBit;
	int stopBit;
	int parity;
	int receiveTimeout;

	public SerialParam() {
		baudRate = 9600;
		dataBit = SerialPort.DATABITS_8;
		stopBit = SerialPort.STOPBITS_1;
		parity = SerialPort.PARITY_EVEN;
		receiveTimeout = 30;
	}

	public String getCOMM() {
		return COMM;
	}

	public void setCOMM(String comm) {
		COMM = comm;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getDataBit() {
		return dataBit;
	}

	public void setDataBit(int dataBit) {
		this.dataBit = dataBit;
	}

	public int getStopBit() {
		return stopBit;
	}

	public void setStopBit(int stopBit) {
		this.stopBit = stopBit;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public int getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	public static void main(String[] args) {

	}

}
