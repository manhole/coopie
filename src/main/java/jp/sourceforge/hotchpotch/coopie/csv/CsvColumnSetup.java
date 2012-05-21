package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(ColumnName name);

    ColumnBuilder column(String name);

    ColumnBuilder column(String propertyName, String label);

    interface ColumnBuilder {

        void converter(Converter converter);

    }

}
