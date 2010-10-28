package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.exception.IORuntimeException;

import au.com.bytecode.opencsv.CSVReader;

abstract class DefaultCsvReader<T> implements Closable, CsvReader<T> {

    private CsvSetting csvSetting = new CsvSetting();

    /**
     * CsvReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader = true;

    protected RecordDesc<T> recordDesc;
    protected boolean closed = true;
    protected CSVReader csvReader;

    private Boolean hasNext;

    private String[] nextLine;

    public DefaultCsvReader(final RecordDesc<T> recordDesc) {
        this.recordDesc = recordDesc;
    }

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

    @Override
    public void read(final T bean) {
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

        recordDesc.setValues(bean, line);
    }

    @Override
    public T read() {
        final T bean = newInstance();
        read(bean);
        return bean;
    }

    protected abstract T newInstance();

    protected String[] readLine() {
        try {
            return csvReader.readNext();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
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

    public void open(final Reader reader) {
        csvReader = csvSetting.openReader(reader);
        closed = false;

        setupByHeader();
    }

    private void setupByHeader() {
        if (recordDesc.isWithHeader()) {
            final String[] header = readLine();
            recordDesc = recordDesc.setupByHeader(header);
        } else {
            /*
             * ヘッダなしの場合は、列順が指定されていないとダメ。
             * JavaBeansのプロパティ情報は順序が不定なため。
             */
            if (OrderSpecified.SPECIFIED != recordDesc.getOrderSpecified()) {
                throw new IllegalStateException("no column order set");
            }
        }
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
