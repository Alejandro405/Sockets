package practicas.bolqueII.tftp.ejecucion;

import practicas.bolqueII.tftp.datagram.headers.Header;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.datagram.headers.RequestHeader;
import practicas.bolqueII.tftp.handlers.TFTPServerHandler;
import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import static practicas.bolqueII.tftp.datagram.headers.HeaderFactory.*;
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

                if (req.getOpCode() == RRQ_OPCODE){
                    String fileName = req.getFileName();
                    handler = new TFTPServerHandler(socket, fileName, RRQ_OPCODE, clientInetAdress, clientTID);
                    handler.attend();
                } else if (req.getOpCode() == WRQ_OPCODE) {
                    String fileName = req.getFileName();
                    handler = new TFTPServerHandler(socket, fileName, WRQ_OPCODE, clientInetAdress, clientTID);
                    handler.attend();
                }else if (req.getOpCode() == DATA_OPCODE) {
                    System.err.println("[WARNING] DataPacket recivido sin inicio de transacci'on");
                }else if (req.getOpCode() == ACK_OPCODE) {
                    System.err.println("[WARNING] AckPacket recivido sin inicio de transacci'on");
                }else if (req.getOpCode() == ERROR_OPCODE) {
                    System.err.println("[WARNING] ErrorPacket recivido sin inicio de transacci'on");
                } else {
                    throw new UnsupportedOperationException("[ERROR] Fallo al recibir petici'on. Petici'on recivida");
                }


            } catch (IOException | UnsupportedTFTPOperation | TFTPHeaderFormatException e) {
                System.err.println(e.getMessage());
            }

        }
    }
}
