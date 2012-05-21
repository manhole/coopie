package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(ColumnName name);

    ColumnBuilder column(String name);

    ColumnBuilder column(String propertyName, String label);

    ColumnBuilder columns(final String... names);

    interface ColumnBuilder {

        ColumnBuilder property(final String propertyName);

        void converter(Converter converter);

    }

}
