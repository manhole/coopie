package jp.sourceforge.hotchpotch.coopie.csv;

public enum QuoteMode {

    /**
     * 常にクォートする
     */
    ALWAYS,

    /**
     * null値以外はクォートする
     */
    ALWAYS_EXCEPT_NULL,

    /**
     * 必要なときのみクォートする
     */
    MINIMUM

}
