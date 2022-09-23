package teoria.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class ExtendUDPClient {
    // Temporizador de retransmision (ms)
    private static final int TIMEOUT = 3000;
    // Maximo numero de retransmisiones
    private static final int MAXTRIES = 5;


    public static void main(String[] args) throws IOException {
        InetAddress serverAddress = InetAddress.getLocalHost();
        String s = new String("Asignatura DST");
        byte[] bytesToSend = s.getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
                bytesToSend.length, serverAddress, 5678);
        DatagramPacket receivePacket = new DatagramPacket(
                new byte[bytesToSend.length], bytesToSend.length);


        int tries = 0;
        boolean receivedResponse = false;
        do {
            socket.send(sendPacket);
            socket.setSoTimeout(TIMEOUT); // Temporizador para cada envio
            try {
                socket.receive(receivePacket);
                receivedResponse = true;
            } catch (SocketTimeoutException e) { // Expiro el temporizador
                tries += 1;
                System.out.println("Timeout, "+(MAXTRIES - tries)+" mas");
            }
        } while ((!receivedResponse) && (tries < MAXTRIES));


        if (receivedResponse) {
            System.out.println("Received: " + new String(
                    receivePacket.getData()));
            socket.setSoTimeout(0);
        } else {
            System.out.println("No hubo respuesta del servidor.");
        }
        socket.close();
    }
}

