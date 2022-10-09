package teoria.udp.filetransfer;

import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.tools.OutOfTriesException;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;

public class ServerUDP {
	public static final int ECHOMAX = 255; // Tamagno maximo de los mensajes
	private static final File sFolder = new File(System.getProperty("user.dir") + "/src/teoria/udp/filetransfer/server");
	private static final HeaderFactory headerFactory = new HeaderFactory();
	private static final int MAX_TRIES = 5;

	/**
	 * Cliente solicita un fichero que he de enviar
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		/*
		File txt = new File(sFolder.toPath() + "texto.txt");

		DatagramSocket clientSocket = new DatagramSocket(12345);
		DatagramPacket packet = null;
		byte[] data = Files.readAllBytes(txt.toPath());
		int i = 0, tries = 0;
		short idBlock = 1;
		while (i < 512 && tries < MAX_TRIES) {
			packet =  new DatagramPacket(data, i, i + 512);
			headerFactory.getDataHeader(idBlock, Arrays.copyOfRange(data, i, i + 512));
			//packet = HeaderFactory.getDataBlock((short) 3, (short) idBlock, Arrays.copyOfRange(data, i, i + 512));
			clientSocket.send(packet);
			clientSocket.setSoTimeout(1000);
			try{
				do{
					clientSocket.receive(packet);
				} while (!errorFreeACK(packet, serverTID, serverName));
			} catch (SocketTimeoutException e){
				tries += 1;
				System.err.println("[ERROR] Tiempo de time-out Superado");
			}


		}

		if (tries >= MAX_TRIES){
			//Error numero de intentos superados, finalizar comunicación, recuperar estado.
			//packet = HeaderFactory.getErrorPack(TRIES_ERROR, "[ERROR] Se han superado el número de intentos de retransmision");
			packet = new DatagramPacket(headerFactory
					.getErrorHeader(TRIES_ERROR, MAX_TRIES_ERROR_MSG)
					.compactHeader()
					, MAX_TRIES_ERROR_MSG.length() + 2);
			// El método púlbico será el encargado limpiar el proceso y salvar estado anterior
			throw new OutOfTriesException("[ERROR] Se han superado el número de intentos de retransmision");
		}*/
	}
}
