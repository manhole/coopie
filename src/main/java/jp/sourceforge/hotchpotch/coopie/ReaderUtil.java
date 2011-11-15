package jp.sourceforge.hotchpotch.coopie;

import java.io.BufferedReader;
import java.io.Reader;

public class ReaderUtil {

    public static BufferedReader toBufferedReader(final Reader r) {
        if (r instanceof BufferedReader) {
            return (BufferedReader) r;
        }
        return new BufferedReader(r);
    }

}
