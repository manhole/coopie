package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.csv.CsvLayout.OrderSpecified;

import org.t2framework.commons.exception.IORuntimeException;

import au.com.bytecode.opencsv.CSVReader;

public class MapCsvReader implements Closable {

    private CsvSetting csvSetting = new CsvSetting();
    private CSVReader csvReader;
    protected boolean closed = true;
    private boolean closeReader = true;
    private Boolean hasNext = null;
    private String[] nextLine;
    private CsvLayout<Map<String, String>> csvLayout;

    public MapCsvReader() {
        csvLayout = new MapCsvLayout();
    }

    public MapCsvReader(final MapCsvLayout columnLayout) {
        this.csvLayout = columnLayout;
    }

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

    public void open(final Reader reader) {
        csvReader = csvSetting.openReader(reader);
        closed = false;

        setupByHeader();
    }

    private void setupByHeader() {
        if (csvLayout.isWithHeader()) {
            final String[] header = readLine();
            csvLayout = csvLayout.setupByHeader(header);
        } else {
            /*
             * ヘッダなしの場合は、列順が指定されていないとダメ。
             * JavaBeansのプロパティ情報は順序が不定なため。
             */
            if (OrderSpecified.SPECIFIED != csvLayout.getOrderSpecified()) {
                throw new IllegalStateException("no column order set");
            }
        }
    }

    public void read(final Map<String, String> bean) {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        hasNext = null;

        final String[] line;
        if (nextLine != null) {
            line = nextLine;
        } else {
            throw new AssertionError();
        }

        csvLayout.setValues(bean, line);
    }

    private String[] readLine() {
        try {
            return csvReader.readNext();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private boolean hasNext() {
        if (hasNext != null) {
            return hasNext.booleanValue();
        }
        nextLine = readLine();

        if (nextLine == null) {
            hasNext = Boolean.FALSE;
        } else {
            hasNext = Boolean.TRUE;
        }
        return hasNext.booleanValue();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (closeReader) {
            csvReader.close();
            csvReader = null;
        }
    }

    public void setCloseReader(final boolean closeReader) {
        this.closeReader = closeReader;
    }

}
