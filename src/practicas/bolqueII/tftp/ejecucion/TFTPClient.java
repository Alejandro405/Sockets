package practicas.bolqueII.tftp.ejecucion;

import practicas.bolqueII.tftp.handlers.client.TFTPClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class TFTPClient {
    private static final int PORT_SERVICE = 12345;
    private static final String FIN_SERVICE = "quit";

    public static void main(String[] args) throws IOException {
        System.out.println("Bienvenido al servicio TFTP:\n"+
                                "\tFormato de lectura -> mode [<ascii>|<binary>]\n"+
                                "\tRegristro host de destino -> connect <host>\n"+
                                "\tLectura/Descarga de fichero -> get <fichero>\n"+
                                "\tEscritura/envío de fichero -> put <fichero>\n"+
                                "\tFinalización del servicio -> quit\n");

        String command = "";
        BufferedReader sys = new BufferedReader(new InputStreamReader(System.in));


        TFTPClientHandler handler = null;
        boolean finalizado = false;
        do {
            command = sys.readLine();
            try {
                if (command.contains("mode")) {
                    //Actualizar atributo mode
                    handler.setMode(command);
                } else if (command.contains("connect")) {
                    // Cear un nuevo ClientHandler sobrescribiendo el anterior
                    String [] aux = command.split(" ");
                    handler = new TFTPClientHandler(InetAddress.getByName(aux[1]));
                    System.out.println("Conexion establecida");
                } else if (command.contains("get")) {
                    // Cambiar modo de operación
                    handler.setOpMode(command);
                    handler.setCommand(command);
                    // Ejecutar peticion
                    handler.adttend();
                } else if (command.contains("put")) {
                    //Cabiar modo de operacion
                    handler.setOpMode(command);
                    handler.setCommand(command);
                    // Ejecutar peticion
                    handler.adttend();
                } else if (command.contains("quit")) {
                    finalizado = true;
                    handler = null;
                } else {
                    System.out.println("Comando no soportado, pruebe de nuevo");
                }
            } catch (NullPointerException e) {
                System.err.println("Error no se ha establecido conexion."+e.getMessage());
            }

        } while (!finalizado);
    }
}
