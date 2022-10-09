package practicas.bolqueII.tftp.handlers;

import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

    public void attend(){

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTransferMode(String transferMode) {
        this.transferMode = transferMode;
    }
}
