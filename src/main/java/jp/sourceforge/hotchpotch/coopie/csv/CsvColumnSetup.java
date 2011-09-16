package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(final ColumnName name);

    ColumnBuilder column(final String name);

    ColumnBuilder column(final String propertyName, final String label);

    ColumnBuilder columns(final String... names);

    interface ColumnBuilder {

        ColumnBuilder property(final String propertyName);

        <OBJ, EXT> void converter(Converter<OBJ, EXT> converter);

    }

}
