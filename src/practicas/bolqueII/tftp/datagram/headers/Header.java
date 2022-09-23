package practicas.bolqueII.tftp.datagram.headers;

/**
 * Orden de las cabeceras de la torre de protocolos
 *  ----------------------------------------------------
 *  | Local Medium | Internet | Datagram | TFTP Opcode |
 *  ----------------------------------------------------
 *
 */
public abstract class Header {
    private short opCode;

    /**
     * Concatena todos los campos de la cabecera del tipo concreto de paquete
     * @return Array de bytes que almacena los campos de la cabecera de forma secuencial, con el formato <TFTP><OpCode>
     */
    public abstract byte[] compactCabecera();

    /**
     * Función inversa a compactCabecera(), dado un array de bytes que almacenan los campos, dividir el array en función del opcode propio de cada operación TFTP
     * @param input array de bytes recibidos del cliente, <TFTP><OpCode>
     * @return cabecera que almacena los campos concatenados del array
     */
    public abstract Header descompactCabecera(byte[] input);
}
