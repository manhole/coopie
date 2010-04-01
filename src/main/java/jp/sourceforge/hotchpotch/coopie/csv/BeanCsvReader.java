package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.Closable;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;

import au.com.bytecode.opencsv.CSVReader;

public class BeanCsvReader<T> implements Closable {

    private CsvSetting csvSetting = new CsvSetting();

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

    /**
     * BeanCsvReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader = true;
    private Boolean hasNext = null;
    private CSVReader csvReader;
    protected boolean closed = true;
    private final BeanDesc<T> beanDesc;
    private String[] nextLine;
    private final BeanColumnLayout<T> columnLayout;

    public BeanCsvReader(final Class<T> beanClass) {
        beanDesc = BeanDescFactory.getBeanDesc(beanClass);
        columnLayout = new BeanColumnLayout<T>();
    }

    public BeanCsvReader(final Class<T> beanClass,
        final BeanColumnLayout<T> columnLayout) {
        beanDesc = BeanDescFactory.getBeanDesc(beanClass);
        this.columnLayout = columnLayout;
    }

    public void open(final Reader reader) {
        csvReader = new CSVReader(reader, csvSetting.getElementSeparator(),
            csvSetting.getQuoteMark());
        closed = false;

        setupColumnDescByHeader();
    }

    private void setupColumnDescByHeader() {
        final String[] header = readLine();
        columnLayout.setupColumnDescByHeader(beanDesc, header);
    }

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

        columnLayout.setValues(bean, line);
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
