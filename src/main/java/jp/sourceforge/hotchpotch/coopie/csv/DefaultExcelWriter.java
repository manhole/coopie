package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.OutputStream;

public class DefaultExcelWriter<T> extends AbstractCsvWriter<T> {

    public DefaultExcelWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void open(final OutputStream os) {
        // TODO Auto-generated method stub

    }

    static class PoiWriter implements CsvElementWriter {

        public PoiWriter(final OutputStream os) {
        }

        @Override
        public boolean isClosed() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub

        }

        @Override
        public void writeRecord(final String[] line) {
            // TODO Auto-generated method stub

        }

    }

}
