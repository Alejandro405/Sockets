package practicas.bloqueIII;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class echoTCPClient {
    public static void main(String[] args) throws IOException {

        String serverHostname ="localhost";
        int puerto=7;

        Socket socket=null;

        PrintWriter out=null;
        BufferedReader in=null;

        try {
            socket=new Socket(serverHostname,puerto);
            System.out.println("Establecida Conexion");

            out=new PrintWriter(socket.getOutputStream(), true);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error con el servidor: " + serverHostname);
            System.exit(1);
        }
        try{


            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput=stdIn.readLine()) != null){
                if (userInput.equals(".")){
                    break;
                }
                out.println(userInput);
                System.out.println("echo: "+in.readLine());
            }
            out.close();
            in.close();
            stdIn.close();
            socket.close();
        }catch(Exception e){
            System.out.println("El cliente esta cerrado en el servidor");
            System.exit(1);
        }
    }
}
