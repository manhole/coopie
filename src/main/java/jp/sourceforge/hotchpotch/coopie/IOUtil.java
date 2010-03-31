package jp.sourceforge.hotchpotch.coopie;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;

public class IOUtil {

    private static final Logger logger = LoggerFactory.getLogger();

    public static void closeNoException(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                logger.warn("closing failure", e);
            }
        }
    }

}
