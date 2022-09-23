package teoria.tcp;

import java.net.Socket;
import java.util.logging.Logger;

public class EchoProtocol implements Runnable{
    private static final int BUFSIZE = 32; // Tamaño buffer de E/S
    private Socket clntSock; // Socket de datos
    private Logger logger; // Logger del servidor
    public EchoProtocol(Socket clntSock, Logger logger) {
        this.clntSock = clntSock;
        this.logger = logger;
    }
    public static void handleEchoClient(Socket clntSock, Logger logger){
        // Implementacion del protocolo de nivel de aplicacion
    }
    public void run() {
        handleEchoClient(clntSock, logger);
    }

}
