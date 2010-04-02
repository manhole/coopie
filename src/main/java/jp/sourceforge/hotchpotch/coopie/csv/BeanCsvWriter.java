package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.Closable;
import au.com.bytecode.opencsv.CSVWriter;

public class BeanCsvWriter<T> implements Closable {

    private CsvSetting csvSetting = new CsvSetting();

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

    protected boolean closed = true;
    /**
     * BeanCsvWriter close時に、Writerを一緒にcloseする場合はtrue。
     */
    private boolean closeWriter = true;
    private CSVWriter csvWriter;
    private final BeanColumnLayout<T> columnLayout;

    public BeanCsvWriter(final Class<T> beanClass) {
        columnLayout = new BeanColumnLayout<T>(beanClass);
    }

    public BeanCsvWriter(final BeanColumnLayout<T> columnLayout) {
        this.columnLayout = columnLayout;
    }

    public void open(final Writer writer) {
        csvWriter = new CSVWriter(writer, csvSetting.getElementSeparator(),
            csvSetting.getQuoteMark(), csvSetting.getLineSeparator());
        writeHeader();
        closed = false;
    }

    private void writeHeader() {
        final ColumnName[] names = columnLayout.getNames();
        final String[] line = new String[names.length];
        int i = 0;
        for (final ColumnName name : names) {
            line[i] = name.getLabel();
            i++;
        }
        csvWriter.writeNext(line);
    }

    public void write(final T bean) {
        final String[] line = columnLayout.getValues(bean);
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

}