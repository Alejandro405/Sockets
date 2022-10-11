package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class RRQHeader extends RequestHeader {
    public RRQHeader(byte[] input) throws IOException, TFTPHeaderFormatException {
        super(input);
    }

    public RRQHeader(String fileName, String mode) {
        super(fileName, mode, (short) 1);
    }

    public RRQHeader() {
        super();
    }
}
