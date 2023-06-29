package socialnet.exception;

public class EmptyEmailException extends RuntimeException {

    public EmptyEmailException(String errorMessage) {
        super(errorMessage);
    }
}
