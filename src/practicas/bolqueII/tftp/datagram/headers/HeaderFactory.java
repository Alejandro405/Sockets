package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;

public class HeaderFactory implements AbstractTFTPHeaderFactory {
    public static final short ACK_OPCODE = 4;
    public static final short ERROR_OPCODE = 5;
    public static final short DATA_OPCODE = 3;
    public static final short WRQ_OPCODE = 2;
    public static final short RRQ_OPCODE = 1;

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
        short opcode;
        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(input));
        Header newHeader;

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

    @Override
    public RRQHeader getRRQHeader(String name, String transferMode) {
        return new RRQHeader(name, transferMode);
    }

    @Override
    public WRQHeader getWRQHeader(String fileName, String aByte) {
        return new WRQHeader(fileName, aByte);
    }

    /**
     * Deserializa un array de bytes en una petición de servicio del cliente
     * @param input Array de bytes para la deserialización
     * @return RequestHeader almacenado en el array de bytes
     * @throws IOException En caso de Fallo en la lectura/escritura de los atributos
     * @throws TFTPHeaderFormatException En caso de que los atributos de la petición no sean correctos o no se presenten en orden adecuado
     * @throws UnsupportedTFTPOperation En caso de que el modo de operación leído en el array no se encuentre soportado por el servidor.
     */
    public RequestHeader createRequestHeader(byte[] input) throws IOException, TFTPHeaderFormatException, UnsupportedTFTPOperation {
        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(input));
        short opCode = reader.readShort();
        RequestHeader newHeader;

        if (opCode == RRQ_OPCODE) {
            newHeader = new RRQHeader(input);
        } else if (opCode == WRQ_OPCODE) {
            newHeader = new WRQHeader(input);
        } else {
            throw new UnsupportedTFTPOperation("No existe soporte para la operación deseada: "+opCode);
        }


        return newHeader;
    }

    /**
     * Deserializa un array de bytes en un objeto de tipo DataHeader
     * @param data Array de bytes para la deserialización
     * @return DataHeader almacenado en el array de bytes
     * @throws IOException En caso de Fallo en la lectura/escritura de los atributos
     */
    public DataHeader getDataHeader(byte[] data) throws IOException {
        return new DataHeader(data);
    }

    /**
     * Deserializa un array de bytes en un objeto de tipo ACKHeader
     * @param copyOf Array de bytes para la deserialización
     * @return ACKHeader almacenado en el array de bytes
     * @throws IOException En caso de Fallo en la lectura/escritura de los atributos
     */
    public ACKHeader getAckHeader(byte[] copyOf) throws IOException {
        return new ACKHeader(copyOf);
    }
}
