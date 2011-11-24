package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;

public class AbstractRecordWriter<T> implements Closable, RecordWriter<T> {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    /**
     * RecordWriter close時に、Writerを一緒にcloseする場合はtrue。
     */
    private boolean closeWriter_ = true;

    private boolean firstRecord_ = true;

    private ElementWriter elementWriter_;
    private boolean withHeader_;
    private boolean writtenHeader_;

    private RecordDesc<T> recordDesc_;

    public AbstractRecordWriter(final RecordDesc<T> recordDesc) {
        recordDesc_ = recordDesc;
    }

    /*
     * 1レコード目を出力するときに、このメソッドが呼ばれる。
     */
    protected void writeHeader(final T bean) {
        final String[] line = recordDesc_.getHeaderValues();
        elementWriter_.writeRecord(line);
    }

    @Override
    public void write(final T bean) {
        if (firstRecord_) {
            firstRecord_ = false;
            recordDesc_ = recordDesc_.setupByBean(bean);
        }
        if (withHeader_ && !writtenHeader_) {
            writeHeader(bean);
            writtenHeader_ = true;
        }
        final String[] line = recordDesc_.getValues(bean);
        elementWriter_.writeRecord(line);
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (closeWriter_) {
            elementWriter_.close();
            elementWriter_ = null;
        }
    }

    protected void setClosed(final boolean closed) {
        closed_ = closed;
    }

    public void setCloseWriter(final boolean closeWriter) {
        closeWriter_ = closeWriter;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected void setElementWriter(final ElementWriter elementWriter) {
        elementWriter_ = elementWriter;
    }

}
