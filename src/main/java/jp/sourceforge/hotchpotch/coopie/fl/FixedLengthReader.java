package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.csv.ElementReader;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;

import org.t2framework.commons.exception.IORuntimeException;

public class FixedLengthReader implements ElementReader {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private LineReadable reader_;
    private final FixedLengthElementDesc[] elementDescs_;
    private int lineNo_;

    public FixedLengthReader(final FixedLengthElementDesc[] columns) {
        elementDescs_ = columns;
    }

    public void open(final Readable readable) {
        reader_ = new LineReadable(readable);
        closed_ = false;
    }

    @Override
    public int getRecordNumber() {
        return lineNo_;
    }

    @Override
    public int getLineNumber() {
        return lineNo_;
    }

    @Override
    public String[] readRecord() {
        try {
            final String line = reader_.readLineBody();
            if (line == null) {
                return null;
            }
            final String[] record = new String[elementDescs_.length];
            for (int i = 0; i < elementDescs_.length; i++) {
                final FixedLengthElementDesc elementDesc = elementDescs_[i];
                final String elem = elementDesc.read(line);
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
        CloseableUtil.closeNoException(reader_);
    }

}
