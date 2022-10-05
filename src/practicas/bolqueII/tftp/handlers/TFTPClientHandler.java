package practicas.bolqueII.tftp.handlers;

import practicas.bolqueII.tftp.datagram.headers.ACKHeader;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.tools.OutOfTriesException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.*;
import java.util.Arrays;


/**
 * Revisar esqueleto pseudocodigo
 * Revisar Refactorizacion código.
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

    private static final int MAX_TRIES = 5;
    private static String sFolder = System.getProperty("user.dir");



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
        if (opMode.compareToIgnoreCase("get") == 0) {
            attendGetRequest();
        } else if (opMode.compareToIgnoreCase("put") == 0) {
            try {
                attendPutRequest();
            } catch (OutOfTriesException e) {
                System.err.println(e.getMessage());
            } finally {
                command = "";
                serverName = null;
                serverTID = -1;
                mode = "";
                opMode = "";
                fileName = "";
            }
        }
    }

    /**
     * Envío de fichero
     * Sender Parada Espera
     */
    private void attendPutRequest() throws IOException, OutOfTriesException {
        File txt = new File(sFolder+"/TFTPClient/data"+fileName);
        if (!txt.exists())
            throw new NoSuchFileException("ERROR: Fichero no encontrado para el envío");

        DatagramPacket packet = null;
        byte[] data = Files.readAllBytes(txt.toPath());
        int i = 0, tries = 0;
        short idBlock = 1;
        while (i < 512 && tries < MAX_TRIES) {
            packet =  new DatagramPacket(data, i, i + 512);headerFactory.getDataHeader(idBlock, Arrays.copyOfRange(data, i, i + 512));
            //packet = HeaderFactory.getDataBlock((short) 3, (short) idBlock, Arrays.copyOfRange(data, i, i + 512));
            clientSocket.send(packet);
            clientSocket.setSoTimeout(1000);
            try{
                do{
                    clientSocket.receive(packet);
                } while (!errorFreeACK(packet, serverTID, serverName));
            } catch (SocketTimeoutException e){
                tries += 1;
                System.err.println("[ERROR] Tiempo de time-out Superado");
            }


        }

        if (tries >= MAX_TRIES){
            //Error numero de intentos superados, finalizar comunicación, recuperar estado.
            //packet = HeaderFactory.getErrorPack(TRIES_ERROR, "[ERROR] Se han superado el número de intentos de retransmision");
            packet = new DatagramPacket(headerFactory
                                            .getErrorHeader(TRIES_ERROR, MAX_TRIES_ERROR_MSG)
                                            .compactHeader()
                                       , MAX_TRIES_ERROR_MSG.length() + 2);
            // El método púlbico será el encargado limpiar el proceso y salvar estado anterior
            throw new OutOfTriesException("[ERROR] Se han superado el número de intentos de retransmision");
        }



    }

    /**
     * Para que sea válido ha de ser un ack, con idBlock correecto, TID de la sesion
     * @param packet
     * @param serverTID
     * @param serverName
     * @return
     */
    private boolean errorFreeACK(DatagramPacket packet, int serverTID, InetAddress serverName) {
        return false;
    }

    /**
     * Recepcion de fichero, modo reciver de parada y espera
     */
    private void attendGetRequest() throws IOException {
        File serverFolder = new File(sFolder + "/TFTPClient/sessions/" + serverTID);
        if (!serverFolder.exists())
            serverFolder.mkdirs();

        FileOutputStream file = new FileOutputStream(serverFolder + fileName);
        BufferedOutputStream out = new BufferedOutputStream(file);
        DatagramPacket packet = new DatagramPacket(new byte[512], 512);
        boolean finalizado = false;
        do {
            clientSocket.receive(packet);
            if (packet.getLength() < 512) {
                finalizado = true;
            } else if (isValid(packet, serverTID, serverName)) {
                out.write(packet.getData());
                ACKHeader ack = headerFactory.getAckHeader((short) -1);
                clientSocket.send(ack.getDatagramPacket());
            } else {
                // Trat de paquete no autorizado
                clientSocket.send(packet);
            }
        } while (!finalizado);

        file.close();
    }

    private boolean isValid(DatagramPacket packet, int serverTID, InetAddress serverName) {
        return false;
    }

}