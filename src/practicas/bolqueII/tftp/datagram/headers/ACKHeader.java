package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class ACKHeader implements Header {
    private static final short opCode = 4;
    private short blockId;

    public ACKHeader(byte[] input) throws IOException {
        decode(this, new DataInputStream(new ByteArrayInputStream(input)));
    }

    public ACKHeader(short blockId) {
        this.blockId = blockId;
    }

    public ACKHeader() {
    }

    public ACKHeader(DatagramPacket packet) throws IOException {
        new ACKHeader(Arrays.copyOf(packet.getData(), packet.getLength()));
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

    public void setBlockId(short blockId) {
        this.blockId = blockId;
    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream(4);
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        res.writeShort(blockId);

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

    /**
     * Genera un datagrama UDP con el contenido del objeto. Este m'etodo no configura direcciones ni puertos
     * @throws IOException fallo en la lectura desde el stream
     */
    public DatagramPacket getDatagramPacket() throws IOException {
        byte[] aux;
        try {
            aux = this.compactHeader();
        } catch (IOException e) {
            throw new IOException("Error al compactar la cabecera para formar el datagrama");
        }
        return new DatagramPacket(aux, aux.length);
    }

    /**
     * Aniade informacion a header desde el data input stream
     * @param header contenedor de informacion
     * @param inputStream proveedor de atriibutos del contenedor
     * @throws IOException en caso de que el inputStream no funcione correctamente
     */
    private static void decode(ACKHeader header, DataInputStream inputStream) throws IOException {
        inputStream.readShort();
        header.blockId = inputStream.readShort();
    }
}
