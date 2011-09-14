package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(final ColumnName name);

    ColumnBuilder column(final String name);

    ColumnBuilder column(final String propertyName, final String label);

    interface ColumnBuilder {

        <OBJ, EXT> void converter(Converter<OBJ, EXT> converter);

    }

}
