package jp.sourceforge.hotchpotch.coopie.csv;

public interface ColumnDesc<T> {

    ColumnName getName();

    String getValue(T bean);

    void setValue(T bean, String value);

}
