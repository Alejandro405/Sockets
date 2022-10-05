package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import javax.xml.crypto.Data;
import java.io.*;

public class HeaderFactory implements AbstractTFTPHeaderFactory {
    private static final byte DELIMITER = 0;
    private static final int ACK_DATA_LENGTH = 500;

    private static final short ACK_OPCODE = 4;
    private static final short ERROR_OPCODE = 5;
    private static final short DATA_OPCODE = 3;
    private static final short WRQ_OPCODE = 2;
    private static final short RRQ_OPCODE = 1;

    @Override
    public ACKHeader getAckHeader(short blockId) {
        return new ACKHeader(blockId);
    }

    @Override
    public DataHeader getDataHeader(short blockId, byte[] data) {
        return new DataHeader(blockId, data);
    }

    @Override
    public ErrorHeader getErrorHeader(short errorCode, String errorMsg) {
        return new ErrorHeader(errorCode, errorMsg);
    }

    @Override
    public Header createHeader(byte[] input) throws IOException, TFTPHeaderFormatException, UnsupportedTFTPOperation {
        int n = input.length;
        short opcode;
        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(input));
        Header newHeader = null;

        reader.skipBytes(n - 2);
        opcode = reader.readShort();

        if (opcode == RRQ_OPCODE) {
            newHeader = new RRQHeader(input);
        } else if (opcode == WRQ_OPCODE) {
            newHeader = new WRQHeader(input);
        } else if (opcode == DATA_OPCODE) {
            newHeader = new DataHeader(input);
        } else if (opcode == ACK_OPCODE) {
            newHeader = new ACKHeader(input);
        } else if (opcode == ERROR_OPCODE) {
            newHeader = new ErrorHeader(input);
        } else {
            throw new UnsupportedTFTPOperation("No existe soporte para la operación deseada: "+opcode);
        }

        return newHeader;
    }
}
