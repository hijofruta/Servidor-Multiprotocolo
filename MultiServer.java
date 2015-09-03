import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.io.IOException;

public class MultiServer{

  public static int PORT = 7;
  
  public static final int PuertoServer = 7;
  public static final int PuertoClient = 8;
  public static final int Echomax = 255;  
  
  
  
  
  public static void main(String[] args) throws IOException{
    
    System.out.println("Escuchando por el puerto: " + PORT);
    
    ServerSocketChannel serverChannel;
    ServerSocket ss;
    DatagramChannel canalUdp;
    DatagramSocket socketUdp;
    Selector selector;
    SelectionKey TCP,UDP;
    
    
	try {
      serverChannel = ServerSocketChannel.open();
      ss = serverChannel.socket();
      InetSocketAddress address = new InetSocketAddress(PORT);
      ss.bind(address);
   //abrir canal Udp y ligarlo al puerto de servidor     
      canalUdp=DatagramChannel.open();
      socketUdp=canalUdp.socket();
      socketUdp.bind(address);
           
      canalUdp.configureBlocking(false);
      serverChannel.configureBlocking(false);
      selector = Selector.open();
      TCP=serverChannel.register(selector, SelectionKey.OP_ACCEPT);
      UDP=canalUdp.register(selector, SelectionKey.OP_READ);
      
    }
    catch (IOException ex) {
      ex.printStackTrace();
      return;
    }

    while (true) { //incio bucle principal

      try {
        selector.select();
      }
      catch (IOException ex) {
        ex.printStackTrace();
        break;
      }

      Set leerKeys=selector.selectedKeys();
      Iterator iterator=leerKeys.iterator();
      while (iterator.hasNext()) {//inicio bucle key
   
        SelectionKey key=(SelectionKey) iterator.next();
        iterator.remove();
        try {
          if(key==TCP){
        	  
        	  if (key.isAcceptable()) {
                  ServerSocketChannel server=(ServerSocketChannel) key.channel();
                  SocketChannel cliente=server.accept();
                  System.out.println("Conexion aceptada para: " + cliente);
                  cliente.configureBlocking(false);
                  SelectionKey clienteKey=cliente.register(selector,SelectionKey.OP_READ);
                  ByteBuffer buffer=ByteBuffer.allocate(100);
                  clienteKey.attach(buffer);
                }
        	         	  	
          }else if(key==UDP){//si es udp se activa el servidor udp

	        	// Crear un buffer suficientemente largo para los paquetes entrantes
	      		ByteBuffer buffer =ByteBuffer.allocate(Echomax);
	      		buffer.clear();	      			    
	      			try{
	      				canalUdp.receive(buffer);      					      				
	      				System.out.println("lectura Datagram ");
	      				ServerUDP sUdp=new ServerUDP();
	      				sUdp.activarCliente();	      				
	      			}catch (IOException ioe){					
	      				System.err.println ("Error : " + ioe);
	      				break;
	      			}
	      		          	  
          }else	if (key.isReadable()) {// si es el tcp y tiene activado la lectura
        	  System.out.println("estoy aqui");
              SocketChannel cliente=(SocketChannel) key.channel();
              ByteBuffer output=(ByteBuffer) key.attachment();
              cliente.read(output);
 
              if(cliente.finishConnect()){
            	  key.channel().close();
              }
              //empieza tcp
              	ServidorTCP sTcp=new ServidorTCP();              
              //termina el tcp
              
          }
        }catch (IOException ex) {
        key.cancel();
        }
        try {
           key.channel().close();
        }catch (IOException cex) {}     
      }//fin bucle claves

    }//fin bucle principal

  }
}
