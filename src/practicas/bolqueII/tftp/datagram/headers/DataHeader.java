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

    public short getBlockId() {
        return blockId;
    }

    private static void decode(DataHeader header, DataInputStream inputStream) throws IOException {
        inputStream.readShort();// short correspondiente al opCode
        header.blockId = inputStream.readShort();
        header.data = inputStream.readAllBytes();
    }
}
