import java.net.*;
import java.io.*;

public class ServerUDP {
	// puerto UDP al cual se enlaza el servicio
		public static final int PuertoServer = 8;
	// tamaño  maximo del paquete, lo suficientemente largo para casi cualquier cliente
		public static final int Echomax = 255;
	// Socket usado para leer y escribir paquetes UDP
		private DatagramSocket socket;
		
		public ServerUDP(){
			try{
	// enlazarse al puerto UDP especificado para escuchar paquetes de datos entrantes
				socket =new DatagramSocket(PuertoServer);
				System.out.println("Servidor activado en el puerto " +socket.getLocalPort());
			}catch (Exception e){
				System.out.println("ERROR Servidor:No se puede enlazar el puerto");
			}
		}
		
		public void activarCliente(){
	// Crear un buffer suficientemente largo para los paquetes entrantes
			byte[] buffer = new byte[Echomax];
//		 	Crear un DatagramPacket para leer paquetes UDP
			DatagramPacket packet = new DatagramPacket( buffer, Echomax );
			while(true){
				try{
						// 	Recibir paquetes entrantes
					socket.receive(packet);
					System.out.println("Paquete recibido de "+packet.getAddress()+":"+packet.getPort() +" longitud: "+packet.getLength());
					
					socket.send(packet);
					packet.setLength(Echomax);
					
				}catch (IOException ioe){					
					System.err.println ("Error : " + ioe);
					break;
				}
			}
		}

		public static void main(String args[]){
			ServerUDP server = new ServerUDP();
			server.activarCliente();
		}
}
