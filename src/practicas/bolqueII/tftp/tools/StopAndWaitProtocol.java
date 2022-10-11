package practicas.bolqueII.tftp.tools;

import practicas.bolqueII.tftp.datagram.headers.ACKHeader;
import practicas.bolqueII.tftp.datagram.headers.DataHeader;
import practicas.bolqueII.tftp.datagram.headers.Header;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class StopAndWaitProtocol {
    private static final int MAX_TRIES = 5;
    private static final HeaderFactory headerFactory = new HeaderFactory();
    public static final int TAM_FRAME = 512;
    public static final int TIMEOUT = 5000;

    public static void sendFile(DatagramSocket server, File dataFile, DatagramPacket clientReq) throws IOException {
        sendFile(server, dataFile, clientReq.getAddress(), clientReq.getPort());
    }

    public static void sendFile(DatagramSocket server, File dataFile, InetAddress dstInetAddress, int dstPort) throws IOException {

        byte[] data = Files.readAllBytes(Path.of(dataFile.getPath()));


        DatagramPacket packet = null;
        int i = 0, tries = 0, idBlock = 1;
        while (i + 512 < data.length && tries < MAX_TRIES) {
            packet =  new DatagramPacket(data
                    , i
                    , 512
                    , dstInetAddress
                    , dstPort);
            //packet = HeaderFactory.getDataBlock((short) 3, (short) idBlock, Arrays.copyOfRange(data, i, i + 512));
            boolean confirmado = false;
            while (!confirmado && tries < MAX_TRIES)
            {
                server.send(packet);
                server.setSoTimeout(1000);
                try{
                    do{
                        server.receive(packet);
                    } while (!isAuthorizedMessager(dstInetAddress, dstPort, packet));// && packet instanceof ACKHeader()
                    //String line = new String(packet.getData(),packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    ACKHeader ackHeader = headerFactory.getAckHeader(Arrays.copyOf(packet.getData(), packet.getLength()));
                    if (ackHeader.getBlockId() == idBlock)
                        confirmado = true;

                    //confirmado = line.equalsIgnoreCase("OK"); // Si es un ack paso al siguiente packete
                } catch (SocketTimeoutException e){
                    tries += 1;
                    System.err.println("[ERROR] Tiempo de time-out Superado");
                }
            }

            if (confirmado && i + 512 < data.length){
                i += 512;
                idBlock++;
            }
        }

        if (tries >= MAX_TRIES){
            //Error numero de intentos superados, finalizar comunicación, recuperar estado.
            //packet = HeaderFactory.getErrorPack(TRIES_ERROR, "[ERROR] Se han superado el número de intentos de retransmision");
            String errMsg = "[ERROR] Superadp intentos de retransmisión";
            packet = new DatagramPacket(errMsg.getBytes()
                    , errMsg.length()
                    , dstInetAddress
                    , dstPort);


            // El método púlbico será el encargado limpiar el proceso y salvar estado anterior
            server.send(packet);
        } else {
            packet = new DatagramPacket(
                    data
                    , i
                    , data.length - i
                    , dstInetAddress
                    , dstPort
            );

            server.send(packet);
        }
    }

    public static void attendDowload(DatagramSocket clientSocket, BufferedOutputStream out, DatagramPacket serverInitData) throws IOException {
        attendDownload(clientSocket, out, serverInitData.getAddress(), serverInitData.getPort());
    }

    public static void attendDownload(DatagramSocket clientSocket, BufferedOutputStream out, InetAddress srcInetAdres, int srcPort) throws IOException {
        DatagramPacket recivePacket = new DatagramPacket(new byte[512], 512);
        String ack = "OK";
        DatagramPacket ackPacket = new DatagramPacket(ack.getBytes(), 0, ack.length(), srcInetAdres, srcPort);

        boolean finalizado = false;
        while (!finalizado){
            do{
                clientSocket.receive(recivePacket);// Espero trama con datos
            } while (!isAuthorizedMessager(srcInetAdres, srcPort, recivePacket));

            String line = new String(recivePacket.getData(), recivePacket.getOffset(), recivePacket.getLength(), StandardCharsets.UTF_8);
            if (recivePacket.getLength() == 512) { // loque de datos -> confirmo recepción
                clientSocket.send(ackPacket);
            } else {
                finalizado = true;
            }

            out.write(recivePacket.getData(), recivePacket.getOffset(), recivePacket.getLength());
        }
    }

    private static boolean isAuthorizedMessager(InetAddress srcInetAdres, int srcPort, DatagramPacket recivePacket) {
        return srcInetAdres.equals(recivePacket.getAddress())
                && srcPort == recivePacket.getPort();
    }
}
