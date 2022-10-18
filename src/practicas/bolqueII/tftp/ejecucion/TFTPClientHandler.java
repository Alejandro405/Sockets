package practicas.bolqueII.tftp.ejecucion;

import practicas.bolqueII.tftp.datagram.headers.*;
import practicas.bolqueII.tftp.tools.InterruptedTransmissionException;
import practicas.bolqueII.tftp.tools.OutOfTriesException;
import practicas.bolqueII.tftp.tools.StopAndWaitProtocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.*;
import java.util.Arrays;

import static practicas.bolqueII.tftp.ejecucion.ErrorCodes.ILLEGAL_OPERATION;
import static practicas.bolqueII.tftp.tools.StopAndWaitProtocol.MTU;


/**
 * Genera los datos necesarios para el envio del fichero mediante el protocoo de parada y espera:
 *      -> Fichero donde se almacenara'an los datos provinientes del servidor extraidos del socket
 *      -> TID de la transaccion para asegurar la autoria de los mensajes que se extraigan del socket
 *      -> Direcciones de nivel de red y transporte del servidor
 */
public class TFTPClientHandler{
    private static final short TRIES_ERROR = 1;
    private static final HeaderFactory headerFactory = new HeaderFactory();
    public static final String MAX_TRIES_ERROR_MSG = "[ERROR] Se han superado el número de intentos de retransmision";
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

    public TFTPClientHandler(DatagramSocket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public TFTPClientHandler(DatagramSocket clientSocket, int portService) {
        this.clientSocket = clientSocket;
        this.serverTID = portService;
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

    public void adttend() throws IOException {
        // Generar petici'on para el servidor
        if (opMode.compareToIgnoreCase("get") == 0) {
            RRQHeader requestMessage = headerFactory.getRRQHeader(fileName, "Byte");

            clientSocket.send(requestMessage.encapsulate(serverName, serverTID));
            attendGetRequest();
        } else if (opMode.compareToIgnoreCase("put") == 0) {
            try {
                WRQHeader requestMessage = headerFactory.getWRQHeader(fileName, "Byte");
                clientSocket.send(requestMessage.encapsulate(serverName, serverTID)); // Servidor a de responder con un ACK(0)

                attendPutRequest();
            } catch (OutOfTriesException e) {
                System.err.println(e.getMessage());
            }
        } else {
            ErrorHeader err = headerFactory.getErrorHeader(ILLEGAL_OPERATION, "[ERROR] Método de operación no soprtado");
            this.clientSocket.send(err.encapsulate(this.serverName, this.serverTID));
        }
    }

    /**
     * Envío de fichero
     * Sender Parada Espera
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
            StopAndWaitProtocol.sendFile(clientSocket, txt, ackRequest);
        } catch (InterruptedTransmissionException e) {
            System.err.println(e.getMessage());
        }

    }


    private void attendGetRequest() throws IOException {
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
