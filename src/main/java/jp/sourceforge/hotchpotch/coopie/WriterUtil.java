package jp.sourceforge.hotchpotch.coopie;

import java.io.BufferedWriter;
import java.io.Writer;

public class WriterUtil {

    public static BufferedWriter toBufferedWriter(final Writer w) {
        if (w instanceof BufferedWriter) {
            return (BufferedWriter) w;
        }
        return new BufferedWriter(w);
    }

}
