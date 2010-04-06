package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.Closable;
import au.com.bytecode.opencsv.CSVWriter;

class DefaultCsvWriter<T> implements Closable, CsvWriter<T> {

    private boolean closed = true;

    /**
     * CsvWriter close時に、Writerを一緒にcloseする場合はtrue。
     */
    private boolean closeWriter = true;

    private boolean firstRecord = true;

    protected CSVWriter csvWriter;

    private boolean writtenHeader;

    private CsvSetting csvSetting = new CsvSetting();

    protected RecordDesc<T> recordDesc;

    public DefaultCsvWriter(final RecordDesc<T> csvLayout) {
        this.recordDesc = csvLayout;
    }

    public void open(final Writer writer) {
        csvWriter = csvSetting.openWriter(writer);
        closed = false;
    }

    /*
     * 1レコード目を出力するときに、このメソッドが呼ばれる。
     */
    protected void writeHeader(final T bean) {
        final ColumnName[] names = recordDesc.getColumnNames();
        final String[] line = new String[names.length];
        int i = 0;
        for (final ColumnName name : names) {
            line[i] = name.getLabel();
            i++;
        }
        csvWriter.writeNext(line);
    }

    public void write(final T bean) {
        if (firstRecord) {
            firstRecord = false;
            recordDesc = recordDesc.setupByBean(bean);
        }
        if (!writtenHeader) {
            writeHeader(bean);
            writtenHeader = true;
        }
        final String[] line = recordDesc.getValues(bean);
        csvWriter.writeNext(line);
    }

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
