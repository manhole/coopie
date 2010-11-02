package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

import org.t2framework.commons.util.StringUtil;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CsvSetting {

    public static final char TAB = '\t';
    public static final char COMMA = ',';
    public static final char DOUBLE_QUOTE = '\"';
    public static final String CRLF = "\r\n";

    /**
     * 要素区切り文字。
     * 未指定の場合はタブです。
     */
    private char elementSeparator = TAB;

    /**
     * 要素をクォートする文字。
     * 未指定の場合はダブルクォート(二重引用符)です。
     */
    private char quoteMark = DOUBLE_QUOTE;

    /**
     * 改行文字。
     * 未指定の場合はCRLFです。
     * 
     * CsvWriterを使う場合は、何らかの値が設定されている必要があります。
     * (CRLFのままでもOKです)
     * 
     * CsvReaderを使う場合は、未設定のままで構いません。
     */
    private String lineSeparator = CRLF;

    public CsvElementWriter openWriter(final Writer writer) {
        final CSVWriter csvWriter = new CSVWriter(writer,
                getElementSeparator(), getQuoteMark(), getLineSeparator());
        return new OpenCsvWriterAdapter(csvWriter);
    }

    public CsvElementReader openReader(final Reader reader) {
        final CSVReader csvReader = new CSVReader(reader,
                getElementSeparator(), getQuoteMark());
        return new OpenCsvReaderAdapter(csvReader);
    }

    public String getLineSeparator() {
        if (StringUtil.isEmpty(lineSeparator)) {
            lineSeparator = CRLF;
        }
        return lineSeparator;
    }

    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public char getQuoteMark() {
        return quoteMark;
    }

    public void setQuoteMark(final char quoteMark) {
        this.quoteMark = quoteMark;
    }

    public char getElementSeparator() {
        return elementSeparator;
    }

    public void setElementSeparator(final char elementSeparator) {
        this.elementSeparator = elementSeparator;
    }

}
