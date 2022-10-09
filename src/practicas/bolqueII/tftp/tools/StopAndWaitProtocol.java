package practicas.bolqueII.tftp.tools;

import practicas.bolqueII.tftp.datagram.headers.ACKHeader;
import practicas.bolqueII.tftp.datagram.headers.DataHeader;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.datagram.headers.RRQHeader;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StopAndWaitProtocol {
    private static final int MAX_TRIES = 5;
    private static final HeaderFactory headerFactory = new HeaderFactory();
    public static final int TAM_FRAME = 512;
    public static final int TIMEOUT = 5000;

    /*public static void attendDowload(DatagramSocket clientSocket, File reciver, DatagramPacket serverData) throws IOException {
        FileOutputStream file = new FileOutputStream(reciver.getPath());
        BufferedOutputStream out = new BufferedOutputStream(file);
        DatagramPacket recivePacket = new DatagramPacket(new byte[512], 512);
        String ack = "OK";
        DatagramPacket ackPacket = new DatagramPacket(ack.getBytes(), 0, ack.length(), serverName, serverPort);
        boolean finalizado = false;
        while (!finalizado){
            clientSocket.receive(recivePacket);
            String line = new String(recivePacket.getData(), recivePacket.getOffset(), recivePacket.getLength(), StandardCharsets.UTF_8);
            if (recivePacket.getLength() == 512) {
                clientSocket.send(ackPacket);
            } else {
                finalizado = true;
            }

            out.write(recivePacket.getData(), recivePacket.getOffset(), recivePacket.getLength());
        }
        out.close();
        file.close();
    }*/

    public static void sendFile(DatagramSocket server, File dataFile, DatagramPacket clientReq) throws IOException {
        /*byte[] data = Files.readAllBytes(dataFile.toPath());

        // Procesamiento de la logica del protocolo
        DataHeader dataPacket = null;
        ACKHeader ackPacket = headerFactory.getAckHeader((short) -1);

        // Envio y recepcion de datos
        DatagramPacket packet = null;
        DatagramPacket resPacket = null;

        int i = 0, tries = 0, idBlock = 1;
        while (i + 512 < data.length && tries < MAX_TRIES) {
            dataPacket = headerFactory.getDataHeader((short) idBlock, Arrays.copyOfRange(data, i, i + 512));
            packet = dataPacket.encapsulate(serverName, serverPort);
            boolean confirmado = false;
            while (!confirmado && tries < MAX_TRIES)
            {
                server.send(packet);
                server.setSoTimeout(1000000);
                try{
                    resPacket = ackPacket.encapsulate(serverName, serverPort);
                    do{
                        server.receive(resPacket);//dataPacket.encapsulate(serverName, serverPort)
                    } while (!packet.getAddress().equals(serverName));
                    String line = new String(resPacket.getData(),resPacket.getOffset(), resPacket.getLength(), StandardCharsets.UTF_8);
                    confirmado = line.equalsIgnoreCase("OK");
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
            String errMsg = "[ERROR] Superadp intentos de retransmisión";
            resPacket = headerFactory.getErrorHeader((short) 2, errMsg).encapsulate(serverName, serverPort);


            // El método púlbico será el encargado limpiar el proceso y salvar estado anterior
            server.send(resPacket);
        } else {
            resPacket = headerFactory.getDataHeader((short) (idBlock + 1), Arrays.copyOfRange(data, i, data.length - i)).encapsulate(serverName, serverPort);

            server.send(resPacket);
        }*/
    }

    public static void attendDownload(DatagramSocket socket, File storageFile, InetAddress dataSourceAddress, int dataSourcePort) throws IOException {
        String ack = "OK";
        FileOutputStream file = new FileOutputStream(storageFile.getPath());
        BufferedOutputStream out = new BufferedOutputStream(file);
        DatagramPacket recivePacket = new DatagramPacket(new byte[TAM_FRAME], TAM_FRAME);



        boolean finalizado = false;
        while (!finalizado){
            socket.receive(recivePacket);
            //String line = new String(recivePacket.getData(), recivePacket.getOffset(), recivePacket.getLength(), StandardCharsets.UTF_8);
            if (recivePacket.getLength() == TAM_FRAME) {
                DataHeader fileSegment = headerFactory.getDataHeader(recivePacket.getData());
                ACKHeader ackPacket = headerFactory.getAckHeader(fileSegment.getBlockId());
                socket.send(ackPacket.encapsulate(dataSourceAddress, dataSourcePort));
            } else {
                finalizado = true;
            }

            out.write(recivePacket.getData(), recivePacket.getOffset(), recivePacket.getLength());
        }
        out.close();
        file.close();
    }



    public static void sendFile(DatagramSocket socket, File txt, InetAddress dstInetAdress, int dstPort) throws IOException {
        byte[] data = Files.readAllBytes(txt.toPath());

        // Procesamiento de la logica del protocolo
        DataHeader dataPacket = null;
        ACKHeader ackPacket = headerFactory.getAckHeader((short) -1);

        // Envio y recepcion de datos
        DatagramPacket packet = null;
        DatagramPacket resPacket = null;

        int i = 0, tries = 0, idBlock = 1;
        while (i + TAM_FRAME < data.length && tries < MAX_TRIES) {
            dataPacket = headerFactory.getDataHeader((short) idBlock, Arrays.copyOfRange(data, i, i + TAM_FRAME));
            packet = dataPacket.encapsulate(dstInetAdress, dstPort);
            boolean confirmado = false;
            while (!confirmado && tries < MAX_TRIES)
            {
                socket.send(packet);
                socket.setSoTimeout(TIMEOUT);
                try{
                    resPacket = ackPacket.encapsulate(dstInetAdress, dstPort);
                    do{
                        socket.receive(resPacket);//dataPacket.encapsulate(serverName, serverTID)
                    } while (!packet.getAddress().equals(dstInetAdress));
                    String line = new String(resPacket.getData(),resPacket.getOffset(), resPacket.getLength(), StandardCharsets.UTF_8);
                    confirmado = line.equalsIgnoreCase("OK");
                } catch (SocketTimeoutException e){
                    tries += 1;
                    System.err.println("[ERROR] Tiempo de time-out Superado");
                }
            }

            if (confirmado && i + TAM_FRAME < data.length){
                i += TAM_FRAME;
                idBlock++;
            }
        }

        if (tries >= MAX_TRIES){
            //Error numero de intentos superados, finalizar comunicación, recuperar estado.
            String errMsg = "[ERROR] Superadp intentos de retransmisión";
            resPacket = headerFactory.getErrorHeader((short) 2, errMsg).encapsulate(dstInetAdress, dstPort);


            // El método púlbico será el encargado limpiar el proceso y salvar estado anterior
            socket.send(resPacket);
        } else {
            resPacket = headerFactory.getDataHeader((short) (idBlock + 1), Arrays.copyOfRange(data, i, data.length - i)).encapsulate(dstInetAdress, dstPort);

            socket.send(resPacket);
        }
    }
}
