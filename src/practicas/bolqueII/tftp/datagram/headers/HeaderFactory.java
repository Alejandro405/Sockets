package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.datagram.headers.*;
import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;

public abstract class HeaderFactory {

    public static Header getHeader(byte[] input) throws UnsupportedTFTPOperation, TFTPHeaderFormatException, IOException {
        int n = input.length;
        short opcode;
        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(input));
        Header newHeader = null;

        reader.skipBytes(n - 2);
        opcode = reader.readShort();

        if (opcode == 1) {
            newHeader = new RRQHeader(input);
        } else if (opcode == 2) {
            newHeader = new WRQHeader(input);
        } else if (opcode == 3) {
            newHeader = new DataHeader(input);
        } else if (opcode == 4) {
            newHeader = new ACKHeader(input);
        } else if (opcode == 5) {
            newHeader = new ErrorHeader(input);
        } else {
            throw new UnsupportedTFTPOperation("No existe soporte para la operación deseada: "+opcode);
        }

        return newHeader;
    }
}
