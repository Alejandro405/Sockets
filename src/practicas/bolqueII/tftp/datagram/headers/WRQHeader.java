package practicas.bolqueII.tftp.datagram.headers;

import practicas.bolqueII.tftp.tools.TFTPHeaderFormatException;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WRQHeader extends RequestHeader {
    public WRQHeader(byte[] input) throws IOException, TFTPHeaderFormatException {
        super(input);
    }

    public WRQHeader(String fileName, String mode) {
        super(fileName, mode, (short) 2);
    }

    public WRQHeader() {
        super();
    }
}
