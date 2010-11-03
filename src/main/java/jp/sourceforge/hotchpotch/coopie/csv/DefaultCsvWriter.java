package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Writer;

class DefaultCsvWriter<T> extends AbstractCsvWriter<T> {

    private CsvSetting csvSetting = new CsvSetting();

    public DefaultCsvWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public CsvSetting getCsvSetting() {
        return csvSetting;
    }

    public void setCsvSetting(final CsvSetting csvSetting) {
        this.csvSetting = csvSetting;
    }

    public void open(final Writer writer) {
        elementWriter = csvSetting.openWriter(writer);
        closed = false;
    }

}
