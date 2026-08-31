/**
 * Represents an error caused by an invalid command given to Kairo.
 */
public class KairoException extends Exception {

    /**
     * Creates an exception with an explanation of the error.
     *
     * @param message explanation of the error
     */
    public KairoException(String message) {
        super(message);
    }
}
