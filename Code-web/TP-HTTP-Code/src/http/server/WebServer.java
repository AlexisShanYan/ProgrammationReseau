///A Simple Web Server (WebServer.java)

package http.server;

import java.awt.image.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;


import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * <b>WebServer repr�sente un serveur web sur lequel des clients pourront se connecter gr�ce au protocole HTTP.</b>
 * 
 * <p>Ce serveur pourra g�rer toutes sortes de requ�tes (GET, PUT, ...) et se chargera d'informer l'utilisateur en cas de probl�me.
 * 		Ces probl�mes peuvent �tre multiples : fichier non trouv� (erreur 404), fichier dont la consultation/modification est interdite (erreur 403),
 * 			probl�me arriv� au serveur (erreur 500). Lorsque tout se passe bien, le client re�oit une r�ponse compos�e d'un code 200 OK.</p>
 * 
 * @author alexis, zihao
 * @version 2.0
 */
public class WebServer {

	  /**
	   * <p>Cette m�thode d�marre le serveur et �coute les requ�tes formul�es par les clients. 
	   * 		Une fois la requ�te identifi�e, elle en confie la responsabilit� � la m�thode associ�e.</p>
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
	        String postData = "";
	        byte[] byteData = null;
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
		        
		        if(map.containsKey("Content-Length")) {
			        int cL = Integer.valueOf(map.get("Content-Length"));
			        char[]  buffer      = new char[cL];

			        in.read(buffer, 0, cL);
			        
			        postData = new String(buffer, 0, buffer.length);
			        System.out.println(postData);
			        
			        Charset cs = Charset.forName("UTF-8");
			        CharBuffer cb = CharBuffer.allocate(buffer.length);
			        cb.put(buffer);
			        cb.flip();
			        ByteBuffer bb = cs.encode(cb);
			        byteData = bb.array();
		        }
	        }
		       
		        
	        switch(command) {
	        	case "GET":
	        		get(remote, out, uri);
	        		break;
	        	case "HEAD":
	        		head(out, uri);
	        		break;
	        	case "PUT":
	        		put(remote, out, uri, byteData);
	        		break;
	        	case "POST":
	        		post(out, uri, map, byteData);
	        		break;
	        	case "DELETE":
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
	   * D�marre l'application.
	   * 
	   * @param args
	   *            Les param�tres de ligne de commande ne sont pas utilis�s.
	   *            
	   * @see WebServer#start
	   */
	  public static void main(String args[]) {
		  WebServer ws = new WebServer();
		  ws.start();
	  }
	  
	  /**
	   * Envoie au client l'�tat de la requ�te avec le type de la ressource manipul�e.
	   * 
	   * @param out
	   * 	D�signe le flux de communication en sortie dirig� vers le client.
	   * @param stat
	   * 	D�signe le code de l'�tat de la requ�te.
	   * @param contentType
	   * 	D�signe le type de la ressource manipul�e.
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
			  out.println("<html>");
			  out.println("<head><title>403 Forbidden</title></head>");
	    	  out.println("<body><h1>403 Forbidden</h1>");
	    	  out.println("<p>Access is forbidden to the requested page.</p></body>");
	    	  out.println("</html>");
			  break;
		  case "404":
			  out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Found");
			  out.println("Content-Type: " + contentType);
			  out.println("Server: Bot");
			  out.println("");
	    	  out.println("<html>");
	    	  out.println("<head><title>404 Not Found</title></head>");
	    	  out.println("<body><h1>404 Not Found</h1>");
	    	  out.println("<p>The requested URL was not found on this server.</p></body>");
	    	  out.println("</html>");
			  break;
		  }
		  out.flush();
	  }
	  
	  /**
	   * Envoie au client l'�tat de la requ�te.
	   * 
	   * @param out
	   * 	D�signe le flux de communication en sortie dirig� vers le client.
	   * @param stat
	   * 	D�signe le code de l'�tat de la requ�te.
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
			  out.println("<html>");
			  out.println("<head><title>403 Forbidden</title></head>");
	    	  out.println("<body><h1>403 Forbidden</h1>");
	    	  out.println("<p>Access is forbidden to the requested page.</p></body>");
	    	  out.println("</html>");
			  break;
		  case "404":
			  out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Found");
			  out.println("Server: Bot");
			  out.println("");
	    	  out.println("<html>");
	    	  out.println("<head><title>404 Not Found</title></head>");
	    	  out.println("<body><h1>404 Not Found</h1>");
	    	  out.println("<p>The requested URL was not found on this server.</p></body>");
	    	  out.println("</html>");
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
		  out.flush();
	  }
	  
	  /**
	   * R�cup�re le type du fichier manipul� par le serveur web.
	   * 
	   * @param file
	   * 		D�signe le fichier manipul� par le serveur web.
	   * 
	   * @return type
	   * 		Retourne le type du fichier manipul�.
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
	   * Lorsqu'un client veut r�cup�rer une ressource du serveur, cette m�thode sera appel�e par la m�thode start(). 
	   * 		Elle aura pour but de montrer en sortie le fichier demand� par le client, qu'il soit compos� de texte ou d'images.
	   * 
	   * @param remote
	   * 		D�signe la socket associ�e au client.
	   * @param out
	   * 		D�signe le flux de communication en sortie dirig� vers le client.
	   * @param uri
	   * 		L'URL du fichier que l'on veut r�cup�rer.
	   * 
	   * @see WebServer#start
	   * @see WebServer#requestHandler(PrintWriter, int, String)
	   */
	  
	  private void get(Socket remote, PrintWriter out, String uri) {
		  try{
			  String path = ClassLoader.getSystemResource("").toString().substring(6);
			  path = path.substring(0, path.length()-1) + (uri.indexOf("?")==-1 ? uri : uri.substring(0, uri.indexOf("?")));
			  File file = new File(path);
			  System.out.println(path); //Les fichiers se trouvent sous /bin
			  
			  if(!uri.contains("?")) {
					  
				  if (uri.equals("/")) {
					  requestHandler(out,200,"text/html");
					  out.println("<link rel=\"icon\" href=\"data:;base64,=\">"); //ignorer favicon.ico
					  // Send the HTML page
					  out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
					  out.flush();
				 
				  } else {
					  String type = getContentType(file);
					  System.out.println(type);
					  if(file.isFile() && file.exists() && !file.canRead()){
						  requestHandler(out,403,getContentType(file));
					  } else {
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
								  
							  } else if (type.equals("image/png")) {
								  try {
									  BufferedImage img = ImageIO.read(new File(path));
									  requestHandler(out,200,"image/png");
									  out.flush();
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
								  }
								  
							  } else {
								  BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
								  BufferedOutputStream byteOut = new BufferedOutputStream(remote.getOutputStream());
								  requestHandler(out,200,type);
								  
								  byte[] buffer = new byte[1024];
								  
								  int nbRead;
								  while((nbRead = in.read(buffer)) != -1) {
									  byteOut.write(buffer, 0, nbRead);
								  }
								  in.close();
								  byteOut.flush(); 
							  }
						  
						  } else if(file.isFile()) {
							  requestHandler(out,403,type);
						  } else {
							  requestHandler(out,404,type);
						  }
					  }
				  }
			  } else if(uri.contains("Adder.html?") && !uri.endsWith("?") && file.exists() && file.isFile()){ //GET avec parametres
				  requestHandler(out,200,"text/html");
				  out.println("<link rel=\"icon\" href=\"data:;base64,=\">");
				  Map<String, String> map = new HashMap<String,String>();
				  String lineParam = null;
				  String[] params = null;
				  lineParam = uri.substring(uri.indexOf("?")+1);
				  params = lineParam.split("&");
				  for(String s : params) 
					  map.put(s.split("=")[0], s.split("=")[1]);
				  int result = 0;
				  for(String key:map.keySet()){
					  if((key.equals("first")||key.equals("second")) && !map.get(key).isEmpty()) {
						  result += Integer.parseInt(map.get(key));
					  }
				  }
				  
				  out.println(result);
				  out.flush();
			  } else {
				  requestHandler(out,404,"text/html");
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
		  
	  }
	  
	  /**
	   * Lorsqu'un client veut r�cup�rer l'ent�te d'une ressource web, cette m�thode sera appel�e par la m�thode start(). 
	   * 		Elle aura pour but de montrer en sortie l'ent�te demand�e par le client.
	   * 
	   * @param out
	   * 		D�signe le flux de communication en sortie dirig� vers le client.
	   * @param uri
	   * 		L'URL du fichier que l'on veut r�cup�rer.
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
	   * Lorsqu'un client veut uploader une ressource sur le serveur, cette m�thode sera appel�e par la m�thode start(). 
	   * 		Elle aura pour but de cr�er le fichier avec le contenu voulu, ou remplacer ce dernier si le fichier existe d�j�.
	   * 
	   * @param remote
	   * 		D�signe la socket associ�e au client.
	   * @param out
	   * 		D�signe le flux de communication en sortie dirig� vers le client.
	   * @param uri
	   * 		L'URL du fichier que l'on veut r�cup�rer.
	   * @param byteData
	   * 		Les donn�es du body sous forme byte[].
	   * 
	   * @see WebServer#start
	   * @see WebServer#requestHandler(PrintWriter, int)
	   */
	  
		private void put(Socket remote, PrintWriter out, String uri, byte[] byteData) {
			  
			  try {
				  String path = ClassLoader.getSystemResource("").toString().substring(6);
				  path = path.substring(0, path.length()-1) + uri;
				  File file = new File(path);
				  
				  if(file.isFile()&&file.exists()&&!file.canWrite()){
					  requestHandler(out,403,getContentType(file));
				  } else {
					  FileOutputStream fos = new FileOutputStream(file);
					  fos.write(byteData);
					  fos.close();
					  
					  if (file.createNewFile()) {
						  requestHandler(out,204);
					  } else {
						  requestHandler(out,201);
					  }
					  out.flush();
				  }
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
	   * Lorsqu'un client veut supprimer une ressource sur le serveur, cette m�thode sera appel�e par la m�thode start(). 
	   * 	Comme son nom l'indique, elle supprimera la ressource demand�e si cette derni�re existe, sinon, une erreur surviendra.
	   * 
	   * @param out
	   * 		D�signe le flux de communication en sortie dirig� vers le client.
	   * @param uri
	   * 		L'URL du fichier que l'on veut r�cup�rer.
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
		          if(file.isFile()&&file.exists()&&!file.canWrite()){
					  requestHandler(out,403,getContentType(file));
				  } else {
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
				  }
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
	   * Lorsqu'un client veut mettre � jour une ressource sur le serveur, cette m�thode sera appel�e par la m�thode start(). 
	   * 		Elle aura pour but de cr�er le fichier avec le contenu voulu, 
	   * 			ou de rajouter des informations (sans remplacer celles d�j� pr�sentes !) � ce dernier si le fichier existe d�j�.
	   * 
	   * @param out
	   * 		D�signe le flux de communication en sortie dirig� vers le client.
	   * @param uri
	   * 		L'URL du fichier que l'on veut r�cup�rer.
	   * @param headers
	   * 		Un hashmap contenant toutes les informations du header.
	   * @param byteData
	   * 		Les donn�es du body sous forme byte[].
	   * 
	   * @see WebServer#start 
	   * @see WebServer#requestHandler(PrintWriter, int, String)
	   */
	  
		private void post(PrintWriter out, String uri, Map<String, String> headers, byte[] byteData) {
			  
			  String path = ClassLoader.getSystemResource("").toString().substring(6);
			  path = path.substring(0, path.length()-1) + uri;
			  File file = new File(path);
			  
			  try {
				  if(headers.containsKey("test-500") && headers.get("test-500").equals("1")) {
					  int i = 0;
					  i = 1/i;
				  }
				  if(file.isFile()&&file.exists()&&!file.canWrite()){
					  requestHandler(out,403,getContentType(file));
				  } else {
					  if(file.isFile() && file.exists()){ 
						  requestHandler(out,200,"text/html");
						  RandomAccessFile rf = new RandomAccessFile(file, "rw");
						  rf.seek(rf.length());
						  rf.write(byteData);
						  rf.close();
						  
					  }else{
						  System.out.println("File not found.");
						  requestHandler(out,404,getContentType(file));
					  }
				  }
			  } catch (Exception e) {
				  e.printStackTrace();
				  try {
		        	  requestHandler(out,500);
		          } catch (Exception e2) {
		              System.out.println(e2);
		          }
			  }
			      
		  }
		}
