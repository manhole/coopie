package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public abstract class AbstractCsvReader<T> implements Closable, CsvReader<T> {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * CsvReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader = true;

    protected RecordDesc<T> recordDesc;
    protected boolean closed = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new ClosingGuardian(this);

    protected CsvElementReader elementReader;
    private ReadEditor readEditor = DefaultReadEditor.getInstance();
    private boolean withHeader;

    private Boolean hasNext;

    private String[] nextLine;

    public AbstractCsvReader(final RecordDesc<T> recordDesc) {
        this.recordDesc = recordDesc;
    }

    @Override
    public void read(final T bean) {
        if (!hasNext()) {
            throw new NoSuchElementException("no element");
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

    protected T newInstance() {
        return recordDesc.newInstance();
    }

    protected String[] readLine() {
        final String[] line = readEditor.readRecord(elementReader);
        return line;
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
        if (withHeader) {
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

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

    public void setReadEditor(final ReadEditor readEditor) {
        if (readEditor == null) {
            throw new NullPointerException("readEditor");
        }
        this.readEditor = readEditor;
    }

    public static interface ReadEditor {

        String[] readRecord(CsvElementReader elementReader);

    }

    private static class DefaultReadEditor implements ReadEditor {

        private static final DefaultReadEditor INSTANCE = new DefaultReadEditor();

        static ReadEditor getInstance() {
            return INSTANCE;
        }

        @Override
        public String[] readRecord(final CsvElementReader elementReader) {
            return elementReader.readRecord();
        }

    }

}
