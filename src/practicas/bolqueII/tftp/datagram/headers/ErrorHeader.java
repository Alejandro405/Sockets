package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;
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
        res.writeShort(DELIMITER);

        return aux.toByteArray();
    }

    private static void decode(ErrorHeader header, DataInputStream inputStream, int length) throws IOException {
        header.errorCode = inputStream.readShort();
        header.errorMessage = new String(inputStream.readNBytes(length - 5), StandardCharsets.UTF_8);
    }
}
