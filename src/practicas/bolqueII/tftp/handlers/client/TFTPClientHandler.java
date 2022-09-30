package practicas.bolqueII.tftp.handlers.client;

import java.net.InetAddress;

public class TFTPClientHandler{
    private String command;
    private InetAddress serverName;
    private String mode;
    private String opMode;
    private String fileName;

    public TFTPClientHandler(InetAddress byName) {
        serverName = byName;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setOpMode(String opMode) {
        this.opMode = opMode;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public InetAddress getServerName() {
        return serverName;
    }

    public String getMode() {
        return mode;
    }

    public void adttend() {
        if (opMode.compareToIgnoreCase("get") == 0) {
            attendGetRequest();
        } else if (opMode.compareToIgnoreCase("put") == 0) {
            attendPutRequest();
        }
    }

    private void attendPutRequest() {

    }

    private void attendGetRequest() {

    }

}
