package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Closeable;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.IOUtil;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.StringUtil;

public class FixedLengthWriter implements ElementWriter {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private final Appendable appendable_;
    private final FixedLengthColumn[] columns_;
    private String lineSeparator_ = CsvSetting.CRLF;

    public FixedLengthWriter(final Appendable appendable,
            final FixedLengthColumn[] columns) {
        appendable_ = appendable;
        columns_ = columns;
        closed_ = false;
    }

    @Override
    public void writeRecord(final String[] line) {
        final int len = Math.min(line.length, columns_.length);
        try {
            for (int i = 0; i < len; i++) {
                final String s = line[i];
                final FixedLengthColumn column = columns_[i];
                column.write(s, appendable_);
            }
            appendable_.append(getLineSeparator());
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
        if (appendable_ instanceof Closeable) {
            final Closeable closeable = Closeable.class.cast(appendable_);
            IOUtil.closeNoException(closeable);
        }
    }

    public String getLineSeparator() {
        if (StringUtil.isEmpty(lineSeparator_)) {
            lineSeparator_ = CsvSetting.CRLF;
        }
        return lineSeparator_;
    }

    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

}
