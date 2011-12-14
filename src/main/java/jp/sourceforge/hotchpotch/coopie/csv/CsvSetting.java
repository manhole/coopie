package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.IOUtil;

public interface CsvSetting {

    char TAB = '\t';
    char COMMA = ',';
    char DOUBLE_QUOTE = '\"';
    char CR = IOUtil.CR;
    char LF = IOUtil.LF;
    String CR_S = IOUtil.CR_S;
    String LF_S = IOUtil.LF_S;
    String CRLF = IOUtil.CRLF;

    String getLineSeparator();

    /**
     * 改行文字を設定します。
     * 改行文字はCSVを出力する際に使用します。
     * CSVを入力する場合には、ここで設定した改行文字は使用せず、
     * CR/LF/CRLFを改行文字として扱います。
     */
    void setLineSeparator(String lineSeparator);

    char getQuoteMark();

    void setQuoteMark(char quoteMark);

    char getElementSeparator();

    void setElementSeparator(char elementSeparator);

    QuoteMode getQuoteMode();

    /**
     * クォートモードを設定します。
     * CSVを出力する際に使用します。
     * CSVを入力する場合は、ここで設定したクォートモードは使用せず、
     * CSV RFCに従います。
     */
    void setQuoteMode(QuoteMode quoteMode);

}
