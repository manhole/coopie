package jp.sourceforge.hotchpotch.coopie.csv;

public interface ColumnName {

    /**
     * CSV項目名を返します。
     */
    String getLabel();

    boolean labelEquals(String label);

}
