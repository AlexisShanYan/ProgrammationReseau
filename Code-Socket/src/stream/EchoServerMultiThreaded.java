/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class EchoServerMultiThreaded  {
  
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
   public static void main(String args[]){
	    ServerSocket listenSocket;
	        
	  	if (args.length != 1) {
	          System.out.println("Usage: java EchoServer <EchoServer port>");
	          System.exit(1);
	  	}
		try {
			listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
			System.out.println("Server ready...");
			int numberSocket = 0;
			File myObj = new File("filename.txt");
		    if (myObj.createNewFile()) {}
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from:" + clientSocket.getInetAddress());
				ClientThread ct = new ClientThread(clientSocket, numberSocket++, "Client " + numberSocket);
				ct.start();
			}
	    } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
            e.printStackTrace();
	    }
   }
}

  