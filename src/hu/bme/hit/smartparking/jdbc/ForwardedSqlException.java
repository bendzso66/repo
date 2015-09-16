package hu.bme.hit.smartparking.jdbc;

public class ForwardedSqlException extends Exception {

    private static final long serialVersionUID = -453486785241680170L;

    public ForwardedSqlException() {
        super();
    }

    public ForwardedSqlException(String message) {
        super(message);
    }

    public ForwardedSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForwardedSqlException(Throwable cause) {
        super(cause);
    }
}
