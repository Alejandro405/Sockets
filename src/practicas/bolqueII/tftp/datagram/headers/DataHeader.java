package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class DataHeader implements Header {
    private static final short opCode = 3;
    private short blockId;
    private byte[] data;

    public DataHeader(byte[] input) throws IOException {
        decode(this, new DataInputStream(new ByteArrayInputStream(input)));
    }

    protected DataHeader(short blockId, byte[] data) {
        this.blockId = blockId;
        this.data = data;
    }

    public DataHeader() {
    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        res.writeShort(blockId);
        res.write(data);

        return aux.toByteArray();
    }

    @Override
    public DatagramPacket encapsulate(InetAddress address, int port) {
        return null;
    }

    private static void decode(DataHeader header, DataInputStream inputStream) throws IOException {
        header.blockId = inputStream.readShort();
        header.data = inputStream.readAllBytes();
    }
}
