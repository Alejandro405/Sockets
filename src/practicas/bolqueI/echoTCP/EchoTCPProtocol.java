package practicas.bolqueI.echoTCP;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoTCPProtocol implements Runnable{
    private static final int BUFSIZE = 32; // Tamaño buffer de E/S
    private Socket clntSock; // Socket de datos
    private Logger logger; // Logger del servidor
    public EchoTCPProtocol(Socket clntSock, Logger logger) {
        this.clntSock = clntSock;
        this.logger = logger;
    }
    public static void handleEchoClient(Socket client, Logger logger)  {
        // Implementacion del protocolo de nivel de aplicacion
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream()));
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client.getOutputStream())), true);


            while (client.isConnected()) {
                out.println(in.readLine());
            }

            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }


    }

    @Override
    public void run() {
        handleEchoClient(clntSock, logger);
    }

}
