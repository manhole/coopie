package jp.sourceforge.hotchpotch.coopie.util;

public interface LineSeparator {

    /**
     * @return 改行文字
     */
    String getSeparator();

    LineSeparator CR = new LineSeparatorImpl(IOUtil.CR_S, "<CR>");

    LineSeparator LF = new LineSeparatorImpl(IOUtil.LF_S, "<LF>");

    LineSeparator CRLF = new LineSeparatorImpl(IOUtil.CRLF, "<CRLF>");

    /**
     * 改行文字で終了しない場合。(改行で終了しない最終行など)
     */
    LineSeparator NONE = new LineSeparatorImpl("", "<NONE>");

}
