package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(CsvColumnDef columnDef);

    ColumnBuilder column(String name);

    CompositeColumnBuilder columns(
            final SetupBlock<CsvCompositeColumnSetup> compositeSetup);

    public interface ColumnBuilder {

        /*
         * カラム名とプロパティ名が異なる場合は、当メソッドでプロパティ名を指定してください。
         */
        ColumnBuilder toProperty(final String propertyName);

        /*
         * 型変換およびCompositeカラムである場合は、
         * 当メソッドでconverterを指定してください。
         */
        ColumnBuilder withConverter(Converter converter);

    }

    public interface CsvCompositeColumnSetup {

        ColumnBuilder column(String name);

    }

    public interface CompositeColumnBuilder {

        CompositeColumnBuilder toProperty(final String propertyName);

        CompositeColumnBuilder withConverter(Converter converter);

    }

}
