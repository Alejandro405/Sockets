package practicas.bolqueI.echoUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class echoUDPHandler extends Thread{
    private static final int TAM_PACKETS = 100;
    private static final Charset CODIF_FORMAT = StandardCharsets.UTF_8;
    private DatagramSocket socket;
    private byte[] datos;
    private Logger logger;
    private InetAddress address;
    private int tid;
    private static final String FIN =  ".";

    public echoUDPHandler(DatagramPacket datagram) {
        tid = datagram.getPort();
        address = datagram.getAddress();
        datos = datagram.getData();
    }

    /**
     * La conexión ha de estar preestablecida
     */
    private void handleEchoClient(){
        boolean recived = false;
        boolean finalized = false;
        byte[] dataRecived = new byte[TAM_PACKETS];
        DatagramPacket recivedPaket = new DatagramPacket(dataRecived, dataRecived.length);
        do{

            try {
                socket.receive(recivedPaket);

                if (recivedPaket.getPort() == tid)
                    throw new IOException("ERROR: paquete no deseado");
                else
                    recived = true;

                String line = getLine(recivedPaket);
                finalized = line.compareTo(FIN) == 0;

                DatagramPacket response = new DatagramPacket(line.getBytes(CODIF_FORMAT), line.length());

                boolean successSend = false;
                do{
                    try {
                        socket.send(response);
                        //Confirmar la recepción del msg

                            //socket.receive(response);
                            //if (tid == response.tid && respose.line.compareTo(OK) == 0)
                        successSend = true;
                    } catch (IOException e) {
                    }
                } while (!successSend);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } while (!recived || !finalized);
    }

    private String getLine(DatagramPacket recivedPaket) {
        return new String(recivedPaket.getData()
                , recivedPaket.getOffset()
                , recivedPaket.getLength()
                , CODIF_FORMAT);
    }

    @Override
    public void start() {
        handleEchoClient();
    }
}
