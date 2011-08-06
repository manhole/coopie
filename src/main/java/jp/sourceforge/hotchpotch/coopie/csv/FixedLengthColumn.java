package jp.sourceforge.hotchpotch.coopie.csv;

public interface FixedLengthColumn extends ColumnName {

    /**
     * 1行の文字列から、当カラムぶんのデータを読みます。
     */
    String read(String line);

}
