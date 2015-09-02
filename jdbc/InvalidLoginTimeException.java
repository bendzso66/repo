public class InvalidLoginTimeException extends Exception {

    private static final long serialVersionUID = 1153999873461016605L;

    public InvalidLoginTimeException() {
        super();
    }

    public InvalidLoginTimeException(String message) {
        super(message);
    }

    public InvalidLoginTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLoginTimeException(Throwable cause) {
        super(cause);
    }
}
