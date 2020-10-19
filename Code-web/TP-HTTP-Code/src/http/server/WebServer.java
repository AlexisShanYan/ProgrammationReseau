///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
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
        String str = ".";
        while (str != null && !str.equals("")) {
        	str = in.readLine();
        	
        	System.out.println(str);
        
	        String[] request = str.split(" ");
	        
	        switch(request[0]) {
	        	case "GET":
	        		get(out, request[1]);
	        		break;
	        	case "HEAD":
	        		System.out.println("HEAD");
	        		break;
	        	case "PUT":
	        		System.out.println("PUT");
	        		break;
	        	case "POST":
	        		System.out.println("POST");
	        		break;
	        	case "DELETE":
	        		System.out.println("DELETE");
	        		break;
	        }
        }
        
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
  
  public static void get(PrintWriter out, String url) {
	  if (url == "/") {
		  out.println("HTTP/1.0 200 OK");
		  out.println("Content-Type: text/html");
		  out.println("Server: Bot");
		  // this blank line signals the end of the headers
		  out.println("");
	      // Send the HTML page
	      out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
	      out.flush();
	  }
	  else {
		  try(FileReader reader = new FileReader("D:\\info\\réseau\\4IF\\Code-web\\TP-HTTP-Code\\src\\http\\server\\" + url);
			BufferedReader br = new BufferedReader(reader)) {
		  		out.println("HTTP/1.0 200 OK");
		        out.println("Content-Type: text/html");
		        out.println("Server: Bot");
		        // this blank line signals the end of the headers
		        out.println("");
		        String line;
		        while ((line = br.readLine()) != null) {
		        	out.println(line);
		        }
	  	    } catch (IOException e) {
	  	        e.printStackTrace();
	  	    }
	  }
	  
  }
}
