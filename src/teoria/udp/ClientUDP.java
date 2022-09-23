package teoria.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientUDP {
	
	public static void main(String[] args) throws IOException {
		if ((args.length < 1) || (args.length > 2)) {
			throw new IllegalArgumentException("Parameter(s): <Server> [<Port>]");
		}
		InetAddress serverAddress = InetAddress.getByName(args[0]); // IP Servidor
		int servPort = (args.length == 2) ? Integer.parseInt(args[1]) : 3000;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String mensaje=stdIn.readLine();
		byte[] bytesToSend = mensaje.getBytes();
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, serverAddress, servPort); //Datagrama de envío
		socket.send(sendPacket);


		DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);//Datagrama a recibir
		socket.receive(receivePacket); // Podria no llegar nunca el datagrama de ECO
		System.out.println("ECO:"+ new String(receivePacket.getData()));
		socket.close();
	}
	
	
}
