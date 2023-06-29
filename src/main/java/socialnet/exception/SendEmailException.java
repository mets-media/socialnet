package socialnet.exception;

public class SendEmailException extends RuntimeException {
    public SendEmailException(String errorMessage) {
        super(errorMessage);
    }
}
