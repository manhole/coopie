package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.IOUtil;
import jp.sourceforge.hotchpotch.coopie.util.ReaderUtil;

import org.t2framework.commons.exception.IORuntimeException;

public class FixedLengthReader implements ElementReader {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private final BufferedReader reader_;
    private final FixedLengthColumn[] columns_;
    private int lineNo_;

    public FixedLengthReader(final Reader reader,
            final FixedLengthColumn[] columns) {
        reader_ = ReaderUtil.toBufferedReader(reader);
        columns_ = columns;
        closed_ = false;
    }

    @Override
    public int getRecordNo() {
        return lineNo_;
    }

    @Override
    public String[] readRecord() {
        try {
            final String line = reader_.readLine();
            if (line == null) {
                return null;
            }
            final String[] record = new String[columns_.length];
            for (int i = 0; i < columns_.length; i++) {
                final FixedLengthColumn column = columns_[i];
                final String elem = column.read(line);
                record[i] = elem;
            }
            lineNo_++;
            return record;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        IOUtil.closeNoException(reader_);
    }

}
