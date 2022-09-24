package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.IOException;

/**
 * Orden de las cabeceras de la torre de protocolos
 *  ----------------------------------------------------
 *  | Local Medium | Internet | Datagram | TFTP Opcode |
 *  ----------------------------------------------------
 *
 */
public interface Header {
    static final byte DELIMITER = 0;


    /**
     * Concatena todos los campos de la cabecera del tipo concreto de paquete
     * @return Array de bytes que almacena los campos de la cabecera de forma secuencial, con el formato <TFTP><OpCode>
     */
    byte[] compactHeader() throws IOException;

}
