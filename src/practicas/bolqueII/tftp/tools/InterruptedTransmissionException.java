package practicas.bolqueII.tftp.tools;

public class InterruptedTransmissionException extends Throwable {
    public InterruptedTransmissionException(String errorMessage) {
        super(errorMessage);
    }
}
