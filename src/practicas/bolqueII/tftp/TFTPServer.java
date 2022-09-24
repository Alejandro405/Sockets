package practicas.bolqueII.tftp;

import java.io.IOException;
import java.net.*;
import static teoria.udp.ServerUDP.ECHOMAX;

public class TFTPServer {
    private static final int TIME_OUT = 100;

    public static void main(String[] args) throws SocketException {
        int clientTID;
        SocketAddress clientSocketAdress;
        DatagramSocket socket = new DatagramSocket(6789);
        DatagramPacket sendPacket = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        DatagramPacket request = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

        while (true) {
            try {
                socket.receive(request); // Recibe un datagrama del cliente

            } catch (IOException e) {
                System.err.println("Fallo al recibir datagrama");
            }
            System.out.println("IP cliente: " + request.getAddress().getHostAddress() +" Puerto cliente: " + request.getPort());
        }
    }
}
