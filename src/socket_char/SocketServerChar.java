package socket_char;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.util.HashMap;
import java.util.Map;
  
//socket ����ˣ� ����char���ͽ��������շ�����
//�ο� http://blog.csdn.net/kongxx/article/details/7259465
public class SocketServerChar {

	// ���Ӷ��б�
	static Map CLIENT1 = new HashMap();
	
    public static void main(String[] args) throws IOException {
    	
    	// �����˿�Ϊ10000
        ServerSocket server = new ServerSocket(10000);  
        while (true) {  
            Socket socket = server.accept();  
            invoke(socket);  
        } 
    }  
    
    private static void invoke(final Socket client) throws IOException {  
        new Thread(new Runnable() {  
            public void run() {
            	// �����Ӷ��б��������
            	CLIENT1.put(client.getRemoteSocketAddress().toString(),client);
                BufferedReader in = null;  
                PrintWriter out = null;
                try {
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));  
                    out = new PrintWriter(client.getOutputStream());  
  
                    while (true) {
                        String msg = in.readLine();
                        
                        String str = client.getRemoteSocketAddress().toString();
                        System.out.println("2:"+((Socket)CLIENT1.get(str)).getRemoteSocketAddress().toString());  
                        
                        System.out.println(msg);  
                        out.println("Server received " + msg+"^^"+client.getRemoteSocketAddress());  
                        out.flush();  
                        if (msg.equals("bye")) {  
                            break;  
                        }
                    }
                } catch(IOException ex) {  
                    ex.printStackTrace();  
                } finally {  
                    try {  
                        in.close();  
                    } catch (Exception e) {}  
                    try {  
                        out.close();  
                    } catch (Exception e) {}  
                    try {  
                        client.close();  
                    } catch (Exception e) {}  
                }  
            }  
        }).start();  
    }  
}  
