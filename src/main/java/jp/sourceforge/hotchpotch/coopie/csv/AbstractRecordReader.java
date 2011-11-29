package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.Closable;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;

import org.slf4j.Logger;

public abstract class AbstractRecordReader<T> implements Closable,
        RecordReader<T> {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * RecordReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader_ = true;

    private RecordDesc<T> recordDesc_;
    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private ElementReader elementReader_;
    private ReadEditor readEditor_ = DefaultReadEditor.getInstance();
    private boolean withHeader_;

    private Boolean hasNext_;
    private String[] nextLine_;

    private ElementEditor elementEditor_;

    public AbstractRecordReader(final RecordDesc<T> recordDesc) {
        recordDesc_ = recordDesc;
    }

    @Override
    public void read(final T bean) {
        if (!hasNext()) {
            throw new NoSuchElementException("no element");
        }
        hasNext_ = null;

        final String[] line;
        if (nextLine_ != null) {
            line = nextLine_;
        } else {
            throw new AssertionError();
        }

        recordDesc_.setValues(bean, line);
    }

    @Override
    public T read() {
        final T bean = newInstance();
        read(bean);
        return bean;
    }

    protected T newInstance() {
        return recordDesc_.newInstance();
    }

    protected String[] readLine() {
        final String[] line = readEditor_.readRecord(elementReader_);
        if (line != null && elementEditor_ != null) {
            for (int i = 0; i < line.length; i++) {
                final String elem = line[i];
                final String edited = elementEditor_.edit(elem);
                line[i] = edited;
            }
        }
        return line;
    }

    @Override
    public boolean hasNext() {
        if (hasNext_ != null) {
            return hasNext_.booleanValue();
        }
        nextLine_ = readLine();

        if (nextLine_ == null) {
            hasNext_ = Boolean.FALSE;
        } else {
            hasNext_ = Boolean.TRUE;
        }
        return hasNext_.booleanValue();
    }

    protected void setupByHeader() {
        if (withHeader_) {
            final String[] header = readLine();
            if (header == null) {
                logger.debug("header is null");
                return;
            }
            recordDesc_ = recordDesc_.setupByHeader(header);
        } else {
            /*
             * ヘッダなしの場合は、列順が指定されていないとダメ。
             * JavaBeansのプロパティ情報は順序が不定なため。
             */
            if (OrderSpecified.SPECIFIED != recordDesc_.getOrderSpecified()) {
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
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (closeReader_) {
            CloseableUtil.closeNoException(elementReader_);
            elementReader_ = null;
        }
    }

    protected void setClosed(final boolean closed) {
        closed_ = closed;
    }

    public void setCloseReader(final boolean closeReader) {
        closeReader_ = closeReader;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    public void setReadEditor(final ReadEditor readEditor) {
        if (readEditor == null) {
            throw new NullPointerException("readEditor");
        }
        readEditor_ = readEditor;
    }

    public void setElementEditor(final ElementEditor elementEditor) {
        elementEditor_ = elementEditor;
    }

    protected ElementReader getElementReader() {
        return elementReader_;
    }

    protected void setElementReader(final ElementReader elementReader) {
        elementReader_ = elementReader;
    }

}
