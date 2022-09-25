package practicas.bolqueI.echoTCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class echoTCPServer {
    private static final String FIN = ".";

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(12345);
        Logger logger = Logger.getLogger("schoTCPServer-DST");
        Executor threads = Executors.newCachedThreadPool();

        while (true){
            threads.execute(new EchoTCPProtocol(server.accept(), logger));
        }
    }

    public static void mainSecuencial(String[] args) throws IOException {
        int serverPort = Integer.parseInt(args[0]);
        ServerSocket server = null;
        Socket client = null;
        PrintWriter out = null;
        BufferedReader in = null;

        while (true){
            try {
                //Inic Servidor
                System.out.println("\tInicializando servidor. Espere...");
                server = new ServerSocket(serverPort);

                System.out.println("Servidor inicializado con éxito, esperando petición");
                client = server.accept();
                in = new BufferedReader(
                        new InputStreamReader(
                        client.getInputStream()));
                out = new PrintWriter(
                        new BufferedWriter(
                        new OutputStreamWriter(
                        client.getOutputStream())), true);

                System.out.println("Cliente aceptado con éxito");


                boolean ended = false;
                do {
                    String line = in.readLine();
                    if (line.compareToIgnoreCase(FIN) == 0)
                        ended = true;
                    else
                        out.println(line);

                } while (!ended);


            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                out.close();
                in.close();
            }
        }
    }

}
