/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;
import java.util.Scanner;



public class EchoClient {

 
  /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
        	File myObj = new File("filename.txt");
            Scanner myReader = new Scanner(myObj);
            System.out.println("***");
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              System.out.println(data);
            }
            myReader.close();
            System.out.println("***");
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
		    socOut= new PrintStream(echoSocket.getOutputStream());
		    stdIn = new BufferedReader(new InputStreamReader(System.in));
		    ClientListenThread clt = new ClientListenThread("client",echoSocket);
	        clt.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
        
        String line;
        while (true) {
        	line=stdIn.readLine();
        	if (!line.equals("")) {
        		socOut.println(line);
        	}
        	if (line.equals(".")) {
        		socOut.println(echoSocket.getInetAddress() + "leaves the chat.");
        		break;
        	}
        }
      socOut.close();
      stdIn.close();
      echoSocket.close();
    }
    
    public static class ClientListenThread extends Thread {
    	private Socket clientSocket;
    	ClientListenThread(String name,Socket s) {
    		super(name);
    		this.clientSocket = s;
    	}

    	@Override
    	public void run() {
    		try {
    			BufferedReader socIn = null;
	    		socIn = new BufferedReader(
	  		          new InputStreamReader(clientSocket.getInputStream()));    
	    		while (true) {
	    			System.out.println(socIn.readLine());
	    		}
	    	} catch (Exception e) {
	    		System.err.println("Error in EchoClientReception:" + e); 
	    	}
    	}
    }

}


