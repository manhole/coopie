package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.csv.Rfc4180Writer.QuoteMode;

import org.t2framework.commons.util.StringUtil;

public class CsvSetting implements ElementSetting {

    public static final char TAB = '\t';
    public static final char COMMA = ',';
    public static final char DOUBLE_QUOTE = '\"';
    public static final String CR = "\r";
    public static final String LF = "\n";
    public static final String CRLF = "\r\n";

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
    public ElementWriter openWriter(final Writer writer) {
        final Rfc4180Writer csvWriter = new Rfc4180Writer();
        csvWriter.setElementSeparator(getElementSeparator());
        csvWriter.setLineSeparator(getLineSeparator());
        csvWriter.setQuoteMark(getQuoteMark());
        csvWriter.setQuoteMode(QuoteMode.ALWAYS_EXCEPT_NULL);
        csvWriter.open(writer);
        return csvWriter;
    }

    @Override
    public ElementReader openReader(final Reader reader) {
        final Rfc4180Reader rfcReader = new Rfc4180Reader();
        rfcReader.setElementSeparator(getElementSeparator());
        rfcReader.setQuoteMark(getQuoteMark());
        rfcReader.open(reader);
        return rfcReader;
    }

    public String getLineSeparator() {
        if (StringUtil.isEmpty(lineSeparator_)) {
            lineSeparator_ = CRLF;
        }
        return lineSeparator_;
    }

    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

    public char getQuoteMark() {
        return quoteMark_;
    }

    public void setQuoteMark(final char quoteMark) {
        quoteMark_ = quoteMark;
    }

    public char getElementSeparator() {
        return elementSeparator_;
    }

    public void setElementSeparator(final char elementSeparator) {
        elementSeparator_ = elementSeparator;
    }

}
