package practicas.bloqueIII.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class echoTCPClient extends Thread{
    private final Socket serverSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    public echoTCPClient(String serverName) throws IOException {
        this.serverSocket = new Socket(serverName, echoTCPServer.puerto);
        this.out = new PrintWriter(serverSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    }

    @Override
    public void run(){
        /*try {
            serverSocket=new Socket(SERVER_NAME,puerto);
            System.out.println("Establecida Conexion");

            out=new PrintWriter(serverSocket.getOutputStream(), true);
            in=new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error con el servidor: " + SERVER_NAME);
            System.exit(1);
        }*/
        try (serverSocket){
            boolean endService = false;
            String userInput;
            while (!endService){
                do {
                    userInput = stdin.readLine();
                } while (userInput != null);

                if (!userInput.equals(".")){
                    out.println(userInput);
                    System.out.println("echo: " + in.readLine());
                } else {
                    endService = true;
                }

            }
            out.close();
            in.close();
            stdin.close();
        }catch(Exception e){
            System.out.println("El cliente esta cerrado en el servidor");
            System.exit(1);
        }
    }
}
