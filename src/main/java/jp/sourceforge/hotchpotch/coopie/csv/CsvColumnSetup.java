package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(ColumnName name);

    /*
     * カラム名とプロパティ名が同じ場合
     */
    ColumnBuilder column(String name);

    ColumnBuilder column(String propertyName, String label);

    ColumnBuilder columns(final String... names);

    interface ColumnBuilder {

        ColumnBuilder toProperty(final String propertyName);

        void withConverter(Converter converter);

    }

}
