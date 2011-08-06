package jp.sourceforge.hotchpotch.coopie.csv;

public interface FixedLengthColumn extends ColumnName {

    /**
     * 1行の文字列から、当カラムぶんのデータを読みます。
     */
    String read(String line);

    /**
     * 1カラムぶんのデータから、固定長ファイルへ記述するだけの文字数とし、出力します。
     */
    void write(String s, Appendable appendable);

}
