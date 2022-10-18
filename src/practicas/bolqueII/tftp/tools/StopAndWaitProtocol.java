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

import static practicas.bolqueII.tftp.ejecucion.ErrorCodes.TRIES_EXCEED;

public class StopAndWaitProtocol {
    public static final int MAX_TRIES = 5;
    private static final HeaderFactory headerFactory = new HeaderFactory();
    public static final int TIMEOUT = 1000;
    public static final int MTU = 512;

    public static void sendFile(DatagramSocket server, File dataFile, DatagramPacket clientReq) throws IOException, InterruptedTransmissionException {
        sendFile(server, dataFile, clientReq.getAddress(), clientReq.getPort());
    }

    public static void sendFile(DatagramSocket server, File dataFile, InetAddress dstInetAddress, int dstPort) throws IOException, InterruptedTransmissionException {

        byte[] data = Files.readAllBytes(Path.of(dataFile.getPath()));
        DatagramPacket sendPacket = null;
        DatagramPacket recivPacket = null;

        DataHeader dataBlock;
        ACKHeader ackHeader;

        int i = 0, tries = 0, idBlock = 1;
        while (i + MTU < data.length && tries < MAX_TRIES) {
            tries = 0;
            boolean confirmado = false;
            while (!confirmado && tries < MAX_TRIES)
            {
                dataBlock = headerFactory.getDataHeader((short) idBlock, Arrays.copyOfRange(data, i, i + MTU));
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

        if (tries >= MAX_TRIES){
            ErrorHeader err = headerFactory.getErrorHeader(TRIES_EXCEED, "[ERROR] Superado intentos de retransmisión");
            server.send(err.encapsulate(dstInetAddress, dstPort));
        } else {
            DataHeader lastDataBlock = headerFactory.getDataHeader(Arrays.copyOfRange(data, i, data.length - i));
            tries = 0;
            boolean confirmado = false;
            do {
                server.send(lastDataBlock.encapsulate(dstInetAddress, dstPort));
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
                } catch (SocketTimeoutException e){
                    tries += 1;
                    System.err.println("[ERROR] Tiempo de time-out Superado");
                }
            } while (!confirmado && tries < MAX_TRIES);

        }
    }

    private static void checkTransmission(DatagramPacket recivPacket) throws IOException, InterruptedTransmissionException {
        try {
            Header aux = headerFactory.createHeader(Arrays.copyOf(recivPacket.getData(), recivPacket.getLength()));
            if (aux instanceof ErrorHeader) {
                throw new InterruptedTransmissionException(((ErrorHeader) aux).getErrorMessage());
            }
        } catch (TFTPHeaderFormatException | UnsupportedTFTPOperation e) {
            throw new RuntimeException(e);
        }
    }

    public static void attendDowload(DatagramSocket clientSocket, BufferedOutputStream out, DatagramPacket serverInitData, int numSeq) throws IOException, InterruptedTransmissionException {
        attendDownload(clientSocket, out, serverInitData.getAddress(), serverInitData.getPort(), numSeq);
    }

    public static void attendDownload(DatagramSocket clientSocket, BufferedOutputStream out, InetAddress srcInetAdres, int srcPort, int numSeq) throws IOException, InterruptedTransmissionException {
        DatagramPacket recivePacket = new DatagramPacket(new byte[MTU], MTU);
        String ack = "OK";
        DatagramPacket ackPacket = new DatagramPacket(ack.getBytes(), 0, ack.length(), srcInetAdres, srcPort);
        ACKHeader ackHeader = null;
        DataHeader datablock = null;

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
                clientSocket.send(ackHeader.encapsulate(srcInetAdres, srcPort));
                numSeq++;
            } else {
                finalizado = true;
            }


            out.write(datablock.getData());
        }

        out.close();
    }

    private static boolean isAuthorizedMessager(InetAddress srcInetAdres, int srcPort, DatagramPacket recivePacket) {
        return srcInetAdres.equals(recivePacket.getAddress())
                && srcPort == recivePacket.getPort();
    }
}
