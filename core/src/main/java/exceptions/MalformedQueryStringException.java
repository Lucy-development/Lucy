package exceptions;

/**
 * Created on 04/03/2016.
 */
public class MalformedQueryStringException extends RuntimeException {

    public MalformedQueryStringException() {
        super();
    }

    public MalformedQueryStringException(String message) {
        super(message);
    }
}
