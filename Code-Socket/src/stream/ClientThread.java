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

/**
 * <b>ClientThread est la classe qui a pour r�le d'�tre un thread permettant � chaque client de diffuser son message sur le chat.</b>
 * 
 * <p>Chaque client aura un seul thread associ�. Ce dernier s'occupera de diffuser le message de son client associ� � tous les autres clients</p>
 * 
 * <p>Pour de plus amples informations sur le thread s'occupant de la connexion des nouveaux clients, 
 * 					voir la documentation sur la classe EchoServerMultiThreaded.</p>
 * 
 * @see EchoServerMultiThreaded
 * @see EchoClient
 * 
 * @author alexis
 * @version 2.0
 */

public class ClientThread
	extends Thread {
	
	/**
	 * La liste des sockets c�t� serveur qui sont li�s aux sockets c�t� client. 
	 * 			C'est une liste pouvant �tre acc�d�e par toutes les instances de ClientThread
	 * 
	 *  @see ClientThread#totalSocket
	 */
	
	public static LinkedList<Socket> list=new LinkedList<Socket>();
	
	/**
	 * Symbolise le nombre de sockets c�t� serveur �tant connect�es � une socket client. 
	 * 			C'est un nombre pouvant �tre acc�d� par toutes les instances de ClientThread
	 * 
	 * @see ClientThread#list
	 */
	
	public static int totalSocket = 0;
	
	/**
	 * <p>La socket c�t� serveur formant un canal de communication avec une machine d'un client.</p>
	 */
	
	private Socket clientSocket;
	
	/**
	 * Constructeur ClientThread
	 * 
	 * <p>Lors de la construction de l'objet, on ajoute � la liste de sockets celle pass�e en param�tre. 
	 * 			On incr�mente aussi le nombre de sockets contenus dans la liste.</p>
	 * 
	 * @param s
	 * 		C'est la socket li�e au client
	 * 
	 * @see ClientThread#list
	 * @see ClientThread#totalSocket
	 */
	
	ClientThread(Socket s) {
		this.clientSocket = s;
		list.add(clientSocket);
		totalSocket += 1;
	}

 	/**
  	* Se charge de transmettre le message du client associ� � tous les autres clients connect�s au chat.
  	* 
  	* @see ClientThread#list
  	* @see ClientThread#totalSocket
  	* @see ClientThread#clientSocket
  	**/
	
	public void run() {
    	  try {
    		  
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));
    		
    		while (true) {
    		  String line = socIn.readLine();
    		  
    		  if (line.contains("leaves the chat")) {
    			  list.remove(clientSocket);
				  totalSocket -= 1;
    		  }
    		  
    		  for (int i=0; i<totalSocket; i++) {
      			  PrintStream socOut = new PrintStream(list.get(i).getOutputStream());
    			  socOut.println(line);
    		  }
    		  
		      Files.write(Paths.get("filename.txt"), (line + "\n").getBytes(), StandardOpenOption.APPEND);
    		}
    	} catch (NullPointerException e) {
        	System.out.println("A client has disconnected"); 
        } catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
     }
  
  }

  