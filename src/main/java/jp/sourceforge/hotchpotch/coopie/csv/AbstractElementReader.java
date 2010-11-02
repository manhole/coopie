package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.slf4j.Logger;

public abstract class AbstractElementReader<T> implements Closable,
        CsvReader<T> {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * CsvReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader = true;

    protected RecordDesc<T> recordDesc;
    protected boolean closed = true;
    protected CsvElementReader elementReader;

    private Boolean hasNext;

    private String[] nextLine;

    public AbstractElementReader(final RecordDesc<T> recordDesc) {
        this.recordDesc = recordDesc;
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
        return elementReader.readRecord();
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

    protected void setupByHeader() {
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
            elementReader.close();
            elementReader = null;
        }
    }

    public void setCloseReader(final boolean closeReader) {
        this.closeReader = closeReader;
    }

}
