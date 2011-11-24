package jp.sourceforge.hotchpotch.coopie.util;

import java.io.Closeable;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

public class CloseableUtil {

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
