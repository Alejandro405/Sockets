package practicas.bolqueII.tftp.ejecucion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TFTPClient {
    private static final int PORT_SERVICE = 55600;

    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) throws IOException {
        System.out.println(ANSI_YELLOW + "Bienvenido al servicio TFTP:\n"+
                                        "\tFormato de lectura -> mode [<ascii>|<binary>]\n"+
                                        "\tRegristro host de destino -> connect <host>\n"+
                                        "\tLectura/Descarga de fichero -> get <fichero>\n"+
                                        "\tEscritura/envío de fichero -> put <fichero>\n"+
                                        "\tFinalización del servicio -> quit\n");

        String command;
        BufferedReader sys = new BufferedReader(new InputStreamReader(System.in));

        DatagramSocket clientSocket = new DatagramSocket();
        TFTPClientHandler handler = new TFTPClientHandler(clientSocket, PORT_SERVICE);
        boolean finalizado = false;
        do {
            System.out.print(">");
            command = sys.readLine();
            try {
                if (command.contains("mode")) {
                    //Actualizar atributo mode
                    handler.setMode(command);
                    System.out.println("Comando atendido con éxito");
                } else if (command.contains("connect")) {
                    // Cear un nuevo ClientHandler sobrescribiendo el anterior
                    String [] aux = command.split(" ");
                    handler.setServerName(InetAddress.getByName(aux[1]));
                    System.out.println("Conexion establecida");
                } else if (command.contains("get")) {
                    String[] aux = command.split(" ");

                    // Cambiar modo de operación

                    handler.setOpMode("get");
                    handler.setCommand(command);
                    handler.setFileName(aux[1]);
                    // Ejecutar peticion
                    handler.adttend();
                } else if (command.contains("put")) {
                    String[] aux = command.split(" ");

                    // Cambiar modo de operación

                    handler.setOpMode("put");
                    handler.setCommand(command);
                    handler.setFileName(aux[1]);
                    // Ejecutar peticion
                    handler.adttend();
                } else if (command.contains("quit")) {
                    finalizado = true;
                    handler = null;
                } else {
                    System.err.println("Comando no soportado, pruebe de nuevo");
                }
            } catch (NullPointerException e) {
                System.err.println("Error no se ha establecido conexion."+e.getMessage());
            }


        } while (!finalizado);
    }
}
