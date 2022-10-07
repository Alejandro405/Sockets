package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WRQHeader implements Header {
    private static final short opCode = 2;
    private String fileName;
    private String Mode;

    public WRQHeader(byte[] input) throws IOException, TFTPHeaderFormatException {
        decode(this, new DataInputStream(new ByteArrayInputStream(input)));
    }

    public WRQHeader(String fileName, String mode) {
        this.fileName = fileName;
        Mode = mode;
    }

    public WRQHeader() {
    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        res.writeChars(fileName);
        res.writeShort(DELIMITER);
        res.writeChars(Mode);
        res.writeShort(DELIMITER);

        return aux.toByteArray();
    }

    @Override
    public DatagramPacket encapsulate(InetAddress address, int port) {
        return null;
    }

    private static void decode(WRQHeader header, DataInputStream inputStream) throws IOException, TFTPHeaderFormatException {
        inputStream.skipBytes(2);
        String mode_name = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String[] aux = mode_name.split(String.valueOf(DELIMITER));

        if (aux.length == 2){
            header.fileName = aux[0];
            header.Mode = aux[1];
        } else {
            throw new TFTPHeaderFormatException("Error: formato de paquete erróneo. <OpCode><Filename><0><Mode><0> ["+mode_name+"]");
        }
    }
}
