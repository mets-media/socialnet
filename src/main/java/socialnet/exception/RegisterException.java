package socialnet.exception;

public class RegisterException extends RuntimeException {
    public RegisterException(String errorMessage) {
        super(errorMessage);
    }
}
