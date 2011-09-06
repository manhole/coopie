package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;

public class AbstractRecordWriter<T> implements Closable, RecordWriter<T> {

    protected boolean closed = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new ClosingGuardian(this);

    /**
     * RecordWriter close時に、Writerを一緒にcloseする場合はtrue。
     */
    private boolean closeWriter = true;

    private boolean firstRecord = true;

    protected CsvElementWriter elementWriter;
    private boolean withHeader;
    private boolean writtenHeader;

    protected RecordDesc<T> recordDesc;

    public AbstractRecordWriter(final RecordDesc<T> recordDesc) {
        this.recordDesc = recordDesc;
    }

    /*
     * 1レコード目を出力するときに、このメソッドが呼ばれる。
     */
    protected void writeHeader(final T bean) {
        final String[] line = recordDesc.getHeaderValues();
        elementWriter.writeRecord(line);
    }

    @Override
    public void write(final T bean) {
        if (firstRecord) {
            firstRecord = false;
            recordDesc = recordDesc.setupByBean(bean);
        }
        if (withHeader && !writtenHeader) {
            writeHeader(bean);
            writtenHeader = true;
        }
        final String[] line = recordDesc.getValues(bean);
        elementWriter.writeRecord(line);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (closeWriter) {
            elementWriter.close();
            elementWriter = null;
        }
    }

    public void setCloseWriter(final boolean closeWriter) {
        this.closeWriter = closeWriter;
    }

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

}
