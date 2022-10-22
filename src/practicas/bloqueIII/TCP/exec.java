package practicas.bloqueIII.TCP;

import java.io.IOException;

public class exec {
    public static void main(String[] args) throws IOException {
        echoTCPServer server = new echoTCPServer();
        server.start();
        echoTCPClient client = new echoTCPClient("localhost");
        client.start();
    }
}
