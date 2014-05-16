package sockets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import logger.GlobalLogger;

public class ClientSocket{
	public ClientSocket() {
		
	}
	
	public int send(byte[] frame) {
		Socket socket;
		BufferedReader in;

		try {	
			GlobalLogger.debug("CLI:Creation d'une socket");
			socket = new Socket(InetAddress.getLocalHost(), 4242);	
			GlobalLogger.debug("CLI:Demande de connexion");

	       /* in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
	        String message_distant = in.readLine();
	        GlobalLogger.debug("CLI:"+message_distant);*/
			int len = frame.length;
			 OutputStream out = socket.getOutputStream();
			 DataOutputStream dos = new DataOutputStream(out);

			    dos.writeInt(20);
			    if (len > 0) {
			        dos.write(frame, 0, len);
			    }
	        
	        socket.close();       
		}catch (UnknownHostException e) {
			
			e.printStackTrace();
		}catch (IOException e) {	
			e.printStackTrace();
		}
		
		return 0;
	}

}
