package teoria.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TCPEchoServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7890);
        Logger logger = Logger.getLogger("servidorDST-C3");
        Executor service = Executors.newCachedThreadPool();

        while (true){
            service.execute(new EchoProtocol(serverSocket.accept(), logger));
        }
    }
}
