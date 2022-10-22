package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.IOException;

public interface AbstractTFTPHeaderFactory {
    /**
     * Instancia un mensaje de tipo ACK a partir de la información pasada por parámetro
     * @param blockId Id del bloque de datos a confirmar
     * @return Mensaje de confirmación para el bloque blockId
     */
    ACKHeader getAckHeader(short blockId);

    /**
     * Instancia un mensaje de tipo ACK a partir de la información pasada por parámetro
     * @param blockId Id del bloque de datos a enviar/recibir
     * @param data Array de bytes para el envío o recepción de datos
     * @return Mensaje de datas ligado al array data y de número de identificación blockId
     */
    DataHeader getDataHeader(short blockId, byte[] data);

    /**
     * Instancia un mensaje de error a partir de la información pasada por parámetro
     * @param errorCode Id de la naturaleza del error
     * @param errorMsg Mensaje descriptor del error
     * @return Mensaje de error asociado a errorCode y errorMsg
     */
    ErrorHeader getErrorHeader(short errorCode, String errorMsg);

    /**
     * Deserializa una secuencia de bytes en un objeto de tipo Header, propio de la descripción del protocolo TFTP especificado por el RFC
     * @param data Secuencia de bytes a deserializar
     * @return Objeto deserializado
     * @throws IOException En caso de Fallo en la lectura/escritura de los atributos
     * @throws TFTPHeaderFormatException En caso de que los atributos de la petición no sean correctos o no se presenten en orden adecuado
     * @throws UnsupportedTFTPOperation En caso de que el modo de operación leído en el array no se encuentre soportado por el servidor.
     */
    Header createHeader(byte[] data) throws IOException, TFTPHeaderFormatException, UnsupportedTFTPOperation;// Deserialization

    /**
     * Instancia una petición de lectura a partir de la información pasada por parámetro
     * @param name Nombre del fichero a leer (descargar)
     * @param aByte modo de transferencia de fichero
     * @return RRQHeader con la información necesaria para el manejo de la petición
     */
    RRQHeader getRRQHeader(String name, String aByte);

    /**
     * Instancia una petición de escritura a partir de la información pasada por parámetro
     * @param fileName Nombre del fichero a escribir (enviar)
     * @param aByte modo de transferencia de fichero
     * @return WRQHeader con la información necesaria para el manejo de la petición
     */
    WRQHeader getWRQHeader(String fileName, String aByte);
}
