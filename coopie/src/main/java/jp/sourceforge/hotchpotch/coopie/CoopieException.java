package jp.sourceforge.hotchpotch.coopie;

/**
 * @author manhole
 */
public class CoopieException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CoopieException(final String message) {
        super(message);
    }

    public CoopieException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CoopieException(final Throwable cause) {
        super(cause);
    }

}
