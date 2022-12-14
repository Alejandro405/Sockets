package practicas.bolqueII.tftp.datagram.headers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Orden de las cabeceras de la torre de protocolos
 *  ----------------------------------------------------
 *  | Local Medium | Internet | Datagram | TFTP Opcode |
 *  ----------------------------------------------------
 *
 */
public interface Header {
    byte DELIMITER = 35;// "#"


    /**
     * Concatena todos los campos de la cabecera del tipo concreto de paquete
     * @return Array de bytes que almacena los campos de la cabecera de forma secuencial, con el formato <TFTP><OpCode>
     */
    byte[] compactHeader() throws IOException;

    /**
     * Dados una dirección de red y un puerto, generar un datagrama UDP que encapsule los datos del mensaje TFTP
     * @param address Dirección de destino de nivel de red
     * @param port puerto de destino nivel de transporte
     * @return datagrama udp listo para el envío
     */
    DatagramPacket encapsulate(InetAddress address, int port);


    short getOpCode();

    String getFileName();
}
