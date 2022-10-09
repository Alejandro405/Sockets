package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class RRQHeader implements Header {
    private static final short opCode = 1;
    private String fileName;
    private String mode;
    public RRQHeader(byte[] input) throws IOException, TFTPHeaderFormatException {
        decode(this, input);
    }

    public RRQHeader(String fileName, String mode) {
        this.fileName = fileName;
        this.mode = mode;
    }

    public RRQHeader() {
    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        res.writeChars(fileName);
        res.writeByte(DELIMITER);
        res.writeChars(mode);
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
    public int getOpCode() {
        return opCode;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMode() {
        return mode;
    }

    private static void decode(RRQHeader header, byte[] inputStream) throws IOException, TFTPHeaderFormatException {
       // replaceAll(inputStream, (byte) 0, (byte) -1);
        String line = new String(inputStream, 2, inputStream.length - 2, StandardCharsets.UTF_8);
        String[] aux = line.split("#");


        String name = aux[0];
        String mode = aux[1];

        header.fileName = name;
        header.mode = mode;

    }
}
