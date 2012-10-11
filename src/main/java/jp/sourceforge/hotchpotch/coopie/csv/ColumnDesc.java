package jp.sourceforge.hotchpotch.coopie.csv;

public interface ColumnDesc<BEAN> {

    ColumnName getName();

    String getValue(BEAN bean);

    void setValue(BEAN bean, String value);

}
