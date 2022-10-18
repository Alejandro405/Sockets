package practicas.bolqueII.tftp.ejecucion;

import practicas.bolqueII.tftp.datagram.headers.ErrorHeader;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.datagram.headers.RequestHeader;
import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import static practicas.bolqueII.tftp.datagram.headers.HeaderFactory.*;
import static practicas.bolqueII.tftp.ejecucion.ErrorCodes.ILLEGAL_OPERATION;
import static teoria.udp.filetransfer.ServerUDP.ECHOMAX;

public class TFTPServer {
    private static final HeaderFactory headerFactory = new HeaderFactory();

    public static void main(String[] args) throws SocketException {
        int clientTID;
        InetAddress clientInetAdress;
        DatagramSocket socket = new DatagramSocket(55600);
        DatagramPacket requestPacket = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        TFTPServerHandler handler = null;
        System.out.println("[STATUS] Servidor inicializado");
        while (true) {
            try {
                System.out.println("Extrayendo Petici'on");
                socket.receive(requestPacket); // Recibe un datagrama del cliente
            } catch (IOException e) {
                System.err.println("Fallo al recibir datagrama");
            }
            System.out.println("IP cliente: " + requestPacket.getAddress().getHostAddress() +", Puerto cliente: " + requestPacket.getPort());

            RequestHeader req = null;
            try {
                req = headerFactory.createRequestHeader(Arrays.copyOf(requestPacket.getData(), requestPacket.getLength()));
                clientInetAdress = requestPacket.getAddress();
                clientTID = requestPacket.getPort();
                String fileName = req.getFileName();
                if (req.getOpCode() == RRQ_OPCODE){
                    handler = new TFTPServerHandler(socket, fileName, RRQ_OPCODE, clientInetAdress, clientTID);
                } else if (req.getOpCode() == WRQ_OPCODE) {
                    handler = new TFTPServerHandler(socket, fileName, WRQ_OPCODE, clientInetAdress, clientTID);
                } else if (req.getOpCode() == DATA_OPCODE || req.getOpCode() == ACK_OPCODE || req.getOpCode() == ERROR_OPCODE) {
                    ErrorHeader err  =headerFactory.getErrorHeader(ErrorCodes.UNKNOWN_ID, "[WARNING] DataPacket recivido sin inicio de transacci'on");
                    socket.send(err.encapsulate(clientInetAdress, clientTID));
                } else {
                    ErrorHeader err  =headerFactory.getErrorHeader(ILLEGAL_OPERATION, "[ERROR] Fallo al recibir petici'on. Petici'on recivida");
                    socket.send(err.encapsulate(clientInetAdress, clientTID));
                }

                handler.attend();
            } catch (IOException | UnsupportedTFTPOperation | TFTPHeaderFormatException e) {
                short aux = 0;
                if (e instanceof UnsupportedTFTPOperation)
                    aux = ILLEGAL_OPERATION;

                ErrorHeader err = headerFactory.getErrorHeader(aux, e.getMessage());
                try {
                    socket.send(err.encapsulate(requestPacket.getAddress(), requestPacket.getPort()));
                } catch (IOException ex) {
                    System.err.println("[ERROR] Fallo en el cierre rematuro");;
                }
            }

        }
    }
}
