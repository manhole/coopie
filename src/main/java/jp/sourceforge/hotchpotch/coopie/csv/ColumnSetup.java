package jp.sourceforge.hotchpotch.coopie.csv;

public interface ColumnSetup {

    void column(final ColumnName name);

    void column(final String name);

    void column(final String propertyName, final String label);

}
