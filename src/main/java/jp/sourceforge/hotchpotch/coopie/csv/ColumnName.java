package jp.sourceforge.hotchpotch.coopie.csv;

public interface ColumnName {

    /**
     * オブジェクトのプロパティ名を返します。
     */
    String getName();

    /**
     * CSV項目名を返します。
     */
    String getLabel();

    boolean labelEquals(String label);

}
