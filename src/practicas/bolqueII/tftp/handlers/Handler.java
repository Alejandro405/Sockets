package practicas.bolqueII.tftp.handlers;

import java.net.DatagramSocket;

public interface Handler {
    void atenderPeticion(DatagramSocket serverSocket);
}
