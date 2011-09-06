package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;

class DefaultRecordReader<T> extends AbstractRecordReader<T> {

    private ElementSetting elementSetting;

    public DefaultRecordReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementSetting(final ElementSetting elementSetting) {
        this.elementSetting = elementSetting;
    }

    public void open(final Reader reader) {
        elementReader = elementSetting.openReader(reader);
        closed = false;

        setupByHeader();
    }

}
