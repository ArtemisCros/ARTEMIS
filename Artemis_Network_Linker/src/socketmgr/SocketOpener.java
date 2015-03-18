package socketmgr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import logger.GlobalLogger;

public class SocketOpener {
	private Socket client;
	
	public void connect()
	   {
	      String serverName = "127.0.0.1";
	      int port = 4242;
	      try
	      {
	         System.out.println("Connecting to " + serverName
	                             + " on port " + port);
	         client = new Socket(serverName, port);
	         System.out.println("Just connected to "
	                      + client.getRemoteSocketAddress());
	        
	        /* DataInputStream in = new DataInputStream(client.getInputStream());
	         
	         String data = "test";
	         System.out.println("Sending "+data);
	         out.writeUTF("Hello from test" + client.getLocalSocketAddress());
	         
	         System.out.println("Server says " + in.readUTF());*/
	        // client.close();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	   }
	
	public void send(byte[] data) {
		 try {
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			
			out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
