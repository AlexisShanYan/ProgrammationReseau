/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

public class ClientThread
	extends Thread {
	
	public static LinkedList<Socket> list=new LinkedList<Socket>();
	public static int totalSocket = 0;
	
	private Socket clientSocket;
	private String clientName;
	
	ClientThread(Socket s, int numberSocket, String clientName) {
		this.clientSocket = s;
		list.add(clientSocket);
		totalSocket += 1;
		this.clientName = clientName;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
       		
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));
    		
    		for (int i=0; i<totalSocket; i++) {
  			  PrintStream socOut = new PrintStream(list.get(i).getOutputStream());
  			  socOut.println(clientName + " est entré dans le chat !");
  		  	}
	       
    	   Files.write(Paths.get("filename.txt"), (clientName + " est entré dans le chat !" + "\n").getBytes(), StandardOpenOption.APPEND);
    		
    		while (true) {
    		  String line = socIn.readLine();
    		  if (line.equals(".")) {
    			  list.remove(clientSocket);
				  totalSocket -= 1;
    			  
    			  for (int i=0; i<totalSocket; i++) {
    	  			  PrintStream socOut = new PrintStream(list.get(i).getOutputStream());
    	  			  socOut.println(clientName + " vient de se déconnecter !");
	  		  	  }
    			  Files.write(Paths.get("filename.txt"), (clientName + " vient de se déconnecter !" + "\n").getBytes(), StandardOpenOption.APPEND);
    			  break;
    		  }
    		  
    		  for (int i=0; i<totalSocket; i++) {
      			  PrintStream socOut = new PrintStream(list.get(i).getOutputStream());
    			  socOut.println(clientName + " : " + line);
    		  }
		      Files.write(Paths.get("filename.txt"), (clientName + " : " + line + "\n").getBytes(), StandardOpenOption.APPEND);
    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
     }
  
  }

  