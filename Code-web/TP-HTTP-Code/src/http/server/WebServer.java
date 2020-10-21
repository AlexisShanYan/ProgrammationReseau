///A Simple Web Server (WebServer.java)

package http.server;

import java.awt.image.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;


import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * <b>WebServer représente un serveur web sur lequel des clients pourront se connecter grâce au protocole HTTP.</b>
 * 
 * <p>Ce serveur pourra gérer toutes sortes de requêtes (GET, PUT, ...) et se chargera d'informer l'utilisateur en cas de problème, 
 * 			comme un fichier manquant par exemple.</p>
 * 
 * @author alexis
 * @version 2.0
 */
public class WebServer {

  /**
   * <p>Cette méthode démarre le serveur et écoute les requêtes formulées par les clients. 
   * 		Une fois la requête identifiée, elle en confie la responsabilité à la méthode associée.</p>
   * 
   * @see WebServer#get
   * @see WebServer#head
   * @see WebServer#put
   * @see WebServer#post
   * @see WebServer#delete
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());
        
        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        
        String uri = "";
        String command = "";
        String message = "";
        Map<String, String> map = new HashMap<String,String>();
        String str = ".";
        str = in.readLine();
        if(str != null && !str.equals("")) {
	        System.out.println(str);
	        String[] request = str.split(" ");
	        command = request[0];
	        uri = request[1];
	        String[] lines = null;
	        str = in.readLine();
	        System.out.println(str);
	        while (str != null && !str.equals("")) {
	        	
	        	lines = str.split(": ");
	        	map.put(lines[0], lines[1]); // enregistrer tous les headers
	        	str = in.readLine();
	        	System.out.println(str);
	        }
	        
	        /*
	        do {
		        str = in.readLine();
		        System.out.println(str);
	        } while (!str.endsWith("\r\n"));*/
	        
        }
	        
        switch(command) {
        	case "GET":
        		get(remote, out, uri);
        		break;
        	case "HEAD":
        		System.out.println("HEAD");
        		head(out,uri);
        		break;
        	case "PUT":
        		System.out.println("PUT");
        		put(remote,out,uri);
        		break;
        	case "POST":
        		post(in, out, uri, map);
        		break;
        	case "DELETE":
        		System.out.println("DELETE");
        		delete(out,uri);
        		break;
    		default:
    			try {
    				requestHandler(out,501);
                } catch (Exception e) {
                    System.out.println(e);
                }
        }
        
        
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Démarre l'application.
   * 
   * @param args
   *            Les paramètres de ligne de commande ne sont pas utilisés.
   *            
   * @see WebServer#start
   */
  public static void main(String args[]) {
	  WebServer ws = new WebServer();
	  ws.start();
  }
  
  /**
   * Envoie au client l'état de la requête avec le type de la ressource manipulée.
   * 
   * @param out
   * 	Désigne le flux de communication en sortie dirigé vers le client.
   * @param stat
   * 	Désigne le code de l'état de la requête
   * @param contentType
   * 	Désigne le type de la ressource manipulée
   */
  
  private void requestHandler(PrintWriter out, int stat, String contentType) {
	  switch(String.valueOf(stat)) {
	  case "100":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Continue");
		  out.println("Content-Type: " + contentType);
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "200":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " OK");
		  out.println("Content-Type: " + contentType);
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "403":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Forbidden");
		  out.println("Content-Type: " + contentType);
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "404":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Found");
		  out.println("Content-Type: " + contentType);
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  }
	  
  }
  
  /**
   * Envoie au client l'état de la requête.
   * 
   * @param out
   * 	Désigne le flux de communication en sortie dirigé vers le client.
   * @param stat
   * 	Désigne le code de l'état de la requête
   */
  
  private void requestHandler(PrintWriter out, int stat) {
	  
	  switch(String.valueOf(stat)) {
	  case "100":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Continue");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "204":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " No Content");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "201":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Created");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "403":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Forbidden");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "404":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Found");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "500":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Internal Server Error");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  case "501":
		  out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Implemented");
		  out.println("Server: Bot");
		  out.println("");
		  break;
	  }
  }
  
  /**
   * Récupère le type du fichier manipulé par le serveur web.
   * 
   * @param file
   * 		Désigne le fichier manipulé par le serveur web
   */
  
  public String getContentType(File file) {

      String fileName = file.getName();
      String type = ".txt";

      if (fileName.endsWith(".html") || fileName.endsWith(".htm") || fileName.endsWith(".txt")) {
          type = "text/html";
      } else if (fileName.endsWith(".mp4")) {
          type = "video/mp4";
      } else if (fileName.endsWith(".png")) {
          type = "image/png";
      } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
          type = "image/jpg";
      } else if (fileName.endsWith(".mp3")) {
          type = "audio/mp3";
      } else if (fileName.endsWith(".avi")) {
          type = "video/x-msvideo";
      } else if (fileName.endsWith(".css")) {
          type = "text/css";
      } else if (fileName.endsWith(".pdf")) {
          type = "application/pdf";
      } else if (fileName.endsWith(".odt")) {
          type = "application/vnd.oasis.opendocument.text";
      } else if (fileName.endsWith(".json")) {
          type = "application/json";
      }

      return type;
  }
  
  /**
   * Lorsqu'un client veut récupérer une ressource du serveur, cette méthode sera appelée par la méthode start(). 
   * 		Elle aura pour but de montrer en sortie le fichier demandé par le client, qu'il soit composé de texte ou d'images.
   * 
   * @param remote
   * 		Désigne la socket associée au client
   * @param out
   * 		Désigne le flux de communication en sortie dirigé vers le client.
   * @param uri
   * 		L'URL du fichier que l'on veut récupérer
   * 
   * @see WebServer#start
   * @see WebServer#requestHandler(PrintWriter, int, String)
   */
  
  private void get(Socket remote, PrintWriter out, String uri) {
	  
	  try{

		  String path = ClassLoader.getSystemResource("").toString().substring(6);
		  path = path.substring(0, path.length()-1) + uri;
		  File file = new File(path);
		  System.out.println(path); //Les fichiers se trouvent sous /bin
		  
		  if (uri.equals("/")) {
			  
			  requestHandler(out,200,"text/html");
			  out.println("<link rel=\"icon\" href=\"data:;base64,=\">"); //ignorer favicon.ico
			  // Send the HTML page
			  out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
			  out.flush();
		 
		  } else {
			  
			  String type = getContentType(file);
			  if(file.isFile() && file.exists()){
				  
				  if(type.equals("text/html")){
					  requestHandler(out,200,"text/html");
					  out.println("<link rel=\"icon\" href=\"data:;base64,=\">");
					  
					  String encoding="GBK";
					  InputStreamReader read = new InputStreamReader(
							  new FileInputStream(file),encoding);
					  BufferedReader bufferedReader = new BufferedReader(read);
					  String lineTxt = null;
					  while((lineTxt = bufferedReader.readLine()) != null){
						  out.println(lineTxt);
					  }
					  read.close();
					  out.flush();
					  
				  /*} else if (type.equals("image/png")) {
	
					  try {
						  BufferedImage img = ImageIO.read(new File(path));
						  requestHandler(out,200,"image/png");
						  ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
						  ImageIO.write(img,"png", pngBaos);
						  pngBaos.flush();
						  byte[] imgByte = pngBaos.toByteArray();
						  pngBaos.close();
						  remote.getOutputStream().write(imgByte);
					  } catch (IIOException e) {
						  requestHandler(out,404,"image/png");
					  } catch (Exception e) {
						  e.printStackTrace();
					  }*/
					  
				  } else {
					  
					  BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
					  BufferedOutputStream byteOut = new BufferedOutputStream(remote.getOutputStream());
					  requestHandler(out,200,type);
					  byte[] buffer = new byte[1048576];
					  int nbRead;
					  while((nbRead = in.read(buffer)) != -1) {
						  byteOut.write(buffer, 0, nbRead);
					  }
					  in.close();
					  byteOut.flush(); 
					  //byteOut.close();
				  }
				  
			  } else {
	              if(file.isFile()) {
	            	  requestHandler(out,403,type);
	            	  out.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
	            	  out.println("<title>404 Not Found</title>");
	            	  out.println("<h1>404 Not Found</h1>");
	            	  out.println("<p>The requested URL was not found on this server.</p>");
	              } else {
	            	  requestHandler(out,404,type);
	            	  out.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
	            	  out.println("<title>404 Not Found</title>");
	            	  out.println("<h1>404 Not Found</h1>");
	            	  out.println("<p>The requested URL was not found on this server.</p>");
	              }
	              out.flush();
	          }
		  }
	  } catch (IOException e) {
          e.printStackTrace();
          try {
        	  requestHandler(out,500);
              out.flush();
          } catch (Exception e2) {
              e2.printStackTrace();
          }
      }
	  //else if(url.contains(".html?"))//GET avec param¨¨tres
	  
  }
  
  /**
   * Lorsqu'un client veut récupérer l'entête d'une ressource web, cette méthode sera appelée par la méthode start(). 
   * 		Elle aura pour but de montrer en sortie l'entête demandée par le client.
   * 
   * @param out
   * 		Désigne le flux de communication en sortie dirigé vers le client.
   * @param uri
   * 		L'URL du fichier que l'on veut récupérer
   * 
   * @see WebServer#start
   * @see WebServer#requestHandler
   * @see WebServer#requestHandler(PrintWriter, int, String)
   */
  
  private void head(PrintWriter out, String uri) {
	  
	  try {
		  String path = ClassLoader.getSystemResource("").toString().substring(6);
		  path = path.substring(0, path.length()-1) + uri;
		  File file = new File(path);
		  
		  if (file.exists() && file.isFile()){
		      String type = getContentType(file);
		      requestHandler(out,200,type);
		  }else{
			  requestHandler(out,404);
		  }
		  out.flush();
	  } catch (Exception e) {
          e.printStackTrace();
          try {
        	  requestHandler(out,500);
              out.flush();
          } catch (Exception e2) {
              System.out.println(e);
          }
	  }
  }
  
  /**
   * Lorsqu'un client veut uploader une ressource sur le serveur, cette méthode sera appelée par la méthode start(). 
   * 		Elle aura pour but de créer le fichier avec le contenu voulu, ou remplacer ce dernier si le fichier existe déjà.
   * 
   * @param remote
   * 		Désigne la socket associée au client
   * @param out
   * 		Désigne le flux de communication en sortie dirigé vers le client.
   * @param uri
   * 		L'URL du fichier que l'on veut récupérer
   * 
   * @see WebServer#start
   * @see WebServer#requestHandler(PrintWriter, int)
   */
  
  private void put(Socket remote, PrintWriter out, String uri) {
	  
	  try {
		  BufferedInputStream  in = new BufferedInputStream (remote.getInputStream());
		  
		  String path = ClassLoader.getSystemResource("").toString().substring(6);
		  path = path.substring(0, path.length()-1) + uri;
		  File file = new File(path);
		  
		  if (!file.createNewFile()){
              PrintWriter pw = new PrintWriter(file);
              pw.close();
          }
		  
		  BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file, file.exists()));

          byte[] buffer = new byte[256];
          while(in.available() > 0) {
              int nbRead = in.read(buffer);
              fileOut.write(buffer, 0, nbRead);
          }
          fileOut.flush();
          fileOut.close();

          if (file.createNewFile()) {
        	  requestHandler(out,204);
          } else {
        	  requestHandler(out,201);
          }
          out.flush();
	  } catch (Exception e) {
          e.printStackTrace();
          try {
        	  requestHandler(out,500);
              out.flush();
          } catch (Exception e2) {
              System.out.println(e);
          }
	  }
	  
  }
  
  /**
   * Lorsqu'un client veut supprimer une ressource sur le serveur, cette méthode sera appelée par la méthode start(). 
   * 	Comme son nom l'indique, elle supprimera la ressource demandée si cette dernière existe, sinon, une erreur surviendra.
   * 
   * @param out
   * 		Désigne le flux de communication en sortie dirigé vers le client.
   * @param uri
   * 		L'URL du fichier que l'on veut récupérer
   * 
   * @see WebServer#start 
   * @see WebServer#requestHandler(PrintWriter, int)
   */
  
  private void delete(PrintWriter out, String uri) {
	  
	  try {
		  String path = ClassLoader.getSystemResource("").toString().substring(6);
		  path = path.substring(0, path.length()-1) + uri;
		  File file = new File(path);
		  
          boolean removed = false;
          
          if (file.exists() && file.isFile()){
              removed = file.delete();
          }
          
          if(removed) {
        	  requestHandler(out,204);
          } else if (!file.exists()) {
        	  requestHandler(out,404);
          } else {
        	  requestHandler(out,403);
          }
          out.flush();
	  } catch (Exception e) {
          e.printStackTrace();
          try {
        	  requestHandler(out,500);
              out.flush();
          } catch (Exception e2) {
              System.out.println(e);
          }
	  }
	  
  }
  
  /**
   * Lorsqu'un client veut mettre à jour une ressource sur le serveur, cette méthode sera appelée par la méthode start(). 
   * 		Elle aura pour but de créer le fichier avec le contenu voulu, 
   * 			ou de rajouter des informations (sans remplacer celles déjà présentes !) à ce dernier si le fichier existe déjà.
   * 
   * @param out
   * 		Désigne le flux de communication en sortie dirigé vers le client.
   * @param uri
   * 		L'URL du fichier que l'on veut récupérer
   * 
   * @see WebServer#start 
   * @see WebServer#requestHandler(PrintWriter, int, String)
   */
  
  private void post(BufferedReader in, PrintWriter out, String uri, Map<String, String> request) {
	  
	  String path = ClassLoader.getSystemResource("").toString().substring(6);
	  path = path.substring(0, path.length()-1) + uri;
	  File file = new File(path);
	  //requestHandler(out,100,"text/html");
	  
	  String encoding="GBK";
	  try {
		  requestHandler(out,200,"text/html");
	      if(file.isFile() && file.exists()){ 
	          InputStreamReader read = new InputStreamReader(
	          new FileInputStream(file),encoding);
	          BufferedReader bufferedReader = new BufferedReader(read);
	          String lineTxt = null;
	          while((lineTxt = bufferedReader.readLine()) != null){
	              out.println(lineTxt);
	          }
	          read.close();
	          
	      }else{
	          System.out.println("File not found.");
	      }
	  } catch (IOException e) {
  				e.printStackTrace();
	  }
	  out.flush();
	      
  }
}
