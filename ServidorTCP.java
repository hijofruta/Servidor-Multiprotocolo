import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.stream.events.StartDocument;

public class ServidorTCP implements Runnable{
	
 protected Socket clientSocket;
 static int puerto=1008;
 static int tiempo=2*1000;
 
 public ServidorTCP() throws IOException{ 
    ServerSocket serverSocket = null;
    Executor ejecuto=Executors.newCachedThreadPool();
    try { 
         serverSocket= new ServerSocket(puerto); 
         System.out.println ("Conexion establecida");
         try { 
        	 //serverSocket.setSoTimeout(tiempo);
              while (true){                  
                  System.out.println ("Esperando para Aceptar Conexion");
                  try {
                       ejecuto.execute(new ServidorTCP(serverSocket.accept())); 
                  }catch (SocketTimeoutException ste){
                       System.out.println ("Error: Tiempo acabado");                       
                  }
             }
         }catch (IOException e){ 
              System.err.println("Fallo al aceptar conexion"); 
              System.exit(1);     
         } 
    }catch (IOException e){ 
    	System.err.println("Error:Al establecer conexion");
    	System.exit(1);
    }finally{
         try {
              serverSocket.close();
              System.out.println ("Cerrado el servidor");
         }catch (IOException e){ 
              System.err.println("Error al cerrar el puerto");
              System.exit(1);
         } 
    }
 }

 public ServidorTCP(Socket clientSoc){
    clientSocket = clientSoc; 
 }

 public void run(){
    System.out.println ("Iniciando nuevo hilo");
    try { 
         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true); 
         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
         String inputLine; 
         
         while ((inputLine = in.readLine()) != null){        	  
              System.out.println ("Server: " + inputLine);               
              out.println(inputLine);
              if (inputLine.equals("stop")){            	              	  
            	  break;
              }           
         }
         out.close(); 
   	  	 in.close();    	  	 
   	  	 System.out.println(".......Cerrado el Cliente: "+clientSocket.getPort()+"..........");
   	  	 clientSocket.close();
    }catch (IOException e){ 
         System.err.println("Problema con el servidor");
         System.exit(1);
    }
    
  }
} 