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

    /**
     * ヘッダ行の文字列を返します。
     */
    String[] getHeaderValues();

    OrderSpecified getOrderSpecified();

    /**
     * CSVを読む際に、ヘッダ行からrecord定義を修正します。
     */
    RecordDesc<T> setupByHeader(String[] header);

    /**
     * CSVを書く際に、1行目のオブジェクトからrecord定義を修正します。
     */
    RecordDesc<T> setupByBean(T bean);

    T newInstance();

    public enum OrderSpecified {

        /**
         * 列順が指定されていない場合。
         * 不定の場合。
         */
        NO,

        /**
         * 列順が明示的に指定されている場合。
         */
        SPECIFIED

    }

}
