package practicas.bolqueII.tftp.ejecucion;

import practicas.bolqueII.tftp.datagram.headers.*;
import practicas.bolqueII.tftp.tools.InterruptedTransmissionException;
import practicas.bolqueII.tftp.tools.OutOfTriesException;
import practicas.bolqueII.tftp.tools.StopAndWaitProtocol;
import practicas.bolqueII.tftp.tools.TransferStatistics;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.*;
import java.util.Arrays;

import static practicas.bolqueII.tftp.ejecucion.ErrorCodes.ILLEGAL_OPERATION;
import static practicas.bolqueII.tftp.ejecucion.TFTPServer.TFTP_SERVICE_PORT;
import static practicas.bolqueII.tftp.tools.StopAndWaitProtocol.MTU;


/**
 * Clase encargada del manejo de las peticiones introducidas de teclado. Encapsula los datos necesarios para las transacciones
 * entre cliente y servidor
 */
public class TFTPClientHandler{
    private static final HeaderFactory headerFactory = new HeaderFactory();
    private String command;
    private InetAddress serverName;
    private int serverTID;
    private String mode;
    private String opMode;
    private DatagramSocket clientSocket;
    private String fileName;
    private static final String sFolder = System.getProperty("user.dir");



    public TFTPClientHandler(String command, InetAddress serverName, String mode, String opMode, DatagramSocket clientSocket, String fileName) {
        this.command = command;
        this.serverName = serverName;
        this.mode = mode;
        this.opMode = opMode;
        this.clientSocket = clientSocket;
        this.fileName = fileName;
    }

    public TFTPClientHandler(InetAddress byName) {
        serverName = byName;
    }

    public TFTPClientHandler(DatagramSocket clientSocket){
        this.clientSocket = clientSocket;
    }

    public TFTPClientHandler(DatagramSocket clientSocket, int portService) {
        this.clientSocket = clientSocket;
        this.serverTID = portService;
    }

    /**
     * Desencadena la ejecución del tratamiento de la petición del cliente, usando los datos de la conexión almacenados en el propio objeto
     * En caso de no instanciar los atributos del objeto se mostrará un mensaje de error y se volverá a la ejecución normal del servidor
     * @throws IOException en caso de fallo en la entrada salida sobre los ficheros o sockets
     */
    public void adttend() throws IOException {
        if (this.serverName == null || serverTID == 0) {
            System.err.println("[ERROR] peticion get/put sin establecimiento de conexi'on");
            return;
        }

        // Generar petici'on para el servidor
        if (opMode.compareToIgnoreCase("get") == 0) {
            RRQHeader requestMessage = headerFactory.getRRQHeader(fileName, "Byte");

            clientSocket.send(requestMessage.encapsulate(serverName, serverTID));
            attendGetRequest();
            System.out.println("Comando atendido con éxito");
            this.serverTID = TFTP_SERVICE_PORT;
        } else if (opMode.compareToIgnoreCase("put") == 0) {
            try {
                WRQHeader requestMessage = headerFactory.getWRQHeader(fileName, "Byte");
                clientSocket.send(requestMessage.encapsulate(serverName, serverTID)); // Servidor a de responder con un ACK(0)

                attendPutRequest();
                System.out.println("Comando atendido con éxito");
                this.serverTID = TFTP_SERVICE_PORT;
            } catch (OutOfTriesException e) {
                System.err.println(e.getMessage());
            }
        } else {
            ErrorHeader err = headerFactory.getErrorHeader(ILLEGAL_OPERATION, "[ERROR] Método de operación no soprtado");
            this.clientSocket.send(err.encapsulate(this.serverName, this.serverTID));
        }


    }

    /**
     * Encapsula y ejecuta la lógica del tratamiento de las peticiones de escritura del usuario (descarga de fichero).
     * @throws IOException en caso de fallo en la entrada salida sobre los ficheros o sockets
     * @throws OutOfTriesException En caso de que se hallan superados los intentos de retransmisión. En dicho caso se interpretará como que el otro extremo a dejado de escuchar las comunicaciones
     */
    private void attendPutRequest() throws IOException, OutOfTriesException {
        File txt = new File(sFolder+"/TFTPClient/data/"+fileName);
        if (!txt.exists())
            throw new NoSuchFileException("ERROR: Fichero no encontrado para el envío");


        // Reestableciendo TID
        DatagramPacket ackRequest = new DatagramPacket(new byte[MTU], MTU);

        do {
            clientSocket.receive(ackRequest);
        } while (!ackRequest.getAddress().equals(serverName));
        serverTID = ackRequest.getPort();


        try {
            TransferStatistics transferStatistics = StopAndWaitProtocol.sendFile(clientSocket, txt, ackRequest);
            System.out.println(transferStatistics);
        } catch (InterruptedTransmissionException e) {
            System.err.println(e.getMessage());
            txt.delete();
        }

    }

    /**
     * Encapsula y ejecuta la lógica del tratamiento de las peticiones de escritura del usuario (descarga de fichero).
     *
     */
    private void attendGetRequest() {
        File strFile = new File(sFolder
                + "/TFTPClient/sessions/"
                + fileName);

        try (FileOutputStream outFile = new FileOutputStream(strFile)){
            BufferedOutputStream out = new BufferedOutputStream(outFile);
            DatagramPacket ackRequest = new DatagramPacket(new byte[MTU], MTU);
            clientSocket.receive(ackRequest); //Confirmacion de la peticion ->

            DataHeader firstDatablock = headerFactory.getDataHeader(Arrays.copyOf(ackRequest.getData(), ackRequest.getLength()));
            serverName = ackRequest.getAddress();
            serverTID = ackRequest.getPort();

            out.write(firstDatablock.getData());

            ACKHeader ack = headerFactory.getAckHeader(firstDatablock.getBlockId());
            clientSocket.send(ack.encapsulate(ackRequest.getAddress(), ackRequest.getPort()));
            try {
                StopAndWaitProtocol.attendDowload(clientSocket, out, ackRequest, firstDatablock.getBlockId() + 1);
            } catch (InterruptedTransmissionException e) {
                System.err.println(e.getMessage());
                strFile.delete();
            }
        }catch (IOException e ){
            String aux = "[ERROR] " + e.getMessage();
            System.err.println(aux);
        }


    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setOpMode(String opMode) {
        this.opMode = opMode;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public InetAddress getServerName() {
        return serverName;
    }

    public String getMode() {
        return mode;
    }

    public void setFileName(String aux) {
        this.fileName = aux;
    }

    public void setServerName(InetAddress byName) {
        this.serverName = byName;
    }

    public void  setTID(int portService) {
        this.serverTID = portService;
    }
}
