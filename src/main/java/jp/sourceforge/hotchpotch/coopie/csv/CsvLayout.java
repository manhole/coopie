package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvLayout<T> {

    String[] getValues(T bean);

    void setValues(T bean, String[] values);

    OrderSpecified getOrderSpecified();

    CsvLayout<T> setupByHeader(String[] header);

    ColumnName[] getNames();

    void setupColumns(ColumnSetup columnSetup);

    boolean isWithHeader();

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
