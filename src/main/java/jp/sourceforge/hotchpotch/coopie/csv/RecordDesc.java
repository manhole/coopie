package jp.sourceforge.hotchpotch.coopie.csv;

public interface RecordDesc<BEAN> {

    /**
     * ファイル 1レコードぶんの文字列を、オブジェクトから構築します。
     */
    String[] getValues(BEAN bean);

    /**
     * ファイル 1レコードぶんの文字列を、オブジェクトへセットします。
     */
    void setValues(BEAN bean, String[] values);

    /**
     * ヘッダ行の文字列を返します。
     * ファイルを出力する際に使用します。
     */
    String[] getHeaderValues();

    /**
     * 列順が指定されているかどうかを返します。
     */
    OrderSpecified getOrderSpecified();

    /**
     * ファイルを読む際に、ヘッダ行からrecord定義を修正します。
     */
    RecordDesc<BEAN> setupByHeader(String[] header);

    /**
     * ファイルへ書く際に、1行目のオブジェクトからrecord定義を修正します。
     */
    RecordDesc<BEAN> setupByBean(BEAN bean);

    /**
     * 1レコードぶんのオブジェクトを生成します。
     */
    BEAN newInstance();

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
