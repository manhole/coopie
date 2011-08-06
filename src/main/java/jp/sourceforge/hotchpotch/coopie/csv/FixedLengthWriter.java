package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.IOUtil;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.StringUtil;

public class FixedLengthWriter implements CsvElementWriter {

    protected boolean closed = true;
    private final BufferedWriter writer;
    private final FixedLengthColumn[] columns;
    private String lineSeparator = CsvSetting.CRLF;

    public FixedLengthWriter(final Writer writer,
            final FixedLengthColumn[] columns) {
        this.writer = toBufferedWriter(writer);
        this.columns = columns;
        closed = false;
    }

    private BufferedWriter toBufferedWriter(final Writer w) {
        if (w instanceof BufferedWriter) {
            return (BufferedWriter) w;
        }
        return new BufferedWriter(w);
    }

    @Override
    public void writeRecord(final String[] line) {
        final int len = Math.min(line.length, columns.length);
        try {
            for (int i = 0; i < len; i++) {
                final String s = line[i];
                final FixedLengthColumn column = columns[i];
                column.write(s, writer);
            }
            writer.write(getLineSeparator());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        IOUtil.closeNoException(writer);
    }

    public String getLineSeparator() {
        if (StringUtil.isEmpty(lineSeparator)) {
            lineSeparator = CsvSetting.CRLF;
        }
        return lineSeparator;
    }

    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

}
