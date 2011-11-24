package jp.sourceforge.hotchpotch.coopie.csv;

public interface FixedLengthColumnDesc {

    /**
     * 1行の文字列から、当カラムぶんのデータを読みます。
     */
    String read(CharSequence line);

    /**
     * 1カラムぶんのデータから、固定長ファイルへ記述するだけの文字数とし、出力します。
     */
    void write(CharSequence elem, Appendable appendable);

}
