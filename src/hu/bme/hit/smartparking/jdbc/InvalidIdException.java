package hu.bme.hit.smartparking.jdbc;

public class InvalidIdException extends Exception {

    private static final long serialVersionUID = 6918883092769788641L;

    public InvalidIdException() {
        super();
    }

    public InvalidIdException(String message) {
        super(message);
    }

    public InvalidIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIdException(Throwable cause) {
        super(cause);
    }
}
