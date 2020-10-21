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

/**
 * 
 * <b>EchoClient est la classe qui représente un client de l'application.</b>
 * 
 * <p>
 * Un Client peut effectuer deux actions simultanément : 
 * <ul>
 * <li>Ecrire un message et l'envoyer au serveur pour qu'il le transmette au chat.</li>
 * <li>Recevoir les messages du serveur pour afficher ces derniers sur le terminal ou l'interface graphique du client.</li>
 * </ul>
 * 
 * 
 * @author alexis
 * @version 2.0
 */

public class EchoClient {

 
  /**
  *  Méthode main
  *  <p>Accepte une connexion, initialise un thread d'écoute des messages du chat, et se charge de l'envoi des messages des clients vers le chat.</p>
  *  
  *  @param args
  *  		adresseIP du serveur ainsi que son port
  *  
  *  @throws IOException   Si jamais il y a un problème survenu chez le client
  *  
  **/
	
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }
        
        Scanner scanner = new Scanner( System.in );
        System.out.print( "Veuillez saisir votre pseudo : " );
        String clientName = scanner.nextLine();

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
        
        socOut.println(clientName + " joins the chat !");
        
        String line;
        while (true) {
        	line=stdIn.readLine();
        	if (line.equals(".")) {
        		socOut.println(clientName + " leaves the chat.");
        		System.out.println("Successfully disconnected !");
        		break;
        	}
        	else if (!line.equals("") || !line.equals(".")) {
        		socOut.println(clientName + " : " + line);
        	}
        }
      socOut.close();
      stdIn.close();
      echoSocket.close();
    }
    
    /**
     * 
     * Classe statique
     * <p>Cette classe se charge d'écouter les messages du chat afin de les afficher au client.</p>
     *
     */
    
    public static class ClientListenThread extends Thread {
    	
    	/**
    	 * La socket du client qui fait le pont entre sa machine et le serveur.
    	 */
    	
    	private Socket clientSocket;
    	
    	/**
    	 * Constructeur ClientListenthread
    	 * 
    	 * <p>Nous avons choisi de nous servir de l'hérédité afin d'implémenter cette classe. On hérite de la classe Thread.</p>
    	 * 
    	 * @param name
    	 * 			Le nom du thread créé
    	 * @param s
    	 * 			La socket du client
    	 */
    	
    	ClientListenThread(String name,Socket s) {
    		super(name);
    		this.clientSocket = s;
    	}

    	/**
    	 * Ecoute les messages provenant du serveur en utilisant la socket du client, et les affiche sur le terminal.
    	 * 
    	 * @see clientSocket
    	 */
    	
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


