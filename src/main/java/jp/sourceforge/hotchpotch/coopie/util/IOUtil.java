package jp.sourceforge.hotchpotch.coopie.util;

public class IOUtil {

    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final String CR_S = Character.toString(CR);
    public static final String LF_S = Character.toString(LF);
    public static final String CRLF = CR_S + LF_S;

    private static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    public static String getSystemLineSeparator() {
        return LINE_SEPARATOR;
    }

}
