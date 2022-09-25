package practicas.bolqueI.echoUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class echoUDPServer {
    public static void main(String[] args) {
        DatagramSocket ds = null;
        try {
            InetAddress localAddr = InetAddress.getByName("localhost");
            int wellKnownPort = 3000; // Puerto conocido del servidor
            ds = new DatagramSocket(wellKnownPort, localAddr);
            byte[] buffer = new byte[2048];
            DatagramPacket datagram = new DatagramPacket(buffer,
                    buffer.length);
            while (true) {
                ds.receive(datagram);
                System.out.println("Nueva peticion de servicio");
                // Inicio de una hebra para la peticion actual
                (new echoUDPHandler(datagram)).start();
            }
        } catch (IOException e) {
            System.err.println("Error E/S en: " + e.getMessage());
        }finally {
                if (ds != null)
                    ds.close();

        }

    }
}
