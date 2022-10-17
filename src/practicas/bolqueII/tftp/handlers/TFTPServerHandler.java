package practicas.bolqueII.tftp.handlers;

import practicas.bolqueII.tftp.datagram.headers.ErrorHeader;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.tools.InterruptedTransmissionException;
import practicas.bolqueII.tftp.tools.StopAndWaitProtocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static practicas.bolqueII.tftp.datagram.headers.HeaderFactory.RRQ_OPCODE;
import static practicas.bolqueII.tftp.datagram.headers.HeaderFactory.WRQ_OPCODE;
import static practicas.bolqueII.tftp.tools.ErrorCodes.ABORT_TRANSACTION;

public class TFTPServerHandler {
    private static final HeaderFactory headerFactory = new HeaderFactory();
    private static final File sFolder = new File(System.getProperty("user.dir"));

    private DatagramSocket socket;
    private InetAddress clientInetAddress;
    private int clientTID;
    private String fileName;
    private String transferMode;
    private int operationMode;

    public TFTPServerHandler(DatagramSocket socket, InetAddress clientInetAddress, int clientTID, String fileName, String transferMode) {
        this.socket = socket;
        this.clientInetAddress = clientInetAddress;
        this.clientTID = clientTID;
        this.fileName = fileName;
        this.transferMode = transferMode;
    }

    public TFTPServerHandler(DatagramSocket socket, InetAddress clientInetAddress) {
        this.socket = socket;
        this.clientInetAddress = clientInetAddress;
    }

    public TFTPServerHandler() {
    }

    public TFTPServerHandler(DatagramSocket socket, DatagramPacket requestPacket, InetAddress clientInetAdress, int clientTID) {
        this.socket = socket;
        this.clientInetAddress = clientInetAdress;
        this.clientTID = clientTID;

    }

    public TFTPServerHandler(DatagramSocket socket, String fileName, int opCode, InetAddress clientInetAdress, int clientTID) {
        this.socket = socket;
        this.clientInetAddress = clientInetAdress;
        this.clientTID = clientTID;
        this.fileName = fileName;
        this.transferMode = "Byte";
        this.operationMode = opCode;
    }

    /**
     * Atender la petici'on recivida previamente por el servidor
     */
    public void attend()  {
        try (DatagramSocket serviceSocket = new DatagramSocket()) {
            this.socket = serviceSocket;
            if (operationMode == RRQ_OPCODE){
                attendGetClientRequest(); // La confirmacion de recepción de req se realiza con el primer bloque de datos
            } else if (operationMode == WRQ_OPCODE) {
                serviceSocket.send(headerFactory.getAckHeader((short) 0).encapsulate(clientInetAddress, clientTID));
                attendPutClientRequest();
            } else {
                System.err.println(" asdfasdfasf");
            }
        } catch (IOException e) {
            interrupTransmission(e.getMessage());
        }


    }

    /**
     * La peticion WRQ del cliente ha de ser ciobfirmada con el primer bloque de datos
     */
    private void attendPutClientRequest() {
        File strFile = new File(sFolder.toPath() + "/TFTPServer/sessions/" + fileName);
        try (FileOutputStream outFile = new FileOutputStream(strFile)) {
            StopAndWaitProtocol.attendDownload(socket, new BufferedOutputStream(outFile), clientInetAddress, clientTID, 1);
        } catch (IOException | InterruptedTransmissionException e) {
            interrupTransmission(e.getMessage());
        }
    }

    private void attendGetClientRequest() {
        File datafile = new File(sFolder + "/TFTPServer/data/" + fileName);
        try {
            String line = datafile.getPath();
            StopAndWaitProtocol.sendFile(socket, datafile, clientInetAddress, clientTID);
        } catch (IOException | InterruptedTransmissionException e) {
            interrupTransmission(e.getMessage());
        }
    }

    private void interrupTransmission(String e) {
        ErrorHeader err = headerFactory.getErrorHeader(ABORT_TRANSACTION, "[ERROR] Fallo en la transmision: " + e);
        try {
            socket.send(err.encapsulate(clientInetAddress, clientTID));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /*
    [67, 58, 92, 85, 115, 101, 114, 115, 92, 85, 115, 117, 97, 114, 105, 111, 92, 73, 100, 101, 97, 80, 114, 111, 106, 101, 99, 116, 115, 92, 83, 111, 99, 107, 101, 116, 115, 92, 84, 70, 84, 80, 83, 101, 114, 118, 101, 114, 92, 100, 97, 116, 97, 92, 0, 116, 0, 101, 0, 120, 0, 116, 0, 111, 0, 46, 0, 116, 0, 120, 0, 116]
     */

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTransferMode(String transferMode) {
        this.transferMode = transferMode;
    }
}
