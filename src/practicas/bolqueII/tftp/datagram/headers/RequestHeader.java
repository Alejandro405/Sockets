package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class RequestHeader implements Header{
    private short opCode;
    private String fileName;
    private String mode;


    public RequestHeader(String fileName, String mode, short opCode) {
        this.fileName = fileName;
        this.mode = mode;
        this.opCode = opCode;
    }

    public RequestHeader(byte[] input) throws IOException, TFTPHeaderFormatException {
        decode(this, input);
    }

    public RequestHeader() {
    }

    @Override
    public byte[] compactHeader() throws IOException {
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        DataOutputStream res = new DataOutputStream(aux);

        res.writeShort(opCode);
        //res.writeChars(fileName);
        res.write(fileName.getBytes(StandardCharsets.UTF_8));
        res.writeByte(DELIMITER);
        //res.writeChars(mode);
        res.write(mode.getBytes(StandardCharsets.UTF_8));
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

    public String getFileName(){
        return fileName;
    }

    public String geOpMode() {
        return mode;
    }

    private static void decode(RequestHeader header, byte[] inputStream) throws IOException, TFTPHeaderFormatException {
        // replaceAll(inputStream, (byte) 0, (byte) -1);
        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(inputStream));
        header.opCode = reader.readShort();

        //Saltamos el opcode
        String line = new String(inputStream, 2, inputStream.length - 2, StandardCharsets.UTF_8);
                //line = reader.readUTF();
        String[] aux = line.split("#");


        String name = aux[0];
        String mode = aux[1];

        header.fileName = name;
        header.mode = mode;
    }

}
