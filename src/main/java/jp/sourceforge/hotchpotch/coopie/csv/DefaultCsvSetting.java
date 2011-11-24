package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.util.StringUtil;

public class DefaultCsvSetting implements CsvSetting {

    /**
     * 要素区切り文字。
     * 未指定の場合はタブです。
     */
    private char elementSeparator_ = TAB;

    /**
     * 要素をクォートする文字。
     * 未指定の場合はダブルクォート(二重引用符)です。
     */
    private char quoteMark_ = DOUBLE_QUOTE;

    /**
     * 改行文字。
     * 未指定の場合はCRLFです。
     * 
     * CsvWriterを使う場合は、何らかの値が設定されている必要があります。
     * (CRLFのままでもOKです)
     * 
     * CsvReaderを使う場合は、未設定のままで構いません。
     */
    private String lineSeparator_ = CRLF;

    private QuoteMode quoteMode_ = QuoteMode.ALWAYS_EXCEPT_NULL;

    @Override
    public String getLineSeparator() {
        if (StringUtil.isEmpty(lineSeparator_)) {
            lineSeparator_ = CRLF;
        }
        return lineSeparator_;
    }

    @Override
    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

    @Override
    public char getQuoteMark() {
        return quoteMark_;
    }

    @Override
    public void setQuoteMark(final char quoteMark) {
        quoteMark_ = quoteMark;
    }

    @Override
    public char getElementSeparator() {
        return elementSeparator_;
    }

    @Override
    public void setElementSeparator(final char elementSeparator) {
        elementSeparator_ = elementSeparator;
    }

    @Override
    public QuoteMode getQuoteMode() {
        return quoteMode_;
    }

    @Override
    public void setQuoteMode(final QuoteMode quoteMode) {
        quoteMode_ = quoteMode;
    }

}
