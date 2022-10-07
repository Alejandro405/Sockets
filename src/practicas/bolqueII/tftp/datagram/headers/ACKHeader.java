package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ACKHeader implements Header {
    private static final short opCode = 4;
    private short blockId;

    public ACKHeader(byte[] input) throws IOException {
        decode(this, new DataInputStream(new ByteArrayInputStream(input)));
    }

    protected ACKHeader(short blockId) {
        this.blockId = blockId;
    }

    public ACKHeader() {
    }

    public ACKHeader(DatagramPacket packet) {

    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        res.writeShort(blockId);

        return aux.toByteArray();
    }

    @Override
    public DatagramPacket encapsulate(InetAddress address, int port) {
        return null;
    }

    private static void decode(ACKHeader header, DataInputStream inputStream) throws IOException {
        header.blockId = inputStream.readShort();
    }

    public DatagramPacket getDatagramPacket() {

        return null;
    }
}
