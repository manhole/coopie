package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.IOUtil;

import org.t2framework.commons.exception.IORuntimeException;

import au.com.bytecode.opencsv.CSVReader;

public class OpenCsvReaderAdapter implements CsvElementReader {

    protected boolean closed = true;
    private final CSVReader csvReader;

    public OpenCsvReaderAdapter(final CSVReader csvReader) {
        this.csvReader = csvReader;
        closed = false;
    }

    @Override
    public String[] readLine() {
        try {
            return csvReader.readNext();
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
