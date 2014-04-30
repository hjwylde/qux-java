package qux.lang;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class InternalError extends Error {

    public InternalError() {
        super();
    }

    public InternalError(String message) {
        super(message);
    }

    public InternalError(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalError(Throwable cause) {
        super(cause);
    }
}
