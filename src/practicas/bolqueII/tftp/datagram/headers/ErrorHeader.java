package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ErrorHeader implements Header {
    private static final short opCode = 5;
    private short errorCode;
    private String errorMessage;

    public ErrorHeader(byte[] input) throws IOException {
        decode(this, new DataInputStream(new ByteArrayInputStream(input)), input.length);
    }

    public ErrorHeader(short errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorHeader() {
    }

    protected ErrorHeader(short errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMessage = errorMsg;
    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        res.writeShort(errorCode);
        res.writeChars(errorMessage);
        res.writeByte(DELIMITER);

        return aux.toByteArray();
    }

    @Override
    public DatagramPacket encapsulate(InetAddress address, int port) {
        byte[] TFTPData = null;
        while (TFTPData == null)
        {
            try {
                TFTPData = this.compactHeader();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        return new DatagramPacket(TFTPData, TFTPData.length, address, port);
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    @Override
    public String getFileName() {
        return null;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private static void decode(ErrorHeader header, DataInputStream inputStream, int length) throws IOException {
        inputStream.readShort();
        header.errorCode = inputStream.readShort();
        header.errorMessage = new String(inputStream.readNBytes(length - 5), StandardCharsets.UTF_8);
    }
}
