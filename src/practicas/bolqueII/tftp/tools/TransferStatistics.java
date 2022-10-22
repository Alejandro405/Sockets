package practicas.bolqueII.tftp.tools;

public class TransferStatistics {
    private int numLosses;
    private int numRetransmissions;

    public TransferStatistics(int x, int y) {
        this.numLosses = x;
        this.numRetransmissions = y;
    }

    public TransferStatistics() {
    }

    public int getX() {
        return numLosses;
    }

    public void setX(int x) {
        this.numLosses = x;
    }

    public int getY() {
        return numRetransmissions;
    }

    public void setY(int y) {
        this.numRetransmissions = y;
    }

    @Override
    public String toString() {
        return "statistics{" +
                "NumLosses=" + numLosses +
                ", numRetransmissions=" + numRetransmissions +
                '}';
    }
}
