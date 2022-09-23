package practicas.bolqueII.tftp.datagram.headers;

public class DataHeader extends Header{
    @Override
    public byte[] compactCabecera() {
        return new byte[0];
    }

    @Override
    public Header descompactCabecera(byte[] input) {
        return null;
    }
}
