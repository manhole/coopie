package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.csv.Rfc4180Writer.QuoteMode;

import org.t2framework.commons.util.StringUtil;

public class DefaultCsvSetting implements ElementSetting, CsvSetting {

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

    @Override
    public ElementWriter openWriter(final Appendable appendable) {
        final Rfc4180Writer csvWriter = new Rfc4180Writer();
        csvWriter.setElementSeparator(getElementSeparator());
        csvWriter.setLineSeparator(getLineSeparator());
        csvWriter.setQuoteMark(getQuoteMark());
        csvWriter.setQuoteMode(QuoteMode.ALWAYS_EXCEPT_NULL);
        csvWriter.open(appendable);
        return csvWriter;
    }

    @Override
    public ElementReader openReader(final Readable readable) {
        final Rfc4180Reader rfcReader = new Rfc4180Reader();
        rfcReader.setElementSeparator(getElementSeparator());
        rfcReader.setQuoteMark(getQuoteMark());
        rfcReader.open(readable);
        return rfcReader;
    }

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

}
