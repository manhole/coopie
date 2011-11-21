package jp.sourceforge.hotchpotch.coopie.util;

import java.io.Closeable;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public class IOUtil {

    private static final Logger logger = LoggerFactory.getLogger();

    private static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    public static String getSystemLineSeparator() {
        return LINE_SEPARATOR;
    }

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
