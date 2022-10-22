package practicas.bloqueIII.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class echoTCPServer extends Thread{

    private static final int TIME_OUT = 2000;

    protected static class echoTCPServerHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public echoTCPServerHandler(Socket clientSoc){
            clientSocket = clientSoc;
            boolean repeat = false;
            while (!repeat)
                try {
                    out = new PrintWriter(clientSoc.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    repeat = true;
                }
        }

        @Override
        public void run(){
            System.out.println ("Iniciando nuevo hilo");
            try {
                String inputLine;

                while ((inputLine = in.readLine()) != null){
                    System.out.println ("Server: " + inputLine);
                    out.println(inputLine);
                    if (inputLine.equals("stop")){
                        break;
                    }
                }
                out.close();
                in.close();
                System.out.println(".......Cerrado el Cliente: "+clientSocket.getPort()+"..........");
                clientSocket.close();
            }catch (IOException e){
                System.err.println("Problema con el servidor");
                System.exit(1);
            }

        }
    }

    protected Socket clientSocket;
    public static final int puerto = 55600;

    @Override
     public void run(){
        Executor ejecuto = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(puerto)){
            System.out.println ("Conexion establecida");
            try {
                serverSocket.setSoTimeout(TIME_OUT);
                while (true) {
                    System.out.println ("Esperando para Aceptar Conexion");
                    try {
                        ejecuto.execute(new echoTCPServerHandler(serverSocket.accept()));
                    } catch (SocketTimeoutException ste){
                        System.out.println ("Error: Tiempo acabado");
                    }
                }
            }catch (IOException e){
                System.err.println("Fallo al aceptar conexi'on");
            }
        }catch (IOException e){
            System.err.println("Error:Al establecer conexi'on");
            System.exit(1);
        }
    }
}
