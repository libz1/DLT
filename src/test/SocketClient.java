package test;

import java.io.BufferedReader;  
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.net.Socket;  


public class SocketClient {  
    public static void main(String[] args) throws Exception {
    	
    	// 连接端口为10000
        //Socket socket = new Socket("localhost", 10000);
    	Socket socket = new Socket("127.0.0.1", 10000);
        
        InputStream in = socket.getInputStream();  
        OutputStream out = socket.getOutputStream();  
        
        // 从控制台读取数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
  
        while (true) {  
            
        	String msg = reader.readLine();
            
            sendData(out, msg);
            
            if (msg.equals("bye")) {  
                break;  
            }
            
            msg = readData(in);
            
            System.out.println(msg);  
        }  
        socket.close();  
    }  
    
    
	public static String readData(InputStream is) {
    	// 按照byte流的模式读取数据
		byte[] receiveByte = new byte[4096];
		int messageLength = 0;
		try {
			messageLength = is.read(receiveByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] currReceiveByte = null;
		currReceiveByte = new byte[messageLength];
		for (int i = 0; i < messageLength; i++) {
			currReceiveByte[i] = receiveByte[i];
		}

		String recvData ="";
		if (currReceiveByte != null) {
			// byte转为字符串
			recvData = bytes2HexString(currReceiveByte);
		}
        String msg = recvData;
        return msg;
	}
    
    
	public static void sendData(OutputStream os,String sData) {
		// 发送数据
		try {
			byte[] byteData = new byte[sData.length() / 2];
			// 将16进制字符串转为Byte数组
			byteData = hexString2ByteArray(sData);
			os.write(byteData);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex;
		}
		ret = ret.toUpperCase();
		return ret;
	}
	public static byte[] hexString2ByteArray(String param) {
		param = param.replaceAll(" ", "");
		byte[] result = new byte[param.length() / 2];
		for (int i = 0, j = 0; j < param.length(); i++) {
			result[i] = (byte) Integer.parseInt(param.substring(j, j + 2), 16);
			j += 2;
		}
		return result;
	}
}  
