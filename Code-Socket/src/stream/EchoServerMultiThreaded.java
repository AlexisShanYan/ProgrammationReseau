/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

/**
 * <b>EchoServerMultiThreaded représente une fonctionnalité (thread) d'un serveur faisant fonctionner une application de chat utilisant le protocole TCP.</b>
 * 
 * <p>Ce serveur ne sera instancié qu'une seule fois et se chargera des connexions des nouveaux clients.</p>
 *
 * <p>Pour de plus amples informations concernant la gestion des différentes communications entrantes et sortantes vers les clients, 
 *		veuillez vous référer à la documentation de la classe ClientThread.</p>
 *
 * @see ClientThread
 * @see EchoServerMultiThreaded#main
 * 
 * @author alexis
 * @version 2.0
 */



public class EchoServerMultiThreaded  {
  
 	/**
  	* Méthode main
  	* <p>Gère la connexion d'un client au serveur. Pour chaque client connecté, crée une instance de la classe ClientThread</p>
  	* 
  	* @param args
  	* 			Le port du serveur
	* 
	* @see ClientThread
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
			File myObj = new File("filename.txt");
			if (myObj.createNewFile()) {}
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from:" + clientSocket.getInetAddress());
				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
			}
	    } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
            e.printStackTrace();
	    }
   }
}

  