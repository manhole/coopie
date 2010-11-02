package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;

class DefaultCsvReader<T> extends AbstractElementReader<T> {

    private CsvSetting csvSetting = new CsvSetting();

    private final RecordBeanType<T> recordBeanType;

    public DefaultCsvReader(final RecordDesc<T> recordDesc,
            final RecordBeanType<T> recordBeanType) {
        super(recordDesc);
        this.recordBeanType = recordBeanType;
    }

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

    public void open(final Reader reader) {
        elementReader = csvSetting.openReader(reader);
        closed = false;

        setupByHeader();
    }

    @Override
    protected T newInstance() {
        return recordBeanType.newInstance();
    }

}
