/***
 * Client
 * Example of a UDP client 
 * Date: 10/01/04
 * Authors:
 */
package udp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * <b>Client représente un client se connectant à un système de chat utilisant le protocole UDP.</b>
 * 
 * <p>Chaque client sera connecté au système de chat via UDP multicast. 
 * 			Chaque client se verra affecter 2 threads : l'un pour la réception des messages (la classe statique ClientListenThreadUdp), 
 * 																											l'autre pour l'envoi (le main)</p>
 * 
 * @see Client#main
 * @see ClientListenThreadUdp
 * 
 * @author alexis
 * @version 2.0
 */

public class Client {
 
  /**
  *  <p>Le main de la classe. C'est lui qui se charge de créer une bonne connexion au multicast 
  *  			ainsi que du bon envoi des messages vers les autres participants du chat.</p>
  *  
  *  <p>Il instancie aussi la classe ClientListenThreadUdp qui s'occupe quant à elle de l'écoute des messages du chat.</p>
  *  
  *  @param args
  *  		Adresse IP du serveur ainsi que du numéro de port.
  *  
  *  @throws IOException   Si jamais il y a un problème survenu chez le client
  *  
  *  @see ClientListenThreadUdp
  **/
    public static void main(String[] args) throws IOException {

        MulticastSocket socket = null;
        BufferedReader stdIn = null;
        InetAddress groupAddr = null;
        int groupPort = 8888;
        
        if (args.length != 2) {
          System.out.println("Usage: java Client <Server host> <Server port>");
          System.exit(1);
        }
        groupAddr = InetAddress.getByName(args[0]);
    	groupPort = new Integer(args[1]).intValue();
    	//groupAddr = InetAddress.getByName("225.0.0.20");
    	
    	Scanner scanner = new Scanner( System.in );
        System.out.print( "Veuillez saisir votre pseudo : " );
        String clientName = scanner.nextLine();
    	
        try {
      	    // creation socket ==> connexion
      	    socket = new MulticastSocket(groupPort);
      	    socket.joinGroup(groupAddr);
		    stdIn = new BufferedReader(new InputStreamReader(System.in));
		    ClientListenThreadUdp clt = new ClientListenThreadUdp(groupAddr.toString()+":"+String.valueOf(groupPort),socket);
	        clt.start();
	        
	        String connexionText = clientName + " joins the chat";
    		DatagramPacket msg = new DatagramPacket(connexionText.getBytes(),connexionText.length(),groupAddr,groupPort);
    		socket.send(msg);
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
        	if (line.equals(".")) {
        		String deconnexionText = clientName + " leaves the chat";
        		DatagramPacket msg = new DatagramPacket(deconnexionText.getBytes(),deconnexionText.length(),groupAddr,groupPort);
        		socket.send(msg);
        		socket.leaveGroup(groupAddr);
        		break;
        	}
        	String stringMessage = clientName + " : " + line;
        	DatagramPacket msg = new DatagramPacket(stringMessage.getBytes(),stringMessage.length(),groupAddr,groupPort);
        	socket.send(msg);
        	
        }
      stdIn.close();
    }
    
    /**
     * <p>C'est la classe qui s'occupe de l'écoute d'éventuels messages provenant du chat multicast. 
     * 			A la réception d'un message, elle l'affichera sur la console ou l'interface graphique utilisée par le client.</p>
     * 
     * <p>Elle est instanciée par le main de Client, qui lui s'occupe des envois de messages sur le chat</p>
     * 
     * @see Client#main
     */
    
    public static class ClientListenThreadUdp extends Thread {
    	private MulticastSocket socket;
    	ClientListenThreadUdp(String name,MulticastSocket s) {
    		super(name);
    		this.socket = s;
    	}

    	@Override
    	public void run() {
    		try {
	    		while (true) {
	    			byte[] buf = new byte[1000];
	    			DatagramPacket recv = new DatagramPacket(buf,buf.length);
	    			socket.receive(recv);
	    			String msgrcv = new String(recv.getData(),0,recv.getLength());
	    			System.out.println(msgrcv);
	    			
	    		}
	    	} catch (Exception e) {
	    		System.err.println("Error in UDPClientReception:" + e); 
	    	}
    	}
    }

}