package teoria.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class ExtendUDPServer {
    private static final int TIME_OUT = 300; //ms
    private static final int ECHOMAX = 255; // Tamagno maximo de los mensajes


    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(6789);
        DatagramPacket sendPacket = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        DatagramPacket recivePacket = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

        while (true) {
            socket.receive(sendPacket); // Recibe un datagrama del cliente
            System.out.println("IP cliente: " + sendPacket.getAddress().getHostAddress() +" Puerto cliente: " + sendPacket.getPort());
            SocketAddress clientAdress = sendPacket.getSocketAddress();
            boolean recivedresponse = false;
            do {
                socket.send(sendPacket); // Enviar el mismo datagrama al cliente
                socket.setSoTimeout(TIME_OUT);
                try{
                    socket.receive(recivePacket);//permanezco a la espera hasta que salte el temporizador
                    if (recivePacket.getSocketAddress().equals(clientAdress))
                        recivedresponse = true;
                } catch(SocketTimeoutException e) {
                    System.out.println("Error de time out");
                }
            }while (!recivedresponse);


            sendPacket.setLength(ECHOMAX); // Limpiar buffer
        }
    }

}
