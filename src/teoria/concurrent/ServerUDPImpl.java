package teoria.concurrent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerUDPImpl extends Thread{
    private byte[] datos;
    private InetAddress address;
    private int port;
    public ServerUDPImpl(DatagramPacket d) {
        this.datos=d.getData();
        this.address=d.getAddress();
        this.port = d.getPort();
    }
    public void run() {
        try {
            DatagramSocket ds = new DatagramSocket(); // Nuevo socket y puerto
            DatagramPacket sd = new DatagramPacket
                    (datos,datos.length,address,port);
            ds.send(sd);
//DENTRO DE BUCLE SEGÚN IMPLEMENTACION
//DEL PROTOCOLO
            ds.close();
        } catch (Exception e) {

        }
    }

}


