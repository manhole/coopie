package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.slf4j.Logger;

abstract class DefaultCsvReader<T> implements Closable, CsvReader<T> {

    private static final Logger logger = LoggerFactory.getLogger();
    private CsvSetting csvSetting = new CsvSetting();

    /**
     * CsvReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader = true;

    protected RecordDesc<T> recordDesc;
    protected boolean closed = true;
    protected CsvElementReader csvReader;

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
        return csvReader.readLine();
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
            if (header == null) {
                logger.debug("header is null");
                return;
            }
            recordDesc = recordDesc.setupByHeader(header);
        } else {
            /*
             * ヘッダなしの場合は、列順が指定されていないとダメ。
             * JavaBeansのプロパティ情報は順序が不定なため。
             */
            if (OrderSpecified.SPECIFIED != recordDesc.getOrderSpecified()) {
                if (readLine() == null) {
                    /*
                     * 本来はエラーだが、空ファイルに限ってはOKとしておく。
                     * (将来エラーに変更するかも)
                     */
                    logger.debug("header is null");
                    return;
                }
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
