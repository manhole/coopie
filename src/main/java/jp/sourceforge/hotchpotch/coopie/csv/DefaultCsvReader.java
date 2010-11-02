package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;

abstract class DefaultCsvReader<T> extends AbstractElementReader<T> {

    private CsvSetting csvSetting = new CsvSetting();

    public DefaultCsvReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
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

}
