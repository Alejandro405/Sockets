package practicas.bolqueII.tftp.tools;

import practicas.bolqueII.tftp.datagram.headers.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import static practicas.bolqueII.tftp.ejecucion.ErrorCodes.TRIES_EXCEED;
import static practicas.bolqueII.tftp.ejecucion.TFTPServer.TFTP_SERVICE_PORT;


/**
 * Clase estática que provee de los métodos necesarios para el envío/recepción de ficheros mediante el protocolo de parada y espera
 */
public class StopAndWaitProtocol {

    private static final Random rng = new Random();


    public static final int MAX_TRIES = 5;

    public static final int TIMEOUT = 1000;
    public static final int MTU = 512;

    private static final HeaderFactory headerFactory = new HeaderFactory();

    /**
     * Delega la transferencia del fichero al método sendFile(DatagramSocket server, File dataFile, InetAddress dstInetAddress, int dstPort)
     * pasando en dstInetAddress y dstPort la información contenida en clientReq
     *
     * @param server    Socket para el envío de segmentos de datos
     * @param dataFile  Fichero a enviar
     * @param clientReq Datagrama recibido con la petición inicial del cliente
     * @return
     * @throws IOException                      En caso de fallo con las operaciones de entrada-salida de ficheros o sockets
     * @throws InterruptedTransmissionException En caso de error durante la transferencia del fichero
     */
    public static TransferStatistics sendFile(DatagramSocket server, File dataFile, DatagramPacket clientReq) throws IOException, InterruptedTransmissionException {

        return sendFile(server, dataFile, clientReq.getAddress(), clientReq.getPort());
    }

    /**
     * Envía el fichero introducido como parámetro, empleando el protocolo de parada y espera
     *
     * @param server         Socket para el envío de segmentos de datos
     * @param dataFile       Fichero a enviar
     * @param dstInetAddress Dirección de red del destinatario del fichero
     * @param dstPort        Puerto de escucha del destinatario
     * @return
     * @throws IOException                      En caso de fallo con las operaciones de entrada-salida de ficheros o sockets
     * @throws InterruptedTransmissionException En caso de error durante la transferencia del fichero
     */
    public static TransferStatistics sendFile(DatagramSocket server, File dataFile, InetAddress dstInetAddress, int dstPort) throws IOException, InterruptedTransmissionException {
        int numLoss = 0, numRetrans = 0;
        byte[] data = Files.readAllBytes(Path.of(dataFile.getPath()));
        DatagramPacket recivPacket;

        DataHeader dataBlock;
        ACKHeader ackHeader;


        boolean loseSegment = false;
        int i = 0, tries = 0, idBlock = 1;
        while (i + MTU < data.length && tries < MAX_TRIES) { // Particiones del fichero == Envío de fichero
            if (rng.nextDouble(1) > 0.6){
                loseSegment = true;
                numLoss++;
            }


            tries = 0;
            boolean confirmado = false ;
            while (!confirmado && tries < MAX_TRIES) // Envío de un bloque del fichero
            {
                dataBlock = headerFactory.getDataHeader((short) idBlock, Arrays.copyOfRange(data, i, i + MTU));
                // Simulacion de pérdida
                if (loseSegment)
                    numRetrans++;
                server.send(dataBlock.encapsulate(dstInetAddress, dstPort));
                server.setSoTimeout(TIMEOUT);
                try{
                    do{
                        recivPacket = new DatagramPacket(new byte[MTU], MTU);
                        server.receive(recivPacket);
                    } while (!isAuthorizedMessager(dstInetAddress, dstPort, recivPacket));
                    checkTransmission(recivPacket);
                    ackHeader = headerFactory.getAckHeader(Arrays.copyOf(recivPacket.getData(), recivPacket.getLength()));
                    if (ackHeader.getBlockId() == idBlock)
                        confirmado = true;

                    //confirmado = line.equalsIgnoreCase("OK"); // Si es un ack paso al siguiente packete
                } catch (SocketTimeoutException e){
                    tries += 1;
                    System.err.println("[ERROR] Tiempo de time-out Superado");
                }


                if (confirmado && i + MTU < data.length){
                    i += MTU;
                    idBlock++;
                }
            }
        }

        if (rng.nextDouble(1) > 60) {
            loseSegment = true;
            numLoss++;
        }

        if (tries >= MAX_TRIES){
            ErrorHeader err = headerFactory.getErrorHeader(TRIES_EXCEED, "[ERROR] Superado intentos de retransmisión");
            server.send(err.encapsulate(dstInetAddress, dstPort));
        } else {
            tries = 0;
            boolean confirmado = false;
            while (!confirmado && tries < MAX_TRIES){
                DataHeader lastDataBlock = headerFactory.getDataHeader((short) idBlock, Arrays.copyOfRange(data, i, i + (data.length - i)));
                if (loseSegment)
                    numRetrans++;
                server.send(lastDataBlock.encapsulate(dstInetAddress, dstPort));
                server.setSoTimeout(TIMEOUT);
                try{
                    do{ // Confirmar env'io
                        recivPacket = new DatagramPacket(new byte[MTU], MTU);
                        server.receive(recivPacket);
                    } while (!isAuthorizedMessager(dstInetAddress, dstPort, recivPacket));
                    checkTransmission(recivPacket);
                    ackHeader = headerFactory.getAckHeader(Arrays.copyOf(recivPacket.getData(), recivPacket.getLength()));
                    if (ackHeader.getBlockId() == idBlock)
                        confirmado = true;
                } catch (SocketTimeoutException e){
                    tries += 1;
                    System.err.println("[ERROR] Tiempo de time-out Superado");
                }
            }
        }

        return new TransferStatistics(numLoss, numRetrans);
    }

    /**
     * Delega la descarga del fichero al método attendDownload(DatagramSocket clientSocket, BufferedOutputStream out, InetAddress srcInetAdres, int srcPort, int numSeq)
     * pasando en dstInetAddress y dstPort la información contenida en clientReq
     * @param clientSocket Socket para la recepción del fichero
     * @param out Buffer Vinculado al fichero donde volcar los segmentos recibidos
     * @param serverInitData Datagrama recibido con la petición inicial del cliente
     * @param numSeq Numeración del primer segmento a recivir
     * @throws IOException En caso de fallo con las operaciones de entrada-salida de ficheros o sockets
     * @throws InterruptedTransmissionException En caso de error durante la transferencia del fichero
     */
    public static void attendDowload(DatagramSocket clientSocket, BufferedOutputStream out, DatagramPacket serverInitData, int numSeq) throws IOException, InterruptedTransmissionException {
        attendDownload(clientSocket, out, serverInitData.getAddress(), serverInitData.getPort(), numSeq);
    }

    /**
     * Descarga el fichero introducido como parámetro, empleando el protocolo de parada y espera
     * @param clientSocket Socket para la recepción del fichero
     * @param out Buffer vinculado al fichero donde volcar los segmentos recibidos
     * @param srcInetAdres Dirección de red de lafuente del fichero
     * @param srcPort Puerto de envío de datos
     * @param numSeq Numeración del primer segmento a recivir
     * @throws IOException En caso de fallo con las operaciones de entrada-salida de ficheros o sockets
     * @throws InterruptedTransmissionException En caso de error durante la transferencia del fichero
     */
    public static void attendDownload(DatagramSocket clientSocket, BufferedOutputStream out, InetAddress srcInetAdres, int srcPort, int numSeq) throws IOException, InterruptedTransmissionException {
        DatagramPacket recivePacket = new DatagramPacket(new byte[MTU], MTU);
        ACKHeader ackHeader;
        DataHeader datablock;

        //int blockSeq = 2; El primer paquete de datos ya fué enviado
        boolean finalizado = false;
        while (!finalizado){
            do{
                clientSocket.receive(recivePacket);// Espero trama con datos
            } while (!isAuthorizedMessager(srcInetAdres, srcPort, recivePacket));
            checkTransmission(recivePacket);
            datablock = headerFactory.getDataHeader(Arrays.copyOf(recivePacket.getData(), recivePacket.getLength()));
            ackHeader = headerFactory.getAckHeader(datablock.getBlockId());
            if (recivePacket.getLength() == MTU && numSeq == datablock.getBlockId()) { // loque de datos -> confirmo recepción
                numSeq++;
            } else {
                finalizado = true;
            }
            clientSocket.send(ackHeader.encapsulate(srcInetAdres, srcPort));

            out.write(datablock.getData());
        }

        out.close();
    }

    /**
     * Método auxiliar para el control de la recepción de datagramas
     * @param srcInetAdres Dirección de red del emisor
     * @param srcPort Puerto emisor
     * @param recivePacket Segmento recibido
     * @return True sii las direcciones pasadas como parámetros concuerdan con las contenidas en los atributos del datagratama
     */
    private static boolean isAuthorizedMessager(InetAddress srcInetAdres, int srcPort, DatagramPacket recivePacket) {
        return srcInetAdres.equals(recivePacket.getAddress())
                && srcPort == recivePacket.getPort();
    }

    /**
     * Dado un segmento enviado por el otro extremo, analiza el contenido del mismo, elevando excepción en función del tipo de error emitido por el extremo opuesto
     * @param recivPacket segmento recibido a analizar
     * @throws IOException En caso de fallo en la desiarialización del segmento
     * @throws InterruptedTransmissionException En caso de que el otro extremo informe de algún tipo de error
     */
    private static void checkTransmission(DatagramPacket recivPacket) throws IOException, InterruptedTransmissionException {
        try {
            Header aux = headerFactory.createHeader(Arrays.copyOf(recivPacket.getData(), recivPacket.getLength()));
            if (aux instanceof ErrorHeader) {
                throw new InterruptedTransmissionException(((ErrorHeader) aux).getErrorMessage());
            }
        } catch (TFTPHeaderFormatException | UnsupportedTFTPOperation e) {
            throw new InterruptedTransmissionException(e.getMessage());
        }
    }
}
