package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.IOUtil;

import org.t2framework.commons.exception.IORuntimeException;

public class FixedLengthReader implements CsvElementReader {

    protected boolean closed = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new ClosingGuardian(this);

    private final BufferedReader reader;
    private final FixedLengthColumn[] columns;

    public FixedLengthReader(final Reader reader,
            final FixedLengthColumn[] columns) {
        this.reader = toBufferedReader(reader);
        this.columns = columns;
        closed = false;
    }

    private BufferedReader toBufferedReader(final Reader r) {
        if (r instanceof BufferedReader) {
            return (BufferedReader) r;
        }
        return new BufferedReader(r);
    }

    @Override
    public String[] readRecord() {
        try {
            final String line = reader.readLine();
            if (line == null) {
                return null;
            }
            final String[] record = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                final FixedLengthColumn column = columns[i];
                final String elem = column.read(line);
                record[i] = elem;
            }
            return record;
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
        IOUtil.closeNoException(reader);
    }

}
