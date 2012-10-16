package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvColumnSetup {

    ColumnBuilder column(ColumnName name);

    ColumnBuilder column(String name);

    ColumnBuilder columns(final String... names);

    interface ColumnBuilder {

        /*
         * カラム名とプロパティ名が異なる場合は、当メソッドでプロパティ名を指定してください。
         */
        ColumnBuilder toProperty(final String propertyName);

        /*
         * 型変換およびcompositカラムである場合は、
         * 当メソッドでconverterを指定してください。
         */
        void withConverter(Converter converter);

    }

}
