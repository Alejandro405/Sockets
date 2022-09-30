package practicas.bolqueII.tftp.handlers.server;

import java.net.DatagramSocket;

/**
 *   Creating an object of FileInputStream to read from a file
 *         FileInputStream fl = new FileInputStream(file);
 *
 *           Now creating byte array of same length as file
 *         byte[] arr = new byte[(int)file.length()];
 *
 *           Reading file content to byte array
 *           using standard read() method
 *         fl.read(arr);
 *
 *           lastly closing an instance of file input stream
 *           to avoid memory leakage
 *         fl.close();
 */
public interface Handler {
    void atenderPeticion(DatagramSocket serverSocket);
}
