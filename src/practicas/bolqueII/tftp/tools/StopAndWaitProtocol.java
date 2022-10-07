package practicas.bolqueII.tftp.tools;

import practicas.bolqueII.tftp.datagram.headers.ACKHeader;
import practicas.bolqueII.tftp.datagram.headers.DataHeader;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class StopAndWaitProtocol {
    private static final int MAX_TRIES = 5;
    private static final HeaderFactory headerFactory = new HeaderFactory();

    public static void attendDowload(DatagramSocket clientSocket, File reciver, DatagramPacket serverData) throws IOException {
        FileOutputStream file = new FileOutputStream(reciver.getPath());
        BufferedOutputStream out = new BufferedOutputStream(file);
        DatagramPacket recivePacket = new DatagramPacket(new byte[512], 512);
        String ack = "OK";
        DatagramPacket ackPacket = new DatagramPacket(ack.getBytes(), 0, ack.length(), serverData.getAddress(), serverData.getPort());
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
    }

    public static void sendFile(DatagramSocket server, File dataFile, DatagramPacket clientReq) throws IOException {
        byte[] data = Files.readAllBytes(dataFile.toPath());

        // Procesamiento de la logica del protocolo
        DataHeader dataPacket = null;
        ACKHeader ackPacket = headerFactory.getAckHeader((short) -1);

        // Envio y recepcion de datos
        DatagramPacket packet = null;
        DatagramPacket resPacket = null;

        int i = 0, tries = 0, idBlock = 1;
        while (i + 512 < data.length && tries < MAX_TRIES) {
            /**
            packet =  new DatagramPacket(data
                    , i
                    , 512
                    , clientReq.getAddress()
                    , clientReq.getPort());
             */
            dataPacket = headerFactory.getDataHeader((short) idBlock, Arrays.copyOfRange(data, i, i + 512));
            packet = dataPacket.encapsulate(clientReq.getAddress(), clientReq.getPort());
            boolean confirmado = false;
            while (!confirmado && tries < MAX_TRIES)
            {
                server.send(packet);
                server.setSoTimeout(1000000);
                try{
                    resPacket = ackPacket.encapsulate(clientReq.getAddress(), clientReq.getPort());
                    do{
                        server.receive(resPacket);//dataPacket.encapsulate(clientReq.getAddress(), clientReq.getPort())
                    } while (!packet.getAddress().equals(clientReq.getAddress()));
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
            //packet = HeaderFactory.getErrorPack(TRIES_ERROR, "[ERROR] Se han superado el número de intentos de retransmision");
            String errMsg = "[ERROR] Superadp intentos de retransmisión";
            resPacket = headerFactory.getErrorHeader((short) 2, errMsg).encapsulate(clientReq.getAddress(), clientReq.getPort());


            // El método púlbico será el encargado limpiar el proceso y salvar estado anterior
            server.send(resPacket);
        } else {
            resPacket = headerFactory.getDataHeader((short) (idBlock + 1), Arrays.copyOfRange(data, i, data.length - i)).encapsulate(clientReq.getAddress(), clientReq.getPort());

            server.send(resPacket);
        }
    }

    public static void attendDowload(DatagramSocket clientSocket, File serverFolder, InetAddress serverName, int serverTID) {
    }

    public static void sendFile(DatagramSocket clientSocket, File txt, InetAddress serverName, int serverTID) {
    }
}
