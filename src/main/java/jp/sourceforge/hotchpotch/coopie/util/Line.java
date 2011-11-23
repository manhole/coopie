package jp.sourceforge.hotchpotch.coopie.util;

public interface Line {

    /**
     * @return 行の文字列。改行文字は含みません。
     */
    String getBody();

    /**
     * @return 行番号(1行目は0)
     */
    int getNumber();

    LineSeparator getSeparator();

}
