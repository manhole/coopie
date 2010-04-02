package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.Closable;
import au.com.bytecode.opencsv.CSVWriter;

abstract class AbstractCsvWriter<T> implements Closable {

    protected boolean closed = true;

    /**
     * CsvWriter close時に、Writerを一緒にcloseする場合はtrue。
     */
    private boolean closeWriter = true;

    protected CSVWriter csvWriter;

    private CsvSetting csvSetting = new CsvSetting();

    public void open(final Writer writer) {
        csvWriter = csvSetting.openWriter(writer);
        writeHeader();
        closed = false;
    }

    protected void writeHeader() {
        final ColumnName[] names = getColumnNames();
        final String[] line = new String[names.length];
        int i = 0;
        for (final ColumnName name : names) {
            line[i] = name.getLabel();
            i++;
        }
        csvWriter.writeNext(line);
    }

    public void write(final T bean) {
        final String[] line = getValues(bean);
        csvWriter.writeNext(line);
    }

    protected abstract ColumnName[] getColumnNames();

    protected abstract String[] getValues(final T bean);

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (closeWriter) {
            csvWriter.close();
            csvWriter = null;
        }
    }

    public void setCloseWriter(final boolean closeWriter) {
        this.closeWriter = closeWriter;
    }

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

}
