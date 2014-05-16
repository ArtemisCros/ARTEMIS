
package sockets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import utils.Converter;
import logger.GlobalLogger;

/* Server socket for ethernet communication */

public class ServSocket extends Thread{
	public ServerSocket socketserver  ;
	public Socket socketduserveur ;
	
	public ServSocket() {

	}
 
	/*
	 * Launch a server socket, listening to a designed port and decrypting ethernet 802.3 frames 
	 */
	public int listen() {
		try {
			socketserver = new ServerSocket(4242);
			GlobalLogger.debug("SRV:Le serveur est ˆ l'Žcoute du port "+socketserver.getLocalPort());
			
			boolean listening = true;
			while(listening) {
				socketduserveur = socketserver.accept(); 
				GlobalLogger.debug("SRV:Connection attempt");
				
				/* Reading bytes from client */
				InputStream inputStream = socketduserveur.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
				
				/* We initialize an array to read input byte per byte */
				byte[] content = new byte[1];  	
				
				/* Maximum full-length message */
				byte[] globalMessageUnlength = new byte[2048];
				
				/* Data-part size */
				byte[] size = new byte[4];
				
				int bytesRead = -1;
				
				/* We count the number of bytes, and locate data size on the frame */
				int bytesNum = 0;
				int sizeInt = 2048;
				
				while( ( bytesRead = inputStream.read( content ) ) != -1 ) { 
				    baos.write( content, 0, bytesRead ); 
				    
				    System.arraycopy(content, 0, globalMessageUnlength, bytesNum, 1);
				    
				    if(bytesNum == 25) {
				    	size[2] = content[0];
				    }
				    if(bytesNum == 26) {
				    	size[3] = content[0];
				    	sizeInt = ByteBuffer.wrap(size).getInt();
				    }
				   if(bytesNum > sizeInt+32)
				    	break;
				   bytesNum++;
				}
				
				/* We initialize the final array, containing the whole frame */
				byte[] finalMsg = new byte[bytesNum];
				System.arraycopy(globalMessageUnlength, 0, finalMsg, 0, bytesNum);
				GlobalLogger.debug("Received:");
				Converter.printHexArray(finalMsg);  
			}
			    
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	public void closeServer() {
		GlobalLogger.debug("Closing socket");
        try {
			socketduserveur.close();
	        socketserver.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.listen();
	}
}
