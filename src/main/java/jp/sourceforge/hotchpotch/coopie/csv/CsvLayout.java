package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public interface CsvLayout<T> {

    /**
     * CSV 1レコードぶんの文字列を、オブジェクトから構築します。
     */
    String[] getValues(T bean);

    /**
     * CSV 1レコードぶんの文字列を、オブジェクトへセットします。
     */
    void setValues(T bean, String[] values);

    OrderSpecified getOrderSpecified();

    CsvLayout<T> setupByHeader(String[] header);

    ColumnName[] getNames();

    void setupColumns(ColumnSetupBlock columnSetup);

    boolean isWithHeader();

    CsvReader<T> openReader(Reader reader);

    CsvWriter<T> openWriter(Writer writer);

    public enum OrderSpecified {

        /**
         * 列順が指定されていない場合。
         * 不定の場合。
         */
        NO,

        /**
         * 説順が明示的に指定されている場合。
         */
        SPECIFIED

    }

}
