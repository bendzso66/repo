package hu.bme.hit.smartparking.jdbc;

public class NotLoggedInException extends Exception {

    private static final long serialVersionUID = -3045144497357043959L;

    public NotLoggedInException() {
        super();
    }

    public NotLoggedInException(String message) {
        super(message);
    }

    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLoggedInException(Throwable cause) {
        super(cause);
    }
}
