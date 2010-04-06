package jp.sourceforge.hotchpotch.coopie.csv;

public interface RecordDesc<T> {

    /**
     * CSV 1レコードぶんの文字列を、オブジェクトから構築します。
     */
    String[] getValues(T bean);

    /**
     * CSV 1レコードぶんの文字列を、オブジェクトへセットします。
     */
    void setValues(T bean, String[] values);

    ColumnName[] getColumnNames();

    boolean isWithHeader();

    OrderSpecified getOrderSpecified();

    /*
     * CSVを読むとき
     */
    RecordDesc<T> setupByHeader(String[] header);

    /*
     * CSVを書くとき
     */
    RecordDesc<T> setupByBean(T bean);

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
