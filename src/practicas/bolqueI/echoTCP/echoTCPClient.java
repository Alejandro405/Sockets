package practicas.bolqueI.echoTCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class echoTCPClient {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) throws IOException {
        InetAddress serverAdress = InetAddress.getByName(args[0]);
        int serverPort = Integer.parseInt(args[1]);
        Socket socket = new Socket(serverAdress, serverPort);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        PrintWriter out = new PrintWriter(
                                new BufferedWriter(
                                new OutputStreamWriter(
                                socket.getOutputStream())),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Bienvenido al servicio de ECHO.Introduzca texto, <END> para finalizar el servicio\n");

        boolean ended = false;
        do {
            String line = console.readLine();
            if (line.compareToIgnoreCase(".") != 0){
                out.println(line);
                String echoLine = in.readLine();
                System.out.println(ANSI_BLUE + echoLine);
            } else {
                ended = true;
            }

        } while (!ended);


        console.close();
        out.close();
        in.close();
        socket.close();
    }
}
