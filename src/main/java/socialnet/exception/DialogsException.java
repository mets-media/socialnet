package socialnet.exception;

public class DialogsException extends RuntimeException {
    public DialogsException(Throwable cause) {
        super(cause);
    }

    public DialogsException(String message) {
        super(message);
    }

    public DialogsException(String message, Throwable cause) {
        super(message, cause);
    }
}
