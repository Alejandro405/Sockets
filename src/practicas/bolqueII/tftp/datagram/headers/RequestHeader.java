package practicas.bolqueII.tftp.datagram.headers;

public abstract class RequestHeader implements Header{
    private String fileName;
    private String mode;

    public RequestHeader(String fileName, String mode) {
        this.fileName = fileName;
        this.mode = mode;
    }

    public RequestHeader() {
    }

    public String getFileName(){
        return fileName;
    }

    public String getMode() {
        return mode;
    }


}
