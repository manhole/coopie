package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.IOUtil;
import jp.sourceforge.hotchpotch.coopie.WriterUtil;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.StringUtil;

public class FixedLengthWriter implements ElementWriter {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private final BufferedWriter writer_;
    private final FixedLengthColumn[] columns_;
    private String lineSeparator_ = CsvSetting.CRLF;

    public FixedLengthWriter(final Writer writer,
            final FixedLengthColumn[] columns) {
        writer_ = WriterUtil.toBufferedWriter(writer);
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
                column.write(s, writer_);
            }
            writer_.write(getLineSeparator());
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
        IOUtil.closeNoException(writer_);
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
