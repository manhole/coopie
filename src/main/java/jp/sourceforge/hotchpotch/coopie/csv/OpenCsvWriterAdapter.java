package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.IOUtil;
import au.com.bytecode.opencsv.CSVWriter;

public class OpenCsvWriterAdapter implements CsvElementWriter {

    protected boolean closed = true;
    private final CSVWriter csvWriter;

    public OpenCsvWriterAdapter(final CSVWriter csvWriter) {
        this.csvWriter = csvWriter;
        closed = false;
    }

    @Override
    public void writeLine(final String[] line) {
        csvWriter.writeNext(line);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        IOUtil.closeNoException(csvWriter);
    }

}
