package jp.sourceforge.hotchpotch.coopie.util;

public interface Line {

    /**
     * @return 行の文字列。改行文字は含みません。
     */
    String getBody();

    /**
     * @return 行の文字列。改行文字も含みます。
     */
    String getBodyAndSeparator();

    /**
     * @return 行番号(1行目は0)
     */
    int getNumber();

    /**
     * @return 行の改行文字。
     */
    LineSeparator getSeparator();

    Line reinit(String body, int number, LineSeparator separator);

}
