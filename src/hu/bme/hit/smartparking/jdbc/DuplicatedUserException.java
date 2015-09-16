package hu.bme.hit.smartparking.jdbc;

public class DuplicatedUserException extends Exception {

    private static final long serialVersionUID = 6044993918719437082L;

    public DuplicatedUserException() {
        super();
    }

    public DuplicatedUserException(String message) {
        super(message);
    }

    public DuplicatedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedUserException(Throwable cause) {
        super(cause);
    }
}
