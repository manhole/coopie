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

    void setLineSeparator(String lineSeparator);

    char getQuoteMark();

    void setQuoteMark(char quoteMark);

    char getElementSeparator();

    void setElementSeparator(char elementSeparator);

    QuoteMode getQuoteMode();

    void setQuoteMode(QuoteMode quoteMode);

}
