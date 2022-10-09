package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.IOException;

public interface AbstractTFTPHeaderFactory {
    ACKHeader getAckHeader(short blockId);

    DataHeader getDataHeader(short blockId, byte[] data);

    ErrorHeader getErrorHeader(short errorCode, String errorMsg);

    Header createHeader(byte[] data) throws IOException, TFTPHeaderFormatException, UnsupportedTFTPOperation;// Deserialization

    RRQHeader getRRQHeader(String name, String aByte);


}
