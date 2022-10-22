package practicas.bolqueII.tftp.ejecucion;

import practicas.bolqueII.tftp.datagram.headers.ErrorHeader;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.tools.InterruptedTransmissionException;
import practicas.bolqueII.tftp.tools.StopAndWaitProtocol;
import practicas.bolqueII.tftp.tools.TransferStatistics;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static practicas.bolqueII.tftp.datagram.headers.HeaderFactory.RRQ_OPCODE;
import static practicas.bolqueII.tftp.datagram.headers.HeaderFactory.WRQ_OPCODE;
import static practicas.bolqueII.tftp.ejecucion.ErrorCodes.ABORT_TRANSACTION;

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

    public TFTPServerHandler(DatagramSocket socket, InetAddress clientInetAdress, int clientTID) {
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
     * A partir de una petición recibida por el servidor, ejecuta la lógica que atiende dicha petición
     * Previamente a la invocación del método, los atributos de la clase deben ser debidamente actualizados.
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

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTransferMode(String transferMode) {
        this.transferMode = transferMode;
    }

    /**
     * Ejecuta el procedimiento definido por el RFC para el envío de datos por parte del cliente
     */
    private void attendPutClientRequest() {
        File strFile = new File(sFolder.toPath() + "/TFTPServer/sessions/" + fileName);
        try (FileOutputStream outFile = new FileOutputStream(strFile)) {
            StopAndWaitProtocol.attendDownload(socket, new BufferedOutputStream(outFile), clientInetAddress, clientTID, 1);
        } catch (IOException | InterruptedTransmissionException e) {
            interrupTransmission(e.getMessage());
            strFile.delete();
        }
    }

    /**
     * Ejecuta el procedimiento definido por el RFC para la recepción de datos por parte del cliente
     */
    private void attendGetClientRequest() {
        File datafile = new File(sFolder + "/TFTPServer/data/" + fileName);
        try {
            TransferStatistics transferStatistics = StopAndWaitProtocol.sendFile(socket, datafile, clientInetAddress, clientTID);
            System.out.println(transferStatistics);
        } catch (IOException | InterruptedTransmissionException e) {
            interrupTransmission(e.getMessage());
            datafile.delete();
        }
    }

    /**
     * Procedimiento definido por el protocolo a aplicar en caso de situación anómala recogida en la clase ErrorCodes
     * @param e mensaje exacto de error de la excepción elevada durante la ejecución de una transacción
     */
    private void interrupTransmission(String e) {
        ErrorHeader err = headerFactory.getErrorHeader(ABORT_TRANSACTION, "[ERROR] Fallo en la transmision: " + e);
        try {
            socket.send(err.encapsulate(clientInetAddress, clientTID));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
