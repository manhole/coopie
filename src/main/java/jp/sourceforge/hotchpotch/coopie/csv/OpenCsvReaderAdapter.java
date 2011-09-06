package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.IOUtil;

import org.t2framework.commons.exception.IORuntimeException;

import au.com.bytecode.opencsv.CSVReader;

public class OpenCsvReaderAdapter implements ElementReader {

    protected boolean closed = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new ClosingGuardian(this);

    private final CSVReader csvReader;
    private int lineNo;

    public OpenCsvReaderAdapter(final CSVReader csvReader) {
        this.csvReader = csvReader;
        closed = false;
    }

    @Override
    public int getRecordNo() {
        return lineNo;
    }

    @Override
    public String[] readRecord() {
        try {
            final String[] read = csvReader.readNext();
            if (read == null) {
                return null;
            }
            lineNo++;
            return read;
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
        IOUtil.closeNoException(csvReader);
    }

}
